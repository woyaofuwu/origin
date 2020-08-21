
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.product.productexplain;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public class ProductExplainInfoHttpHandler extends CSBizHttpHandler
{

    public void getProductExplainInfoByProductId() throws Exception
    {
        String productId = getData().getString("GRP_PRODUCT_ID");
        IData productExplainInfo = GroupProductUtilView.getProductExplainInfo(this, productId);
        this.setAjax(productExplainInfo);
    }

}
