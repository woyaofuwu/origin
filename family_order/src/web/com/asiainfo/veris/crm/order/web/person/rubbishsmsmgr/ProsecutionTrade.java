
package com.asiainfo.veris.crm.order.web.person.rubbishsmsmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ProsecutionTrade extends PersonBasePage
{

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData();
        IDataset result = CSViewCall.call(this, "SS.ProsecutionTradeSVC.tradeReg", inparam);
        setAjax(result);
    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setDealInfo(IData dealInfo);
}
