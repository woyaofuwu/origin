
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradePostInfoQry
{

    /**
     * 根据tradeId查询所有的邮寄信息备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakPostInfoByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_POSTINFO_BAK", "SEL_ALL_BAK_BY_TRADE", params);
    }

    /**
     * 根据tradeId查询所有的邮寄信息台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradePostInfoByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_POST", "SEL_BY_TRADE", params,Route.getJourDb());
    }

    public static IDataset queryPosErrorLog(String serialNumber, String tradePosId, String refNo, String startDate, String endDate, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_POS_ID", tradePosId);
        param.put("REF_NO", refNo);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        return Dao.qryByCodeParser("TF_B_TRADE_POS", "SEL_BY_TRADEPOSID_FOR_CANCEL", param, page);
    }
}
