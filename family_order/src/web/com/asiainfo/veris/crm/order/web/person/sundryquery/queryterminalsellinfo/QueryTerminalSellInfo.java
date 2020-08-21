package com.asiainfo.veris.crm.order.web.person.sundryquery.queryterminalsellinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;



public abstract class QueryTerminalSellInfo extends PersonQueryPage{
	public abstract void setInfos(IDataset infos);
	public abstract void setTerminals(IDataset terminals);
	public abstract void setTerminal(IData terminal);
	public abstract void setInfo(IData info);
	public abstract void setCount(long count);
	public abstract void setCondition(IData condition);	
	
	/**
	 * 功能描述:钱从分转为元
	 * @param str 源串
	 */
	public String chargeMoney(String str) {
		String retnStr = "0元";
		if (str == null || "".equals(str)) {
			return retnStr;
		}
		try{
			float money= Float.parseFloat(str);
			money = money/100;
			retnStr = money+"元";
		}catch (Exception e) {
			retnStr = "0元";
			throw new RuntimeException(e);
		}

		return retnStr;
	}
	
	public void getTerminalByHW(IRequestCycle cycle) throws Exception
    {        		
		IData params = getData() ;
        String startPrice = params.getString("STARTPRICE");
        String endPrice = params.getString("ENDPRICE");
        String factorCode = params.getString("FACTOR_CODE");
        String terminalType = params.getString("TERMINAL_TYPE");
        params.put("QRY_TER_PRICE", "1");
        if (StringUtils.isBlank(factorCode)&& StringUtils.isBlank(startPrice)&& StringUtils.isBlank(endPrice))
        {
        	CSViewException.apperr( CrmCommException.CRM_COMM_1, "终端厂商和价格区间必选一项！");
            return;
        } 
        params.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset products = CSViewCall.call(this, "CS.TerminalQuerySVC.getTerminalByPriceOrBrand", params);
        
        
        if(!"".equals(terminalType))
        {
        	IDataset result = new DatasetList();
        	for(int idx = 0; idx < products.size(); idx++)
            {
        		
        		IData data=products.getData(idx);
        		
        		if(IDataUtil.isEmpty(data)){
        			continue;
        		}
        		if(data.getString("DEVICE_TYPE_CODE","").equals("")){
        			continue;
        		}
        		
        		String StrTmp = data.getString("GOODS_EXPLAIN");
        		String[] goodsInfo =  StrTmp.split("\\|");
        		String TargetInfo = goodsInfo[0] + goodsInfo[1];
  
        		if(TargetInfo.contains(terminalType))
              	{
        			result.add(products.getData(idx));
              	}
            }
        	this.setAjax(result);
        }else{
        	
        	IDataset result = new DatasetList();
        	for(int idx = 0; idx < products.size(); idx++)
            {
        		
        		IData data=products.getData(idx);
        		
        		if(IDataUtil.isEmpty(data)){
        			continue;
        		}
        		if(data.getString("DEVICE_TYPE_CODE","").equals("")){
        			continue;
        		}
        		
        		result.add(data);
            }
        	this.setAjax(result);
        }
     }

	public void refreshProduct(IRequestCycle cycle) throws Exception
    {
		IData param = getData();
		String deviceModelCode= param.getString("DEVICE_MODEL_CODE");
		String[] infoStr = deviceModelCode.split(",");
		param.put("DEVICE_TYPE_CODE", infoStr[1]);
		param.put("DEVICE_MODEL_CODE", infoStr[0]);
		param.put("DEVICE_COST", infoStr[2]);
		param.put("SALE_PRICE", infoStr[3]);
		param.put("EPARCHY_CODE", this.getTradeEparchyCode());
		
		IDataset products = CSViewCall.call(this, "SS.QueryTerminalSellInfoSVC.queryProductsByTerminal", param);
		this.setAjax(products);
    }
	
	public void getTerminalsByProductID(IRequestCycle cycle) throws Exception
    {        		
		IData param = getData();
        if (StringUtils.isBlank(param.getString("SALE_CAMPN_TYPE")))
        {
        	CSViewException.apperr( CrmCommException.CRM_COMM_1, "请选择营销方案！");
            return;
        }
        param.put("EPARCHY_CODE", this.getTradeEparchyCode());
        param.put("PRODUCT_ID",param.getString("SALE_CAMPN_TYPE"));
        param.put("QUERY_TERMINAL", "1");
        IDataset products = CSViewCall.call(this, "CS.SaleActiveQuerySVC.getTerminalsByProductID", param);
        
        
        IDataset result = new DatasetList(); 
        
        String compare = "";
        for(int idx = 0; idx < products.size(); idx++)
        {
        	
        	String deviceModelCode = products.getData(idx).getString("DEVICE_MODEL_CODE");
        	//第一次直接赋值
        	if(idx == 0)
        	{
        		compare = deviceModelCode;
        		result.add(products.getData(idx));
        		continue;
        	}
        		
            if(compare.contains(deviceModelCode))
            {
            	continue;
            }	
        	

        	compare += ","+deviceModelCode;
        	result.add(products.getData(idx));
        }
        
       
        this.setAjax(result);
        
    }
	
	public void refreshProduct1(IRequestCycle cycle) throws Exception
    {
		IData param = new DataMap();
		param.put("EPARCHY_CODE", this.getTradeEparchyCode());
		IDataset products = CSViewCall.call(this, "SS.QueryTerminalSellInfoSVC.queryProductsByCamp", param);
		this.setAjax(products);
    }
	
	/**
	 * 终端销售价格查询
	 * @param cycle
	 * @throws Exception
	 */
	public void queryPriceInfo(IRequestCycle cycle) throws Exception{
		IData data = getData();
		String salecampntype = data.getString("SALE_CAMPN_TYPE", "");
		
		if(!"".equals(salecampntype)&&salecampntype!=null)//根据活动类型查询
		{
			IDataOutput results = CSViewCall.callPage(this, "SS.QueryTerminalSellInfoSVC.queryPriceInfoByCamp", data,getPagination("qryInfoNav"));
	        setTerminals(results.getData());
	        setCount(results.getDataCount());
		}
		
		else
		{
			IDataOutput results = CSViewCall.callPage(this, "SS.QueryTerminalSellInfoSVC.queryPriceInfoByPID", data,getPagination("qryInfoNav"));
	        setTerminals(results.getData());
	        setCount(results.getDataCount());
		}
	}
	
}
