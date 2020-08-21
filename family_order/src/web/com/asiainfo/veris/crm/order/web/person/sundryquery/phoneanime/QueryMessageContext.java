package com.asiainfo.veris.crm.order.web.person.sundryquery.phoneanime; 

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.person.sundryquery.ibossutil.CommonQuery;






public abstract class QueryMessageContext extends CommonQuery
{
	
	//短信上下行查询
	@Override
	public String operateConditions() throws Exception{
		IData pageData= getData("cond");
		StringBuilder operateConditions = new StringBuilder();
		operateConditions.append(pageData.getString("PARA_CODE1", "")); //号码
		operateConditions.append("|");
		operateConditions.append(pageData.getString("START_DATE", "")); 
		operateConditions.append("|");
		operateConditions.append(pageData.getString("END_DATE", "")); 
		operateConditions.append("|");
		operateConditions.append(pageData.getString("PARA_CODE2", "")); 
		operateConditions.append("|");
		operateConditions.append(pageData.getString("PARA_CODE3", "")); 

		
		return operateConditions.toString();
	}
	
	@Override
	public String operateTypeId() throws Exception{
		// TODO Auto-generated method stub
		return "01013";
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
