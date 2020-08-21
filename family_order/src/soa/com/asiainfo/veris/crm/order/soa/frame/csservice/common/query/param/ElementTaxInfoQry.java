
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class ElementTaxInfoQry
{

    /**
     * 获取税率
     * 
     * @param tradeTypeCode
     * @param productId
     * @param elementId
     * @param feeMode
     * @param feeTypeCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryTaxByElementId(String tradeTypeCode, String productId, String elementId, String feeMode, String feeTypeCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();

        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("PRODUCT_ID", productId);
        param.put("ELEMENT_ID", elementId);
        param.put("FEE_MODE", feeMode);
        param.put("FEE_TYPE_CODE", feeTypeCode);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_ELEMENT_TAX", "SEL_BY_PROUDCT_ELEMENTID", param, Route.CONN_CRM_CEN);
    }

    /**
     * 获取税率
     * 
     * @param tradeTypeCode
     * @param productId
     * @param elementId
     * @param feeMode
     * @param feeTypeCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryTaxByPackageId(String tradeTypeCode, String productId, String packageId, String elementId, String eparchyCode) throws Exception
    {
//        IData param = new DataMap();
//
//        param.put("TRADE_TYPE_CODE", tradeTypeCode);
//        param.put("PRODUCT_ID", productId);
//        param.put("PACKAGE_ID", packageId);
//        param.put("ELEMENT_ID", elementId);
//        param.put("EPARCHY_CODE", eparchyCode);
//
//        return Dao.qryByCode("TD_B_ELEMENT_TAX", "SEL_BY_PROUDCT_PACKAGEID", param, Route.CONN_CRM_CEN);
        return UpcCall.qryOfferTax(packageId, "K", null, null, productId, null, elementId, tradeTypeCode);
    }
}
