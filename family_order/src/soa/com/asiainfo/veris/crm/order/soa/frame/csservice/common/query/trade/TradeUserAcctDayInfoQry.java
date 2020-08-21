
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeUserAcctDayInfoQry
{
    /**
     * 根据TRADE_ID获取所有用户账期备份台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakUserAcctDayByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_USER_ACCTDAY_BAK", "SEL_ALL_BAK_BY_TRADEID", params);
    }

    /**
     * 根据TRADE_ID获取所有用户账期台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeUserAcctDayInfoByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_USER_ACCTDAY", "SEL_BY_TRADE_ID", params,Route.getJourDb());
    }

    /**
     * 根据TRADE_ID获取用户账期台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeUserAcctDayInfoByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_USER_ACCTDAY", "SEL_BY_FINISH", params,Route.getJourDb());
    }

    /**
     * 根据TRADE_ID,USER_ID获取用户账期台账
     * 
     * @param tradeId
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeUserAcctDayInfoByTradeIdUserId(String tradeId, String userId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("USER_ID", userId);

        return Dao.qryByCodeParser("TF_B_TRADE_USER_ACCTDAY", "SEL_BY_USERID_TRADEID", params,Route.getJourDb());
    }

    /**
     * 获取用户主动账期变更台账信息
     * 
     * @param userId
     * @param chgType
     * @return
     * @throws Exception
     */
    public static IDataset getTradeUserAcctDayInfoByUserId(String userId, String chgType) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("CHG_TYPE", chgType);

        return Dao.qryByCodeParser("TF_B_TRADE_USER_ACCTDAY", "SEL_BY_USERID_CHGTYPE", params);
    }

    public static void updateStartDate(String tradeId, String startDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("START_DATE", startDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_USER_ACCTDAY", "UPD_STARTDATE_FSZQ", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

}
