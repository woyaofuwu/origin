
package com.asiainfo.veris.crm.order.soa.person.busi.taskalarm.managerule;

import java.util.Calendar;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ManageRuleQry;

public class ManageRuleBean extends CSBizBean
{

    private static String generatePlan(IData ruleData, String execTime) throws Exception
    {
        // 格化化时间字符串成yyyy-MM-dd
        // 保存plan值
        String plan = "";
        // 执行周期:0-天,1-周,2-月
        String cycleType = ruleData.getString("CYCLE_TYPE", "");
        // 规则生效时间
        String startTime = ruleData.getString("START_TIME", "");
        // 规则失效时间
        String endTime = ruleData.getString("END_TIME", "");

        // 根据不同的运行周期拼成PLAN字段的值
        // 运行周期为每天
        if ("0".equals(cycleType))
        {
            plan = "D/" + execTime + "/" + DateFormatUtils.format(SysDateMgr.encodeTimestamp(startTime).getTime(), "yyyy-MM-dd") + "/0,1/" + DateFormatUtils.format(SysDateMgr.encodeTimestamp(endTime).getTime(), "yyyy-MM-dd");
        }
        // 运行周期为每周.暂定于周日执行
        else if ("1".equals(cycleType))
        {
            plan = "W/" + execTime + "/" + DateFormatUtils.format(SysDateMgr.encodeTimestamp(startTime).getTime(), "yyyy-MM-dd") + "/7/0,1/" + DateFormatUtils.format(SysDateMgr.encodeTimestamp(endTime).getTime(), "yyyy-MM-dd");
        }
        // 运行周期为每月执行
        else if ("2".equals(cycleType))
        {
            // 保存拼成PLAN字段串的结果
            plan = "M/" + execTime + "/" + DateFormatUtils.format(SysDateMgr.encodeTimestamp(startTime).getTime(), "yyyy-MM-dd") + "/";
            // 执行日期号拼成的串
            String day = "";
            // 执行日期月份拼成的串
            String month = "";

            // 将字符串型的日期转成Calendar型的日期
            // 构造日期实例
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            // 为实例日期设置实际值
            cal1.setTime(SysDateMgr.string2Date(startTime, "yyyy-MM-dd"));
            cal2.setTime(SysDateMgr.string2Date(endTime, "yyyy-MM-dd"));

            // 两个日期的临时变量，分保为规则生效时间的那月的最后一天,失效时间的那月的最后一天
            Calendar temp1 = Calendar.getInstance();
            Calendar temp2 = Calendar.getInstance();

            // 给两临时变更设置值
            temp1.set(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), cal1.getActualMinimum(Calendar.DAY_OF_MONTH));
            temp2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.getActualMinimum(Calendar.DAY_OF_MONTH));

            // 遍历日期时间差，得出每个月的最后一天拼成的串以及月份拼成的串
            while (!temp1.after(temp2))
            {
                day += temp1.getActualMaximum(Calendar.DAY_OF_MONTH) + ",";
                month += (temp1.get(Calendar.MONTH) + 1) + ",";
                temp1.set(Calendar.MONTH, temp1.get(Calendar.MONTH) + 1);
            }

            // 格式化字符串,去掉day与month串末尾多余的","
            day = day.substring(0, day.length() - 1);
            month = month.substring(0, month.length() - 1);

