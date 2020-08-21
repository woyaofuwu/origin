
package com.asiainfo.veris.crm.order.soa.frame.bcf.plugin;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class PluginFactory
{
    private static Map proxyObjectMap = new HashMap();

    private static PluginFactory instance = new PluginFactory();

    public static PluginFactory getInstance()
    {
        return instance;
    }

    public static IDataset qryPluginImpls(String pluginCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PLUGIN_CODE", pluginCode);

        return Dao.qryByCode("TD_B_PLUGIN_IMPL", "SEL_BY_CODE", cond, Route.CONN_CRM_CEN);
    }

    public <T> T build(String pluginName, Class<T> t) throws Exception
    {
        // 如果缓存里有，则从缓存里取
        if (proxyObjectMap.containsKey(pluginName))
        {
            return (T) proxyObjectMap.get(pluginName);
        }

        IData cond = new DataMap();
        PluginHelper ph = new PluginHelper();

        IDataset plugins = qryPluginImpls(pluginName);
        int size = plugins.size();
        for (int i = 0; i < size; i++)
        {
            IData plugin = plugins.getData(i);
            String className = plugin.getString("PLUGIN_CLASS").trim();
            Object pluginObject = Class.forName(className).newInstance();
            ph.add(pluginObject);
        }

        ClassLoader loader = this.getClass().getClassLoader();
        Object proxyObject = Proxy.newProxyInstance(loader, new Class[]
        { t }, ph);

        // 加到缓存中
        proxyObjectMap.put(pluginName, proxyObject);

        return (T) proxyObject;
    }
}
