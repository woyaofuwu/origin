
package com.asiainfo.veris.crm.order.web.group.taxprint;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.TaxException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class GrpTaxPrintTicketApply extends CSBasePage
{

    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();

        IData svcData = new DataMap();

        svcData.put("AREA_CODE", getVisit().getCityCode());
        svcData.put("ROLE_TYPE", "1"); // 默认传1

        // 获取下级审批人信息
        IDataset nextUserList = CSViewCall.call(this, "SS.GrpTaxPrintTicketApplySVC.qryNextUser", svcData);

        condData.put("cond_START_DATE", SysDateMgr.getSysDate());
        condData.put("cond_END_DATE", SysDateMgr.getTomorrowDate());

        // 设置返回值
        setNextUserList(nextUserList);
        setCondition(condData);
    }

    /**
     * 查询增值税业务受理信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryTaxLog(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);

        IData svcData = new DataMap();

        svcData.put("CUST_ID", condData.getString("CUST_ID"));
        svcData.put("START_DATE", condData.getString("START_DATE"));
        svcData.put("END_DATE", condData.getString("END_DATE"));

        IDataOutput dataOutput = CSViewCall.callPage(this, "CS.TaxLogInfoQrySVC.qryTaxLogForPrint", svcData, getPagination("pageNav"));

        IDataset taxLogList = dataOutput.getData();

        // 设置返回值
        setTaxLogList(taxLogList);

        setInfoCount(IDataUtil.isEmpty(taxLogList) ? 0 : taxLogList.size());
    }

    /**
     * 查询增值税详细信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryTaxDetail(IRequestCycle cycle) throws Exception
    {
        String tradeId = getData("cond", true).getString("TRADE_ID");

        IData svcData = new DataMap();

        svcData.put("TRADE_ID", tradeId);

        IDataset taxDetailList = CSViewCall.call(this, "SS.GrpTaxPrintTicketApplySVC.qryTaxDetailByTradeId", svcData);

        setAjax(taxDetailList);
    }

    /**
     * 查询客户资质信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryCustTaxApply(IRequestCycle cycle) throws Exception
    {
        String groupId = getData().getString("GROUP_ID");

        IData svcData = new DataMap();

        svcData.put("GROUP_ID", groupId);
        svcData.put("OPER_TYPE", "2"); // 查询类型

        IDataset taxApplyList = CSViewCall.call(this, "SS.GrpTaxPrintTicketApplySVC.qryTaxApplyByGroupId", svcData);

        // 无客户资质信息
        if (IDataUtil.isEmpty(taxApplyList))
        {
            CSViewException.apperr(TaxException.CRM_TAX_15, groupId);
        }

        IData taxApplyData = taxApplyList.getData(0);

        // 判断是否有客户资质
        if (!"0".equals(taxApplyData.getString("X_RESULTCODE")))
        {
            CSViewException.apperr(TaxException.CRM_TAX_20, taxApplyData.getString("X_RESULTINFO"));
        }

        // 只有一般人有纳税人资质
        if (!"1".equals(taxApplyData.getString("QUA_TYPE")))
        {
            CSViewException.apperr(TaxException.CRM_TAX_13);
        }

        // 设置返回值
        setTaxApply(taxApplyData);
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);

        // 查询集团客户信息
        String groupId = condData.getString("GROUP_ID");

        IData grpCustData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);

        // 构建服务数据
        IData svcData = new DataMap();

        svcData.put("CUST_ID", grpCustData.getString("CUST_ID"));
        svcData.put("TRADE_ID", condData.getString("TRADE_ID"));
        svcData.put("COMP_NAME", condData.getString("COMP_NAME"));
        svcData.put("BANK_NO", condData.getString("BANK_NO"));
        svcData.put("TELEPHONE", condData.getString("TELEPHONE"));
        svcData.put("NEXT_USER", condData.getString("NEXT_USER"));
        svcData.put("REMARK", condData.getString("REMARK"));
        svcData.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());

        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.GrpTaxPrintTicketApplySVC.createApproverReceipt", svcData);

        // 设置返回值
        setAjax(retDataset);
    }

    public abstract void setNextUserList(IDataset nextUserList);

    public abstract void setCondition(IData condition);

    public abstract void setTaxApply(IData taxApply);

    public abstract void setTaxLogList(IDataset taxLogList);

    public abstract void setInfoCount(long infoCount);
}
