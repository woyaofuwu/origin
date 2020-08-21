
package com.asiainfo.veris.crm.iorder.web.merch.component.merchandise.ordered;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class Product extends CSBizTempComponent
{
	@Override
	protected void cleanupAfterRender(IRequestCycle cycle)
	{
		super.cleanupAfterRender(cycle);
	}

	@Override
	public void renderComponent(StringBuilder arg0, IMarkupWriter writer, IRequestCycle cycle) throws Exception
	{
		boolean isAjax = isAjaxServiceReuqest(cycle);
		String id = getId();
		if (id == null || "".equals(id))
		{
			id = "userProduct";
		}
		String scriptContent = "window[\"" + id + "\"] = UserProduct('" + id + "');";
		if (isAjax)
		{
			includeScript(writer, "merch/component/merchandise/ordered/product.js");
			addScriptContent(writer, scriptContent);
		}
		else
		{
			this.getPage().addResAfterBodyBegin("merch/component/merchandise/ordered/product.js");
			this.getPage().addScriptBeforeBodyEnd(id, scriptContent);
		}
	}

	public abstract String getChangeAction();

	public abstract void setChangeAction(String changeAction);

	public abstract String getResetAction();

	public abstract void setResetAction(String resetAction);

	public abstract String getEffectAction();

}
