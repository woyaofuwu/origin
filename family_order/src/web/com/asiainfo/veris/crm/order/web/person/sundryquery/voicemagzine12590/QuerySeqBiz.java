package com.asiainfo.veris.crm.order.web.person.sundryquery.voicemagzine12590; 





import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.person.sundryquery.ibossutil.CommonQuery;

public abstract class QuerySeqBiz extends CommonQuery
{
	
	/**
	 * 按次业务查询
	 */
	@Override
	public String operateConditions() throws Exception {
		// TODO Auto-generated method stub
		
		IData pageData= getData("cond");
		StringBuilder operateConditions = new StringBuilder();
		operateConditions.append(pageData.getString("PARA_CODE1", "")); //号码
		operateConditions.append("|");
		operateConditions.append(pageData.getString("START_DATE", "")); //开始时间
		operateConditions.append("|");
		operateConditions.append(pageData.getString("END_DATE", "")); //结束时间
		return operateConditions.toString();
	}
	@Override
	public String operateTypeId() throws Exception {
		// TODO Auto-generated method stub
		return "01006";
	}
	
	@Override
	public String kindId() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String serviceTypeId() throws Exception {
		// TODO Auto-generated method stub
		return "121";
	}
	@Override
	public String svctypeId() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	

	
}
