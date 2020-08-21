
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class TradeMag
{
    public static int actNextTradeByUserId(String userId) throws Exception
    {
        IData map = new DataMap();

        map.put("UESR_ID", userId);

        StringBuilder sql = new StringBuilder(500);

        sql.append("UPDATE TF_B_TRADE T ");
        sql.append("SET T.SUBSCRIBE_STATE = 'P', ");
        sql.append("EXEC_DESC = '[' || TO_CHAR(SYSTIMESTAMP, 'YYYY-MM-DD HH24:MI:ss:ff3') || ']订单自动激活，进入订单完工等待' ");
        sql.append("WHERE T.USER_ID = TO_NUMBER(:UESR_ID) ");
        sql.append("AND T.SUBSCRIBE_STATE = 'B' ");

        int c = Dao.executeUpdate(sql, map,Route.getJourDb(CSBizBean.getTradeEparchyCode()));

        return c;
    }

    public static int delLimit0ByTradeId(String tradeId) throws Exception
    {
        String acceptMonth = StrUtil.getAcceptMonthById(tradeId);

        IData map = new DataMap();
        map.put("LIMIT_TRADE_ID", tradeId);
        map.put("ACCEPT_MONTH", acceptMonth);

        StringBuilder sql = new StringBuilder(500);

        sql.append("UPDATE TF_B_TRADE_LIMIT ");
        sql.append("SET STATE = 'F' ");
        sql.append("WHERE LIMIT_TRADE_ID = TO_NUMBER(:LIMIT_TRADE_ID) ");
        sql.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");
        sql.append("AND LIMIT_TYPE = '0' ");
        sql.append("AND STATE = '0' ");

        return Dao.executeUpdate(sql, map, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static int delLimit1ByTradeId(String tradeId) throws Exception
    {
        String acceptMonth = StrUtil.getAcceptMonthById(tradeId);

        IData map = new DataMap();
        map.put("TRADE_ID", tradeId);
        map.put("ACCEPT_MONTH", acceptMonth);

        StringBuilder sql = new StringBuilder(500);

        sql.append("UPDATE TF_B_TRADE_LIMIT ");
        sql.append("SET STATE = 'F' ");
        sql.append("WHERE TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");
        sql.append("AND LIMIT_TYPE = '1' ");
        sql.append("AND STATE = '0' ");

        return Dao.executeUpdate(sql, map, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static int getTradeMgrPbossAttrCountById(String tradeId, String attrCode) throws Exception
    {
        IData map = new DataMap();
        map.put("TRADE_ID", tradeId);
        map.put("ATTR_CODE", attrCode);
        StringBuilder sql = new StringBuilder(500);

        sql.append("SELECT * FROM TF_B_TRADEMGRPBOSS_ATTR ");
        sql.append("WHERE ");
        sql.append("TRADE_ID = TO_NUMBER(:TRADE_ID) and ATTR_CODE=:ATTR_CODE ");

        return Dao.executeUpdate(sql, map,Route.getJourDb());
    }

    public static int intTradeMgrPbossAttr(String tradeId, String acceptMonth, String attrCode, String seq, String attrName, String attrValue) throws Exception
    {

        IData map = new DataMap();
        map.put("TRADE_ID", tradeId);
        map.put("ORDER_ID", tradeId);
        map.put("ACCEPT_MONTH", acceptMonth);
        map.put("ATTR_CODE", attrCode);
        map.put("SEQ", seq);
        map.put("ATTR_NAME", attrName);
        map.put("ATTR_VALUE", attrValue);
        map.put("UPDATE_TIME", SysDateMgr.getSysTime());

        StringBuilder sql = new StringBuilder(500);
        sql.append("INSERT INTO TF_B_TRADEMGRPBOSS_ATTR");
        sql.append("(TRADE_ID,ORDER_ID,ACCEPT_MONTH,ATTR_CODE,SEQ,ATTR_NAME,ATTR_VALUE,UPDATE_TIME) ");
        sql.append("VALUES");
        sql.append("(TO_NUMBER(:TRADE_ID), TO_NUMBER(:ORDER_ID), TO_NUMBER(:ACCEPT_MONTH), :ATTR_CODE, TO_NUMBER(:SEQ), :ATTR_NAME, :ATTR_VALUE, TO_DATE(:UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS')) ");

        return Dao.executeUpdate(sql, map,Route.getJourDb());
    }

    public static int intTradePbossFinish(String tradeId, String acceptMonth, String cancelTag, String pfRtnResultCode, String finishDate) throws Exception
    {

        IData map = new DataMap();

        map.put("TRADE_ID", tradeId);
        map.put("ACCEPT_MONTH", acceptMonth);
        map.put("CANCEL_TAG", cancelTag);
        map.put("DAY", SysDateMgr.getSysTime().substring(8, 10));
        map.put("PF_RESULTCODE", "0");
        map.put("PFRTN_RESULTCODE", pfRtnResultCode);
        map.put("FINISH_DATE", finishDate);
        map.put("UPDATE_TIME", finishDate);

        StringBuilder sql = new StringBuilder(500);

        sql.append("INSERT INTO TF_B_TRADE_PBOSS_FINISH");
        sql.append("(TRADE_ID, ACCEPT_MONTH, CANCEL_TAG, DAY,PF_RESULTCODE, PFRTN_RESULTCODE, FINISH_DATE, UPDATE_TIME) ");
        sql.append("VALUES");
        sql.append("(TO_NUMBER(:TRADE_ID), TO_NUMBER(:ACCEPT_MONTH), :CANCEL_TAG, TO_NUMBER(:DAY), :PF_RESULTCODE, :PFRTN_RESULTCODE, TO_DATE(:FINISH_DATE,'YYYY-MM-DD HH24:MI:SS'), TO_DATE(:UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS')) ");

        return Dao.executeUpdate(sql, map, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    public static int traceLog(String orderId, String tradeId, String acceptMonth, String lastTime, String activeCode, String resultCode, String resultInfo) throws Exception
    {
        String resultInfo4 = StrUtil.strLimit(resultInfo, 4000);

        IData map = new DataMap();

        map.put("ORDER_ID", orderId);
        map.put("TRADE_ID", tradeId);
        map.put("ACCEPT_MONTH", acceptMonth);
        map.put("ACTIVE_CODE", activeCode);
        map.put("LAST_TIME", lastTime);
        map.put("RESULT_CODE", resultCode);
        map.put("RESULT_INFO", resultInfo4);

        StringBuilder sql = new StringBuilder(500);

        sql.append("INSERT INTO TL_B_ORDER_TRACE ");
        sql.append("(ORDER_ID, TRADE_ID, ACCEPT_MONTH, ACTIVE_CODE, TRACE_TIME, LAST_TIME, RESULT_CODE, RESULT_INFO) ");
        sql.append("VALUES ");
        sql.append("(:ORDER_ID, :TRADE_ID, :ACCEPT_MONTH, :ACTIVE_CODE, SYSTIMESTAMP, TO_CHAR(:LAST_TIME, 'YYYY-MM-DD HH24:MI:ss:ff3'), :RESULT_CODE, :RESULT_INFO) ");

        return Dao.executeUpdate(sql, map, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static int updateStateByOrderId(String orderId, String orderState, String routeId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("ORDER_ID", orderId);
        inparams.put("STATE", orderState);

        StringBuilder sql = new StringBuilder(500);

        sql.append("UPDATE TF_B_ORDER ");
        sql.append("SET ORDER_STATE = :STATE, EXEC_TIME = sysdate ");
        sql.append("WHERE ORDER_ID = :ORDER_ID ");

        return Dao.executeUpdate(sql, inparams, Route.getJourDb(routeId));
    }

    /**
     * 更新order表信息
     * 
     * @param orderId
     * @param hq_tag
     * @param app_type
     * @throws Exception
     */
    public static void updateStateHq(String orderId, String hq_tag, String app_type, String orderState) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("ORDER_ID", orderId);
        inparams.put("ORDER_STATE", orderState);
        inparams.put("HQ_TAG", hq_tag);
        inparams.put("APP_TYPE", app_type);

        StringBuilder sql = new StringBuilder(100);

        sql.append("UPDATE TF_B_ORDER O ");
        sql.append("SET O.ORDER_STATE = :ORDER_STATE, ");
        sql.append("O.HQ_TAG      = :HQ_TAG, ");
        sql.append("O.APP_TYPE    = :APP_TYPE, ");
        sql.append("O.update_time = SYSDATE ");
        sql.append("WHERE O.ORDER_ID = :ORDER_ID ");

        Dao.executeUpdate(sql, inparams, Route.getJourDb());
    }

    public static int updLimitTradeStateByTradeId(String tradeId, String acceptMonth, String cancelTag, String subscribeState, String execDesc, String routeId) throws Exception
    {
        IData map = new DataMap();

        map.put("TRADE_ID", tradeId);
        map.put("ACCEPT_MONTH", acceptMonth);
        map.put("CANCEL_TAG", cancelTag);
        map.put("SUBSCRIBE_STATE", subscribeState);
        map.put("EXEC_DESC", execDesc);

        StringBuilder sql = new StringBuilder(500);

        sql.append("UPDATE TF_B_TRADE T ");
        sql.append("SET T.SUBSCRIBE_STATE = :SUBSCRIBE_STATE, ");
        sql.append("EXEC_DESC = '[' || TO_CHAR(SYSTIMESTAMP, 'YYYY-MM-DD HH24:MI:ss:ff3') || ']' || :EXEC_DESC ");
        sql.append("WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND T.ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH) ");
        sql.append("AND T.CANCEL_TAG = :CANCEL_TAG ");
        sql.append("AND T.SUBSCRIBE_STATE = 'B' ");

        int c = Dao.executeUpdate(sql, map, Route.getJourDb(routeId));

        return c;
    }

    public static int updOrdNextDealTagByPk(String orderId, String acceptMonth, String cancelTag, String nextDealTag, String routeId) throws Exception
    {
        IData map = new DataMap();

        map.put("ORDER_ID", orderId);
        map.put("ACCEPT_MONTH", acceptMonth);
        map.put("CANCEL_TAG", cancelTag);
        map.put("NEXT_DEAL_TAG", nextDealTag);

        StringBuilder sql = new StringBuilder(500);

        sql.append("UPDATE TF_B_ORDER T ");
        sql.append("SET T.NEXT_DEAL_TAG = :NEXT_DEAL_TAG ");
        sql.append("WHERE T.ORDER_ID = TO_NUMBER(:ORDER_ID) ");
        sql.append("AND T.ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH) ");
        sql.append("AND T.CANCEL_TAG = :CANCEL_TAG ");

        return Dao.executeUpdate(sql, map, Route.getJourDb(routeId));
    }

    public static int updOrdStateByOrderId(String orderId, String acceptMonth, String cancelTag, String oredeState, String nextDealTag) throws Exception
    {

        IData map = new DataMap();

        map.put("ORDER_ID", orderId);
        map.put("ACCEPT_MONTH", acceptMonth);
        map.put("CANCEL_TAG", cancelTag);
        map.put("ORDER_STATE", oredeState);
        map.put("NEXT_DEAL_TAG", nextDealTag);

        StringBuilder sql = new StringBuilder(500);

        sql.append("UPDATE TF_B_ORDER T ");
        sql.append("SET T.ORDER_STATE = :ORDER_STATE, ");
        sql.append("T.NEXT_DEAL_TAG = DECODE(:NEXT_DEAL_TAG, NULL, T.NEXT_DEAL_TAG, :NEXT_DEAL_TAG) ");
        sql.append("WHERE T.ORDER_ID = TO_NUMBER(:ORDER_ID) ");
        sql.append("AND T.ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH) ");
        sql.append("AND T.CANCEL_TAG = :CANCEL_TAG ");

        return Dao.executeUpdate(sql, map,Route.getJourDb());
    }

    public static int updTradeBatDealByBatchId(String batchId, String state, String result) throws Exception
    {
        String acceptMonth = StrUtil.getAcceptMonthById(batchId);

        IData map = new DataMap();
        map.put("OPERATE_ID", batchId);
        map.put("ACCEPT_MONTH", acceptMonth);
        map.put("DEAL_STATE", state);
        map.put("DEAL_RESULT", result);

        StringBuilder sql = new StringBuilder(500);

		sql.append("UPDATE TF_B_TRADE_BATDEAL T ");
		sql.append("SET T.DEAL_STATE = :DEAL_STATE, T.DEAL_TIME = SYSDATE, T.DEAL_RESULT = :DEAL_RESULT, ");
		sql.append("T.DEAL_DESC = '' ");
		sql.append("WHERE OPERATE_ID = TO_NUMBER(:OPERATE_ID) ");
		sql.append("AND T.ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH) ");

        int c = Dao.executeUpdate(sql, map, Route.getJourDb(Route.CONN_CRM_CG));

		return c;
	}
	
	public static int updTradeBatDealByBatchId(IData data) throws Exception
	{
		StringBuilder sql = new StringBuilder(500);

		sql.append("UPDATE TF_B_TRADE_BATDEAL T ");
		sql.append("SET T.DEAL_STATE = :DEAL_STATE, T.TRADE_ID = :TRADE_ID , ROUTE_EPARCHY_CODE = :ROUTE_EPARCHY_CODE, T.DEAL_TIME = SYSDATE, T.DEAL_RESULT = :DEAL_RESULT, ");
		sql.append("T.DEAL_DESC = '' ");
		sql.append("WHERE OPERATE_ID = TO_NUMBER(:OPERATE_ID) ");
		sql.append("AND T.ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH) ");

		int c = Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));

		return c;
	}
	

    public static int updTradeStateBack(String tradeId, String acceptMonth, String cancelTag, String subscribeState) throws Exception
    {
        IData map = new DataMap();

        map.put("TRADE_ID", tradeId);
        map.put("ACCEPT_MONTH", acceptMonth);
        map.put("CANCEL_TAG", cancelTag);
        map.put("SUBSCRIBE_STATE", subscribeState);

        StringBuilder sql = new StringBuilder(500);

        sql.append("UPDATE TF_B_TRADE T ");
        sql.append("SET T.SUBSCRIBE_STATE = :SUBSCRIBE_STATE ");
        sql.append("WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND T.ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH) ");
        sql.append("AND T.CANCEL_TAG = :CANCEL_TAG ");
        sql.append("AND (T.SUBSCRIBE_STATE = 'F' OR T.SUBSCRIBE_STATE = 'M') ");

        return Dao.executeUpdate(sql, map,Route.getJourDb());
    }

    public static int updTradeStateByOrder(String orderId, String acceptMonth, String cancelTag, String subscribeState, String execDesc, String routeId) throws Exception
    {
        IData map = new DataMap();

        map.put("ORDER_ID", orderId);
        map.put("ACCEPT_MONTH", acceptMonth);
        map.put("CANCEL_TAG", cancelTag);
        map.put("SUBSCRIBE_STATE", subscribeState);
        map.put("EXEC_DESC", execDesc);

        StringBuilder sql = new StringBuilder(500);

        sql.append("UPDATE TF_B_TRADE T ");
        sql.append("SET T.SUBSCRIBE_STATE = :SUBSCRIBE_STATE, ");
        sql.append("EXEC_DESC = '[' || TO_CHAR(SYSTIMESTAMP, 'YYYY-MM-DD HH24:MI:ss:ff3') || ']' || :EXEC_DESC ");
        sql.append("WHERE T.ORDER_ID = TO_NUMBER(:ORDER_ID) ");
        sql.append("AND T.ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH) ");
        sql.append("AND T.CANCEL_TAG = :CANCEL_TAG ");

        int c = Dao.executeUpdate(sql, map, Route.getJourDb(routeId));

        return c;
    }

    public static int updTradeStateByTradeId(String tradeId, String acceptMonth, String cancelTag, String subscribeState, String execDesc, String routeId) throws Exception
    {
        IData map = new DataMap();

        map.put("TRADE_ID", tradeId);
        map.put("ACCEPT_MONTH", acceptMonth);
        map.put("CANCEL_TAG", cancelTag);
        map.put("SUBSCRIBE_STATE", subscribeState);
        map.put("EXEC_DESC", execDesc);

        StringBuilder sql = new StringBuilder(500);

        sql.append("UPDATE TF_B_TRADE T ");
        sql.append("SET T.SUBSCRIBE_STATE = :SUBSCRIBE_STATE, ");
        sql.append("EXEC_DESC = '[' || TO_CHAR(SYSTIMESTAMP, 'YYYY-MM-DD HH24:MI:ss:ff3') || ']' || :EXEC_DESC ");
        sql.append("WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND T.ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH) ");
        sql.append("AND T.CANCEL_TAG = :CANCEL_TAG ");

        int c = Dao.executeUpdate(sql, map,Route.getJourDb(routeId));

        return c;
    }
    
    public static int upWideNetTradeByTradeId(String tradeId, String acceptMonth, String cancelTag, String construction_addr, String const_staff_id, String const_phone, String rsrvTag3, String finishDate, String deviceId, String portId) throws Exception
    {
        IData map = new DataMap();

        map.put("TRADE_ID", tradeId);
        map.put("ACCEPT_MONTH", acceptMonth);
        map.put("CANCEL_TAG", cancelTag);
        map.put("CONSTRUCTION_ADDR", construction_addr);
        map.put("CONST_STAFF_ID", const_staff_id);
        map.put("CONST_PHONE", const_phone);
        map.put("RSRV_TAG3", rsrvTag3);
        map.put("FINISH_DATE", finishDate);
        map.put("RSRV_NUM1", deviceId);
        map.put("RSRV_NUM4", portId);

        StringBuilder sql = new StringBuilder(500);

        sql.append("UPDATE TF_B_TRADE_WIDENET T ");
        sql.append("SET T.CONSTRUCTION_ADDR = :CONSTRUCTION_ADDR, ");
        sql.append("T.CONST_STAFF_ID = :CONST_STAFF_ID, ");
        sql.append("T.CONST_PHONE = :CONST_PHONE, ");
        sql.append("T.RSRV_TAG3 = :RSRV_TAG3, ");
        /*
         * QR-20170210-01/tf_f_user_widenet表rsrv_num1为空的问题
         * zhangxing3
         * 2017/02/13
         */
        //sql.append("T.RSRV_NUM1 = :RSRV_NUM1, ");
        sql.append("T.RSRV_NUM4 = :RSRV_NUM4, ");
        sql.append("T.RSRV_DATE3 = TO_DATE(:FINISH_DATE,'YYYY-MM-DD HH24:MI:SS') ");
        sql.append("WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND T.ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH) ");
        sql.append("AND T.MODIFY_TAG = :CANCEL_TAG ");

        int c = Dao.executeUpdate(sql, map,Route.getJourDb());

        return c;
    }
}
