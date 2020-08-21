
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ctt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CttInfoQry
{
    public static IDataset checkLPZSCountInfo(IData param, String RouteId) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("  select *  from TF_F_USER_OTHER t where 1=1 ");
        parser.addSQL("    AND END_DATE > SYSDATE ");
        parser.addSQL("    AND t.rsrv_value_code = 'LPZS' ");
        parser.addSQL("    AND t.RSRV_VALUE = :SERIAL_NUMBER ");
        parser.addSQL("    AND t.RSRV_STR1 = :RSRV_STR1 ");
        parser.addSQL("    AND t.RSRV_STR2 = :RSRV_STR2 ");

        return Dao.qryByParse(parser, RouteId);
    }

    public static IDataset checkLPZSInfo(IData param, String RouteId) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("  select *  from TF_F_USER_OTHER t where ");
        parser.addSQL("    t.INST_ID = :INST_ID ");
        parser.addSQL("    AND t.USER_ID = :USER_ID ");
        parser.addSQL("    AND END_DATE > SYSDATE ");
        parser.addSQL("    AND t.rsrv_value_code = 'LPZS' ");
        parser.addSQL("    AND t.rsrv_str6 in ('1') ");
        return Dao.qryByParse(parser, RouteId);
    }

    public static IDataset getBundleChargesTradeInfo(IData param, String RouteId) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("  SELECT TO_CHAR(a.trade_id) trade_id,a.trade_eparchy_code,ORDER_ID  FROM tf_b_trade a ");
        parser.addSQL("  WHERE a.trade_type_code = :TRADE_TYPE_CODE ");
        parser.addSQL("  AND a.subscribe_type = '1' ");
        parser.addSQL("  AND a.exec_time > SYSDATE ");
        parser.addSQL("  AND a.RSRV_STR1 = :RSRV_STR1 ");
        parser.addSQL("  AND a.RSRV_STR10 = :RSRV_STR10 ");
        return Dao.qryByParse(parser, RouteId);
    }

    /**
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getTietTradeLog(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT ACCEPT_MONTH,SOURCE_SERIALNUMBER,TO_CHAR(CHARGE_ID) CHARGE_ID,TO_CHAR(ACCEPT_TIME, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME,");
        parser.addSQL("CHANNEL_TRADE_ID,TO_NUMBER(TRADE_TYPE_CODE) TRADE_TYPE_CODE,TRADE_DEPART_ID,TRADE_STAFF_ID,TRADE_EPARCHY_CODE,TRADE_CITY_CODE,CANCEL_TAG,");
        parser.addSQL("RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,TO_CHAR(PREVALUED1, 'yyyy-mm-dd hh24:mi:ss') PREVALUED1,");
        parser.addSQL("TO_CHAR(PREVALUED2, 'yyyy-mm-dd hh24:mi:ss') PREVALUED2,TO_CHAR(PREVALUED3, 'yyyy-mm-dd hh24:mi:ss') PREVALUED3,PREVALUEC1,PREVALUEC2");
        parser.addSQL("   FROM TL_B_TIETLOG T WHERE T.SOURCE_SERIALNUMBER = :SOURCE_SERIALNUMBER AND T.TRADE_TYPE_CODE = :TRADE_TYPE_CODE ");
        parser.addSQL(" AND T.CANCEL_TAG = '0' AND T.PREVALUED1 IS NULL   AND T.ACCEPT_TIME = (SELECT MAX(L.ACCEPT_TIME) FROM TL_B_TIETLOG L ");
        parser.addSQL(" WHERE L.SOURCE_SERIALNUMBER = :SOURCE_SERIALNUMBER  AND L.TRADE_TYPE_CODE = :TRADE_TYPE_CODE) ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset getTradeHInfo(IData param, String RouteId) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("  SELECT TO_CHAR(a.trade_id) trade_id,a.trade_eparchy_code,RSRV_STR3  FROM tf_bh_trade a ");
        parser.addSQL("  WHERE a.trade_type_code = :TRADE_TYPE_CODE ");
        parser.addSQL("  AND a.SERIAL_NUMBER = :SERIAL_NUMBER ");
        return Dao.qryByParse(parser, RouteId);
    }

    public static IDataset getTradeInfo(IData param, String RouteId) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("  SELECT TO_CHAR(a.trade_id) trade_id,a.trade_eparchy_code,RSRV_STR3  FROM tf_b_trade a ");
        parser.addSQL("  WHERE a.trade_type_code = :TRADE_TYPE_CODE ");
        parser.addSQL("  AND a.SERIAL_NUMBER = :SERIAL_NUMBER ");
        return Dao.qryByParse(parser, RouteId);
    }

    public static IDataset getTradeOtherInfo(IData param, String RouteId) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("  select *  from TF_B_TRADE_OTHER t where ");
        parser.addSQL("    t.trade_id = :TRADE_ID ");
        return Dao.qryByParse(parser, RouteId);
    }

}
