
package com.asiainfo.veris.crm.order.web.person.returnactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ReturnActiveExchangeView extends PersonBasePage
{

    public void checkGGCard(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.ReturnActiveSVC.checkExchangeCard", pageData);
        this.setAjax(rtDataset);
    }

    public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put("RSRV_VALUE_CODE", "RAGGCARD");
        data.put("PROCESS_TAG", "0");
        IDataset userOtherList = CSViewCall.call(this, "CS.UserOtherInfoQrySVC.getOtherInfoByIdPTag", data);

        setGgCardInfos(userOtherList);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.ReturnActiveExchangeRegSVC.tradeReg", pageData);
        this.setAjax(rtDataset);
    }

    public abstract void setGgCardInfos(IDataset ggCardInfos);

    public abstract void setInfo(IData info);
}
