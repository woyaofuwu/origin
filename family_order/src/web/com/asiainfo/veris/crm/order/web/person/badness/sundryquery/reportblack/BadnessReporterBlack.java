
package com.asiainfo.veris.crm.order.web.person.badness.sundryquery.reportblack;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class BadnessReporterBlack extends PersonQueryPage
{

    public void addBlack(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        CSViewCall.call(this, "SS.BlackWhiteManageSVC.addBlack", data);
    }

    public void delBlack(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        CSViewCall.call(this, "SS.BlackWhiteManageSVC.delBlack", data);
    }

    public void queryReporterBlack(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        IDataset result = CSViewCall.call(this, "SS.BadnessQuerySVC.queryReporterBlack", data);
        if (IDataUtil.isEmpty(result))
        {
            this.setAjax("ALERT_INFO", "按条件获取黑名单无数据!");
        }
        else
        {
            setInfos(result);
        }
    }

    public abstract void setCount(long count);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public void updBlack(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        CSViewCall.call(this, "SS.BlackWhiteManageSVC.updBlack", data);
    }
}
