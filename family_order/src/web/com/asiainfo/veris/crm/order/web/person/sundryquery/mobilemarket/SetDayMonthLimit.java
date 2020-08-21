package com.asiainfo.veris.crm.order.web.person.sundryquery.mobilemarket;



import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.person.sundryquery.ibossutil.CommonSubmit;

public abstract class SetDayMonthLimit extends CommonSubmit {

	static transient final Logger logger = Logger.getLogger(SetDayMonthLimit.class);

	@Override
	public String operateConditions() throws Exception{
		IData pageData= getData("cond",true);
		StringBuilder operateConditions = new StringBuilder();
		operateConditions.append("CCSetMSDNLimitReq");
		operateConditions.append("|");
		operateConditions.append("1.0.0");
		operateConditions.append("|");
		operateConditions.append(pageData.getString("SUBMIT_MODE1", "")); //号码
		operateConditions.append("|");
		operateConditions.append(pageData.getString("SUBMIT_MODE2", "")); //日限额
		operateConditions.append("|");
		operateConditions.append(pageData.getString("SUBMIT_MODE3", "")); //月限额

		
		return operateConditions.toString();
	}
	
	@Override
	public String operateTypeId() throws Exception{
		// TODO Auto-generated method stub
		return "02002";
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
	
	@Override
	public IDataset setResult(IDataset infos) throws Exception {
		IDataset result=new DatasetList();
		IData data=new DataMap();
		IData info=infos.first();
		data.put("RESULT_CODE", info.get("RSRV_STR2"));
		data.put("RESULT_INFO", info.get("RSRV_STR3"));
		result.add(data);
		return result;
	}

}
