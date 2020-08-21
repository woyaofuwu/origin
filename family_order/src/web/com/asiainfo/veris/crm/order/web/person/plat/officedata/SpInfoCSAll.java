
package com.asiainfo.veris.crm.order.web.person.plat.officedata;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class SpInfoCSAll extends CSBasePage
{

    public void queryList(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.SpInfoCSSVC.querySpInfoCS", data, getPagination("queryNav"));
        setCount(dataCount.getDataCount());
        setInfos(dataCount.getData());
    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);
}
