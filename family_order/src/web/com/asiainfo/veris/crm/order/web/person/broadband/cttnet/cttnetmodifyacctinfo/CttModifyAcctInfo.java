package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetmodifyacctinfo;

import org.apache.tapestry.IRequestCycle;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CttModifyAcctInfo extends PersonBasePage
{

    /**
     * 查询改名费
     * 
     * @param cycle
     * @throws Exception
     */
    public void ajaxModiAcctName(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        data.put("TRADE_TYPE_CODE", "9728");
        IDataset dataset = CSViewCall.call(this, "SS.CttModifyCustInfoSVC.ajaxModiCustName", data);

        if (dataset == null || dataset.isEmpty())
        {
            dataset = new DatasetList();
        }
        setAjax(dataset);
    }

    // 判断是否是统付用户
    public void checkUserSpecialepay(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IData params = new DataMap();
        params.put("PAY_MODE_CODE", pageData.getString("acctinfo_PAY_MODE_CODE", ""));
        params.put("USER_ID", pageData.getString("USER_ID", ""));
        params.put(Route.ROUTE_EPARCHY_CODE, pageData.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.CttModifyAcctInfoSVC.checkUserSpecialepay", params);
        setAjax(dataset.getData(0));
    }

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String userId = data.getString("USER_ID");
        String eparchyCode = data.getString("EPARCHY_CODE");

        IData acctInfo = new DataMap(data.getString("ACCT_INFO", ""));
        String bankCode = acctInfo.getString("BANK_CODE","");
        String bank = StaticUtil.getStaticValue(getVisit(), "TD_B_BANK_CTT", "BANK_CODE", "BANK", bankCode);
        acctInfo.put("BANK", bank);
        String superBankCode = StaticUtil.getStaticValue(getVisit(), "TD_B_BANK_CTT", "BANK_CODE", "SUPER_BANK_CODE", bankCode);
        acctInfo.put("SUPER_BANK_CODE", superBankCode);
        String superBank = StaticUtil.getStaticValue(getVisit(), "TD_S_SUPERBANK_CTT", "SUPER_BANK_CODE", "SUPER_BANK", superBankCode);
        acctInfo.put("SUPER_BANK", superBank);
        setAcctInfo(acctInfo);
        
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset acctDayInfoSet = CSViewCall.call(this, "SS.CttModifyAcctInfoSVC.getUserAcctDayInfo", param);
        setAcctDayInfo(acctDayInfoSet.getData(0));
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        param.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        // param.put("REMARK", getData().getString("REMARK"));
        param.putAll(getData("acctinfo", true));
        param.putAll(getData("comminfo", true));

        IDataset dataset = CSViewCall.call(this, "SS.CttModifyAcctInfoRegSVC.tradeReg", param);
        setAjax(dataset);
    }

    public abstract void setAcctDayInfo(IData acctDayInfo);

    public abstract void setAcctInfo(IData acctInfo);

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfo(IData info);

    public abstract void setNewSnInfo(IData newSnInfo);

    public abstract void setUserInfo(IData userInfo);
}
