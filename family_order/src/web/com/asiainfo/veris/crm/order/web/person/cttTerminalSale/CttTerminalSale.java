package com.asiainfo.veris.crm.order.web.person.cttTerminalSale;

import java.math.BigDecimal;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public class CttTerminalSale extends PersonBasePage{

	/**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CttTerminalSaleSVC.checkUserInfo", data);
        
        if (IDataUtil.isNotEmpty(dataset))
        {
            this.setAjax(dataset);
        }
    }
    
    /**
     * 提交方法
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	
    	String serialNumber=data.getString("AUTH_SERIAL_NUMBER");
    	
    	data.put("SERIAL_NUMBER", serialNumber);
    	data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	
    	
    	//根据终端类型来设置不同的费用类型
    	IDataset dataset = CSViewCall.call(this, "SS.CttTerminalSaleSVC.obtainTerminalTypeFeeType", data);
    	String feeTypeCode=dataset.getData(0).getString("DATA_NAME");
    	data.put("FEE_TYPE_CODE", feeTypeCode);
    	
    	double totalPrice=data.getDouble("TERMINAL_TOTAL_PRICE",0);
    	
    	if(totalPrice>0){
    		
    		BigDecimal totalPriceD = new BigDecimal(totalPrice*100);
    		String totalPriceDStr=totalPriceD.toString();
    		
    		IData tradeFeeSub=new DataMap();
        	tradeFeeSub.put("TRADE_TYPE_CODE", "9115");
        	tradeFeeSub.put("FEE_TYPE_CODE", feeTypeCode);	//营业费用
        	tradeFeeSub.put("FEE", totalPriceDStr);
        	tradeFeeSub.put("OLDFEE", totalPriceDStr);
        	tradeFeeSub.put("FEE_MODE", "0");	//营业费用
        	tradeFeeSub.put("ELEMENT_ID", "");
        	
        	IData tradePayMoney=new DataMap();
        	tradePayMoney.put("PAY_MONEY_CODE", "0");
        	tradePayMoney.put("MONEY", totalPriceDStr);
        	
        	IDataset tradeFeeSubs=new DatasetList();
        	tradeFeeSubs.add(tradeFeeSub);
        	
        	IDataset tradePayMoneys=new DatasetList();
        	tradePayMoneys.add(tradePayMoney);
        	
        	
        	data.put("X_TRADE_FEESUB", tradeFeeSubs);
        	data.put("X_TRADE_PAYMONEY", tradePayMoneys);
    	}
    	
        IDataset saleActives = CSViewCall.call(this, "SS.CttTerminalSaleRegSVC.tradeReg", data);
        setAjax(saleActives);
    }

}
