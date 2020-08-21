
package com.asiainfo.veris.crm.order.pub.frame.bcf.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ailk.biz.util.TimeUtil;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;

public final class SysDateMgr
{
    public final static String END_DATE = " 23:59:59";

    public final static String END_TIME_FOREVER = "2050-12-31";

    public final static String END_DATE_FOREVER = "2050-12-31 23:59:59";
    
    public final static String END_TIME_FOREVER_2030 = "2030-12-31";
    
    public final static String END_DATE_FOREVER_2030 = "2030-12-31 23:59:59";

    public final static String PATTERN_STAND_SHORT = "yyyyMMddHHmmss";

    public final static String PATTERN_STAND = "yyyy-MM-dd HH:mm:ss";

    public final static String PATTERN_STAND_MS = "yyyy-MM-dd HH:mm:ss:S";

    public final static String PATTERN_STAND_YYYYMMDD = "yyyy-MM-dd";

    public final static String PATTERN_TIME_YYYYMMDD = "yyyyMMdd";

    public final static String START_DATE_FOREVER = " 00:00:00";

    public final static String PATTERN_CHINA_DATE = "yyyy年MM月dd日";

    public final static String PATTERN_CHINA_MONTH = "yyyy年MM月";

    public final static String PATTERN_CHINA_TIME = "yyyy年MM月dd日 HH时mm分ss秒";

    public final static String PATTERN_TIME_YYYYMM = "yyyyMM";

    public final static String PATTERN_STAND_DAY = "dd";

    private static String addAcctDay(String date, int acctOffset) throws Exception
    {
        AcctTimeEnv env = AcctTimeEnvManager.getAcctTimeEnv();

        if (env != null)
        {
            if (acctOffset == -1)
            {
                return SysDateMgr.getFirstDayThisMonth(date, env);
            }
            return addAcctDay(date, acctOffset, env.getAcctDay(), env.getFirstDate(), env.getNextAcctDay(), env.getNextFirstDate());
        }
        else
        {
            return getAddMonthsNowday(acctOffset, date);
        }
    }

    private static String addAcctDay(String date, int acctOffset, String acctDay, String firstDate, String bookAcctDay, String bookFirstDate) throws Exception
    {
        if (acctOffset == 0)
        {
            return date;
        }

        if (acctOffset < 0)
        {
            return date;
        }

        if (bookAcctDay == null || "".equals(bookAcctDay))
        {
            bookAcctDay = acctDay;
            bookFirstDate = firstDate;
        }

        boolean isNext = false;

        String today = getSysDate();

        String nowAcctLastDay = getLastDayThisAcct(today, acctDay, firstDate);

        if (date.compareTo(nowAcctLastDay) > 0)
        {
            isNext = true;
        }

        if (isNext)
        {

            if (acctOffset == 1)
            {
                String firstDateNextAcctDay = getFirstDayNextAcct(date, bookAcctDay, bookFirstDate);
                return firstDateNextAcctDay;
            }
            else
            {

                String firstDateNextAcctDay = getFirstDayNextAcct(date, bookAcctDay, bookFirstDate);
                firstDateNextAcctDay = SysDateMgr.date2String(DateUtils.addMonths(SysDateMgr.string2Date(firstDateNextAcctDay, "yyyy-MM-dd"), acctOffset - 1), "yyyy-MM-dd");

                return firstDateNextAcctDay;
            }
        }
        else
        {

            if (acctOffset == 1)
            {
                String firstDateNextAcctDay = getFirstDayNextAcct(date, acctDay, firstDate);
                return firstDateNextAcctDay;
            }
            else
            {

                String firstDateNextAcctDay = getFirstDayNextAcct(date, acctDay, firstDate);
                String nextNextAcctDay = getFirstDayNextAcct(firstDateNextAcctDay, bookAcctDay, bookFirstDate);
                nextNextAcctDay = SysDateMgr.date2String(DateUtils.addMonths(SysDateMgr.string2Date(nextNextAcctDay, "yyyy-MM-dd"), acctOffset - 2), "yyyy-MM-dd");

                return nextNextAcctDay;
            }
        }
    }

    private static Calendar addCalendarYears(String strDate, int year_offset) throws Exception
    {
        Date date = string2Date(strDate, PATTERN_STAND_YYYYMMDD);

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, year_offset);

