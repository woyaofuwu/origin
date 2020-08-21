
package com.asiainfo.veris.crm.order.web.person.monitorinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryAllSuspect extends PersonQueryPage
{

    public void querySuspectInfos(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.MonitorInfoQuerySVC.querySuspectInfos", data, this.getPagination("queryNav"));

        setInfos(output.getData());
        setCount(output.getDataCount());
    }

    public abstract void setCount(long count);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
