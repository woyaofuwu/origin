package com.asiainfo.veris.crm.order.web.frame.csview.common.component.offer;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class OfferLabel extends CSBizTempComponent {
	public abstract IDataset getLabelList();
    public abstract IDataset getGroupList();
  
    public void renderComponent(StringBuilder builder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {

    }
}
