
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeSvcStateParamInfoQry
{
    /**
     * 查询服务状态变化参数表
     * 
     * @param tradeTypeCode
     * @param baranCode
     * @param productId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset querySvcStateParamByKey(String tradeTypeCode, String brandCode, String productId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("BRAND_CODE", brandCode);
        param.put("PRODUCT_ID", productId);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_TRADE_SVCSTATE", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    public static IDataset querySvcStateParamByOldStateCode(String tradeTypeCode, String serviceId, String stateCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SERVICE_ID", serviceId);
        param.put("OLD_STATE_CODE", stateCode);
        return Dao.qryByCode("TD_S_TRADE_SVCSTATE", "SEL_BY_OLDSTATE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset querySvcStateParamByOperCode(String tradeTypeCode, String brandCode, String productId, String eparchyCode, String operCode, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("BRAND_CODE", brandCode);
        param.put("PRODUCT_ID", productId);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("OPER_CODE", operCode);
        param.put("SERVICE_ID", serviceId);
        return Dao.qryByCode("TD_S_TRADE_SVCSTATE", "SEL_BY_OPER_CODE", param, Route.CONN_CRM_CEN);
    }
}
