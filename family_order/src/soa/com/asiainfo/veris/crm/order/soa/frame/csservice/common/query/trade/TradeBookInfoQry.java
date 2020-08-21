
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeBookInfoQry
{
    public static IDataset getTradeBookQuery(IData param) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_BOOK", "SEL_BOOK_TRADE", param);
    }

    // todo
    /**
     * @param iData
     * @return
     */
    public static IDataset getTradeBooks(String bookTypeCode, String serialNumber, String bookStatus) throws Exception
    {
        IData cond = new DataMap();
        cond.put("BOOK_TYPE_CODE", bookTypeCode);
        cond.put("SERIAL_NUMBER", serialNumber);
        cond.put("BOOK_STATUS", bookStatus);
        return Dao.qryByCode("TF_B_TRADE_BOOK", "SEL_BY_RESCODE", cond);
    }

    public static IDataset getTradeBooksInfo(IData data) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_BOOK", "SEL_BY_ALLNEW", data);
    }

    public static int insertAllData(IData data) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_B_TRADE_BOOK", "INSERT_BY_ALL", data);
    }

    /**
     * @Function: modifyTradeBook
     * @Description: 修改网上预约订单
     * @param: @param serialNumber
     * @param: @param str1
     * @param: @param str2
     * @param: @return
     * @param: @throws Exception
     * @return
     * @throws
     * @version: v1.0.0
     * @author: Administrator
     * @date: 02:27:46 2013-9-21 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-9-21 longtian3 v1.0.0 TODO:
     */
    public static int modifyTradeBook(String serialNumber, String str1, String str2) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("RSRV_STR1", str1);
        param.put("RSRV_STR2", str2);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" UPDATE TF_B_TRADE_BOOK ");
        parser.addSQL(" SET BOOK_STATUS = '1' ");
        parser.addSQL(" WHERE SERIAL_NUMBER =:SERIAL_NUMBER ");
        parser.addSQL("  AND BOOK_TYPE_CODE = '258' ");
        parser.addSQL(" AND RSRV_STR2 =:RSRV_STR2 ");
        parser.addSQL(" AND RSRV_STR1 =:RSRV_STR1 ");
        parser.addSQL(" AND BOOK_STATUS = '0' ");

        return Dao.executeUpdate(parser);
    }

    public static IDataset qryBookInfo(IData param, Pagination page) throws Exception
    {

        String sql = "SELECT T.TRADE_ID,T.SERIAL_NUMBER,T.RSRV_STR1,T.RSRV_STR2,T.RSRV_STR3,T.RSRV_STR4,T.RSRV_STR5,T.RSRV_STR11,T.RSRV_STR12,T.RSRV_STR13,T.RSRV_STR14,T.RSRV_STR8,T.RSRV_STR9,T.BOOK_TYPE,T.BOOK_TYPE_CODE,T.TRADE_DEPART_ID,T.BOOK_STATUS,T.IN_MOD_CODE,T.TRADE_STAFF_ID,T.REMARK,T.RSRV_TAG2,"
                + " TO_CHAR(T.BOOK_DATE, 'YYYY-MM-DD HH24:mi:ss') AS BOOK_DATE,TO_CHAR(T.BOOK_END_DATE, 'YYYY-MM-DD HH24:mi:ss') AS BOOK_END_DATE,"
                + " TO_CHAR(T.RSRV_DATE1, 'YYYY-MM-DD HH24:mi:ss') AS DEAL_DATE,TO_CHAR(T.RSRV_DATE2, 'YYYY-MM-DD HH24:mi:ss') AS ACCEPT_DATE,TO_CHAR(T.RSRV_DATE3, 'YYYY-MM-DD HH24:mi:ss') AS RSRV_DATE3,"
                + " T.PSPT_TYPE_CODE,T.PSPT_ID,T.CUST_NAME,T.CONTACT_PHONE,T.BOOK_PHONE,T.GOODS_ID,T.DOOR_END_DATE,T.TRADE_CITY_CODE,T.TRADE_EPARCHY_CODE FROM TF_B_TRADE_BOOK T WHERE 1 = 1 ";
        SQLParser parser = new SQLParser(param);
        parser.addSQL(sql);
        parser.addSQL(" AND T.BOOK_TYPE_CODE = :BOOK_TYPE_CODE ");
        parser.addSQL(" AND T.TRADE_ID = :TRADE_ID ");
        parser.addSQL(" AND T.ACCEPT_MONTH = :ACCEPT_MONTH  ");
        parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER  ");
        parser.addSQL(" AND T.IN_MOD_CODE = :IN_MOD_CODE  ");
        parser.addSQL(" AND T.BOOK_DATE <= TO_DATE(:BOOK_END_DATE, 'YYYY-MM-DD hh24:mi:ss') ");
        parser.addSQL(" AND T.BOOK_DATE >= TO_DATE(:BOOK_DATE, 'YYYY-MM-DD HH24:mi:ss') ");
        parser.addSQL(" AND T.BOOK_STATUS = :BOOK_STATUS  ");
        parser.addSQL(" AND T.RSRV_STR1 = :CITY_CODE  ");
        parser.addSQL(" ORDER BY T.TRADE_ID DESC,BOOK_DATE DESC");

        return Dao.qryByParse(parser, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryAllBookNewList(IData data) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_BOOK", "SEL_BY_ALLBOOKNEW", data);
    }

    public static IDataset queryExportData(String transType, String busiTypeCode, String serialNumber, String cancelFlag, String startTime, String endDate, String orderId, String dbType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("BOOK_TYPE_CODE", "250");
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("BOOK_DATE", startTime);
        param.put("END_DATE", endDate);
        param.put("RSRV_STR2", busiTypeCode);
        param.put("RSRV_STR7", cancelFlag);
        param.put("RSRV_STR5", transType);
        param.put("RSRV_STR1", orderId);

        // IDataset set = StaticInfoQry.queryStaticValuesByPdataId(dbType, "TRASH_AREA_CODE");
        String routeId = dbType;
        // if (set != null && set.size() > 0)
        // {
        // routeId = set.getData(0).getString("DATA_ID");
        // }
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT TRADE_ID,ACCEPT_MONTH,USER_ID,SERIAL_NUMBER,PSPT_TYPE_CODE,PSPT_ID,CUST_NAME,CONTACT_PHONE,IN_MOD_CODE,BOOK_TYPE_CODE, ");
        parser.addSQL(" TO_CHAR(BOOK_DATE, 'YYYY-MM-DD hh24:mi:ss') BOOK_DATE,BOOK_STATUS,BOOK_PHONE,GOODS_ID, ");
        parser.addSQL(" TO_CHAR(BOOK_END_DATE, 'YYYY-MM-DD hh24:mi:ss') BOOK_END_DATE,TO_CHAR(DOOR_END_DATE, 'YYYY-MM-DD hh24:mi:ss') DOOR_END_DATE, ");
        parser.addSQL(" RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5, ");
        parser.addSQL(" decode(is_number(RSRV_STR6), '1', to_number(RSRV_STR6) / 100, 0) RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10, ");
        parser.addSQL(" RSRV_STR11,RSRV_STR12,RSRV_STR13,RSRV_STR14,RSRV_STR15,RSRV_STR16,RSRV_STR17,RSRV_STR18,RSRV_STR19,RSRV_STR20,RSRV_STR21, ");
        parser.addSQL(" RSRV_STR22,RSRV_STR23,RSRV_STR24,RSRV_STR25,RSRV_STR26,RSRV_STR27,RSRV_STR28,RSRV_STR29,RSRV_STR30, ");
        parser.addSQL(" TO_CHAR(RSRV_DATE1, 'YYYY-MM-DD hh24:mi:ss') RSRV_DATE1,TO_CHAR(RSRV_DATE2, 'YYYY-MM-DD hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL(" TO_CHAR(RSRV_DATE3, 'YYYY-MM-DD hh24:mi:ss') RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,TRADE_STAFF_ID,TRADE_DEPART_ID, ");
        parser.addSQL(" TRADE_CITY_CODE,TRADE_EPARCHY_CODE FROM TF_B_TRADE_BOOK a WHERE BOOK_TYPE_CODE = :BOOK_TYPE_CODE ");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER AND BOOK_DATE >= to_date(:BOOK_DATE, 'YYYY-MM-DD') ");
        parser.addSQL(" AND BOOK_DATE <= to_date(:END_DATE, 'YYYY-MM-DD') + 1 AND RSRV_STR7 = :RSRV_STR7 AND RSRV_STR2 = :RSRV_STR2 ");
        parser.addSQL(" AND RSRV_STR5 = :RSRV_STR5 AND RSRV_STR5 != '4' AND BOOK_STATUS in ('0', '2') ");
        parser.addSQL(" AND TRADE_ID = (SELECT MAX(TRADE_ID) FROM TF_B_TRADE_BOOK B WHERE BOOK_TYPE_CODE = :BOOK_TYPE_CODE AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND BOOK_DATE >= to_date(:BOOK_DATE, 'YYYY-MM-DD') AND BOOK_DATE <= to_date(:END_DATE, 'YYYY-MM-DD') + 1 ");
        parser.addSQL(" AND RSRV_STR7 = :RSRV_STR7 AND RSRV_STR2 = :RSRV_STR2 AND RSRV_STR5 = :RSRV_STR5 ");
        parser.addSQL(" AND RSRV_STR1 = :RSRV_STR1 AND BOOK_STATUS in ('0', '2') AND A.RSRV_STR1 = B.RSRV_STR1) ");
        parser.addSQL(" ORDER BY BOOK_DATE DESC ");
        return Dao.qryByParse(parser, page, routeId);
    }

    public static IDataset queryListByCodeCodeParser(IData param, Pagination page) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_BOOK", "SEL_BY_ALLBOOK", param, page);
    }

    public static IDataset queryTradeBoos243(String tradeId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_BOOK", "SEL_BOOK_TRADE", cond);
    }

    public static int updateStatus(IData data) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_B_TRADE_BOOK", "UPDATE_STATUS", data);
    }

    public static int updateStatusNew(IData data) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_B_TRADE_BOOK", "UPDATE_STATUSNEW", data);
    }
}
