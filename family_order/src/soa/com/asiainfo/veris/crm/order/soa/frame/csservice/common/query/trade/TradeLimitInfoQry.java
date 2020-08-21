
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeLimitInfoQry
{
    public static IDataset getLimitByTradeId(String limitTradeId, String limitType) throws Exception
    {
        String acceptMonth = StrUtil.getAcceptMonthById(limitTradeId);

        IData param = new DataMap();
        param.put("LIMIT_TRADE_ID", limitTradeId);
        param.put("ACCEPT_MONTH", acceptMonth);
        param.put("LIMIT_TYPE", limitType);

        StringBuilder sql = new StringBuilder(500);

        sql.append("SELECT TRADE_ID,LIMIT_TRADE_ID,LIMIT_TYPE,ROUTE_ID ");
        sql.append("FROM TF_B_TRADE_LIMIT ");
        sql.append("WHERE LIMIT_TRADE_ID = TO_NUMBER(:LIMIT_TRADE_ID) ");
        sql.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");
        sql.append("AND LIMIT_TYPE = :LIMIT_TYPE ");
        sql.append("AND STATE = '0' ");

        IDataset out = Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
        return out;
    }

    /**
     * @Description: 订单完工依赖查询
     * @param tradeId
     * @param limitType
     *            0 完全依赖（当trade_id 和limit_trade_id 为1对1关系，当limit_trade_id完工之后才能完工trade_id） 1 部分依赖，一条trade_id
     *            对应多条limit_trade_id，当limit_trade_id任意完工，则依赖关系失效，trade_id可执行完工
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jul 21, 2014 9:10:58 PM
     */
    public static IDataset getLimitedByTradeId(String tradeId) throws Exception
    {
        String acceptMonth = StrUtil.getAcceptMonthById(tradeId);

        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("ACCEPT_MONTH", acceptMonth);

        StringBuilder sql = new StringBuilder(500);

        sql.append("SELECT TRADE_ID,LIMIT_TRADE_ID,LIMIT_TYPE,ROUTE_ID ");
        sql.append("FROM TF_B_TRADE_LIMIT ");
        sql.append("WHERE TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");
        sql.append("AND LIMIT_TYPE IN ('0','1') ");
        sql.append("AND STATE = '0' ");

        IDataset out = Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
        return out;
    }
    
    public static IDataset getLimitTradeByTradeId(String tradeId) throws Exception {
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
		IData param = new DataMap();
		param.put("LIMIT_TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		return Dao.qryByCode("TF_B_TRADE_LIMIT", "SEL_TRADE_ID_BY_LIMITED", param, Route.CONN_CRM_CEN);
	}
    
}
