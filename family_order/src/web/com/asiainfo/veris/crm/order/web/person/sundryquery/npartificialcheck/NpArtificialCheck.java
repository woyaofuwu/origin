
package com.asiainfo.veris.crm.order.web.person.sundryquery.npartificialcheck;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NpArtificialCheck extends PersonBasePage
{
    public void checkNotPass(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.NpArtificialCheckSVC.checkNotPass", data);
        this.setAjax(rtDataset);
    }

    public void checkPass(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.NpArtificialCheckSVC.checkPass", data);
        this.setAjax(rtDataset);
    }

    public void getInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.NpArtificialCheckSVC.queryTradeNpInfos", data, getPagination("infofonav"));
        setInfos(output.getData());
        setInfosCount(output.getDataCount());

    }

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long size);
}
