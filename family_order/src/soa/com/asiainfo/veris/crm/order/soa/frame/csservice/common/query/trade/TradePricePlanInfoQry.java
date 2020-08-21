
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;


public class TradePricePlanInfoQry
{
    /**
     * 更新定价计划开始时间
     * 
     * @author yuyj3
     * @param tradeId
     * @param offerType
     * @param startDate
     * @throws Exception
     */
    public static void updateStartDate(String tradeId, String offerType, String startDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("OFFER_TYPE", offerType);
        param.put("START_DATE", startDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_PRICE_PLAN", "UPD_TRADE_PRICE_PLAN_STARTDATE", param, Route.getJourDb());
    }
    
    
    /**
     * 更新宽带保底优惠定价计划开始时间
     * 
     * @author yuyj3
     * @param tradeId
     * @param offerType
     * @param startDate
     * @throws Exception
     */
    public static void updateStartDate2(String tradeId, String offerType, String startDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("OFFER_TYPE", offerType);
        param.put("START_DATE", startDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_PRICE_PLAN", "UPD_TRADE_PRICE_PLAN_STARTDATE2", param, Route.getJourDb());
    }
    
    
    /**
     * 更新定价计划开始时间、结束时间
     * @param tradeId
     * @param offerInsId
     * @param startDate
     * @param endDate
     * @throws Exception
     * @author yuyj3
     */
    public static void updateStartAndEndDate(String tradeId, String offerInsId, String startDate, String endDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("OFFER_INS_ID", offerInsId);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_PRICE_PLAN", "UPD_TRADE_PRICE_PLAN_STARTENDDATE", param, Route.getJourDb());
    }
    
    
    public static IDataset getPricePlanByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_PRICE_PLAN", "SEL_BY_TRADEID", param, Route.getJourDb());
    }
    
    public static IDataset getPricePlanBakByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_PRICE_PLAN_BAK", "SEL_BY_TRADEID", param);
    }
    public static IDataset getPricePlanByPriceId(String userId,String priceId,String pricePlanId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRICE_ID", priceId);
        param.put("USER_ID", userId);
        param.put("PRICE_PLAN_ID", pricePlanId);
        return Dao.qryByCodeParser("TF_B_TRADE_PRICE_PLAN", "SEL_BY_PRICEID", param, Route.getJourDb());
    }

}
