
package com.asiainfo.veris.crm.order.soa.frame.bof.config;

import java.util.HashMap;
import java.util.Map;

import com.ailk.common.config.PropertiesConfig;

/**
 * @author Administrator
 */
public class BofProperties
{
    private static Map<String, String> properties = new HashMap();

    static
    {
        PropertiesConfig bofProperties = null;
        try
        {
            bofProperties = new PropertiesConfig("bof.properties");
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        properties = bofProperties.getProperties();
    }

    public static boolean getBofProductMode() throws Exception
    {
        String value = properties.get("bof.productMode");
        if ("true".equals(value))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String getProperty(String prop) throws Exception
    {
        String value = properties.get(prop);
        return value;
    }
}
