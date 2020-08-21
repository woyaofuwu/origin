
package com.asiainfo.veris.crm.order.pub.frame.bcf.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;

public final class StrUtil
{
    public static String chkParam(IData data, String strColName, String strDefault) throws Exception
    {

        String strParam = data.getString(strColName, strDefault == null ? "-1" : strDefault);

        if (strParam.equals("-1"))
        {
            String strError = "接口参数检查: 输入参数[" + strColName + "]不存在或者参数值为空";

            Utility.error("-1", null, strError);
        }

        return strParam;
    }

    /*
     * 转换 Flag字段
     */
    public static String comFlagField(String strFlag)
    {

        String strNewFlag = "0";

        if (strFlag.length() > 1)
        {
            strNewFlag = (strFlag == null) ? "0" : "1";
            return strNewFlag;
        }

        return strNewFlag;
    }

    /**
     * ip比较 0 目标ip和源ip相等 1 目标ip大于源ip相等 -1 目标ip小于源ip相等
     * 
     * @param ipsource
     * @param ip
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static int comparaIP(String ip, String ipsource) throws Exception
    {

        String[] iparray = ip.split("\\.");
        String[] ipsourcearry = ipsource.split("\\.");
        if (iparray.length != 4)
        {
            String message = "ip格式不正确,ip.len=" + iparray.length + "ip=" + ip;
            Utility.error("-1", null, message);
        }

        if (ipsourcearry.length != 4)
        {
            String message = "ip格式不正确,ipsource.len=" + ipsourcearry.length + "ipsource=" + ipsource;
            Utility.error("-1", null, message);
        }

        for (int i = 0; i < 4; i++)
        {
            int ipint = Integer.parseInt(iparray[i]);
            int ipsint = Integer.parseInt(ipsourcearry[i]);
            if (ipint > ipsint)
                return 1;
            if (ipint < ipsint)
                return -1;
        }
        return 0;
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
     * 
     * @param v1
     *            被除数
     * @param v2
     *            除数
     * @param scale
     *            表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale)
    {

        if (scale < 0)
        {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 浮点型转字符串
     * 
     * @param doubleFee
     * @return 字符串
     * @throws Exception
     */
    public static String doubleToStr(double doubleFee)
    {

        DecimalFormat df = new DecimalFormat("0.00");
        String strFee = (doubleFee > 0 ? df.format(doubleFee) : "0.00");
        return strFee.substring(0, strFee.indexOf('.'));
    }

    /**
     * 和find_first_of 是个反的 返回strValue中第一个在 strBunch 中找不到的 char 我们C++用法一般是判断一个字符串是不是全部是数字例如
     * strDiscntCode.find_first_not_of("0123456789") == string::nops 就判断strDiscntCode是不是存数字字符串 我们现在java 用法是
     * StrUtil.find_first_not_of(strDiscntCode, "0123456789") == -1 例如： strValue = "66008767" strBunch = "1234567890"
     * return -1 strValue = "66008767a" strBunch = "1234567890" return 8 strValue = "66a008767a" strBunch = "1234567890"
     * return 2
     * 
     * @param strValue
     * @param strBunch
     * @return
     * @throws Exception
     */
    public static int find_first_not_of(String strValue, String strBunch) throws Exception
    {
        int iResult = -1;
        for (int i = 0; i < strValue.length(); i++)
        {
            if (strBunch.indexOf(strValue.charAt(i)) < 0)
            {
                return i;
            }
        }
        return iResult;
    }

    /**
     * 返回所有strBunch中所有字符（字符：char），在strValue中第一次出现的位置 例如： strValue = "0123456789" strBunch = "9" return 8 strValue =
     * "0123456789" strBunch = "95" return 5 strValue = "0123456789" strBunch = "abc" return -1 C++ ::
     * strDiscntCode.find_first_of("0123456789") == string::nops java :: StrUtil.find_first_of(strDiscntCode,
     * "0123456789") == -1
     * 
     * @param strValue
     * @param strBunch
     * @return
     * @throws Exception
     */
    public static int find_first_of(String strValue, String strBunch) throws Exception
    {
        int iResult = -1;

        for (int i = 0; i < strValue.length(); i++)
        {
            if (strBunch.indexOf(strValue.charAt(i)) > -1)
            {
                return strBunch.indexOf(strValue.charAt(i));
            }
        }

        return iResult;
    }

