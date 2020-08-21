
package com.asiainfo.veris.crm.order.pub.util;

import java.util.Calendar;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;

public final class AcctDateUtils
{

    /**
     * 按帐期偏移
     * 
     * @param date
     * @param month_offset
     * @return
     * @throws Exception
     */
    public static String addAcctDay(String date, int acctOffset, String acctDay, String firstDate, String bookAcctDay, String bookFirstDate) throws Exception
    {
        if (acctOffset == 0)
        {
            return date;
        }

        if (bookAcctDay == null || "".equals(bookAcctDay))
        {
            // bookAcctDay为空，则bookFirstDate也必定为空，表示用户没有预约的帐期变化
            bookAcctDay = acctDay;
            bookFirstDate = firstDate;
        }

        boolean isNext = false;

        String today = SysDateMgr.getSysDate();

        String nowAcctLastDay = getLastDayThisAcct(today, acctDay, firstDate);

        if (date.compareTo(nowAcctLastDay) > 0)
        {
            isNext = true;
        }

        if (isNext)
        {
            // 传过来的日期已经处于下帐期内
            if (acctOffset == 1)
            {
                String firstDateNextAcctDay = getFirstDayNextAcct(date, bookAcctDay, bookFirstDate);
                return firstDateNextAcctDay;
            }
            else
            {
                // 偏移值大于1
                String firstDateNextAcctDay = getFirstDayNextAcct(date, bookAcctDay, bookFirstDate);
                return SysDateMgr.getAddMonthsNowday(acctOffset - 1, firstDateNextAcctDay);
            }
        }
        else
        {
            // 传递过来的日期仍然在本帐期内
            if (acctOffset == 1)
            {
                String firstDateNextAcctDay = getFirstDayNextAcct(date, acctDay, firstDate);
                return firstDateNextAcctDay;
            }
            else
            {
                // 偏移值大于1
                String firstDateNextAcctDay = getFirstDayNextAcct(date, acctDay, firstDate);
                String nextNextAcctDay = getFirstDayNextAcct(firstDateNextAcctDay, bookAcctDay, bookFirstDate);
                return SysDateMgr.getAddMonthsNowday(acctOffset - 2, nextNextAcctDay);
            }
        }
    }

    /**
     * 按自然年偏移
     * 
     * @param date
     * @param yearOffset
     * @param acctDay
     *            当前日期在本帐期内的结帐日
     * @param firstDate
     *            当前日期在本帐期内的首次结帐日
     * @param bookAcctDay
     *            预约帐期变化后的结帐日
     * @param bookFirstDate
     *            预约帐期变化后的首次结帐日
     * @return
     * @throws Exception
     */
    public static String addYearsNature(String date, int yearOffset, String acctDay, String firstDate,

    String bookAcctDay, String bookFirstDate) throws Exception
    {
        if (yearOffset == 0)
        {
            return date;
        }

        if (bookAcctDay == null || "".equals(bookAcctDay))
        {
            // bookAcctDay为空，则bookFirstDate也必定为空，表示用户没有预约的帐期变化
            bookAcctDay = acctDay;
            bookFirstDate = firstDate;
        }

        boolean isNext = false;

        String today = SysDateMgr.getSysDate();

        String nowAcctLastDay = getLastDayThisAcct(today, acctDay, firstDate);

        if (date.compareTo(nowAcctLastDay) > 0)
        {
            isNext = true;
        }

        int monthOffset = yearOffset * 12;

        String returnDate = "";

        for (int i = 1; i <= monthOffset; i++)
        {
            if (isNext)
            {
                // 传过来的日期已经处于下帐期内
                if (i == 1)
                {
                    returnDate = getFirstDayNextAcct(date, bookAcctDay, bookFirstDate);
                }
                else
                {
                    // 偏移值大于1
                    returnDate = getFirstDayNextAcct(returnDate, bookAcctDay, bookFirstDate);
                }
            }
            else
            {
                // 传递过来的日期仍然在本帐期内
                if (i == 1)
                {
                    returnDate = getFirstDayNextAcct(date, acctDay, firstDate);
                }
                else
                {
                    // 偏移值大于1
                    returnDate = getFirstDayNextAcct(returnDate, bookAcctDay, bookFirstDate);
                }
            }

            if (returnDate.substring(0, 4).compareTo(date.substring(0, 4)) > 0)
            {
                break;
            }
        }
        return returnDate;
    }

