
package com.asiainfo.veris.crm.order.web.person.saleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MobileLotteryActiveDeal extends PersonBasePage
{

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        // 测试下看看
        IData data = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.MobileLotteryActiveDealSVC.tradeReg", data);
        this.setAjax(rtDataset);
    }

    public void queryLotteryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData lotteryInfos = CSViewCall.callone(this, "SS.MobileLotteryActiveDealSVC.queryLotteryInfo", data);
        setAjax(lotteryInfos);
        setInfo(lotteryInfos);
    }

    public abstract void setCondition(IData custInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset goods);

}
