
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryScoreRewardSumQry extends CSBizBean
{
    public static IDataset queryCityScoreExchange(String startDate, String endDate, String ruleId, String tradeCityCode, String departKindCode,Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("START_DATE", startDate);
        params.put("END_DATE", endDate);
        params.put("RULE_ID", ruleId);
        params.put("TRADE_CITY_CODE", tradeCityCode);
        params.put("DEPART_KIND_CODE", departKindCode);


        return Dao.qryByCodeParser("TF_B_TRADE_SCORE", "GET_SCORE_REWARDSUM2_NEW", params,pagination,Route.getJourDb(CSBizBean.getTradeEparchyCode()));//原sql拆分,duhj
    }
    


    /**
     * 查询部门类别
     * 
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset queryDepartKind(String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_M_DEPARTKIND", "SEL_DEPART_KIND", params);
    }

    /**
     * 查询兑奖项
     * 
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset queryRules(String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_EXCHANGE_RULE", "SEL_BY_EPARCHY1", params);
    }

    public static IDataset queryUserScoreExchange(String tradeFlag, String startDate, String endDate, String tradeStaffIdS, String tradeStaffIdE, String tradeDepartId) throws Exception
    {
        IData params = new DataMap();
        params.put("START_DATE", startDate);
        params.put("END_DATE", endDate);
        params.put("TRADE_STAFF_ID_S", tradeStaffIdS);
        params.put("TRADE_STAFF_ID_E", tradeStaffIdE);
        params.put("TRADE_DEPART_ID", tradeDepartId);
        if ("2".equals(tradeFlag))
        { // 如果是2，表示"所有(含返销)",否则为"正常(不含返销)"
            return Dao.qryByCodeParser("TF_B_TRADE_SCORE", "GET_SCORE_REWARDSUM_ALL_HAIN", params,Route.getJourDb(CSBizBean.getTradeEparchyCode()));//订单表改为jour用户,duhj
        }
        else
        {
            return Dao.qryByCodeParser("TF_B_TRADE_SCORE", "GET_SCORE_REWARDSUM_NEW", params,Route.getJourDb(CSBizBean.getTradeEparchyCode()));//订单表改为jour用户,duhj
        }

    }


    
}
