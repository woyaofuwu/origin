
package com.asiainfo.veris.crm.order.soa.frame.bre.tools;

import java.util.Calendar;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.data.common.TimeUtil;

public final class BreTimeUtil extends BreBase
{
    private static final Logger logger = Logger.getLogger(BreTimeUtil.class);

    public static String getCurDate(IData databus) throws Exception
    {
        if (databus.isNoN("CUR_DATE"))
        {
            IBREDataPrepare data = new TimeUtil();
            data.run(databus);
        }

        return databus.getString("CUR_DATE");
    }

    public static String getFirstDayOfNextMonth(IData databus) throws Exception
    {
        if (databus.isNoN("FIRST_DAY_OF_NEXT_MONTH"))
        {
            IBREDataPrepare data = new TimeUtil();
            data.run(databus);
        }

        return databus.getString("FIRST_DAY_OF_NEXT_MONTH");
    }

    public static String getFirstDayOfThisMonth(IData databus) throws Exception
    {
        if (databus.isNoN("FIRST_DAY_OF_THIS_MONTH"))
        {
            IBREDataPrepare data = new TimeUtil();
            data.run(databus);
        }

        return databus.getString("FIRST_DAY_OF_THIS_MONTH");
    }

    public static String getLastDayOfCurMonth(IData databus) throws Exception
    {
        if (databus.isNoN("LAST_DAY_OF_CUR_MONTH"))
        {
            IBREDataPrepare data = new TimeUtil();
            data.run(databus);
        }

        return databus.getString("LAST_DAY_OF_CUR_MONTH");
    }

    public static String getSysdate(IData databus) throws Exception
    {
        if (databus.isNoN("CUR_DATE"))
        {
            IBREDataPrepare data = new TimeUtil();
            data.run(databus);
        }

        return databus.getString("CUR_DATE");
    }

    /**
     * 判断 strStart 是否只比 strEnd 小一秒钟 strStart ＋ 2s > strEnd
     * 
     * @param strStart
     * @param strEnd
     * @return
     * @throws Exception
     */
    public static boolean isContinuousTime(String strStartDate, String strEndDate) throws Exception
    {
        Calendar cStart = setCalendar(Calendar.getInstance(), strStartDate);
        Calendar cEnd = setCalendar(Calendar.getInstance(), strEndDate);

        cStart.add(Calendar.SECOND, 2);

        return cStart.after(cEnd);
    }

    public static Calendar setCalendar(Calendar cld, String strDate) throws Exception
    {
        return setCalendar(cld, strDate, "*", 0);
    }

    /**
     * 时间处理
     * 
     * @param cld
     * @param strDate
     * @param strType
     * @param iNum
     *            0-天;1-自然天;2-月;3-自然月;4-年;5-自然年
     * @return
     * @throws Exception
     */
    public static Calendar setCalendar(Calendar cld, String strDate, String strType, int iNum) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" strDate = [" + strDate + "] ");

        int YY = 0;
        int MM = 0;
        int DD = 0;
        int HH = 0;
        int MI = 0;
        int SS = 0;

        String[] str1 = strDate.split(" ");
        String[] str2 = str1[0].split("-");

        YY = Integer.parseInt(str2[0]);
        MM = Integer.parseInt(str2[1]);
        DD = Integer.parseInt(str2[2]);

        if (strDate.length() > 10)
        {
            String[] str3 = str1[1].split(":");
            HH = Integer.parseInt(str3[0]);
            MI = Integer.parseInt(str3[1]);
            SS = Integer.parseInt(str3[2]);
        }
        else
        {
            HH = 0;
            MI = 0;
            SS = 0;
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("set 之前的时间 ： " + YY + "-" + MM + "-" + DD + " " + HH + ":" + MI + ":" + SS);

        MM = MM - 1;

        if ("*".equals(strType))
        {
            cld.set(YY, MM, DD, HH, MI, SS);
        }
        else if ("0".equals(strType))
        {
            cld.set(YY, MM, DD, HH, MI, SS);
            cld.add(Calendar.DAY_OF_YEAR, iNum);
        }
        else if ("1".equals(strType))
        {
            cld.set(YY, MM, DD, 0, 0, 0);
            cld.add(Calendar.DATE, iNum);
        }
        else if ("2".equals(strType))
        {
            cld.set(YY, MM, DD, HH, MI, SS);
            cld.add(Calendar.MONTH, iNum);
        }
        else if ("3".equals(strType))
        {
            cld.set(YY, MM, 0, 0, 0, 0);
            cld.add(Calendar.MONTH, iNum);
        }
        else if ("4".equals(strType))
        {
            cld.set(YY, MM, DD, HH, MI, SS);
            cld.add(Calendar.YEAR, iNum);
        }
        else if ("5".equals(strType))
        {
            cld.set(YY, 0, 0, 0, 0, 0);
            cld.add(Calendar.YEAR, iNum);
        }
        else
        {
            cld.set(YY, MM, DD, HH, MI, SS);
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("set 之后的时间 ： " + cld.get(cld.YEAR) + "-" + (cld.get(cld.MONTH) + 1) + "-" + cld.get(cld.DAY_OF_MONTH) + " " + cld.get(cld.HOUR_OF_DAY) + ":" + cld.get(cld.MINUTE) + ":" + cld.get(cld.SECOND));

        return cld;
    }
}
