
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.order;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class UOrderInfoQry
{
    /**
     * 根据ORDER_ID查询客户订单表
     * 
     * @param orderId
     * @return
     * @throws Exception
     */
    public static IData qryOrderAllByOrderId(String orderId) throws Exception
    {
        IData orderData = qryOrderByOrderId(orderId, null);

        if (IDataUtil.isEmpty(orderData))
        {
            orderData = UOrderHisInfoQry.qryOrderHisByOrderId(orderId);
        }

        return orderData;
    }

    /**
     * 根据ORDER_ID查询客户订单表
     * 
     * @param orderId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryOrderByOrderId(String orderId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);

        StringBuilder sql = new StringBuilder(2000);

        sql.append("SELECT ORDER_ID, ACCEPT_MONTH, BATCH_ID, BATCH_COUNT, SUCC_TOTAL, FAIL_TOTAL, ");
        sql.append("ORDER_TYPE_CODE, TRADE_TYPE_CODE, PRIORITY, ORDER_STATE, NEXT_DEAL_TAG, ");
        sql.append("IN_MODE_CODE, CUST_ID, CUST_NAME, PSPT_TYPE_CODE, PSPT_ID, EPARCHY_CODE, ");
        sql.append("CITY_CODE, AUTH_CODE, ACTOR_NAME, ACTOR_PHONE, ACTOR_PSPT_TYPE_CODE, ");
        sql.append("ACTOR_PSPT_ID, TO_CHAR(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
        sql.append("TRADE_STAFF_ID, TRADE_DEPART_ID, TRADE_CITY_CODE, TRADE_EPARCHY_CODE, ");
        sql.append("TERM_IP, OPER_FEE, FOREGIFT, ADVANCE_PAY, INVOICE_NO, FEE_STATE, ");
        sql.append("TO_CHAR(FEE_TIME, 'yyyy-mm-dd hh24:mi:ss') FEE_TIME, FEE_STAFF_ID, ");
        sql.append("PROCESS_TAG_SET, ");
        sql.append("TO_CHAR(FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
        sql.append("TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME, EXEC_ACTION, ");
        sql.append("EXEC_RESULT, EXEC_DESC, CUST_IDEA, HQ_TAG, DECOMPOSE_RULE_ID, ");
        sql.append("DISPATCH_RULE_ID, CUST_CONTACT_ID, SERV_REQ_ID, CONTRACT_ID, SOLUTION_ID, ");
        sql.append("CANCEL_TAG, TO_CHAR(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE, ");
        sql.append("CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE, CANCEL_EPARCHY_CODE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, RSRV_STR2, ");
        sql.append("RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, ");
        sql.append("RSRV_STR9, RSRV_STR10, ORDER_KIND_CODE ");
        sql.append("FROM TF_B_ORDER ");
        sql.append("WHERE ORDER_ID = :ORDER_ID ");
        sql.append("AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:ORDER_ID, 5, 2)) ");

        IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));

        if (IDataUtil.isEmpty(ids))
        {
            return new DataMap();
        }

        return ids.getData(0);
    }

    /**
     * 查询客户订单信息
     * 
     * @param orderId
     * @param acceptMonth
     * @param cancelTag
     * @return
     * @throws Exception
     */
    public static IData qryOrderByPk(String orderId, String acceptMonth, String cancelTag) throws Exception
    {
        IData inparams = new DataMap();

        inparams.put("ORDER_ID", orderId);
        inparams.put("ACCEPT_MONTH", acceptMonth);
        inparams.put("CANCEL_TAG", cancelTag);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT ACCEPT_MONTH, TO_CHAR(ORDER_ID) ORDER_ID, ");
        sql.append("TRADE_TYPE_CODE, ORDER_TYPE_CODE, PRIORITY, ");
        sql.append("ORDER_STATE, NEXT_DEAL_TAG, IN_MODE_CODE, TERM_IP, ");
        sql.append("EPARCHY_CODE, CITY_CODE, TO_CHAR(OPER_FEE) OPER_FEE, ");
        sql.append("TO_CHAR(FOREGIFT) FOREGIFT, ");
        sql.append("TO_CHAR(ADVANCE_PAY) ADVANCE_PAY, INVOICE_NO, ");
        sql.append("FEE_STATE, FEE_STAFF_ID, ");
        sql.append("TO_CHAR(FEE_TIME, 'yyyy-mm-dd hh24:mi:ss') FEE_TIME, ");
        sql.append("TO_CHAR(CUST_ID) CUST_ID, CUST_NAME, AUTH_CODE, ");
        sql.append("PSPT_TYPE_CODE, PSPT_ID, ACTOR_NAME, ACTOR_PHONE, ");
        sql.append("ACTOR_PSPT_TYPE_CODE, ACTOR_PSPT_ID, ");
        sql.append("TO_CHAR(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
        sql.append("TRADE_STAFF_ID, TRADE_DEPART_ID, TRADE_CITY_CODE, ");
        sql.append("TRADE_EPARCHY_CODE, ");
        sql.append("TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME, ");
        sql.append("TO_CHAR(FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
        sql.append("HQ_TAG, DECOMPOSE_RULE_ID, DISPATCH_RULE_ID, ");
        sql.append("TO_CHAR(SERV_REQ_ID) SERV_REQ_ID, CONTRACT_ID, ");
        sql.append("TO_CHAR(SOLUTION_ID) SOLUTION_ID, PROCESS_TAG_SET, ");
        sql.append("CANCEL_TAG, ");
        sql.append("TO_CHAR(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE, ");
        sql.append("CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE, ");
        sql.append("CANCEL_EPARCHY_CODE, APP_TYPE, BATCH_ID,ORDER_KIND_CODE, SUBSCRIBE_TYPE ");

        // 为了割接前业务服务开通能够返销，返销业务提交时重新组织数据送服务开通,固定传cancle_tag='C';
        if (StringUtils.equals("C", cancelTag))
        {
            sql.append("FROM TF_BH_ORDER ");
        }
        else
        {
            sql.append("FROM TF_B_ORDER ");
        }

        sql.append("WHERE ORDER_ID = TO_NUMBER(:ORDER_ID) ");
        sql.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");

        if (StringUtils.equals("C", cancelTag))
        {
            sql.append("AND CANCEL_TAG = '0' ");
        }
        else
        {
            sql.append("AND CANCEL_TAG = :CANCEL_TAG ");
        }

        IDataset ids = Dao.qryBySql(sql, inparams, Route.getJourDb(BizRoute.getRouteId()));

        if (IDataUtil.isEmpty(ids))
        {
            return new DataMap();
        }

        return ids.getData(0);
    }
}
