
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeImpuInfoQry
{

    /*
     * @description 根据用户编号查询IMPU的台账信息
     * @author xunyl
     * @date 2013-10-14
     */
    public static IDataset qryTradeImpuByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_B_TRADE_IMPU", "SEL_BY_USER_TYPE", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 查询台账IMPU信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeImpuInfo(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        return Dao.qryByCode("TF_B_TRADE_IMPU", "SEL_BY_TRADEID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    /**
     * 查询备份IMPU信息
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeImpuBakByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        return Dao.qryByCode("TF_B_TRADE_IMPU_BAK", "SEL_ALL_BAK_BY_TRADE", param);
    }
}
