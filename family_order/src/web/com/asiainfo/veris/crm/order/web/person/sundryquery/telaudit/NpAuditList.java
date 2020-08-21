
package com.asiainfo.veris.crm.order.web.person.sundryquery.telaudit;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NpAuditList extends PersonBasePage
{
    public void getInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataset rtDataset = CSViewCall.call(this, "SS.NpAuditSVC.getNpAuditInfo", data);

        setInfo(rtDataset.getData(0));
    }

    public void getInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.NpAuditSVC.getNpAuditInfos", data, getPagination("infofonav"));
        setInfos(output.getData());
        setInfosCount(output.getDataCount());

    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long size);

}
