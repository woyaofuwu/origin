package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.offer;


import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class OfferGroupList extends CSBizTempComponent {
	public abstract IDataset getGroupList();
	public abstract IData getGroup();

	public void renderComponent(StringBuilder builder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {

    }
}
