
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.payplanedit;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;

public class PayPlanEditHttpHandler extends CSBizHttpHandler
{

    public void rendPayPlanEditInfo() throws Exception
    {

        String userId = getData().getString("USER_ID", "");
        String productId = getData().getString("PRODUCT_ID", "");
        IData result = PayPlanEditView.renderPayPlanEditInfo(this, productId, userId);

        this.setAjax(result);
    }

}
