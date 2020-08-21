package com.asiainfo.veris.crm.order.web.frame.csview.common.component.offer;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class OfferDetail extends CSBasePage
{

    public abstract void setOffer(IData offer);

    public abstract void setReadOnly(String readOnly);

    public abstract void setSrcObject(String srcObject);

    public void init(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IData offer = CSViewCall.callone(this, "CS.OfferDetailSVC.getOfferDetail", param);
        this.setOffer(offer);
        this.setSrcObject("$.getSrcWindow()." + param.getString("SRC_OBJECT") + ";");
    }
}
