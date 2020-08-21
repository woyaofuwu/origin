
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ticket;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class TicketInfoQry
{

    /**
     * @Description 获取可冲红发票
     * @param tradeId
     * @param acceptMonth
     * @param acceptTime
     * @param tradeStaffId
     * @param ticketId
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset qryChReceipt(String tradeId, String serialNumber, String tradeStaffId, String acceptMonth, String acceptTime, String ticketId) throws Exception
    {
        IData qryParam = new DataMap();
        qryParam.put("TRADE_ID", tradeId);
        qryParam.put("SERIAL_NUMBER", serialNumber);
        qryParam.put("TRADE_STAFF_ID", tradeStaffId);
        qryParam.put("ACCEPT_MONTH", acceptMonth);
        qryParam.put("ACCEPT_TIME", acceptTime);
        qryParam.put("TICKET_ID", ticketId);
        return Dao.qryByCodeParser("TF_B_TICKET", "SEL_CH_RECEIPT", qryParam, Route.getJourDb());
    }

    /**
     * @Description 获取已经打印发票
     * @param serialNumber
     * @param tradeId
     * @param acceptMonth
     * @param acceptTime
     * @param tradeStaffId
     * @param ticketId
     * @return
     * @throws Exception
     */
    public static IDataset qryPrintedReceipts(String tradeId, String serialNumber, String tradeStaffId) throws Exception
    {
        IData qryParam = new DataMap();
        qryParam.put("TRADE_ID", tradeId);
        qryParam.put("SERIAL_NUMBER", serialNumber);
        qryParam.put("TRADE_STAFF_ID", tradeStaffId);
        return Dao.qryByCodeParser("TF_B_TICKET", "SEL_PRINT_RECEIPT", qryParam, Route.getJourDb());
    }
    
    /**
     * 获取已打印的电子发票
     */
    public static IDataset queryPrintPDFLogByTradeIdSerialNumber(String tradeId, String serialNumber, String tradeStaffId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_STAFF_ID", tradeStaffId);
        return Dao.qryByCodeParser("TF_B_PRINTPDF_LOG", "SELECT_PRINTPDFLOG_BY_TRADEIDSERIAL", param, Route.getJourDb());
    }

    /**
     * @Description 根据PRINT_ID获取发票记录
     * @param printId
     * @return
     * @throws Exception
     */
    public static IDataset qryReceiptInfo(String printId) throws Exception
    {
        IData qryParam = new DataMap();
        qryParam.put("PRINT_ID", printId);
        return Dao.qryByCodeParser("TF_B_TICKET", "SEL_BY_PK", qryParam, Route.getJourDb());
    }

    /**
     * @Description 获取可作废发票记录
     * @param serialNumber
     * @param tradeId
     * @param acceptMonth
     * @param acceptTime
     * @param tradeStaffId
     * @param ticketId
     * @return
     * @throws Exception
     */
    public static IDataset qryZfReceipt(String serialNumber, String tradeId, String acceptMonth, String acceptTime, String tradeStaffId, String ticketId) throws Exception
    {
        IData qryParam = new DataMap();
        qryParam.put("SERIAL_NUMBER", serialNumber);
        qryParam.put("TRADE_ID", tradeId);
        qryParam.put("ACCEPT_MONTH", acceptMonth);
        qryParam.put("ACCEPT_TIME", acceptTime);
        qryParam.put("TRADE_STAFF_ID", tradeStaffId);
        qryParam.put("TICKET_ID", ticketId);
        return Dao.qryByCodeParser("TF_B_TICKET", "SEL_ZF_RECEIPT", qryParam, Route.getJourDb());
    }

    /**
     * 获取票据信息
     * 
     * @param tradeId
     * @param refundTag
     * @return
     * @throws Exception
     */
    public static IDataset queryTicketInfos(String tradeId, String staffId) throws Exception
    {
        IData inData = new DataMap();
        inData.put("TRADE_ID", tradeId);
        inData.put("TRADE_STAFF_ID", staffId);
        return Dao.qryByCodeParser("TF_B_TICKET", "SEL_BY_TRADEID_STAFFID", inData, Route.getJourDb());
    }
    
    /**
     * @Description 获取业务当前打印的有效票据
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeTickets(String tradeId) throws Exception{
        IData qryParam = new DataMap();
        qryParam.put("TRADE_ID", tradeId);
//        return Dao.qryByCodeParser("TF_B_TICKET", "SEL_TICKETS_BY_TRADEID", qryParam);
        return Dao.qryByCodeParser("TF_B_TICKET", "SEL_TICKETS_BY_TRADEID", qryParam,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    /**
     * @Description 获取业务当前打印的有效发票
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeReceipt(String tradeId) throws Exception{
        IData qryParam = new DataMap();
        qryParam.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TICKET", "SEL_RECEIPT_BY_TRADE", qryParam,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    /**
     * @Description 获取业务是否有打印冲红发票
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset qryPrintedCHReceipt(String tradeId) throws Exception{
        IData qryParam = new DataMap();
        qryParam.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TICKET", "SEL_PRINTED_CH_RECEIPT", qryParam,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    /**
     * @Description 获取业务收据是否已经打印补打过发票
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset qryPrintedSJReceipt(String tradeId) throws Exception{
        IData qryParam = new DataMap();
        qryParam.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TICKET", "SEL_SJ_RECEIPT", qryParam, Route.getJourDb());
    }

}
