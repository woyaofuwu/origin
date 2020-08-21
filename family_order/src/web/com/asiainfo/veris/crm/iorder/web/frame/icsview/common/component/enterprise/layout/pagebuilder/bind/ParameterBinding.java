
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder.bind;

import org.apache.log4j.Logger;
import org.apache.tapestry.IBinding;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.ILocation;
import org.apache.tapestry.IResourceResolver;
import org.apache.tapestry.binding.ExpressionBinding;

public class ParameterBinding implements IBinding
{

    protected Logger log = Logger.getLogger(this.getClass());

    private IResourceResolver _resolver;

    private IComponent _component;

    private String _expression = null;

    private ILocation _location = null;

    private String _default = null;

    /**
     * 为XMLComponent创建可扩展的参数绑定
     * 
     * @param resolver
     * @param component
     * @param location
     * @param expression
     */
    public ParameterBinding(IResourceResolver resolver, IComponent component, ILocation location, String expression, String defval)
    {
        this._resolver = resolver;
        this._component = component;
        this._location = location;
        this._expression = expression;
        this._default = defval;
    }

    public boolean getBoolean()
    {
        Object value = getValue();

        if (value == null || "".equals(value))
            return _default == null || "".equals(_default) ? false : Boolean.valueOf(_default).booleanValue();
        else
        {
            return Boolean.valueOf(String.valueOf(value)).booleanValue();
        }
    }

    public double getDouble()
    {
        Object value = getValue();
        if (value == null || "".equals(value))
            return _default == null || "".equals(_default) ? 0.00 : Double.parseDouble(_default);
        else
            return Double.parseDouble(String.valueOf(value));
    }

    public int getInt()
    {
        Object value = getValue();
        if (value == null || "".equals(value))
            return _default == null || "".equals(_default) ? 0 : Integer.parseInt(_default);
        else
            return Integer.parseInt(String.valueOf(value));
    }

    public ILocation getLocation()
    {
        return _location;
    }

    public Object getObject()
    {
        Object value = getValue();
        return value;
    }

    public Object getObject(String parameterName, Class type)
    {
        Object value = getValue();
        return value == null || "".equals(_default) ? null : (Class) value;
    }

    public String getString()
    {
        Object value = getValue();
        if (value == null)
            return _default == null || "".equals(_default) ? "" : _default;
        else
            return String.valueOf(value);
    }

    private Object getValue()
    {
        if (_expression == null || "".equals(_expression))
            return "";

        int index = _expression.indexOf("ognl");

        if (_expression.equals(_default) && _default.indexOf("'") != -1)
            return _expression.replaceAll("'", "");

        if (index == -1)
            return _expression;
        else
            return new ExpressionBinding(_resolver, _component, _expression.substring(index + 5), _location).getObject();
    }

    public boolean isInvariant()
    {
        return true;
    }

    public void setBoolean(boolean value)
    {
    }

    public void setDouble(double value)
    {
    }

    public void setInt(int value)
    {
    }

    public void setObject(Object value)
    {
    }

    public void setString(String value)
    {
    }
}
