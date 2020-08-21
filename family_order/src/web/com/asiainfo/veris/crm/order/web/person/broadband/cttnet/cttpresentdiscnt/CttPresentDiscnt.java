
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttpresentdiscnt;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CttPresentDiscnt extends PersonBasePage
{

    /**
     * 卡类费用登记查询初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initPresentInfo(IRequestCycle cycle) throws Exception
    {

        IData cond = getData("cond");
        if (IDataUtil.isEmpty(cond))
        {
            String nowDate = SysDateMgr.getSysDate();
            cond.put("START_DATE", nowDate);
            cond.put("END_DATE", nowDate);
        }

        setCond(cond);
    }

    /**
     * 查询后设置页面信息
     */
    public void queryPresentInfos(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IDataOutput result = null;
        String alertInfo = null;
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        result = CSViewCall.callPage(this, "SS.CttPresentDiscntSVC.queryPresentInfos", data, getPagination("pageinfo"));

        if (IDataUtil.isEmpty(result.getData()))
        {
            alertInfo = "未获取到相关资料！";
            this.setAjax("ALERT_INFO", alertInfo);
        }

        setInfos(result.getData());
        setCount(result.getDataCount());

    }

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset dataset);
}
