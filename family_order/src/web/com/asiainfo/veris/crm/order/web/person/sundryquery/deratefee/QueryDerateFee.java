
package com.asiainfo.veris.crm.order.web.person.sundryquery.deratefee;

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
 * 功能：减免费用查询 作者：GongGuang
 */
public abstract class QueryDerateFee extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 初始化
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        // 设置起止时间
        String startData = SysDateMgr.getSysDate();// 格式为YYYY-MM-DD
        String endDate = startData;
        IData data = getData("cond", true);
        data.put("cond_START_DATE", startData);
        data.put("cond_END_DATE", endDate);
        this.setCondition(data);
        queryArea(cycle);
        queryTradeTypeList(cycle);

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
        IDataset areas = CSViewCall.call(this, "SS.QueryDerateFeeSVC.queryArea", input);
        if (IDataUtil.isNotEmpty(areas))
        {
            for (int i = 0; i < areas.size(); i++)
            {
                String areaName = areas.getData(i).getString("AREA_NAME");
                String areaCode = areas.getData(i).getString("AREA_CODE");
                areas.getData(i).put("AREA_NAME", "[" + areaCode + "]" + areaName);
            }
            setArList(areas);
        }
    }

    /**
     * 功能：减免费用查询
     */
    public void queryDerateFee(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QueryDerateFeeSVC.queryDerateFee", inparam, getPagination("navt"));
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合条件的数据";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setInfos(results);
        setCount(dataCount.getDataCount());
        setCondition(getData("cond", true));

    }

    /**
     * 查询TradeTypeList
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryTradeTypeList(IRequestCycle cycle) throws Exception
    {

        IData input = new DataMap();
        IDataset tradeTypeList = CSViewCall.call(this, "SS.QueryDerateFeeSVC.queryTradeTypeList", input);
        if (IDataUtil.isNotEmpty(tradeTypeList))
        {
            for (int i = 0; i < tradeTypeList.size(); i++)
            {
                String tradeTypeName = tradeTypeList.getData(i).getString("PARA_CODE1");
                String tradeTypeCode = tradeTypeList.getData(i).getString("PARAM_CODE");
                tradeTypeList.getData(i).put("PARA_CODE1", "[" + tradeTypeCode + "]" + tradeTypeName);
            }
            setTradeTypeList(tradeTypeList);
        }

    }

    public abstract void setArList(IDataset areas);

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

    public abstract void setTradeTypeList(IDataset tradeTypeList);

}
