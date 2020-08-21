package com.asiainfo.veris.crm.order.web.group.cargroup;

import org.apache.tapestry.IRequestCycle;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;



public abstract class GrpWlwDiscntRebateApply extends GroupBasePage
{


	/**
	 * 分页查询
	 * @param cycle
	 * @throws Exception
	 */
	public void queryDiscntApplyInfo(IRequestCycle cycle) throws Exception 
	{
		IData condition = getData("cond", true);
		
		IData param = new DataMap();
		param.put("EC_ID", condition.getString("GROUP_SERIAL_NUMBER",""));
		param.put("START_DATE", condition.getString("START_DATE",""));
		param.put("END_DATE", condition.getString("END_DATE",""));
		param.put("APPROVAL_RSLT", condition.getString("APPROVAL_RSLT",""));
		param.put("RSRV_STR1", condition.getString("RSRV_STR1",""));
		
		IDataOutput qryRecordsCount = CSViewCall.callPage(this, 
				"SS.GrpWlwDiscntRebateApplySVC.queryDiscntApplyInfo", 
				param,
				getPagination("NavBarPart"));
		
		IDataset qryRecords = qryRecordsCount.getData();
		long count = qryRecordsCount.getDataCount();

		setCondition(condition);
		setInfos(qryRecords);
		setTotalCount(count);
		setAjax(qryRecords);
	}
	
	/**
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void onSubmitAdd(IRequestCycle cycle) throws Exception 
	{
		IData data = getData();
		IData input = new DataMap();
		input.put("EC_ID", data.getString("EC_ID", ""));
		input.put("REPORT_NUM", data.getString("REPORT_NUM", ""));
		input.put("CARDS_NUM", data.getString("CARDS_NUM", ""));
		input.put("CONTRACT_AWARD_DATE", data.getString("CONTRACT_AWARD_DATE", ""));
		input.put("CONTRACT_EFFEC_DATE", data.getString("CONTRACT_EFFEC_DATE", ""));
		input.put("CONTRACT_EXPIRE_DATE", data.getString("CONTRACT_EXPIRE_DATE", ""));
		input.put("APPLICATION_ATTR", data.getString("APPLICATION_ATTR", ""));
		input.put("PROV_DOC", data.getString("PROV_DOC", ""));
		input.put("APPLICANT", data.getString("APPLICANT", ""));
		input.put("APPLICANT_PHONE", data.getString("APPLICANT_PHONE", ""));
		input.put("APPLY_REASON", data.getString("APPLY_REASON", ""));
		
		input.put("DIS_PRODUCTS_INFO", data.getString("DIS_PRODUCTS_INFO", ""));
		
		IData result = CSViewCall.callone(this, "SS.GrpWlwDiscntRebateApplySVC.addDiscntApplyInfo", input);
		setAjax(result);
	}

	

	public abstract void setTotalCount(long count);

	public abstract void setCondition(IData condition);

	public abstract void setInfo(IData info);

	public abstract void setInfos(IDataset infos);

}
