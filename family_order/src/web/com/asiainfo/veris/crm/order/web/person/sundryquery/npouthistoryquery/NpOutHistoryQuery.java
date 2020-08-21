
package com.asiainfo.veris.crm.order.web.person.sundryquery.npouthistoryquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NpOutHistoryQuery extends PersonBasePage
{

    public void getInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        //IDataOutput output = CSViewCall.callPage(this, "SS.NpOutHistoryQuerySVC.getOutNpInfos", data, getPagination("infofonav"));
        IDataset rtDataset = CSViewCall.call(this, "SS.NpOutHistoryQuerySVC.getOutNpInfos", data);
        setInfos(rtDataset);
       // setInfosCount(output.getDataCount());
    }

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long size);
}
