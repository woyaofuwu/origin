package com.asiainfo.veris.crm.order.pub.util;


public final class SqlUtil
{
	/**
	 * 去除前面空白，后面空白替换为一个空格
	 * @param sql
	 * @return
	 * @Author:chenzg
	 * @Date:2016-12-14
	 */
	public static String trimSql(String sql)
	{
		String s = sql.replaceAll("^\\s*", "").replaceAll("\\s*$", " ");
		return s;
	}
}
