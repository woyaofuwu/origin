
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UTradeInfoQry
{

    /**
     * 根据ORDER_ID查询台账信息,带路由
     * 
     * @param orderId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeByOrderId(String orderId, String routeId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("ORDER_ID", orderId);

        StringBuilder sql = new StringBuilder(2500);

        sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID, A.ACCEPT_MONTH, ");
        sql.append("TO_CHAR(A.BATCH_ID) BATCH_ID, ");
        sql.append("TO_CHAR(A.ORDER_ID) ORDER_ID, ");
        sql.append("TO_CHAR(A.PROD_ORDER_ID) PROD_ORDER_ID, A.BPM_ID, ");
        sql.append("TO_CHAR(A.CAMPN_ID) CAMPN_ID, A.TRADE_TYPE_CODE, ");
        sql.append("A.PRIORITY, A.SUBSCRIBE_TYPE, A.SUBSCRIBE_STATE, ");
        sql.append("A.NEXT_DEAL_TAG, A.IN_MODE_CODE, ");
        sql.append("TO_CHAR(A.CUST_ID) CUST_ID, A.CUST_NAME, ");
        sql.append("TO_CHAR(A.USER_ID) USER_ID, ");
        sql.append("TO_CHAR(A.ACCT_ID) ACCT_ID, A.SERIAL_NUMBER, ");
        sql.append("A.NET_TYPE_CODE, A.EPARCHY_CODE, A.CITY_CODE, ");
        sql.append("A.PRODUCT_ID, A.BRAND_CODE, ");
        sql.append("TO_CHAR(A.CUST_ID_B) CUST_ID_B, ");
        sql.append("TO_CHAR(A.USER_ID_B) USER_ID_B, ");
        sql.append("TO_CHAR(A.ACCT_ID_B) ACCT_ID_B, A.SERIAL_NUMBER_B, ");
        sql.append("A.CUST_CONTACT_ID, A.SERV_REQ_ID, A.INTF_ID, ");
        sql.append("TO_CHAR(A.ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE, ");
        sql.append("A.TRADE_STAFF_ID, A.TRADE_DEPART_ID, ");
        sql.append("A.TRADE_CITY_CODE, A.TRADE_EPARCHY_CODE, A.TERM_IP, ");
        sql.append("TO_CHAR(A.OPER_FEE) OPER_FEE, ");
        sql.append("TO_CHAR(A.FOREGIFT) FOREGIFT, ");
        sql.append("TO_CHAR(A.ADVANCE_PAY) ADVANCE_PAY, A.INVOICE_NO, ");
        sql.append("A.FEE_STATE, ");
        sql.append("TO_CHAR(A.FEE_TIME, 'YYYY-MM-DD HH24:MI:SS') FEE_TIME, ");
        sql.append("A.FEE_STAFF_ID, A.PROCESS_TAG_SET, A.OLCOM_TAG, ");
        sql.append("TO_CHAR(A.FINISH_DATE, 'YYYY-MM-DD HH24:MI:SS') FINISH_DATE, ");
        sql.append("TO_CHAR(A.EXEC_TIME, 'YYYY-MM-DD HH24:MI:SS') EXEC_TIME, ");
        sql.append("A.EXEC_ACTION, A.EXEC_RESULT, A.EXEC_DESC, ");
        sql.append("A.CANCEL_TAG, ");
        sql.append("TO_CHAR(A.CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS') CANCEL_DATE, ");
        sql.append("A.CANCEL_STAFF_ID, A.CANCEL_DEPART_ID, ");
        sql.append("A.CANCEL_CITY_CODE, A.CANCEL_EPARCHY_CODE, ");
        sql.append("TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        sql.append("A.UPDATE_STAFF_ID, A.UPDATE_DEPART_ID, A.REMARK, ");
        sql.append("A.RSRV_STR1, A.RSRV_STR2, A.RSRV_STR3, A.RSRV_STR4, ");
        sql.append("A.RSRV_STR5, A.RSRV_STR6, A.RSRV_STR7, A.RSRV_STR8, ");
        sql.append("A.RSRV_STR9, A.RSRV_STR10, PF_WAIT ");
        if(ProvinceUtil.isProvince(ProvinceUtil.SHXI))
        {
        	sql.append(", TO_CHAR(A.BOOK_DATE, 'YYYY-MM-DD HH24:MI:SS') BOOK_DATE ");
        }
        sql.append("FROM TF_B_TRADE A ");
        sql.append("WHERE A.ORDER_ID = TO_NUMBER(:ORDER_ID) ");

        return Dao.qryBySql(sql, inparams, Route.getJourDb(routeId));
    }

    /**
     * 根据ORDER_ID查询台账信息,带路由
     * 
     * @param orderId
     * @param cancelTag
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeByOrderId(String orderId, String cancelTag, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        param.put("CANCEL_TAG", cancelTag);
        param.put("ROUTE_ID", routeId); // 给批量发报文用
        param.put("DB_SRC", routeId); // 传服务开通需要路由信息

        StringBuilder sql = new StringBuilder(2500);

        sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID, A.ACCEPT_MONTH, ");
        sql.append("TO_CHAR(A.BATCH_ID) BATCH_ID, ");
        sql.append("TO_CHAR(A.ORDER_ID) ORDER_ID, ");
        sql.append("TO_CHAR(A.PROD_ORDER_ID) PROD_ORDER_ID, A.BPM_ID, ");
        sql.append("TO_CHAR(A.CAMPN_ID) CAMPN_ID, A.TRADE_TYPE_CODE, ");
        sql.append("A.PRIORITY, A.SUBSCRIBE_TYPE, A.SUBSCRIBE_STATE, ");
        sql.append("A.NEXT_DEAL_TAG, A.IN_MODE_CODE, ");
        sql.append("TO_CHAR(A.CUST_ID) CUST_ID, A.CUST_NAME, ");
        sql.append("TO_CHAR(A.USER_ID) USER_ID, ");
        sql.append("TO_CHAR(A.ACCT_ID) ACCT_ID, A.SERIAL_NUMBER, ");
        sql.append("A.NET_TYPE_CODE, A.EPARCHY_CODE, A.CITY_CODE, ");
        sql.append("A.PRODUCT_ID, A.BRAND_CODE, ");
        sql.append("TO_CHAR(A.CUST_ID_B) CUST_ID_B, ");
        sql.append("TO_CHAR(A.USER_ID_B) USER_ID_B, ");
        sql.append("TO_CHAR(A.ACCT_ID_B) ACCT_ID_B, A.SERIAL_NUMBER_B, ");
        sql.append("A.CUST_CONTACT_ID, A.SERV_REQ_ID, A.INTF_ID, ");
        sql.append("TO_CHAR(A.ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE, ");
        sql.append("A.TRADE_STAFF_ID, A.TRADE_DEPART_ID, ");
        sql.append("A.TRADE_CITY_CODE, A.TRADE_EPARCHY_CODE, A.TERM_IP, ");
        sql.append("TO_CHAR(A.OPER_FEE) OPER_FEE, ");
        sql.append("TO_CHAR(A.FOREGIFT) FOREGIFT, ");
        sql.append("TO_CHAR(A.ADVANCE_PAY) ADVANCE_PAY, A.INVOICE_NO, ");
        sql.append("A.FEE_STATE, ");
        sql.append("TO_CHAR(A.FEE_TIME, 'YYYY-MM-DD HH24:MI:SS') FEE_TIME, ");
        sql.append("A.FEE_STAFF_ID, A.PROCESS_TAG_SET, A.OLCOM_TAG, ");
        sql.append("TO_CHAR(A.FINISH_DATE, 'YYYY-MM-DD HH24:MI:SS') FINISH_DATE, ");
        sql.append("TO_CHAR(A.EXEC_TIME, 'YYYY-MM-DD HH24:MI:SS') EXEC_TIME, ");
        sql.append("A.EXEC_ACTION, A.EXEC_RESULT, A.EXEC_DESC, ");
        sql.append("A.CANCEL_TAG, ");
        sql.append("TO_CHAR(A.CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS') CANCEL_DATE, ");
        sql.append("A.CANCEL_STAFF_ID, A.CANCEL_DEPART_ID, ");
        sql.append("A.CANCEL_CITY_CODE, A.CANCEL_EPARCHY_CODE, ");
        sql.append("TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        sql.append("A.UPDATE_STAFF_ID, A.UPDATE_DEPART_ID, A.REMARK, ");
        sql.append("A.RSRV_STR1, A.RSRV_STR2, A.RSRV_STR3, A.RSRV_STR4, ");
        sql.append("A.RSRV_STR5, A.RSRV_STR6, A.RSRV_STR7, A.RSRV_STR8, ");
        sql.append("A.RSRV_STR9, A.RSRV_STR10 ,PF_WAIT,OLCOM_TAG, :DB_SRC DB_SRC, :ROUTE_ID ROUTE_ID ");
        if(ProvinceUtil.isProvince(ProvinceUtil.SHXI))
        {
        	sql.append(", TO_CHAR(A.BOOK_DATE, 'YYYY-MM-DD HH24:MI:SS') BOOK_DATE ");
        }

        // 为了割接前业务服务开通能够返销，返销业务提交时重新组织数据送服务开通,固定传cancle_tag='C';
        if (StringUtils.equals("C", cancelTag))
        {
            sql.append("FROM TF_BH_TRADE A ");
        }
        else
        {
            sql.append("FROM TF_B_TRADE A ");
        }

        sql.append("WHERE A.ORDER_ID = TO_NUMBER(:ORDER_ID) ");
        sql.append("AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:ORDER_ID, 5, 2)) ");

        if (StringUtils.equals("C", cancelTag))
        {
            sql.append("AND A.CANCEL_TAG = '0' ");
        }
        else
        {
            sql.append("AND A.CANCEL_TAG = :CANCEL_TAG ");
        }

        return Dao.qryBySql(sql, param, Route.getJourDb(routeId));
    }

    /**
     * 查询台账信息,不带路由
     * 
     * @param tradeId
     * @param canceltag
     * @return
     * @throws Exception
     */
    public static IData qryTradeByTradeId(String tradeId, String canceltag) throws Exception
    {
        return qryTradeByTradeId(tradeId, canceltag, null);
    }

    /**
     * 查询台账信息,带路由
     * 
     * @param tradeId
     * @param canceltag
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryTradeByTradeId(String tradeId, String canceltag, String routeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("CANCEL_TAG", canceltag); // 返销标志

        StringBuilder sql = new StringBuilder(2500);

        sql.append("SELECT TO_CHAR(TRADE_ID) TRADE_ID, ACCEPT_MONTH, ");
        sql.append("TO_CHAR(BATCH_ID) BATCH_ID, ");
        sql.append("TO_CHAR(ORDER_ID) ORDER_ID, ");
        sql.append("TO_CHAR(PROD_ORDER_ID) PROD_ORDER_ID, ");
        sql.append("TO_CHAR(BPM_ID) BPM_ID, CAMPN_ID, TRADE_TYPE_CODE, ");
        sql.append("PRIORITY, SUBSCRIBE_TYPE, SUBSCRIBE_STATE, ");
        sql.append("NEXT_DEAL_TAG, IN_MODE_CODE, ");
        sql.append("NVL(TO_CHAR(CUST_ID), '0') CUST_ID, CUST_NAME, ");
        sql.append("TO_CHAR(USER_ID) USER_ID, ");
        sql.append("NVL(TO_CHAR(ACCT_ID), '0') ACCT_ID, SERIAL_NUMBER, ");
        sql.append("NET_TYPE_CODE, EPARCHY_CODE, CITY_CODE, PRODUCT_ID, ");
        sql.append("BRAND_CODE, TO_CHAR(CUST_ID_B) CUST_ID_B, ");
        sql.append("TO_CHAR(USER_ID_B) USER_ID_B, ");
        sql.append("TO_CHAR(ACCT_ID_B) ACCT_ID_B, SERIAL_NUMBER_B, ");
        sql.append("CUST_CONTACT_ID, SERV_REQ_ID, INTF_ID, ");
        sql.append("TO_CHAR(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
        sql.append("TRADE_STAFF_ID, TRADE_DEPART_ID, TRADE_CITY_CODE, ");
        sql.append("TRADE_EPARCHY_CODE, TERM_IP, ");
        sql.append("TO_CHAR(OPER_FEE) OPER_FEE, ");
        sql.append("TO_CHAR(FOREGIFT) FOREGIFT, ");
        sql.append("TO_CHAR(ADVANCE_PAY) ADVANCE_PAY, INVOICE_NO, ");
        sql.append("FEE_STATE, ");
        sql.append("TO_CHAR(FEE_TIME, 'yyyy-mm-dd hh24:mi:ss') FEE_TIME, ");
        sql.append("FEE_STAFF_ID, PROCESS_TAG_SET, OLCOM_TAG, ");
        sql.append("TO_CHAR(FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
        sql.append("TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME, ");
        sql.append("EXEC_ACTION, EXEC_RESULT, EXEC_DESC, CANCEL_TAG, ");
        sql.append("TO_CHAR(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE, ");
        sql.append("CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE, ");
        sql.append("CANCEL_EPARCHY_CODE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, ");
        sql.append("RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
        sql.append("RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10 ,PF_WAIT ");
        if(ProvinceUtil.isProvince(ProvinceUtil.SHXI))
        {
        	sql.append(", TO_CHAR(BOOK_DATE, 'YYYY-MM-DD HH24:MI:SS') BOOK_DATE ");
        }
        sql.append("FROM TF_B_TRADE T ");
        sql.append("WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        sql.append("and T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) ");
        sql.append("AND T.CANCEL_TAG = :CANCEL_TAG ");

        if (StringUtils.isNotEmpty(routeId))
        {
            routeId = CSBizBean.getUserEparchyCode();
        }
        
        IDataset ids = Dao.qryBySql(sql, params, Route.getJourDb());

        if (IDataUtil.isEmpty(ids))
        {
            return new DataMap();
        }

        return ids.getData(0);
    }

  
    
    public static IDataset qryTradeByTradeIdMonth_1(String tradeId, String acceptMonth) throws Exception
    {
    	String routeId = BizRoute.getRouteId();
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("ACCEPT_MONTH", acceptMonth);
//        params.put("CANCEL_TAG", canceltag);
        params.put("ROUTE_ID", routeId);// 给批量发报文用
        params.put("DB_SRC", routeId); // 传服务开通时给出路由信息

        StringBuilder sql = new StringBuilder(3000);

        sql.append("SELECT TO_CHAR(TRADE_ID) TRADE_ID, ACCEPT_MONTH, ");
        sql.append("TO_CHAR(BATCH_ID) BATCH_ID, ");
        sql.append("TO_CHAR(ORDER_ID) ORDER_ID, ");
        sql.append("TO_CHAR(PROD_ORDER_ID) PROD_ORDER_ID, ");
        sql.append("TO_CHAR(BPM_ID) BPM_ID, CAMPN_ID, TRADE_TYPE_CODE, ");
        sql.append("PRIORITY, SUBSCRIBE_TYPE, SUBSCRIBE_STATE, ");
        sql.append("NEXT_DEAL_TAG, IN_MODE_CODE, ");
        sql.append("NVL(TO_CHAR(CUST_ID), '0') CUST_ID, CUST_NAME, ");
        sql.append("TO_CHAR(USER_ID) USER_ID, ");
        sql.append("NVL(TO_CHAR(ACCT_ID), '0') ACCT_ID, SERIAL_NUMBER, ");
        sql.append("NET_TYPE_CODE, EPARCHY_CODE, CITY_CODE, PRODUCT_ID, ");
        sql.append("BRAND_CODE, TO_CHAR(CUST_ID_B) CUST_ID_B, ");
        sql.append("TO_CHAR(USER_ID_B) USER_ID_B, ");
        sql.append("TO_CHAR(ACCT_ID_B) ACCT_ID_B, SERIAL_NUMBER_B, ");
        sql.append("CUST_CONTACT_ID, SERV_REQ_ID, INTF_ID, ");
        sql.append("TO_CHAR(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
        sql.append("TRADE_STAFF_ID, TRADE_DEPART_ID, TRADE_CITY_CODE, ");
        sql.append("TRADE_EPARCHY_CODE, TERM_IP, ");
        sql.append("TO_CHAR(OPER_FEE) OPER_FEE, ");
        sql.append("TO_CHAR(FOREGIFT) FOREGIFT, ");
        sql.append("TO_CHAR(ADVANCE_PAY) ADVANCE_PAY, INVOICE_NO, ");
        sql.append("FEE_STATE, ");
        sql.append("TO_CHAR(FEE_TIME, 'yyyy-mm-dd hh24:mi:ss') FEE_TIME, ");
        sql.append("FEE_STAFF_ID, PROCESS_TAG_SET, OLCOM_TAG, ");
        sql.append("TO_CHAR(FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
        sql.append("TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME, ");
        sql.append("EXEC_ACTION, EXEC_RESULT, EXEC_DESC, CANCEL_TAG, ");
        sql.append("TO_CHAR(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE, ");
        sql.append("CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE, ");
        sql.append("CANCEL_EPARCHY_CODE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, ");
        sql.append("RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
        sql.append("RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10 ,PF_WAIT, :DB_SRC DB_SRC, :ROUTE_ID ROUTE_ID ");
        /*if(ProvinceUtil.isProvince(ProvinceUtil.SHXI))
        {
        	sql.append(", TO_CHAR(BOOK_DATE, 'YYYY-MM-DD HH24:MI:SS') BOOK_DATE ");
        }*/

        // 为了割接前业务服务开通能够返销，返销业务提交时重新组织数据送服务开通,固定传cancle_tag='C';
       /* if (StringUtils.equals("C", canceltag))
        {
            sql.append("FROM TF_BH_TRADE T ");
        }
        else
        {
            sql.append("FROM TF_B_TRADE T ");
        }*/
        sql.append("FROM TF_B_TRADE T ");
        sql.append("WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND T.ACCEPT_MONTH = :ACCEPT_MONTH ");

        /*if ("0".equals(canceltag))
        {
            sql.append("AND T.ACCEPT_MONTH = :ACCEPT_MONTH ");
        }
        if (!StringUtils.equals("C", canceltag))
        {
            sql.append("AND T.CANCEL_TAG = :CANCEL_TAG ");
        }*/

        IDataset iDataset = Dao.qryBySql(sql, params, Route.getJourDb(routeId));

        return iDataset;
    }
    
    /**
     * 查询台账信息,不带路由
     * 
     * @param tradeId
     * @param acceptMonth
     * @param canceltag
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeByTradeIdMonth(String tradeId, String acceptMonth, String canceltag) throws Exception
    {
        return qryTradeByTradeIdMonth(tradeId, acceptMonth, canceltag, BizRoute.getRouteId());
    }

    /**
     * 查询台账信息,带路由
     * 
     * @param tradeId
     * @param acceptMonth
     * @param canceltag
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeByTradeIdMonth(String tradeId, String acceptMonth, String canceltag, String routeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("ACCEPT_MONTH", acceptMonth);
        params.put("CANCEL_TAG", canceltag);
        params.put("ROUTE_ID", routeId);// 给批量发报文用
        params.put("DB_SRC", routeId); // 传服务开通时给出路由信息

        StringBuilder sql = new StringBuilder(3000);

        sql.append("SELECT TO_CHAR(TRADE_ID) TRADE_ID, ACCEPT_MONTH, ");
        sql.append("TO_CHAR(BATCH_ID) BATCH_ID, ");
        sql.append("TO_CHAR(ORDER_ID) ORDER_ID, ");
        sql.append("TO_CHAR(PROD_ORDER_ID) PROD_ORDER_ID, ");
        sql.append("TO_CHAR(BPM_ID) BPM_ID, CAMPN_ID, TRADE_TYPE_CODE, ");
        sql.append("PRIORITY, SUBSCRIBE_TYPE, SUBSCRIBE_STATE, ");
        sql.append("NEXT_DEAL_TAG, IN_MODE_CODE, ");
        sql.append("NVL(TO_CHAR(CUST_ID), '0') CUST_ID, CUST_NAME, ");
        sql.append("TO_CHAR(USER_ID) USER_ID, ");
        sql.append("NVL(TO_CHAR(ACCT_ID), '0') ACCT_ID, SERIAL_NUMBER, ");
        sql.append("NET_TYPE_CODE, EPARCHY_CODE, CITY_CODE, PRODUCT_ID, ");
        sql.append("BRAND_CODE, TO_CHAR(CUST_ID_B) CUST_ID_B, ");
        sql.append("TO_CHAR(USER_ID_B) USER_ID_B, ");
        sql.append("TO_CHAR(ACCT_ID_B) ACCT_ID_B, SERIAL_NUMBER_B, ");
        sql.append("CUST_CONTACT_ID, SERV_REQ_ID, INTF_ID, ");
        sql.append("TO_CHAR(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
        sql.append("TRADE_STAFF_ID, TRADE_DEPART_ID, TRADE_CITY_CODE, ");
        sql.append("TRADE_EPARCHY_CODE, TERM_IP, ");
        sql.append("TO_CHAR(OPER_FEE) OPER_FEE, ");
        sql.append("TO_CHAR(FOREGIFT) FOREGIFT, ");
        sql.append("TO_CHAR(ADVANCE_PAY) ADVANCE_PAY, INVOICE_NO, ");
        sql.append("FEE_STATE, ");
        sql.append("TO_CHAR(FEE_TIME, 'yyyy-mm-dd hh24:mi:ss') FEE_TIME, ");
        sql.append("FEE_STAFF_ID, PROCESS_TAG_SET, OLCOM_TAG, ");
        sql.append("TO_CHAR(FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
        sql.append("TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME, ");
        sql.append("EXEC_ACTION, EXEC_RESULT, EXEC_DESC, CANCEL_TAG, ");
        sql.append("TO_CHAR(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE, ");
        sql.append("CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE, ");
        sql.append("CANCEL_EPARCHY_CODE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, ");
        sql.append("RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
        sql.append("RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10 ,PF_WAIT, :DB_SRC DB_SRC, :ROUTE_ID ROUTE_ID ");
        if(ProvinceUtil.isProvince(ProvinceUtil.SHXI))
        {
        	sql.append(", TO_CHAR(BOOK_DATE, 'YYYY-MM-DD HH24:MI:SS') BOOK_DATE ");
        }

        // 为了割接前业务服务开通能够返销，返销业务提交时重新组织数据送服务开通,固定传cancle_tag='C';
        if (StringUtils.equals("C", canceltag))
        {
            sql.append("FROM TF_BH_TRADE T ");
        }
        else
        {
            sql.append("FROM TF_B_TRADE T ");
        }

        sql.append("WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");

        if ("0".equals(canceltag))
        {
            sql.append("AND T.ACCEPT_MONTH = :ACCEPT_MONTH ");
        }
        if (!StringUtils.equals("C", canceltag))
        {
            sql.append("AND T.CANCEL_TAG = :CANCEL_TAG ");
        }

        IDataset iDataset = Dao.qryBySql(sql, params, Route.getJourDb(routeId));

        return iDataset;
    }
    
    
    
    
    /**
     * 查询台账历史信息,带路由
     * 
     * @param tradeId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeHisInfo(String order_id, String routeId) throws Exception
    {
        IData params = new DataMap();
        params.put("ORDER_ID", order_id);
        IDataset iDataset = Dao.qryByCode("TF_BH_TRADE", "qryTradeHisInfo", params);
        return iDataset;
    }
    
    
    /**
     * 查询台账信息,带路由
     * 
     * @param tradeId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryTradebyTradeid(String order_id, String routeId) throws Exception
    {
        IData params = new DataMap();
        params.put("ORDER_ID", order_id);

        IDataset iDataset = Dao.qryByCode("TF_B_TRADE", "qryTradeLineinfo", params);

        return iDataset;
    }
    
    /**
     * 根据手机号码和交易类型查询台账信息
     * 
     * @param serialNumber
     * @param tradeTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryTradebySerialNum(String serialNumber) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        //params.put("TRADE_TYPE_CODE", tradeTypeCode);        
        StringBuilder sql = new StringBuilder(2500);

        sql.append("SELECT * ");
        sql.append("FROM TF_B_TRADE A ");
        sql.append("WHERE A.SERIAL_NUMBER = :SERIAL_NUMBER ");
        //sql.append("AND A.TRADE_TYPE_CODE = :TRADE_TYPE_CODE ");

        IDataset ids = Dao.qryBySql(sql, params, Route.getJourDb(Route.CONN_CRM_CG));
		return ids;
    }
    
}
