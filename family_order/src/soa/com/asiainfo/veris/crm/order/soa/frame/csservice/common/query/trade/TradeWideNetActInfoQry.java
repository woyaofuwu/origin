
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeWideNetActInfoQry
{

    /**
     * 更新用户宽带资料表时间
     * 
     * @author chenzm
     * @param trade_id
     * @param start_date
     * @throws Exception
     */
    public static void updateStartDate(String trade_id, String start_date) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("START_DATE", start_date);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_WIDENET_ACT", "UPD_STARTDATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    public static void updateProductEndDate(String trade_id, String finishDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("END_DATE", finishDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_PRODUCT", "UPD_WIDENET_END_DATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    public static void updateAttrEndDate(String trade_id, String finishDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("END_DATE", finishDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_ATTR", "UPD_WIDENET_END_DATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    
    public static void updateDiscntEndDate(String trade_id, String finishDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("END_DATE", finishDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_DISCNT", "UPDATE_ENDDATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    
    public static void updateResEndDate(String trade_id, String finishDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("END_DATE", finishDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_RES", "UPD_WIDENET_END_DATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    
    public static void updateSvcEndDate(String trade_id, String finishDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("END_DATE", finishDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_SVC", "UPD_WIDENET_END_DATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    public static void updateWidenetEndDate(String trade_id, String finishDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("END_DATE", finishDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_WIDENET", "UPD_WIDENET_END_DATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    public static void updateWidnetActEndDate(String trade_id, String finishDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("END_DATE", finishDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_WIDENET_ACT", "UPD_WIDENET_END_DATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
   
    public static void updatePayRelaEndDate(String trade_id, String finishDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("END_CYCLE_ID", finishDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_PAYRELATION", "UPD_WIDENET_END_DATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    public static void updateUserAcctDayEndDate(String tradeId, String finishDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("END_DATE", finishDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_USER_ACCTDAY", "UPD_WIDENET_END_DATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    /**
     * 根据ACCID查询宽带账号台账信息
     *
     * @param tradeId
     * @throws Exception
     * @author wuwangfeng
     */
    public static IDataset qeyWideNetActInfoBTradeByAcctId(String acctId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        return Dao.qryByCode("TF_B_TRADE_WIDENET_ACT", "SEL_BY_ACCT_ID_BTRADE", param, Route.getJourDbDefault());
    }
    
    
    
}
