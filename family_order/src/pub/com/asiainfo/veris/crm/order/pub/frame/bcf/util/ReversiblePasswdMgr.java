package com.asiainfo.veris.crm.order.pub.frame.bcf.util;

public class ReversiblePasswdMgr {

	public static String Deciphering(String passwd) throws Exception
	{
		StringBuilder DecipheringPasswd = new StringBuilder(passwd.length());  
		  
		for(int i = 0, size = passwd.length(); i < size; i++)
		{
			int asc = StrUtil.getAsc(passwd.substring(i,i+1));
		  	DecipheringPasswd.append(StrUtil.backchar(asc-1));
		}
		  
	    return DecipheringPasswd.toString();
	}
}
