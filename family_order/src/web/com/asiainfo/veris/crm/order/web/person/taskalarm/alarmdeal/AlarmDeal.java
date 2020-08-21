
package com.asiainfo.veris.crm.order.web.person.taskalarm.alarmdeal;

import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import com.wade.web.v4.tapestry.component.chart.ChartManager;
import com.wade.web.v4.tapestry.component.chart.info.BarChart;
import com.wade.web.v4.tapestry.component.chart.info.KeyValue;

/**
 * 功能：规则管理 作者：GongGuang
 */
public abstract class AlarmDeal extends PersonBasePage
{

    public static String getEncodeStr(String value)
    {
        StringBuilder valuestr = new StringBuilder();
        char chars[] = new char[value.length()];
        value.getChars(0, chars.length, chars, 0);
        for (int i = 0; i < chars.length; i++)
            if (i == 0)
                valuestr.append("%" + Integer.toHexString(chars[i]));
            else
                valuestr.append(chars[i]);

        return valuestr.toString();
    }

    public static String getParameters(Object source) throws Exception
    {
        return getParameters(source, null);
    }

    public static String getParameters(Object source, Object columns) throws Exception
    {
        StringBuilder str = new StringBuilder();
        IData sources = getSources(source, columns);
        if (sources != null)
        {
            Iterator it = sources.entrySet().iterator();
            do
            {
                if (!it.hasNext())
                    break;
                java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
                String key = (String) entry.getKey();
                String value = null;
                if (entry.getValue() instanceof String[])
                {
                    value = ((String[]) (String[]) entry.getValue())[0];
                    log.warn("parameter name " + key + " already repeated,default get first value.");
                }
                else
                {
                    value = (String) entry.getValue();
                }
                if (value != null && key != null && !"".equals(key) && !"".equals(value))
                    str.append("&" + getEncodeStr(key) + "=" + value.replaceAll("%", "%25").replaceAll("&", "%26").replaceAll("#", "%23").replaceAll("\\?", "%3F"));
            }
            while (true);
        }
        return str.toString();
    }

    public static IData getSources(Object source, Object columns)
    {
        if (source == null)
            return null;
        IData param = new DataMap();
        if (source instanceof IData)
        {
            IData data = (IData) source;
            if (columns == null)
            {
                param.putAll(data);
            }
            else
            {
                String cols[] = ((String) columns).split(",");
                for (int i = 0; i < cols.length; i++)
                {
                    Object value = data.get(cols[i]);
                    if (value != null)
                        param.put(cols[i], value);
                }

            }
        }
        if (source instanceof List)
        {
            List paramlist = (List) source;
            List columnlist = (List) columns;
            for (int i = 0; i < paramlist.size(); i++)
            {
                IData paramdata = (IData) paramlist.get(i);
                String columndata = columnlist != null ? (String) columnlist.get(i) : null;
                if (paramdata == null)
                    continue;
                if (columndata == null)
                {
                    param.putAll(paramdata);
                    continue;
                }
                String cols[] = columndata.split(",");
                for (int j = 0; j < cols.length; j++)
                {
                    Object value = paramdata.get(cols[j]);
                    if (value != null)
                        param.put(cols[j], value);
                }

            }

        }
        return param;
    }

