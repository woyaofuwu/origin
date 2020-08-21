package com.asiainfo.veris.crm.order.web.group.cargroup;

import org.apache.tapestry.IRequestCycle;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;



public abstract class CarGroupRateInfo extends GroupBasePage {


	public void onSubmitAdd(IRequestCycle cycle) throws Exception {
		IData data = getData();
		IData input = new DataMap();
		input.put("EC_ID", data.getString("EC_ID", ""));
		input.put("DISCNT_RATE", data.getString("DISCNT_RATE", ""));
		input.put("CARD_BIND", data.getString("CARD_BIND", "0"));
		input.put("PROV_DOC", data.getString("PROV_DOC", ""));
		input.put("RSRV_STR1", data.getString("APPLY_TYPE",""));
		input.put("APPLICANT", data.getString("APPLICANT", ""));
		input.put("APPLICANT_PHONE", data.getString("APPLICANT_PHONE", ""));
		input.put("APPLY_REASON", data.getString("APPLY_REASON", ""));
		IData result = CSViewCall.callone(this,
				"SS.CarGroupRateInfoSVC.addCarGroupRateInfo", input);
		setAjax(result);

	}

	public void queryByECID(IRequestCycle cycle) throws Exception {

		IData condition = getData("COND", true);
		IDataOutput qryRecordsCount = CSViewCall.callPage(this,
				"SS.CarGroupRateInfoSVC.queryByECID", condition,
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
