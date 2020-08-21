
package com.asiainfo.veris.crm.order.web.person.taskalarm.managetask;

import java.net.URLDecoder;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 在统计类告警过程中,需要对指 定业务进行告警,该类主要完成对要进行监控的业务进行配置.
 */
public abstract class ManageTask extends PersonBasePage
{

    public void addTask(IRequestCycle cycle) throws Exception
    {

        // 参数容器
        IData param = new DataMap();

        // 数据信息
        IData data = getData("add", true);
        // 用于查询结果的刷新
        IData paramCond = this.getData("cond", true);
        // 判断该业务是否已经配置
        // SUBSYS_CODE
        param.put("SUBSYS_CODE", "CSM");
        // 配置PARAM_ATTR
        param.put("PARAM_ATTR", "9984");
        // 得到业务编码
        param.put("PARAM_CODE", data.getString("TRADE_TYPE_CODE", ""));
        // 判断所要配置的业务编码是否已配置过
        param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.ManageTaskSVC.isTaskConfigured", param, getPagination("navt"));
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (!IDataUtil.isEmpty(results))
        {
            alertInfo = "该业务已经配置";
            this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
            return;
        }
        /*
         * 数据准备将要配置的数据写入静态表中.
         */
        // 配置业务类型
        param.put("PARAM_NAME", data.getString("TRADE_TYPE"));
        // 监控该业务的阀值
        param.put("PARA_CODE1", data.getString("TRADE_TYPE_VALUE"));
        // 地州编码
        param.put("EPARCHY_CODE", "ZZZZ");
        // 配置生效时间
        param.put("START_DATE", SysDateMgr.getSysTime());// 格式为YYYY-MM-DD
        // 配置失效时间
        param.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        // 加入到配置的业务信息表中
        IDataOutput dataCountAdd = CSViewCall.callPage(this, "SS.ManageTaskSVC.insertTaskConfiguration", param, null);
        IDataset resultsAdd = dataCountAdd.getData();
        // this.setCondition(paramCond);
        IData cond = this.getData("MAINCOND", true);
        this.setCondition(cond);
        // queryConfiguredTask(cycle);
        this.setAjax("ADD_SUCCESS_FLAG", resultsAdd.getData(0).getString("ADD_SUCCESS_FLAG"));
    }

    /**
     * 根据业务编码删除监控改业务对应的域值.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void delTask(IRequestCycle cycle) throws Exception
    {
        IData cond = this.getData("MAINCOND", true);
        IData tabData = getData();
        // IData paramIData = new DataMap ();
        // paramIData.put("dataSet" , paramSet);
        IDataOutput dataCountDel = CSViewCall.callPage(this, "SS.ManageTaskSVC.delConfiguredTask", tabData, null);
        IDataset resultsDel = dataCountDel.getData();
        this.setCondition(cond);
        queryConfiguredTask(cycle);
        this.setAjax("DELETE_SUCCESS_FLAG", resultsDel.getData(0).getString("DELETE_SUCCESS_FLAG"));
    }

    /**
     * 根据业务编码修改监控改业务对应的域值.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void editTask(IRequestCycle cycle) throws Exception
    {
        // 参数容器
        IData param = new DataMap();
        IData cond = this.getData("MAINCOND", true);
        IData data = getData("edit", true);

        IData dataCond = getData("cond", true);
        // 得到前台传过来的传数信息并设置到参数容器中
        // 业务编码
        param.put("PARAM_CODE", data.getString("TRADE_TYPE_CODE"));
        // 业务名称
        param.put("PARAM_NAME", data.getString("TRADE_TYPE"));
        // 监控业务对应的域值
        param.put("PARA_CODE1", data.getString("TRADE_TYPE_VALUE"));
        // 监控业务对应的类型
        param.put("PARAM_ATTR", "9984");
        // 更新对应业务的阀值
        // 加入到配置的业务信息表中
        IDataOutput dataCountUpdate = CSViewCall.callPage(this, "SS.ManageTaskSVC.updateConfiguredTask", param, null);
        IDataset resultsUpdate = dataCountUpdate.getData();
        this.setCondition(cond);
        // queryConfiguredTask4EditAdd(cycle);
        this.setAjax("UPDATE_SUCCESS_FLAG", resultsUpdate.getData(0).getString("UPDATE_SUCCESS_FLAG"));
    }

    /**
     * 为已配置业务修改其域值设置条件信息.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void initPage(IRequestCycle cycle) throws Exception
    {

        // 得到条件信息
        IData condition = getData("cond", true);
        String tradeType = URLDecoder.decode(condition.getString("TRADE_TYPE"), "UTF-8");
        // condition.put("TRADE_TYPE", new String(condition.getString("TRADE_TYPE").getBytes("iso8859-1"),"UTF-8"));
        condition.put("TRADE_TYPE", tradeType);
        // 设置到业面上
        setCondition(condition);
    }

    /**
     * 按条件查询已经配置的业务.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void queryConfiguredTask(IRequestCycle cycle) throws Exception
    {
        // 数据信息
        IData data = getData("cond", true);
        // 参数容器
        IData param = new DataMap();

        // TYPE_ID
        param.put("SUBSYS_CODE", "CSM");
        // 配置PARAM_ATTR
        param.put("PARAM_ATTR", "9984");
        // 得到前台传过来的业务编码
        param.put("PARAM_CODE", data.getString("TRADE_TYPE_CODE", ""));
        // 得到前台传过来的业务名称
        param.put("PARAM_NAME", data.getString("TRADE_TYPE", ""));
        // 地州编码
        param.put("EPARCHY_CODE", "ZZZZ");
        // 按业务编码检索业务信息
        param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.ManageTaskSVC.queryConfiguredTask", param, getPagination("navt"));
        IDataset taskInfos = dataCount.getData();
        // 设置到业面
        setTaskInfos(taskInfos);
        setCount(dataCount.getDataCount());
    }

    /**
     * 根据业务编码得到对应的业务说细信息.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void queryTaskByCode(IRequestCycle cycle) throws Exception
    {

        // 数据信息
        IData param = getData("cond", true);

        // 按业务编码检索业务信息
        param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.ManageTaskSVC.queryTaskByCode", param, getPagination("navt"));
        IDataset task = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(task))
        {
            alertInfo = "检索的业务编码" + param.getString("TRADE_TYPE_CODE") + "不存在";
            this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
            return;
        }
        // 将所得到的业务信息放入condition中并加入页面流中
        IData condition = new DataMap();
        // 业务编号
        condition.put("cond_TRADE_TYPE_CODE", task.getData(0).getString("TRADE_TYPE_CODE"));
        // 业务名称
        condition.put("cond_TRADE_TYPE", task.getData(0).getString("TRADE_TYPE"));
        // 设置到页面上去
        setCondition(condition);
    }

    /**
     * 按条件检索业务信息,如果相应的条件都为空时,检索出所有的业务信息.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void queryTaskByCon(IRequestCycle cycle) throws Exception
    {
        // 参数容器
        IData param = new DataMap();

        // 得到页面传来的参数
        param = getData("cond", true);
        // 得到满足条件的业务信息

        param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.ManageTaskSVC.queryTaskByCon", param, getPagination("navt"));
        IDataset taskSet = dataCount.getData();
        // 将得到的业务信息设置到业面上
        this.setTaskInfos(taskSet);
    }

    // 设置条件
    public abstract void setCondition(IData condition);

    public abstract void setCount(long count);

    // 业务信息
    public abstract void setTaskInfos(IDataset taskInfos);

}
