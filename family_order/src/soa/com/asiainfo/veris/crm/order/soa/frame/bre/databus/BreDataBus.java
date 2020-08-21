
package com.asiainfo.veris.crm.order.soa.frame.bre.databus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreCacheHelp;

public final class BreDataBus extends HashMap<String, Object> implements IData
{
    private static final Logger logger = Logger.getLogger(BreDataBus.class);

    private static final long serialVersionUID = 5728540280422795959L;

    private static final String CLASS_STRING1 = "\"class\":";

    private static final String CLASS_REP_STRING1 = "\"_^CCBW^_\":";

    private static final String CLASS_STRING2 = "class";

    private static final String CLASS_REP_STRING2 = "_^CCBW^_";

    public static DataMap fromJSONObject(JSONObject object)
    {
        if (object != null)
        {
            DataMap data = new DataMap();
            Iterator keys = object.keys();
            while (keys.hasNext())
            {
                Object key = keys.next();
                Object value = object.get(key);

                if (((String) key).indexOf("_^CCBW^_") != -1)
                {
                    key = StringUtils.replace((String) key, "_^CCBW^_", "class");
                }

                if (value != null)
                {
                    if ((value instanceof JSONObject))
                    {
                        data.put((String) key, JSONUtils.isNull(value) ? null : fromJSONObject((JSONObject) value));
                    }
                    else if ((value instanceof JSONArray))
                    {
                        data.put((String) key, JSONUtils.isNull(value) ? null : new DatasetList(value.toString()));
                    }
                    else if ((value instanceof String))
                    {
                        data.put((String) key, value);
                    }
                    else
                        data.put((String) key, value);
                }
                else
                {
                    data.put((String) key, value);
                }
            }
            return data;
        }
        return null;
    }

    public BreDataBus()
    {
    }

    public BreDataBus(Map<String, Object> map)
    {
        super(map);
    }

    public BreDataBus(String jsonObject)
    {
        if ((jsonObject != null) && (jsonObject.indexOf("\"class\":") != -1))
        {
            jsonObject = StringUtils.replace(jsonObject, "\"class\":", "\"_^CCBW^_\":");
        }

        JSONObject map = JSONObject.fromObject(jsonObject);
        if (map != null)
            putAll(fromJSONObject(map));
    }

    public void existsLoadData(BreDataBus bus, String strName)
    {
        if (bus.isNoN(strName))
        {
            try
            {
                /* 根据规则脚本配置信息, 反射调用规则类.run方法 */
                Object obj = BreCacheHelp.getScriptObjectByScriptId(strName);

                if (obj != null)
                {
                    IBREDataPrepare script = (IBREDataPrepare) obj;
                    script.run(bus);
                }
            }
            catch (Exception e)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("BreDataBus.existsLoadData (" + strName + ") 报错了");
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean getBoolean(String strName)
    {
        return getBoolean(strName, false);
    }

    public boolean getBoolean(String strName, boolean defaultValue)
    {
        String value = getString(strName);
        return "".equals(value) ? defaultValue : Boolean.valueOf(value).booleanValue();
    }

    public IData getData(String strName)
    {
        existsLoadData(this, strName);
        if (isNoN(strName))
        {
            this.put(strName, new DataMap());
        }

        Object value = get(strName);
        if (value == null)
        {
            return null;
        }
        if ((value instanceof IData))
        {
            return (IData) value;
        }
        return null;
    }

    public IData getData(String strName, IData def)
    {
        Object value = super.get(strName);
        if (value == null)
        {
            return def;
        }
        if ((value instanceof IData))
        {
            return (IData) value;
        }
        return def;
    }

    public IDataset getDataset(String strName)
    {

        existsLoadData(this, strName);

        if (isNoN(strName))
        {
            this.put(strName, new DatasetList());
        }

        Object value = get(strName);
        if (value == null)
        {
            return null;
        }
        if ((value instanceof IDataset))
        {
            return (IDataset) value;
        }
        return null;
    }

    public IDataset getDataset(String strName, IDataset def)
    {
        Object value = super.get(strName);
        if (value == null)
        {
            return def;
        }
        if ((value instanceof IDataset))
        {
            return (IDataset) value;
        }
        return def;
    }

    public double getDouble(String strName)
    {
        return getDouble(strName, 0.0D);
    }

    public double getDouble(String strName, double defaultValue)
    {
        Object value = getString(strName);
        if (value == null || "".equals(value.toString()))
            return defaultValue;

        return Double.parseDouble(value.toString());
    }

    public int getInt(String strName)
    {
        return getInt(strName, 0);
    }

    public int getInt(String strName, int defaultValue)
    {
        Object value = getString(strName);
        if (value == null || "".equals(value.toString()))
            return defaultValue;
        return Integer.parseInt(value.toString());
    }

    public long getLong(String strName)
    {
        return getLong(strName, 0L);
    }

    public long getLong(String strName, long defaultValue)
    {
        Object value = getString(strName);
        if (value == null || "".equals(value.toString()))
            return defaultValue;
        return Long.parseLong(value.toString());
    }

    public String[] getNames()
    {
        String[] strNames = new String[size()];
        Iterator keys = keySet().iterator();
        int index = 0;
        while (keys.hasNext())
        {
            strNames[index] = ((String) keys.next());
            index++;
        }
        return strNames;
    }

    public String getString(String strName)
    {
        existsLoadData(this, strName);

        Object value = super.get(strName);
        if (value == null)
            return null;
        return value.toString();
    }

    public String getString(String strName, String defaultValue)
    {
        Object value = super.get(strName);
        if (value == null)
            return defaultValue;
        return value.toString();
    }

    public boolean isNoN(String strName)
    {
        return (strName == null) || (!containsKey(strName));
    }

    public IData put(String key, IData value)
    {
        return (IData) super.put(key, value);
    }

    public IDataset put(String key, IDataset value)
    {
        return (IDataset) super.put(key, value);
    }

    public String put(String key, String value)
    {
        return (String) super.put(key, value);
    }

    public IData subData(String group) throws Exception
    {
        return subData(group, false);
    }

    public IData subData(String group, boolean istrim) throws Exception
    {
        IData element = new DataMap();

        String[] strNames = getNames();
        for (String strName : strNames)
        {
            if (strName.startsWith(group + "_"))
            {
                element.put(istrim ? strName.substring((group + "_").length()) : strName, get(strName));
            }
        }

        return element;
    }

    @Override
    public String toString()
    {
        return JSONObject.fromObject(this).toString();
    }
}
