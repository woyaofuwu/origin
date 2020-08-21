
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.twocheck;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TwoCheckInfoQry extends CSBizBean
{

    /**
     * 根据REQUEST_ID 查询预约工单信息
     * 
     * @param reqId
     * @return
     * @throws Exception
     */
    public static IDataset queryPreOrderInfosByReqId(String reqId) throws Exception
    {
        IData param = new DataMap();
        param.put("REQUEST_ID", reqId);
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT PRE_ORDER_ID, ");
        sql.append("PRE_ORDER_TYPE_CODE, ");
        sql.append("ACCEPT_MONTH, ");
        sql.append("ACCEPT_STATE, ");
        sql.append("REQUEST_ID, ");
        sql.append("ORDER_ID, ");
        sql.append("to_char(START_DATE,'yyyy-MM-dd HH:mm:ss') START_DATE, ");
        sql.append("to_char(END_DATE,'yyyy-MM-dd HH:mm:ss') END_DATE, ");
        sql.append("TRADE_TYPE_CODE, ");
        sql.append("SERIAL_NUMBER, ");
        sql.append("REPLY_STATE, ");
        sql.append("to_char(REPLY_TIME,'yyyy-MM-dd HH:mm:ss') REPLY_TIME, ");
        sql.append("REPLY_CONTENT, ");
        sql.append("ACCEPT_RESULT, ");
        sql.append("ACCEPT_DATA1||ACCEPT_DATA2||ACCEPT_DATA3||ACCEPT_DATA4||ACCEPT_DATA5 ACCEPT_DATA, ");
        sql.append("REMARK, ");
        sql.append("X_TRANS_CODE ");
        sql.append("FROM TF_B_ORDER_PRE T ");
        sql.append("WHERE T.REQUEST_ID = :REQUEST_ID ");
        return Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 获取TD_B_SEC_AFFIRM表数据
     * 
     * @param tradeTypeCode
     * @param inModeCode
     * @param idType
     * @param id
     * @return
     * @throws Exception
     */
    public static IDataset querySecAffirmByPK(String tradeTypeCode, String inModeCode, String idType, String id) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("IN_MODE_CODE", inModeCode);
        param.put("ID_TYPE", idType);
        param.put("ID", id);

        return Dao.qryByCode("TD_B_SEC_AFFIRM", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    /**
     * 获取TF_B_TWO_CHECK表数据
     * 
     * @param forceObject
     * @param inModeCode
     * @param idType
     * @param id
     * @return
     * @throws Exception
     */
    public static IDataset querySecCheckByPK(String tradeId, String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_ID", tradeId);

        return Dao.qryByCode("TF_B_TWO_CHECK", "SEL_BY_TRADEID", param);
    }
}
