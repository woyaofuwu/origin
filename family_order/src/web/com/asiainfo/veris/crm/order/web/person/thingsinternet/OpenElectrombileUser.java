package com.asiainfo.veris.crm.order.web.person.thingsinternet;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class OpenElectrombileUser extends PersonBasePage{

	public abstract void setInfo(IData info);
	
	public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData rtData = CSViewCall.call(this, "SS.OpenElectrombileUserSVC.loadInfo", data).getData(0);

        setInfo(rtData);
    }
	
	public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.OpenElectrombileUserRegSVC.tradeReg", pageData);
        this.setAjax(rtDataset);
    }
}
