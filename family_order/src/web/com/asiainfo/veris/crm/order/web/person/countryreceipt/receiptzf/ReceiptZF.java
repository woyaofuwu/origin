
package com.asiainfo.veris.crm.order.web.person.countryreceipt.receiptzf;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ReceiptZF extends PersonBasePage
{

    /**
     * 初始化界面提示信息
     * 
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
     * 查询可作废发票记录
     * 
     * @param cycle
     */
    public void queryZFReceipt(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData("cond");
        inputData.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.ReceiptZFSVC.queryZFReceipt", inputData);

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
     * 发票作废
     * 
     * @param cycle
     */
    public void submitZFReceipt(IRequestCycle cycle) throws Exception
    {
        IData inputData = getData();
        inputData.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        CSViewCall.call(this, "SS.ReceiptZFSVC.submitZFReceipt", inputData);
    }
}
