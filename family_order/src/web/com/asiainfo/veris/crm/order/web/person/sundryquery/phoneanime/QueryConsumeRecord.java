package com.asiainfo.veris.crm.order.web.person.sundryquery.phoneanime; 





import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.person.sundryquery.ibossutil.CommonQuery;

public abstract class QueryConsumeRecord extends CommonQuery
{
	
	/**
	 * 消费记录查询
	 */
	@Override
	public String operateConditions() throws Exception {
		// TODO Auto-generated method stub
		
		IData pageData= getData("cond");
		StringBuilder operateConditions = new StringBuilder();
		operateConditions.append(pageData.getString("PARA_CODE1", "")); //号码
		operateConditions.append("|");
		operateConditions.append(pageData.getString("PARA_CODE2", "")); //开始时间
		operateConditions.append("|");
		operateConditions.append(pageData.getString("PARA_CODE3", "")); //结束时间
		operateConditions.append("|");
		operateConditions.append(pageData.getString("PARA_CODE4", "")); //消费情况
		operateConditions.append("|");
		operateConditions.append(pageData.getString("PARA_CODE5", "")); //支付方式
		return operateConditions.toString();
	}
	@Override
	public String operateTypeId() throws Exception {
		// TODO Auto-generated method stub
		return "01011";
	}
	
	@Override
	public String kindId() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String serviceTypeId() throws Exception {
		// TODO Auto-generated method stub
		return "95";
	}
	@Override
	public String svctypeId() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	

	
}
