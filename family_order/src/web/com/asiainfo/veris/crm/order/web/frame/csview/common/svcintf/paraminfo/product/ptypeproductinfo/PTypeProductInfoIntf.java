
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.ptypeproductinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class PTypeProductInfoIntf
{

    /**
     * 通过产品类型编码，地州编码查询产品类型下包含的产品列表
     * 
     * @param bc
     * @param productTypeCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryProductInfosByProductTypeCodeAndEparchyCode(IBizCommon bc, String productTypeCode, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCT_TYPE_CODE", productTypeCode);
        inparam.put("TRADE_EPARCHY_CODE", eparchyCode);
        return CSViewCall.call(bc, "CS.ProductInfoQrySVC.getProductsByTypeForGroup", inparam);
    }

    /**
     * 通过产品编码查询产品类型信息
     * 
     * @param bc
     * @param productId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryProductTypeInfosByProductIdAndEparchyCode(IBizCommon bc, String productId, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", productId);
        inparam.put("EPARCHY_CODE", eparchyCode);
        return CSViewCall.call(bc, "CS.ProductTypeInfoQrySVC.getProductTypeByProductID", inparam);
    }

    /**
     * 通过产品编码和父产品类型查询产品类型信息
     * 
     * @param bc
     * @param productId
     * @param parentCode
     * @return
     * @throws Exception
     */
    public static IDataset qryProductTypeInfosByProductIdAndParentPtypeCode(IBizCommon bc, String productId, String parentCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", productId);
        inparam.put("PARENT_PTYPE_CODE", parentCode);
        return CSViewCall.call(bc, "CS.ProductTypeInfoQrySVC.getProductTypeByProductIDAndParentPtype", inparam);
    }

}
