
package com.asiainfo.veris.crm.order.web.person.badness.sundryquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BadnessQueryManage extends PersonBasePage
{

    public void queryBadnessInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond");
        Pagination page = this.getPagination("badInfoNav");
        IDataOutput result = CSViewCall.callPage(this, "SS.BadnessQuerySVC.queryOtherProvBadnessInfo", data, page);
        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "获取不良信息无数据");
        }
        setBadInfoCount(result.getDataCount());
        setBadInfos(dataset);
    }

    public void queryOtherBadnessInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset result = CSViewCall.call(this, "SS.BadnessQuerySVC.queryOtherBadnessInfo", data);
        setOtherInfos(result);
        this.setAjax(result.getData(0));
    }

    public abstract void setBadInfo(IData data);

    public abstract void setBadInfoCount(long count);

    public abstract void setBadInfos(IDataset dataset);

    public abstract void setEditInfo(IData data);

    public abstract void setOtherInfo(IData data);

    public abstract void setOtherInfos(IDataset dataset);
}
