
package com.asiainfo.veris.crm.order.pub.util;

import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;

public final class TextParseUtil
{

    /**
     * 获取占位符数据集iData
     * 
     * @author jiangjj
     * @param content
     * @return
     */
    private static IData analy(String content)
    {

        IData data = new DataMap();

        parseSet(data, content); // 解析数据集占位符
        parseOne(data, content); // 解析字符串占位符

        return data;
    }

    /**
     * 生成第一层map的值
     * 
     * @param holder
     *            key值
     * @return
     */
    private static IData crtSetMap(String holder)
    {

        IData map = new DataMap();

        String exp = holder.substring(holder.indexOf("{") + 1, holder.lastIndexOf("}"));
        IData subMap = parseSub(exp);

        map.put("SUBKEY", subMap);
        map.put("HOLDER", holder);
        map.put("_separator", holder.substring(holder.length() - 2, holder.length() - 1));
        map.put("_expression", exp);

        return map;
    }

    /**
     * 根据规则获得应替换的字符串
     * 
     * @author jiangjj
     * @param indata
     *            应替换数据集
     * @param subMap
     *            子Map数据集
     * @param exp
     *            数据集元素表达式
     * @param sep
     *            分割符
     * @param rootKey
     *            顶级key值
     * @return
     * @throws Exception
     * @throws Exception
     */
    private static String getSetValue(IData indata, IData subMap, String exp, String sep, String rootKey) throws Exception
    {

        StringBuilder readableStr = new StringBuilder();
        IDataset subList = indata.getDataset(rootKey);

        if (subList == null)
        {
            return null;
        }

        Set<String> keys = subMap.keySet();
        for (int i = 0; i < subList.size(); i++)
        {
            String tempExp = exp; // 重置 %SID!:%SNAME!
            IData unit = subList.getData(i); // 1 语音

            // 遍历subMap中的key值，取出indata中的应替换字符串
            for (Iterator iter = keys.iterator(); iter.hasNext();)
            {
                String key = (String) iter.next();
                String value = unit.getString(key);
                if (value != null)
                    tempExp = tempExp.replace("%" + key + "!", value);
            }

            if (i != 0) // 如果不是第一个同添加分割符
                readableStr.append(sep);
            readableStr.append(tempExp);
        }

        return readableStr.toString();
    }

    public static String parse(String str, IData inData) throws Exception
    {

        IData parseData = analy(str);
        String result = place(str, parseData, inData);
        return result;
    }

    /**
     * 统计字符串占位符信息
     * 
     * @author jiangjj
     * @param data
     *            占位符信息载体
     * @param smsContent
     *            短信内容
     */
    private static void parseOne(IData data, String content)
    {

        Matcher m = Pattern.compile("%[^%\\{!]+!").matcher(content);
        while (m.find())
        {
            String holder = m.group(); // %CUST_NAME!

            Matcher invalidMatcher = Pattern.compile("[\\{].*" + holder + ".*[\\}]").matcher(content);

            if (invalidMatcher.find()) // 如果满足{***%CUST_NAME!***}则不予统计
            {
                continue;
            }

            data.put(holder.substring(1, holder.length() - 1), "");
        }
    }

    /**
     * 解析iData占位符
     * 
     * @param data
     *            占位符信息载体
     * @param content
     *            短信模板内容
     */
    private static void parseSet(IData data, String content)
    {

        Matcher m = Pattern.compile("%[^%\\{!]+\\{[^\\}]+\\}.!").matcher(content);
        while (m.find())
        {
            String holder = m.group(); // %SS{%SID!:%SNAME!};!

            String key = holder.substring(1, holder.indexOf("{")); // SS
            IData value = crtSetMap(holder);

            data.put(key, value);
        }
    }

    /**
     * 解析子占位符
     * 
     * @param 数据集占位符表达式
     * @return
     */
    private static IData parseSub(String exp)
    {

        IData subMap = new DataMap();
        Matcher m = Pattern.compile("%[^\\{|^!]+!").matcher(exp);
        while (m.find())
        {
            String holder = m.group(); // %CUST_NAME!
            subMap.put(holder.substring(1, holder.length() - 1), "");
        }
        return subMap;
    }

    /**
     * 将含有占位符(形如"%...!"和"%XX{%...!:%...!};}!")短信模板解析为用户可读的字符串
     * 
     * @param str
     *            信息模板
     * @param parseData
     *            占位符号数据集
     * @param inData
     *            应替换数据集
     * @return
     * @throws Exception
     * @throws Exception
     */
    private static String place(String str, IData parseData, IData inData) throws Exception
    {

        String value = ""; // 替换的对象

        Set<String> keys = parseData.keySet();
        Iterator iterator = keys.iterator();

        while (iterator.hasNext())
        {
            String key = (String) iterator.next(); // SS
            Object type = parseData.get(key); // 获取一个规则中的第一个元素

            if (type instanceof String) // 如果是字符串占位符
            {
                value = inData.getString(key);

                if (value != null)
                {
                    str = str.replace("%" + key + "!", value);
                }
            }
            else if (type instanceof IData) // 如果是数据集占位符
            {
                IData map = (IData) type;

                IData subMap = map.getData("SUBKEY");
                String holder = map.getString("HOLDER");
                String exp = map.getString("_expression");
                String sep = map.getString("_separator");

                value = getSetValue(inData, subMap, exp, sep, key);

                if (value != null)
                {
                    str = str.replace(holder, value);
                }
            }
        }

        return str;
    }

}
