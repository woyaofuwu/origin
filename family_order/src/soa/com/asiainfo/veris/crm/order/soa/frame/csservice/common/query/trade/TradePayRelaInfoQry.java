
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradePayRelaInfoQry extends CSBizBean
{
    /**
     * 根据tradeId查询所有的用户付费关系备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakPayRelaByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_PAYRELATION_BAK", "SEL_ALL_BAK_BY_TRADE", params);
    }

    /**
     * 根据tradeId查询所有的用户付费关系台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradePayRelaByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_PAYRELATION", "SEL_BY_TRADE", params,Route.getJourDb());
    }

    /**
     * 更新付费关系子表时间
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
        param.put("START_CYCLE_ID", start_date);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_PAYRELATION", "UPD_STARTCYCLEID_FSZQ", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
}
