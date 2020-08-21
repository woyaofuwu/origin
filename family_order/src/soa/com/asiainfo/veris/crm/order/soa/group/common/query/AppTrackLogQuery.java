
package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class AppTrackLogQuery
{
    public static void delAppTrackLogs(IData iData) throws Exception
    {
        SQLParser parser = new SQLParser(iData);

        parser.addSQL("DELETE TL_B_APP_TRACK_LOG  ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND LOG_DAY = :LOG_DAY ");
        parser.addSQL("AND STAFF_ID = :STAFF_ID ");
        parser.addSQL("AND MENU_ID LIKE '%'|| :MENU_ID  ||'%' ");
        parser.addSQL("AND PAGE_NAME LIKE '%'|| :PAGE_NAME  ||'%' ");
        parser.addSQL("AND SVC_NAME LIKE '%'|| :SVC_NAME  ||'%' ");
        parser.addSQL("AND FILE_ID = :FILE_ID ");
        parser.addSQL("AND WEB_SID = :WEB_SID ");
        parser.addSQL("AND APP_UUID = :APP_UUID ");
        parser.addSQL("AND TO_CHAR(START_TIME,'yyyy-MM-dd HH24:mi:ss') > TO_CHAR(to_date(:START_TIME,'yyyy-MM-dd HH24:mi:ss'),'yyyy-MM-dd HH24:mi:ss') ");
        parser.addSQL("AND TO_CHAR(START_TIME,'yyyy-MM-dd HH24:mi:ss') < TO_CHAR(to_date(:END_TIME,'yyyy-MM-dd HH24:mi:ss'),'yyyy-MM-dd HH24:mi:ss') ");

        Dao.executeUpdate(parser, Route.CONN_LOG);
    }

    public static IDataset qryAppTrackLogs(IData data, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL("SELECT LOG_DAY, STAFF_ID, MENU_ID, PAGE_NAME, SVC_NAME, ");
        parser.addSQL("TO_CHAR(START_TIME,'yyyy-MM-dd HH24:MI:ss.ff') START_TIME, TO_CHAR(END_TIME,'yyyy-MM-dd HH24:MI:ss.ff') END_TIME, ");
        parser.addSQL("COST, FILE_ID, WEB_SID, APP_UUID, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK ");
        parser.addSQL("FROM TL_B_APP_TRACK_LOG  ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND LOG_DAY = :LOG_DAY ");
        parser.addSQL("AND STAFF_ID = :STAFF_ID ");
        parser.addSQL("AND MENU_ID LIKE '%'|| :MENU_ID  ||'%' ");
        parser.addSQL("AND PAGE_NAME LIKE '%'|| :PAGE_NAME  ||'%' ");
        parser.addSQL("AND SVC_NAME LIKE '%'|| :SVC_NAME  ||'%' ");
        parser.addSQL("AND FILE_ID = :FILE_ID ");
        parser.addSQL("AND WEB_SID = :WEB_SID ");
        parser.addSQL("AND APP_UUID = :APP_UUID ");
        parser.addSQL("AND TO_CHAR(START_TIME,'yyyy-MM-dd HH24:mi:ss') > TO_CHAR(to_date(:START_TIME,'yyyy-MM-dd HH24:mi:ss'),'yyyy-MM-dd HH24:mi:ss') ");
        parser.addSQL("AND TO_CHAR(START_TIME,'yyyy-MM-dd HH24:mi:ss') < TO_CHAR(to_date(:END_TIME,'yyyy-MM-dd HH24:mi:ss'),'yyyy-MM-dd HH24:mi:ss') ");
        parser.addSQL("ORDER BY START_TIME DESC");

        IDataset resultset = Dao.qryByParse(parser, pagination, Route.CONN_LOG);

        return resultset;

    }

}
