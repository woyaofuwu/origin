package com.asiainfo.veris.crm.order.web.group.cargroup;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;


public abstract class CarGroupRateApprove extends GroupBasePage {


	public void onSubmitApprove(IRequestCycle cycle) throws Exception {
		IData data = getData();
		IData input = new DataMap();
		input.put("OPR_SEQS", data.getString("OPR_SEQS",""));
		input.put("RSRV_STR2", data.getString("RSRV_STR2",""));//省侧审批结果
		IData result = CSViewCall.callone(this,
				"SS.CarGroupRateApproveSVC.addCarGroupRateApprove", input);
		setAjax(result);
	}

	public void queryByECID(IRequestCycle cycle) throws Exception {

		IData condition = getData("COND", true);
		IDataOutput qryRecordsCount = CSViewCall.callPage(this,
				"SS.CarGroupRateApproveSVC.queryByECID", condition,
				getPagination("NavBarPart")); // NavBarPart为HTML页的分页组件
		IDataset qryRecords = qryRecordsCount.getData();
		long count = qryRecordsCount.getDataCount();

		setCondition(condition);
		setInfos(qryRecords);
		setTotalCount(count);
		setAjax(qryRecords);
	}

	public abstract void setTotalCount(long count);

	public abstract void setCondition(IData condition);

	public abstract void setInfo(IData info);

	public abstract void setInfos(IDataset infos);

}
