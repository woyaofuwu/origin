package com.asiainfo.veris.crm.order.soa.person.common.util;  

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;


/**
 * 证件类型工具类，Z
 * @version 1.0, 2018-05-28
 */
public class PsptUtils {
	/**
	 * 检查证件类型入口，但暂时只添加了A护照、O港澳居民来往内地通行证、N台湾居民往来大陆通行证
	 * @param psptName 客户名称
	 * @param psptId	证件号码
	 * @param psptTypeCode	证件类型
	 * @return
	 * @throws Exception
	 */
   public IData checkPspt(String psptName,String psptId,String psptTypeCode) throws Exception{
	   IData data=new DataMap();
	   if(psptId.indexOf(" ")!=-1){
		   data.put("X_RESULTCODE", "2999");
		   data.put("X_RESULTINFO", "证件号码不能有空格!");
		   return data;
	   }
	   if(isRepeatCode(psptId)){
		   data.put("X_RESULTCODE", "2999");
		   data.put("X_RESULTINFO", "证件号码不能全为同一个数字!");
		   return data;
	   }
	   if(isSerialCode(psptId)){
		   
		   data.put("X_RESULTCODE", "2999");
		   data.put("X_RESULTINFO", "证件号码不能为连续数字!");
		   return data;
	   }
	   
	   
	   if(psptTypeCode.equals("A")){//护照类型校验
		   
		   if(psptName.trim().length()<3){
			   data.put("X_RESULTCODE", "2999");
			   data.put("X_RESULTINFO", "护照证件名称必需大于等于3位!");
			   return data;
           } 
		   if(StringUtils.isNumeric(psptName)){
			   data.put("X_RESULTCODE", "2999");
			   data.put("X_RESULTINFO", "护照证件名称不能全为阿拉伯数字!");
			   return data;
		   }
		   
		   
           if(psptId.trim().length()<6){
        	   data.put("X_RESULTCODE", "2999");
			   data.put("X_RESULTINFO", "护照证件号码必须大于等于6位!");
			   return data;
           } 
		   
	   }else if(psptTypeCode.equals("O")){//港澳居民来往内地通行证
		   if(psptId.length()!=9&&psptId.length()!=11){
			   data.put("X_RESULTCODE", "2999");
			   data.put("X_RESULTINFO", "港澳居民来往内地通行证证件号码必须为9位或11位!");
			   return data;
		   }
		   if(!(psptId.charAt(0)=='H'||psptId.charAt(0)=='M')||!StringUtils.isNumeric(psptId.substring(1))){
			   data.put("X_RESULTCODE", "2999");
			   data.put("X_RESULTINFO", "港澳居民来往内地通行证证件号码首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字!");
			   return data;
		   }
		   
		   
	   }else if(psptTypeCode.equals("N")){//台湾居民往来大陆通行证
		   if(psptId.length()==12 || psptId.length()==11){
			   if(!StringUtils.isNumeric(psptId.substring(0, 10))){
				   data.put("X_RESULTCODE", "2999");
	  			   data.put("X_RESULTINFO", "台湾居民往来大陆通行证证件号码为11位或12位时，前10位必须均为阿拉伯数字！");
	  			   return data; 
			   }
			   
   			}
		   if(psptId.length()==8||psptId.length()==7){
			   if(!StringUtils.isNumeric(psptId)){
            		data.put("X_RESULTCODE", "2999");
       			   	data.put("X_RESULTINFO", "台湾居民往来大陆通行证证件号码为7位或8位时，必须均为阿拉伯数字！");
       			   	return data;
   				}
   			}
		    String psptIdsub=null; 
		    String psptIdsub2=null; 
		    if(psptId.substring(0, 2).equals("TW")){
		    	psptIdsub="TW";
		    	psptIdsub2=psptId.substring(2);
		    }else if(psptId.substring(0, 4).equals("LXZH")){
		    	psptIdsub="LXZH";
		    	psptIdsub2=psptId.substring(4);
		    }
		    if(psptIdsub!=null){
		    	if(!isHaveUpperCase(psptIdsub2)||!isHaveNumeric(psptIdsub2)
		    			||psptIdsub2.indexOf("(")==-1||psptIdsub2.indexOf(")")==-1){//有大写
		    		data.put("X_RESULTCODE", "2999");
       			   	data.put("X_RESULTINFO", "台湾居民往来大陆通行证证件号码前2位为“TW”或 “LXZH”字符时，后面需是阿拉伯数字、英文大写字母与半角“()”的组合");
       			   	return data;
		    	}
		    }
	   }
	   else {
		   data.put("X_RESULTCODE", "2999");
		   data.put("X_RESULTINFO", "无法识别的证件类型");
		   //无该证件类型
		   return data;
	   }
	   data.put("X_RESULTCODE", "0000");
	   data.put("X_RESULTINFO", "result is ok！");
	   //无该证件类型
	   return data;
   }
   public boolean isHaveUpperCase(String code) throws Exception{
	   for(int i=0;i<code.length();i++){
		  char c=code.charAt(i);
		  if(Character.isUpperCase(c)){
			  return true;
		  }
	   }
	   return false;
   }
   public  boolean isHaveNumeric(String cs) throws Exception
   {
       if(cs == null || cs.length() == 0)
           return false;
       int sz = cs.length();
       for(int i = 0; i < sz; i++)
           if(Character.isDigit(cs.charAt(i)))
               return true;

       return false;
   }
   private boolean isRepeatCode(String code) throws Exception
   {
		for(int i=0; i< (code.length()-1); i++){
			if(code.charAt(i+1) != code.charAt(i)){
				return false;
			}
		}
		return true;
	}
   private boolean isSerialCode(String code){
		if(!this.isInDigit(code)){
			return false;
		}
		boolean flag = true;
		int step = 0;
		for(int i=0; i < (code.length()-1); i++){
			int n1 = code.charAt(i+1);
			int n2 = code.charAt(i);
			step = n1- n2;
			if(step != 1 && step !=-1){
				flag = false;
				break;
			}
		}
		step=0;
		return flag;
	}
   private boolean isInDigit(String strName) 
   {
   	char[] ch = strName.toCharArray();
       for (int i = 0; i < ch.length; i++) 
       {
           char c = ch[i];
           if (!isDigit(c)) 
           {
               return false;
           }
       }
       return true;
   }
   private boolean isDigit(char c) 
   {
   	if (!Character.isDigit(c)) 
   	{
   		return false;
   	}
   	 return true;
   }
   
}