
package com.asiainfo.veris.crm.iorder.web.merch.component.merchandise.selectedarea;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class ItemList extends CSBizTempComponent
{

	public void renderComponent(StringBuilder builder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
	{
		boolean isAjax = isAjaxServiceReuqest(cycle);
		if (isAjax)
		{
			includeScript(writer, "merch/component/merchandise/selectedarea/itemlist.js");
			includeScript(writer, "scripts/csserv/common/date/dateutils.js");
			includeScript(writer, "merch/component/merchandise/selectedarea/searchtools.js");
			includeScript(writer, "merch/component/merchandise/selectedarea/pagingcomponent.js");
		}
		else
		{
			this.getPage().addResAfterBodyBegin("merch/component/merchandise/selectedarea/itemlist.js");
			this.getPage().addResBeforeBodyEnd("scripts/csserv/common/date/dateutils.js");
			this.getPage().addResBeforeBodyEnd("merch/component/merchandise/selectedarea/searchtools.js");
			this.getPage().addResBeforeBodyEnd("merch/component/merchandise/selectedarea/pagingcomponent.js");
		}
		
		String id = getId();
		if (id == null || "".equals(id))
		{
			id = "itemList";
		}

//		writer.printRaw("<script language=\"javascript\">\n");
//        writer.printRaw("$(function(){\n");
//        writer.printRaw("itemList.init('" + id + "');\n");
//        writer.printRaw("});\n");
//        writer.printRaw("</script>\n");
        
		String scriptContent = "window[\"" + id + "\"] = ItemList('" + id + "');";
		if (isAjax)
			addScriptContent(writer, scriptContent);
		else
			this.getPage().addScriptBeforeBodyEnd(id, scriptContent);
	}

	@Override
	protected void cleanupAfterRender(IRequestCycle cycle)
	{
		super.cleanupAfterRender(cycle);
	}

	public abstract String getAfterAction();

	public abstract String getSwitchAction();

	public abstract String getSwitchLabelService();

	public abstract void setBindedComponent(String bindedComponentId);

	public abstract String getBindedComponent();

}
