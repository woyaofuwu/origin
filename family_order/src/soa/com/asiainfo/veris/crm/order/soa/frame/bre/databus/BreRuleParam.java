
package com.asiainfo.veris.crm.order.soa.frame.bre.databus;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;

public final class BreRuleParam extends HashMap<String, String>
{

    private static final Logger logger = Logger.getLogger(BreRuleParam.class);

    public BreRuleParam()
    {
    }

    public int getInt(IData databus, String strName)
    {
        String strValue = this.getString(databus, strName);

        if (strValue == null || "".equals(strValue))
        {
            return 0;
        }

        return Integer.parseInt(strValue);
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

    public String getString(IData databus, String strName)
    {
        Object value = super.get(strName);

        if (logger.isDebugEnabled())
            logger.debug(" decode in  ===  >>  strName = [" + strName + "] ; strKey = [" + value + "]");

        if (value == null)
        {
            return null;
        }

        String strValue = value.toString();

        if (strValue.startsWith("%") && strValue.endsWith("!"))
        {
            String strParam = strValue.substring(1, strValue.length() - 1);
            if (strParam.indexOf(".") > 0)
            {
                String strObjName = strParam.substring(0, strParam.indexOf("."));
                String strFieldName = strParam.substring(strParam.indexOf(".") + 1, strParam.length());

                IDataset list = databus.getDataset(strObjName);

                if (list == null || list.size() == 0)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("databus 中没有获取到 " + strObjName + "." + strFieldName + " 数据! 返回空字符串出去，可能对程序判断造成影响！");
                        logger.debug(databus);
                    }
                }

                value = list.getData(0).getString(strFieldName);
            }
            else if (strParam.equalsIgnoreCase("SYSDATE"))
            {
                value = databus.getString(BreFactory.CUR_DATE);
            }
            else if (databus.containsKey(strParam))
            {
                value = databus.getString(strParam);
                if (value == null)
                {
                    StringBuilder strError = new StringBuilder("业务规则限制判断:获取参数域值出错！无法获取参数域值[").append(strParam).append("]");
                    if (logger.isDebugEnabled())
                    {
                        logger.debug(strError);
                    }

                    return "";
                }
            }
            else
            {

                StringBuilder strError = new StringBuilder("业务规则限制判断:获取参数域值出错！无法获取参数域值[").append(strParam).append("]");
                if (logger.isDebugEnabled())
                {
                    logger.debug(strError);
                }
                return "";
            }
        }
        else
        {
            if (logger.isDebugEnabled())
                logger.debug(" decode out1  ===  >>  strName = [" + strName + "] ; strValue = [" + strValue + "]");
            return strValue;
        }

        if (logger.isDebugEnabled())
            logger.debug(" decode out2  ===  >>  strName = [" + strName + "] ; strValue = [" + value.toString() + "]");

        return value.toString();
    }

    public String getString(String strName, String defaultValue)
    {
        Object value = super.get(strName);
        if (value == null)
            return defaultValue;
        return value.toString();
    }

    @Override
    public String put(String key, String value)
    {
        return super.put(key, value);
    }
}
