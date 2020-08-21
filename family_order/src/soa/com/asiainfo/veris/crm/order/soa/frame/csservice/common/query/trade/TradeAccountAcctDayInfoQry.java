
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeAccountAcctDayInfoQry
{
    /**
     * 根据TRADE_ID获取所有用户账期备份台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakAccountAcctDayByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_ACCOUNT_ACCTDAY_BAK", "SEL_ALL_BAK_BY_TRADEID", params);
    }

    /**
     * 根据TRADE_ID获取账户账期台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAccountAcctDayInfoByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_ACCOUNT_ACCTDAY", "SEL_BY_FINISH", params, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    /**
     * 根据TRADE_ID获取账户账期所有台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAllAccountAcctDayInfoByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_ACCOUNT_ACCTDAY", "SEL_BY_TRADE_ID", params, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    public static void updateStartDate(String tradeId, String startDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("START_DATE", startDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_ACCOUNT_ACCTDAY", "UPD_STARTDATE_FSZQ", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
}
