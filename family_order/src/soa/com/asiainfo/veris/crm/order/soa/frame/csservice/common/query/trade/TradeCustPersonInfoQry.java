
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class TradeCustPersonInfoQry
{
    /**
     * 根据tradeId查询所有的账户信息备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakCustPersonByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_PERSON_BAK", "SEL_ALL_BAK_BY_TRADE", params);
    }

    // todo
    /**
     * 获取客户台账子表
     * 
     * @param iData
     * @return
     */
    public static IDataset getTradeCustPerson(String tradeId, Pagination pagination) throws Exception
    {

        if (tradeId == null || "".equals(tradeId))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_84);
        }
        IData params = new DataMap();
        params.put("VTRADE_ID", tradeId);
        try
        {
            IDataset iDataset = Dao.qryByCodeParser("TF_B_TRADE_CUST_PERSON", "SEL_TRADE_CUST_PERSON", params, pagination);
            return iDataset;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            CSAppException.apperr(TradeException.CRM_TRADE_94);
            return null;
        }
    }

    /**
     * 根据tradeId查询所有的账户信息台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeCustPersonByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_CUST_PERSON", "SEL_TRADE_CUST_PERSON", params, Route.getJourDb());
    }
}
