
package com.asiainfo.veris.crm.iorder.web.merch.component.merchandise.ordered;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class SelectedElements extends CSBizTempComponent
{
	@Override
	protected void cleanupAfterRender(IRequestCycle cycle)
	{
		super.cleanupAfterRender(cycle);
	}

	@Override
	public void renderComponent(StringBuilder stringbuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
	{
		boolean isAjax = isAjaxServiceReuqest(cycle);
		if (isAjax)
		{
			includeScript(writer, "merch/component/merchandise/ordered/selectedelements.js");
		}
		else
		{
			this.getPage().addResAfterBodyBegin("merch/component/merchandise/ordered/selectedelements.js");
		}

		String id = getId();
		if (id == null || "".equals(id))
		{
			id = "selectedElements";
		}
		String renderSvc = this.getRenderCallSvc();
		String addElementSvc = this.getCallAddElementSvc();
		StringBuilder config = new StringBuilder();
		config.append("{");
		config.append("renderCallSvc:" + "'" + renderSvc + "'");
		config.append(",");
		config.append("callAddElementSvc:" + "'" + addElementSvc + "'");
		config.append("}");
		
		StringBuilder scriptContent = new StringBuilder();
		scriptContent.append("window['" + id + "'] = SelectedElements('" + id + "'," + config + "); \n");
		if (StringUtils.isNotEmpty(getScrollId()))
		{
			scriptContent.append("window[\"elementAttr\"] = ElementAttr('elementAttr'");
			scriptContent.append(",'" + getScrollId() + "'");
			scriptContent.append(");");
		}
		

		if (isAjax)
			addScriptContent(writer, scriptContent.toString());
		else
			this.getPage().addScriptBeforeBodyEnd(id, scriptContent.toString());
	}

	public abstract String getBasicCancelDateControlId();

	public abstract String getAfterRenderAction();

	public abstract String getBasicStartDateControlId();

	public abstract String getCallAddElementSvc();

	public abstract IData getInitParam();

	public abstract String getRenderCallSvc();

	public abstract String getTradeTypeCode();
	
	public abstract String getScrollId();

	public abstract void setBasicCancelDateControlId(String basicCancelDateControlId);

	public abstract void setBasicStartDateControlId(String basicStartDateControlId);

	public abstract void setCallAddElementSvc(String callAddElementSvc);

	public abstract void setRenderCallSvc(String renderCallSvc);

	public abstract void setTradeTypeCode(String tradeTypeCode);

	public abstract String getAttrComponent();

	public abstract void setAttrComponent(String bindedComponentId);

	public abstract String getItemComponent();

	public abstract void setItemComponent(String bindedComponentId);
	
	public abstract String getTimeComponent();

	public abstract void setTimeComponent(String bindedComponentId);

}
