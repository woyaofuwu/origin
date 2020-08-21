
package com.asiainfo.veris.crm.order.web.person.badness.sundryquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @ClassName: BadnessFilingManage
 * @Description: 不良信息举报归档
 * @author longtian3
 * @version 1.0
 * @created Apr 4, 2014 10:35:34 AM
 */
public abstract class BadnessFilingManage extends PersonBasePage
{

    public void queryBadInfos(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond", true);
        data.put("STATE", "02");
        data.put("BADNESS_INFO_PROVINCE", "898");// 举报受理省
        data.put("REPORT_CUST_PROVINCE", "898");// 举报用户归属省

        IDataOutput result = CSViewCall.callPage(this, "SS.BadnessQuerySVC.queryBadHastenInfo", data, this.getPagination("badInfoNav"));
        if (IDataUtil.isEmpty(result.getData()))
        {
            this.setAjax("ALERT_INFO", "获取待归档不良信息无数据!");
        }

        setCount(result.getDataCount());
        setBadInfos(result.getData());
    }

    public abstract void setBadInfo(IData info);

    public abstract void setBadInfos(IDataset infos);

    public abstract void setCount(long count);

    public abstract void setDealInfo(IData dealInfo);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setTipInfo(String tipInfo);

    public void updateBadInfos(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        CSViewCall.call(this, "SS.BadnessManageSVC.archBadnessInfos", data);
    }
}