    /**
     * 根据format格式转换数据
     * 
     * @param format
     * @param data
     * @return
     * @throws Exception
     * @author chenlei
     */
    public static String floatToStr(String format, Object data) throws Exception
    {

        DecimalFormat df = new DecimalFormat(format);
        return df.format(data);
    }

    public static String formatDecimal(String s, double pattern) throws Exception
    {

        return "no";
    }

    /**
     * 根据Id获取受理日
     * 
     * @param strId
     * @return
     */
    public static String getAcceptDayById(String strId)
    {

        String acceptDay = strId.substring(6, 8);

        return acceptDay;
    }

    /**
     * 根据Id获取受理月份
     * 
     * @param strId
     * @return
     */
    public static String getAcceptMonthById(String strId)
    {

        String acceptMonth = strId.substring(4, 6);

        return acceptMonth;
    }

    public static String getNotFuzzyKey() throws Exception
    {
        String notFuzzyKey = BizEnv.getEnvString("crm.svc.datafuzzy.notfuzzykey", "X_DATA_NOT_FUZZY");
        return notFuzzyKey;
    }

    /**
     * 根据Id获取分区标识后4位
     * 
     * @param strId
     * @return
     */
    public static String getPartition4ById(String strId)
    {

        return getPartitionNById(strId, 4);
    }

    /**
     * 根据Id获取分区标识后n位
     * 
     * @param strId
     * @return
     */
    public static String getPartitionNById(String strId, int n)
    {

        String partionId = "";
        partionId = strId.substring(strId.length() - n);
        partionId = Integer.parseInt(partionId) + "";// 去除前面的0
        return partionId;
    }

