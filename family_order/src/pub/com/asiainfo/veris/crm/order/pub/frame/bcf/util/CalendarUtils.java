
package com.asiainfo.veris.crm.order.pub.frame.bcf.util;

/**
 * 日历工具类
 * 
 * @author xiaoxl2
 */
public final class CalendarUtils
{
    /**
     * Comment for <code>iLunarCalendarTable</code> 每个阴历年数据用5个16进制数表示，共20bit 前4位，代表这年润月的大小月，为1则润大月，为0则润小月
     * 中间12位，每位代表一个月，为1则为大月，为0则为小月 最后4位，代表这一年的润月月份，为0则不润。 首4位要与末4位搭配使用
     */
    private static final int[] lunarCalendarTable =
    { 0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,// 1900-1909
            0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,// 1910-1919
            0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,// 1920-1929
            0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,// 1930-1939
            0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,// 1940-1949
            0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0,// 1950-1959
            0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,// 1960-1969
            0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,// 1970-1979
            0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,// 1980-1989
            0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,// 1990-1999
            0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,// 2000-2009
            0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,// 2010-2019
            0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,// 2020-2029
            0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,// 2030-2039
            0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0 // 2040-2049
    };

    /**
     * 获取阴历年中的第多少天
     * 
     * @param year
     *            阴历年
     * @param month
     *            阴历月
     * @param day
     *            阴历日
     * @return day 在 year 中的第几天
     */
    public static int getDayOfLunarYear(int year, int month, int day)
    {

        int dayOfYear = 0;
        int leapMonth = getLunarLeapMonth(year);
        for (int i = 1; i < month; i++)
        {
            dayOfYear += getLunarMonthDays(year, i);
            // 如果是闰月则只累加到其前一个正常月
            if (leapMonth == month - 12 && i == leapMonth)
            {
                break;
            }
            // 闰月后的月份加上闰月的天数
            if (i == leapMonth)
                dayOfYear += getLunarMonthDays(year, leapMonth + 12);
        }
        // 农历月份天数每年不一样，为防止天数溢出作此处理
        if (day > getLunarMonthDays(year, month))
        {
            day = getLunarMonthDays(year, month);
        }
        dayOfYear += day;
        return dayOfYear;
    }

    /**
     * 获取阳历年中的第多少天
     * 
     * @param year
     *            阳历年
     * @param month
     *            阳历月
     * @param day
     *            阳历日
     * @return day 在 year 中的第几天
     */
    public static int getDayOfSolarYear(int year, int month, int day)
    {

        int dayOfYear = 0;
        for (int i = 1; i < month; i++)
        {
            dayOfYear += getSolarMonthDays(year, i);
        }
        // 为防止闰月天数溢出作此处理
        if (day > getSolarMonthDays(year, month))
        {
            day = getSolarMonthDays(year, month);
        }
        dayOfYear += day;
        return dayOfYear;
    }

    /**
     * 根据阴历年获取闰月
     * 
     * @param year
     *            阴历年
     * @return 该阴历年的闰月，无闰月则返回0
     */
    public static int getLunarLeapMonth(int year)
    {

        int month = lunarCalendarTable[year - 1900] & 0xF;
        return month;
    }

    /**
     * 获取阴历月份天数
     * 
     * @param year
     *            阴历年
     * @param month
     *            阴历月
     * @return 阴历月每月天数
     */
    public static int getLunarMonthDays(int year, int month)
    {

        int leapMonth = getLunarLeapMonth(year);
        if ((month > 12) && (month - 12 != leapMonth) || (month < 0))
        {
            return -1;
        }
        if (leapMonth > 0 && month - 12 == leapMonth)
        {
            if (((lunarCalendarTable[year - 1900] & 0xF0000) >> 16) == 0)
                return 29;
            else
                return 30;
        }
        if (((lunarCalendarTable[year - 1900] & (0x8000 >> (month - 1))) >> 4) == 0)
            return 29;
        else
            return 30;
    }

    /**
     * 阴历年天数
     * 
     * @param year
     *            阴历年
     * @return 该阴历年的天数
     */
    public static int getLunarYearDays(int year)
    {

        int yearDays = 0;
        int leapMonth = getLunarLeapMonth(year);
        // 累加12个月的天数
        for (int i = 1; i <= 12; i++)
        {
            yearDays += getLunarMonthDays(year, i);
        }
        // 有闰月则加上闰月天数
        if (leapMonth > 0)
            yearDays += getLunarMonthDays(year, leapMonth + 12);
        return yearDays;
    }

