package com.asiainfo.veris.crm.order.web.person.sundryquery.queryreadrecord; 





import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.person.sundryquery.ibossutil.CommonQuery;

public abstract class QueryMessageContext extends CommonQuery
{
	
	/**
	 * 短信上下行查询
	 */
	@Override
	public String operateConditions() throws Exception {
		// TODO Auto-generated method stub
		IData pageData= getData("cond",true);
		StringBuilder operateConditions = new StringBuilder();
		operateConditions.append(pageData.getString("PARA_CODE1", "")); //号码
		operateConditions.append("|");
		operateConditions.append(pageData.getString("START_DATE")); //开始时间	
		operateConditions.append("|");
		operateConditions.append(pageData.getString("END_DATE")); //结束时间
		operateConditions.append("|");
		operateConditions.append(pageData.getString("PARA_CODE2", "")); //上下行
		SysDateMgr.getEndDate(SysDateMgr.getSysDate());
		
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
		return "42";
	}
	@Override
	public String svctypeId() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	

	
}
