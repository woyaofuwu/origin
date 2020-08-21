
package com.asiainfo.veris.crm.order.web.person.badness.sundryquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @ClassName: BadnessHastenManage
 * @Description: 不良信息举报催办
 * @author longtian3
 * @version 1.0
 * @created Apr 4, 2014 10:35:50 AM
 */
public abstract class BadnessHastenManage extends PersonBasePage
{
    public void hastenBadnessInfos(IRequestCycle cycle) throws Exception
    {
        IData data1 = getData();
        IData data = getData("cond");
        data.putAll(data1);
        CSViewCall.call(this, "SS.BadnessManageSVC.hastenBadnessInfos", data);
    }

    public void queryBadInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);

        data.put("REPORT_CUST_PROVINCE", "898");
        data.put("BADNESS_INFO_PROVINCE", "898");
        data.put("STATE", "01");
        IDataOutput result = CSViewCall.callPage(this, "SS.BadnessQuerySVC.queryBadHastenInfo", data, this.getPagination("badInfoNav"));
        if (IDataUtil.isEmpty(result.getData()))
        {
            this.setAjax("ALERT_INFO", "获取待催办不良信息无数据!");
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
}
