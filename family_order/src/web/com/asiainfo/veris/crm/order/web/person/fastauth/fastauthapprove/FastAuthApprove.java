
package com.asiainfo.veris.crm.order.web.person.fastauth.fastauthapprove;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FastAuthApprove extends PersonBasePage
{
    /**
     * 授权审核操作
     */
    public void approveAuthFuc(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData("OPE", true);
        IDataOutput dataCountApprove = CSViewCall.callPage(this, "SS.FastAuthApproveSVC.approveAuth", param, null);
        IData cond = this.getData("COND", true);
        this.setCondition(cond);
        IDataset resultsApprove = dataCountApprove.getData();
        queryApplyTradeList(cycle);
        this.setAjax("UPDATE_SUCCESS_FLAG", "1");
    }

    public void onInitTrade1(IRequestCycle cycle) throws Exception
    {
        queryAcceptTradeList(cycle);
    }

    /** 查询已允许申请的授权业务 */
    public void queryAcceptTradeList(IRequestCycle cycle) throws Exception
    {
        IData input = new DataMap();
        input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        IDataset authTradeTypes = CSViewCall.call(this, "SS.FastAuthApproveSVC.queryAuthTradeType", input);
        if (IDataUtil.isNotEmpty(authTradeTypes))
        {
            for (int i = 0; i < authTradeTypes.size(); i++)
            {
                String name = authTradeTypes.getData(i).getString("MENU_TITLE");
                String id = authTradeTypes.getData(i).getString("MENU_ID");
                authTradeTypes.getData(i).put("MENU_TITLE", "[" + id + "]" + name);
            }
            setAcceptTradeList(authTradeTypes);
        }
    }

    /** 查询已允许申请的授权业务 */
    public void queryApplyTradeList(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData("COND", true);
        param.put("AWS_STAFF_ID", this.getVisit().getStaffId()); // 审核人才可以看到
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.FastAuthApproveSVC.queryApplyTrade", param, getPagination("navt"));
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合查询条件的【已允许申请的授权业务】数据~";
        }
        this.setCondition(param);
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setApplyTradeList(results);
    }

    public abstract void setAcceptTradeList(IDataset acceptTradeList);

    public abstract void setApplyTradeList(IDataset applyTradeList);

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

}
