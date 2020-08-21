package com.asiainfo.veris.crm.order.web.person.sundryquery.ibossutil;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;

public class TimeUtil {
	public static String getTime(int status) throws Exception{
		switch (status) {
		case 1:
			return SysDateMgr.getSysDate()+SysDateMgr.getEndTime235959();
		case 0:
			return SysDateMgr.getSysDate()+SysDateMgr.getFirstTime00000();
		default:
			return null;
		}
	}
	public static String getNextMonth(int num)throws Exception{
		return SysDateMgr.getAddMonthsNowday(num, SysDateMgr.getSysDate());
	}
	
}
