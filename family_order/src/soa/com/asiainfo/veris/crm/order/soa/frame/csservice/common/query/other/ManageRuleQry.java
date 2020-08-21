
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqRuleAlarmId;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqTaskId;

public class ManageRuleQry extends CSBizBean
{

    public static boolean addTaskInfo(String ruleId, String taskID, String className, Pagination pagination) throws Exception
    {
        IData param = new DataMap();

        // 任务标识(TASK_ID)
        param.put("TASK_ID", taskID);
        // 任务类型
        param.put("TASK_KIND", "1");
        // 任务TYPE
        param.put("TASK_TYPE", "0");
        // 类名
        param.put("CLASS_NAME", className);
        // 可用标识
        param.put("USE_TAG", "1");
        // 过期标识
        param.put("EXPIRED_TAG", "0");
        // SUBSYS_CODE
        param.put("SUBSYS_CODE", "saleserv");
        param.put("RULE_ID", ruleId);
        return Dao.insert("TD_M_TASK", param);
    }

    public static boolean addTaskPlanInfo(String taskID, String plan, String className, Pagination pagination) throws Exception
    {
        IData param = new DataMap();

        // 任务标识(TASK_ID)
        param.put("TASK_ID", taskID);
        param.put("PLAN_INDEX", "1");
        param.put("PLAN", plan);
        // 类名
        param.put("CLASS_NAME", className);
        // 可用标识
        param.put("USE_TAG", "1");
        // 过期标识
        param.put("EXPIRED_TAG", "0");
        // SUBSYS_CODE
        param.put("SUBSYS_CODE", "saleserv");

        return Dao.insert("TD_M_TASK_PLAN", param);

    }

    public static IDataset checkClassIsAdd(String className, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("CLASS_NAME", className);
        return Dao.qryByCode("TD_RULE_FORALARM", "SEL_BY_CLASSNAME", params, pagination);

    }

    public static IDataset checkClassIsUpdate(String ruleId, String className, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("RULE_ID", ruleId);
        params.put("CLASS_NAME", className);
        return Dao.qryByCode("TD_RULE_FORALARM", "SEL_BY_CLASSNAME_RULEID", params, pagination);

    }

    public static String getRuleID() throws Exception
    {
        return Dao.getSequence(SeqRuleAlarmId.class);

    }

    public static String getSeqTaskId() throws Exception
    {
        return Dao.getSequence(SeqTaskId.class);

    }

    public static boolean insertRuleToAlarm(String className, String ruleID, String ruleName, String ruleType, String paramA, String paramB, String startTime, String endTime, String monitorObject, String cycleType, String alarmLevel, String staffId,
            String departId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("CLASS_NAME", className);
        param.put("RULE_ID", ruleID);
        param.put("RULE_NAME", ruleName);
        param.put("PARAM_TYPE", ruleType);
        param.put("PARAM_A", paramA);
        param.put("PARAM_B", paramB);
        param.put("START_TIME", startTime);
        param.put("END_TIME", endTime);
        param.put("MONITOR_OBJECT", monitorObject);
        param.put("CYCLE_TYPE", cycleType);
        param.put("ALARM_LEVEL", alarmLevel);
        param.put("DEAL_METHOD", "0");
        param.put("REMOVE_TAG", "0");
        param.put("USE_TAG", "0");
        param.put("UPDATE_STAFF_ID", staffId);
        param.put("UPDATE_DEPART_ID", departId);
        return Dao.insert("TD_RULE_FORALARM", param);
    }

    public static IDataset isRunning(String taskId, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("TASK_ID", taskId);
        return Dao.qryByCode("TD_M_TASK_PLAN", "SEL_IS_RUNNING", params, pagination);
    }

    public static IDataset queryClassNames(String subsysCode, String paramAttr, String eparchyCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("SUBSYS_CODE", subsysCode);
        params.put("PARAM_ATTR", paramAttr);
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAATTR", params, pagination);
    }

    public static IDataset queryExecTime(String eparchyCode, String tagCode, String subSysCode, String useTag, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        // 指定TAG标识
        params.put("TAG_CODE", tagCode);
        params.put("SUBSYS_CODE", subSysCode);
        params.put("USE_TAG", useTag);
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_TAG", "SEL_BY_PK", params, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset queryLevels(String typeId, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("TYPE_ID", typeId);
        return Dao.qryByCode("TD_S_STATIC", "SEL_BY_TYPEID", params, pagination);
    }

    public static IDataset queryRuleByPK(String ruleId, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("RULE_ID", ruleId);
        return Dao.qryByCode("TD_RULE_FORALARM", "SEL_BY_PK", params, pagination);

    }

