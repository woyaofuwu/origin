
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ExchangeRuleInfoQry
{

    /**
     * 根据规则ID查询规则
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset queryByRuleId(String ruleId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("RULE_ID", ruleId);
        return Dao.qryByCode("TD_B_EXCHANGE_RULE", "SEL_RULE_BY_PK", data, Route.CONN_CRM_CEN);
    }

    /**
     * 查询分值组
     * 
     * @param EPARCHY_CODE
     * @param BRAND_CODE
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset queryExchangeCent(String EPARCHY_CODE, String BRAND_CODE, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", EPARCHY_CODE);
        param.put("BRAND_CODE", BRAND_CODE);
        return Dao.qryByCode("TD_B_EXCHANGE_RULE", "SEL_BY_SCORE", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryexchangeList(String eparchyCode, String score, String brandCode) throws Exception
    {
        IData param = new DataMap();
        // 查询前赋值
        param.put("BRAND_CODE", "VIP1");
        param.put("SCORE", score);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_EXCHANGE_RULE", "SEL_BY_EPARCHY_2", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询可兑换的实物
     * 
     * @param EPARCHY_CODE
     * @param BRAND_CODE
     * @param SCORE
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset queryExchangeObject(String EPARCHY_CODE, String BRAND_CODE, String SCORE, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", EPARCHY_CODE);
        param.put("BRAND_CODE", BRAND_CODE);
        param.put("SCORE", SCORE);
        return Dao.qryByCode("TD_B_EXCHANGE_RULE", "SEL_BY_GOODS", param, page, Route.CONN_CRM_CEN);
    }

    /**
     * 积分兑换规则查询
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryExchangeRule(IData data, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TD_B_EXCHANGE_RULE", "SEL_RULE_1", data, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 查询兑换类型
     * 
     * @param EPARCHY_CODE
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset queryExchangeType(String EPARCHY_CODE, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", EPARCHY_CODE);
        return Dao.qryByCode("TD_B_SCORE_EXCHANGE_TYPE", "SEL_SCORE_EXCHANGE_TYPE", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryExchRuleByRuleId(String ruleId) throws Exception
    {
        IData param = new DataMap();
        param.put("RULE_ID", ruleId);
        return Dao.qryByCode("TD_B_EXCHANGE_RULE", "SEL_EXCHGRULE_BY_PK", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryExRuleByEparchy1(String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_EXCHANGE_RULE", "SEL_BY_EPARCHY1", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryExRuleByRuleId(String eparchyCode, String ruleId) throws Exception
    {
        IData param = new DataMap();
        param.put("PARA_CODE1", eparchyCode);
        param.put("PARA_CODE2", ruleId);
        return Dao.qryByCode("TD_B_EXCHANGE_RULE", "SEL_RULEINFO_BY_ID", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryExRuleByScore(String score, String eparchyCode, String brandCode) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("SCORE", score);
        param.put("BRAND_CODE", brandCode);
        return Dao.qryByCode("TD_B_EXCHANGE_RULE", "SEL_RULE_BY_SCORE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryExRuleByVipBrandCode(String score,String brandCode,String eparchyCode) throws Exception
    {
    	IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("SCORE", score);
        param.put("BRAND_CODE", brandCode);
        return Dao.qryByCode("TD_B_EXCHANGE_RULE", "SEL_BY_EPARCHY", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryExRuleByVipBrandCode3(IData param) throws Exception
    {
        return Dao.qryByCode("TD_B_EXCHANGE_RULE", "SEL_BY_EPARCHY_3", param, Route.CONN_CRM_CEN);
    }
    
    
    public static IDataset queryCityScoreExchange2(String ruleId) throws Exception
    {
    	IData param = new DataMap();
        param.put("RULE_ID", ruleId);
        return Dao.qryByCode("TD_B_EXCHANGE_RULE", "SEL_BY_RULEID_DEPART", param, Route.CONN_CRM_CEN);
    }
}
