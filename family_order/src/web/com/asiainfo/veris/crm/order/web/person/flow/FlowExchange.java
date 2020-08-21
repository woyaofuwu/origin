package com.asiainfo.veris.crm.order.web.person.flow;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FlowExchange extends PersonBasePage{

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
    }
    
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
    	IData pgData = this.getData();
        IData userInfo = new DataMap(pgData.getString("USER_INFO", ""));
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        inparam.put("USER_ID", userInfo.getString("USER_ID"));
        inparam.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IDataset idsBalances = CSViewCall.call(this, "SS.FlowChargeSVC.queryBalanceDetail", inparam);
        setBalances(idsBalances);
    }
    
    public void flowProdQry(IRequestCycle cycle) throws Exception 
    {
    	IData cond = this.getData();
    	
    	String strCommID = cond.getString("SEL_COMM_ID");
    	String strCommName = cond.getString("SEL_COMM_NAME");
    	IData param = new DataMap();
    	param.put("COMM_ID", strCommID);
    	param.put("COMM_NAME", strCommName);
    	param.put(Route.ROUTE_EPARCHY_CODE, "0898");
    	IDataset idsComparam1688 = CSViewCall.call(this, "SS.FlowChargeSVC.flowProdQry", param);
    	if(IDataUtil.isNotEmpty(idsComparam1688))
		{
    		for(int i = 0; i < idsComparam1688.size(); i++)
        	{
        	    IData idComparam1688 = idsComparam1688.getData(i);
        	    //商品编码 商品名称 账本类型 话费(元) 流量(M) 生效时间 失效时间
        	    String strPCommID = idComparam1688.getString("PARA_CODE1");
        	    String strPCommName = idComparam1688.getString("PARA_CODE2");
        	    String strAsserTypeID = idComparam1688.getString("PARA_CODE3");
        	    Long strPrice = idComparam1688.getLong("PARA_CODE4") / 100;
        	    Long strInitValue = idComparam1688.getLong("PARA_CODE5") / 1024;
        	    String strStartDate = idComparam1688.getString("PARA_CODE26");
        	    String strEndDate = idComparam1688.getString("PARA_CODE27");
        	    idComparam1688.put("COMM_ID", strPCommID);
        	    idComparam1688.put("COMM_NAME", strPCommName);
        	    idComparam1688.put("ASSET_TYPE_ID", strAsserTypeID);
        	    idComparam1688.put("PRICE", strPrice);
        	    idComparam1688.put("INIT_VALUE", strInitValue);
        	    idComparam1688.put("START_DATE", strStartDate);
        	    idComparam1688.put("END_DATE", strEndDate);
        	}
        	setInfos(idsComparam1688);
		}
    }
    
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        //String strCheckBox = pageData.getString("updateNowCheckBox");
        //pageData.putAll(getData("FMY"));
        //pageData.put("IN_TAG", "0");// 0表示前台办理
        //pageData.put("updateNowCheckBox", strCheckBox);
        IDataset rtDataset = CSViewCall.call(this, "SS.FlowExchangeRegSVC.tradeReg", pageData);
        this.setAjax(rtDataset);
    }
    
    public abstract void setBalances(IDataset idsBalances);
    
    public abstract void setAcctInfo(IData acctInfo);

    public abstract void setCommInfo(IData commInfo);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfos(IDataset infos);
}
