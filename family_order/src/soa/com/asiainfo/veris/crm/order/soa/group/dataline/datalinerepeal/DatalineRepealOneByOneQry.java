
package com.asiainfo.veris.crm.order.soa.group.dataline.datalinerepeal;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class DatalineRepealOneByOneQry
{
	
	/**
     * 根据tradeId把trade表数据搬历史表
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int insertBhTradeByTradeId(String tradeId)
    	throws Exception
    {
    	IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(2000);
	    sql.append(" INSERT INTO TF_BH_TRADE");
	    sql.append("  (TRADE_ID,");
	    sql.append("   ACCEPT_MONTH,");
	    sql.append("   BATCH_ID,");
	    sql.append("   ORDER_ID,");
	    sql.append("   PROD_ORDER_ID,");
	    sql.append("   BPM_ID,");
	    sql.append("   CAMPN_ID,");
	    sql.append("   TRADE_TYPE_CODE,");
	    sql.append("   PRIORITY,");
	    sql.append("   SUBSCRIBE_TYPE,");
	    sql.append("   SUBSCRIBE_STATE,");
	    sql.append("   NEXT_DEAL_TAG,");
	    sql.append("   IN_MODE_CODE,");
	    sql.append("   CUST_ID,");
	    sql.append("   CUST_NAME,");
	    sql.append("   USER_ID,");
	    sql.append("   ACCT_ID,");
	    sql.append("   SERIAL_NUMBER,");
	    sql.append("   NET_TYPE_CODE,");
	    sql.append("   EPARCHY_CODE,");
	    sql.append("   CITY_CODE,");
	    sql.append("   PRODUCT_ID,");
	    sql.append("   BRAND_CODE,");
	    sql.append("   CUST_ID_B,");
	    sql.append("   USER_ID_B,");
	    sql.append("   ACCT_ID_B,");
	    sql.append("   SERIAL_NUMBER_B,");
	    sql.append("   CUST_CONTACT_ID,");
	    sql.append("   SERV_REQ_ID,");
	    sql.append("   INTF_ID,");
	    sql.append("   ACCEPT_DATE,");
	    sql.append("   TRADE_STAFF_ID,");
	    sql.append("   TRADE_DEPART_ID,");
	    sql.append("   TRADE_CITY_CODE,");
	    sql.append("   TRADE_EPARCHY_CODE,");
	    sql.append("   TERM_IP,");
	    sql.append("   OPER_FEE,");
	    sql.append("   FOREGIFT,");
	    sql.append("   ADVANCE_PAY,");
	    sql.append("   INVOICE_NO,");
	    sql.append("   FEE_STATE,");
	    sql.append("   FEE_TIME,");
	    sql.append("   FEE_STAFF_ID,");
	    sql.append("   PROCESS_TAG_SET,");
	    sql.append("   OLCOM_TAG,");
	    sql.append("   FINISH_DATE,");
	    sql.append("   EXEC_TIME,");
	    sql.append("   EXEC_ACTION,");
	    sql.append("   EXEC_RESULT,");
	    sql.append("   EXEC_DESC,");
	    sql.append("   CANCEL_TAG,");
	    sql.append("   CANCEL_DATE,");
	    sql.append("   CANCEL_STAFF_ID,");
	    sql.append("   CANCEL_DEPART_ID,");
	    sql.append("   CANCEL_CITY_CODE,");
	    sql.append("   CANCEL_EPARCHY_CODE,");
	    sql.append("   UPDATE_TIME,");
	    sql.append("   UPDATE_STAFF_ID,");
	    sql.append("   UPDATE_DEPART_ID,");
	    sql.append("   REMARK,");
	    sql.append("   RSRV_STR1,");
	    sql.append("   RSRV_STR2,");
	    sql.append("   RSRV_STR3,");
	    sql.append("   RSRV_STR4,");
	    sql.append("   RSRV_STR5,");
	    sql.append("   RSRV_STR6,");
	    sql.append("   RSRV_STR7,");
	    sql.append("   RSRV_STR8,");
	    sql.append("   RSRV_STR9,");
	    sql.append("   RSRV_STR10,");
	    sql.append("   IS_NEED_HUMANCHECK,");
	    sql.append("   PF_TYPE,");
	    sql.append("   FREE_RESOURCE_TAG,");
	    sql.append("   PF_WAIT)");
	    sql.append("  SELECT TRADE_ID,");
	    sql.append("         ACCEPT_MONTH,");
	    sql.append("         BATCH_ID,");
	    sql.append("         ORDER_ID,");
	    sql.append("         PROD_ORDER_ID,");
	    sql.append("         BPM_ID,");
	    sql.append("         CAMPN_ID,");
	    sql.append("         TRADE_TYPE_CODE,");
	    sql.append("         PRIORITY,");
	    sql.append("         SUBSCRIBE_TYPE,");
	    sql.append("         SUBSCRIBE_STATE,");
	    sql.append("         NEXT_DEAL_TAG,");
	    sql.append("         IN_MODE_CODE,");
	    sql.append("         CUST_ID,");
	    sql.append("         CUST_NAME,");
	    sql.append("         USER_ID,");
	    sql.append("         ACCT_ID,");
	    sql.append("         SERIAL_NUMBER,");
	    sql.append("         NET_TYPE_CODE,");
	    sql.append("         EPARCHY_CODE,");
	    sql.append("         CITY_CODE,");
	    sql.append("         PRODUCT_ID,");
	    sql.append("         BRAND_CODE,");
	    sql.append("         CUST_ID_B,");
	    sql.append("         USER_ID_B,");
	    sql.append("         ACCT_ID_B,");
	    sql.append("         SERIAL_NUMBER_B,");
	    sql.append("         CUST_CONTACT_ID,");
	    sql.append("         SERV_REQ_ID,");
	    sql.append("         INTF_ID,");
	    sql.append("         ACCEPT_DATE,");
	    sql.append("         TRADE_STAFF_ID,");
	    sql.append("         TRADE_DEPART_ID,");
	    sql.append("         TRADE_CITY_CODE,");
	    sql.append("         TRADE_EPARCHY_CODE,");
	    sql.append("         TERM_IP,");
	    sql.append("         OPER_FEE,");
	    sql.append("         FOREGIFT,");
	    sql.append("         ADVANCE_PAY,");
	    sql.append("         INVOICE_NO,");
	    sql.append("         FEE_STATE,");
	    sql.append("         FEE_TIME,");
	    sql.append("         FEE_STAFF_ID,");
	    sql.append("         PROCESS_TAG_SET,");
	    sql.append("         OLCOM_TAG,");
	    sql.append("         FINISH_DATE,");
	    sql.append("         EXEC_TIME,");
	    sql.append("         EXEC_ACTION,");
	    sql.append("         EXEC_RESULT,");
	    sql.append("         EXEC_DESC,");
	    sql.append("         CANCEL_TAG,");
	    sql.append("         CANCEL_DATE,");
	    sql.append("         CANCEL_STAFF_ID,");
	    sql.append("         CANCEL_DEPART_ID,");
	    sql.append("         CANCEL_CITY_CODE,");
	    sql.append("         CANCEL_EPARCHY_CODE,");
	    sql.append("         UPDATE_TIME,");
	    sql.append("         UPDATE_STAFF_ID,");
	    sql.append("         UPDATE_DEPART_ID,");
	    sql.append("         '资管回单无法开通搬历史表-一单多线',");
	    sql.append("         RSRV_STR1,");
	    sql.append("         RSRV_STR2,");
	    sql.append("         RSRV_STR3,");
	    sql.append("         RSRV_STR4,");
	    sql.append("         RSRV_STR5,");
	    sql.append("         RSRV_STR6,");
	    sql.append("         RSRV_STR7,");
	    sql.append("         RSRV_STR8,");
	    sql.append("         RSRV_STR9,");
	    sql.append("         RSRV_STR10,");
	    sql.append("         IS_NEED_HUMANCHECK,");
	    sql.append("         PF_TYPE,");
	    sql.append("         FREE_RESOURCE_TAG,");
	    sql.append("         PF_WAIT");
	    sql.append("    FROM TF_B_TRADE T");
	    sql.append("   WHERE T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))");
	    sql.append("     AND T.TRADE_ID = TO_NUMBER(:TRADE_ID)");
	    return Dao.executeUpdate(sql, param, Route.getJourDb(BizRoute.getRouteId()));
    }
	
    /**
     * 根据tradeId把trade表数据删除
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int deleteBTradeByTradeId(String tradeId)
    	throws Exception
    {
    	IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" DELETE FROM TF_B_TRADE T  ");
	    sql.append("  WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
    }
	
    /**
     * 根据产品实例编号和productNo查询流水号
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryDatalineTradeIdByProductNo(IData param) 
		throws Exception
	{
	    SQLParser parser = new SQLParser(param);
	    parser.addSQL(" SELECT T.TRADE_ID,");
	    parser.addSQL("       T.PRODUCT_NO,");
	    parser.addSQL("       T.USER_ID,");
	    parser.addSQL("       T.INST_ID,");
	    parser.addSQL("       T.SHEET_TYPE,");
	    parser.addSQL("       T.ATTR_CODE,");
	    parser.addSQL("       T.ATTR_VALUE");
	    parser.addSQL("  FROM TF_B_TRADE_DATALINE_ATTR T");
	    parser.addSQL(" WHERE T.ATTR_CODE = 'SERIALNO'");
	    parser.addSQL("   AND T.ATTR_VALUE = :ATTR_VALUE");
	    parser.addSQL("   AND T.PRODUCT_NO = :PRODUCT_NO");
	    return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
	}
}
