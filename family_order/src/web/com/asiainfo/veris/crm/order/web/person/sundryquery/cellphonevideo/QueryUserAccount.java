package com.asiainfo.veris.crm.order.web.person.sundryquery.cellphonevideo;





import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.person.sundryquery.ibossutil.CommonQuery;

public abstract class QueryUserAccount extends CommonQuery
{
	
	/**
	 * 用户账户查询
	 */
	@Override
	public String operateConditions() throws Exception {
		// TODO Auto-generated method stub
		
		IData pageData= getData("cond");
		StringBuilder operateConditions = new StringBuilder();
		operateConditions.append(pageData.getString("PARA_CODE1", "")); //号码
		return operateConditions.toString();
	}
	@Override
	public String operateTypeId() throws Exception {
		// TODO Auto-generated method stub
		return "01009";
	}
	
	@Override
	public String kindId() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String serviceTypeId() throws Exception {
		// TODO Auto-generated method stub
		return "82";
	}
	@Override
	public String svctypeId() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	

	
}