            // 将日期加入plan串中
            plan += day + ":" + month + "/" + DateFormatUtils.format(SysDateMgr.encodeTimestamp(endTime).getTime(), "yyyy-MM-dd");
        }
        return plan;
    }

    public IDataset checkClassIsAdd(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String calssName = data.getString("CLASS_NAME", "");
        return ManageRuleQry.checkClassIsAdd(calssName, page);
    }

    public IDataset checkClassIsUpdate(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String ruleId = data.getString("RULE_ID", "");
        String className = data.getString("CLASS_NAME", "");
        return ManageRuleQry.checkClassIsUpdate(ruleId, className, page);
    }

    public int delRuleBatch(IData tabData, Pagination page) throws Exception
    {
        // 设置失效
        String removeTag = "1";
        // 规则标识
        String ruleId = tabData.getString("RULE_ID");
        // 使用标识为禁用
        String useTag = "0";
        // 任务标识(TASK_ID)
        String taskId = tabData.getString("TASK_ID", "");
        // TASK_ID字段不为空,禁用TASK表与TASK_PLAN表里对应的信秘
        if (!"".equals(taskId))
        {
            // 将TASK表的使用标识置于禁用状态
            ManageRuleQry.updateUseTag4Task(useTag, taskId, page);
            // 将TASK_PLAN表的使用标识置于禁用状态
            ManageRuleQry.updateUseTag4TaskPlan(useTag, taskId, page);
        }
        // 禁用规则信息
        return ManageRuleQry.updateRemoveTag4Alarm(useTag, removeTag, ruleId, page);
    }

    public int disenableRule(IData tabData, Pagination page) throws Exception
    {
        String ruleIdList = tabData.getString("multi_RULE_IDs");
        IDataset ids = new DatasetList(ruleIdList);
        String ruleId = "";
        IData ruleData;
        String taskID = "";
        String useTag = "0";
        int disenableSuccessFlag = 0;
        // 将规则ID进行分割并保存在参数容器中
        for (int i = 0; i < ids.size(); i++)
        {
            ruleId = ids.getData(i).getString("RULE_ID");
            ruleData = ManageRuleQry.queryRuleByPK(ruleId, page).getData(0);
            taskID = ruleData.getString("TASK_ID", "").trim();
            IDataset runState = ManageRuleQry.isRunning(taskID, page);
            if (!IDataUtil.isEmpty(runState))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_783, "规则" + ruleId + "处于正在运行的状态，请稍后再试");
            }
            useTag = "0";
            disenableSuccessFlag = ManageRuleQry.updateRuleTaskID(taskID, ruleId, useTag, page);
            if (!"".equals(taskID))
            {
                // 将TASK表的使用标识置于禁用状态
                ManageRuleQry.updateUseTag4Task(useTag, taskID, page);
                // 将TASK_PLAN表的使用标识置于禁用状态
                disenableSuccessFlag = ManageRuleQry.updateUseTag4TaskPlan(useTag, taskID, page);
            }
        }
        return disenableSuccessFlag;
    }

    public int enableRule(IData tabData, Pagination page) throws Exception
    {
        String ruleIdList = tabData.getString("multi_RULE_IDs");
        String execTime = tabData.getString("EXEC_TIME");
        IDataset ids = new DatasetList(ruleIdList);
        String ruleId = "";
        IData ruleData;
        String taskID = "";
        String useTag = "0";
        int enableSuccessFlag = 0;
        String className = "";
        String plan = "";
        boolean flag = false;
        // 将规则ID进行分割并保存在参数容器中
        for (int i = 0; i < ids.size(); i++)
        {
            ruleId = ids.getData(i).getString("RULE_ID");
            ruleData = ManageRuleQry.queryRuleByPK(ruleId, page).getData(0);
            taskID = ruleData.getString("TASK_ID", "").trim();
            className = ruleData.getString("CLASS_NAME", "").trim();
            // 如果在RULE表中未配置TASK_ID，则重新配置，之后再将TASK与TASK_PLAN置于可用状态
            // 如果该规规是初次启用，则需要插入数据到TASK与TASK_PLAN表中
            // TASK_ID字段不为空,禁用TASK表与TASK_PLAN表里对应的信秘
            if ("".equals(taskID))
            {
                taskID = ManageRuleQry.getRuleID();
                ManageRuleQry.updateRuleTaskID(taskID, ruleId, "1", page);
                // 插放规则自动执行的相关信息，即在TASK表中插入相应的数据信息
                ManageRuleQry.addTaskInfo(ruleId, taskID, className, page);
                // 插放规则自动执行的相关信息，即在TASK_PLAN表中插入相应的数据信息
                plan = generatePlan(ruleData, execTime);
                flag = ManageRuleQry.addTaskPlanInfo(taskID, plan, className, page);
                if (true == flag)
                {
                    enableSuccessFlag = 1;
                }
                else
                {
                    enableSuccessFlag = 0;
                }
            }
            else
            {
                // 使用标识为禁用
                useTag = "1";
                ManageRuleQry.updateTagUseTag4Alarm(useTag, taskID, ruleId, page);
                // 将TASK表的使用标识置于禁用状态
                ManageRuleQry.updateUseTag4Task(useTag, taskID, page);
                // 将TASK_PLAN表的使用标识置于禁用状态
                enableSuccessFlag = ManageRuleQry.updateUseTag4TaskPlan(useTag, taskID, page);
            }
        }
        // 任务标识(TASK_ID)
        return enableSuccessFlag;
    }

    public String getRuleID(IData data, Pagination page) throws Exception
    {
        return ManageRuleQry.getRuleID();
    }

    public boolean insertRuleToAlarm(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String className = data.getString("CLASS_NAME", "");
        String ruleID = data.getString("RULE_ID", "");
        String ruleName = data.getString("RULE_NAME", "");
        String ruleType = data.getString("PARAM_TYPE", "");
        String paramA = data.getString("PARAM_A", "");
        String paramB = data.getString("PARAM_B", "");
        String startTime = data.getString("START_TIME", "");
        String endTime = data.getString("END_TIME", "");
        String monitorObject = data.getString("MONITOR_OBJECT", "");
        String cycleType = data.getString("CYCLE_TYPE", "");
        String alarmLevel = data.getString("ALARM_LEVEL", "");
        String staffId = data.getString("UPDATE_STAFF_ID", "");
        String departId = data.getString("UPDATE_DEPART_ID", "");
        return ManageRuleQry.insertRuleToAlarm(className, ruleID, ruleName, ruleType, paramA, paramB, startTime, endTime, monitorObject, cycleType, alarmLevel, staffId, departId, page);
    }

    public IDataset isRunning(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String taskId = data.getString("TASK_ID", "");
        return ManageRuleQry.isRunning(taskId, page);
    }

    /**
     * 功能：查询ClassNames 作者：GongGuang
     */
    public IDataset queryClassNames(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String subsysCode = "CSM";
        String paramAttr = "9983";
        return ManageRuleQry.queryClassNames(subsysCode, paramAttr, routeEparchyCode, page);
    }

    public IDataset queryExecTime(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String eparchyCode = "ZZZZ";
        String tagCode = "TASKALARM_EXEC_TIME";
        String subSysCode = "CSM";
        String useTag = "0";
        return ManageRuleQry.queryExecTime(eparchyCode, tagCode, subSysCode, useTag, page);
    }

    /**
     * 功能：查询Levels 作者：GongGuang
     */
    public IDataset queryLevels(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String typeId = "TASKALARM_LEVEL";
        return ManageRuleQry.queryLevels(typeId, page);
    }

    public IDataset queryRuleByPK(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String ruleId = data.getString("RULE_ID", "");
        return ManageRuleQry.queryRuleByPK(ruleId, page);
    }

    public IDataset queryRuleByPKSpecial(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String ruleId = data.getString("RULE_ID", "");
        return ManageRuleQry.queryRuleByPKSpecial(ruleId, page);
    }

    public IDataset queryRulesByCond(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String ruleName = data.getString("RULE_NAME", "");
        String startTime = data.getString("START_TIME", "");
        String endTime = data.getString("END_TIME", "");
        String className = data.getString("CLASS_NAME", "");
        String useTag = data.getString("USE_TAG", "");
        return ManageRuleQry.queryRulesByCond(ruleName, className, startTime, endTime, useTag, page);
    }

    public int updateRule(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String ruleName = data.getString("RULE_NAME", "");
        String paramType = data.getString("PARAM_TYPE", "");
        String paramA = data.getString("PARAM_A", "");
        String paramB = data.getString("PARAM_B", "");
        String startTime = data.getString("START_TIME", "");
        String endTime = data.getString("END_TIME", "");
        String className = data.getString("CLASS_NAME", "");
        String alarmLevel = data.getString("ALARM_LEVEL", "");
        String monitorObject = data.getString("MONITOR_OBJECT", "");
        String cycleType = data.getString("CYCLE_TYPE", "");
        String ruleId = data.getString("RULE_ID", "");
        return ManageRuleQry.updateRule(ruleName, paramType, paramA, paramB, startTime, endTime, className, alarmLevel, monitorObject, cycleType, ruleId, page);
    }

    public int updateTaskClassName(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String taskId = data.getString("TASK_ID", "");
        String className = data.getString("CLASS_NAME", "");
        return ManageRuleQry.updateTaskClassName(taskId, className, page);
    }

    public int updateTaskPlan(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String taskId = data.getString("TASK_ID", "");
        String plan = data.getString("PLAN", "");
        return ManageRuleQry.updateTaskPlan(taskId, plan, page);
    }

}
