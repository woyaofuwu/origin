
package com.asiainfo.veris.crm.order.web.person.taskalarm.managerule;

import java.util.Calendar;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：规则管理 作者：GongGuang
 */
public abstract class ManageRule extends PersonBasePage
{

    /**
     * 根据规则信息中的运行周期(CYCLE_TYPE)以及生效时间(START_TIME)、失效时间(END_TIME)生成该规则 对在TASK_PLAN表中的PLAN字段(该字段确定规则运行的具体时间).
     * 
     * @param ruleData
     *            规则信息
     * @param execTime
     *            执行的具体时间(具体到HH:mm:ss)
     * @return 返回规则在TASK_PLAN表中执行时间字段
     * @throws Exception
     *             抛出所有的异常信息
     */
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
            // plan = "D/" + execTime + "/" + format.format(format.parse(startTime)) + "/0,1/" +
            // format.format(format.parse(endTime));
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

    /**
     * 增加业务风险告警规则.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void addRule(IRequestCycle cycle) throws Exception
    {
        // 得到页面所填写的数据
        IData info = getData("cond", true);

        // 装载参数信息
        IData param = new DataMap();

        // 判断所要填加的规则执行对象是否已经被加载了，如果已经被加载了不能再次被添加
        param.put("CLASS_NAME", info.getString("CLASS_NAME", ""));
        IDataOutput dataCountCheck = CSViewCall.callPage(this, "SS.ManageRuleSVC.checkClassIsAdd", param, null);
        IDataset ruleSet = dataCountCheck.getData();
        String alertInfo = "";
        // 如果要填加的执行对象已经被在数据库中，则不允许增加
        if (!IDataUtil.isEmpty(ruleSet))
        {
            alertInfo = "执行对象" + info.getString("CLASS_NAME", "") + "已经被添加,请参照规则" + ruleSet.getData(0).getString("RULE_NAME");
            this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
            return;
        }

        IDataOutput dataCountRuleId = CSViewCall.callPage(this, "SS.ManageRuleSVC.getRuleID", param, null);
        // 得到规则的所有信息
        String ruleID = dataCountRuleId.getData().getData(0).getString("RULE_ID");
        param.put("RULE_ID", ruleID);
        param.put("RULE_NAME", info.getString("RULE_NAME", ""));
        param.put("PARAM_TYPE", info.getString("RULE_TYPE", ""));
        param.put("PARAM_A", info.getString("PARAM_A", ""));
        param.put("PARAM_B", info.getString("PARAM_B", ""));
        param.put("START_TIME", info.getString("START_TIME", ""));
        param.put("END_TIME", info.getString("END_TIME", ""));
        param.put("MONITOR_OBJECT", info.getString("MONITOR_OBJECT", ""));
        param.put("CYCLE_TYPE", info.getString("CYCLE_TYPE", ""));
        param.put("CLASS_NAME", info.getString("CLASS_NAME", ""));
        param.put("ALARM_LEVEL", info.getString("ALARM_LEVEL", ""));
        param.put("DEAL_METHOD", "0");
        param.put("REMOVE_TAG", "0");
        param.put("USE_TAG", "0");
        param.put("UPDATE_STAFF_ID", this.getVisit().getStaffId());
        param.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());

        // 插入规则信息到TD_RULE_FORALARM
        IDataOutput dataCountAdd = CSViewCall.callPage(this, "SS.ManageRuleSVC.insertRuleToAlarm", param, null);
        IDataset resultsAdd = dataCountAdd.getData();
        String addSuccessFlag = resultsAdd.getData(0).getString("ADD_SUCCESS_FLAG");
        this.setAjax("ADD_SUCCESS_FLAG", addSuccessFlag);
        if ("1".equals(addSuccessFlag))
        {
            alertInfo = "规则增加成功";
            this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        }

    }

    /**
     * 删险业务风险告警规则. 1.当此规则从未启用,即在任务表(TD_M_TASK与TD_M_TASK_PLAN)表中并不存在相应的执行信息,给要改变其禁用标识
     * 2.当此规则曾被启用过,即在任务表(TD_M_TASK与TD_M_TASK_PLAN)表中并在相应的执行信息,在改规则禁用标识同时 还要禁用TASK表与TASK_PLAN表的执行信息.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void delRuleBatch(IRequestCycle cycle) throws Exception
    {
        // 得到所有要启用规则标识字符串
        IData tabData = getData();
        IData cond = this.getData("MAINCOND", true);

        String ruleIDs = getData("multi", true).getString("RULE_IDs", "");
        String alertInfo = "";
        // 操作标识非空判断
        if ("".equals(ruleIDs))
        {
            alertInfo = "请先选中要操作的数据";
            this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
            return;
        }
        String ruleIds = getRuleIds(tabData);
        tabData.put("RULE_ID", ruleIds);
        IDataOutput dataCheck = CSViewCall.callPage(this, "SS.ManageRuleSVC.queryRuleByPKSpecial", tabData, null);
        IDataset resultsCheck = dataCheck.getData();
        if (IDataUtil.isEmpty(resultsCheck))
        {
            alertInfo = "该规则对应信息不存在";
            this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
            return;
        }
        IDataOutput dataCountDel = CSViewCall.callPage(this, "SS.ManageRuleSVC.delRuleBatch", tabData, null);
        IDataset resultsDel = dataCountDel.getData();
        this.setCond(cond);
        queryRulesByCond(cycle);
        this.setAjax("DELETE_SUCCESS_FLAG", resultsDel.getData(0).getString("DELETE_SUCCESS_FLAG"));
    }

    /**
     * 批量禁用业务风险告警规则.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void disenableRuleBatch(IRequestCycle cycle) throws Exception
    {
        IData cond = this.getData("MAINCOND", true);
        IData tabData = getData();
        String alertInfo = "";
        String ruleData = tabData.getString("multi_RULE_IDs");
        IDataset ids = new DatasetList(ruleData);
        String useTag = "";
        for (int i = 0; i < ids.size(); i++)
        {
            useTag = ids.getData(i).getString("USE_TAG");
            if ("禁用".equals(useTag))
            {
                alertInfo = "所选记录的状态已经为【禁用】,不能做此操作!";
                this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
                return;
            }
        }
        IDataOutput dataCountDisenable = CSViewCall.callPage(this, "SS.ManageRuleSVC.disenableRuleBatch", tabData, null);
        IDataset resultsDisenable = dataCountDisenable.getData();
        this.setCond(cond);
        queryRulesByCond(cycle);
        this.setAjax("DISENABLE_SUCCESS_FLAG", resultsDisenable.getData(0).getString("DISENABLE_SUCCESS_FLAG"));
    }

    /**
     * 编辑业务风险告警的规则信息. 修改业务风险告警的规则信息,包括规则名称、判断规则、取值一、取值二、生效时间、失效时间.
     * 注意事项：如果修改的信息包括生效时间、失效时间且该规则有配置对应的任务类，即规则表(TD_RULE_FORALARM) 的任务信息字段(RSRV_STR1)不为空,需要编辑相应的任务类的信息.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void editRule(IRequestCycle cycle) throws Exception
    {

        // 得到前台传过来的参数信息
        IData newRuleData = getData("cond", true);
        // 得到规则标识(RULE_ID)
        String ruleID = newRuleData.getString("RULE_ID");

        // 保存逻辑操作的参数信息
        IData param = new DataMap();
        // 修改后的规则信息

        // 将规则标识放入参数容器中
        param.put("RULE_ID", ruleID);
        // 检索规则是不是已经启用过

        IDataOutput dataCount = CSViewCall.callPage(this, "SS.ManageRuleSVC.queryRuleByPK", newRuleData, null);
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "所要禁用的规则信息不存在,请与管理员联系";
            this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
            return;
        }
        IData ruleData = results.getData(0);
        /*
         * 判断规则是否处理禁用状态,只有规则处于禁用状态才可以被修改
         */
        if (!ruleData.getString("USE_TAG").equals("0"))
        {
            alertInfo = "只有当规则处理禁用状态才可以被修改.";
            this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
            return;
        }