    /**
     * 结束时间偏移计算
     * 
     * @param startDate
     * @param endEnableTag
     * @param endAbsoluteDate
     * @param endOffset
     * @param endUnit
     * @param acctDayMap
     *            结帐日Map结构，需要包含四个值，NOW_ACCT_DAY--当前日期在本帐期内的结帐日,NOW_FIRST_ACCT_DAY--当前日期在本帐期内的首次结帐日,BOOK_ACCT_DAY--预约帐期变化后的结帐日
     *            ,BOOK_FIRST_ACCT_DAY--预约帐期变化后的首次结帐日
     * @return
     * @throws Exception
     */
    public static String endDate(IData acctDayMap, String startDate, String endEnableTag, String endAbsoluteDate, String endOffset, String endUnit) throws Exception
    {
        String acctDay = acctDayMap.getString("ACCT_DAY");

        String firstDate = acctDayMap.getString("FIRST_DATE");

        String bookAcctDay = acctDayMap.getString("NEXT_ACCT_DAY");

        String bookFirstDate = acctDayMap.getString("NEXT_FIRST_DATE");

        return AcctDateUtils.endDate(startDate, endEnableTag, endAbsoluteDate, endOffset, endUnit, acctDay, firstDate, bookAcctDay, bookFirstDate);
    }

    /**
     * 结束时间偏移计算
     * 
     * @param startDate
     *            开始时间
     * @param endEnableTag
     * @param endAbsoluteDate
     * @param endOffset
     * @param endUnit
     * @param acctDay
     *            当前日期在本帐期内的结帐日
     * @param firstDate
     *            当前日期在本帐期内的首次结帐日
     * @param bookAcctDay
     *            预约帐期变化后的结帐日
     * @param bookFirstDate
     *            预约帐期变化后的首次结帐日
     * @return
     * @throws Exception
     */
    public static String endDate(String startDate, String endEnableTag, String endAbsoluteDate, String endOffset, String endUnit,

    String acctDay, String firstDate, String bookAcctDay, String bookFirstDate) throws Exception
    {
        if ("0".equals(endEnableTag))
        { // 0:绝对时间
            return endAbsoluteDate;
        }
        else if ("1".equals(endEnableTag))
        { // 1:相对时间
            return endDateOffset(startDate, endOffset, endUnit, acctDay, firstDate, bookAcctDay, bookFirstDate);
        }
        else if ("2".equals(endEnableTag))
        { // 2:手工录入
            return SysDateMgr.END_TIME_FOREVER;
        }

        return SysDateMgr.END_TIME_FOREVER;
    }

    /**
     * 结束时间偏移计算
     * 
     * @param date
     * @param offset
     * @param unit
     * @param acctDay
     *            当前日期在本帐期内的结帐日
     * @param firstDate
     *            当前日期在本帐期内的首次结帐日
     * @param bookAcctDay
     *            预约帐期变化后的结帐日
     * @param bookFirstDate
     *            预约帐期变化后的首次结帐日
     * @return
     * @throws Exception
     */
    public static String endDateOffset(String date, String offset, String unit,

    String acctDay, String firstDate, String bookAcctDay, String bookFirstDate) throws Exception
    {
        String endDate = date;

        if (offset == null || offset.equals("") || unit == null || unit.equals(""))
        {
            return SysDateMgr.END_TIME_FOREVER;
        }
        if (offset.equals("0"))
        {
            // 解决如果偏移量为0，返回的时间会是date减去一个偏移量后的时间
            return date;
        }

        if ("0".equals(unit))
        { // 按天偏移
            endDate = SysDateMgr.addDays(date, Integer.parseInt(offset));
        }
        else if ("1".equals(unit))
        { // 按自然天偏移
            endDate = SysDateMgr.addDays(date, Integer.parseInt(offset));
        }
        else if ("2".equals(unit))
        { // 原按月偏移，现按帐期偏移，只要开始时间有偏移都是帐期起始时间
            endDate = addAcctDay(date, Integer.parseInt(offset), acctDay, firstDate, bookAcctDay, bookFirstDate);
            endDate = SysDateMgr.addDays(endDate, -1);
        }
        else if ("3".equals(unit))
        { // 原按自然月偏移，现按帐期偏移，只要开始时间有偏移都是帐期起始时间
            endDate = addAcctDay(date, Integer.parseInt(offset), acctDay, firstDate, bookAcctDay, bookFirstDate);
            endDate = SysDateMgr.addDays(endDate, -1);
        }
        else if ("4".equals(unit))
        { // 按年偏移
            endDate = addAcctDay(date, Integer.parseInt(offset) * 12, acctDay, firstDate, bookAcctDay, bookFirstDate);
            endDate = SysDateMgr.addDays(endDate, -1);
        }
        else if ("5".equals(unit))
        { // 按自然年偏移
            endDate = addYearsNature(date, Integer.parseInt(offset), acctDay, firstDate, bookAcctDay, bookFirstDate);
            endDate = SysDateMgr.addDays(endDate, -1);
        }

        return endDate;
    }

