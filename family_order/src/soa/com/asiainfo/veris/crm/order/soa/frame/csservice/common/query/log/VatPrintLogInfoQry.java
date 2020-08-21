
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.log;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class VatPrintLogInfoQry
{
    /**
     * 发票作废处理
     * 
     * @param param
     * @throws Exception
     */
    public static void deleteVatPrintLog(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" UPDATE TF_B_VATPRINTLOG T ");
        parser.addSQL("    SET T.TICKET_STATE_CODE = :TICKET_STATE_CODE, ");
        parser.addSQL("    	   T.CANCEL_TAG        = '1', "); // 返销标记: 0-正常; 1-收费返销; 2-打印返销
        parser.addSQL("    	   T.REMARK            = T.REMARK || :REMARK ");
        parser.addSQL("  WHERE T.TRADE_ID = :TRADE_ID ");
        parser.addSQL("    AND T.ACCEPT_MONTH = :ACCEPT_MONTH ");

        Dao.executeUpdate(parser, Route.CONN_CRM_CG);
    }

    /**
     * 查询集团可打的增值税台账记录及可打金额
     * 
     * @param custId
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public static IDataset getTaxPrintInfosForGrp(String custId, String startDate, String endDate) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("CUST_ID", custId);
        inparams.put("START_DATE", startDate);
        inparams.put("END_DATE", endDate);

        return Dao.qryByCodeParser("TF_B_VATPRINTLOG", "SEL_INFOS_BY_PRINT", inparams, Route.CONN_CRM_CG);
    }

    /**
     * 插入TF_B_VATPRINTLOG表
     * 
     * @param data
     * @throws Exception
     */
    public static void insertVatPrintLog(IData data) throws Exception
    {
        Dao.insert("TF_B_VATPRINTLOG", data, Route.CONN_CRM_CG);
    }

    /**
     * @param tradeId
     * @param cancelTag
     * @return
     * @throws Exception
     */
    public static IDataset qryHisVatPrintLogByTradeId(String tradeId, String cancelTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        param.put("CANCEL_TAG", cancelTag);

        return Dao.qryByCode("TF_B_VATPRINTLOG", "SEL_BY_TRADEID_CANCELTAG", param, Route.CONN_CRM_CG);
    }

    /**
     * 查询打印日志信息
     * 
     * @param tradeId
     * @param ticketStateCode
     * @param cancelTag
     * @return
     * @throws Exception
     */
    public static IDataset qryVatPrintLogByTradeIdForGrp(String tradeId, String ticketStateCode, String cancelTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        param.put("TICKET_STATE_CODE", ticketStateCode);
        param.put("CANCEL_TAG", cancelTag);

        return Dao.qryByCode("TF_B_VATPRINTLOG", "SEL_BY_TRADEID_STATE_CANCEL_TAG", param, Route.CONN_CRM_CG);
    }

    /**
     * 根据客户标志和发票号查询增值税发票打印日志[可返销]
     * 
     * @param custId
     * @param noteNo
     * @param startDate
     * @param endDate
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryVatPrintLogForGrp(String ticketStateCode, String custId, String noteNo, String startDate, String endDate, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("TICKET_STATE_CODE", ticketStateCode);
        param.put("CUST_ID", custId);
        param.put("NOTE_NO", noteNo);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);

        return Dao.qryByCodeParser("TF_B_VATPRINTLOG", "SEL_BY_CUSTID_NOTENO_DATE", param, pg, Route.CONN_CRM_CG);
    }

}
