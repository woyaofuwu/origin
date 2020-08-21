
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.SqlUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class AuditStaffInfoQry
{

    /**
     * 获取VPMN客户经理权限
     * 
     * @param staffId
     * @param userProdCode
     * @param rightCode
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset checkVpmnRight(String staffId, String userProdCode, String rightCode) throws Exception
    {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        param.put("USER_PRODUCT_CODE", userProdCode);
        param.put("RIGHT_CODE", rightCode);
        return Dao.qryByCodeParser("TF_M_STAFF_GRP_RIGHT", "SEL_RIGHT_BY_STAFFID_PRODCODE_RIGHTCODE", param);
    }

    /**
     * 获取VPMN客户经理权限（包括注销的）
     * 
     * @param staffId
     * @param userProdCode
     * @param rightCode
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset checkVpmnRightAll(String staffId, String userProdCode, String rightCode, String startDate) throws Exception
    {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        param.put("USER_PRODUCT_CODE", userProdCode);
        param.put("RIGHT_CODE", rightCode);
        param.put("START_DATE", startDate);
        return Dao.qryByCodeParser("TF_M_STAFF_GRP_RIGHT", "SEL_RIGHT_BY_STAFF_PROD_RIGHT", param);
    }

    /**
     * @Description:批量导入到临时表(VPMN产品客户经理分配)
     * @author sungq3
     * @date 2014-05-20
     * @return
     * @throws Exception
     */
    public static IDataset importVpmnDisInfo(IDataset dataset, String importType, String fileName, String staffId, String departId, String cityCode, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        String importId = SeqMgr.getImportId();
        data.put("IMPORT_ID", importId);
        data.put("IMPORT_FILENAME", fileName); // 导入文件的文件名
        data.put("IMPORT_TYPE", importType); // VPMN客户经理批量导入类型
        data.put("EPARCHY_CODE", eparchyCode);// 归属地州
        data.put("CITY_CODE", cityCode);// 导入业务区
        data.put("DEPART_ID", departId);// 导入部门
        data.put("DEAL_STATE", "0");
        data.put("STAFF_ID", staffId);// 导入员工
        data.put("RSRV_DATE15", SysDateMgr.getSysTime());
        data.put("REMARK", "前台批量导入数据！");
        Dao.insert("TF_F_CMS_IMPORT_BAT", data, Route.CONN_CRM_CG);

        IDataset succDataset = new DatasetList();
        IDataset failDataset = new DatasetList();
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData importData = dataset.getData(i);
            if (importData.getBoolean("IMPORT_RESULT"))
            {
                importData.put("IMPORT_ID", importId);
                importData.put("DEAL_STATE", "I");// 处理状态
                succDataset.add(importData);
            }
            else
            {
                failDataset.add(importData);
            }
        }
        Dao.insert("TF_F_CMS_IMPORT_DATA", succDataset, Route.CONN_CRM_CG);
        return failDataset;
    }

    /**
     * 新增VPMN客户经理权限
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static boolean insertVpnRight(IData inparam) throws Exception
    {
        return Dao.insert("TF_M_STAFF_GRP_RIGHT", inparam);
    }

    /**
     * 查询客户经理权限信息列表
     * 
     * @param staffId
     * @param userProdCode
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryVpmnCustMgrStaffList(String staffId, String userProdCode, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_MANAGER_ID", staffId);
        param.put("USER_PRODUCT_CODE", userProdCode);
        return Dao.qryByCodeParser("TF_F_CUST_MANAGER_STAFF", "SEL_BY_CUSTMGRID_USERPRODCODE", param, pg, Route.CONN_CRM_CG);
    }

    /**
     * @Description:查询VPMN产品客户经理分配批量导入批次信息
     * @author sungq3
     * @date 2014-05-19
     * @return
     * @throws Exception
     */
    public static IDataset qryVpmnDisInfo(String importId, String dealState, String impFileName, String impType, String startDate, String endDate, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("IMPORT_ID", importId);
        param.put("DEAL_STATE", dealState);
        param.put("IMPORT_FILENAME", impFileName);
        param.put("IMPORT_TYPE", impType);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        param.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        return Dao.qryByCodeParser("TF_F_CMS_IMPORT_BAT", "SEL_CMS_IMPORT_BY_STAFFID", param, pg, Route.CONN_CRM_CG);
    }

    /**
     * 查询VPMN集团用户信息
     * 
     * @param groupId
     * @param vpnNo
     * @param vpnName
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryVpmnGroupInfo(String groupId, String vpnNo, String vpnName, String cityCode, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("VPN_NO", vpnNo);
        param.put("VPN_NAME", vpnName);
        param.put("CITY_CODE", cityCode);
        return Dao.qryByCodeParser("TF_F_USER_VPN", "SEL_VPN_BY_GRPID_VPNNO_VPNNAME", param, pg);
    }

    /**
     * 查询VPMN客户经理信息
     * 
     * @param jobType
     * @param custMgrId
     * @return
     * @throws Exception
     */
    public static IDataset qryVpmnManagerInfo(String jobType, String custMgrId) throws Exception
    {
        IData param = new DataMap();
        param.put("JOB_TYPE", jobType);
        param.put("CUST_MANAGER_ID", custMgrId);
        return Dao.qryByCodeParser("TF_F_CUST_MANAGER_STAFF", "SEL_BY_MGRID_JOBTYPE", param, Route.CONN_CRM_CG);
    }

    /**
     * 查询员工权限信息列表
     * 
     * @param staffId
     * @param userProdCode
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryVpmnStaffList(String staffId, String userProdCode, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_MANAGER_ID", staffId);
        param.put("USER_PRODUCT_CODE", userProdCode);
        return Dao.qryByCodeParser("TF_M_STAFF_GRP_RIGHT", "SEL_BY_STAFFID_USERPRODCODE", param, pg, Route.CONN_CRM_CG);
    }

    /**
     * @Description:查询此次导入明细
     * @author sungq3
     * @date 2014-05-20
     * @return
     * @throws Exception
     */
    public static IDataset queryThisVpmnManagerInfo(String importId, String dealState, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("IMPORT_ID", importId);
        param.put("DEAL_STATE", dealState);
        return Dao.qryByCodeParser("TF_F_CMS_IMPORT_DATA", "SEL_IMPORT_DATA_BY_IMPORT_ID", param, pg, Route.CONN_CRM_CG);
    }

    /**
     * 查询VPMN集团所在地市
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryVpmnCityCode(String vpnNo) throws Exception
    {
        IData param = new DataMap();
        param.put("VPN_NO", vpnNo);
        return Dao.qryByCodeParser("TF_F_USER_VPN", "SEL_CITYCODE_BY_VPNNO", param, Route.CONN_CRM_CG);
    }

    /**
     * @Description:更新VPMN产品客户经理分配批量导入临时表
     * @author sungq3
     * @date 2014-05-22
     * @return
     * @throws Exception
     */
    public static boolean updateImportBatVpmnDisInfo(IData inparam, String eparchyCode) throws Exception
    {
        inparam.put("DEAL_TIME", SysDateMgr.getSysTime());
        inparam.put("DEAL_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("DEAL_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparam.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        inparam.put("EPARCHY_CODE", eparchyCode);

        SQLParser parser = new SQLParser(inparam);
        parser.addSQL("update TF_F_CMS_IMPORT_BAT  set ");
        parser.addSQL(" DEAL_STATE = :DEAL_STATE, ");
        parser.addSQL(" DEAL_TIME = to_date(:DEAL_TIME, 'yyyy-mm-dd hh24:mi:ss'), ");
        parser.addSQL(" DEAL_DEPART_ID = :DEAL_DEPART_ID, ");
        parser.addSQL(" DEAL_STAFF_ID = :DEAL_STAFF_ID, ");
        parser.addSQL(" CITY_CODE = :CITY_CODE, ");
        parser.addSQL(" EPARCHY_CODE = :EPARCHY_CODE, ");
        parser.addSQL(" RSRV_STR10 = :RSRV_STR10 ");
        parser.addSQL(" where IMPORT_ID = :IMPORT_ID ");

        return Dao.executeUpdate(parser, Route.CONN_CRM_CG) != 0 ? true : false;
    }

    /**
     * @Description:更新VPMN产品客户经理分配批量导入明细表
     * @author sungq3
     * @date 2014-05-22
     * @return
     * @throws Exception
     */
    public static boolean updateImportDataVpmnDisInfo(IData inparam) throws Exception
    {
        String importId = inparam.getString("IMPORT_ID");
        String rsrv_str1 = inparam.getString("RSRV_STR1");
        String rsrv_str2 = inparam.getString("RSRV_STR2");

        return Dao.update("TF_F_CMS_IMPORT_DATA", inparam, new String[]
        { "IMPORT_ID", "RSRV_STR1", "RSRV_STR2" }, new String[]
        { importId, rsrv_str1, rsrv_str2 }, Route.CONN_CRM_CG);
    }

    /**
     * @Description:更新VPMN产品客户经理分配批量导入明细表
     * @author sungq3
     * @date 2014-05-22
     * @return
     * @throws Exception
     */
    public static boolean updateManagerImportInfo(IData inparam) throws Exception
    {
        String importId = inparam.getString("IMPORT_ID");
        String rsrv_str1 = inparam.getString("RSRV_STR1");
        String rsrv_str2 = inparam.getString("RSRV_STR2");
        String rsrv_str3 = inparam.getString("RSRV_STR3");
        String rsrv_str4 = inparam.getString("RSRV_STR4");
        return Dao.update("TF_F_CMS_IMPORT_DATA", inparam, new String[]
        { "IMPORT_ID", "RSRV_STR1", "RSRV_STR2", "RSRV_STR3", "RSRV_STR4" }, new String[]
        { importId, rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4 }, Route.CONN_CRM_CG);
    }

    /**
     * 员工集团权限表
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean updateStaffGrpRightByPK(IData data) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_M_STAFF_GRP_RIGHT", "UPDATE_ENDDATE_BY_PK", data, Route.CONN_CRM_CG) != 0 ? true : false;
    }

    /**
     * @Description:保存VpnInfo
     * @author sungq3
     * @date 2014-05-22
     * @return
     * @throws Exception
     */
    public static boolean updateVpnInfo(IData vpnInfo) throws Exception
    {
        SQLParser parser = new SQLParser(vpnInfo);
        parser.addSQL("update TF_F_USER_VPN  set ");
        parser.addSQL(" CUST_MANAGER = :CUST_MANAGER, ");
        parser.addSQL(" UPDATE_TIME = to_date(:UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss'), ");
        parser.addSQL(" UPDATE_STAFF_ID = :UPDATE_STAFF_ID, ");
        parser.addSQL(" UPDATE_DEPART_ID = :UPDATE_DEPART_ID ");
        parser.addSQL(" where PARTITION_ID = MOD(to_number(:USER_ID),10000) ");
        parser.addSQL(" and USER_ID = to_number(:USER_ID) ");
        return Dao.executeUpdate(parser, Route.CONN_CRM_CG) != 0 ? true : false;
    }

    /**
     * 更新VPMN客户经理权限
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static boolean updVpnRight(IData inparam) throws Exception
    {
        SQLParser parser = new SQLParser(inparam);
        parser.addSQL("update TF_M_STAFF_GRP_RIGHT  set ");
        parser.addSQL(" RSRV_STR2   = :RSRV_STR2,  ");
        parser.addSQL(" RSRV_STR1   = :RSRV_STR1,  ");
        parser.addSQL(" RSRV_STR3   = :RSRV_STR3,  ");
        parser.addSQL(" REMARK      = :REMARK,  ");
        parser.addSQL(" STAFF_ID    = :STAFF_ID,  ");
        parser.addSQL(" RIGHT_CODE  = :RIGHT_CODE,  ");
        parser.addSQL(" USER_PRODUCT_CODE    = :USER_PRODUCT_CODE,  ");
        parser.addSQL(" END_DATE    = to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss'),  ");
        parser.addSQL(" START_DATE  = to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss'),  ");
        parser.addSQL(" UPDATE_TIME = to_date(:UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss'),  ");
        parser.addSQL(" UPDATE_DEPART_ID = :UPDATE_DEPART_ID,  ");
        parser.addSQL(" UPDATE_STAFF_ID = :UPDATE_STAFF_ID  ");
        parser.addSQL(" where 1 = 1  ");
        parser.addSQL(" and RIGHT_CODE = :RIGHT_CODE  ");
        parser.addSQL(" and STAFF_ID = :STAFF_ID  ");
        parser.addSQL(" and USER_PRODUCT_CODE = :USER_PRODUCT_CODE  ");
        parser.addSQL(" and START_DATE  = to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')  ");
        if (Dao.executeUpdate(parser) > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    
    public static IData getAreaGrantByDeptId(String dept_id, String staffId)
    throws Exception
  {
    String tmp_dept = (dept_id == null) || ("".equals(dept_id)) ? staffId : dept_id;
    IData param = new DataMap();
    param.put("DEPART_ID", tmp_dept);
    SQLParser parser = new SQLParser(param);
    parser.addSQL(" select * from TD_M_DEPART where DEPART_ID=:DEPART_ID and VALIDFLAG=0 ");
    IDataset ds = Dao.qryByParse(parser);
    return (ds == null) || (ds.size() == 0) ? null : ds.getData(0);
  }
    
    /**
     * 根据集团客户cust_id和手机号码号码查询集团关键人信息
     * @param custId
     * @param memSn
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-4-24
     */
    public static IData getGroupKeyMemByGrpCustIdAndMemSn(String custId, String memSn) throws Exception{
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        param.put("MEM_SERIAL_NUMBER", memSn);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlUtil.trimSql("SELECT A.*												"));
        sql.append(SqlUtil.trimSql("  FROM TF_F_CUST_GROUP_EXTEND A                         "));
        sql.append(SqlUtil.trimSql(" WHERE 1 = 1                                            "));
        sql.append(SqlUtil.trimSql("   AND A.EXTEND_VALUE = :CUST_ID                        "));
        sql.append(SqlUtil.trimSql("   AND A.EXTEND_TAG = 'keym'                            "));
        sql.append(SqlUtil.trimSql("   AND A.RSRV_STR25 = :MEM_SERIAL_NUMBER                "));
        sql.append(SqlUtil.trimSql("   AND (RSRV_DATE10 IS NULL OR RSRV_DATE10 > SYSDATE)   "));
        IDataset ds = Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
        return (ds == null) || (ds.size() == 0) ? null : ds.getData(0);
    }
    
    /**
     * 根据集团产品用户userid查询集团客户经理的手机号码
     * @param userId
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-12-8
     */
    public static IData getCustmanagerSnByGrpUserId(String userId) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlUtil.trimSql("SELECT B.CUST_MANAGER_ID, C.SERIAL_NUMBER							"));	
        sql.append(SqlUtil.trimSql("  FROM TF_F_USER A, TF_F_CUST_GROUP B, TF_F_CUST_MANAGER_STAFF C    "));
        sql.append(SqlUtil.trimSql(" WHERE A.CUST_ID = B.CUST_ID                                        "));
        sql.append(SqlUtil.trimSql("   AND B.CUST_MANAGER_ID = C.CUST_MANAGER_ID                        "));
        sql.append(SqlUtil.trimSql("   AND A.PARTITION_ID = MOD(:USER_ID, 10000)                        "));
        sql.append(SqlUtil.trimSql("   AND A.USER_ID = :USER_ID                                         "));
        sql.append(SqlUtil.trimSql("   AND A.REMOVE_TAG = '0'                                           "));
        sql.append(SqlUtil.trimSql("   AND B.REMOVE_TAG = '0'                                           "));
        sql.append(SqlUtil.trimSql("   AND C.SERIAL_NUMBER IS NOT NULL                                  "));
        IDataset ds = Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
        return (ds == null) || (ds.size() == 0) ? null : ds.getData(0);
    }
    /**
     * 查询稽核人员信息
     * @param param
     * @param pg
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-7-10
     */
    public static IDataset qryAuditStaffInfo(IData param, Pagination pg) throws Exception
    { 
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT DISTINCT M.*");
        parser.addSQL("  FROM UOP_SYS.TF_M_STAFFFUNCRIGHT F, TD_M_STAFF M, TD_M_AREA A");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL("   AND M.STAFF_ID = F.STAFF_ID");
        parser.addSQL("   AND (A.AREA_CODE = M.CITY_CODE OR A.AREA_CODE = M.EPARCHY_CODE)");
        parser.addSQL("   AND F.RIGHT_TAG = '1'");
        parser.addSQL("   AND M.STAFF_ID = :STAFF_ID");
        parser.addSQL("   AND M.STAFF_NAME LIKE '%' || :STAFF_NAME || '%'");
        parser.addSQL("   AND (A.AREA_CODE = :CITY_CODE OR A.AREA_CODE = :EPARCHY_CODE)");
        parser.addSQL("   AND ((F.RIGHT_ATTR = '0' AND F.RIGHT_CODE IN ('PRIV_AUDIT_AUDSTAFF')) OR");
        parser.addSQL("       (RIGHT_ATTR = '1' AND EXISTS");
        parser.addSQL("        (SELECT 1");
        parser.addSQL("            FROM UOP_SYS.TF_M_ROLEFUNCRIGHT K");
        parser.addSQL("           WHERE F.RIGHT_CODE = K.ROLE_CODE");
        parser.addSQL("             AND K.RIGHT_CODE IN ('PRIV_AUDIT_AUDSTAFF'))))");
        return Dao.qryByParse(parser, pg, Route.CONN_CRM_CEN);
    }
}