    /**
     * 根据配置获取终止时间(针对预约)
     * 
     * @param cancelTag
     * @param acctDay
     * @param firstDate
     * @param startDate
     * @return YYY-MM-DD格式的时间
     * @throws Exception
     */
    public static String getCancelDate2(String oneday, String cancelTag, String acctDay, String firstDate, String startDate) throws Exception
    {
        if (StringUtils.isBlank(oneday))
        {
            oneday = SysDateMgr.getSysDate();
        }
        if ("0".equals(cancelTag))
        {
            return SysDateMgr.addDays(oneday, -1);
        }
        else if ("1".equals(cancelTag))
        {
            return SysDateMgr.addDays(oneday, -1);
        }
        else if ("2".equals(cancelTag))
        {
            return oneday;
        }
        else if ("3".equals(cancelTag))
        {
            return getLastDayThisAcct(oneday, acctDay, firstDate);
        }
        else
        {
            return null;
        }
    }

    /**
     * 根据配置获取终止时间
     * 
     * @param cancelTag
     * @param acctDay
     * @param firstDate
     * @param startDate
     * @return YYY-MM-DD格式的时间
     * @throws Exception
     */
    public static String getCancelDateByDate(String nowdate, String cancelTag, String acctDay, String firstDate, String startDate) throws Exception
    {

        if (StringUtils.isBlank(nowdate))
            nowdate = SysDateMgr.getSysDate();
        if ("0".equals(cancelTag))
        {
            return nowdate;
        }
        else if ("1".equals(cancelTag))
        {
            return SysDateMgr.addDays(nowdate, -1);
        }
        else if ("2".equals(cancelTag))
        {
            return nowdate;
        }
        else if ("3".equals(cancelTag))
        {
            return getLastDayThisAcct(nowdate, acctDay, firstDate);
        }
        else
        {
            return null;
        }
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
        IData data = setDayNew(oneDate, acctDay);
        boolean flag = data.getBoolean("flag");
        String firstDayThisAcct = data.getString("date");

        if (firstDayThisAcct.compareTo(oneDate) > 0 && flag == true)
        {

            firstDayThisAcct = SysDateMgr.getAddMonthsNowday(-1, firstDayThisAcct);
        }
        else if (firstDayThisAcct.compareTo(oneDate) > 0 && flag == false)
        {
            firstDayThisAcct = setDay(SysDateMgr.getAddMonthsNowday(-1, firstDayThisAcct), acctDay);
        }
        if (firstDayThisAcct.compareTo(firstDate) < 0)
        {
            firstDayThisAcct = startDate;
        }
        return firstDayThisAcct;
    }

    /**
     * 根据某个日期以及结账日（某个日期归属的结账日）获得某个日期归属账期的下账期的第一天
     * 
     * @param oneDate
     *            某个日期
     * @param acctDay
     *            某个日期所在帐期结帐日
     * @param firstDate
     *            某个日期所在帐期首次结帐日
     * @return
     * @throws Exception
     *             2011-5-24
     */

    public static String getFirstDayNextAcct(String oneDate, String acctDay, String firstDate) throws Exception
    {
        IData data = setDayNew(oneDate, acctDay);
        boolean flag = data.getBoolean("flag");
        String firstDayNextAcct = data.getString("date");
        if (oneDate.compareTo(firstDayNextAcct) >= 0 && flag == true)
        {
            firstDayNextAcct = SysDateMgr.getAddMonthsNowday(1, firstDayNextAcct);
        }
        else if (oneDate.compareTo(firstDayNextAcct) >= 0 && flag == false)
        {

            firstDayNextAcct = setDay(SysDateMgr.getAddMonthsNowday(1, firstDayNextAcct), acctDay);
        }

        if (firstDayNextAcct.compareTo(firstDate) < 0)
        {
            firstDayNextAcct = firstDate;
        }

        return firstDayNextAcct;
    }

