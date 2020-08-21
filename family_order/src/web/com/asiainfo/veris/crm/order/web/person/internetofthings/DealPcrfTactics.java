package com.asiainfo.veris.crm.order.web.person.internetofthings;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;


public abstract class DealPcrfTactics extends PersonBasePage{
    
    public abstract void setInfo(IData info);
    
    public abstract void setInfos(IDataset infoa);
    
    public abstract void setSvcInfos(IDataset infoa);
    
    public abstract void setTradeTypeCode(String tradeTypeCode);
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
    }
    
    public void getUserPcrfs(IRequestCycle cycle) throws Exception
    {
    	IData param = this.getData();
    	param.put("SERIAL_NUMBER", param.getString("AUTH_SERIAL_NUMBER"));
    	IDataset userPcrfInfos = CSViewCall.call(this, "SS.IOTQuerySVC.queryUserPcrfInfos", param);
    	this.setInfos(userPcrfInfos);
    	setAjax(userPcrfInfos);
    }
    
    public void getUserSvcs(IRequestCycle cycle) throws Exception
    {
    	IData param = this.getData();
    	param.put("SERIAL_NUMBER", param.getString("AUTH_SERIAL_NUMBER"));
    	IDataset userSvcInfos = CSViewCall.call(this, "SS.IOTQuerySVC.queryUserSvcInfos", param);
    	this.setSvcInfos(userSvcInfos);
    	setAjax(userSvcInfos);
    }
    /**
     * 提交生成订单
     * 
     * @param cycle
     * @throws Exception
     */
    public void submitTrade(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        param.put("SERIAL_NUMBER", param.getString("AUTH_SERIAL_NUMBER"));
        IDataset result = CSViewCall.call(this, "SS.DealPcrfTacticsRegSVC.tradeReg", param);    
        System.out.println(param);
        this.setAjax(result);
    }
}
