package com.asiainfo.veris.crm.order.web.frame.csview.common.component.selectedoffers;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class SelectedOffersHandler extends CSBizHttpHandler
{

    public void dealOffers() throws Exception
    {
        IData data = this.getData();
        if ("".equals(data.getString("SERVICE_NAME", "")))
        {
            return;
        }
        data.put("ROUTE_CODE", data.getString("ROUTE_CODE"));
        IDataset offers = new DatasetList(data.getString("OFFERS"));
        data.put("OFFERS", offers);
        IDataset result = CSViewCall.call(this, data.getString("SERVICE_NAME"), data);
        this.setAjax(result);
    }

}
