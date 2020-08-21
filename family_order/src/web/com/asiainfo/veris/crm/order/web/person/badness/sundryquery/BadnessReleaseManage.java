
package com.asiainfo.veris.crm.order.web.person.badness.sundryquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class BadnessReleaseManage extends PersonQueryPage
{
    public void dealBadnessReleaseInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        CSViewCall.call(this, "SS.BadnessManageSVC.dealBadnessReleaseInfo", data);
    }

    public void queryBadnessReleaseInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        IDataOutput output = CSViewCall.callPage(this, "SS.BadnessQuerySVC.queryBadnessReleaseInfo", data, this.getPagination("QueryInfoNav"));
        setInfos(output.getData());
        setCount(output.getDataCount());
    }

    public abstract void setCount(long count);

    public abstract void setEditInfo(IData info);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
