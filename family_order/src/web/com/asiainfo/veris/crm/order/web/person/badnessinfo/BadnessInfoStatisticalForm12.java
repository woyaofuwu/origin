
package com.asiainfo.veris.crm.order.web.person.badnessinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class BadnessInfoStatisticalForm12 extends PersonQueryPage
{

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData cond = new DataMap();
        IData data = getData();

        String sysdate = SysDateMgr.getSysDate();
        cond.put("REPORT_START_TIME", sysdate);
        cond.put("REPORT_END_TIME", sysdate);
        cond.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        setEditInfo(cond);

        String month = SysDateMgr.getCurMonth();
        if (Integer.parseInt(month) - 1 <= 0)
        {
            cond.put("MONTH1", 12);
        }
        else
        {
            cond.put("MONTH1", Integer.parseInt(month) - 1);
        }

        cond.put("MONTH2", month);

        if (Integer.parseInt(month) + 1 >= 12)
        {
            cond.put("MONTH3", 12);
        }
        else
        {
            cond.put("MONTH3", Integer.parseInt(month) + 1);
        }

        setCondition(cond);

        IDataset result = CSViewCall.call(this, "SS.BadnessInfoSVC.getReportCode", data);
        setReports(result);
    }

    public void queryBadnessInfosForm(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset cond = CSViewCall.call(this, "SS.BadnessInfoSVC.getMonths", data);
        setCondition(cond.getData(0));

        IDataset result = CSViewCall.call(this, "SS.BadnessInfoSVC.queryBadnessInfosForm", data);
        if (IDataUtil.isEmpty(result))
        {
            setAjax("ALERT_CODE", "0");
        }
        setBadInfos(result);
    }

    public abstract void setBadInfo(IData info);

    public abstract void setBadInfos(IDataset infos);

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setReports(IDataset reports);
}
