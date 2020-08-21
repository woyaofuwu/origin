package com.asiainfo.veris.crm.order.web.person.sundryquery.mobilemarket;


import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.person.sundryquery.ibossutil.CommonQuery;

public abstract class QueryGraphicsVerification extends CommonQuery
{
	@Override
	public String operateConditions() throws Exception{
		IData pageData= getData("cond");
		StringBuilder operateConditions = new StringBuilder();
		operateConditions.append("CCQryGraphicsVerificationReq");
		operateConditions.append("|");
		operateConditions.append("1.0.0");
		operateConditions.append("|");
		operateConditions.append(pageData.getString("PARA_CODE1", "")); //号码

		return operateConditions.toString();
	}
	
	@Override
	public String operateTypeId() throws Exception{
		// TODO Auto-generated method stub
		return "01007";
	}
	@Override
	public String serviceTypeId() throws Exception{
		// TODO Auto-generated method stub
		return "41";
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
