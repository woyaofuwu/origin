
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class NewFastAuthConfigQry extends CSBizBean
{

    public static int[] applyAuthFunc(IDataset inputDataset) throws Exception
    {
        return Dao.insert("TD_S_TEMPAUTHTRADETYPE", inputDataset);
    }

    /** 删除选中的可快速授权业务 */
    public static boolean delFastAuth(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        return Dao.delete("TD_S_TEMPAUTHTRADETYPE", param, new String[]
        { "MENU_ID" });
    }

    /**
     * 判断该菜单信息是否存在
     */
    public static IDataset judgeMenuExist(String menuId, String subsysCode) throws Exception
    {
        IData params = new DataMap();
        params.put("MENU_ID", menuId);
        params.put("SUBSYS_CODE", subsysCode);
        SQLParser parser = new SQLParser(params);

        parser.addSQL(" SELECT A.MENU_ID MENU_ID," + "NVL(A.PARENT_MENU_ID, ' ') PARENT_MENU_ID," + "NVL(A.RIGHT_CODE, ' ') RIGHT_CODE," + "NVL(A.SUBSYS_CODE, ' ') SUBSYS_CODE," + "NVL(A.MENU_TITLE, ' ') MENU_TITLE,"
                + "NVL(A.SHOW_ORDER, -1) SHOW_ORDER," + "NVL(A.CLASS_LEVEL, 1) CLASS_LEVEL," + "M.MOD_NAME MOD_NAME,M.MOD_TYPE MOD_TYPE");
        parser.addSQL(" FROM TD_B_SYSTEMGUIMENU A,TD_M_FUNCRIGHT F, TD_S_MODFILE M");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL(" AND A.SUBSYS_CODE = :SUBSYS_CODE");
        parser.addSQL(" AND A.Menu_Id = :MENU_ID");
        parser.addSQL(" AND F.RIGHT_ATTR = '0'");
        parser.addSQL(" AND F.MOD_CODE = M.MOD_CODE");
        parser.addSQL(" AND F.RIGHT_CODE = A.RIGHT_CODE");

        return Dao.qryByParse(parser);
    }

    /**
     * 判断该菜单信息是否可配置
     */
    public static IDataset judgeMenuIsNot(String menuId, String startDate, String endDate) throws Exception
    {
        IData params = new DataMap();
        params.put("MENU_ID", menuId);
        params.put("END_DATE", endDate);
        params.put("START_DATE", startDate);
        SQLParser parser = new SQLParser(params);

        parser.addSQL(" SELECT T.MENU_ID,T.MENU_TITLE,T.URL,T.RIGHT_CODE,T.START_DATE,T.END_DATE FROM TD_S_TEMPAUTHTRADETYPE T");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL(" AND T.MENU_ID = :MENU_ID");
        parser.addSQL(" AND (EXISTS (SELECT 1 FROM TD_S_TEMPAUTHTRADETYPE TT WHERE TO_DATE(:START_DATE, 'YYYY-MM-DD') BETWEEN TT.START_DATE AND TT.END_DATE AND TT.MENU_ID = :MENU_ID)");
        parser.addSQL(" OR EXISTS (SELECT 1 FROM TD_S_TEMPAUTHTRADETYPE TT WHERE TO_DATE(:END_DATE, 'YYYY-MM-DD') BETWEEN TT.START_DATE AND TT.END_DATE AND TT.MENU_ID = :MENU_ID))");

        return Dao.qryByParse(parser);
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

    /** 查询已允许授权的业务 */
    public static IDataset queryAuthTradeType2(int dateFlag, String menuId, String endDate, Pagination page) throws Exception
    {
        IData params = new DataMap();
        params.put("MENU_ID", menuId);
        if (dateFlag == 0)
        { // 有效
            params.put("END_DATE2", endDate);
        }
        else
        { // 1为无效
            params.put("END_DATE1", endDate);
        }
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.MENU_ID,T.MENU_TITLE,T.RIGHT_CODE,T.URL,TO_CHAR(T.START_DATE,'yyyy-mm-dd') START_DATE,"
                + "TO_CHAR(T.END_DATE,'yyyy-mm-dd') END_DATE,T.UPDATE_STAFF_ID,T.UPDATE_DEPART_ID,TO_CHAR(T.UPDATE_TIME,'yyyy-mm-dd') UPDATE_TIME");
        parser.addSQL(" FROM TD_S_TEMPAUTHTRADETYPE T");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL(" AND T.MENU_ID = :MENU_ID");
        parser.addSQL(" AND T.END_DATE <  TO_DATE(:END_DATE1, 'YYYY-MM-DD')");
        parser.addSQL(" AND T.END_DATE >= TO_DATE(:END_DATE2, 'YYYY-MM-DD')");
        return Dao.qryByParse(parser, page);
    }

    /**
     * 取得子系统信息
     */
    public static IDataset queryChildSysRange() throws Exception
    {
        IData params = new DataMap();
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT DECODE(T.SUBSYS_CODE,'CRM','NCM','NCM') SUBSYS_CODE, T.SUBSYS_NAME");
        parser.addSQL(" FROM TD_S_SUBSYS T WHERE 1=1");
        parser.addSQL(" AND T.SUBSYS_CODE IN ('CRM')");

        return Dao.qryByParse(parser);
    }

    /**
     * 查询菜单树
     */
    public static IDataset queryMenus(String subsysCode, String parentMenuId) throws Exception
    {

        IData params = new DataMap();
        SQLParser parser = new SQLParser(params);

        parser.addSQL(" SELECT T.MENU_ID,T.PARENT_MENU_ID,T.RIGHT_CODE,T.SUBSYS_CODE,T.MENU_TITLE,T.SHOW_ORDER,T.CLASS_LEVEL,M.MOD_NAME MOD_NAME,M.MOD_TYPE MOD_TYPE");
        parser.addSQL(" FROM (SELECT A.MENU_ID MENU_ID,NVL(A.PARENT_MENU_ID, ' ') PARENT_MENU_ID," + "NVL(A.RIGHT_CODE, ' ') RIGHT_CODE," + "NVL(A.SUBSYS_CODE, ' ') SUBSYS_CODE," + "NVL(A.MENU_TITLE, ' ') MENU_TITLE,"
                + "NVL(A.SHOW_ORDER, -1) SHOW_ORDER," + "NVL(A.CLASS_LEVEL, 1) CLASS_LEVEL");
        parser.addSQL(" FROM TD_B_SYSTEMGUIMENU A");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL(" AND A.MENU_ID NOT IN (SELECT MENU_ID FROM TD_B_HIDEMENUITEM WHERE STAFF_ID = 'SUPERUSR')");
        parser.addSQL(" AND SUBSYS_CODE = :SUBSYS_CODE");
        parser.addSQL(" START WITH A.PARENT_MENU_ID =:PARENT_MENU_ID");
        parser.addSQL(" CONNECT BY PRIOR A.MENU_ID = A.PARENT_MENU_ID");
        parser.addSQL(" ORDER BY A.SHOW_ORDER) T");
        parser.addSQL(" LEFT JOIN TD_M_FUNCRIGHT F ON T.RIGHT_CODE = F.RIGHT_CODE ");
        parser.addSQL(" LEFT JOIN TD_S_MODFILE M ON F.MOD_CODE = M.MOD_CODE");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 查询员工数据(中测新增)
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryStaffcode(String staffId, String staffName, Pagination page) throws Exception
    {
        IData params = new DataMap();
        params.put("STAFF_ID", staffId);
        params.put("STAFF_NAME", staffName);
        SQLParser parser = new SQLParser(params);
        parser.addSQL("select * from TD_M_STAFF f ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and f.staff_id = :STAFF_ID ");
        parser.addSQL(" and f.staff_name like '%' || :STAFF_NAME ||'%'");
        return Dao.qryByParse(parser, page);
    }

    /**
     * 取得业务子范围
     */
    public static IDataset queryTradeChildRange() throws Exception
    {
        IData params = new DataMap();
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T2.MENU_ID,T2.PARENT_MENU_ID,T2.SUBSYS_CODE,T2.MENU_TITLE,T2.CLASS_LEVEL");
        parser.addSQL(" FROM TD_B_SYSTEMGUIMENU T2");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T2.CLASS_LEVEL = '1'");
        parser.addSQL(" AND T2.SUBSYS_CODE = 'NCM'");
        parser.addSQL(" AND T2.MENU_ID IN ('crm9000','crm8000')");
        return Dao.qryByParse(parser);
    }

    /**
     * 取得业务类型范围
     */
    public static IDataset queryTradeTypeRange() throws Exception
    {
        IData params = new DataMap();
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T2.MENU_ID,T2.PARENT_MENU_ID,T2.SUBSYS_CODE,T2.MENU_TITLE,T2.CLASS_LEVEL");
        parser.addSQL(" FROM TD_B_SYSTEMGUIMENU T2");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T2.CLASS_LEVEL = '2'");
        parser.addSQL(" AND T2.SUBSYS_CODE = 'NCM'");
        parser.addSQL(" AND T2.PARENT_MENU_ID IN ('crm9000','crm8000')");
        return Dao.qryByParse(parser);
    }

    /** 修改选中的可快速授权业务 */
    public static int updateFastAuth(String menuId, String startDate, String endDate, String updateStaffId, String updateDepartId, String updateTime) throws Exception
    {
        IData params = new DataMap();
        params.put("MENU_ID", menuId);
        params.put("START_DATE", startDate);
        params.put("END_DATE", endDate);
        params.put("UPDATE_STAFF_ID", updateStaffId);
        params.put("UPDATE_DEPART_ID", updateDepartId);
        params.put("UPDATE_TIME", updateTime);
        StringBuilder strSql = new StringBuilder();
        strSql.delete(0, strSql.length());
        strSql.append("UPDATE TD_S_TEMPAUTHTRADETYPE T");
        strSql.append(" SET T.START_DATE = TO_DATE(:START_DATE, 'YYYY-MM-DD'),");
        strSql.append(" T.END_DATE         = TO_DATE(:END_DATE, 'YYYY-MM-DD'),");
        strSql.append(" T.UPDATE_STAFF_ID  = :UPDATE_STAFF_ID,");
        strSql.append(" T.UPDATE_DEPART_ID = :UPDATE_DEPART_ID,");
        strSql.append(" T.UPDATE_TIME      = TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD')");
        strSql.append(" WHERE 1=1");
        strSql.append(" AND T.MENU_ID = :MENU_ID");
        return Dao.executeUpdate(strSql, params);
    }
}
