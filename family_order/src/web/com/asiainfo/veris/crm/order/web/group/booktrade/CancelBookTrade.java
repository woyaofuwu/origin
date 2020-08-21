
package com.asiainfo.veris.crm.order.web.group.booktrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class CancelBookTrade extends GroupBasePage
{

    public abstract void setCancelTradeList(IDataset datas);

    public abstract void setValidCancelTradeList(IDataset datas);

    public abstract void setCancelTradeId(String cancelTradeId);

    public abstract void setCondition(IData cond);

    public abstract void setCust(IData cust);

    public abstract void setTrade(IData trade);

    public abstract void setHintInfo(String hintInfo);

    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }

    public void queryUserCancelTrade(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String SerialNumber = param.getString("cond_SERIAL_NUMBER", "");
        String subscribe_type = param.getString("cond_SUBSCRIBE_TYPE", "");

        IData inputparam = new DataMap();
        inputparam.put("SERIAL_NUMBER", SerialNumber);
        inputparam.put("SUBSCRIBE_TYPE", subscribe_type);

        IDataset ErrorInfos = CSViewCall.call(this, "SS.BookTradeSVC.queryErrorInfoTrade", inputparam);
        if (ErrorInfos != null && ErrorInfos.size() > 0)
        {
            CSViewException.apperr(GrpException.CRM_GRP_661);
        }

        IDataset datas = CSViewCall.call(this, "SS.BookTradeSVC.queryUserCancelTrade", inputparam);
        if (datas != null && datas.size() > 0)
        {
        }
        else
        {
            CSViewException.apperr(GrpException.CRM_GRP_662);
        }

        setValidCancelTradeList(datas);
        setCondition(param);
        setHintInfo("查询成功！");
    }

    public void cancelTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String trade_id = param.getString("pam_NOTIN_TRADE_ID", "");
        String user_id = param.getString("pam_NOTIN_USER_ID", "");
        String foregift = param.getString("pam_NOTIN_FOREGIFT", "");
        String allFee = param.getString("pam_NOTIN_ALL_FEE", "");
        String serialNumber = param.getString("pam_NOTIN_SERIAL_NUMBER", "");
        String remark = param.getString("pam_NOTIN_CANCEL_REMARK", "");

        IData inRuleParam = new DataMap();

        inRuleParam.put("ID", user_id);
        inRuleParam.put("PROVINCE_CODE", getVisit().getProvinceCode());
        inRuleParam.put("TRADE_EPARCHY_CODE", getVisit().getLoginLogId());
        inRuleParam.put("TRADE_STAFF_ID", getVisit().getStaffId());
        inRuleParam.put("TRADE_CITY_CODE", getVisit().getCityCode());
        inRuleParam.put("TRADE_DEPART_ID", getVisit().getDepartId());
        inRuleParam.put("TRADE_ID", trade_id);
        // IData outData = CheckForPer.chkTradeBeforeCancel(pd, inRuleParam);

        IData inparam = new DataMap();
        inparam.put("IN_MODE_CODE", "0");
        inparam.put("TERM_IP", getVisit().getLoginIP());
        inparam.put("FOREGIFT", Double.valueOf(foregift) * 100);
        inparam.put("REMARK", remark);
        inparam.put("TRADE_ID", trade_id);
        inparam.put("TRADE_STAFF_ID", getVisit().getStaffId());
        inparam.put("TRADE_CITY_CODE", getVisit().getCityCode());
        inparam.put("TRADE_DEPART_ID", getVisit().getDepartId());
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        IDataset resultData = CSViewCall.call(this, "SS.CancelTradeSVC.cancelTradeReg", inparam);
        IData result = resultData.getData(0);
        String orderId = result.getString("ORDER_ID");
        IData data = new DataMap();
        data.put("DATAINFO", "取消登记成功!业务流水号为:"+orderId);
        setAjax(data);

    }

}