        // 规则表中的伤务类信息
        String taskID = ruleData.getString("TASK_ID", "");

        // 装载要进行变更的规则信息
        // 清空参数信息
        param.clear();
        // 将页面得到的参数全部放入参数容器
        // 规则名称
        param.put("RULE_NAME", newRuleData.getString("RULE_NAME", ""));
        // 参数类型
        param.put("PARAM_TYPE", newRuleData.getString("RULE_TYPE", ""));
        // 参数A
        param.put("PARAM_A", newRuleData.getString("PARAM_A", ""));
        // 参数B
        param.put("PARAM_B", newRuleData.getString("PARAM_B", ""));
        // 生效时间
        param.put("START_TIME", newRuleData.getString("START_TIME", ""));
        // 失效时间
        param.put("END_TIME", newRuleData.getString("END_TIME", ""));
        // 执行对象
        param.put("CLASS_NAME", newRuleData.getString("CLASS_NAME", ""));
        // 告警等级
        param.put("ALARM_LEVEL", newRuleData.getString("ALARM_LEVEL", ""));
        // 监控对象
        param.put("MONITOR_OBJECT", newRuleData.getString("MONITOR_OBJECT", ""));
        // 规则周期
        param.put("CYCLE_TYPE", newRuleData.getString("CYCLE_TYPE", ""));
        // 规则标识
        param.put("RULE_ID", ruleID);

