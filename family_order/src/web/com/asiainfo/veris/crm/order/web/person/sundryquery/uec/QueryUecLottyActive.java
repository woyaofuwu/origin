
package com.asiainfo.veris.crm.order.web.person.sundryquery.uec;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryUecLottyActive extends PersonBasePage
{

    public void init(IRequestCycle cycle) throws Exception
    {
        String today = SysDateMgr.getSysDate();
        String yestoday = SysDateMgr.getYesterdayDate();
        String firstDayOfThisMonth = SysDateMgr.getFirstDayOfThisMonth4WEB();

        IData cond = getData();
        if (firstDayOfThisMonth.equals(today))
        {
            cond.put("cond_BEGIN_DATE", today);
            cond.put("cond_END_DATE", today);
        }
        else
        {
            cond.put("cond_BEGIN_DATE", yestoday);
            cond.put("cond_END_DATE", today);
        }
        setCondition(cond);
    }

    public void querylottys(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);

        IDataOutput orderInfos = CSViewCall.callPage(this, "SS.QueryUecLottySVC.querylottys", data, getPagination("navt"));

        if (orderInfos != null)
        {
            setCount(orderInfos.getDataCount());
        }
        setInfos(orderInfos.getData());
    }

    public void queryPrizes(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);

        String an = data.getString("ACTIVITY_NUMBER");

        data.put("TYPE_ID", "UECLOTTERY_PRIZE_TYPE_CODE" + an);
        data.put("PDATA_ID", an);
        IDataOutput output = CSViewCall.callPage(this, "CS.StaticInfoQrySVC.getStaticListByTypeIdPDataId", data, getPagination("navt"));
        if (output != null)
        {
            setPrizes(output.getData());
        }

    }

    public abstract void setActivities(IDataset activities);

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset viceInfos);

    public abstract void setPrizes(IDataset prizes);

}
