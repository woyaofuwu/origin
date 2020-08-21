package com.asiainfo.veris.crm.order.web.person.sundryquery.wirelessmusic; 

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.person.sundryquery.ibossutil.CommonQuery;






public abstract class QueryMessageContextRecord extends CommonQuery
{
	
	/**
	 * 短信上下行记录查询
	 */
	@Override
	public String operateConditions() throws Exception {
		// TODO Auto-generated method stub
		
		IData pageData= getData("cond", true);
		StringBuilder operateConditions = new StringBuilder();
		operateConditions.append(pageData.getString("PARA_CODE1", "")); //号码
		operateConditions.append("|");
		operateConditions.append(pageData.getString("START_DATE")); //日限额
		operateConditions.append("|");
		operateConditions.append(pageData.getString("END_DATE")); //月限额
		
		return operateConditions.toString();
	}
	@Override
	public String operateTypeId() throws Exception {
		// TODO Auto-generated method stub
		return "01008";
	}
	
	@Override
	public String kindId() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String serviceTypeId() throws Exception {
		// TODO Auto-generated method stub
		return "72";
	}
	@Override
	public String svctypeId() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	

	
}