    /**
     * 批量处理业务风险数据.处理关闭状态。0未关闭，1关闭
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void closeAlarm(IRequestCycle cycle) throws Exception
    {

        // 参数容器
        IData tabData = getData();
        // 用于查询结果的刷新
        IData param = this.getData("cond", true);
        // 如果已是已处理，则不允许再次处理
        // String alertInfo = "";
        // String alarmData=tabData.getString("multi_ALARM_ID");
        // IDataset ids = new DatasetList(alarmData);
        // String handleState="";
        /*
         * for (int i = 0; i < ids.size(); i++) { handleState = ids.getData(i).getString("RSRV_STR2"); if
         * ("已关闭".equals(handleState)){ alertInfo = "所选记录已经为【已关闭】,不能做此操作!"; this.setAjax("ALERT_INFO", alertInfo);//
         * 传给页面提示 return; } }
         */
        String alarmId = tabData.getString("multi_ALARM_ID");
        // String alarmId = getAlarmIds(tabData);
        tabData.put("ALARM_ID", alarmId);
        IDataOutput dataCountClose = CSViewCall.callPage(this, "SS.AlarmDealSVC.updAlarmClose", tabData, null);
        IDataset resultsClose = dataCountClose.getData();
        this.setAlarmData(param);
        queryAlarmByCond(cycle);
        // this.setAjax("ALERT_INFO", alertInfo);
        this.setAjax("UPDATE_SUCCESS_FLAG", resultsClose.getData(0).getString("UPDATE_SUCCESS_FLAG"));
    }

    /**
     * 处理业务风险数据.当系统管理员确认该业务风险数据后,需求改变该业务风险数据处理状态.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void dealAlarm(IRequestCycle cycle) throws Exception
    {
        // 参数容器
        IData param = new DataMap();

        /*
         * 处理业务风险数据
         */
        // 业务风险编号
        param.put("ALARM_ID", getData("cond", true).getString("ALARM_ID"));
        // 重置处理之后的业务风险状态
        // 处理业务风险信息
        IDataOutput dataCountDeal = CSViewCall.callPage(this, "SS.AlarmDealSVC.updAlarmState", param, null);
        IDataset resultsDeal = dataCountDeal.getData();
        this.setAlarmData(param);
        queryAlarmByCond(cycle);
        this.setAjax("DEAL_SUCCESS_FLAG", resultsDeal.getData(0).getString("DEAL_SUCCESS_FLAG"));
    }

    /**
     * 批量处理业务风险数据.当系统管理员确认该业务风险数据后,需求改变该业务风险数据处理状态.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void dealAlarmBatch(IRequestCycle cycle) throws Exception
    {
        // 参数容器
        IData tabData = getData();
        IData param = this.getData("cond", true);
        // 如果已是已处理，则不允许再次处理
        String alertInfo = "";
        String alarmData = tabData.getString("multi_ALARM_ID");
        IDataset ids = new DatasetList(alarmData);
        String handleState = "";
        for (int i = 0; i < ids.size(); i++)
        {
            handleState = ids.getData(i).getString("HANDLE_STATE");
            if ("已处理".equals(handleState))
            {
                alertInfo = "所选记录已经为【已处理】,不能做此操作!";
                this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
                return;
            }
        }

        String alarmIds = getAlarmIds(tabData);
        tabData.put("ALARM_ID", alarmIds);
        IDataOutput dataCountDeal = CSViewCall.callPage(this, "SS.AlarmDealSVC.updAlarmStateBatch", tabData, null);
        IDataset resultsDeal = dataCountDeal.getData();
        this.setAlarmData(param);
        // queryAlarmByCond(cycle);
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        this.setAjax("DEAL_SUCCESS_FLAG", resultsDeal.getData(0).getString("DEAL_SUCCESS_FLAG"));

    }

    public String getAlarmIds(IData tabData) throws Exception
    {
        String idList = tabData.getString("multi_ALARM_ID");
        IDataset ids = new DatasetList(idList);
        String alarmIds = "";
        // 将ID进行分割并保存在参数容器中
        for (int i = 0; i < ids.size(); i++)
        {
            alarmIds += ids.getData(i).getString("ALARM_ID");
            if (ids.size() > 1 && i < ids.size() - 1)
                alarmIds += ",";
        }
        return alarmIds;
    }

    /**
     * 在逐条处理业务风险数据时，将要处理的业务风险数据信息设置弹出页面.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void initPage(IRequestCycle cycle) throws Exception
    {
        // 得到条件信息
        IData condition = getData("cond", true);
        String newMessage = URLDecoder.decode(condition.getString("TASKWARNING_MESSAGE"), "UTF-8");
        // String newMessage =new String(condition.getString("TASKWARNING_MESSAGE").getBytes("iso8859-1"),"UTF-8");
        condition.put("TASKWARNING_MESSAGE", newMessage);

        // 设置到业面上
        setAlarmData(condition);
    }

    /**
     * 按条件(生效时间、失效时间、处理状态)检索业务风险数据.
     * 
     * @param cycle
     * @throws Exception
     *             抛出所有的异常信息
     */
    public void queryAlarmByCond(IRequestCycle cycle) throws Exception
    {
        IDataset alarmSet = null;

        // 参数容器
        IData param = new DataMap();

        /*
         * 检索业务风险数据
         */
        // 得到参数信息
        param = getData("cond", true);

        // 默认是1.表示全量查询信息
        String messageType = param.getString("MESSAGE_TYPE", "1");
        IDataOutput dataCount = null;
        if ("0".equals(messageType))
        {// 增量。当月信息
            param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
            dataCount = CSViewCall.callPage(this, "SS.AlarmDealSVC.queryAlarmByMonth", param, getPagination("navt"));
            alarmSet = dataCount.getData();
        }
        else
        {
            // 按条件检索业务风险数据
            param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
            dataCount = CSViewCall.callPage(this, "SS.AlarmDealSVC.queryAlarmByCond", param, getPagination("navt"));
            alarmSet = dataCount.getData();
        }
        String alertInfo = "";
        // 因为数据库中表中TF_B_TASKALARM中的HANDLE_STATE为固定长为2（多了一空格），因为在未修改数据库表结构的情况下，先做如下处理。
        if (IDataUtil.isNotEmpty(alarmSet))
        {
            for (int i = 0; i < alarmSet.size(); i++)
            {
                String handleState = alarmSet.getData(i).getString("HANDLE_STATE");
                alarmSet.getData(i).put("HANDLE_STATE", handleState.trim());
            }
        }
        if (IDataUtil.isEmpty(alarmSet))
        {
            alertInfo = "没有符合查询条件的【业务风险】数据~";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        // 将业务风险信息设置到页面 上
        setAlarmInfos(alarmSet);
        // 设置条件信息
        setAlarmData(getData("cond", false));
    }

    /**
     * 风险数据统计图表显示
     */
    public void queryChart(IRequestCycle cycle) throws Exception
    {

        IData param = getData();
        // 用于查询结果的刷新

        BarChart barchart = new BarChart();
        barchart.setTitle("业务风险数据统计"); // 设置标题
        barchart.setHorizontalTitle("风险业务"); // 设置横向标题
        barchart.setVerticalTitle("风险数"); // 设置纵向标题
        barchart.setWidth(600);// 设置宽度
        barchart.setHeight(400);// 设置高度
        barchart.setCutline(false); // 是否显示柱状图底下的图例说明，默认显示
        // 数据集
        IDataset dataset = queryLatestTradeDevs(param);

        ChartManager.createBarChart(cycle, barchart, dataset);

    }

    /**
     * 图表展示
     */
    public IDataset queryLatestTradeDevs(IData param) throws Exception
    {
        IDataOutput dataCountQueryChart = CSViewCall.callPage(this, "SS.AlarmDealSVC.queryChart", param, null);
        IDataset dataset = dataCountQueryChart.getData();
        IDataset bardataset = new DatasetList();
        KeyValue keyValue = null;
        for (int i = 0; i < dataset.size(); i++)
        {
            IData tmp = dataset.getData(i);
            keyValue = new KeyValue("规则ID：" + tmp.getString("RULE_ID"), tmp.getInt("CHART_COUNT"));
            bardataset.add(keyValue);
        }
        return bardataset;
    }

    // 检索业务风险条件信息
    public abstract void setAlarmData(IData alarmData);

    // 业务风险信息
    public abstract void setAlarmInfos(IDataset alarmInfos);

    public abstract void setCount(long count);

}
