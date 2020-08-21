
package com.asiainfo.veris.crm.order.web.person.canceltrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.upc.UpcViewCallIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TradeInfo.java
 * @Description: 个人业务返销订单详细信息
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-7-30 下午3:41:06
 */
public abstract class TradeInfo extends PersonBasePage
{
    /**
     * 初始化查询宽带台账详细信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void initQueryCancelTrade(IRequestCycle cycle) throws Exception
    {
        IData input = new DataMap();
        input.put("TRADE_ID", getData().getString("TRADE_ID"));
        input.put("CANCEL_TAG", "0");
        input.put(Route.ROUTE_EPARCHY_CODE, getData().getString("EPARCHY_CODE"));
        IDataset tradeInfos = CSViewCall.call(this, "CS.TradeInfoQrySVC.queryCancelTradeByTradeId", input);
        if (IDataUtil.isNotEmpty(tradeInfos))
        {
            setTrade(tradeInfos.getData(0));
        }
    }

    /**
     * 初始化查询台账详细信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void initQueryTrade(IRequestCycle cycle) throws Exception
    {
	
        IData input = new DataMap();
        String tradeTypeCode = getData().getString("TRADE_TYPE_CODE","").trim();
        input.put("TRADE_ID", getData().getString("TRADE_ID"));
        input.put("CANCEL_TAG", "0");
        input.put("TRADE_TYPE_CODE", tradeTypeCode);
        
        input.put(Route.ROUTE_EPARCHY_CODE, getData().getString("EPARCHY_CODE"));
        IDataset tradeInfos = CSViewCall.call(this, "CS.TradeHistoryInfoQrySVC.queryTradeHistoryInfos", input);
        if (IDataUtil.isNotEmpty(tradeInfos))
        {
            IData re = tradeInfos.getData(0);
            if(tradeTypeCode.equals("230")||tradeTypeCode.equals("240")||tradeTypeCode.equals("330")||tradeTypeCode.equals("3301")||tradeTypeCode.equals("142")){
        	 re.put("TRADE_TYPE_CODE", tradeTypeCode);
            }else{
        	 re.put("TRADE_TYPE_CODE", "");
        	 re.put("PRODUCT_NAME",UpcViewCall.queryOfferNameByOfferId(this, "P", re.getString("PRODUCT_ID")));
        	 re.put("BRAND_NAME",  UpcViewCall.getBrandNameByBrandCode(this, re.getString("BRAND_CODE")));
            }
            setTrade(re);
            
            if(tradeTypeCode.equals("330")||tradeTypeCode.equals("3301")){
        	setTradeList(tradeInfos);        	
            }

        }
    }

    public abstract void setTrade(IData trade);
    
    public abstract void setTradeList(IDataset tradeList);
}
