package com.asiainfo.veris.crm.order.pub.frame.bcf.util;


/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: SQLInjectCheck
 * @description: 解决SQL注入问题
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2015-6-21
 */
public class SQLInjectCheck {
	
	private static final String[] KEYSWORDS = {"insert", "select", "delete", "update", "truncate", "drop", "count"};
	
	/**
	 *入参SQL注入检查
	 * 
	 * @param paramName
	 */
	public static final void safeCheck(String paramName) {
		
		paramName = paramName.toLowerCase();
		for (String keyword : KEYSWORDS) {
			if (-1 != paramName.indexOf(keyword)) {
				throw new IllegalArgumentException("sql injection exception!");
			}
		}
		
	}
	
	/**
	 * 入参SQL注入检查(检查通过，将参数直接返回)
	 * 
	 * @param paramName
	 */
	public static final String safeCheckAndReturn(String paramName) {
		
		String pn = paramName.toLowerCase();
		for (String keyword : KEYSWORDS) {
			if (-1 != pn.indexOf(keyword)) {
				throw new IllegalArgumentException("sql injection exception!");
			}
		}
		
		return paramName;
		
	}

}
