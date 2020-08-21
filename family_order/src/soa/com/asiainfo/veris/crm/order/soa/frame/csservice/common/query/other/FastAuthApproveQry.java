
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.statement.Parameter;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class FastAuthApproveQry extends CSBizBean
{

    /** 修改授权申请 */
    public static void ApproveFastAuth(IDataset param) throws Exception
    {
        StringBuilder strSql = new StringBuilder();
        strSql.delete(0, strSql.length());
        strSql.append("UPDATE TF_B_TEMPAUTH_LOG T SET T.AWS_STATE = ?,T.AWS_STAFF_ID = ?,T.AWS_DEPART_ID = ?,T.AWS_DATE = TO_DATE(?, 'YYYY-MM-DD') WHERE 1=1 AND T.ASK_ID = ?");
        Parameter[] params = new Parameter[param.size()];
        for (int i = 0; i < params.length; i++)
        {
            /** 构造绑定对象，按顺序绑定参数值 */
            params[i] = new Parameter();
            params[i].add(((IData) param.get(i)).getString("AWS_STATE"));
            params[i].add(((IData) param.get(i)).getString("AWS_STAFF_ID"));
            params[i].add(((IData) param.get(i)).getString("AWS_DEPART_ID"));
            params[i].add(((IData) param.get(i)).getString("AWS_DATE"));
            params[i].add(((IData) param.get(i)).getString("ASK_ID"));
        }
        Dao.executeBatch(strSql, params, Route.CONN_CRM_CEN);
    }

    public static IDataset getSerialInfo(String askId) throws Exception
    {
        IData params = new DataMap();
        params.put("ASK_ID", askId);
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.ASK_STAFF_ID,T.ASK_SERIAL,T.MENU_TITLE ");
        parser.addSQL(" FROM TF_B_TEMPAUTH_LOG T ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T.ASK_ID = :ASK_ID");
        return Dao.qryByParse(parser);
    }

    public static int insertPwd(String askId, String pwd) throws Exception
    {
        IData params = new DataMap();
        StringBuilder strSql = new StringBuilder();
        params.put("ASK_ID", askId);
        params.put("PWD", pwd);
        strSql.delete(0, strSql.length());
        strSql.append("UPDATE TF_B_TEMPAUTH_LOG SET RSRV_STR3=:PWD WHERE ASK_ID=:ASK_ID ");
        return Dao.executeUpdate(strSql, params, Route.CONN_CRM_CEN);
    }

    /**
     * 查询出已申请的快速授权业务
     * 
     * @param 人员ID
     * @param 审核状态
     */
    public static IDataset queryApplyTrade(String menuId, String askStaffId, String askDepartId, String awsState, String askStartDate, String askEndDate, Pagination page) throws Exception
    {
        IData params = new DataMap();
        params.put("MENU_ID", menuId);
        params.put("ASK_STAFF_ID", askStaffId);
        params.put("ASK_DEPART_ID", askDepartId);
        params.put("AWS_STATE", awsState);
        params.put("ASK_START_DATE", askStartDate);
        params.put("ASK_END_DATE", askEndDate);
        SQLParser parser = new SQLParser(params);
        parser
                .addSQL(" SELECT T.ASK_ID,T.MENU_ID,T.MENU_TITLE,T.ASK_STAFF_ID,T.ASK_DEPART_ID,to_char(T.ASK_TIME,'yyyy-mm-dd') ASK_TIME,T.ASK_NUM,to_char(T.ASK_START_DATE,'yyyy-mm-dd') ASK_START_DATE,to_char(T.ASK_END_DATE,'yyyy-mm-dd') ASK_END_DATE,T.AWS_STATE,T.AWS_STAFF_ID,T.AWS_DEPART_ID,T.AWS_DATE,T.RSRV_STR1 ");
        parser.addSQL(" FROM TF_B_TEMPAUTH_LOG T ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T.MENU_ID = :MENU_ID");
        // parser.addSQL(" AND T.ASK_STAFF_ID = :ASK_STAFF_ID");
        // parser.addSQL(" AND T.ASK_DEPART_ID = :ASK_DEPART_ID");
        parser.addSQL(" AND T.AWS_STATE = :AWS_STATE");
        // parser.addSQL(" AND T.AWS_STAFF_ID = :AWS_STAFF_ID ");
        parser.addSQL(" AND T.ASK_START_DATE >= TO_DATE(:ASK_START_DATE, 'YYYY-MM-DD')");
        parser.addSQL(" AND T.ASK_END_DATE <= TO_DATE(:ASK_END_DATE, 'YYYY-MM-DD')");
        parser.addSQL(" AND T.RSRV_STR2 = '0'");
        return Dao.qryByParse(parser, page);
    }

    /** 查询已允许授权的业务 */
    public static IDataset queryAuthTradeType(String menuId, String nowDate, String startDate, String endDate, Pagination page) throws Exception
    {
        IData params = new DataMap();
        params.put("MENU_ID", menuId);
        params.put("NOW_DATE", nowDate);
        params.put("START_DATE", startDate);
        params.put("END_DATE", endDate);
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.MENU_ID,T.MENU_TITLE,T.RIGHT_CODE,T.URL,TO_CHAR(T.START_DATE,'yyyy-mm-dd') START_DATE,"
                + "TO_CHAR(T.END_DATE,'yyyy-mm-dd') END_DATE,T.UPDATE_STAFF_ID,T.UPDATE_DEPART_ID,TO_CHAR(T.UPDATE_TIME,'yyyy-mm-dd') UPDATE_TIME,RSRV_STR2");
        parser.addSQL(" FROM TD_S_TEMPAUTHTRADETYPE T");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL(" AND T.MENU_ID = :MENU_ID");
        parser.addSQL(" AND T.START_DATE <= TO_DATE(:NOW_DATE, 'YYYY-MM-DD')");
        parser.addSQL(" AND T.END_DATE >= TO_DATE(:NOW_DATE, 'YYYY-MM-DD')");
        parser.addSQL(" AND (EXISTS (SELECT 1 FROM TD_S_TEMPAUTHTRADETYPE TT WHERE TO_DATE(:START_DATE, 'YYYY-MM-DD') between TT.START_DATE AND TT.END_DATE )");
        parser.addSQL(" OR EXISTS (SELECT 1 FROM TD_S_TEMPAUTHTRADETYPE TT WHERE TO_DATE(:END_DATE, 'YYYY-MM-DD') between TT.START_DATE AND TT.END_DATE )");
        parser.addSQL(" OR EXISTS (SELECT 1 FROM TD_S_TEMPAUTHTRADETYPE TT WHERE TT.START_DATE >= TO_DATE(:START_DATE, 'YYYY-MM-DD') AND TT.END_DATE <= TO_DATE(:END_DATE, 'YYYY-MM-DD')))");
        return Dao.qryByParse(parser, page);
    }

    public static IDataset queryCommByAttr(String subsysCode, String paramAttr, String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.put("SUBSYS_CODE", subsysCode);
        params.put("PARAM_ATTR", paramAttr);
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_ONLY_BY_ATTR_ORDERED", params, Route.CONN_CRM_CEN);
    }

    public static IDataset queryCommByAttrCode(String subsysCode, String paramAttr, String paramCode, String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.put("SUBSYS_CODE", subsysCode);
        params.put("PARAM_ATTR", paramAttr);
        params.put("PARAM_CODE", paramCode);
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", params, Route.CONN_CRM_CEN);
    }

    /**
     * 发送通知
     */
    public static int[] sendMsgToCust(IDataset param, String routeEparchyCode) throws Exception
    {

        return Dao.executeBatchByCodeCode("TI_O_SMS", "FAST_AUTH_XJ", param, routeEparchyCode);
    }
}