    public static IDataset queryRuleByPKSpecial(String ruleId, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        SQLParser parser = new SQLParser(params);
        parser.addSQL("SELECT RULE_ID ");
        parser.addSQL(" FROM TD_RULE_FORALARM ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND REMOVE_TAG = '0'");
        parser.addSQL("  AND RULE_ID IN(");
        parser.addSQL(ruleId);
        parser.addSQL(")");
        return Dao.qryByParse(parser, pagination);

    }

    public static IDataset queryRulesByCond(String ruleName, String className, String startTime, String endTime, String useTag, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("RULE_NAME", ruleName);
        params.put("CLASS_NAME", className);
        params.put("START_TIME", startTime);
        params.put("END_TIME", endTime);
        params.put("USE_TAG", useTag);
        return Dao.qryByCode("TD_RULE_FORALARM", "SEL_RULE_BY_COND_NEW", params, pagination, Route.CONN_CRM_CEN);

    }

    public static int updateRemoveTag4Alarm(String useTag, String removeTag, String ruleId, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("USE_TAG", useTag);
        params.put("REMOVE_TAG", removeTag);
        params.put("RULE_ID", ruleId);
        StringBuilder strSql = new StringBuilder();
        strSql.delete(0, strSql.length());
        strSql.append("UPDATE TD_RULE_FORALARM SET REMOVE_TAG = :REMOVE_TAG,USE_TAG = :USE_TAG WHERE REMOVE_TAG = '0' AND RULE_ID in(");
        strSql.append(ruleId);
        strSql.append(")");
        return Dao.executeUpdate(strSql, params);
    }

    public static int updateRule(String ruleName, String paramType, String paramA, String paramB, String startTime, String endTime, String className, String alarmLevel, String monitorObject, String cycleType, String ruleId, Pagination pagination)
            throws Exception
    {
        IData params = new DataMap();
        params.put("RULE_NAME", ruleName);
        params.put("PARAM_TYPE", paramType);
        params.put("PARAM_A", paramA);
        params.put("PARAM_B", paramB);
        params.put("START_TIME", startTime);
        params.put("END_TIME", endTime);
        params.put("CLASS_NAME", className);
        params.put("ALARM_LEVEL", alarmLevel);
        params.put("MONITOR_OBJECT", monitorObject);
        params.put("CYCLE_TYPE", cycleType);
        params.put("RULE_ID", ruleId);
        return Dao.executeUpdateByCodeCode("TD_RULE_FORALARM", "UPD_BY_RULEID", params);
    }

    public static int updateRuleTaskID(String taskId, String ruleID, String useTag, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("TASK_ID", taskId);
        params.put("RULE_ID", ruleID);
        params.put("USE_TAG", useTag);
        return Dao.executeUpdateByCodeCode("TD_RULE_FORALARM", "UPD_USETAG_BY_PK", params);
    }

    public static int updateTagUseTag4Alarm(String useTag, String taskId, String ruleId, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("USE_TAG", useTag);
        params.put("REMOVE_TAG", taskId);
        params.put("RULE_ID", ruleId);
        StringBuilder strSql = new StringBuilder();
        strSql.delete(0, strSql.length());
        strSql.append("UPDATE TD_RULE_FORALARM SET TASK_ID = :TASK_ID,USE_TAG = :USE_TAG WHERE REMOVE_TAG = '0' AND RULE_ID in(");
        strSql.append(ruleId);
        strSql.append(")");
        return Dao.executeUpdate(strSql, params);
    }

    public static int updateTaskClassName(String taskId, String className, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("TASK_ID", taskId);
        params.put("CLASS_NAME", className);
        return Dao.executeUpdateByCodeCode("TD_M_TASK", "UPD_CLASSNAME_BY_PK", params);
    }

    public static int updateTaskPlan(String taskId, String plan, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("TASK_ID", taskId);
        params.put("PLAN", plan);
        return Dao.executeUpdateByCodeCode("TD_M_TASK_PLAN", "UPD_PLAN_BY_PK", params);
    }

    public static int updateUseTag4Task(String useTag, String taskId, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("USE_TAG", useTag);
        params.put("TASK_ID", taskId);
        StringBuilder strSql = new StringBuilder();
        strSql.delete(0, strSql.length());
        strSql.append("UPDATE TD_M_TASK SET USE_TAG = :USE_TAG WHERE TASK_ID in(");
        strSql.append(taskId);
        strSql.append(")");
        return Dao.executeUpdate(strSql, params);

    }

    public static int updateUseTag4TaskPlan(String useTag, String taskId, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("USE_TAG", useTag);
        params.put("TASK_ID", taskId);
        StringBuilder strSql = new StringBuilder();
        strSql.delete(0, strSql.length());
        strSql.append("UPDATE TD_M_TASK_PLAN SET USE_TAG = :USE_TAG WHERE TASK_ID in(");
        strSql.append(taskId);
        strSql.append(")");
        return Dao.executeUpdate(strSql, params);
    }

}
