
package com.asiainfo.veris.crm.order.web.person.countryreceipt.receiptprint;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ReceiptPrint extends PersonBasePage
{

    /**
     * @Description 初始化界面提示信息
     * @param cycle
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData();
        inputData.put("cond_ACCEPT_TIME", SysDateMgr.getSysDate().substring(0, 7));
        inputData.put("cond_TRADE_STAFF_ID", getVisit().getStaffId());
        this.setCondition(inputData);
    }

    /**
     * @Description 查询可补打流水
     * @param cycle
     */
    public void queryTradeReceipt(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData("cond");
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.ReceiptPrintSVC.queryTradeReceipt", inputData);

        if (IDataUtil.isEmpty(result))
        {
            this.setAjax("ALERT_INFO", "无数据");
        }
        // 设置页面返回数据
        setInfos(result);
    }

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    /**
     * @Description 补打发票
     * @param cycle
     */
    public void submitPrintReceipt(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData();
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.ReceiptPrintSVC.submitPrintReceipt", inputData);

        // 设置页面返回数据
        setAjax(result);
    }
    
    /**
     * @Description 构建打印数据
     * @param cycle
     */
    public void printTrade(IRequestCycle cycle) throws Exception{
        IData inputData = this.getData();
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        inputData.put("STAFF_NAME", getVisit().getStaffName());
        inputData.put("DEPART_NAME", getVisit().getDepartName());

        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.ReceiptPrintSVC.printTrade", inputData);

        // 设置页面返回数据
        setAjax(result);
    }
    
}
