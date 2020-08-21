
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.function;

import java.util.Date;

import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * 系统默认函数
 */
public class SystemFunctions
{

    /**
     * 字符串包含比较(源串包含目标串)
     * 
     * @param str1
     *            源串
     * @param str2
     *            目标串
     * @return 包含为true，不包含为false
     */
    public boolean contains(String str1, String str2)
    {
        if (str1 == null || str2 == null)
        {
            return false;
        }
        return str1.indexOf(str2) >= 0;
    }

    /**
     * 获取特殊字符$($为函数调用符，表达式中不允许显式使用，需要使用本函数间接使用) <BR>
     * 
     * @return 返回$
     */
    public String dollar()
    {
        return "\\$";
    }

    /**
     * 字符串后缀比较(源串以目标串为结尾)
     * 
     * @param str1
     *            源串
     * @param str2
     *            目标串
     * @return 源串以目标串结尾为true，源串不以目标串结尾为false
     */
    public boolean endsWith(String str1, String str2)
    {
        if (str1 == null || str2 == null)
        {
            return false;
        }
        return str1.endsWith(str2);
    }

    /**
     * 判断字符串是否为空 <BR>
     * 
     * @param str
     *            字符串
     * @return 字符串为null或字符空或全为空格则为true，否则为false
     */
    public boolean isEmpty(String str)
    {
        if (null != str && str.trim().length() > 0)
        {
            return false;
        }
        return true;
    }

    /**
     * 左取num位字符串 <BR>
     * 
     * @param str
     *            源串
     * @param num
     *            截取位数
     * @return
     */
    public String leftString(String str, int num)
    {
        if (StringUtils.isEmpty(str))
        {
            return "";
        }
        return str.substring(0, str.length() > num ? num : str.length());
    }

    /**
     * 字符串长度 <BR>
     * 
     * @param str
     * @return
     */
    public int length(String str)
    {
        if (StringUtils.isEmpty(str))
        {
            return 0;
        }
        return str.length();
    }

    /**
     * 将字符串转换为数字 <BR>
     * 
     * @param numStr
     *            数字字符串
     * @return 字符串为空时返回0，否则为Integer.parseInt解析结果，未作数字字符校验，须自行保证传入为数字字符。
     */
    public int parseInt(String numStr)
    {
        if (StringUtils.isEmpty(numStr))
        {
            return 0;
        }
        return Integer.parseInt(numStr);
    }

    /**
     * 替换字符串 <BR>
     * 
     * @param source
     * @param regex
     * @param replacement
     * @return
     */
    public String replace(String source, String regex, String replacement)
    {
        if (StringUtils.isEmpty(source))
        {
            return "";
        }
        if (StringUtils.isEmpty(regex))
        {
            return source;
        }
        return source.replaceAll(regex, replacement);
    }

    /**
     * 右取num位字符串 <BR>
     * 
     * @param str
     *            源串
     * @param num
     *            截取位数
     * @return
     */
    public String rightString(String str, int num)
    {
        if (StringUtils.isEmpty(str))
        {
            return "";
        }
        return str.substring(str.length() > num ? (str.length() - num) : 0);
    }

    /**
     * 字符串前缀比较(源串以目标串开头)
     * 
     * @param str1
     *            源串
     * @param str2
     *            目标串
     * @return 源串以目标串开头为true，源串不以目标串开头为false
     */
    public boolean startsWith(String str1, String str2)
    {
        if (str1 == null || str2 == null)
        {
            return false;
        }
        return str1.startsWith(str2);
    }

    /**
     * 获取系统当前时间
     * 
     * @return
     */
    public Date sysDate()
    {
        return new Date();
    }

}
