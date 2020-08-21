
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.productinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class ProductInfoIntf
{

    /**
     * 通过产品ID查询包
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getPackagesByProductId(IBizCommon bc, String productId, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", productId);
        inparam.put("EPARCHY_CODE", eparchyCode);
        return CSViewCall.call(bc, "CS.ProductInfoQrySVC.getPackagesByProductId", inparam);
    }

    /**
     * 通过产品ID查询产品信息
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset qryProductInfoByProductId(IBizCommon bc, String productId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", productId);
        return CSViewCall.call(bc, "CS.ProductInfoQrySVC.getProductInfoByID", inparam);
    }

    /**
     * 通过产品ID查询产品信息
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset querySaleActiveProduct(IBizCommon bc, String campnType, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("RSRV_STR2", campnType);
        inparam.put("EPARCHY_CODE", eparchyCode);
        return CSViewCall.call(bc, "CS.ProductInfoQrySVC.querySaleActiveProduct", inparam);
    }
}
