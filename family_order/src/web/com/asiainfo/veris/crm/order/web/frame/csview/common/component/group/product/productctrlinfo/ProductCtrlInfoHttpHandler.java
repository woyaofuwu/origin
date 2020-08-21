
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.product.productctrlinfo;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;

public class ProductCtrlInfoHttpHandler extends CSBizHttpHandler
{

    public void getProductCtrlInfoByProductIdAndBusiType() throws Exception
    {

        String productId = getData().getString("GRP_PRODUCT_ID");
        String busiType = getData().getString("BUSI_TYPE");
        IData productCtrlInfoData = AttrBizInfoIntfViewUtil.qrySimpleNormalProductCtrlInfoByGrpProductIdAndBusiType(this, productId, busiType);

        this.setAjax(productCtrlInfoData);
    }

}
