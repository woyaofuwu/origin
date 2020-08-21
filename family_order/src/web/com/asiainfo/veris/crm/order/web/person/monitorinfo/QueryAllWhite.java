
package com.asiainfo.veris.crm.order.web.person.monitorinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryAllWhite extends PersonQueryPage
{
    public void delWhiteUser(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        CSViewCall.call(this, "SS.ManageWhiteSVC.delWhiteUser", data);

        queryWhiteInfos(cycle);
    }

    public void queryWhiteInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.MonitorInfoQuerySVC.queryWhiteInfos", data, getPagination("queryNav"));
        setCount(output.getDataCount());
        setInfos(output.getData());
    }

    public abstract void setCount(long count);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
