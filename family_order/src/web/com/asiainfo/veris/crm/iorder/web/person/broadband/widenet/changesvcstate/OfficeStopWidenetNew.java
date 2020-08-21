package com.asiainfo.veris.crm.iorder.web.person.broadband.widenet.changesvcstate;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class OfficeStopWidenetNew extends PersonBasePage {
	public abstract void setInfo(IData info);

	public abstract void setWideInfo(IData wideInfo);

	public void loadChildInfo(IRequestCycle cycle) throws Exception {
		IData data = getData();
		IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
		IData wideInfo = dataset.first();
		if (wideInfo == null) {
			CSViewException.apperr(WidenetException.CRM_WIDENET_1, data.getString("USER_ID"));
		} else {
			wideInfo.put("WIDE_TYPE", wideInfo.getString("RSRV_STR2"));
			setWideInfo(wideInfo);
		}
	}

	public void onTradeSubmit(IRequestCycle requestCycle) throws Exception {
		IData data = getData();
		data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
		IDataset dataset = CSViewCall.call(this, "SS.ChangeWidenetSvcStateRegSVC.tradeReg", data);
		setAjax(dataset);
	}
}