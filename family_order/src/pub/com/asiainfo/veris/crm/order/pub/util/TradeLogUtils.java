
package com.asiainfo.veris.crm.order.pub.util;

import java.util.Calendar;

import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;

public final class TradeLogUtils
{

    private static long iseq9 = 1L;

    private static long iseq6 = 1L;

    public static String get6BufSeq()
    {
        String r = "";
        if (iseq6 > 999999)
        {
            iseq6 = 0;
        }
        iseq6 = iseq6 + 1;
        String sbuf = String.valueOf(iseq6);
        char[] chars = sbuf.toCharArray();
        char[] c =
        { '0', '0', '0', '0', '0', '0' };
        if (chars.length < 9)
        {
            for (int i = 0; i < chars.length; i++)
            {
                c[6 - chars.length + i] = chars[i];
            }
            r = new String(c);
        }
        else
        {
            r = sbuf;
        }
        return r;
    }

    /**
     * @Function: getBufSeq
     * @Description: 内存序列产生算法
     * @param: @return
     * @return：String
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 下午2:26:44 2013年9月9日 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013年9月9日 tangxy v1.0.0 TODO:
     */
    public static String get9BufSeq()
    {
        String r = "";
        if (iseq9 > 999999999)
        {
            iseq9 = 0;
        }
        iseq9 = iseq9 + 1;
        String sbuf = String.valueOf(iseq9);
        char[] chars = sbuf.toCharArray();
        char[] c =
        { '0', '0', '0', '0', '0', '0', '0', '0', '0' };
        if (chars.length < 9)
        {
            for (int i = 0; i < chars.length; i++)
            {
                c[9 - chars.length + i] = chars[i];
            }
            r = new String(c);
        }
        else
        {
            r = sbuf;
        }
        return r;
    }

    /**
     * @Function: getFlowId
     * @Description: 获取错误日志表中的流水
     * @param: @return
     * @return：String
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 上午11:11:16 2013年9月18日 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013年9月18日 tangxy v1.0.0 TODO:
     */
    public static String getFlowId()
    {
        String fileName_2 = DateFormatUtils.format(Calendar.getInstance().getTime(), "yyyyMMddHHmmss");
        return fileName_2 + get6BufSeq();
    }
}
