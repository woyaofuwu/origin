
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ProductdLimitInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getPlusProductByProdId(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String eparchycode = input.getString("TRADE_EPARCHY_CODE");
        IDataset data = ProductdLimitInfoQry.getPlusProductByProdId(productId, eparchycode);

        return data;
    }
}
