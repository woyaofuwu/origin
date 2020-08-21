
package com.asiainfo.veris.crm.order.soa.person.busi.wap;

public final class WapUtil
{

    /**
     * 品牌代码转换 输入值： G001：全球通 G002：神州行 G010：动感地带 返回值： 01：全球通；02：神州行；03：动感地带；09：其他品牌
     */
    public static String convertBrandCode(String brandCode)
    {
        if ((brandCode == null) || ("".equals(brandCode.trim())))
        {
            return "09";
        }
        brandCode = brandCode.trim();
        if ("G001".equals(brandCode))
        {
            return "01";
        }
        else if ("G002".equals(brandCode))
        {
            return "02";
        }
        else if ("G010".equals(brandCode))
        {
            return "03";
        }
        else
        {
            return "09";
        }
    }

    /**
     * 操作代码:一级BOSS侧->CRM侧 一级BOSS侧：01-业务开通 02-业务退订 CRM侧：06-服务定购 07-服务订购取消
     */
    public static String convertOperCode(String source)
    {
        String result = "";

        if ((source == null) || ("".equals(source.trim())))
        {
            return "";
        }

        if ("01".equals(source))
        {
            result = "06";
        }
        else if ("02".equals(source))
        {
            result = "07";
        }

        return result;
    }

    /**
     * 包含特殊字符的字符串转换 model: 1 编码 2 解码
     */
    public static String convertStr(String source, int model)
    {
        String result = "";
        if ((source == null) || ("".equals(source.trim())))
        {
            return "";
        }

        result = source;

        if (model == 1)
        {
            if (source.indexOf("+") > -1)
            {
                result = source.replaceAll("\\+", "%2B");
            }
        }
        else if (model == 2)
        {
            if (source.indexOf("%2B") > -1)
            {
                result = source.replaceAll("%2B", "+");
            }
        }

        return result;
    }
}
