package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeEcrecepProductInfoQry {
    //集客大厅
    public static IDataset getJKDTMerchpOnlineByProductofferId(String productOfferId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_OFFER_ID", productOfferId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select a.trade_id, ");
        parser.addSQL("a.user_id, ");
        parser.addSQL("a.merch_spec_code, ");//新增资费处理需要用到--add by huangzl3
        parser.addSQL("a.product_order_id, ");
        parser.addSQL("a.product_offer_id, ");
        parser.addSQL("b.order_id, ");
        parser.addSQL("b.cust_id, ");
        parser.addSQL("b.rsrv_str10, ");
        parser.addSQL("b.product_id, ");
        parser.addSQL("a.inst_id, ");
        parser.addSQL("a.PRODUCT_SPEC_CODE, ");
        parser.addSQL("a.GROUP_ID ");
        parser.addSQL("from TF_B_TRADE_ECRECEP_PRODUCT a, tf_b_trade b ");
        parser.addSQL("where 1 = 1 ");
        parser.addSQL("and a.trade_id = b.trade_id ");
        parser.addSQL("and a.ACCEPT_MONTH = b.accept_month ");
        parser.addSQL("and PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
        parser.addSQL("and rownum = 1 ");

        return Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset qryJKDTMerchpInfoByProductOfferId(String productOfferId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_OFFER_ID", productOfferId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* ");
        parser.addSQL(" FROM TF_B_TRADE_ECRECEP_PRODUCT T ");
        parser.addSQL(" WHERE T.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
        parser.addSQL(" AND SYSDATE > T.START_DATE ");
        parser.addSQL(" AND SYSDATE < T.END_DATE ");

        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    public static IDataset qryJKDTMerchpInfoByTradeId(String trade_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("select INST_ID, ");
        sql.append("to_char(TRADE_ID) TRADE_ID, ");
        sql.append("ACCEPT_MONTH, ");
        sql.append("to_char(USER_ID) USER_ID, ");
        sql.append("MERCH_SPEC_CODE, ");
        sql.append("PRODUCT_SPEC_CODE, ");
        sql.append("PRODUCT_ORDER_ID, ");
        sql.append("PRODUCT_OFFER_ID, ");
        sql.append("GROUP_ID, ");
        sql.append("SERV_CODE, ");
        sql.append("BIZ_ATTR, ");
        sql.append("to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("MODIFY_TAG, ");
        sql.append("to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, ");
        sql.append("REMARK, ");
        sql.append("RSRV_NUM1, ");
        sql.append("RSRV_NUM2, ");
        sql.append("RSRV_NUM3, ");
        sql.append("to_char(RSRV_NUM4) RSRV_NUM4, ");
        sql.append("to_char(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("RSRV_STR1, ");
        sql.append("RSRV_STR2, ");
        sql.append("RSRV_STR3, ");
        sql.append("RSRV_STR4, ");
        sql.append("RSRV_STR5, ");
        sql.append("to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("RSRV_TAG1, ");
        sql.append("RSRV_TAG2, ");
        sql.append("RSRV_TAG3 ");
        sql.append("FROM TF_B_TRADE_ECRECEP_PRODUCT ");
        sql.append("WHERE trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");

        return Dao.qryBySql(sql, inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }


}
