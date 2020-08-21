
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.score;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryScoreRewardSumSVC extends CSBizService
{

    /**
     * 查询业务区
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryArea(IData param) throws Exception
    {
        String eparchyCode = param.getString("EPARCHY_CODE");
        QueryScoreRewardSumBean bean = BeanManager.createBean(QueryScoreRewardSumBean.class);
        IDataset results = bean.queryArea(eparchyCode);
        return results;
    }

    /**
     * 查询市县兑奖情况(不分页)
     * 
     * @param data
     * @return dataset
     * @throws Exception
     */
    public IDataset queryCityScoreExchange(IData data) throws Exception
    {
        String startDate = data.getString("cond_START_DATE");
        String endDate = data.getString("cond_END_DATE");
        String ruleId = data.getString("cond_RULE_ID");
        String tradeCityCode = data.getString("cond_TRADE_CITY_CODE");
        String departKindCode = data.getString("cond_DEPART_KIND_CODE");
        QueryScoreRewardSumBean bean = BeanManager.createBean(QueryScoreRewardSumBean.class);
        IDataset results = bean.queryCityScoreExchange(startDate, endDate, ruleId, tradeCityCode, departKindCode,this.getPagination());
        return results;
    }

    /**
     * 查询部门类型
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryDepartKind(IData param) throws Exception
    {
        String eparchyCode = param.getString("EPARCHY_CODE");
        QueryScoreRewardSumBean bean = BeanManager.createBean(QueryScoreRewardSumBean.class);
        IDataset results = bean.queryDepartKind(eparchyCode);
        return results;
    }

    public IData queryInitCondition(IData param) throws Exception
    {
        IData result = new DataMap();
        String startDate = SysDateMgr.getFirstDayOfThisMonth();
        String endDate = SysDateMgr.getSysDate();
        result.put("START_DATE", startDate);
        result.put("END_DATE", endDate);
        return result;
    }

    /**
     * 查询兑奖编码
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryRules(IData param) throws Exception
    {
        String eparchyCode = param.getString("EPARCHY_CODE");
        QueryScoreRewardSumBean bean = BeanManager.createBean(QueryScoreRewardSumBean.class);
        IDataset results = bean.queryRules(eparchyCode);
        return results;
    }

    /**
     * 查询用户兑奖情况(不分页)
     * 
     * @param data
     * @return dataset
     * @throws Exception
     */
    public IDataset queryUserScoreExchange(IData data) throws Exception
    {
        String tradeFlag = data.getString("cond_TRADE");
        String startDate = data.getString("cond_START_DATE");
        String endDate = data.getString("cond_END_DATE");
        String tradeStaffIdS = data.getString("cond_TRADE_STAFF_ID_S");
        String tradeStaffIdE = data.getString("cond_TRADE_STAFF_ID_E");
        String tradeDepartId = data.getString("cond_TRADE_DEPART_ID");
        QueryScoreRewardSumBean bean = BeanManager.createBean(QueryScoreRewardSumBean.class);
        //IDataset results = bean.queryUserScoreExchange(tradeFlag, startDate, endDate, tradeStaffIdS, tradeStaffIdE, tradeDepartId,this.getPagination());
        IDataset results = bean.queryUserScoreExchange(tradeFlag, startDate, endDate, tradeStaffIdS, tradeStaffIdE, tradeDepartId);

        return results;
    }
}
