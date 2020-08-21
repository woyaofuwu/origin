
package com.asiainfo.veris.crm.order.web.person.changeacctinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ModifyAcctInfo extends PersonBasePage
{

    // 判断是否是统付用户
    public void checkUserSpecialepay(IRequestCycle cycle) throws Exception
    { 
        IData pageData = getData();
        IData params = new DataMap();
        params.put("PAY_MODE_CODE", pageData.getString("acctinfo_PAY_MODE_CODE", ""));
        params.put("USER_ID", pageData.getString("USER_ID", ""));
        params.put(Route.ROUTE_EPARCHY_CODE, pageData.getString("EPARCHY_CODE"));
        params.put("BANK_CODE", pageData.getString("acctinfo_BANK_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.ModifyAcctInfoSVC.checkUserSpecialepay", params);
        setAjax(dataset.getData(0));
    }

    /**
     * 页面初始化方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setTradeTypeCode(data.getString("TRADE_TYPE_CODE"));
        setAuthType(data.getString("AUTH_TYPE", "00"));

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
        setAcctInfo(acctInfo);

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset acctDayInfoSet = CSViewCall.call(this, "SS.ModifyAcctInfoSVC.getUserAcctDayInfo", param);
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
        IData pageData = getData();
        IData data = getData("acctinfo", true);
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", pageData.getString("SERIAL_NUMBER"));
        param.put("TRADE_TYPE_CODE", pageData.getString("TRADE_TYPE_CODE"));
        param.put("CHECK_MODE", pageData.getString("CHECK_MODE"));// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱
        param.putAll(data);
        param.putAll(getData("comminfo", true));                

        IDataset dataset = CSViewCall.call(this, "SS.ModifyAcctInfoRegSVC.tradeReg", param);
        setAjax(dataset);
    }

    public abstract void setAcctDayInfo(IData acctDayInfo);

    public abstract void setAcctInfo(IData acctInfo);

    public abstract void setAuthType(String authType);

    public abstract void setTradeTypeCode(String tradeTypeCode);

    public abstract void setUserInfo(IData userInfo);

}
