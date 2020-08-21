
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.order;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class UOrderSubInfoQry
{
    /**
     * 根据ORDER_ID查询客户订单关系表
     * 
     * @param orderId
     * @return
     * @throws Exception
     */
    public static IDataset qryOrderSubByOrderId(String orderId) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(orderId));

        StringBuilder sql = new StringBuilder(100);
        sql.append("SELECT ORDER_ID, TRADE_ID, ROUTE_ID, EPARCHY_CODE, MAIN_TAG, ACCEPT_MONTH ");
        sql.append("FROM TF_B_ORDER_SUB T ");
        sql.append("WHERE T.ORDER_ID = :ORDER_ID ");
        sql.append("AND T.ACCEPT_MONTH = :ACCEPT_MONTH ");

        return Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据TRADE_ID查询客户订单关系表
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset qryOrderSubByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));

        StringBuilder sql = new StringBuilder(100);
        sql.append("SELECT ORDER_ID, TRADE_ID, ROUTE_ID, EPARCHY_CODE, ACCEPT_MONTH ");
        sql.append("FROM TF_B_ORDER_SUB T ");
        sql.append("WHERE T.TRADE_ID = :TRADE_ID ");
        sql.append("AND T.ACCEPT_MONTH = :ACCEPT_MONTH ");

        return Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }
}
