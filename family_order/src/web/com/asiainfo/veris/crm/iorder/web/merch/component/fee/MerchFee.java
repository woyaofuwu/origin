package com.asiainfo.veris.crm.iorder.web.merch.component.fee;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class MerchFee extends CSBizTempComponent {
	protected void cleanupAfterRender(IRequestCycle cycle) {
		super.cleanupAfterRender(cycle);
	}

	public void renderComponent(StringBuilder infoParamsBuilder, IMarkupWriter writer, IRequestCycle cycle)
			throws Exception {
		getPage().addResAfterBodyBegin("scripts/merch/fee/MerchFeeMgr.js");
	}

}
