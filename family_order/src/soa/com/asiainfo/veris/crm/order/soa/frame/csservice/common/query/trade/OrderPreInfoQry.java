
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class OrderPreInfoQry
{

    public static IDataset qryDelNotSuccessByReqId(String reqId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("REQUEST_ID", reqId);
        return Dao.qryByCode("TF_B_ORDER_PRE", "SEL_BY_REQID_STATE", cond, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset qryDelNotSuccessBySn(String serialNumber) throws Exception
    {
        IData cond = new DataMap();
        cond.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_B_ORDER_PRE", "SEL_BY_SN_STATE", cond, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IData queryOrderPreInfoByPreId(String preId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PRE_ID", preId);
        IDataset dataset = Dao.qryByCode("TF_B_ORDER_PRE", "SEL_BY_PREID", cond, Route.getJourDb(Route.CONN_CRM_CG));
        return IDataUtil.isEmpty(dataset) ? null : dataset.getData(0);
    }

    public static IDataset queryOrderPreInfoByReqId(String reqId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("REQUEST_ID", reqId);
        return Dao.qryByCode("TF_B_ORDER_PRE", "SEL_BY_REQID", cond, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryOrderPreInfoByReqId2(String reqId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("REQUEST_ID", reqId);
        return Dao.qryByCode("TF_B_ORDER_PRE", "SEL_BY_REQID2", cond, Route.getJourDb(Route.CONN_CRM_CG));
    }
    public static IDataset queryOrderPreInfoBySnType(String serialNumber,String preType) throws Exception
    {
        IData cond = new DataMap();
        cond.put("SERIAL_NUMBER", serialNumber);
        cond.put("PRE_TYPE", preType);
        return Dao.qryByCode("TF_B_ORDER_PRE", "SEL_BY_SN_PRETYPE", cond, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset queryOrderPreInfoBySnPreType(String serialNumber,String preType,String replayState,String acceptState) throws Exception
    {
        IData cond = new DataMap();
        cond.put("SERIAL_NUMBER", serialNumber);
        cond.put("PRE_TYPE", preType);
        cond.put("REPLY_STATE", replayState);
        cond.put("ACCEPT_STATE", acceptState);
        
        return Dao.qryByCode("TF_B_ORDER_PRE", "SEL_BY_SN_PRETYPE", cond, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset queryOrderPreInfoByPreType(String preType,String acceptState) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PRE_TYPE", preType);
        cond.put("ACCEPT_STATE", acceptState);
        
        return Dao.qryByCode("TF_B_ORDER_PRE", "SEL_BY_PRETYPE", cond, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static void updateOrderPreInfoBySnPreType(String serialNumber,String preType,String acceptState,String acceptResult,String replyStateNew,String replyStateOld) throws Exception
    {
        IData cond = new DataMap();
        cond.put("SERIAL_NUMBER", serialNumber);
        cond.put("PRE_TYPE", preType);
        cond.put("ACCEPT_STATE", acceptState);
        cond.put("ACCEPT_RESULT", acceptResult);
        cond.put("REPLY_STATE_NEW", replyStateNew);
        cond.put("REPLY_STATE_OLD", replyStateOld);
        Dao.executeUpdateByCodeCode("TF_B_ORDER_PRE", "UPD_BY_SN_PRETYPE", cond, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset queryOrderPreInfoByPtTradeCode(String preType,String svcName) throws Exception {
		IData cond = new DataMap();
        cond.put("PRE_TYPE", preType);
        cond.put("TRADE_TYPE_CODE", svcName);
        
        return Dao.qryByCode("TF_B_ORDER_PRE", "SEL_BY_PRETYPESVCNAME", cond, Route.getJourDb(Route.CONN_CRM_CG));
	}
    
    public static IDataset queryOrderPreInfoByPtTTCode(String preType,String tradeTypeCode,String acceptState) throws Exception {
		IData cond = new DataMap();
        cond.put("PRE_TYPE", preType);
        cond.put("TRADE_TYPE_CODE", tradeTypeCode);
        cond.put("ACCEPT_STATE", acceptState);
        
        return Dao.qryByCode("TF_B_ORDER_PRE", "SEL_BY_PRETYPETRADE", cond, Route.getJourDb(Route.CONN_CRM_CG));
	}
    /**
     * 根据SERIAL_NUMBER 查询预约工单信息
     * 
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset queryPreOrderInfosBySn(String requestId, String acceptState, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REQUEST_ID", requestId);
        param.put("ACCEPT_STATE", acceptState);
   
        SQLParser sql = new SQLParser(param);
        sql.addSQL("SELECT PRE_ID, ");
        sql.addSQL("PRE_TYPE, ");
        sql.addSQL("ACCEPT_MONTH, ");
        sql.addSQL("ACCEPT_STATE, ");
        sql.addSQL("REQUEST_ID, ");
        sql.addSQL("ORDER_ID, ");
        sql.addSQL("to_char(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        sql.addSQL("to_char(END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
        sql.addSQL("TRADE_TYPE_CODE, ");
        sql.addSQL("SERIAL_NUMBER, ");
        sql.addSQL("REPLY_STATE, ");
        sql.addSQL("to_char(REPLY_TIME,'YYYY-MM-DD HH24:MI:SS') REPLY_TIME, ");
        sql.addSQL("REPLY_CONTENT, ");
        sql.addSQL("ACCEPT_RESULT, ");
        sql.addSQL("REMARK, ");
        sql.addSQL("SVC_NAME, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5 ");
        sql.addSQL("FROM TF_B_ORDER_PRE T ");
        sql.addSQL("WHERE 1=1 ");
        sql.addSQL("AND T.ACCEPT_STATE = :ACCEPT_STATE ");
        sql.addSQL("AND T.REQUEST_ID = :REQUEST_ID ");
        sql.addSQL("ORDER BY START_DATE DESC ");
        return Dao.qryByParse(sql, page, Route.getJourDb(Route.CONN_CRM_CG));
    }
}
