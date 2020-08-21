
package com.asiainfo.veris.crm.order.web.person.returnactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ReturnActiveView extends PersonBasePage
{

    public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData info = CSViewCall.call(this, "SS.ReturnActiveSVC.getReturnActiveInfo", data).getData(0);

        setAjax(info);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.ReturnActiveRegSVC.tradeReg", pageData);
        this.setAjax(rtDataset);
    }

    public abstract void setInfo(IData info);
}
