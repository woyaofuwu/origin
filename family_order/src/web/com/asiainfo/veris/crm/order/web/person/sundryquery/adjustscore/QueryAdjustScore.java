
package com.asiainfo.veris.crm.order.web.person.sundryquery.adjustscore;

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
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryAdjustScore extends PersonQueryPage
{

    /**
     * 查询调整积分
     * 
     * @param cycle
     * @throws Exception
     */
    public void getAdjustScore(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IPage page = cycle.getPage();
        param.put("OBJECT_ID", page.getPageName());
        param.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());
        IDataOutput outPut = CSViewCall.callPage(this, "SS.QueryAdjustScoreSVC.getAdjustScore", param, getPagination("pagin"));

        String alertInfo = "";
        if (IDataUtil.isEmpty(outPut.getData()))
        {
            alertInfo = "获取调整积分无数据!";
        }

        setInfos(outPut.getData());
        setPaginCount(outPut.getDataCount());
        // IData data = new DataMap();
        // data.put("PRINT_FLAG", "true");
        // setCondition(data);
        setCondition(param);
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示

    }

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
        data.put("START_DATE", sysDate);
        data.put("END_DATE", sysDate);
        data.put("PRINT_FLAG", "false");
        setCondition(data);
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPaginCount(long paginCount);

}
