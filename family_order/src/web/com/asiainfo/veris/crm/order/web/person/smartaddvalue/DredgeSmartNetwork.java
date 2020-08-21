package com.asiainfo.veris.crm.order.web.person.smartaddvalue;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class DredgeSmartNetwork extends PersonBasePage{

    /**
     * 页面初始化方法
     *
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData initParam = new DataMap();
        initParam.put("authType", data.getString("authType", "00"));
        initParam.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE", "870"));
        setInfo(initParam);

    }

    /**
     * 业务提交
     *
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData authData = getData("AUTH", true);
        data.putAll(authData);
        IData otherData = getData("otherinfo", true);
        data.putAll(otherData);

        IDataset dataset = CSViewCall.call(this, "SS.DredgeSmartNetworkRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    /**
     * 提交前费用校验
     * @param cycle
     * @throws Exception
     * @author lizj
     */
    public void checkFeeBeforeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.DredgeSmartNetworkIntfSVC.checkFeeBeforeSubmit", data);

        this.setAjax(result);
    }

    /**
     * 提交前宽带校验
     * @param cycle
     * @throws Exception
     * @author lizj
     */
    public void checkWideBeforeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.DredgeSmartNetworkIntfSVC.checkQualificate", data);

        this.setAjax(result);
    }


    /**
     * 获取设备
     * @param cycle
     * @throws Exception
     * @author lizj
     */
    public void getDevice(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.DredgeSmartNetworkSVC.getDevice", data);

        this.setAjax(result);
    }

    public abstract void setAuthType(String authType);

    public abstract void setTradeTypeCode(String tradeTypeCode);

    public abstract void setInfo(IData info);




}
