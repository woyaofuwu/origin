package com.asiainfo.veris.crm.order.web.person.flow;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FlowCharge extends PersonBasePage
{

	/**
     * 菜单点击执行的事件
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        /*IData pgData = this.getData();
        IData userInfo = new DataMap(pgData.getString("USER_INFO", ""));
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        inparam.put("USER_ID", userInfo.getString("USER_ID"));
        inparam.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IDataset idsBalances = CSViewCall.call(this, "SS.FlowChargeSVC.loadFlowChargeInfo", inparam);
        setBalances(idsBalances);*/
        //editInfo.put("TRADE_TYPE_CODE", pgData.getString("TRADE_TYPE_CODE", "666"));
    	
		iniFastPayInfo("1");
    }
    
    /**
     * 菜单点击执行的事件
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryFastPayCode(IRequestCycle cycle) throws Exception
    {
    	IData pgData = this.getData();
    	String strParaCpde3 = pgData.getString("FP_FAST_PAY_CODE");
    	iniFastPayInfo(strParaCpde3);
    }
    
    public void iniFastPayInfo(String strParaCpde3) throws Exception
    {
    	IData param = new DataMap();
        param.put("PARA_CODE3", strParaCpde3);
        param.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IDataset idsFastpays = CSViewCall.call(this, "SS.FlowChargeSVC.qryFastPayInfos", param);
        setFastpays(idsFastpays);
    }
    
    /*public void iniFastPayCode() throws Exception
    {
    	IData param = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IDataset idsFastpays = CSViewCall.call(this, "SS.FlowChargeSVC.qryFastPayInfos", param);
        setFastpays(idsFastpays);
    }*/
    
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
    	IData pgData = this.getData();
        IData userInfo = new DataMap(pgData.getString("USER_INFO", ""));
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        //inparam.put("USER_ID", userInfo.getString("USER_ID"));
        param.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IDataset idsBalances = CSViewCall.call(this, "SS.FlowChargeSVC.queryBalanceDetail", param);
        setBalances(idsBalances);
        setUserInfo(userInfo);
        iniFastPayInfo("1");
    }
    
    public void flowProdQry(IRequestCycle cycle) throws Exception 
    {
    	IData cond = this.getData();
    	
    	String strUserID = cond.getString("QR_USER_ID");
    	String strCommID = cond.getString("SEL_COMM_ID");
    	String strCommName = cond.getString("SEL_COMM_NAME");
    	IData param = new DataMap();
    	param.put("USER_ID", strUserID);
    	param.put("COMM_ID", strCommID);
    	param.put("COMM_NAME", strCommName);
    	param.put(Route.ROUTE_EPARCHY_CODE, "0898");
    	IDataset idsComparam1688 = CSViewCall.call(this, "SS.FlowChargeSVC.flowProdQry", param);
    	//setInfos(idsComparam1688);
    	setInfos(idsComparam1688);
    	setAjax(idsComparam1688);
    }
    
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        String strFastPayFee = pageData.getString("Fast_Pay_Fee", "");
        
        if("1".equals(strFastPayFee))
        {
        	String strTransNeeded = pageData.getString("FP_TRANS_NEEDED", "");
        	String strCommName = pageData.getString("COMM_NAME", "");
        	String strSn = pageData.getString("FP_SERIAL_NUMBER", "");
        	pageData.put("SERIAL_NUMBER", strSn);
        	/*String strTransNeeded = pageData.getString("FP_TRANS_NEEDED", "");
        	String strCommID = pageData.getString("FP_COMM_ID");
        	String strCommName = pageData.getString("FP_COMM_NAME", "");
        	IData param = new DataMap();
        	param.put("COMM_ID", strCommID);
        	param.put("COMM_NAME", strCommName);
        	param.put(Route.ROUTE_EPARCHY_CODE, "0898");
        	IDataset idsComparam1688 = CSViewCall.call(this, "SS.FlowChargeSVC.flowProdQry", param);
        	if(IDataUtil.isNotEmpty(idsComparam1688))
    		{
        		IData idComparam1688 = idsComparam1688.first();
        	    //商品编码 商品名称 账本类型 话费(元) 流量(M) 生效时间 失效时间
        	    String strCommid = idComparam1688.getString("OFFER_CODE");
        	    String strOfferName = idComparam1688.getString("OFFER_NAME");
        	    String strFee = idComparam1688.getString("FEE");
        	    Long nPrice = Long.parseLong(strFee) / 100;
        	    String strInitValue = idComparam1688.getString("INIT_VALUE");
        	    
        	    String strStartDate = idComparam1688.getString("START_DATE");
        	    String strEndDate = idComparam1688.getString("END_DATE");*/
        	    /** 根据配置计算开始时间 */
        	    /*String strEnableMode = idComparam1688.getString("ENABLE_MODE");
        	    String strAbsoluteEnableDate = idComparam1688.getString("ABSOLUTE_ENABLE_DATE");
        	    String strEnableOffset = idComparam1688.getString("ENABLE_OFFSET");
        	    String strEnableUnit = idComparam1688.getString("ENABLE_UNIT");
        	    
        	    String strStartDate = "";
        	    if ("1".equals(strEnableMode))
        	    {
        	    	strStartDate = SysDateMgr.getFirstDayOfNextMonth();
        	    }
        	    else if("0".equals(strEnableMode) || "2".equals(strEnableMode) || "4".equals(strEnableMode))
        	    {
        	    	strStartDate = SysDateMgr.startDate(strEnableMode, strAbsoluteEnableDate, strEnableOffset, strEnableUnit);
        	    }
        	    //String strStartDate = SysDateMgr.startDate(strEnableMode, strAbsoluteEnableDate, strEnableOffset, strEnableUnit);
        	    
        	    *//** 根据配置计算结束时间 *//*
        	    String strDisableMode = idComparam1688.getString("DISABLE_MODE");
        	    String strAbsoluteDisableDate = idComparam1688.getString("ABSOLUTE_DISABLE_DATE");
        	    String strDisableOffset = idComparam1688.getString("DISABLE_OFFSET");
        	    String strDisableUnit = idComparam1688.getString("DISABLE_UNIT");
        	    String strEndDate = SysDateMgr.endDate(strStartDate, strDisableMode, strAbsoluteDisableDate, strDisableOffset, strDisableUnit);*/
        	    
        	    /*pageData.put("COMM_ID", strCommid);
        	    pageData.put("AMOUNT", strInitValue);
        	    pageData.put("TRANS_FEE", nPrice);
        	    pageData.put("EFFECTIVE_DATE", strStartDate);
        	    pageData.put("EXPIRE_DATE", strEndDate);
        	    pageData.put("TRANS_NEEDED", strTransNeeded);
        	    pageData.put("COMM_NUM", "1");*/
        		pageData.put("TRANS_NEEDED", strTransNeeded);
        	    pageData.put("REMARK", strCommName + "-快速充值");
        	    IDataset rtDataset = CSViewCall.call(this, "SS.FlowChargeRegSVC.tradeReg", pageData);
                this.setAjax(rtDataset);
    		//}
        	/*else
        	{
        		CSAppException.apperr(CrmCommException.CRM_COMM_13, "170615001:流量快速充值配置错误,商品编码：",strCommID);
        	}*/
        }
        else
        {
        	IDataset rtDataset = CSViewCall.call(this, "SS.FlowChargeRegSVC.tradeReg", pageData);
            this.setAjax(rtDataset);
        } 
    }
    
    public abstract void setBalances(IDataset idsBalances);
    
    public abstract void setAcctInfo(IData acctInfo);

    public abstract void setCommInfo(IData commInfo);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setFastpays(IDataset fastPays);
    
    public abstract void setUserInfo(IData userInfo);
}
