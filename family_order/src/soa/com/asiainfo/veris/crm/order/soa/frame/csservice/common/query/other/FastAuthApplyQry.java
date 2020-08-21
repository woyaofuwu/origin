
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.biz.bean.org.OrgFactory;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqTradeId;

public class FastAuthApplyQry extends CSBizBean
{

    public static void applyFastAuth(String askId, String menuId, String menuTitle, String askStaffId, String askDepartId, String askTime, String askNum, String askStartDate, String askEndDate, String awsState, String awsStaffId, String awsDepartId,
            String awsDate, String askSerial, String applyRemark, String rsrvStr2) throws Exception
    {
        IData param = new DataMap();
        param.put("ASK_ID", askId);
        param.put("MENU_ID", menuId);
        param.put("MENU_TITLE", menuTitle);
        param.put("ASK_STAFF_ID", askStaffId);
        param.put("ASK_DEPART_ID", askDepartId);
        param.put("ASK_TIME", askTime);
        param.put("ASK_NUM", askNum);
        param.put("ASK_START_DATE", askStartDate);
        param.put("ASK_END_DATE", askEndDate);
        param.put("AWS_STATE", awsState);
        param.put("AWS_STAFF_ID", awsStaffId);
        param.put("AWS_DEPART_ID", awsDepartId);
        param.put("AWS_DATE", awsDate);
        param.put("ASK_SERIAL", askSerial);
        param.put("APPLY_REMARK", applyRemark);
        param.put("RSRV_STR2", rsrvStr2);
        StringBuilder strSql = new StringBuilder();
        strSql.delete(0, strSql.length());
        strSql
                .append(" INSERT INTO TF_B_TEMPAUTH_LOG T(T.ASK_ID,T.MENU_ID,T.MENU_TITLE,T.ASK_STAFF_ID,T.ASK_DEPART_ID,T.ASK_TIME,T.ASK_NUM,T.ASK_START_DATE,T.ASK_END_DATE,T.AWS_STATE,T.AWS_STAFF_ID,T.AWS_DEPART_ID,T.AWS_DATE,T.ASK_SERIAL,T.RSRV_STR1,T.RSRV_STR2)");
        strSql
                .append(" VALUES(TO_NUMBER(:ASK_ID),:MENU_ID,:MENU_TITLE,:ASK_STAFF_ID,:ASK_DEPART_ID,TO_DATE(:ASK_TIME,'YYYY-MM-DD'),TO_NUMBER(:ASK_NUM),TO_DATE(:ASK_START_DATE,'YYYY-MM-DD'),TO_DATE(:ASK_END_DATE,'YYYY-MM-DD'),:AWS_STATE,:AWS_STAFF_ID,:AWS_DEPART_ID,TO_DATE(:AWS_DATE,'YYYY-MM-DD'),TO_NUMBER(:ASK_SERIAL),:APPLY_REMARK,:RSRV_STR2)");
        Dao.executeUpdate(strSql, param);
    }

    public static void delAuthTimes(String askId) throws Exception
    {
        IData param = new DataMap();
        param.put("ASK_ID", askId);
        StringBuilder strSql = new StringBuilder();
        strSql.delete(0, strSql.length());
        strSql.append(" UPDATE TF_B_TEMPAUTH_LOG T SET T.ASK_NUM = T.ASK_NUM - 1 WHERE T.ASK_ID = :ASK_ID");
        Dao.executeUpdate(strSql, param, Route.CONN_CRM_CEN);

    }

    /**
     * get area grant by deptId
     * 
     * @param dept_id
     * @return
     * @throws Exception
     */
    public static IData getAreaGrantByDeptId(String dept_id) throws Exception
    {
        String tmp_dept = dept_id == null || "".equals(dept_id) ? getVisit().getStaffId() : dept_id;
        IData params = new DataMap();
        params.put("DEPART_ID", tmp_dept);
        SQLParser parser = new SQLParser(params);
        parser.addSQL("select * from TD_M_DEPART where DEPART_ID=:DEPART_ID and VALIDFLAG=0");
        IDataset ds = Dao.qryByParse(parser);
        return ds == null || ds.size() == 0 ? null : ds.getData(0);
    }

    public static String getTradeID(String eparchyCode) throws Exception
    {
        return Dao.getSequence(SeqTradeId.class, eparchyCode);

    }

