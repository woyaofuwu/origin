package com.asiainfo.veris.crm.order.web.person.onePsptMultiUser;

import org.apache.tapestry.IRequestCycle;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class OnePsptMultiUser extends PersonBasePage
{
	/**
	 * @param cycle
	 * @throws Exception
	 */
	public void onTradeSubmit(IRequestCycle cycle) throws Exception {
		IData  pageData = getData();
		IDataset checkResult = CSViewCall.call(this,"SS.OnePsptMultiUserSVC.checkInputData", pageData);
		pageData.put("userList", checkResult);
		IDataset returnDataset = CSViewCall.call(this, "SS.OnePsptMultiUserRegSVC.tradeReg", pageData);
        this.setAjax(returnDataset);
	}
}