    /**
     * 根据某个日期以及结账日、首次出账日、预约结账日、预约首次出账日获取某个日期归属账期的下账期的第一天
     * 
     * @param oneDate
     *            某个日期
     * @param acctDay
     *            结账日
     * @param firstDate
     *            首次出账日
     * @param nextAcctDay
     *            预约结账日
     * @param nextFirstDate
     *            预约首次出账日
     * @return 某个日期归属账期的下账期的第一天
     * @throws Exception
     *             2011-5-31
     */
    public static String getFirstDayNextAcct(String oneDate, String acctDay, String firstDate, String nextAcctDay, String nextFirstDate) throws Exception
    {
        // 先获取该日期归属账期的最后一天，然后再加上一天

        String firstDayNextAcct = getLastDayThisAcct(oneDate, acctDay, firstDate, nextAcctDay, nextFirstDate);

        return SysDateMgr.addDays(firstDayNextAcct, 1);
    }

    /**
     * 根据某个日期以及结账日（某个日期归属的结账日）获得某个日期归属账期的最后一天
     * 
     * @param oneDate
     *            某个日期
     * @param acctDay
     *            某个日期所在帐期结帐日
     * @param firstDate
     *            某个日期所在帐期首次结帐日
     * @return
     * @throws Exception
     *             2011-5-24
     */
    public static String getLastDayThisAcct(String oneDate, String acctDay, String firstDate) throws Exception
    {
        String fisrtDayNextAcct = getFirstDayNextAcct(oneDate, acctDay, firstDate);

        return SysDateMgr.addDays(fisrtDayNextAcct, -1);
    }

    /**
     * 根据某个日期以及结账日、首次出账日、预约结账日、预约首次出账日获取某个日期归属账期的最后一天
     * 
     * @param oneDate
     *            某个日期
     * @param acctDay
     *            结账日
     * @param firstDate
     *            首次出账日
     * @param nextAcctDay
     *            预约结账日
     * @param nextFirstDate
     *            预约首次出账日
     * @return 某个日期归属账期的最后一天
     * @throws Exception
     *             2011-5-31
     */
    public static String getLastDayThisAcct(String oneDate, String acctDay, String firstDate, String nextAcctDay, String nextFirstDate) throws Exception
    {
        // 获取当前时间所属账期的最后一天

        String lastDayThisAcct = getLastDayThisAcct(SysDateMgr.getSysDate(), acctDay, firstDate);

        if (StringUtils.isBlank(nextAcctDay)) // 如果没有预约账期,则设置预约结账日等于当前结账日
        {
            nextAcctDay = acctDay;

            nextFirstDate = firstDate;
        }

        if (oneDate.compareTo(lastDayThisAcct) > 0)
        {
            // 传入的时间大于本账期的最后一天，则取下账期的结账日以及下账期的首次出账时间来计算传入的时间归属的账期的最后一天

            lastDayThisAcct = getLastDayThisAcct(oneDate, nextAcctDay, nextFirstDate);
        }

        return lastDayThisAcct;
    }

    /**
     * 修改某个日期为另一天(不变动月和年)
     * 
     * @param nowDate
     * @param acctDay
     * @return
     * @throws Exception
     *             2011-5-24
     */
    private static String setDay(String nowDate, String acctDay) throws Exception
    {
        Calendar c = Calendar.getInstance();

        c.setTime(SysDateMgr.string2Date(nowDate, "yyyy-MM-dd"));

        // 由于出账日为1、5、10、15、20、25中的一个，而每个月的最大天数至少为28，所以下面的方法没有问题
        int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (lastDay < Integer.parseInt(acctDay))
        {
            acctDay = lastDay + "";
        }
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(acctDay));

