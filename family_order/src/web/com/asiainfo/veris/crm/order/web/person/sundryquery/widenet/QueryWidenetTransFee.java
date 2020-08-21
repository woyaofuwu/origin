
package com.asiainfo.veris.crm.order.web.person.sundryquery.widenet;

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

public abstract class QueryWidenetTransFee extends PersonQueryPage
{

    /**
     * 初始化页面
     * 
     * @author chenzme
     * @param cycle
     * @throws Exception
     */
    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        String startDate = SysDateMgr.getSysDate();// 格式为YYYY-MM-DD
        data.put("START_DATE", startDate);
        data.put("END_DATE", startDate);
        setCondition(data);
    }

    /**
     * 查询校园宽带费用转移信息
     * 
     * @author chenzm
     * @param cycle
     * @throws Exception
     */
    public void queryTransFee(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String alertInfo = "";
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataOutput infos = CSViewCall.callPage(this, "SS.QueryWidenetTransFeeSVC.queryTransFee", data, getPagination("olcomnav"));

        if (IDataUtil.isEmpty(infos.getData()))
        {
            alertInfo = "获取校园宽带费用转移无数据!";
        }
        setInfos(infos.getData());
        setInfosCount(infos.getDataCount());
        setCondition(data);

        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long count);

}
