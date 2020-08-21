
package com.asiainfo.veris.crm.iorder.web.merch.component.merchandise.selectedarea;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class ProductSelect extends CSBizTempComponent
{

	@Override
	protected void cleanupAfterRender(IRequestCycle cycle)
	{
		super.cleanupAfterRender(cycle);
	}

	public abstract String getCatalogId();

	public abstract String getServiceName();

	public abstract String getAfterAction();

	public abstract boolean isShowGroup();

	public abstract void setCatalogId(String productTypeCode);

	@Override
	public void renderComponent(StringBuilder builder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
	{
		boolean isAjax = isAjaxServiceReuqest(cycle);
		if (isAjax)
		{
			includeScript(writer, "merch/component/merchandise/selectedarea/productselect.js");
			includeScript(writer, "merch/component/merchandise/selectedarea/searchtools.js");
			includeScript(writer, "merch/component/merchandise/selectedarea/pagingcomponent.js");
		}
		else
		{
			this.getPage().addResAfterBodyBegin("merch/component/merchandise/selectedarea/productselect.js");
			this.getPage().addResBeforeBodyEnd("merch/component/merchandise/selectedarea/searchtools.js");
			this.getPage().addResBeforeBodyEnd("merch/component/merchandise/selectedarea/pagingcomponent.js");
		}
		String serviceName = this.getServiceName();
		String id = getId();
		if (id == null || "".equals(id))
		{
			id = "productSelect";
		}

		boolean showGroup = isShowGroup();
		StringBuilder config = new StringBuilder();
		config.append("{");
		config.append("showGroup:" + showGroup);
		if (StringUtils.isNotBlank(serviceName))
		{
			config.append(",");
			config.append("serviceName:" + "'" + serviceName + "'");
		}
		config.append("}");

		String scriptContent = "window['" + id + "'] = ProductSelect('" + id + "'," + config + "); \n";

		if (isAjax)
			addScriptContent(writer, scriptContent);
		else
			this.getPage().addScriptBeforeBodyEnd(id, scriptContent);
	}
}