        return SysDateMgr.date2String(c.getTime(), "yyyy-MM-dd");
    }

    /**
     * 修改某个日期为另一天(不变动月和年)
     * 
     * @param nowDate
     * @param acctDay
     * @return
     * @throws Exception
     *             2011-5-24
     */
    private static IData setDayNew(String nowDate, String acctDay) throws Exception
    {
        Calendar c = Calendar.getInstance();

        c.setTime(SysDateMgr.string2Date(nowDate, "yyyy-MM-dd"));

        IData returnData = new DataMap();
        returnData.put("flag", true);
        // 由于出账日为1、5、10、15、20、25中的一个，而每个月的最大天数至少为28，所以下面的方法没有问题
        int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (lastDay < Integer.parseInt(acctDay))
        {
            acctDay = lastDay + "";
            returnData.put("flag", false);
        }

        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(acctDay));
        returnData.put("date", SysDateMgr.date2String(c.getTime(), "yyyy-MM-dd"));

        return returnData;
    }

    /**
     * 开始时间按配置计算
     * 
     * @param startDate
     *            YYYY-MM-DD格式
     * @param enableTag
     *            生效标记
     * @param startAbsoluteDate
     *            生效绝对时间
     * @param startOffset
     *            生效偏移值
     * @param startUnit
     *            偏移单位
     * @param acctDay
     *            当前日期在本帐期内的结帐日
     * @param firstDate
     *            当前日期在本帐期内的首次结帐日
     * @param bookAcctDay
     *            预约帐期变化后的结帐日
     * @param bookFirstDate
     *            预约帐期变化后的首次结帐日
     * @return
     * @throws Exception
     */
    public static String startDateByDate(String startDate, String enableTag, String startAbsoluteDate, String startOffset, String startUnit,

    String acctDay, String firstDate, String bookAcctDay, String bookFirstDate) throws Exception
    {
        if (startDate == null || "".equals(startDate))
        {
            startDate = SysDateMgr.getSysDate();
        }
        if ("0".equals(enableTag))
        {
            // 立即生效
            startDate = startDateOffset(startDate, startOffset, startUnit, acctDay, firstDate, bookAcctDay, bookFirstDate);
        }
        else if ("1".equals(enableTag))
        {
            // 下帐期生效
            startDate = startDateOffset(getFirstDayNextAcct(startDate, acctDay, firstDate, bookAcctDay, bookFirstDate), startOffset, startUnit, acctDay, firstDate, bookAcctDay, bookFirstDate);
        }
        else if ("2".equals(enableTag))
        {
            // 次日生效
            startDate = startDateOffset(SysDateMgr.addDays(startDate, 1), startOffset, startUnit, acctDay, firstDate, bookAcctDay, bookFirstDate);
        }
        else if ("3".equals(enableTag))
        {
            // 可选立即还是下帐期，先返回下帐期
            startDate = startDateOffset(getFirstDayNextAcct(startDate, acctDay, firstDate, bookAcctDay, bookFirstDate), startOffset, startUnit, acctDay, firstDate, bookAcctDay, bookFirstDate);
        }
        else if ("4".equals(enableTag))
        {
            // 绝对时间生效
            startDate = startAbsoluteDate;
        }

        return startDate;
    }

    /**
     * 开始时间偏移计算
     * 
     * @param date
     * @param offset
     * @param unit
     * @param acctDay
     *            当前日期在本帐期内的结帐日
     * @param firstDate
     *            当前日期在本帐期内的首次结帐日
     * @param bookAcctDay
     *            预约帐期变化后的结帐日
     * @param bookFirstDate
     *            预约帐期变化后的首次结帐日
     * @return
     * @throws Exception
     */
    public static String startDateOffset(String date, String offset, String unit,

    String acctDay, String firstDate, String bookAcctDay, String bookFirstDate) throws Exception
    {
        String startDate = date;

        if (offset == null || offset.equals("") || unit == null || unit.equals(""))
        {
            return date;
        }

        if ("0".equals(unit))
        {
            // 按天偏移
            startDate = SysDateMgr.addDays(date, Integer.parseInt(offset));
        }
        else if ("1".equals(unit))
        {
            // 按自然天偏移
            startDate = SysDateMgr.addDays(date, Integer.parseInt(offset));
        }
        else if ("2".equals(unit))
        {
            // 原按月偏移，现按帐期偏移，只要开始时间有偏移都是帐期起始时间
            startDate = addAcctDay(date, Integer.parseInt(offset), acctDay, firstDate, bookAcctDay, bookFirstDate);
        }
        else if ("3".equals(unit))
        {
            // 原按自然月偏移，现按帐期偏移，只要开始时间有偏移都是帐期起始时间
            startDate = addAcctDay(date, Integer.parseInt(offset), acctDay, firstDate, bookAcctDay, bookFirstDate);
        }
        else if ("4".equals(unit))
        {
            // 按年偏移
            startDate = addAcctDay(date, Integer.parseInt(offset) * 12, acctDay, firstDate, bookAcctDay, bookFirstDate);
        }
        else if ("5".equals(unit))
        {
            // 按自然年偏移(取年底)
            startDate = addYearsNature(date, Integer.parseInt(offset), acctDay, firstDate, bookAcctDay, bookFirstDate);
        }

        return startDate;
    }
}
