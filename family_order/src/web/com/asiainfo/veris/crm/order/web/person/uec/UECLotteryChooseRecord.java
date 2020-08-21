
package com.asiainfo.veris.crm.order.web.person.uec;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UECLotteryChooseRecord extends PersonBasePage
{

    public void queryUserLotteryInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataset dataset = CSViewCall.call(this, "SS.UECLotteryActiveDealSVC.queryUserLotteryInfos", data);
        setInfos(dataset);
    }

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
