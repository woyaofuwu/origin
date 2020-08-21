
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.order;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class UOrderHisInfoQry
{

    /**
     * 根据ORDER_ID查询客户订单历史表
     * 
     * @param orderId
     * @return
     * @throws Exception
     */
    public static IData qryOrderHisByOrderId(String orderId) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);

        StringBuilder sql = new StringBuilder(1000);

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
        sql.append("FROM TF_BH_ORDER ");
        sql.append("WHERE ORDER_ID = :ORDER_ID ");
        sql.append("AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:ORDER_ID, 5, 2)) ");

        IDataset hisOrderList = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getTradeEparchyCode()));

        if (IDataUtil.isEmpty(hisOrderList))
        {
            return new DataMap();
        }

        return hisOrderList.getData(0);
    }

}
