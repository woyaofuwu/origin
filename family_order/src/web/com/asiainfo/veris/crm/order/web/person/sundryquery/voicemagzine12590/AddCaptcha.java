package com.asiainfo.veris.crm.order.web.person.sundryquery.voicemagzine12590; 



import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.person.sundryquery.ibossutil.CommonSubmit;

public abstract class AddCaptcha extends CommonSubmit {

	static transient final Logger logger = Logger.getLogger(AddCaptcha.class);
	
	
	//添加灰名单
	@Override
	public String operateConditions() throws Exception{
		IData pageData= getData("cond");
		StringBuilder operateConditions = new StringBuilder();
		operateConditions.append(pageData.getString("SUBMIT_MODE1", "")); //号码
		operateConditions.append("|");
		operateConditions.append(pageData.getString("SUBMIT_MODE2", "")); //省份
		operateConditions.append("|");
		operateConditions.append(pageData.getString("SUBMIT_MODE3", "")); //开始时间
		operateConditions.append("|");
		operateConditions.append(pageData.getString("SUBMIT_MODE4", "")); //结束时间
		operateConditions.append("|");
		operateConditions.append(pageData.getString("SUBMIT_MODE5", "")); //操作人员

		
		return operateConditions.toString();
	}
	
	@Override
	public String operateTypeId() throws Exception{
		// TODO Auto-generated method stub
		return "02003";
	}
	@Override
	public String serviceTypeId() throws Exception{
		// TODO Auto-generated method stub
		return "121";
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
