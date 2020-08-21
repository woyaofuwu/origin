
package com.asiainfo.veris.crm.order.web.person.sundryquery.score;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：积分兑奖汇总小计 作者：GongGuang
 */
public abstract class QueryScoreRewardSum extends PersonBasePage
{
    /**
     * 页面初始化方法
     * 
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        IData result = CSViewCall.callone(this, "SS.QueryScoreRewardSumSVC.queryInitCondition", new DataMap());
        if (IDataUtil.isNotEmpty(result))
        {
            data.put("cond_START_DATE", result.getString("START_DATE")); // 得到本月的第一天
            data.put("cond_END_DATE", result.getString("END_DATE"));// 格式为YYYY-MM-DD
            setCondition(data);
            queryRules(cycle);
            queryArea(cycle);
            queryDepartKind(cycle);
        }
    }

    /**
     * 查询业务区
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryArea(IRequestCycle cycle) throws Exception
    {

        IData input = new DataMap();
        input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        IDataset areas = CSViewCall.call(this, "SS.QueryScoreRewardSumSVC.queryArea", input);
        if (IDataUtil.isNotEmpty(areas))
        {
            for (int i = 0; i < areas.size(); i++)
            {
                String areaName = areas.getData(i).getString("AREA_NAME");
                String areaCode = areas.getData(i).getString("AREA_CODE");
                areas.getData(i).put("AREA_NAME", "[" + areaCode + "]" + areaName);
            }
            setAreas(areas);
        }
    }

    /**
     * 查询市县兑奖情况
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCityScoreExchange(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput returnList = CSViewCall.callPage(this, "SS.QueryScoreRewardSumSVC.queryCityScoreExchange", data,getPagination("cityinfo"));
        if (IDataUtil.isEmpty(returnList.getData()))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_573);
        }
        setInfos(returnList.getData());
        setCondition(data);
        setCount(returnList.getDataCount());
    }

    /**
     * 查询部门类别
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryDepartKind(IRequestCycle cycle) throws Exception
    {
        IData input = new DataMap();
        input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        IDataset departKinds = CSViewCall.call(this, "SS.QueryScoreRewardSumSVC.queryDepartKind", input);
        if (IDataUtil.isNotEmpty(departKinds))
        {
            for (int i = 0; i < departKinds.size(); i++)
            {
                String departKind = departKinds.getData(i).getString("DEPART_KIND");
                String departKindCode = departKinds.getData(i).getString("DEPART_KIND_CODE");
                departKinds.getData(i).put("DEPART_KIND", "[" + departKindCode + "]" + departKind);
            }
            setDepartKinds(departKinds);
        }
    }

    /**
     * 查询兑奖项
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryRules(IRequestCycle cycle) throws Exception
    {
        IData input = new DataMap();
        input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        IDataset rules = CSViewCall.call(this, "SS.QueryScoreRewardSumSVC.queryRules", input);
        if (IDataUtil.isNotEmpty(rules))
        {
            for (int i = 0; i < rules.size(); i++)
            {
                String rule = rules.getData(i).getString("RULE_NAME");
                String ruleCode = rules.getData(i).getString("RULE_ID");
                rules.getData(i).put("RULE_NAME", "[" + ruleCode + "]" + rule);
            }
            setRules(rules);
        }
    }

    /**
     * 查询用户兑奖情况
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryUserScoreExchange(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
       // IDataOutput returnList = CSViewCall.callPage(this, "SS.QueryScoreRewardSumSVC.queryUserScoreExchange", data,getPagination("pageinfo"));
        IDataset returnList = CSViewCall.call(this, "SS.QueryScoreRewardSumSVC.queryUserScoreExchange", data);

        String alertInfo = "";
        if (IDataUtil.isEmpty(returnList))
        {
            alertInfo = "没有符合条件的数据";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setInfos(returnList);
        setCondition(data);
        setCount(returnList.size());
    }

    public abstract void setCount(long count);
    
    public abstract void setAreas(IDataset areas);

    public abstract void setCondition(IData condition);

    public abstract void setDepartKinds(IDataset departKinds);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRules(IDataset rules);
}