    /** 判断是否可以申请该业务之 判断与申请过的同一笔授权的时间是否冲突 */
    public static IDataset judgeIsNotApply(String askStaffId, String askDepartId, String rsrvStr2, String startDate, String endDate, String menuId) throws Exception
    {
        IData params = new DataMap();
        params.put("ASK_STAFF_ID", askStaffId);
        params.put("ASK_DEPART_ID", askDepartId);
        params.put("RSRV_STR2", rsrvStr2);
        params.put("ASK_START_DATE", startDate);
        params.put("ASK_END_DATE", endDate);
        params.put("MENU_ID", menuId);
        SQLParser parser = new SQLParser(params);

        parser
                .addSQL(" SELECT T.ASK_ID,T.MENU_ID,T.ASK_STAFF_ID,T.ASK_DEPART_ID,to_char(T.ASK_TIME,'yyyy-mm-dd') ASK_TIME,T.ASK_NUM,to_char(T.ASK_START_DATE,'yyyy-mm-dd') ASK_START_DATE,to_char(T.ASK_END_DATE,'yyyy-mm-dd') ASK_END_DATE,T.AWS_STATE,T.AWS_STAFF_ID,T.AWS_DEPART_ID,T.AWS_DATE ");
        parser.addSQL(" FROM TF_B_TEMPAUTH_LOG T ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T.MENU_ID = :MENU_ID");
        parser.addSQL(" AND T.ASK_STAFF_ID = :ASK_STAFF_ID");
        parser.addSQL(" AND T.ASK_DEPART_ID = :ASK_DEPART_ID");
        parser.addSQL(" AND T.AWS_STATE <> '3'");
        parser.addSQL(" AND T.ASK_NUM <> '0'");
        parser.addSQL(" AND RSRV_STR2 = :RSRV_STR2"); // 添加新增删除条件
        parser.addSQL(" AND (EXISTS (SELECT 1 FROM TF_B_TEMPAUTH_LOG TT WHERE TT.ASK_START_DATE <= TO_DATE(:ASK_START_DATE, 'YYYY-MM-DD') AND TT.ASK_END_DATE >= TO_DATE(:ASK_START_DATE, 'YYYY-MM-DD'))");
        parser.addSQL(" OR EXISTS (SELECT 1 FROM TF_B_TEMPAUTH_LOG TT WHERE TT.ASK_START_DATE <= TO_DATE(:ASK_END_DATE, 'YYYY-MM-DD') AND TT.ASK_END_DATE >= TO_DATE(:ASK_END_DATE, 'YYYY-MM-DD'))");
        parser.addSQL(" OR EXISTS (SELECT 1 FROM TF_B_TEMPAUTH_LOG TT WHERE TT.ASK_START_DATE >= TO_DATE(:ASK_START_DATE, 'YYYY-MM-DD') AND TT.ASK_END_DATE <= TO_DATE(:ASK_END_DATE, 'YYYY-MM-DD')))");
        return Dao.qryByParse(parser);
    }

