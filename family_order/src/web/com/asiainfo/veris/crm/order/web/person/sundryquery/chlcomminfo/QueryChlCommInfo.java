
package com.asiainfo.veris.crm.order.web.person.sundryquery.chlcomminfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：渠道通讯费补贴查询 作者：GongGuang
 */
public abstract class QueryChlCommInfo extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 初始化
     */
    public void initParams(IRequestCycle cycle) throws Exception
    {
        // 设置起止时间

        IData data = getData("cond", true);
        queryArea(cycle);
        data.put("cond_CITY_CODE", getVisit().getCityCode());
        setCond(data);// 默认选上登录业务区

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
     * 功能：渠道通讯费补贴查询
     */
    public void queryChlCommInfo(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QueryChlCommInfoSVC.queryChlCommInfo", inparam, getPagination("navt"));
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "查询渠道补贴信息无数据";
            setExport(false);
        }
        else
        {
            setExport(true);
            setInfos(results);
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示

        setCount(dataCount.getDataCount());
        setCond(getData("cond", true));

    }

    public abstract void setAreas(IDataset areas);

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setExport(boolean b);

    public abstract void setInfos(IDataset infos);

}
