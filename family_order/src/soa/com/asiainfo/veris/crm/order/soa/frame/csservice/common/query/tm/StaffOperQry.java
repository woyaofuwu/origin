
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tm;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class StaffOperQry
{

    /**
     * offwork
     * 
     * @param pd
     * @param data
     * @throws Exception
     */
    public static boolean offWorkInfo(String workId) throws Exception
    {
        IData data = new DataMap();
        data.put("WORK_ID", workId);
        data.put("STATE_CODE", "04");
        data.put("OFF_WORK_TIME", SysDateMgr.getSysTime());

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" UPDATE TF_F_STAFFWORK sw");
        parser.addSQL(" SET sw.STATE_CODE = :STATE_CODE");
        parser.addSQL(" , sw.OFF_WORK_TIME = to_date(:OFF_WORK_TIME,'YYYY-MM-DD HH24:MI:SS')");
        parser.addSQL(" , sw.OFF_WORK_TYPE = '1'");
        parser.addSQL(" WHERE sw.WORK_ID = :WORK_ID");

        int a = Dao.executeUpdate(parser, Route.CONN_CRM_CEN);
        if (a > 0)
            return true;
        return false;
    }

    public static IDataset qryStaffWork(String staffId, String stateCode, String onWorkType) throws Exception
    {
        IData data = new DataMap();
        data.put("STAFF_ID", staffId);
        data.put("STATE_CODE", stateCode);
        data.put("ON_WORK_TYPE", onWorkType);
        data.put("ON_WORK_TIME", SysDateMgr.getSysDate());

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT K.WORK_ID,K.STAFF_ID,K.STAFF_NAME,K.STAFF_DEP_ID,K.ON_WORK_TIME,K.OFF_WORK_TIME,K.STATE_CODE,K.ON_WORK_TYPE,K.OFF_WORK_TYPE");
        parser.addSQL("   FROM TF_F_STAFFWORK K");
        parser.addSQL(" WHERE K.STAFF_ID = :STAFF_ID");
        parser.addSQL(" AND K.STATE_CODE = :STATE_CODE");
        parser.addSQL(" AND K.ON_WORK_TYPE = :ON_WORK_TYPE");
        parser.addSQL(" AND trunc(K.ON_WORK_TIME) = to_date( :ON_WORK_TIME,'yyyy-MM-dd')");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset qryStateInfo(String workId, String stateCode, String staffId) throws Exception
    {
        IData data = new DataMap();
        data.put("WORK_ID", workId);
        data.put("STATE_CODE", stateCode);
        data.put("STAFF_ID", staffId);
        data.put("START_TIME", SysDateMgr.getSysDate());

        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT T.STAFFINFO_ID,");
        parser.addSQL(" T.STAFF_ID,");
        parser.addSQL(" T.STAFF_NAME,");
        parser.addSQL(" T.STATE_CODE,");
        parser.addSQL(" T.START_TIME,");
        parser.addSQL(" T.END_TIME,");
        parser.addSQL(" T.BUSY_TYPE_ID,");
        parser.addSQL(" T.WORK_ID");
        parser.addSQL(" FROM TF_F_STATEINFO T");
        parser.addSQL(" WHERE T.WORK_ID = :WORK_ID");
        parser.addSQL(" AND T.STATE_CODE = :STATE_CODE");
        parser.addSQL(" AND T.STAFF_ID = :STAFF_ID");
        parser.addSQL(" AND T.END_TIME IS NULL");
        parser.addSQL(" AND trunc(T.START_TIME) = to_date( :START_TIME,'yyyy-MM-dd')");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);

    }

    /**
     * 查询员签到信息
     * 
     * @param data
     *            查询条件
     * @return 查询员签到信息
     * @throws Exception
     */
    public static IDataset queryStaffWorkInfo(String staffId, String onWorkType) throws Exception
    {
        IData data = new DataMap();
        data.put("STAFF_ID", staffId);
        data.put("ON_WORK_TYPE", onWorkType);
        data.put("ON_WORK_TIME", SysDateMgr.getSysDate());

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT sw.WORK_ID,sw.STAFF_ID,sw.ON_WORK_TYPE,sw.STATE_CODE, sw.OFF_WORK_TYPE");
        parser.addSQL(" FROM TF_F_STAFFWORK sw");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL(" AND sw.STAFF_ID = :STAFF_ID");
        parser.addSQL(" AND sw.ON_WORK_TYPE = :ON_WORK_TYPE");
        parser.addSQL(" AND sw.STATE_CODE <> '04'");
        parser.addSQL(" AND trunc(sw.ON_WORK_TIME) = to_date( :ON_WORK_TIME,'yyyy-MM-dd')");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 根据主键释放示忙
     * 
     * @param pd
     * @param data
     * @throws Exception
     */
    public static void updateShowBusyInfos(String staffInfoId, String stateCode) throws Exception
    {

        IData data = new DataMap();
        data.put("STATE_CODE", stateCode);
        data.put("STAFFINFO_ID", staffInfoId);

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" UPDATE TF_F_STATEINFO t");
        parser.addSQL(" SET t.END_TIME = sysdate,t.state_code = :STATE_CODE");
        parser.addSQL(" WHERE t.STAFFINFO_ID = :STAFFINFO_ID");

        Dao.executeUpdate(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 更新员工工作状态信息，示忙和释放都可共用。
     * 
     * @param pd
     * @param data
     * @throws Exception
     */
    public static void updateStaffWorkInfo(String workId, String stateCode) throws Exception
    {
        IData data = new DataMap();
        data.put("WORK_ID", workId);
        data.put("STATE_CODE", stateCode);

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" UPDATE TF_F_STAFFWORK sw");
        parser.addSQL(" SET sw.STATE_CODE = :STATE_CODE");
        parser.addSQL(" WHERE sw.WORK_ID = :WORK_ID");

        Dao.executeUpdate(parser, Route.CONN_CRM_CEN);
    }

}
