
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class FeeItemTaxInfoQry
{

    public static IDataset getTaxByFeeTypeCode(String tradeTypeCode, String feeTypeCode, String eparchyCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("TRADE_TYPE_CODE", tradeTypeCode);
        cond.put("FEE_TYPE_CODE", feeTypeCode);
        cond.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_FEEITEM_TAX", "SEL_BY_FEE_TYPE_CODE", cond, Route.CONN_CRM_CEN);
    }

    public static IDataset getTaxByPid(String tradeTypeCode, String productId, String eparchyCode) throws Exception
    {
        IData cond = new DataMap();

        cond.put("TRADE_TYPE_CODE", tradeTypeCode);
        cond.put("PRODUCT_ID", productId);
        cond.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_FEEITEM_TAX", "SEL_TAX_PID_TYPE", cond, Route.CONN_CRM_CEN);
    }

    /**
     * 获取税率
     * 
     * @param tradeTypeCode
     * @param productId
     * @param feeMode
     * @param feeTypeCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryTaxByTradeProductFee(String tradeTypeCode, String productId, String feeMode, String feeTypeCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();

        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("PRODUCT_ID", productId);
        param.put("FEE_MODE", feeMode);
        param.put("FEE_TYPE_CODE", feeTypeCode);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_FEEITEM_TAX", "SEL_BY_PID_TYPECODE_FEE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset qryTaxByTypeCode(String tradeTypeCode, String feeTypeCode, String feeMode, String productId, String packageId, String elementId, String nowDate, String eparchyCode) throws Exception
    {
        IData param = new DataMap();

        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("PRODUCT_ID", productId);
        param.put("FEE_MODE", feeMode);
        param.put("FEE_TYPE_CODE", feeTypeCode);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("PACKAGE_ID", packageId);
        param.put("ELEMENT_ID", elementId);
        param.put("NOW_DATE", nowDate);

        return Dao.qryByCode("TD_B_FEEITEM_TAX", "SEL_FOR_TAX_BY_TYPE_CODE", param, Route.CONN_CRM_CEN);
    }
}
