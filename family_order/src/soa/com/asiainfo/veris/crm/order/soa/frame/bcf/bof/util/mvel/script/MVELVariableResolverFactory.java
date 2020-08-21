
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script;

import java.util.HashMap;
import java.util.Map;

import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.BaseVariableResolverFactory;
import org.mvel2.integration.impl.LocalVariableResolverFactory;

public class MVELVariableResolverFactory extends BaseVariableResolverFactory implements LocalVariableResolverFactory
{
    class MVELScriptCompatibleResolver implements VariableResolver
    {
        private static final long serialVersionUID = -3924687495424018561L;

        private IMVELScriptCompatible content;

        private final String name;

        @SuppressWarnings("unchecked")
        private Class knownType = null;

        public MVELScriptCompatibleResolver(IMVELScriptCompatible i, String name)
        {
            this.content = i;
            this.name = name;
        }

        public MVELScriptCompatibleResolver(IMVELScriptCompatible i, String name, Class knowType)
        {
            this.content = i;
            this.name = name;
            this.knownType = knowType;
        }

        public int getFlags()
        {
            return 0;
        }

        public String getName()
        {
            return name;
        }

        @SuppressWarnings("unchecked")
        public Class getType()
        {
            return this.knownType;
        }

        public Object getValue()
        {
            return this.content.getData(name);
        }

        public void setStaticType(Class knownType)
        {
            this.knownType = knownType;
        }

        public void setValue(Object value)
        {
            if (knownType != null && value != null && value.getClass() != knownType)
            {
                // TODO
            }
            content.setData(name, value);
        }

    }

    private static final long serialVersionUID = -8631524542787717467L;

    private IMVELScriptCompatible content;

    public MVELVariableResolverFactory(IMVELScriptCompatible c)
    {
        this.content = c;

    }

    private void addResolver(String name, VariableResolver vr)
    {
        if (this.variableResolvers == null)
        {
            this.variableResolvers = new HashMap<String, VariableResolver>();
        }
        this.variableResolvers.put(name, vr);
    }

    public VariableResolver createVariable(String name, Object value)
    {

        VariableResolver vr = null;
        try
        {
            vr = getVariableResolver(name);
        }
        catch (Exception e)
        {
        }
        if (vr == null)
        {
            addResolver(name, vr = new MVELScriptCompatibleResolver(content, name));
        }

        vr.setValue(value);
        return vr;
    }

    public VariableResolver createVariable(String name, Object value, Class<?> arg2)
    {
        VariableResolver vr = null;
        try
        {
            vr = getVariableResolver(name);
        }
        catch (Exception e)
        {
        }
        if (vr == null)
        {
            addResolver(name, vr = new MVELScriptCompatibleResolver(content, name, arg2));
        }

        vr.setValue(value);
        return vr;
    }

    @Override
    public Map<String, VariableResolver> getVariableResolvers()
    {
        return this.variableResolvers;
    }

    public boolean isResolveable(String name)
    {
        boolean b = false;
        if (this.nextFactory != null)
        {
            b = nextFactory.isResolveable(name);
        }
        if (!b)
        {
            if (name == null)
                return false;
            if (this.variableResolvers != null && this.variableResolvers.containsKey(name))
            {
                return true;
            }
            if (content.containKey(name))
            {
                VariableResolver vr = new MVELScriptCompatibleResolver(content, name);
                addResolver(name, vr);
                return true;
            }
            return false;
        }
        return b;
    }

    public boolean isTarget(String name)
    {
        if (this.variableResolvers != null)
        {
            return this.variableResolvers.containsKey(name);
        }
        else
        {
            return false;
        }
    }

}
