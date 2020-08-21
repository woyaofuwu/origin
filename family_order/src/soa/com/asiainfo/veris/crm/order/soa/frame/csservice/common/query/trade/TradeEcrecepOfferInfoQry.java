package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeEcrecepOfferInfoQry {

    //集客大厅
    public static IDataset qryJKDTMerchOnlineInfoByMerchOfferId(String merchOfferId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("MERCH_OFFER_ID", merchOfferId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select a.trade_id, ");
        parser.addSQL("a.merch_order_id, ");
        parser.addSQL("a.merch_offer_id, ");
        parser.addSQL("a.user_id, ");
        parser.addSQL("b.order_id, ");
        parser.addSQL("b.cust_id, ");
        parser.addSQL("b.rsrv_str10, ");
        parser.addSQL("b.product_id ");
        parser.addSQL("from TF_B_TRADE_ECRECEP_OFFER a, tf_b_trade b ");
        parser.addSQL("where a.trade_id = b.trade_id ");
        parser.addSQL("and a.ACCEPT_MONTH = b.accept_month ");
        parser.addSQL("and MERCH_OFFER_ID = :MERCH_OFFER_ID ");
        parser.addSQL("and rownum = 1 ");

        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    public static IDataset qryJKDTMerchInfoByMerchOfferId(String merchOfferId) throws Exception
    {
        IData param = new DataMap();
        param.put("MERCH_OFFER_ID", merchOfferId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* ");
        parser.addSQL(" FROM TF_B_TRADE_ECRECEP_OFFER T ");
        parser.addSQL(" WHERE T.MERCH_OFFER_ID = :MERCH_OFFER_ID ");
        parser.addSQL(" AND SYSDATE > T.START_DATE ");
        parser.addSQL(" AND SYSDATE < T.END_DATE ");

        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset updateJKDTMerchInfoByMerchOfferId(String merch_order_id,String time_stamp,String tradeid) throws Exception
    {
        IData param = new DataMap();
        param.put("MERCH_ORDER_ID", merch_order_id);
        param.put("TIME_STAMP", time_stamp);
        param.put("TRADEID", tradeid);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" UPDATE TF_B_TRADE_ECRECEP_OFFER T SET T.MERCH_ORDER_ID=:MERCH_ORDER_ID,");
        parser.addSQL(" T.RSRV_DATE1=to_date(:TIME_STAMP,'YYYY-MM-DD HH24:MI:SS') ");
        parser.addSQL("WHERE T.TRADE_ID=TO_NUMBER(:TRADEID) ");


        return Dao.qryByParse(parser,Route.getJourDb(Route.CONN_CRM_CG));
    }


    /**
     * @Description:根据商品订单号找台帐编码
     * @author hud
     * @date
     * @param merchOfferId
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getMerchTradeInfoByOfferId(String merchOfferId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("MERCH_OFFER_ID", merchOfferId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select a.trade_id,b.order_id,b.cust_id,b.rsrv_str10,b.product_id");
        parser.addSQL(" from tf_b_trade_grp_merch a,tf_b_trade b ");
        parser.addSQL(" where a.trade_id = b.trade_id");
        parser.addSQL(" and a.ACCEPT_MONTH = b.accept_month");
        parser.addSQL(" and MERCH_OFFER_ID = :MERCH_OFFER_ID ");
        parser.addSQL(" and rownum = 1");
        return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));
    }

}
