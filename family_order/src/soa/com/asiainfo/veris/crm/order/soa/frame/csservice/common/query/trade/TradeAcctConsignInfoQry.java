
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeAcctConsignInfoQry
{
    /**
     * 根据tradeId查询所有的账户银行信息备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakAcctConsignByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_CONSIGN_BAK", "SEL_ALL_BAK_BY_TRADE", params);
    }

    /**
     * 根据tradeId查询所有的账户银行信息台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAcctConsignInfoByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_ACCT_CONSIGN", "SEL_BY_TRADE", params, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    public static void updateStartDate(String tradeId, String startDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("START_CYCLE_ID", startDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_ACCT_CONSIGN", "UPD_STARTCYCLEID", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
}
