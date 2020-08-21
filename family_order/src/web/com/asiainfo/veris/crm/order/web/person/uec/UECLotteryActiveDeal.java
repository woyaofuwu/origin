
package com.asiainfo.veris.crm.order.web.person.uec;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UECLotteryActiveDeal extends PersonBasePage
{

    public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataset dataset = CSViewCall.call(this, "SS.UECLotteryActiveDealSVC.queryUserLotteryInfos", data);
        setAjax(dataset);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataset dataset = CSViewCall.call(this, "SS.UECLotteryActiveDealRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setInfo(IData info);
}
