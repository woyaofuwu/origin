
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: TradePosInfoqQry.java
 * @Description: pos刷卡数据
 * @version: v1.0.0
 * @author: liuke
 * @date: 下午03:15:31 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-10-23 liuke v1.0.0 修改原因
 */
public class TradePosInfoQry
{

    /**
     * 根据orderId查询pos刷卡记录
     * 
     * @param orderId
     * @return
     * @throws Exception
     */
    public static IDataset queryTradePosByOrder(String orderId) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT TRADE_POS_ID,to_char(TRADE_ID) TRADE_ID,SERIAL_NUMBER, to_char(USER_ID) USER_ID,STATUS,");
        sql.append("TRANS_TYPE,AMOUNT,BALANCE,POS_ID,CARD_NO,to_char(TRANS_DATE_TIME,'YYYY-MM-DD HH24:MI:SS') TRANS_DATE_TIME, ");
        sql.append("TTC,REF_NO,BATCH_NO,CARD_TYPE,RESP,RESP_INFO, ADD_INFO,MERCH_ID,MERCH_NAME,ACQUIRER,ISSUER, ");
        sql.append("OPER_ID,TRANS_NAME,EXP_DATE,BIZ_UNION,AUTH_NO,MEMBER_CUST_ID,SERIAL_NUMBER_B,USER_ID_B,CANCEL_TRADE_POS_ID, ");
        sql.append("CANCEL_TRADE_ID,CANCEL_TTC,CANCEL_REF_NO,to_char(CANCEL_DATE,'YYYY-MM-DD HH24:MI:SS') CANCEL_DATE,CANCEL_TRANS_TYPE,");
        sql.append("REF_INFO,LOCAL_ADDR,to_char(SETTLE_DATE,'YYYY-MM-DD HH24:MI:SS') SETTLE_DATE,to_char(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,");
        sql.append("UPDATE_STAFF_ID,UPDATE_DEPART_ID,S_REQ,S_RESP,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,");
        sql.append("RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,");
        sql.append("to_char(RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,");
        sql.append("to_char(RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,");
        sql.append("to_char(RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,");
        sql.append("ORDER_ID ");
        sql.append(" FROM TF_B_TRADE_POS T ");
        sql.append(" WHERE T.ORDER_ID = :ORDER_ID ");

        return Dao.qryBySql(sql, param);
    }
}
