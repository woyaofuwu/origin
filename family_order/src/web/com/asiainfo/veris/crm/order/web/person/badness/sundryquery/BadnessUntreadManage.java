
package com.asiainfo.veris.crm.order.web.person.badness.sundryquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @ClassName: BadnessUntreadManage
 * @Description: 不良信息举报退回
 * @author longtian3
 * @version 1.0
 * @created Apr 4, 2014 10:36:18 AM
 */
public abstract class BadnessUntreadManage extends PersonBasePage
{

    public void queryBadInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        data.put("STATE", "00");

        IDataOutput result = CSViewCall.callPage(this, "SS.BadnessQuerySVC.queryBaseBadnessInfo", data, this.getPagination("badInfoNav"));
        if (IDataUtil.isEmpty(result.getData()))
        {
            this.setAjax("ALERT_INFO", "获取待退回不良信息无数据!");
        }

        setCount(result.getDataCount());
        setBadInfos(result.getData());
    }

    public abstract void setBadInfo(IData badInfo);

    public abstract void setBadInfos(IDataset badInfos);

    public abstract void setCount(long count);

    public abstract void setDealInfo(IData phone);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setTipInfo(String tipInfo);

    public void untreadBadnessInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.putAll(getData("cond", true));
        CSViewCall.call(this, "SS.BadnessManageSVC.untreadBadnessInfos", data);
    }

}
