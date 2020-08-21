
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

public class TradeCnoteInfoQry
{

    // todo
    // 免填单号日志 shenh
    /*******************************************************************************************************************
     * 备份票据打印信息
     * 
     * @author hank
     */
    // TODO public static void addCnotelog(TradeData td, IData data) throws Exception
    /*
     * { IData printSaveData = new DataMap(); String sql = ""; if (isProvince(PROVINCE.XINJ)) { sql =
     * "insert into tf_b_trade_cnote_info (ACCEPT_DATE,TRADE_EPARCHY_CODE,TRADE_CITY_CODE,TRADE_DEPART_ID,TRADE_STAFF_ID,UPDATE_DEPART_ID,UPDATE_STAFF_ID,UPDATE_TIME,TRADE_ID, ACCEPT_MONTH, NOTE_TYPE, RECEIPT_INFO1, RECEIPT_INFO2, RECEIPT_INFO3, RECEIPT_INFO4, RECEIPT_INFO5,RSRV_TAG1,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5 )"
     * +
     * "values (to_date(:ACCEPT_DATE,'YYYY-MM-DD HH24:MI:SS'),:TRADE_EPARCHY_CODE,:TRADE_CITY_CODE,:TRADE_DEPART_ID,:TRADE_STAFF_ID,:UPDATE_DEPART_ID,:UPDATE_STAFF_ID,sysdate,:TRADE_ID,:ACCEPT_MONTH,:NOTE_TYPE,:RECEIPT_INFO1,:RECEIPT_INFO2,:RECEIPT_INFO3,:RECEIPT_INFO4,:RECEIPT_INFO5,:RSRV_TAG1,:RSRV_STR1,:RSRV_STR2,:RSRV_STR3,:RSRV_STR4,:RSRV_STR5)"
     * ; } if ("".equals(data.getString("TRADE_ID", ""))) { CSAppException.apperr(TradeException.CRM_TRADE_43); }
     * printSaveData.put("ACCEPT_DATE", SysDateMgr.getSysTime()); printSaveData.put("TRADE_EPARCHY_CODE",
     * getVisit().getStaffEparchyCode()); printSaveData.put("TRADE_ID", data.getString("TRADE_ID", ""));
     * printSaveData.put("TRADE_CITY_CODE", getVisit().getCityCode()); printSaveData.put("TRADE_DEPART_ID",
     * getVisit().getDepartId()); printSaveData.put("TRADE_STAFF_ID", getVisit().getStaffId());
     * printSaveData.put("UPDATE_DEPART_ID", getVisit().getDepartId()); printSaveData.put("UPDATE_STAFF_ID",
     * getVisit().getStaffId()); printSaveData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(data.getString("TRADE_ID",
     * ""))); printSaveData.put("NOTE_TYPE", "1"); if (!"".equals(data.getString("CONTENT", ""))) {
     * printSaveData.put("RECEIPT_INFO1", data.getString("CONTENT", "")); } else { printSaveData.put("RECEIPT_INFO1",
     * data.getString("RECEIPT_INFO1", "")); } printSaveData.put("RECEIPT_INFO2", data.getString("RECEIPT_INFO2", ""));
     * printSaveData.put("RECEIPT_INFO3", ""); printSaveData.put("RECEIPT_INFO4", "");
     * printSaveData.put("RECEIPT_INFO5", ""); printSaveData.put("RSRV_TAG1", "0"); printSaveData.put("RSRV_STR1",
     * td.getTradeTypeCode()); printSaveData.put("RSRV_STR2", "0"); printSaveData.put("RSRV_STR3", "0");
     * printSaveData.put("RSRV_STR4", "0");// printSaveData.put("RSRV_STR5", td.getSerialNumber());
     * Dao.executeUpdate(sql, printSaveData); }
     */

}