        return c;
    }

    /** 在当前的基础上增减天数 */
    public static String addDays(int day_offset) throws Exception
    {
        long millis = currentTimeMillis();

        millis += (1000l * 3600 * 24 * day_offset);

        return DateFormatUtils.format(millis, PATTERN_STAND_YYYYMMDD);
    }
    
    /** 在当前的基础上增减天数  返回类型增加PATTERN_STAND*/
    public static String addDays2(int day_offset) throws Exception
    {
        long millis = currentTimeMillis();

        millis += (1000l * 3600 * 24 * day_offset);

        return DateFormatUtils.format(millis, PATTERN_STAND);
    }
    /**
     * 时间计算＋＋天数 YYYY-MM-DD
     * 
     * @return
     * @throws Exception
     */
    public static String addDays(String strDate, int iDays) throws Exception
    {
        Date dtNew = DateUtils.addDays(string2Date(strDate, PATTERN_STAND_YYYYMMDD), iDays);
        return date2String(dtNew, PATTERN_STAND_YYYYMMDD);
    }

    public static String addMonths(String date, int month_offset) throws Exception
    {
        Date dtNew = DateUtils.addMonths(SysDateMgr.string2Date(date, SysDateMgr.PATTERN_STAND_YYYYMMDD), month_offset);

        return SysDateMgr.date2String(dtNew, SysDateMgr.PATTERN_STAND_YYYYMMDD);
    }

    /**
     * 增加多少秒
     * 
     * @param date
     * @param Second
     * @return
     * @throws Exception
     */
    public static String addSecond(String date, int Second) throws Exception
    {
        date = SysDateMgr.decodeTimestamp(date, SysDateMgr.PATTERN_STAND);
        Date dtNew = DateUtils.addSeconds(SysDateMgr.string2Date(date, SysDateMgr.PATTERN_STAND), Second);
        return SysDateMgr.date2String(dtNew, SysDateMgr.PATTERN_STAND);
    }

    public static String addYears(String date, int year_offset) throws Exception
    {
        Calendar c = addCalendarYears(date, year_offset);

        return DateFormatUtils.format(c, PATTERN_STAND_YYYYMMDD);
    }

    /** 按自然年偏移 */
    public static String addYearsNature(String date, int yearOffset) throws Exception
    {
        String d = addYears(date, yearOffset - 1);
        return d.substring(0, 4) + "-12-31 23:59:59";
    }

    public static String cancelDate(String cancel_tag, String cancel_absolute_date, String cancel_offset, String cancel_unit) throws Exception
    {
        if (StringUtils.isNotBlank(cancel_absolute_date))
        {
            return cancel_absolute_date;
        }
        switch (cancel_tag.charAt(0))
        {
            case '0':
                return getSysTime();
            case '1':
                return getYesterdayDate() + getEndTime235959();
            case '2':
                return getSysDate() + getEndTime235959();
            case '3':
                return getAddMonthsLastDay(1);
            case '4':
                Utility.error("-1", null, "该元素不允许主动取消");
                return "NO_SUPPORT_CANCEL"; // 不支持单笔取消，但支持产品变更中继承
            case '7':
            	return getSysTime();	//默认立即可选本账期末
            case '8':
            	return getSysTime();	//删除可输任意时间  默认立即
        	case '9':
            	return getAddMonthsLastDay(1);	//默认本账期末可选立即
            default:
                return null;
        }
    }

    public static long currentTimeMillis() throws Exception
    {
        return TimeUtil.currentTimeMillis(true);
    }

    /**
     * 把日期从Date转化为String
     * 
     * @param aDate
     * @param pattern
     * @author huyong
     * @return
     */
    public static String date2String(Date aDate, String pattern)
    {
        if (null == aDate)
        {
            return null;
        }

        return DateFormatUtils.format(aDate, pattern);
    }

    /**
     * 获得2个时间的日差值(YYYY-MM-DD HH24:MI:SS)
     * 
     * @return 日差值，返回的是绝对值
     * @throws Exception
     */
    public static int dayInterval(String strDate1, String strDate2) throws Exception
    {
        Date date1 = string2Date(strDate1, PATTERN_STAND_YYYYMMDD);
        Date date2 = string2Date(strDate2, PATTERN_STAND_YYYYMMDD);

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        long allDays = ((c2.getTimeInMillis()) - (c1.getTimeInMillis())) / (1000 * 24 * 60 * 60);

        return Math.abs(Integer.parseInt(String.valueOf(allDays)));
    }

    public static String decodeTimestamp(String date, String pattern) throws Exception
    {

        return Utility.decodeTimestamp(pattern, date);
    }

    public static Timestamp encodeTimestamp(String timeStr) throws Exception
    {
        return TimeUtil.encodeTimestamp(timeStr);
    }

    public static Timestamp encodeTimestamp(String format, String timeStr) throws Exception
    {
        return TimeUtil.encodeTimestamp(format, timeStr);
    }

    /** 根据配置计算结束时间 */
    public static String endDate(String start_date, String end_enable_tag, String end_absolute_date, String end_offset, String end_unit) throws Exception
    {

        if ("0".equals(end_enable_tag))
        {
            // 0:绝对时间
            return end_absolute_date;
        }
        else if ("1".equals(end_enable_tag))
        {
            // 1:相对时间
            return endDateOffset(start_date, end_offset, end_unit);
        }
        else if ("2".equals(end_enable_tag))
        {
            // 2:手工录入
            return getTheLastTime();
        }

        return getTheLastTime();
    }

    /** 结束时间偏移 */
    public static String endDateOffset(String date, String offset, String unit) throws Exception
    {

        String endDate = date;
        if (offset == null || offset.equals("") || unit == null || unit.equals(""))
        {
            return getTheLastTime();
        }
        if (offset.equals("0"))
        {
            // 解决如果偏移量为0，返回的时间会是date减去一个偏移量后的时间
            return date;
        }
        if ("0".equals(unit))
        { // 按天偏移
            endDate = date2String(DateUtils.addDays(string2Date(date, PATTERN_STAND_YYYYMMDD), Integer.parseInt(offset)-1), PATTERN_STAND_YYYYMMDD) + getEndTime235959();
        }
        else if ("1".equals(unit))
        { // 按自然天偏移
            endDate = date2String(DateUtils.addDays(string2Date(date, PATTERN_STAND_YYYYMMDD), Integer.parseInt(offset)-1), PATTERN_STAND_YYYYMMDD) + getEndTime235959();
        }
        else if ("2".equals(unit))
        {
        	if(ProvinceUtil.isProvince(ProvinceUtil.SHXI) )
        	{
        		 Date dateOffsetMonth = DateUtils.addMonths(string2Date(date, PATTERN_STAND_YYYYMMDD), Integer.parseInt(offset));
        		 dateOffsetMonth = DateUtils.addDays(dateOffsetMonth, -1);
        		 endDate = date2String(dateOffsetMonth, PATTERN_STAND_YYYYMMDD) + getEndTime235959();
        	}else
        	{
        		 endDate = date2String(DateUtils.addMonths(string2Date(date, PATTERN_STAND_YYYYMMDD), Integer.parseInt(offset)), PATTERN_STAND_YYYYMMDD) + getEndTime235959();
        	}
        }
        else if ("3".equals(unit))
        {
            endDate = getAddMonthsLastDay(Integer.parseInt(offset), date);
        }
        else if ("4".equals(unit))
        { // 按年偏移
            endDate = getAddMonthsLastDay(Integer.parseInt(offset) * 12, date);
        }
        else if ("5".equals(unit))
        { // 按自然年偏移(取年底)
            endDate = addYearsNature(date, Integer.parseInt(offset));
            endDate = getAddMonthsLastDay(1, endDate);
        }
        else if (ProvinceUtil.isProvince(ProvinceUtil.QHAI) && "6".equals(unit))//青海按小时
        {
            endDate = getAddHoursDate(date, Integer.parseInt(offset));
        }
        return endDate;
    }

    /** 获取指定时间N月后的第一天 */
    public static String firstDayOfDate(String strDate, int month_offset) throws Exception
    {
        AcctTimeEnv timeEnv = AcctTimeEnvManager.getAcctTimeEnv();
        if (timeEnv != null)
        {
            if (month_offset == -1)
            {
                return SysDateMgr.getFirstDayThisMonth(strDate, timeEnv);
            }
            return SysDateMgr.addAcctDay(strDate, month_offset, timeEnv.getAcctDay(), timeEnv.getFirstDate(), timeEnv.getNextAcctDay(), timeEnv.getNextFirstDate());
        }
        Date date = DateUtils.addMonths(string2Date(strDate, PATTERN_STAND_YYYYMMDD), month_offset);

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);

        return DateFormatUtils.format(c, PATTERN_STAND_YYYYMMDD);
    }

    /** 获取N月后的第一天 */
    public static String firstDayOfMonth(int month_offset) throws Exception
    {
        String nowSysDate = getSysDate();
        return firstDayOfDate(nowSysDate, month_offset);
    }

    /**
     * @Function: firstDayOfMonth
     * @Description: 获取指定月份 month_offset月后第一天
     * @param strData
     * @param month_offset
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年7月7日 下午5:09:13
     */
    public static String firstDayOfMonth(String strData, int month_offset) throws Exception
    {

        return firstDayOfDate(strData, month_offset);
    }

    /**
     * 根据传入的时间，算出该时间偏移几个小时后的值
     * 
     * @return 时间YYYY-MM-DD HH24:MI:SS字符串格式
     * @throws Exception
     */
    public static String getAddHoursDate(String date, int hours) throws Exception
    {
        date = decodeTimestamp(date, PATTERN_STAND);
        Date dtNew = DateUtils.addHours(string2Date(date, PATTERN_STAND), hours);
        return date2String(dtNew, PATTERN_STAND);
    }

    /**
     * 获取 N个月后的最后一天
     * 
     * @author huyong 2012-02-03修改,方法名没有修改
     * @return
     * @throws Exception
     */
    public static String getAddMonthsLastDay(int offset) throws Exception
    {
        String nowSysDate = getSysDate();
        return getAddMonthsLastDay(offset, nowSysDate);
    }

    /**
     * 获取指定时间的N个月后的最后一天，在偏移的时候是计算了当前月的 如当前月份是2013-08-05，偏移4个月，那么得到的时间是2013-11-30 23:59:59
     * 
     * @param iMonths
     * @return
     * @throws Exception
     * @author SUNXIN
     */
    public static String getAddMonthsLastDay(int iMonths, String strDate) throws Exception
    {
        AcctTimeEnv env = AcctTimeEnvManager.getAcctTimeEnv();
        if (env != null)
        {
            if (iMonths == -1)
            {
                return SysDateMgr.getPreviousMonthLastDay(strDate, env);
            }
            String tempNextFirstAcctDay = SysDateMgr.addAcctDay(strDate, iMonths, env.getAcctDay(), env.getFirstDate(), env.getNextAcctDay(), env.getNextFirstDate());
            return SysDateMgr.addDays(tempNextFirstAcctDay, -1) + getEndTime235959();
        }
        else
        {
            if (iMonths > 0)
            {
                iMonths = iMonths - 1;
            }
            Date dtNew = DateUtils.addMonths(string2Date(strDate, PATTERN_STAND_YYYYMMDD), iMonths);
            return date2String(getMonthEndTime(dtNew), PATTERN_STAND_YYYYMMDD) + getEndTime235959();
        }
    }
    
	public static String getAddMonthsLastDayNoEnv(int iMonths, String strDate)throws Exception
	{
		if (iMonths > 0)
		{
			iMonths = iMonths - 1;
		}
		Date dtNew = DateUtils.addMonths(string2Date(strDate, PATTERN_STAND_YYYYMMDD), iMonths);
		return date2String(getMonthEndTime(dtNew), PATTERN_STAND_YYYYMMDD)+ getEndTime235959();
	}
    /**
     * 获取指定月＋iMonths月
     * 
     * @param iMonths
     * @return
     * @throws Exception
     * @author SUNXIN
     */
    public static String getAddMonthsNowday(int iMonths, String dateString) throws Exception
    {
        String format = Utility.getTimestampFormat(dateString);
        Date dtNew = DateUtils.addMonths(string2Date(dateString, format), iMonths);
        return date2String(dtNew, format);
    }

    /**
     * 获取注销时失效时间
     * 
     * @param Date
     * @param Mode
     * @return
     */
    public static String getCancelDate(String startDate, String endDate, String sysDate) throws Exception
    {

        String cancelDate = endDate.substring(0, 10);
        // 未生效时注销
        if (sysDate.compareTo(startDate) < 0)
        {
            // 当前时间减一秒
            return getLastSecond(sysDate);
        }
        // 当前时间
        if (cancelDate.compareTo(sysDate) == 0)
        {
            return sysDate;
        }
        // 非立即生效
        else
        {
            return cancelDate + getEndTime235959();
        }
    }

    /**
     * @Function: getChinaDate
     * @Description: 返回xxxx年xx月xx日
     * @param strDate
     * @param pattern
     *            yyyy年MM月dd日
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-6-11 上午10:05:21
     */
    public static String getChinaDate(String strDate, String pattern) throws Exception
    {

        if (strDate.trim().length() == 10)
        {
            return DateFormatUtils.format(string2Date(strDate, PATTERN_STAND_YYYYMMDD), pattern);
        }
        else
        {
            return DateFormatUtils.format(string2Date(strDate, PATTERN_STAND), pattern);
        }
    }

    /* 获取当前时间的日 */
    public static String getCurDay() throws Exception
    {
        String day = getSysDate("dd");
        return String.valueOf(Integer.parseInt(day));
    }

    /**
     * 获取这个月
     * 
     * @author anwx
     * @return
     * @throws Exception
     */
    public static String getCurMonth() throws Exception
    {
        String day = getSysDate("MM");
        return String.valueOf(Integer.parseInt(day));
    }

    /**
     * 根据传入的long,格式化字符串 获取时间
     * 
     * @param time
     * @param pattern
     * @return
     * @throws Exception
     */
    public static String getDateByTimeMillis(long time, String pattern) throws Exception
    {
        if (StringUtils.isNotEmpty(pattern))
        {
            return DateFormatUtils.format(time, pattern);
        }

        return DateFormatUtils.format(time, PATTERN_STAND);
    }

    /**
     * 将日期格式yyyyMMdd转成yyyy-MM-dd
     * 
     * @param date
     * @return
     * @throws Exception
     * @author chenlei
     */
    public static String getDateForSTANDYYYYMMDD(String date) throws Exception
    {

        if (date != null && date.length() == 8)
        {
            StringBuilder dateSb = new StringBuilder(20);
            dateSb = dateSb.append(date.substring(0, 4)).append("-").append(date.substring(4, 6)).append("-").append(date.substring(6));
            date = dateSb.toString();
        }
        return date;
    }

    /**
     * 按指定格式获取时间格式
     * 
     * @param date
     * @return
     * @throws Exception
     * @author chenlei
     */
    public static String getDateForYYYYMMDD(String date) throws Exception
    {

        if (date != null && date.length() >= 10)
        {
            date = date.replaceAll("-", "");
            date = date.substring(0, 8);
        }
        return date;
    }

    /**
     * 获取指定时间的所在月的最后一天
     * 
     * @author fanwg
     * @date 2009-9-12
     * @param dateString
     * @return
     * @throws Exception
     */
    public static String getDateLastMonthSec(String dateString) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) || ProvinceUtil.isProvince(ProvinceUtil.TJIN) || ProvinceUtil.isProvince(ProvinceUtil.SHXI))
        {
            return getAddMonthsLastDay(1, dateString);
        }

        return getAddMonthsLastDay(0, dateString);
    }

    /**
     * 获取指定时间的下个月的第一天
     * 
     * @author fanwg
     * @date 2009-9-12
     * @param dateString
     * @return
     * @throws Exception
     */
    public static String getDateNextMonthFirstDay(String dateString) throws Exception
    {
        return firstDayOfDate(dateString, 1);
    }

    public static int getDayIntervalNoAbs(String strDate1, String strDate2) throws Exception
    {
        Date date1 = string2Date(strDate1, PATTERN_STAND_YYYYMMDD);
        Date date2 = string2Date(strDate2, PATTERN_STAND_YYYYMMDD);

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        long allDays = ((c2.getTimeInMillis()) - (c1.getTimeInMillis())) / (1000 * 24 * 60 * 60);

        return Integer.parseInt(String.valueOf(allDays));
    }

    public static String getEnableDate(String date) throws Exception
    {

        if (date == null || date.length() < 10)
        {
            return null;
        }

        String enableDate = date.substring(0, 10);
        String sysDate = getSysTime();
        String tempSysDate = sysDate.substring(0, 10);

        // 当前时间
        if (enableDate.compareTo(tempSysDate) == 0)
        {
            return sysDate;
        }
        else
        {
            // 非立即生效
            return enableDate + SysDateMgr.getFirstTime00000();
        }
    }

    /**
     * 获取生效时间
     * 
     * @param Date
     * @param Mode
     * @return
     */
    public static String getEnableDate(String date, String sysDate) throws Exception
    {

        String enableDate = date.substring(0, 10);
        // 当前时间 或者比系统时间小
        if (enableDate.compareTo(sysDate.substring(0, 10)) <= 0)
        {
            return sysDate;
        }
        else
        // 非立即生效
        {
            return enableDate + getFirstTime00000();
        }
    }

    // 获取END_CYCLE_ID(年月日6位数)
    public static String getEndCycle205012() throws Exception
    {
        return "205012";
    }

    // 获取END_CYCLE_ID(年月日8位数)
    public static String getEndCycle20501231() throws Exception
    {

        return "20501231";
    }

    /**
     * 取时间的前10位+' 23:59:59'
     * 
     * @author anwx@asiainfo-linkage.com @ 2013-5-3
     * @param date
     * @return
     * @throws Exception
     */
    public static String getEndDate(String date) throws Exception
    {
        return date.substring(0, 10) + getEndTime235959();
    }

    /**
     * 获取结束时间 格式2050-12-31 23:59:59
     * 
     * @param sysDate
     * @param startDate
     * @param endDate
     * @param ifBooking
     * @param cancelTag
     * @param effectNow
     * @return
     * @throws Exception
     */
    public static String getEndDate(String sysDate, String startDate, String endDate, Boolean ifBooking, String cancelTag, Boolean effectNow, String modifyTag) throws Exception
    {
        if (StringUtils.isBlank(sysDate))
        {
            sysDate = SysDateMgr.getSysTime();
        }

        if (StringUtils.isBlank(cancelTag))
        {
            cancelTag = "0";
        }

        if (startDate == null || startDate.length() < 10 || endDate == null || endDate.length() < 10)
        {
            return null;
        }

        String enableStartDate = startDate.substring(0, 10);
        String enableEndDate = endDate.substring(0, 10);
        String enableSysDate = sysDate.substring(0, 10);

        // 元素新增的情况
        if ("0".equals(modifyTag))
        {
            return enableEndDate + getEndTime235959();
        }

        // 如果元素未生效(元素开始时间大于系统时间), 注销到当前时间的前一秒
        if (enableStartDate.compareTo(enableSysDate) > 0)
        {
            return SysDateMgr.getLastSecond(sysDate);
        }

        // 立即 取accept_date时间后半段拼接
        if (effectNow)
        {
            return enableEndDate + sysDate.substring(10);
        }

        // 预约
        if (ifBooking)
        {
            return enableEndDate + getEndTime235959();
        }

        // 结束时间等于当前时间
        if (enableEndDate.compareTo(enableSysDate) == 0)
        {
            String lastDayThisAcct = addDays(SysDateMgr.getFirstDayOfNextMonth(), -1);

            // 当前时间等于本账期末
            if (enableSysDate.compareTo(lastDayThisAcct) == 0)
            {
                // 0立即取消
                if ("0".equals(cancelTag))
                {
                    return sysDate;
                }
                else
                {
                    return enableEndDate + getEndTime235959();
                }
            }
            else
            {
                return sysDate;
            }
        }

        // 结束时间大于当前时间
        if (enableEndDate.compareTo(enableSysDate) > 0)
        {
            return enableEndDate + getEndTime235959();
        }

        return enableEndDate + getEndTime235959();
    }

    public static String getEndTime235959() throws Exception
    {
        return " 23:59:59";
    }

    /**
     * 作用：生成执行时间： 1.默认完工的(RSRV_STR1 0-否,1-是默认)，在当前时间上加上等待回复时间 2.默认返回2050
     * 
     * @author luojh 2050-5-17 create
     * @param attrValue
     *            等待时间
     * @return
     * @throws Exception
     */
    public static String getExecTime(String attrValue) throws Exception
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis());

        // 将当前执行时间设置为当前时间加预约时间
        calendar.add(Calendar.HOUR, Integer.parseInt(attrValue));

        return DateFormatUtils.format(calendar, PATTERN_STAND);
    }

    /**
     * 获取某个时间点的所处帐期的第一天YYYY-MM-DD格式
     * 
     * @param oneDate
     *            某个时间
     * @param acctDay
     *            oneDate处于的帐期的结帐日
     * @param firstDate
     *            oneDate处于的帐期的首次结帐日
     * @param startDate
     *            oneDate处于的帐期的开始时间
     * @return YYYY-MM-DD格式
     * @throws Exception
     */
    public static String getFirstCycleDayThisAcct(String oneDate, String acctDay, String firstDate, String startDate) throws Exception
    {
        int lastDay = SysDateMgr.getMonthMaxDay(oneDate);
        if (lastDay < Integer.parseInt(acctDay))
        {
            acctDay = lastDay + "";
        }

        String firstDayThisAcct = SysDateMgr.setDay(oneDate, acctDay);

        if (firstDayThisAcct.compareTo(oneDate) > 0)
        {

            firstDayThisAcct = SysDateMgr.getAddMonthsNowday(-1, firstDayThisAcct);
        }
        if (firstDayThisAcct.compareTo(firstDate) < 0)
        {
            firstDayThisAcct = startDate;
        }
        return firstDayThisAcct;
    }

    /**
     * 获取下帐期第一天
     * 
     * @param oneDate
     *            yyyy-mm-dd格式
     * @param acctDay
     * @param firstDate
     * @return
     * @throws Exception
     */
    public static String getFirstDayNextAcct(String oneDate, String acctDay, String firstDate) throws Exception
    {
        int lastDay = SysDateMgr.getMonthMaxDay(oneDate);

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            if (lastDay < Integer.parseInt(acctDay))
            {
                acctDay = lastDay + "";
            }

            String firstDayNextAcct = SysDateMgr.setDay(oneDate, acctDay);
            if (oneDate.compareTo(firstDayNextAcct) >= 0)
            {
                firstDayNextAcct = SysDateMgr.getAddMonthsNowday(1, firstDayNextAcct);
            }

            if (firstDayNextAcct.compareTo(firstDate) < 0)
            {
                firstDayNextAcct = firstDate;
            }

            return firstDayNextAcct;
        }

        /*
         * if (lastDay < Integer.parseInt(acctDay)) { acctDay = lastDay + ""; } String firstDayNextAcct =
         * SysDateMgr.setDay(oneDate, acctDay); if (oneDate.compareTo(firstDayNextAcct) >= 0) { firstDayNextAcct =
         * SysDateMgr.getAddMonthsNowday(1, firstDayNextAcct); }
         */

        // =============START=======================
        // 修复帐期为29,30,31时，帐期结束时间出错;如帐期为30号，2月只有28天，这样原有逻辑会在3月计算两遍帐期，导致整个帐期少一个月。
        // 如lastDay >= acctDay,直接计算下一帐期;
        // 如lastDay < acctDay,先计算下个月的最后一天，在计算下一帐期。
        String firstDayNextAcct = null;
        if (lastDay >= Integer.parseInt(acctDay))
        {
            firstDayNextAcct = SysDateMgr.setDay(oneDate, acctDay);
            if (oneDate.compareTo(firstDayNextAcct) >= 0)
            {
                firstDayNextAcct = SysDateMgr.getAddMonthsNowday(1, firstDayNextAcct);
            }
        }
        else
        {
            String nextOneDate = SysDateMgr.getAddMonthsNowday(1, oneDate);
            int nextlastDay = SysDateMgr.getMonthMaxDay(nextOneDate);
            if (nextlastDay >= Integer.parseInt(acctDay))
            {
                firstDayNextAcct = SysDateMgr.setDay(nextOneDate, acctDay);
            }
            else
            {
                firstDayNextAcct = SysDateMgr.setDay(nextOneDate, nextlastDay + "");
            }
        }
        // ==============END======================

        if (firstDayNextAcct.compareTo(firstDate) < 0)
        {
            firstDayNextAcct = firstDate;
        }

        return firstDayNextAcct;
    }

    /**
     * 获取下月第一天 YYYY-MM-DD
     * 
     * @return
     * @throws Exception
     */
    public static String getFirstDayOfNextMonth() throws Exception
    {
        AcctTimeEnv timeEnv = AcctTimeEnvManager.getAcctTimeEnv();
        if (timeEnv != null)
        {
            return SysDateMgr.getFirstDayNextAcct(SysDateMgr.getSysDate(), timeEnv.getAcctDay(), timeEnv.getFirstDate());
        }
        else
        {
            return firstDayOfMonth(1);
        }
    }

    /**
     * @Function: getFirstDayOfNextMonth
     * @Description: 获取指定日期下月第一天
     * @param strDate
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年7月7日 下午5:07:01
     */
    public static String getFirstDayOfNextMonth(String strDate) throws Exception
    {

        AcctTimeEnv timeEnv = AcctTimeEnvManager.getAcctTimeEnv();
        if (timeEnv != null)
        {
            return SysDateMgr.getFirstDayNextAcct(strDate, timeEnv.getAcctDay(), timeEnv.getFirstDate());
        }
        else
        {
            return firstDayOfMonth(strDate, 1);
        }
    }

    /**
     * 获取下月第一天 YYYY-MM-DD 给前台调用
     * 
     * @return
     * @throws Exception
     */
    public static String getFirstDayOfNextMonth4WEB() throws Exception
    {
        String nowSysDate = getSysDate();

        Date date = DateUtils.addMonths(string2Date(nowSysDate, PATTERN_STAND_YYYYMMDD), 1);

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);

        return DateFormatUtils.format(c, PATTERN_STAND_YYYYMMDD);

    }

    /**
     * 获取本月的第一天
     * 
     * @param cal
     * @author huyong
     * @return
     */
    public static String getFirstDayOfThisMonth() throws Exception
    {
        AcctTimeEnv env = AcctTimeEnvManager.getAcctTimeEnv();
        if (env != null)
        {
            return SysDateMgr.getFirstCycleDayThisAcct(getSysDate(), env.getAcctDay(), env.getFirstDate(), env.getStartDate());
        }
        return firstDayOfMonth(0);
    }

    /**
     * 获取本月的第一天
     * 
     * @return
     * @throws Exception
     */
    public static String getFirstDayOfThisMonth4WEB() throws Exception
    {
        String nowSysDate = getSysDate();

        Date date = DateUtils.addMonths(string2Date(nowSysDate, PATTERN_STAND_YYYYMMDD), 0);

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);

        return DateFormatUtils.format(c, PATTERN_STAND_YYYYMMDD);

    }

    private static String getFirstDayThisMonth(String oneDate, AcctTimeEnv env) throws Exception
    {
        String nowAcctLastDay = getLastDayThisAcct(getSysDate(), env.getAcctDay(), env.getFirstDate());
        boolean isNext = false;
        if (oneDate.compareTo(nowAcctLastDay) > 0)
        {
            isNext = true;
        }
        String thisMonthFirstDate = "";
        String nextAcctDay = env.getAcctDay();
        String nextFirstDate = env.getFirstDate();
        String nextStartDate = env.getStartDate();
        if (StringUtils.isNotBlank(env.getNextAcctDay()))
        {
            nextAcctDay = env.getNextAcctDay();
            nextFirstDate = env.getNextFirstDate();
            nextStartDate = env.getNextStartDate();
        }
        if (isNext)
        {
            thisMonthFirstDate = SysDateMgr.getFirstCycleDayThisAcct(oneDate, nextAcctDay, nextFirstDate, nextStartDate);
        }
        else
        {
            thisMonthFirstDate = SysDateMgr.getFirstCycleDayThisAcct(oneDate, env.getAcctDay(), env.getFirstDate(), env.getStartDate());
        }
        return thisMonthFirstDate;
    }

    public static String getFirstTime00000()
    {
        return " 00:00:00";
    }

    /**
     * 根据某个DATE获取它的DAY
     * 
     * @param date
     * @return
     * @throws Exception
     */
    public static int getIntDayByDate(String date) throws Exception
    {
        return Integer.parseInt(getStringDayByDate(date));
    }

    /**
     * 获取上月帐期
     * 
     * @return
     * @throws Exception
     */
    public static String getLastCycle() throws Exception
    {
        String nowSysDate = getSysTime();
        return decodeTimestamp(getAddMonthsNowday(-1, nowSysDate), "yyyyMM");
    }

    /**
     * 获取上月帐期
     * 
     * @return
     * @throws Exception
     */
    public static String getLastCycle(String format) throws Exception
    {
        String nowSysDate = getSysTime();
        return decodeTimestamp(getAddMonthsNowday(-1, nowSysDate), format);
    }

    /**
     * 获取本月最后一天的账期
     * 
     * @return YYYYMMDD
     * @throws Exception
     *             wangjx 2013-9-4
     */
    public static String getLastCycleThisMonth() throws Exception
    {
        return decodeTimestamp(getAddMonthsLastDay(1), PATTERN_TIME_YYYYMMDD);
    }

    /**
     * 获取本月最后一天(yyyy-MM-dd HH:mm:ss)
     * 
     * @author Liuy4
     * @return
     * @throws Exception
     */
    public static String getLastDateThisMonth() throws Exception
    {
        return getAddMonthsLastDay(1);
    }

    /**
     * 获取本月最后一天(yyyy-MM-dd HH:mm:ss)
     * 
     * @return
     * @throws Exception
     */
    public static String getLastDateThisMonth4WEB() throws Exception
    {
        String nowSysDate = getSysDate();

        Date dtNew = DateUtils.addMonths(string2Date(nowSysDate, PATTERN_STAND_YYYYMMDD), 0);

        return date2String(getMonthEndTime(dtNew), PATTERN_STAND_YYYYMMDD) + getEndTime235959();

    }

    /** 获取某月的最后一天 */
    public static String getLastDayOfMonth(int month_offset) throws Exception
    {
        return getAddMonthsLastDay(month_offset);
    }

    /**
     * 功能: 获得今年的最后一天 修改时间: 2009-5-13 下午08:00:12 修改者：tangxy
     * 
     * @return
     * @throws Exception
     */
    public static String getLastDayOfThisYear() throws Exception
    {
        return addYearsNature(getSysDate(), 1);
    }

    public static String getLastDayThisAcct(String oneDate, String acctDay, String firstDate) throws Exception
    {
        String lastDayThisAcct = getFirstDayNextAcct(oneDate, acctDay, firstDate);

        return addDays(lastDayThisAcct, -1);
    }

    /**
     * 获取上月第一天(yyyymmdd);
     * 
     * @author deagle
     * @return
     * @throws Exception
     */
    public static String getlastMonthFirstDate() throws Exception
    {
        return firstDayOfMonth(-1);
    }

    /*
     * @return @throws Exception @author zhaoyi 获取上个月最后的时间
     */
    public static String getLastMonthLastDate() throws Exception
    {
        AcctTimeEnv timeEnv = AcctTimeEnvManager.getAcctTimeEnv();
        if (timeEnv != null)
        {
            return addDays(SysDateMgr.getFirstCycleDayThisAcct(getSysDate(), timeEnv.getAcctDay(), timeEnv.getFirstDate(), timeEnv.getStartDate()), -1);
        }
        return getAddMonthsLastDay(-1);
    }

    /**
     * 根据传入的时间，算出该时间前一秒的时间
     * 
     * @return 时间YYYY-MM-DD HH24:MI:SS字符串格式
     * @throws Exception
     */
    public static String getLastSecond(String date) throws Exception
    {
        date = decodeTimestamp(date, PATTERN_STAND);
        Date dtNew = DateUtils.addSeconds(string2Date(date, PATTERN_STAND), -1);
        return date2String(dtNew, PATTERN_STAND);
    }

    /**
     * 根据传入的时间获得当前日期最早时间
     * 
     * @return 当前日期最早时间
     * @throws Exception
     */
    public static String getLateDateByDate(String dateString) throws Exception
    {
        return firstDayOfDate(dateString, 0);
    }

    /**
     * 获得本月的最后时间
     * 
     * @param cal
     * @author huyong
     * @return
     */
    private static Date getMonthEndTime(Calendar cal)
    {

        Calendar tmpCalendar = (Calendar) cal.clone();
        tmpCalendar.set(Calendar.DAY_OF_MONTH, 1);
        tmpCalendar.set(Calendar.HOUR_OF_DAY, 0);
        tmpCalendar.set(Calendar.MINUTE, 0);
        tmpCalendar.set(Calendar.SECOND, 0);
        tmpCalendar.set(Calendar.MILLISECOND, 0);
        tmpCalendar.add(Calendar.MONTH, 1);
        tmpCalendar.add(Calendar.MILLISECOND, -1);// 往前移动1毫秒,即调到上个月最后一天
        return tmpCalendar.getTime();
    }

    /**
     * @param date
     * @author huyong
     * @return
     */
    private static Date getMonthEndTime(Date date)
    {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getMonthEndTime(cal);
    }

    /**
     * 根据传入的时间，获得月份值
     * 
     * @author lixiuyu
     * @return
     * @throws Exception
     */
    public static String getMonthForDate(String date) throws Exception
    {
        Date dtNew = string2Date(date, PATTERN_STAND_YYYYMMDD);
        return date2String(dtNew, "MM");
    }

    public static int getMonthMaxDay(String oneDate) throws Exception
    {
        DateFormat format = new SimpleDateFormat(PATTERN_STAND_YYYYMMDD);
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(oneDate));
        int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        return lastDay;
    }

    // 顺延时间计算
    public static String getNewEndDateByNewStartDateAndOldDate(String startDate, String endDate, String startDateChanged) throws Exception
    {
        Date _startDate = string2Date(startDate, PATTERN_STAND_YYYYMMDD);
        Date _endDate = string2Date(endDate, PATTERN_STAND_YYYYMMDD);
        Date _startDateChanged = string2Date(startDateChanged, PATTERN_STAND_YYYYMMDD);

        long time = _startDateChanged.getTime() + _endDate.getTime() - _startDate.getTime();

        String result = DateFormatUtils.format(time, PATTERN_STAND_YYYYMMDD);

        return result;
    }

    /**
     * 获取下月帐期
     * 
     * @return
     * @throws Exception
     */
    public static String getNextCycle() throws Exception
    {
        String nowSysDate = getSysTime();
        return decodeTimestamp(getAddMonthsNowday(1, nowSysDate), "yyyyMM");
    }

    /**
     * 获取下月最后一天
     * 
     * @author huyong
     * @author deagle
     * @return
     * @throws Exception
     */
    public static String getNextMonthLastDate() throws Exception
    {
        return getAddMonthsLastDay(2, getSysDate());
    }

    /**
     * 根据传入的时间，算出该时间后一秒的时间
     * 
     * @return 时间YYYY-MM-DD HH24:MI:SS字符串格式
     * @throws Exception
     */
    public static String getNextSecond(String date) throws Exception
    {
        date = decodeTimestamp(date, PATTERN_STAND);
        Date dtNew = DateUtils.addSeconds(string2Date(date, PATTERN_STAND), 1);
        return date2String(dtNew, PATTERN_STAND);
    }

    public static String getNowCyc() throws Exception
    {
        return getSysDate("yyyyMM");
    }

    public static String getNowCycle() throws Exception
    {
        return getSysDate("yyyyMMdd");
    }

    /**
     * 获取今年
     * 
     * @author tangxy
     * @return
     * @throws Exception
     */
    public static String getNowYear() throws Exception
    {
        return getSysDate("yyyy");
    }

    /**
     * 获得当前时间前（正数）或者后（负数）几秒的时间， 并以时间格式 YYYY-MM-DD HH24:MI:SS 输出
     * 
     * @author Caorl
     * @author huyong 2012-01-12修改,方法名没有修改
     * @param seconds
     *            当前时间的后几秒或前几秒，可以有正负号
     * @return
     * @throws Exception
     */
    public static String getOtherSecondsOfSysDate(int seconds) throws Exception
    {
        String nowSysDate = getSysTime();
        Date dtNew = DateUtils.addSeconds(string2Date(nowSysDate, PATTERN_STAND), seconds);
        return date2String(dtNew, PATTERN_STAND);
    }

    private static String getPreviousMonthLastDay(String oneDate, AcctTimeEnv env) throws Exception
    {
        String thisMonthFirstDay = SysDateMgr.getFirstDayThisMonth(oneDate, env);
        return SysDateMgr.addDays(thisMonthFirstDay, -1);
    }

    /**
     * 根据某个DATE获取它的DAY
     * 
     * @param date
     * @return
     * @throws Exception
     */
    public static String getStringDayByDate(String date) throws Exception
    {
        return SysDateMgr.decodeTimestamp(date, PATTERN_STAND_DAY);
    }

    public static String getSysDate() throws Exception
    {
        return TimeUtil.getSysDate(PATTERN_STAND_YYYYMMDD, true);
    }

    /**
     * 获得当前时间(以pattern指定格式) huyong 2012-1-11
     * 
     * @param pattern
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static String getSysDate(String pattern) throws Exception
    {
        return TimeUtil.getSysDate(pattern, true);
    }

    /**
     * 获得系统当前时间YYYYMMDD格式 20140707
     * 
     * @return yyyyMMdd
     * @throws Exception
     * @author huanghui
     */
    public static String getSysDateYYYYMMDD() throws Exception
    {
        return TimeUtil.getSysDate(PATTERN_TIME_YYYYMMDD, true);
    }

    /**
     * 获取当前系统时间YYYYMMDDHHMMSS格式20140717100312
     * 
     * @returnYYYYMMDDHHMMSS
     * @throws Exception
     * @author chenyi
     */
    public static String getSysDateYYYYMMDDHHMMSS() throws Exception
    {
        return TimeUtil.getSysDate(PATTERN_STAND_SHORT, true);
    }

    public static String getSysTime() throws Exception
    {
        return TimeUtil.getSysDate(PATTERN_STAND, true);
    }

    /**
     * 获取当前数据库时间YYYYMMDDHHMMSS格式20140717100312
     * 
     * @param isLocal
     * @return
     * @throws Exception
     */
    public static String getSysTime(boolean isLocal) throws Exception
    {
        return TimeUtil.getSysDate(PATTERN_STAND, isLocal);
    }

    /**
     * @author Liuy4
     * @return
     * @throws Exception
     */
    public static String getTheLastTime() throws Exception
    {
        return END_DATE_FOREVER;
    }

    /**
     * 获取给定时间的月份数
     */
    public static String getTheMonth(String datePtr) throws Exception
    {
        Date dtNew = string2Date(datePtr, PATTERN_STAND_YYYYMMDD);
        int mm = Integer.parseInt(date2String(dtNew, "MM"));
        return String.valueOf(mm);
    }

    public static long getTimeDiff(String startTime, String endTime, String pattern) throws Exception
    {
        long diff = -1;

        try
        {
            Date startDate = string2Date(startTime, pattern);

            Date endDate = string2Date(endTime, pattern);

            diff = endDate.getTime() - startDate.getTime();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return diff;
    }

    /**
     * 获取上月今天的日期,格式为yyyy-MM-dd
     * 
     * @author chenhao 2009-2-23
     * @return String 上月今天的日期
     * @throws Exception
     */
    public static String getTodayLastMonth() throws Exception
    {
        String nowSysDate = getSysDate();
        Date dtNew = DateUtils.addMonths(string2Date(nowSysDate, PATTERN_STAND_YYYYMMDD), -1);
        return date2String(dtNew, PATTERN_STAND_YYYYMMDD);
    }

    /**
     * 获取明天的日期(yyyy-MM-dd)
     * 
     * @return
     * @throws Exception
     */
    public static String getTomorrowDate() throws Exception
    {
        return addDays(1);
    }

    /**
     * 获取上月明天的日期,格式为yyyy-MM-dd
     * 
     * @return String 上月明天的日期
     * @throws Exception
     */
    public static String getTomorrowLastMonth() throws Exception
    {
        String nowSysDate = getSysDate();
        Date dtNew = DateUtils.addMonths(string2Date(nowSysDate, PATTERN_STAND_YYYYMMDD), -1);
        Date lastMonthDay = DateUtils.addDays(dtNew, 1);
        return date2String(lastMonthDay, PATTERN_STAND_YYYYMMDD);
    }

    public static String getTomorrowTime() throws Exception
    {
        String date = getSysTime();
        date = decodeTimestamp(date, PATTERN_STAND);
        Date dtNew = DateUtils.addDays(string2Date(date, PATTERN_STAND), 1);
        return date2String(dtNew, PATTERN_STAND);
    }

    /**
     * 获取昨天的日期(yyyy-MM-dd)
     * 
     * @return
     * @throws Exception
     */
    public static String getYesterdayDate() throws Exception
    {
        String nowSysDate = getSysDate();
        Date dtNew = DateUtils.addDays(string2Date(nowSysDate, PATTERN_STAND_YYYYMMDD), -1);
        return date2String(dtNew, PATTERN_STAND_YYYYMMDD);
    }

    // 获取24小时之前的时间
    public static String getYesterdayTime() throws Exception
    {
        String nowSysTime = SysDateMgr.getSysTime();
        Date dtNew = DateUtils.addDays(string2Date(nowSysTime, PATTERN_STAND_YYYYMMDD), -1);

        return date2String(dtNew, SysDateMgr.PATTERN_STAND);
    }

    /**
     * 获得2个时间的月差值(YYYY-MM-DD HH24:MI:SS)
     * 
     * @return 月差值的绝对值
     * @throws Exception
     */
    public static int monthInterval(String strDate1, String strDate2) throws Exception
    {
        Date date1 = string2Date(strDate1, PATTERN_STAND_YYYYMMDD);
        Date date2 = string2Date(strDate2, PATTERN_STAND_YYYYMMDD);

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        int month;
        month = (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR)) * 12 + (c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH));

        return Math.abs(month) + 1;
    }

    public static int monthIntervalNoAbs(String strDate1, String strDate2) throws Exception
    {
        Date date1 = string2Date(strDate1, PATTERN_STAND_YYYYMMDD);
        Date date2 = string2Date(strDate2, PATTERN_STAND_YYYYMMDD);

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        int month;
        month = (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR)) * 12 + (c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH));

        return month;
    }

    /**
     * 获得2个时间的月差值(YYYYMM)
     * 
     * @return 月差值
     * @throws Exception
     */
    public static int monthIntervalYYYYMM(String strDate1, String strDate2) throws Exception
    {
        Date date1 = string2Date(strDate1, "yyyyMM");
        Date date2 = string2Date(strDate2, "yyyyMM");

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        int month;
        month = (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR)) * 12 + (c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH));

        return month;
    }

    /**
     * 是否同一个月
     * 
     * @param oldStr
     * @param newStr
     * @return
     */
    public static boolean sameMonthCompare(String oldStr, String newStr) throws Exception
    {

        Date date1 = string2Date(oldStr, "yyyy-MM");
        Date date0 = string2Date(newStr, "yyyy-MM");

        boolean flag = false;

        if (date0.compareTo(date1) == 0)
        {
            flag = true;
        }

        return flag;
    }

    private static String setDay(String nowDate, String acctDay) throws Exception
    {
        Calendar c = Calendar.getInstance();

        DateFormat format = new SimpleDateFormat(PATTERN_STAND_YYYYMMDD);

        c.setTime(format.parse(nowDate));

        int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (lastDay < Integer.parseInt(acctDay))
        {
            acctDay = lastDay + "";
        }

        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(acctDay));

        return format.format(c.getTime());
    }

    /** 根据配置计算开始时间 */
    public static String startDate(String enable_tag, String start_absolute_date, String start_offset, String start_unit) throws Exception
    {

        String start_date = "";
        if ("0".equals(enable_tag))
        { // 0:立即生效
            start_date = startDateOffset(getSysTime(), start_offset, start_unit);
        }
        else if ("1".equals(enable_tag))
        { // 1:下帐期生效
            start_date = startDateOffset(firstDayOfMonth(1), start_offset, start_unit);
        }
        else if ("2".equals(enable_tag))
        { // 2:次日生效
            start_date = startDateOffset(getTomorrowDate(), start_offset, start_unit);
        }
        else if ("3".equals(enable_tag))
        { // 3:可选立即或下帐期生效
            start_date = startDateOffset(firstDayOfMonth(1), start_offset, start_unit);
        }
        else if ("4".equals(enable_tag))
        { // 4:绝对时间
            start_date = start_absolute_date;
        }
        else if ("6".equals(enable_tag))
        { //  开始时间可自选任意 默认立即
            start_date = startDateOffset(getSysTime(), start_offset, start_unit);
        }
        else
        {
            start_date = getSysTime();
        }
        return start_date;
    }

    /** 指定预约时间,算开始时间 */
    public static String startDateBook(String fixDate, String enable_tag, String start_absolute_date, String start_offset, String start_unit) throws Exception
    {

        String start_date;

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            if ("0".equals(enable_tag))
            { // 0:立即生效
                start_date = startDateOffset(fixDate, start_offset, start_unit);
            }
            else if ("1".equals(enable_tag))
            { // 1:下帐期生效
                start_date = startDateOffset(fixDate, "1", "2");
            }
            else if ("2".equals(enable_tag))
            { // 2:次日生效
                start_date = startDateOffset(date2String(DateUtils.addDays(string2Date(fixDate, PATTERN_STAND), 1), PATTERN_STAND), start_offset, start_unit);
            }
            else if ("3".equals(enable_tag))
            { // 3:可选立即或下帐期生效
                // start_date = startDateOffset(firstDayOfMonth(fixDate, 1),
                // start_offset, start_unit);

                start_date = startDateOffset(fixDate, "1", "2");
            }
            else if ("4".equals(enable_tag))
            { // 4:绝对时间
                int iresult = start_absolute_date.compareTo(fixDate);// 比较2个日期的大小
                if (iresult >= 0)
                {
                    start_date = start_absolute_date;
                }
                else
                {
                    start_date = fixDate;
                }
            }
            else
            {
                start_date = getSysTime();
            }

            return start_date;
        }

        if ("0".equals(enable_tag))
        { // 0:立即生效
            start_date = startDateOffset(fixDate, start_offset, start_unit);
        }
        else if ("1".equals(enable_tag))
        { // 1:下帐期生效
            start_date = startDateOffset(getFirstDayOfThisMonth(), start_offset, start_unit);
        }
        else if ("2".equals(enable_tag))
        { // 2:次日生效
            start_date = startDateOffset(date2String(DateUtils.addDays(string2Date(fixDate, PATTERN_STAND), 1), PATTERN_STAND), start_offset, start_unit);
        }
        else if ("3".equals(enable_tag))
        { // 3:可选立即或下帐期生效
            // start_date = startDateOffset(firstDayOfMonth(fixDate, 1),
            // start_offset, start_unit);

            start_date = startDateOffset(getFirstDayOfThisMonth(), start_offset, start_unit);
        }
        else if ("4".equals(enable_tag))
        { // 4:绝对时间
            int iresult = start_absolute_date.compareTo(fixDate);// 比较2个日期的大小
            if (iresult >= 0)
            {
                start_date = start_absolute_date;
            }
            else
            {
                start_date = fixDate;
            }
        }
        if ("6".equals(enable_tag))
        { // 开始时间可自选任意 默认立即
            start_date = startDateOffset(fixDate, start_offset, start_unit);
        }
        else
        {
            start_date = getSysTime();
        }
        return start_date;
    }

    /** 开始时间偏移 */
    public static String startDateOffset(String date, String offset, String unit) throws Exception
    {

        String startDate = date;
        if (offset == null || offset.equals("") || unit == null || unit.equals(""))
        {
            return date;
        }

        if ("0".equals(unit))
        { // 按天偏移
            startDate = date2String(DateUtils.addDays(string2Date(date, PATTERN_STAND_YYYYMMDD), Integer.parseInt(offset)), PATTERN_STAND_YYYYMMDD);
        }
        else if ("1".equals(unit))
        { // 按自然天偏移
            startDate = date2String(DateUtils.addDays(string2Date(date, PATTERN_STAND_YYYYMMDD), Integer.parseInt(offset)), PATTERN_STAND_YYYYMMDD);
        }
        else if ("2".equals(unit))
        {
            startDate = addAcctDay(date, Integer.parseInt(offset));
        }
        else if ("3".equals(unit))
        {
        	if(ProvinceUtil.isProvince(ProvinceUtil.SHXI))
        	{
        		startDate = firstDayOfMonth(date,Integer.parseInt(offset));
        	}
        	else
        	{
        		startDate = addAcctDay(date, Integer.parseInt(offset));
        	}
            
        }
        else if ("4".equals(unit))
        { // 按年偏移
            startDate = addAcctDay(date, Integer.parseInt(offset) * 12);
        }
        else if ("5".equals(unit))
        { // 按自然年偏移(取年底)
            startDate = addYearsNature(date, Integer.parseInt(offset));
        }
        else if (ProvinceUtil.isProvince(ProvinceUtil.QHAI) && "6".equals(unit))//青海按小时
        {
            startDate = getAddHoursDate(date, Integer.parseInt(offset));
        }
        return startDate;
    }
    
    /**
     * 把时间串按照响应的格式转换成日期对象
     * 
     * @param strDate
     *            时间串
     * @param format
     *            指定的格式
     * @return 返回java.util.Date的对象,转换失败时返回当前的时间对象
     */
    public static Date string2Date(String strDate, String format) throws Exception
    {
        if (null == strDate)
        {
            throw new NullPointerException();
        }
        DateFormat df = new SimpleDateFormat(format);

        return df.parse(strDate);
    }

    // 如果传入时间不包含时分秒，则给加上时分秒
    // i为0,则加上00:00:00
    // i为1,则加上23:59:59
    public static String suffixDate(String date, int i) throws Exception
    {

        StringBuilder suffix = new StringBuilder();
        if (i == 0)
        {
            suffix.append(getFirstTime00000());
        }
        else
        {
            suffix.append(getEndTime235959());
        }
        if (date.length() < 12)
        {
            date = date + suffix.toString();
        }
        return date;
    }

    /**
     * 先写了用于短信 2013-08-14 19:56:19格式 转换成 2013年08月14日19时56分格式，不支持错误格式
     * 
     * @param givenTime
     * @return
     * @throws Exception
     */
    public static String transTime(String givenTime) throws Exception
    {
        // TODO 可以扩展支持
        if (givenTime != null && givenTime.length() > 16)
        {
            return givenTime.substring(0, 4) + "年" + givenTime.substring(5, 7) + "月" + givenTime.substring(8, 10) + "日" + givenTime.substring(11, 13) + "时" + givenTime.substring(14, 16) + "分";
        }

        return givenTime;
    }

    /**
     * 根据配置将永久结束时间，转换成暂无
     * 
     * @param result
     * @throws Exception
     * @author awx 这个方法先注释，后续加上
     * @date Aug 10, 2010
     */
    // public static void transEndDate(IDataset result) throws Exception
    // {
    // IDataset set = BofQuery.getParamInfoByAttr("2030");
    // for (int i = 0; i < result.size(); i++)
    // {
    // String end_date = result.getData(i).getString("END_DATE", "");
    // if (!end_date.equals(""))
    // {
    // for (int j = 0; j < set.size(); j++)
    // {
    // // PARAM_CODE
    // String compareDate = set.getData(j).getString("PARAM_CODE", "");
    // if (compareDate.length() >= 4)
    // {
    // if (end_date.substring(0, 4).equals(compareDate.substring(0, 4)))
    // {
    // end_date = "暂无";
    // result.getData(i).put("END_DATE", end_date);
    // break;
    // }
    // }
    // else
    // {
    // continue;
    // }
    // }
    // }
    // else
    // {
    // continue;
    // }
    // }
    // }
    /**
     * 获得2个时间的年差值
     * 
     * @return 年差值的绝对值
     * @throws Exception
     */
    public static int yearInterval(String strDate1, String strDate2) throws Exception
    {
        Date date1 = string2Date(strDate1, PATTERN_STAND_YYYYMMDD);
        Date date2 = string2Date(strDate2, PATTERN_STAND_YYYYMMDD);

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        int year;
        year = (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR));

        return Math.abs(year);
    }
    
    public static int compareTo(String date, String compareDate) throws Exception
    {
         int result;
         
         date = SysDateMgr.decodeTimestamp(date, SysDateMgr.PATTERN_STAND);
         compareDate = SysDateMgr.decodeTimestamp(compareDate, SysDateMgr.PATTERN_STAND);
         
         if(date.substring(0, 10).equals(SysDateMgr.END_TIME_FOREVER)
                 && compareDate.substring(0, 10).equals(SysDateMgr.END_TIME_FOREVER))
         {
             return 0;
         }
         
         result = date.compareTo(compareDate);
         
         return result;
    }
    
    
    public static int compareToYYYYMMDD(String date, String compareDate) throws Exception
    {
         int result;
         
         date = SysDateMgr.decodeTimestamp(date, SysDateMgr.PATTERN_STAND_YYYYMMDD);
         compareDate = SysDateMgr.decodeTimestamp(compareDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
         
         if(date.substring(0, 10).equals(SysDateMgr.END_TIME_FOREVER)
                 && compareDate.substring(0, 10).equals(SysDateMgr.END_TIME_FOREVER))
         {
             return 0;
         }
         
         result = date.compareTo(compareDate);
         
         return result;
    }
    
    /**
     * 获得2个时间的日差值
     * @return 非绝对值的日差值
     * @throws Exception
     */
    public static int daysBetween(String smdate,String bdate) throws ParseException    
    {    
    	Date mdate = new SimpleDateFormat("yyyy-MM-dd").parse(smdate); 
    	Date sbdate = new SimpleDateFormat("yyyy-MM-dd").parse(bdate); 
        Calendar cal = Calendar.getInstance();    
        cal.setTime(mdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(sbdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
        return Integer.parseInt(String.valueOf(between_days));           
    }    
    
    /**
     * 获得2个时间的月差值
     * @return 非绝对值的月差值
     * smdate  
     * @throws Exception
     */
    public static int monthsBetween(String startdate,String enddate) throws ParseException    
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //            String start = "2018-8-1 9:18:33";
        //            String end = "2018-6-30 9:18:33";
        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();
        bef.setTime(sdf.parse(startdate));
        aft.setTime(sdf.parse(enddate));
        int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
        int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;
        //            System.out.println(Math.abs(month + result));
        //System.out.println(month + result);
        return (month + result);
    }
	
	 /**
     * 获得2个时间的 差值  
     * @return 非绝对值的时差值
     * @throws Exception
     */
    public static long hoursBetween(String startDate,String endDate) throws ParseException    
    {    
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date sDate = df.parse(startDate); 
    	Date eDate = df.parse(endDate);
        Calendar cal = Calendar.getInstance();    
        cal.setTime(sDate);    
        long sTime = cal.getTimeInMillis();                 
        cal.setTime(eDate);    
        long eTime = cal.getTimeInMillis();         
        long between_days=(eTime-sTime)/(1000*3600*24);  
        return Long.parseLong(String.valueOf(between_days));           
    }
   public static String chgFormat(String strDate, String oldForm, String newForm) throws Exception{
		if (null == strDate)
        {
            throw new NullPointerException();
        }

        DateFormat oldDf = new SimpleDateFormat(oldForm);
        Date date = oldDf.parse(strDate);

		String newStr = "";
        DateFormat newDf = new SimpleDateFormat(newForm);
        newStr = newDf.format(date);        
		return newStr;
	}

	/**
	 * @description 根据身份证号返回n年后的年末时间
	 * @param @param psptId
	 * @param @return
	 * @return String
	 * @author tanzheng
	 * @date 2019年6月9日
	 * @param psptId
	 * @param intervalYear 
	 * @return
	 */
	public static String get25yearEndDate(String psptId, String intervalYear) {
		String birthYear = psptId.substring(6, 10);
		int year25End = Integer.parseInt(birthYear)+Integer.parseInt(intervalYear);
		String year25EndStr = year25End+"-12-31 23:59:59";
		return year25EndStr;
	}
	
	/**
     * 获得2个时间的差值   格式： **年**月**日
     * @return 非绝对值的时差值
     * @throws Exception
     */
    public static String daysBetween4netAge(String startDateStr,String endDateStr) throws ParseException    
    {  
    	//计算网龄
		 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		 Date startDate = df.parse(startDateStr); 
	     Date endDate = df.parse(endDateStr);
	     Calendar calS = Calendar.getInstance();    
	     calS.setTime(startDate);                 

         int startY = calS.get(Calendar.YEAR);  
         int startM = calS.get(Calendar.MONTH);  
         int startD = calS.get(Calendar.DATE);  
         int startDayOfMonth = calS.getActualMaximum(Calendar.DAY_OF_MONTH);  
          
         calS.setTime(endDate);  
         int endY = calS.get(Calendar.YEAR);  
         int endM = calS.get(Calendar.MONTH);  
         int endD = calS.get(Calendar.DATE)+1;  
         int endDayOfMonth = calS.getActualMaximum(Calendar.DAY_OF_MONTH);  
          

         int lday = endD-startD;  
         if (lday<0) {  
            endM = endM -1;  
            lday = startDayOfMonth+ lday;  
         }  
         //处理天数问题，如：2011-01-01 到 2013-12-31  2年11个月31天     实际上就是3年  
         if (lday == endDayOfMonth) {  
            endM = endM+1;  
            lday =0;  
         }  
         int mos = (endY - startY)*12 + (endM- startM) + 1;  
         int lyear = mos/12;  
         int lmonth = mos%12;  
         StringBuilder sBuilder = new StringBuilder();  
         if (lyear >=0) {  
            sBuilder.append(lyear+"年");  
         }  
         if (lmonth >= 0) {  
            sBuilder.append(lmonth+"月");  
         }  
         if (lday >=0 ) {  
            sBuilder.append(lday+"天");  
         }  
         return sBuilder.toString();
    }


    /**
     * 获得2个时间的分差值
     * REQ201912140002 关于全球通美兰机场免费停车的需求
     * @return
     * @throws Exception
     */
    public static long minsBetweenForBenefit(String startDate,String endDate) throws Exception{
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date sDate = df.parse(startDate);
        Date eDate = df.parse(endDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(sDate);
        long sTime = cal.getTimeInMillis();
        cal.setTime(eDate);
        long eTime = cal.getTimeInMillis();
        long betweenMins=(long) Math.ceil((double)(eTime - sTime) / (1000 * 60));
        return Long.parseLong(String.valueOf(betweenMins));
    }

    /**
     * 获得2个时间的日差值
     * 规则:小于24小时返回1, 24-48小时返回2,以此类推
     * REQ201912140002 关于全球通美兰机场免费停车的需求
     * @return
     * @throws Exception
     */
    public static long daysBetweenForBenefit(String startDate,String endDate,long freeTime) throws Exception{
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date sDate = df.parse(startDate);
        Date eDate = df.parse(endDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(sDate);
        long sTime = cal.getTimeInMillis();
        cal.setTime(eDate);
        long eTime = cal.getTimeInMillis();
        if((eTime-sTime)<=freeTime*60*1000){
            return 0L;
        }
        long between_days=(long) Math.ceil((double)(eTime - sTime) / (1000 * 3600 * 24));
        return Long.parseLong(String.valueOf(between_days));
    }

    /**
     * 将日期格式yyyyMMddHHmmss转成yyyy-MM-dd HH:mm:ss
     * REQ201912140002 关于全球通美兰机场免费停车的需求
     * @param date
     * @return
     * @throws Exception
     * @author 
     */
    public static String getDateForSTANDYYYYMMDDHHMMSS(String date) throws Exception{

        if (date != null && date.length() == 14)
        {
            StringBuilder dateSb = new StringBuilder(30);
            dateSb = dateSb.append(date.substring(0, 4)).append("-").append(date.substring(4, 6)).append("-").append(date.substring(6,8));
            dateSb = dateSb.append(" ").append(date.substring(8,10)).append(":").append(date.substring(10,12)).append(":").append(date.substring(12, 14));
            date = dateSb.toString();
        }
        return date;
    }
}
