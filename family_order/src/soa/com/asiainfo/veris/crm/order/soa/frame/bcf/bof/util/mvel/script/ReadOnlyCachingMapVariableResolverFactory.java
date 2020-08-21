
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script;

import java.util.Map;

import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.CachingMapVariableResolverFactory;

public class ReadOnlyCachingMapVariableResolverFactory extends CachingMapVariableResolverFactory
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public ReadOnlyCachingMapVariableResolverFactory(Map variables)
    {
        super(variables);
    }

    @Override
    public VariableResolver createVariable(String arg0, Object arg1)
    {
        if (this.getNextFactory() == null)
        {
            return null;
        }
        return this.nextFactory.createVariable(arg0, arg1);
    }

    @Override
    public VariableResolver createVariable(String arg0, Object arg1, Class<?> arg2)
    {
        if (this.getNextFactory() == null)
        {
            return null;
        }
        return this.nextFactory.createVariable(arg0, arg1, arg2);
    }

}
