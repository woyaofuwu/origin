
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.productmebinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class ProductMebInfoIntf
{

    /**
     * 查询成员附加基本产品
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getMemberMainProductByProductId(IBizCommon bc, String productId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", productId);
        return CSViewCall.call(bc, "CS.ProductMebInfoQrySVC.getMemberMainProductByProductId", inparam);
    }

    /**
     * 通过集团产品ID查询成员产品列表
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset qryProductMebInfosByProductId(IBizCommon bc, String productId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", productId);
        return CSViewCall.call(bc, "CS.ProductMebInfoQrySVC.getMebProductNoRight", inparam);
    }
}
