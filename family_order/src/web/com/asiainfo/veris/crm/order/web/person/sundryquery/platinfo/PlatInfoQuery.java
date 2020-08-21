
package com.asiainfo.veris.crm.order.web.person.sundryquery.platinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class PlatInfoQuery extends PersonQueryPage
{

    public void queryOperCode(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IDataset result = CSViewCall.call(this, "SS.QueryInfoSVC.queryOperCode", data);
        setOperCodes(result);
    }

    public void queryPlatInfo(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IDataOutput result = CSViewCall.callPage(this, "SS.SelfBizSVC.qrySelfBizInfo", data, getPagination("qryInfoNav"));
        setInfos(result.getData());
        setCount(result.getDataCount());
    }

    public abstract void setCount(long count);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setOperCodes(IDataset operCodes);

    public abstract void setTitle(String title);

}
