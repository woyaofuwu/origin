
package com.asiainfo.veris.crm.order.web.person.sundryquery.specqry;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QuerySpecProNumber extends PersonBasePage
{

    /**
     * 初始化界面提示信息
     * 
     * @param cycle
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData();
        inputData.put("cond_UPDATE_TIME_START", SysDateMgr.getAddMonthsNowday(-1, SysDateMgr.getSysDate()));
        inputData.put("cond_UPDATE_TIME_END", SysDateMgr.getSysDate());
        this.setCondition(inputData);
    }

    /**
     * 执行特殊处理号码查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void querySpecProNumber(IRequestCycle cycle) throws Exception
    {

        IData inputData = this.getData("cond");
        inputData.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        inputData.put("UPDATE_TIME_END", inputData.getString("UPDATE_TIME_END") + SysDateMgr.END_DATE);

        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.SundryQrySVC.querySpecProNumber", inputData);

        if (IDataUtil.isEmpty(result))
        {
            this.setAjax("ALERT_INFO", "无数据");
        }
        // 设置页面返回数据
        setInfos(result);
    }

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);
}
