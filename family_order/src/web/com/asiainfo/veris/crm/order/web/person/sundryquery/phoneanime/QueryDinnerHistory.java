package com.asiainfo.veris.crm.order.web.person.sundryquery.phoneanime; 

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.person.sundryquery.ibossutil.CommonQuery;






public abstract class QueryDinnerHistory extends CommonQuery
{
	@Override
	public String operateConditions() throws Exception{
		IData pageData= getData("cond",true);
		StringBuilder operateConditions = new StringBuilder();
		operateConditions.append(pageData.getString("PARA_CODE1", "")); //号码
		operateConditions.append("|");
		operateConditions.append(pageData.getString("PARA_CODE2", "")); //开始时间
		operateConditions.append("|");
		operateConditions.append(pageData.getString("PARA_CODE3", "")); //结束时间

		
		return operateConditions.toString();
	}
	
	
	@Override
	public String operateTypeId() throws Exception{
		// TODO Auto-generated method stub
		return "01010";
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
