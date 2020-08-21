
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeRentInfoQry
{
    /**
     * 根据tradeId查询所有的租机备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakRentByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_RENT_BAK", "SEL_ALL_BAK_BY_TRADE", params);
    }

    /**
     * 根据tradeId查询所有的租机信息台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeRentInfoByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_RENT", "SEL_BY_TRADE_RENT", params,Route.getJourDb());
    }
}
