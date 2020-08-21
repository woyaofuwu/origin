package com.asiainfo.veris.crm.order.web.group.cargroup;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;



public abstract class GrpWlwDiscntRebateApprove extends GroupBasePage {

	/**
	 * 分页查询
	 * @param cycle
	 * @throws Exception
	 */
	public void queryRebateApproveInfo(IRequestCycle cycle) throws Exception 
	{
		IData condition = getData("cond", true);
		
		IData param = new DataMap();
		param.put("EC_ID", condition.getString("GROUP_SERIAL_NUMBER",""));
		param.put("START_DATE", condition.getString("START_DATE",""));
		param.put("END_DATE", condition.getString("END_DATE",""));
		param.put("APPROVAL_RSLT", condition.getString("APPROVAL_RSLT",""));
		param.put("RSRV_STR1", condition.getString("RSRV_STR1",""));
		
		IDataOutput qryRecordsCount = CSViewCall.callPage(this, 
				"SS.GrpWlwDiscntRebateApproveSVC.queryRebateApproveInfo", 
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
	public void onSubmitApprove(IRequestCycle cycle) throws Exception 
	{
		IData data = getData();
		IData input = new DataMap();
		input.put("OPR_SEQS", data.getString("OPR_SEQS",""));
		input.put("RSLT_STATE", data.getString("RSLT_STATE",""));//省侧审批结果
		IData result = CSViewCall.callone(this,
				"SS.GrpWlwDiscntRebateApproveSVC.addCarGroupRateApprove", input);
		setAjax(result);
	}

	
	public abstract void setTotalCount(long count);

	public abstract void setCondition(IData condition);

	public abstract void setInfo(IData info);

	public abstract void setInfos(IDataset infos);

}
