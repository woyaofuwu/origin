package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.group.esop.esopdesktop.EsopDeskTopConst;

/**
 * order
 * 查询代办工单表
 *
 * @author ckh
 * @data 2018/1/15.
 */
public class OpTaskInfoQry
{
    public static void insertOpTaskInfo(IData insertData) throws Exception
    {
        insertData.put("EXEC_MONTH", SysDateMgr.getCurMonth());
        insertData.put("START_TIME", SysDateMgr.getSysTime());

        Dao.insert("TF_F_INFO", insertData, Route.CONN_CRM_CEN);
    }

    public static void updateOpTaskInfo(IData updateData) throws Exception
    {
        StringBuilder sbf = new StringBuilder();
        sbf.append(
                "UPDATE TF_F_INFO SET INFO_STATUS=:INFO_STATUS,FINISH_STAFF_ID=:FINISH_STAFF_ID,FINISH_TIME =SYSDATE WHERE TASK_STATUS='");
        sbf.append(EsopDeskTopConst.WORKINFO_STATUS_UNDO);
        sbf.append("' AND INFO_TYPE=:INFO_TYPE ");
        //根据订单号修改状态
        String relaId = updateData.getString("RELA_ID", "");
        if (StringUtils.isNotEmpty(relaId))
        {
            sbf.append(" AND RELA_ID=:RELA_ID ");
        }
        else
        {//根据流程节点
            sbf.append(" AND INFO_SIGN=:INFO_SIGN ");
        }

        Dao.executeUpdate(sbf, updateData, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void updateOpTaskInfoPlanTime(IData updateData) throws Exception
    {
    	 StringBuilder sbf = new StringBuilder();
         sbf.append(
                 "UPDATE TF_F_INFO T SET T.PLAN_FINISH_TIME = TO_DATE(:PLAN_FINISH_TIME ,'YYYY-MM-dd HH24:MI:SS') WHERE TASK_STATUS='");
         sbf.append(EsopDeskTopConst.WORKINFO_STATUS_UNDO);
         sbf.append("' AND TASK_TYPE_CODE=:TASK_TYPE_CODE ");
         //根据订单号修改状态
         String relaId = updateData.getString("RELA_ID", "");
         if (StringUtils.isNotEmpty(relaId))
         {
             sbf.append(" AND RELA_ID=:RELA_ID ");
         }
         else
         {//根据流程节点
             sbf.append(" AND TASK_SIGN=:TASK_SIGN ");
         }
         
         Dao.executeUpdate(sbf, updateData, Route.CONN_CRM_CEN);
    }

    public static void updateOpTaskInfoStatus(IData param) throws Exception
    {
        StringBuilder sql = new StringBuilder(200);
        sql.append("update TF_F_INFO set INFO_STATUS = :INFO_STATUS where INFO_ID = :INFO_ID ");
        
        Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryOpTaskInfoByRelaIdOrTaskSign(String relaId, String taskSign, String taskTypeCode)
            throws Exception
    {
        StringBuilder strSql = new StringBuilder(1000);
        strSql.append("SELECT ");
        strSql.append(
                "  TASK_ID, EXEC_MONTH, RELA_ID, TASK_SIGN, TASK_TOPIC, TASK_TYPE_CODE, TASK_STATUS, TASK_LEVEL, TODO_URL, TASK_CONTENT, ");
        strSql.append(
                "  TASK_AUTH, START_TIME, PLAN_FINISH_TIME, FINISH_STAFF_ID, FINISH_TIME, OPER_TYPE, TRADE_TYPE_CODE, GROUP_ID, ");
        strSql.append(
                "  CUST_NAME, CUST_SERV_LEVEL, PRODUCT_ID, PRODUCT_NAME, BUSI_TYPE_CODE, TASK_NODE_NAME, OWN_FEE, CYCLE_ID, ");
        strSql.append("  BUSI_REQUIREMENT, TASK_RESULT, RSRV_STR1, RSRV_STR2, RSRV_STR3 ");
        strSql.append("FROM TF_F_INFO ");
        strSql.append("WHERE TASK_STATUS = '9' ");
        strSql.append("  AND TASK_TYPE_CODE = :TASK_TYPE_CODE ");
        if (StringUtils.isNotEmpty(relaId))
        {
            strSql.append("AND RELA_ID = :RELA_ID ");
        }
        else
        {
            strSql.append(" AND TASK_SIGN=:TASK_SIGN ");
        }

        IData param = new DataMap();
        param.put("RELA_ID", relaId);
        param.put("TASK_SIGN", taskSign);
        param.put("TASK_TYPE_CODE", taskTypeCode);
        return Dao.qryBySql(strSql, param, Route.CONN_CRM_CEN);
    }

    public static void deleteOpTaskInfoByPK(String taskId) throws Exception
    {
        IData param = new DataMap();
        param.put("TASK_ID", taskId);
        Dao.delete("TF_F_INFO", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static void deleteOpTaskInfoByPkBatch(IDataset dataset) throws Exception
    {
        Dao.delete("TF_F_INFO", dataset, new String[]{"TASK_ID"}, Route.CONN_CRM_CEN);
    }
    
    public static IDataset queryOpTaskByStaffId(String staffId) throws Exception
    {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        return Dao.qryByCode("TF_F_INFO", "SEL_BY_STAFF_ID", param, Route.CONN_CRM_CEN);
    }
    
    public static IDataset queryOpTaskByStaffIdTaskTypeCodeTaskStatus(String staffId, String taskTypeCode, String taskStatus, String receObjType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        param.put("TASK_TYPE_CODE", taskTypeCode);
        param.put("TASK_STATUS", taskStatus);
        param.put("RECE_OBJ_TYPE", receObjType);
        return Dao.qryByCodeParser("TF_F_INFO", "SEL_BY_CONDS", param, page, Route.CONN_CRM_CEN);
    }
    
    public static IDataset queryOpTaskList(IData param, Pagination page) throws Exception
    {
    	String staffId = param.getString("STAFF_ID","");
        String taskTypeCode = param.getString("INFO_TYPE","");
        String busiTypeCode = param.getString("INFO_CHILD_TYPE","");
        String taskTopic = param.getString("INFO_TOPIC","");
        String taskStatus = param.getString("INFO_STATUS","");
        String receObjType = param.getString("RECE_OBJ_TYPE","");
        String startDate = param.getString("START_DATE","");
        String endDate = param.getString("END_DATE","");
        String ibsysid = param.getString("IBSYSID","");
        String groupId = param.getString("GROUP_ID","");
        String custName = param.getString("CUST_NAME","");
    	
        param.put("STAFF_ID", staffId);
        param.put("INFO_TYPE", taskTypeCode);
        param.put("INFO_CHILD_TYPE", busiTypeCode);
        param.put("INFO_TOPIC", taskTopic);
        param.put("INFO_STATUS", taskStatus);
        param.put("RECE_OBJ_TYPE", receObjType);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("IBSYSID", ibsysid);
        param.put("GROUP_ID", groupId);
        param.put("CUST_NAME", custName);
        return Dao.qryByCodeParser("TF_F_INFO", "SEL_BY_CONDS", param, page, Route.CONN_CRM_CEN);
    }
    
    public static IDataset queryUnReadList(String staffId, String taskTypeCode, String taskTopic, String taskStatus, String receObjType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        param.put("INFO_TYPE", taskTypeCode);
        param.put("INFO_TOPIC", taskTopic);
        param.put("INFO_STATUS", taskStatus);
        param.put("RECE_OBJ_TYPE", receObjType);
        return Dao.qryByCodeParser("TF_F_INFO", "SEL_UNREAD_BY_CONDS", param, page, Route.CONN_CRM_CEN);
    }
    
    public static IDataset queryUnDoneWorkList(String staffId, String taskTypeCode, String taskTopic, String taskStatus, String receObjType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        param.put("INFO_TYPE", taskTypeCode);
        param.put("INFO_TOPIC", taskTopic);
        param.put("INFO_STATUS", taskStatus);
        param.put("RECE_OBJ_TYPE", receObjType);
        return Dao.qryByCodeParser("TF_F_INFO", "SEL_UNDONEWORK_BY_CONDS", param, page, Route.CONN_CRM_CEN);
    }
    
    public static IDataset queryTaskInfo(String instId) throws Exception
    {
        IData param = new DataMap();
        param.put("INST_ID", instId);
        
        return Dao.qryByCodeParser("TF_F_INFO", "SEL_BY_CONDS", param, Route.CONN_CRM_CEN);
    }
    
    public static int[] updStaffForInfo(IDataset infoList) throws Exception {
        
        StringBuilder sql = new StringBuilder(1000);        
        sql.append(" UPDATE tf_f_info_instance A SET A.RECE_OBJ =:RECE_OBJ  WHERE 1=1 and a.INST_ID=:INST_ID ");
        
        return Dao.executeBatch(sql, infoList, Route.CONN_CRM_CEN);

    }
    /**
     * 根据状态和所属员工查询
     * @param infoStatus  待办状态 
     * @param newStaff   当前所属员工
     * @return
     * @throws Exception
     */
    public static IDataset selStaffForInfo(String infoStatus,String newStaff) throws Exception {
        IData param = new DataMap();
        param.put("INFO_STATUS", infoStatus);
        param.put("RECE_OBJ", newStaff);

        StringBuilder sql = new StringBuilder(1000);        
        sql.append(" SELECT b.INST_ID,a.INFO_ID,a.INFO_SIGN FROM tf_f_info a,tf_f_info_instance b where a.info_id=b.info_id and a.INFO_STATUS=:INFO_STATUS and b.RECE_OBJ=:RECE_OBJ  ");
        
        return Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);
    }

	public static IDataset qryInfosByInstId(IData param)throws Exception {
        
        return Dao.qryByCodeParser("TF_F_INFO", "SEL_BY_INSTID", param, Route.CONN_CRM_CEN);
	}
}
