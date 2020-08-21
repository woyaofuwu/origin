
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryUnfinishTradeQry extends CSBizBean
{
    public static IDataset queryUnfinishTrade(String serialNumber, String tradeDepartId, String tradeStaffId, String startDate, String finishTrade, String tradeTypeCode, String tradeEparchyCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("START_DATE", startDate);
        params.put("FINISH_DATE", finishTrade);
        params.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
        params.put("TRADE_TYPE_CODE", tradeTypeCode);
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("TRADE_DEPART_ID", tradeDepartId);
        params.put("TRADE_STAFF_ID", tradeStaffId);
        return Dao.qryByCode("TF_B_TRADE", "SEL_BY_ACCEPT_HX", params, pagination, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
    
    public static IDataset queryUnfinishTradeTrace(String orderId) throws Exception
    {
        IData params = new DataMap();
        params.put("ORDER_ID", orderId);
        return Dao.qryByCode("TL_B_ORDER_TRACE", "SEL_TRACE_BY_TRADEID", params, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
}
