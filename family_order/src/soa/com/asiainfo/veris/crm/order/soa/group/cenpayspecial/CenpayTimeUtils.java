package com.asiainfo.veris.crm.order.soa.group.cenpayspecial;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class CenpayTimeUtils {

	/**
	 * 当前时间日期yyyy-MM-dd
	 * @param dateStr
	 * @return
	 * @throws Exception
	 */
    public static String getThisDay(String dateStr) 
    	throws Exception
    {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	Date date = format.parse(dateStr);
    	return format.format(date);
    }
    
    /**
     * 当前时间日期yyyy-MM-dd的当天开始时间
     * @param dateStr
     * @return
     * @throws Exception
     */
    public static String getTodayStartOfThisDay(String dateStr) 
    	throws Exception
    {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	Date date = format.parse(dateStr);
    	return format.format(date) + " 00:00:00" ;
    }
    
    /**
     * 当前时间日期yyyy-MM-dd的当天结束时间
     * @param dateStr
     * @return
     * @throws Exception
     */
    public static String getTodayEndOfThisDay(String dateStr) throws Exception {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	Date date = format.parse(dateStr);
    	return format.format(date) + " 23:59:59" ;
    }
    
    //当前时间的每个小时的开始时间和结束时间
    /**
     * 获取当前时间的每个小时的开始时间
     * @param dateStr
     * @return
     * @throws Exception
     */
    public static String getStartHourOfEachHour(String dateStr) throws Exception {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
    	Date date = format.parse(dateStr);
    	return format.format(date) + ":00:00" ;
    }
    
    /**
     * 获取当前时间的每个小时的结束时间
     * @param dateStr
     * @return
     * @throws Exception
     */
    public static String getEndHourOfEachHour(String dateStr) throws Exception {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
    	Date date = format.parse(dateStr);
    	return format.format(date) + ":59:59" ;
    }
    
}
