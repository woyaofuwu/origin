
package com.asiainfo.veris.crm.order.web.person.sundryquery.owewriteoff;

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

/**
 * 功能：移动电话欠费销号清单查询 作者：GongGuang
 */
public abstract class QueryOweWriteOff extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 初始化
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        // 设置起止时间
        String endDate = SysDateMgr.getSysDate();// 格式为YYYY-MM-DD
        String firstData = SysDateMgr.getAddMonthsNowday(-1, endDate); // 得到上个月的今天
        IData data = getData("cond", true);
        data.put("cond_START_DATE", firstData);
        data.put("cond_END_DATE", endDate);
        this.setCondition(data);
        queryArea(cycle);

    }

    /**
     * 查询业务区
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryArea(IRequestCycle cycle) throws Exception
    {

        IData input = new DataMap();
        input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        IDataset areas = CSViewCall.call(this, "SS.QueryOweWriteOffSVC.queryArea", input);
        if (IDataUtil.isNotEmpty(areas))
        {
            for (int i = 0; i < areas.size(); i++)
            {
                String areaName = areas.getData(i).getString("AREA_NAME");
                String areaCode = areas.getData(i).getString("AREA_CODE");
                areas.getData(i).put("AREA_NAME", "[" + areaCode + "]" + areaName);
            }
            setAreas(areas);
        }
    }

    /**
     * 功能：移动电话欠费销号清单查询
     */
    public void queryOweWriteOff(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QueryOweWriteOffSVC.queryOweWriteOff", inparam, getPagination("navt"));
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合查询条件的【移动电话欠费销号清单】数据~";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setInfos(results);
        setCount(dataCount.getDataCount());
        setCondition(getData("cond", true));

    }

    public abstract void setAreas(IDataset areas);

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

}
