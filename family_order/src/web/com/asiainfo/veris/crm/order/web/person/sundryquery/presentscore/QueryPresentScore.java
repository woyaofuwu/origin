
package com.asiainfo.veris.crm.order.web.person.sundryquery.presentscore;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryPresentScore extends PersonBasePage
{

    /**
     * 页面初始化方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        // 设置办理时间
        String sysDate = SysDateMgr.getSysTime();
        data.put("cond_START_DATE", sysDate);
        data.put("cond_END_DATE", sysDate);
        setCondition(data);
        queryArea(cycle);
    }

    /**
     * 查询市县
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryArea(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.QueryPresentScoreSVC.queryArea", data);
        setAreas(dataset);
    }

    /**
     * 查询转赠积分
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryPresentScore(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);
        IPage page = cycle.getPage();
        param.put("OBJECT_ID", page.getPageName());
        param.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());
        IDataOutput outPut = CSViewCall.callPage(this, "SS.QueryPresentScoreSVC.queryPresentScore", param, getPagination("pagin"));

        String alertInfo = "";
        if (IDataUtil.isEmpty(outPut.getData()))
        {
            alertInfo = "获取转赠积分无数据!";
        }

        setInfos(outPut.getData());
        setPaginCount(outPut.getDataCount());
        // IData data = new DataMap();
        // data.put("PRINT_FLAG", "true");
        // setCondition(data);
        setCondition(getData("cond", true));

        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示

    }

    public abstract void setAreas(IDataset areas);

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPaginCount(long paginCount);

}
