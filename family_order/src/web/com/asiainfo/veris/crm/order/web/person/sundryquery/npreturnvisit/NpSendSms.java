
package com.asiainfo.veris.crm.order.web.person.sundryquery.npreturnvisit;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NpSendSms extends PersonBasePage
{
    public void getInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.NpSendSmsSVC.getNpOutInfos", data, getPagination("infofonav"));
        setInfos(output.getData());
        setInfosCount(output.getDataCount());

    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.NpSendSmsSVC.upDateNpOutInfo", pageData);
        this.setAjax(rtDataset);
    }

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long size);
}