    /**
     * 获取阳历每月天数
     * 
     * @param year
     *            阳历年
     * @param month
     *            阳历月
     * @return 阳历月天数
     */
    public static int getSolarMonthDays(int year, int month)
    {

        if ((month == 1) || (month == 3) || (month == 5) || (month == 7) || (month == 8) || (month == 10) || (month == 12))// 阳历大月31天
            return 31;
        else if ((month == 4) || (month == 6) || (month == 9)// 阳历小月30天
                || (month == 11))
            return 30;
        else if (month == 2)
        {
            if (isSolarLeapYear(year))// 阳历闰年2月29天
                return 29;
            else
                // 阳历非闰年2月28天
                return 28;
        }
        else
            return 0;
    }

    /**
     * 阳历年天数
     * 
     * @param year
     *            阳历年
     * @return 该阳历年的天数
     */
    public static int getSolarYearDays(int year)
    {

        return isSolarLeapYear(year) ? 366 : 365;
    }

    /**
     * 判断阳历闰年
     * 
     * @param iYear
     *            阳历年
     * @return true 闰年 false 非闰年
     */
    public static boolean isSolarLeapYear(int year)
    {

        return ((year % 4 == 0) && (year % 100 != 0) || year % 400 == 0);
    }

    /**
     * 阴历转阳历
     * 
     * @param year
     *            阴历年
     * @param month
     *            阴历月
     * @param day
     *            阴历日 闰月+12
     * @return 对应的阳历 yyyymmdd
     */
    public static String lundarToSolar(int year, int month, int day)
    {

        int solarYear, solarMonth, solarDay;
        int dayOfYear = getDayOfLunarYear(year, month, day);
        int daysSince1900 = dayOfYear;
        for (int i = year - 1; i >= 1900; i--)
        {
            daysSince1900 += getLunarYearDays(i);
        }
        daysSince1900 += 30;
        for (solarYear = 1900; daysSince1900 >= 0; solarYear++)
        {
            dayOfYear = daysSince1900;
            daysSince1900 -= getSolarYearDays(solarYear);
        }
        solarYear--;
        solarDay = dayOfYear;
        for (solarMonth = 1; dayOfYear > 0; solarMonth++)
        {
            solarDay = dayOfYear;
            dayOfYear -= getSolarMonthDays(solarYear, solarMonth);
        }
        solarMonth--;
        return "" + solarYear + (solarMonth > 9 ? solarMonth + "" : "0" + solarMonth) + (solarDay > 9 ? solarDay + "" : "0" + solarDay);
    }

    /**
     * 阴历转阳历
     * 
     * @param year
     *            阴历年
     * @param month
     *            阴历月
     * @param day
     *            阴历日
     * @param isLeapMonth
     *            是否闰月
     * @return 对应的阳历 yyyymmdd
     */
    public static String lundarToSolar(int year, int month, int day, boolean isLeapMonth)
    {

        if (isLeapMonth && getLunarLeapMonth(year) > 0)
        {
            month += 12;
        }
        return lundarToSolar(year, month, day);
    }

    /**
     * 阳历转换为阴历
     * 
     * @param year
     *            阳历年
     * @param month
     *            阳历月
     * @param day
     *            阳历日
     * @return 对应的阴历yyyymmdd
     */
    public static String solarToLundar(int year, int month, int day)
    {

        int lunarDay, lunarMonth, lunarYear;
        int dayOfYear = getDayOfSolarYear(year, month, day);
        int daysSince1900 = dayOfYear;
        for (int i = year - 1; i >= 1900; i--)
        {
            daysSince1900 += getSolarYearDays(i);
        }
        daysSince1900 -= 30;
        for (lunarYear = 1900; daysSince1900 >= 0; lunarYear++)
        {
            dayOfYear = daysSince1900;
            daysSince1900 -= getLunarYearDays(lunarYear);
        }
        lunarYear--;
        if (lunarYear < 1900)
        {
            return "0";
        }
        lunarDay = dayOfYear;
        int leapMonth = getLunarLeapMonth(year);
        for (lunarMonth = 1; dayOfYear > 0; lunarMonth++)
        {
            lunarDay = dayOfYear;
            dayOfYear -= getLunarMonthDays(lunarYear, lunarMonth);
            if (leapMonth > 0 && lunarMonth == leapMonth && dayOfYear > 0)
            {
                lunarDay = dayOfYear;
                dayOfYear -= getLunarMonthDays(lunarYear, lunarMonth + 12);
                if (dayOfYear <= 0)
                {
                    lunarMonth += 12;
                }
            }
        }
        lunarMonth--;
        return "" + lunarYear + (lunarMonth > 9 ? "" + lunarMonth : "0" + lunarMonth) + (lunarDay > 9 ? "" + lunarDay : "0" + lunarDay);
    }
}
