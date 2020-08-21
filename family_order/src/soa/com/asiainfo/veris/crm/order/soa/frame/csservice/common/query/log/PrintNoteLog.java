
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.log;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PrintNoteLog
{
    public static IDataset queryPosLog(String tradeId, String serialNumber, String noteNo, Pagination pagination) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("NOTE_NO", noteNo);
        inparams.put("SERIAL_NUMBER", serialNumber);
        inparams.put("TRADE_ID", tradeId);
        SQLParser parser = new SQLParser(inparams);

        parser.addSQL(" SELECT PRINT_ID, TRADE_ID, TEMPLET_CODE, TEMPLET_TYPE, NOTE_NO, TAX_NO," + " SOURCE_TYPE, SERIAL_NUMBER, ACCT_ID, PAY_NAME, PRINT_MODE, START_CYCLE_ID, "
                + "END_CYCLE_ID, TRADE_TIME, TRADE_STAFF_ID, TRADE_DEPART_ID, TRADE_CITY_CODE, " + "TRADE_EPARCHY_CODE, TRADE_REASON_CODE, TOTAL_FEE, REPRINT_FLAG, PRINTED_FEE, "
                + "SPECITEM_PRINTFLAG, PREPRINT_FLAG, REMARK, CANCEL_TAG, POST_TAG, EPARCHY_CODE, " + "CANCEL_TIME, CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE," + " CANCEL_EPARCHY_CODE, CANCEL_REASON_CODE, RSRV_FEE1, RSRV_FEE2, "
                + "RSRV_INFO1, RSRV_INFO2, RSRV_INFO3, RSRV_INFO4, RSRV_INFO5 " + "FROM tf_b_printnotelog t where t.REPRINT_FLAG = '0' ");
        parser.addSQL(" and t.NOTE_NO = :NOTE_NO ");
        parser.addSQL(" and t.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" and t.TRADE_ID = :TRADE_ID ");
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 根据tradeId查询未返销的票据打印信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset queryPrintLogByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_PRINTNOTELOG", "SELECT_PRINTNOTELOG_BY_TRADEID", param);
    }
}