    /** 判断是否可以申请该业务之 申请有效期是否在该菜单可使用的效期内 */
    public static IDataset judgeIsNotInApply(String rsrvStr2, String startDate, String endDate, String menuId) throws Exception
    {
        IData params = new DataMap();
        params.put("RSRV_STR2", rsrvStr2);
        params.put("START_DATE", startDate);
        params.put("END_DATE", endDate);
        params.put("MENU_ID", menuId);
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.MENU_ID,T.MENU_TITLE,T.RIGHT_CODE,T.URL,TO_CHAR(T.START_DATE,'yyyy-mm-dd') START_DATE,"
                + "TO_CHAR(T.END_DATE,'yyyy-mm-dd') END_DATE,T.UPDATE_STAFF_ID,T.UPDATE_DEPART_ID,TO_CHAR(T.UPDATE_TIME,'yyyy-mm-dd') UPDATE_TIME");
        parser.addSQL(" FROM TD_S_TEMPAUTHTRADETYPE T");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL(" AND T.MENU_ID = :MENU_ID");
        parser.addSQL(" AND TO_DATE(:START_DATE, 'YYYY-MM-DD') between T.START_DATE AND T.END_DATE ");
        parser.addSQL(" AND TO_DATE(:END_DATE, 'YYYY-MM-DD') between T.START_DATE AND T.END_DATE ");
        return Dao.qryByParse(parser);

    }

    /**
     * 查询出已申请的快速授权业务
     * 
     * @param 人员ID
     * @param 审核状态
     */
    public static IDataset queryApplyTrade(String menuId, String askStaffId, String askDepartId, String awsState, String askStartDate, String askEndDate, String awsStaffId, Pagination page) throws Exception
    {
        IData params = new DataMap();
        params.put("MENU_ID", menuId);
        params.put("ASK_STAFF_ID", askStaffId);
        params.put("ASK_DEPART_ID", askDepartId);
        params.put("AWS_STATE", awsState);
        params.put("ASK_START_DATE", askStartDate);
        params.put("ASK_END_DATE", askEndDate);
        params.put("AWS_STAFF_ID", awsStaffId);
        SQLParser parser = new SQLParser(params);
        parser
                .addSQL(" SELECT T.ASK_ID,T.MENU_ID,T.MENU_TITLE,T.ASK_STAFF_ID,T.ASK_DEPART_ID,to_char(T.ASK_TIME,'yyyy-mm-dd') ASK_TIME,T.ASK_NUM,to_char(T.ASK_START_DATE,'yyyy-mm-dd') ASK_START_DATE,to_char(T.ASK_END_DATE,'yyyy-mm-dd') ASK_END_DATE,T.AWS_STATE,T.AWS_STAFF_ID,T.AWS_DEPART_ID,T.AWS_DATE,T.RSRV_STR1 ");
        parser.addSQL(" FROM TF_B_TEMPAUTH_LOG T ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T.MENU_ID = :MENU_ID");
        parser.addSQL(" AND T.ASK_STAFF_ID = :ASK_STAFF_ID");
        parser.addSQL(" AND T.ASK_DEPART_ID = :ASK_DEPART_ID");
        parser.addSQL(" AND T.AWS_STATE = :AWS_STATE");
        parser.addSQL(" AND T.AWS_STAFF_ID = :AWS_STAFF_ID ");
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

    public static IDataset queryPwd(String askId, String pwd) throws Exception
    {
        IData params = new DataMap();
        params.put("ASK_ID", askId);
        params.put("PWD", pwd);
        return Dao.qryByCode("TF_B_TEMPAUTH_LOG", "SEL_PWD_BY_ID", params);
    }

    /**
     * query staffs
     * 
     * @param param
     * @param pagination
     * @return IDataset
     * @throws Exception
     */
    public static IDataset queryStaffs(String staffId, String serialNumber, String staffName, String sex, boolean flag, String area_code, String dept_id, String need_stafftag, String staff_tags, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("SEX", sex);
        param.put("STAFF_NAME", staffName);
        SQLParser parser = new SQLParser(param);

        parser.addSQL("select staff.STAFF_ID, staff.STAFF_NAME, staff.SERIAL_NUMBER, staff.SEX, dept.DEPART_ID, dept.AREA_CODE CITY_CODE, dept.RSVALUE2 EPARCHY_CODE from TD_M_STAFF staff, TD_M_DEPART dept");
        parser.addSQL(" where staff.DEPART_ID = dept.DEPART_ID");
        parser.addSQL(" and staff.DIMISSION_TAG = '0'");
        if (dept_id == null || "".equals(dept_id))
        {
            if (flag)
            {
                IData root_area = getAreaGrantByDeptId(getVisit().getDepartId());
                if (root_area != null && "".equals(root_area.getString("RSVALUE3", "")))
                {
                    param.put("_AREA_FRAME_1", StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_FRAME", root_area.getString("RSVALUE3")));
                    parser.addSQL(" and exists (select comarea.AREA_CODE from TD_M_AREA comarea where comarea.AREA_CODE = :_AREA_FRAME_1)");
                }
            }
            else
            {
                parser.addSQL(OrgFactory.getAreaSqlByOrg(getVisit(), param, area_code, "dept.AREA_CODE"));
            }
        }
        else
        {
            parser.addSQL(OrgFactory.getDepartSqlByFrame(getVisit(), param, dept_id, "dept.DEPART_FRAME"));
        }

        if ("true".equals(need_stafftag) && !"".equals(staff_tags))
        {
            String[] tags = staff_tags.split("\\|");
            String rsrv_tag1 = "";
            for (int i = 0; i < tags.length; i++)
            {
                rsrv_tag1 = rsrv_tag1 + "'" + tags[i] + "'";
                if (i + 1 < tags.length)
                {
                    rsrv_tag1 = rsrv_tag1 + ",";
                }
            }
            parser.addSQL(" and staff.RSRV_TAG1 in (" + rsrv_tag1 + ")");
        }

        parser.addSQL(" and staff.STAFF_NAME like '%' || :STAFF_NAME || '%'");
        parser.addSQL(" and staff.STAFF_ID = :STAFF_ID");
        parser.addSQL(" and staff.SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL(" and staff.SEX = :SEX");
        parser.addSQL(" and exists (select 1 from tf_m_stafffuncright r where r.STAFF_ID = staff.STAFF_ID and r.RIGHT_CODE='crm9223'/*快速授权审核菜单*/)");
        return Dao.qryByParse(parser);
    }

    /** 根据Staff_id查询出审核人的手机号码 */
    public static IDataset queryStaffSerial(String awsStaffId) throws Exception
    {
        IData params = new DataMap();
        params.put("AWS_STAFF_ID", awsStaffId);
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.STAFF_ID,T.DEPART_ID,T.SERIAL_NUMBER FROM TD_M_STAFF T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.STAFF_ID = :AWS_STAFF_ID");
        return Dao.qryByParse(parser);
    }

    public static boolean updateAuthTrade(IData param) throws Exception
    {
        return Dao.save("TF_B_TEMPAUTH_LOG", param, new String[]
        { "ASK_ID" }, Route.CONN_CRM_CEN);
    }
}
