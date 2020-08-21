
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryUserTradeScoreQry extends CSBizBean
{
    public static IDataset queryScoreDetailInfoByTradeId(String trade_id, String cancelTag, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("CANCEL_TAG", cancelTag);
        return Dao.qryByCodeParser("TF_B_TRADE_SCORE", "SEL_BY_TRADE", param, pagination,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset querySNByTradeId(String tradeId, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADEFEE_GIFTFEE", "SELT_TRADEFEE_GIFTFEE", indata, pagination,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
    
    public static IDataset querySNByTradeId2(String userId) throws Exception
    {
        IData indata = new DataMap();
        indata.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADEFEE_GIFTFEE", "SELT_TRADEFEE_GIFTFEE2", indata);
    }

    public static IDataset queryUserTradeScore(String serialNum, String routeEparchyCode, String accpetStart, String acceptEnd, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("EPARCHY_CODE", routeEparchyCode);
        indata.put("ACCEPT_START", accpetStart);
        indata.put("ACCEPT_END", acceptEnd);
        indata.put("SERIAL_NUMBER", serialNum);
        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_TRADESCORE", indata, pagination,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
    
    public static IDataset queryUserTradeScore2(String tradeTypeCode, String tradeStaffId,String routeEparchyCode) throws Exception
    {
        IData indata = new DataMap();
        indata.put("EPARCHY_CODE", routeEparchyCode);
        indata.put("TRADE_TYPE_CODE", tradeTypeCode);
        indata.put("STAFF_ID", tradeStaffId);
        return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_TRADESCORE2", indata,Route.CONN_CRM_CEN);
    }
}
