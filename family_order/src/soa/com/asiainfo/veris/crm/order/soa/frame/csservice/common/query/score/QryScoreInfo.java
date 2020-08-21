
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QryScoreInfo
{
    public static IDataset queryDepartScore(IData inparam, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_DEPART_SCORE", inparam, pagination);
    }

    public static IDataset queryExchangeList(String routeEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", routeEparchyCode);
        return Dao.qryByCode("TD_B_EXCHANGE_RULE", "SEL_ALL_RULE", param, Route.CONN_CRM_CEN);
    }

    /**
     * 异常订单查询
     * 
     * @param
     * @return
     * @author huangsl
     * @throws Exception
     */

    public static IDataset queryExpOrder(String serialNumber, String orderId, String parentId, String subId, String expType, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("ORDER_ID", orderId);
        param.put("PARENT_ID", parentId);
        param.put("SUB_ID", subId);
        param.put("EXPTYPE", expType);
        return Dao.qryByCode("TI_B_SCORE_EXPORDER", "SEL_EXPORDER", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 查询积分兑奖明细
     * 
     * @author zhuyu
     * @param serialNumber
     *            ,removeTag,startDate,endDate,tradeTypeCode,routeEparchyCode
     * @return dataset
     * @throws Exception
     */
    public static IDataset queryHistoryList(String serialNumber, String removeTag, String startDate, String endDate, String tradeTypeCode, String routeEparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("REMOVE_TAG", removeTag);
        data.put("START_DATE", startDate);
        data.put("END_DATE", endDate);
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("EPARCHY_CODE", routeEparchyCode);

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT TO_CHAR(B.TRADE_ID) TRADE_ID, ");
        parser.addSQL(" A.SERIAL_NUMBER,A.ACCEPT_MONTH,A.USER_ID, A.SCORE_TYPE_CODE, ");
        parser.addSQL(" A.SCORE,A.SCORE_CHANGED,TO_CHAR(NVL(A.VALUE_CHANGED / 100, 0)) VALUE_CHANGED, ");
        parser.addSQL(" A.RULE_ID,A.RES_ID,A.GOODS_NAME, A.REMARK,B.SERIAL_NUMBER SERIAL_NUMBER_B, ");
        parser.addSQL(" B.CUST_NAME,B.TRADE_TYPE_CODE,C.TRADE_TYPE,TO_CHAR(B.ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
        parser.addSQL(" B.TRADE_STAFF_ID,DECODE(B.CANCEL_TAG,'0','未返销','1','被返销','2','返销') CANCEL_TAG,B.CANCEL_DATE, ");
        parser.addSQL(" B.CANCEL_STAFF_ID,B.RSRV_STR5,B.RSRV_STR6,B.RSRV_STR7  ");
        parser.addSQL(" FROM TF_B_TRADE_SCORE A, TF_B_TRADE B, TD_S_TRADETYPE C WHERE A.TRADE_ID = B.TRADE_ID(+) ");
        parser.addSQL(" AND B.TRADE_TYPE_CODE = C.TRADE_TYPE_CODE ");
        parser.addSQL(" AND B.EPARCHY_CODE = :EPARCHY_CODE  ");
        parser.addSQL(" AND C.TRADE_TYPE_CODE =:TRADE_TYPE_CODE   ");
        parser.addSQL(" AND C.EPARCHY_CODE = :EPARCHY_CODE ");
        parser.addSQL(" AND A.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND B.ACCEPT_DATE BETWEEN TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND ");
        parser.addSQL(" TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') + 1 ");
        parser.addSQL(" UNION ALL ");
        parser.addSQL(" SELECT TO_CHAR(B.TRADE_ID) TRADE_ID, ");
        parser.addSQL(" A.SERIAL_NUMBER,A.ACCEPT_MONTH,A.USER_ID, A.SCORE_TYPE_CODE, A.SCORE, A.SCORE_CHANGED, ");
        parser.addSQL(" TO_CHAR(NVL(A.VALUE_CHANGED / 100, 0)) VALUE_CHANGED, ");
        parser.addSQL(" A.RULE_ID,A.RES_ID,A.GOODS_NAME,A.REMARK,B.SERIAL_NUMBER SERIAL_NUMBER_B, ");
        parser.addSQL(" B.CUST_NAME, B.TRADE_TYPE_CODE,C.TRADE_TYPE,TO_CHAR(B.ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
        parser.addSQL(" B.TRADE_STAFF_ID,DECODE(B.CANCEL_TAG,'0','未返销','1','被返销','2','返销') CANCEL_TAG, B.CANCEL_DATE, ");
        parser.addSQL(" B.CANCEL_STAFF_ID,B.RSRV_STR5,B.RSRV_STR6,B.RSRV_STR7 ");
        parser.addSQL(" FROM TF_B_TRADE_SCORE A, TF_BH_TRADE B, TD_S_TRADETYPE C ");
        parser.addSQL(" WHERE A.TRADE_ID = B.TRADE_ID(+) ");
        parser.addSQL(" AND B.TRADE_TYPE_CODE = C.TRADE_TYPE_CODE ");
        parser.addSQL(" AND B.EPARCHY_CODE = :EPARCHY_CODE  ");
        parser.addSQL(" AND C.TRADE_TYPE_CODE =:TRADE_TYPE_CODE   ");
        parser.addSQL(" AND C.EPARCHY_CODE = :EPARCHY_CODE  ");
        parser.addSQL(" AND A.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND B.ACCEPT_DATE+0 BETWEEN TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND ");
        parser.addSQL(" TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') + 1 ");
        parser.addSQL(" ORDER BY TRADE_ID DESC ");
        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 查询积分业务情况
     * 
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryScoreBizInfos(IData inparam, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_TRADESCORE", inparam, pagination, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    /**
     * 导出用
     * 
     * @param inparam
     * @param pagination
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset queryScoreBizInfos(IData inparam, Pagination pagination, String eparchyCode) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_TRADESCORE", inparam, pagination, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    /**
     * 按兑换类型查询积分兑换规则
     * 
     * @author zhuyu
     * @param inparam
     * @return data
     * @throws Exception
     */
    public static IDataset queryScoreExchagneRule(String eparchyCode, String brandCode, String userScore) throws Exception
    {
        IData data = new DataMap();
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("BRAND_CODE", brandCode);
        data.put("SCORE", userScore);
        return Dao.qryByCode("TD_B_EXCHANGE_RULE", "SEL_RULE_BY_SCORE", data, Route.CONN_CRM_CEN);
    }

    /**
     * 查询积分类型
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset queryScoreType(IData inparam) throws Exception
    {
        return Dao.qryByCode("TD_S_SCORETYPE", "SEL_SCORETYPE", inparam, Route.CONN_CRM_CEN);
    }

    public static IDataset queryStaffScore(IData inparam) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_STAFF_SCORE", inparam, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset queryStaffScore(IData inparam, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_STAFF_SCORE", inparam, pagination);
    }
}
