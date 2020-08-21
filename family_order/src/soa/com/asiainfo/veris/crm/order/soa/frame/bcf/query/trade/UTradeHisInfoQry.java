
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class UTradeHisInfoQry
{

    /**
     * 查询历史台账信息
     * 
     * @param tradeId
     * @param cancelTag
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryTradeHisByPk(String tradeId, String cancelTag, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("CANCEL_TAG", cancelTag);

        StringBuilder sql = new StringBuilder(2000);

        sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID, A.ACCEPT_MONTH, ");
        sql.append("TO_CHAR(A.BATCH_ID) BATCH_ID, TO_CHAR(A.ORDER_ID) ORDER_ID, ");
        sql.append("TO_CHAR(A.PROD_ORDER_ID) PROD_ORDER_ID, A.BPM_ID, ");
        sql.append("TO_CHAR(A.CAMPN_ID) CAMPN_ID, A.TRADE_TYPE_CODE, A.PRIORITY, ");
        sql.append("A.SUBSCRIBE_TYPE, A.SUBSCRIBE_STATE, A.NEXT_DEAL_TAG, A.IN_MODE_CODE, ");
        sql.append("TO_CHAR(A.CUST_ID) CUST_ID, A.CUST_NAME, TO_CHAR(A.USER_ID) USER_ID, ");
        sql.append("TO_CHAR(A.ACCT_ID) ACCT_ID, A.SERIAL_NUMBER, A.NET_TYPE_CODE, ");
        sql.append("A.EPARCHY_CODE, A.CITY_CODE, A.PRODUCT_ID, A.BRAND_CODE, ");
        sql.append("TO_CHAR(A.CUST_ID_B) CUST_ID_B, TO_CHAR(A.USER_ID_B) USER_ID_B, ");
        sql.append("TO_CHAR(A.ACCT_ID_B) ACCT_ID_B, A.SERIAL_NUMBER_B, A.CUST_CONTACT_ID, ");
        sql.append("A.SERV_REQ_ID, A.INTF_ID, ");
        sql.append("TO_CHAR(A.ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE, ");
        sql.append("A.TRADE_STAFF_ID, A.TRADE_DEPART_ID, A.TRADE_CITY_CODE, ");
        sql.append("A.TRADE_EPARCHY_CODE, A.TERM_IP, TO_CHAR(A.OPER_FEE) OPER_FEE, ");
        sql.append("TO_CHAR(A.FOREGIFT) FOREGIFT, TO_CHAR(A.ADVANCE_PAY) ADVANCE_PAY, ");
        sql.append("A.INVOICE_NO, A.FEE_STATE, ");
        sql.append("TO_CHAR(A.FEE_TIME, 'YYYY-MM-DD HH24:MI:SS') FEE_TIME, A.FEE_STAFF_ID, ");
        sql.append("A.PROCESS_TAG_SET, A.OLCOM_TAG, ");
        sql.append("TO_CHAR(A.FINISH_DATE, 'YYYY-MM-DD HH24:MI:SS') FINISH_DATE, ");
        sql.append("TO_CHAR(A.EXEC_TIME, 'YYYY-MM-DD HH24:MI:SS') EXEC_TIME, A.EXEC_ACTION, ");
        sql.append("A.EXEC_RESULT, A.EXEC_DESC, A.CANCEL_TAG, ");
        sql.append("TO_CHAR(A.CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS') CANCEL_DATE, ");
        sql.append("A.CANCEL_STAFF_ID, A.CANCEL_DEPART_ID, A.CANCEL_CITY_CODE, ");
        sql.append("A.CANCEL_EPARCHY_CODE, ");
        sql.append("TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        sql.append("A.UPDATE_STAFF_ID, A.UPDATE_DEPART_ID, A.REMARK, A.RSRV_STR1, A.RSRV_STR2, ");
        sql.append("A.RSRV_STR3, A.RSRV_STR4, A.RSRV_STR5, A.RSRV_STR6, A.RSRV_STR7, ");
        sql.append("A.RSRV_STR8, A.RSRV_STR9, A.RSRV_STR10, PF_WAIT ");
        if(ProvinceUtil.isProvince(ProvinceUtil.SHXI))
        {
        	sql.append(", TO_CHAR(A.BOOK_DATE, 'YYYY-MM-DD HH24:MI:SS') BOOK_DATE ");
        }
        sql.append("FROM TF_BH_TRADE A ");
        sql.append("WHERE A.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        sql.append("AND A.CANCEL_TAG = :CANCEL_TAG ");
       // IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(BizBean.getVisit().getStaffEparchyCode()));
        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));//modify  by  duhj  2017/5/15 接口调试修正

        if (IDataUtil.isEmpty(ids))
        {
            return new DataMap();
        }

        return ids.getData(0);
    }
    
    public static IData qryTradeHisByOID(String tradeId, String cancelTag, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", tradeId);
        param.put("CANCEL_TAG", cancelTag);

        StringBuilder sql = new StringBuilder(2000);

        sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID, A.ACCEPT_MONTH, ");
        sql.append("TO_CHAR(A.BATCH_ID) BATCH_ID, TO_CHAR(A.ORDER_ID) ORDER_ID, ");
        sql.append("TO_CHAR(A.PROD_ORDER_ID) PROD_ORDER_ID, A.BPM_ID, ");
        sql.append("TO_CHAR(A.CAMPN_ID) CAMPN_ID, A.TRADE_TYPE_CODE, A.PRIORITY, ");
        sql.append("A.SUBSCRIBE_TYPE, A.SUBSCRIBE_STATE, A.NEXT_DEAL_TAG, A.IN_MODE_CODE, ");
        sql.append("TO_CHAR(A.CUST_ID) CUST_ID, A.CUST_NAME, TO_CHAR(A.USER_ID) USER_ID, ");
        sql.append("TO_CHAR(A.ACCT_ID) ACCT_ID, A.SERIAL_NUMBER, A.NET_TYPE_CODE, ");
        sql.append("A.EPARCHY_CODE, A.CITY_CODE, A.PRODUCT_ID, A.BRAND_CODE, ");
        sql.append("TO_CHAR(A.CUST_ID_B) CUST_ID_B, TO_CHAR(A.USER_ID_B) USER_ID_B, ");
        sql.append("TO_CHAR(A.ACCT_ID_B) ACCT_ID_B, A.SERIAL_NUMBER_B, A.CUST_CONTACT_ID, ");
        sql.append("A.SERV_REQ_ID, A.INTF_ID, ");
        sql.append("TO_CHAR(A.ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE, ");
        sql.append("A.TRADE_STAFF_ID, A.TRADE_DEPART_ID, A.TRADE_CITY_CODE, ");
        sql.append("A.TRADE_EPARCHY_CODE, A.TERM_IP, TO_CHAR(A.OPER_FEE) OPER_FEE, ");
        sql.append("TO_CHAR(A.FOREGIFT) FOREGIFT, TO_CHAR(A.ADVANCE_PAY) ADVANCE_PAY, ");
        sql.append("A.INVOICE_NO, A.FEE_STATE, ");
        sql.append("TO_CHAR(A.FEE_TIME, 'YYYY-MM-DD HH24:MI:SS') FEE_TIME, A.FEE_STAFF_ID, ");
        sql.append("A.PROCESS_TAG_SET, A.OLCOM_TAG, ");
        sql.append("TO_CHAR(A.FINISH_DATE, 'YYYY-MM-DD HH24:MI:SS') FINISH_DATE, ");
        sql.append("TO_CHAR(A.EXEC_TIME, 'YYYY-MM-DD HH24:MI:SS') EXEC_TIME, A.EXEC_ACTION, ");
        sql.append("A.EXEC_RESULT, A.EXEC_DESC, A.CANCEL_TAG, ");
        sql.append("TO_CHAR(A.CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS') CANCEL_DATE, ");
        sql.append("A.CANCEL_STAFF_ID, A.CANCEL_DEPART_ID, A.CANCEL_CITY_CODE, ");
        sql.append("A.CANCEL_EPARCHY_CODE, ");
        sql.append("TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        sql.append("A.UPDATE_STAFF_ID, A.UPDATE_DEPART_ID, A.REMARK, A.RSRV_STR1, A.RSRV_STR2, ");
        sql.append("A.RSRV_STR3, A.RSRV_STR4, A.RSRV_STR5, A.RSRV_STR6, A.RSRV_STR7, ");
        sql.append("A.RSRV_STR8, A.RSRV_STR9, A.RSRV_STR10, PF_WAIT ");
        if(ProvinceUtil.isProvince(ProvinceUtil.SHXI))
        {
        	sql.append(", TO_CHAR(A.BOOK_DATE, 'YYYY-MM-DD HH24:MI:SS') BOOK_DATE ");
        }
        sql.append(" from tf_bh_trade A where A.order_id = TO_NUMBER(:ORDER_ID) ");
        sql.append(" and A.cancel_tag =:CANCEL_TAG and A.trade_type_code='237' ");
        sql.append(" and exists (select 1 from tf_bh_trade r where r.order_id=A.order_id and r.trade_type_code='240') ");
        sql.append(" and not exists (select 1 from tf_bh_trade d where d.order_id=A.order_id and d.trade_type_code='601') ");
        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));

        if (IDataUtil.isEmpty(ids))
        {
            return new DataMap();
        }

        return ids.getData(0);
    }
}
