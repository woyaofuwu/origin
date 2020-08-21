
package com.asiainfo.veris.crm.order.soa.frame.bcf.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class PluginHelper implements InvocationHandler
{
    private List<Object> obs = new ArrayList<Object>();

    public void add(Object o)
    {
        obs.add(o);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        Object result = null;
        for (Object o : obs)
        {
            result = method.invoke(o, args);
        }
        return result;
    }
}
