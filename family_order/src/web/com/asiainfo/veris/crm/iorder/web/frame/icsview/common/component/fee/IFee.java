package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.fee;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;

public abstract class IFee extends BizTempComponent{
	
	public abstract String getOnlyPayMode();
	public abstract void setOnlyPayMode(String onlyPayMode);
	
	
	@Override
	public void renderComponent(StringBuilder paramsBuilder,IMarkupWriter writer, IRequestCycle cycle) throws Exception{

		getPage().addResBeforeBodyEnd("scripts/iorder/icsserv/component/fee/ifee.js");
		
	}
}