        // 判断所要填加的规则执行对象是否已经被加载了，如果已经被加载了不能再次被添加
        IDataOutput dataCountCheck = CSViewCall.callPage(this, "SS.ManageRuleSVC.checkClassIsUpdate", newRuleData, null);
        IDataset ruleSet = dataCountCheck.getData();
        // 如果要填加的执行对象已经被在数据库中，则不允许增加
        if (!IDataUtil.isEmpty(ruleSet))
        {
            alertInfo = "执行对象" + param.getString("CLASS_NAME", "") + "已经被添加,请参照规则" + ruleSet.getData(0).getString("RULE_NAME");
            this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
            return;
        }
        // 更新规则信息
        CSViewCall.callPage(this, "SS.ManageRuleSVC.updateRule", newRuleData, null);
        // 哪果该规则已配置任务标识(TASK_ID,即规则表的RSRV_STR1)，如果进行生效与失效时间变更，要相应的
        // 变更任务类中的相关信息，即(TASK_PLAN表的PLAN字段)
        if (!"".equals(taskID))
        {
            // 当规则的类名、生效时间、失效时间、执行周期变化后，对应的TASK执行记录也要发生相应的变化
            boolean startTimeFlag = ruleData.getString("START_TIME").equals(newRuleData.getString("START_TIME"));
            boolean endTimeFlag = ruleData.getString("END_TIME").equals(newRuleData.getString("END_TIME"));
            boolean classNameFlag = ruleData.getString("CLASS_NAME", "").equals(newRuleData.getString("CLASS_NAME"));
            boolean cycleTypeFlag = ruleData.getString("CYCLE_TYPE", "").equals(newRuleData.getString("CYCLE_TYPE"));

            // 只要生效时间、失效时间、类名有一项发生变化则需要对TASK表与TASK_PLAN表中的相关记录进行变更
            if (!(startTimeFlag && endTimeFlag && classNameFlag && cycleTypeFlag))
            {
                // 检索出规则信息

                IDataOutput dataCountRule = CSViewCall.callPage(this, "SS.ManageRuleSVC.queryRuleByPK", newRuleData, null);
                IDataset resultsRule = dataCountRule.getData();
                if (IDataUtil.isEmpty(results))
                {
                    alertInfo = "所要禁用的规则信息不存在,请与管理员联系";
                    this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
                    return;
                }
                IData rule = resultsRule.getData(0);

                // 清空参数容器
                param.clear();
                param.put("TASK_ID", rule.getString("TASK_ID", ""));

                // 是否该规则处理正在运行的状态，如果是运行禁止修改相关的信息
                IDataOutput dataCountIsRuning = CSViewCall.callPage(this, "SS.ManageRuleSVC.isRunning", newRuleData, null);
                IDataset resultsIsRunning = dataCountIsRuning.getData();
                if (!IDataUtil.isEmpty(resultsIsRunning))
                {
                    alertInfo = "规则" + param.getString("RULE_ID") + "处于正在运行的状态，请稍后再试";
                    this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
                    return;
                }
                /*
                 * 检索出执行TASK类的具体时间，具体到时、分、秒
                 */
                // 执行规则的具体时间,精确到时分秒
                IDataOutput dataCountExecTime = CSViewCall.callPage(this, "SS.ManageRuleSVC.queryExecTime", newRuleData, null);
                IDataset resultsExecTime = dataCountExecTime.getData();
                if (IDataUtil.isEmpty(resultsExecTime))
                {
                    alertInfo = "未在TAG表中配置TASK类的具体执行时间，请与管理员联系";
                    this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
                    return;
                }

                // 执行时间
                String execTime = resultsExecTime.getData(0).getString("TAG_INFO", "");
                /*
                 * 更新TASK表的类名字段以及TASK_PLAN表的PLAN字段
                 */
                // 清空参数
                param.clear();
                // 任务标识(TASK_ID)
                param.put("TASK_ID", rule.getString("TASK_ID", ""));
                // 新类名
                param.put("CLASS_NAME", newRuleData.getString("CLASS_NAME", "").trim());
                // PLAN字段
                String plan = generatePlan(newRuleData, execTime);
                param.put("PLAN", plan);

                // 如果执行对象发生变化, 更新TASK表的执行对象信息
                if (!classNameFlag)
                {
                    CSViewCall.callPage(this, "SS.ManageRuleSVC.updateTaskClassName", newRuleData, null);
                }

                // 如果规则运行周期、失效时间、生效时间有一样发现变化，那么更新TASK_PLAN表的PLAN字段
                if (!(startTimeFlag && endTimeFlag && cycleTypeFlag))
                {
                    CSViewCall.callPage(this, "SS.ManageRuleSVC.updateTaskPlan", newRuleData, null);
                }
            }
        }
        alertInfo = "业务风险告警规则编辑成功";
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
    }

    /**
     * 批量启用业务风险告警规则.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void enableRuleBatch(IRequestCycle cycle) throws Exception
    {
        IData cond = this.getData("MAINCOND", true);
        IData tabData = getData();
        String alertInfo = "";
        String ruleData = tabData.getString("multi_RULE_IDs");
        IDataset ids = new DatasetList(ruleData);
        String useTag = "";
        for (int i = 0; i < ids.size(); i++)
        {
            useTag = ids.getData(i).getString("USE_TAG");
            if ("启用".equals(useTag))
            {
                alertInfo = "所选记录的状态已经为【启用】,不能做此操作!";
                this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
                return;
            }
        }

        // 检索出执行TASK类的具体时间，具体到时、分、秒
        // 执行规则的具体时间,精确到时分秒
        IDataOutput dataCountExecTime = CSViewCall.callPage(this, "SS.ManageRuleSVC.queryExecTime", tabData, null);
        IDataset resultsExecTime = dataCountExecTime.getData();
        if (IDataUtil.isEmpty(resultsExecTime))
        {
            alertInfo = "未在TAG表中配置TASK类的具体执行时间，请与管理员联系";
            this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
            return;
        }
        // 执行时间
        String execTime = resultsExecTime.getData(0).getString("TAG_INFO", ""); // "23:00:00";//
        tabData.put("EXEC_TIME", execTime);
        IDataOutput dataCountEnable = CSViewCall.callPage(this, "SS.ManageRuleSVC.enableRuleBatch", tabData, null);
        IDataset resultsEnable = dataCountEnable.getData();
        this.setCond(cond);
        queryRulesByCond(cycle);
        this.setAjax("ENABLE_SUCCESS_FLAG", resultsEnable.getData(0).getString("ENABLE_SUCCESS_FLAG"));

    }

    public String getRuleIds(IData tabData) throws Exception
    {
        String ruleIdList = tabData.getString("multi_RULE_IDs");
        IDataset ids = new DatasetList(ruleIdList);
        String ruleIds = "";
        // 将规则ID进行分割并保存在参数容器中
        for (int i = 0; i < ids.size(); i++)
        {
            ruleIds += ids.getData(i).getString("RULE_ID");
            if (ids.size() > 1 && i < ids.size() - 1)
                ruleIds += ",";
        }
        return ruleIds;
    }

    /**
     * 初始化界面，在修改业务风险告警规则时，将对应的规则信息写入弹出界面中.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        // 构造参数容器
        IData param = new DataMap();
        // 放入参数
        param.put("RULE_ID", data.getString("cond_RULE_ID"));
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.ManageRuleSVC.queryRuleByPK", data, null);
        IDataset results = dataCount.getData();
        // 根据规则标识查询规则信息
        // 加载执行对象以及告警等级选项
        this.queryCondition(cycle);
        // 将得到规则信息设置到修改界面上
        if (IDataUtil.isNotEmpty(results))
        {
            IData ruleData = results.getData(0);
            this.setRuleData(ruleData);
        }

    }

    public void queryClasses(IRequestCycle cycle) throws Exception
    {

        IData input = new DataMap();
        input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        IDataset classNames = CSViewCall.call(this, "SS.ManageRuleSVC.queryClassNames", input);
        if (IDataUtil.isNotEmpty(classNames))
        {
            /*
             * for (int i = 0; i < classNames.size(); i++) { String dataName =
             * classNames.getData(i).getString("PARAM_NAME"); String dataId =
             * classNames.getData(i).getString("PARA_CODE19"); classNames.getData(i).put("PARAM_NAME", "[" + dataId +
             * "]" + dataName); }
             */
            setClasses(classNames);
        }
    }

    public void queryCondition(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        // 设置等级
        queryLevels(cycle);
        // 设置类句
        queryClasses(cycle);
        this.setCond(data);

    }

    public void queryLevels(IRequestCycle cycle) throws Exception
    {

        IData input = new DataMap();
        input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        IDataset levels = CSViewCall.call(this, "SS.ManageRuleSVC.queryLevels", input);
        if (IDataUtil.isNotEmpty(levels))
        {
            for (int i = 0; i < levels.size(); i++)
            {
                String dataName = levels.getData(i).getString("DATA_NAME");
                String dataId = levels.getData(i).getString("DATA_ID");
                levels.getData(i).put("DATA_NAME", "[" + dataId + "]" + dataName);
            }
            setLevels(levels);
        }
    }

    public void queryRulesByCond(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.ManageRuleSVC.queryRulesByCond", inparam, getPagination("navt"));
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "所要禁用的规则信息不存在,请与管理员联系";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setRuleSet(results);
        setCount(dataCount.getDataCount());
        setCond(getData("cond", true));
    }

    public abstract void setClasses(IDataset classes);

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setLevels(IDataset levels);

    public abstract void setRuleData(IData rule);

    public abstract void setRuleSet(IDataset ruleSet);
}