    /**
     * 获取指定长度的随机数字序列，长度限制为1-128位
     * 
     * @author Liuyt3
     */
    public static String getRandom(int length) throws Exception
    {

        if (length < 1 || length > 128)
        {
            Utility.error("-1", null, "请输入1-128的整数！");
        }
        StringBuilder sb = new StringBuilder("");
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++)
        {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static String getRandomChar() throws Exception
    {

        String[] charsArray =
        { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
        int randNum = (int) (Math.random() * (charsArray.length));
        return charsArray[randNum];
    }

    /**
     * 获取指定长度的随机数字序列，长度限制为1-128位
     * 
     * @author Liuyt3
     */
    public static String getRandomNumAndChar(int length) throws Exception
    {
        if (length < 1 || length > 128)
        {
            Utility.error("-1", null, "请输入1-128的整数！");
        }
        String[] data = new String[]
        { "0", "2", "3", "4", "5", "6", "7", "8", "9", "2", "3", "4", "6", "7", "8", "9", "0", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "M", "N", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "m", "n", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
        StringBuilder sb = new StringBuilder("");
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++)
        {
            sb.append(data[random.nextInt(70)]);
        }
        return sb.toString();
    }

    /**
     * 通过关键字获取字符串
     * 
     * @param key
     * @param data
     * @return
     * @author chenlei
     * @throws Exception
     */
    public static String getString(String key, IData data) throws Exception
    {

        return getString(key, data, true);
    }

    /**
     * 通过关键字获取字符串,为null则抛出异常
     * 
     * @param key
     * @param data
     * @param isNull
     *            是否允许为空
     * @return
     * @author chenlei
     */
    public static String getString(String key, IData data, boolean isNull) throws Exception
    {

        if ("".equals(key))
        {
            Utility.error("-1", null, "其他错误,KEY不能为空!");
        }
        if (data.get(key) instanceof IDataset)
        {
            Utility.error("-1", null, "[" + key + "]传值重复，该值只允许出现一次!");
        }
        String result = data.getString(key, "");
        if (!isNull && "".equals(result))
        {
            Utility.error("-1", null, "其他错误,[" + key + "]不能为空!");
        }
        return result;
    }

    /**
     * 从字段paramMeen set中获取key索引，再从值paramValue根据索引获取值
     * 
     * @param key
     * @param paramMeen
     * @param paramValue
     * @return
     * @author chenlei
     */
    public static String getString(String key, IDataset paramMeen, IDataset paramValue) throws Exception
    {

        if ("".equals(key))
        {
            // ly j2ee CSErr.err(IntfField.OTHER_ERR[0], "其他错误,KEY不能为空!");
        }
        int index = paramMeen.indexOf(key);
        if (index == -1)
        {
            return "";
        }
        if (paramValue.size() <= index)
        {
            // ly j2ee CSErr.err(IntfField.OTHER_ERR[0], "其他错误,VALUE索引位数不够!");
        }
        String result = paramValue.get(index).toString();
        return result;
    }

    public static String[] getValues(Object prop) throws Exception
    {
        return getValues(prop, ",");
    }

    public static String[] getValues(Object prop, String s) throws Exception
    {
        if (prop == null)
        {
            return new String[0];
        }
        if (prop instanceof String[])
        {
            return (String[]) prop;
        }
        else
        {
            if (prop.toString().startsWith("[") && prop.toString().endsWith("]"))
            {
                String props = prop.toString().substring(1, prop.toString().length() - 1);
                String[] pp = props.split(s);

                return pp;
            }
            else
            {
                return (new String[]
                { (String) prop });
            }
        }
    }

    /**
     * 比较日期时间是否一致
     * 
     * @param oldStr
     * @param newStr
     * @return
     */
    public static boolean monthCompare(String oldStr, String newStr)
    {
        if (StringUtils.isBlank(oldStr) || StringUtils.isBlank(newStr))
        {
            return false;
        }

        if (oldStr.length() < 10 || newStr.length() < 10)
        {
            return false;
        }

        String s1 = oldStr.substring(0, 10);
        String s2 = newStr.substring(0, 10);
        return s1.compareTo(s2) < 0;
    }

    /**
     * 逐位判断是否匹配
     * 
     * @param source
     * @param tar
     * @return
     * @author chenlei
     */
    public static boolean posContain(String source, String tar)
    {

        String element = "";
        for (int i = 0; i != source.length(); ++i)
        {
            element = source.charAt(i) + "";
            if (element.equals(tar))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 替换指定位置字符串
     * 
     * @param srcStr
     *            被替换字符串
     * @param desStr
     *            替换字符串
     * @param startIndex
     *            开始位置下标 从1计
     * @param endIndex
     *            结束位置下标(不含结束下标位置字符)从1计
     * @return 返回替换后字符串
     * @throws Exception
     */
    public static String replacStrByint(String srcStr, String desStr, int startIndex, int endIndex) throws Exception
    {
        StringBuilder sb = new StringBuilder(srcStr);

        sb.replace(startIndex - 1, endIndex, desStr);

        return sb.toString();
    }

    public static String str2In(String str) throws Exception
    {
        if (StringUtils.isBlank(str))
        {
            return null;
        }

        String s[] = str.split(",");

        for (int idx = 0, len = s.length; idx < len; idx++)
        {
            s[idx] = "'" + s[idx] + "'";
        }

        String in = StringUtils.join(s, ",");

        return in;
    }

    /**
     * 字符串转化
     * 
     * @author fanwenhui
     * @param resource
     * @param from
     * @param to
     * @return
     * @throws Exception
     */
    public static String strA2strB(String resource, String[] res, String[] des)
    {

        for (int i = 0; i < res.length; i++)
        {
            resource = resource.replace(res[i], des[i]);
        }
        return resource;
    }

    /**
     * 字符串分段方法
     * 
     * @param source
     * @param byteLength
     * @return
     */
    public static IDataset StringSubsection(String source, int byteLength)
    {

        if (source == null || source.length() == 0)
            return new DatasetList();
        byte[] sByte = source.getBytes();
        char[] sChar = source.toCharArray();
        IDataset dataset = new DatasetList();
        if (sByte.length <= byteLength)
        {
            dataset.add(source);
        }
        else
        {
            int byleCount = 0;
            int first = 0;
            for (int i = 0; i < sChar.length; i++)
            {
                if (sChar[i] > 0x80)
                {
                    byleCount += 2;
                }
                else
                {
                    byleCount += 1;
                }
                if (byleCount == byteLength)
                {
                    if (first == 0)
                        dataset.add(new String(sChar, first, i + 1));
                    else
                        dataset.add(new String(sChar, first + 1, i - first));
                    first = i;
                    byleCount = 0;
                }
                if (byleCount == byteLength + 1)
                {
                    if (first == 0)
                        dataset.add(new String(sChar, first, i));
                    else
                        dataset.add(new String(sChar, first + 1, i - first - 1));
                    first = i - 1;
                    byleCount = 2;
                }
            }
            if (byleCount != 0)
                dataset.add(new String(sChar, first + 1, sChar.length - first - 1));
        }
        return dataset;
    }

    public static String strLimit(String src, int slimit)
    {
        if (src == null)
        {
            return "";
        }

        byte[] bytes = src.getBytes();

        if (bytes.length <= slimit)
        {
            return src;
        }

        byte[] newbytes = new byte[slimit];

        for (int i = slimit; i > 0; i--)
        {
            newbytes[slimit - i] = bytes[bytes.length - i];
        }

        String desc = new String(newbytes);

        return desc;
    }

    /**
     * 字符串扩大到100
     * 
     * @param obj
     * @return
     * @author chenl
     * @date 2010-3-5
     */
    public static String strMulti100(Object obj)
    {

        String str;
        if (obj == null || "0".equals(obj.toString()))
        {
            return "0";
        }
        else
        {
            str = obj.toString().trim();
        }
        if (!str.contains("."))
        {
            return str + "00";
        }
        if (str.substring(str.indexOf(".") + 1).length() == 1)
        {// 1位小数
            return str.replaceAll("\\.", "") + "0";
        }
        if (str.substring(str.indexOf(".") + 1).length() == 2)
        {// 2位小数
            return str.replaceAll("\\.", "");
        }
        if (str.substring(str.indexOf(".") + 1).length() > 2)
        {// 大于2位小数
            int myPoint = str.indexOf(".");
            return str.substring(0, myPoint) + str.substring(myPoint + 1, myPoint + 3) + "." + str.substring(myPoint + 3);
        }
        return str;
    }

    /**
     * 根据二进制截取字符串. 参照com.linkage.bpmapp.utils.StringUtils 如：我和ni，截取5个字节:我和n。
     * 
     * @param input
     * @param maxLength
     * @param charset
     * @return
     */
    public static String substringb(String input, int maxLength, String charset)
    {

        StringBuilder out = new StringBuilder();
        byte[] bInput = input.getBytes();
        if (bInput.length < maxLength)
            return input;
        for (int i = 0; i < maxLength; i++)
        {
            if (bInput[i] < 0)
            {
                if (charset.toLowerCase().equals("utf-8"))
                {
                    byte[] bTmp = new byte[3];
                    bTmp[0] = bInput[i];
                    int curPos = ++i;
                    if (curPos >= maxLength)
                        break;
                    bTmp[1] = bInput[curPos];
                    curPos = ++i;
                    if (curPos >= maxLength)
                        break;
                    bTmp[2] = bInput[curPos];
                    String tmp = new String(bTmp);
                    out.append(tmp);
                }
                else
                {
                    byte[] bTmp = new byte[2];
                    bTmp[0] = bInput[i];
                    int curPos = ++i;
                    if (curPos >= maxLength)
                        break;
                    bTmp[1] = bInput[curPos];
                    String tmp = new String(bTmp);
                    out.append(tmp);
                }
            }
            else
            {
                out.append((char) bInput[i]);
            }
        }
        return out.toString();
    }
    
    /**
     * 字符转ASC
     * 
     * @param st
     * @return
     */
    public static int getAsc(String st) {
        byte[] gc = st.getBytes();
        int ascNum = (int) gc[0];
        return ascNum;
    }

    /**
     * ASC转字符
     * 
     * @param backnum
     * @return
     */
    public static char backchar(int backnum) {
        char strChar = (char) backnum;
        return strChar;
    }
}
