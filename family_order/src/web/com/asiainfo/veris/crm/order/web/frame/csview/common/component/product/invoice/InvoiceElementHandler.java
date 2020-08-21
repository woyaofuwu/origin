
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.invoice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class InvoiceElementHandler extends CSBizHttpHandler
{
    public void invoiceCheck() throws Exception
    {
        IData data = this.getData();
        if (StringUtils.isBlank(data.getString("CALL_SVC", "")))
        {
            return;
        }

        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        data.put("SVC_NAME", data.getString("CALL_SVC"));
        IDataset result = CSViewCall.call(this, data.getString("SVC_NAME"), data);
        this.setAjax(result);
    }
}
