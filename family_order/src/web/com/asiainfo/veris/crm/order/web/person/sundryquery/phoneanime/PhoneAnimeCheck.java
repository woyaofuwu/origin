package com.asiainfo.veris.crm.order.web.person.sundryquery.phoneanime; 



import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.person.sundryquery.ibossutil.CommonQuery;

public abstract class PhoneAnimeCheck extends CommonQuery {

	static transient final Logger logger = Logger.getLogger(PhoneAnimeCheck.class);
	
	
	//核查
	@Override
	public String operateConditions() throws Exception{
		IData pageData= getData("cond");
		StringBuilder operateConditions = new StringBuilder();
		operateConditions.append(pageData.getString("PARA_CODE1", "")); //号码/电子邮箱
		operateConditions.append("|");
		operateConditions.append(pageData.getString("PARA_CODE2", "")); //产品ID
		operateConditions.append("|");
		operateConditions.append(pageData.getString("PARA_CODE3", "")); //消费时间
		operateConditions.append("|");
		operateConditions.append(pageData.getString("PARA_CODE4", "")); //BOSSID

		
		return operateConditions.toString();
	}
	
	@Override
	public String operateTypeId() throws Exception{
		// TODO Auto-generated method stub
		return "01014";
	}
	@Override
	public String serviceTypeId() throws Exception{
		// TODO Auto-generated method stub
		return "95";
	}
	
	@Override
	public String svctypeId() throws Exception{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String kindId() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
