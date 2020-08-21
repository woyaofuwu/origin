package com.asiainfo.veris.crm.order.web.person.sundryquery.cellphonevideo;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.person.sundryquery.ibossutil.CommonQuery;



public abstract class QueryRechargeRecord extends CommonQuery
{
	
	/**
	 * 用户充值记录查询
	 */
	@Override
	public String operateConditions() throws Exception {
		// TODO Auto-generated method stub
		
		IData pageData= getData("cond");
		StringBuilder operateConditions = new StringBuilder();
		operateConditions.append(pageData.getString("PARA_CODE1", "")); //号码
		operateConditions.append("|");
		operateConditions.append(pageData.getString("START_DATE")+ SysDateMgr.START_DATE_FOREVER); //开始时间	
		operateConditions.append("|");
		operateConditions.append(pageData.getString("END_DATE")+ SysDateMgr.END_DATE); //结束时间
		SysDateMgr.getEndDate(SysDateMgr.getSysDate());
		
		return operateConditions.toString();
	}
	@Override
	public String operateTypeId() throws Exception {
		// TODO Auto-generated method stub
		return "01010";
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
