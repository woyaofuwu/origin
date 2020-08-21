
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.productcompinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class ProductCompInfoIntf
{

    /**
     * 通过PRODUCTID查询组合产品信息
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset qryProductCompInfosByProductId(IBizCommon bc, String productId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", productId);
        return CSViewCall.call(bc, "CS.ProductCompInfoQrySVC.getCompProductInfoByID", inparam);
    }
}
