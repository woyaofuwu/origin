
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.fee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PosLogInfoQry
{

    public static IDataset getPrePosLog(String tradePosId, String status, String transType) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradePosId);
        param.put("STATUS", status); // 正常状态
        param.put("TRANS_TYPE", transType); // 缴费
        return Dao.qryByCode("TL_B_POS_LOG", "SEL_BY_TRADEID_TRANSTYPE", param);
    }

    /**
     * 查询已经刷卡信息
     * 
     * @param posTradeId
     * @param status
     * @return
     * @throws Exception
     */
    public static IDataset queryPosLog(String posTradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", posTradeId);
        return Dao.qryByCode("TL_B_POS_LOG", "SEL_BY_ALL_TRADEID", param);
    }
    
    public static IDataset queryPosLogBySn(String serialNumber, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TL_B_POS_LOG", "SEL_BY_SN", param, pagination);
    }
    
    public static IDataset queryPosLogBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TO_CHAR(POS_TRADE_ID) POS_TRADE_ID, ");
        sql.append("       TO_CHAR(TRADE_ID) TRADE_ID, ");
        sql.append("       SERIAL_NUMBER, ");
        sql.append("       TO_CHAR(USER_ID) USER_ID, ");
        sql.append("       STATUS, ");
        sql.append("       TRANS_TYPE, ");
        sql.append("       AMOUNT, ");
        sql.append("       POS_ID, ");
        sql.append("       CARD_NO, ");
        sql.append("       BANK_NO, ");
        sql.append("       TO_CHAR(ACCEPT_TIME, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_TIME, ");
        sql.append("       TO_CHAR(TRANS_DATE_TIME, 'YYYY-MM-DD HH24:MI:SS') TRANS_DATE_TIME, ");
        sql.append("       CERT_NO, ");
        sql.append("       REF_NO, ");
        sql.append("       BATCH_NO, ");
        sql.append("       RESP, ");
        sql.append("       RESP_INFO, ");
        sql.append("       MERCH_ID, ");
        sql.append("       OPER_STAFF_ID, ");
        sql.append("       AUTH_NO, ");
        sql.append("       TO_CHAR(CANCEL_POS_TRADE_ID) CANCEL_POS_TRADE_ID, ");
        sql.append("       TO_CHAR(CANCEL_TRADE_ID) CANCEL_TRADE_ID, ");
        sql.append("       CANCEL_CERT_NO, ");
        sql.append("       CANCEL_REF_NO, ");
        sql.append("       LOCAL_ADDR, ");
        sql.append("       S_RESP, ");
        sql.append("       TRUNC(SYSDATE) - TRUNC(TRANS_DATE_TIME) TODAY_PAY, ");
        sql.append("       TO_CHAR(trans_date_time, 'YYYYMMDD') TRANS_YYYYMMDD, ");
        sql.append("       DECODE(TRUNC(CANCEL_DATE), TRUNC(SYSDATE), 0, 1) CAN_CANCEL, ");
        sql.append("       TO_CHAR(TRANS_DATE_TIME + 30, 'YYYY-MM-DD HH24:MI:SS') EXP_DATE, ");
        sql.append("       DECODE(TRUNC(a.PRINT_RECEIPTS), '1', '是', '否') PRINT_RECEIPTS ");
        sql.append("  FROM TL_B_POS_LOG A ");
        sql.append(" WHERE A.SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.append("   AND ((a.TRANS_TYPE = 'S' and A.STATUS = '0') or ");
        sql.append("       (a.TRANS_TYPE = 'M' and A.STATUS = '4')) ");

        return Dao.qryBySql(sql, param);
    }
    
    public static IDataset queryPosLogBytradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TO_CHAR(POS_TRADE_ID) POS_TRADE_ID, ");
        sql.append("       TO_CHAR(TRADE_ID) TRADE_ID, ");
        sql.append("       SERIAL_NUMBER, ");
        sql.append("       TO_CHAR(USER_ID) USER_ID, ");
        sql.append("       STATUS, ");
        sql.append("       TRANS_TYPE, ");
        sql.append("       AMOUNT, ");
        sql.append("       POS_ID, ");
        sql.append("       CARD_NO, ");
        sql.append("       BANK_NO, ");
        sql.append("       TO_CHAR(ACCEPT_TIME, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_TIME, ");
        sql.append("       TO_CHAR(TRANS_DATE_TIME, 'YYYY-MM-DD HH24:MI:SS') TRANS_DATE_TIME, ");
        sql.append("       CERT_NO, ");
        sql.append("       REF_NO, ");
        sql.append("       BATCH_NO, ");
        sql.append("       RESP, ");
        sql.append("       RESP_INFO, ");
        sql.append("       MERCH_ID, ");
        sql.append("       OPER_STAFF_ID, ");
        sql.append("       AUTH_NO, ");
        sql.append("       TO_CHAR(CANCEL_POS_TRADE_ID) CANCEL_POS_TRADE_ID, ");
        sql.append("       TO_CHAR(CANCEL_TRADE_ID) CANCEL_TRADE_ID, ");
        sql.append("       CANCEL_CERT_NO, ");
        sql.append("       CANCEL_REF_NO, ");
        sql.append("       LOCAL_ADDR, ");
        sql.append("       S_RESP, ");
        sql.append("       TRUNC(SYSDATE) - TRUNC(TRANS_DATE_TIME) TODAY_PAY, ");
        sql.append("       TO_CHAR(trans_date_time, 'YYYYMMDD') TRANS_YYYYMMDD, ");
        sql.append("       DECODE(TRUNC(CANCEL_DATE), TRUNC(SYSDATE), 0, 1) CAN_CANCEL, ");
        sql.append("       TO_CHAR(TRANS_DATE_TIME + 30, 'YYYY-MM-DD HH24:MI:SS') EXP_DATE, ");
        sql.append("       DECODE(TRUNC(a.PRINT_RECEIPTS), '1', '是', '否') PRINT_RECEIPTS ");
        sql.append("  FROM TL_B_POS_LOG A ");
        sql.append(" WHERE A.TRADE_ID = :TRADE_ID ");
        sql.append("   AND ((a.TRANS_TYPE = 'S' and A.STATUS = '0') or ");
        sql.append("       (a.TRANS_TYPE = 'M' and A.STATUS = '4')) ");

        return Dao.qryBySql(sql, param);
    }
    

    
    public static IDataset queryPosLogByPosTradeId(String posTradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("POS_TRADE_ID", posTradeId);
        return Dao.qryByCode("TL_B_POS_LOG", "SEL_BY_POSTRADEID", param);
    }
}
