
package com.asiainfo.veris.crm.iorder.web.merch.component.offer;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class ModifyOrderOfferTime extends CSBizTempComponent
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
		IData data = getPage().getData();
		String action = data.getString("ACTION", "");
		
		if (null == action || "".equals(action))
		{
			String id = getId();
			if (id == null || "".equals(id))
			{
				id = "modifyOrderOfferTime";
			}

			String scriptContent = "window[\"" + id + "\"] = new ModifyOrderOfferTime('" + id + "'); \n";

			if (isAjax)
			{
				includeScript(writer, "merch/component/offer/modifyorderoffertime.js");
				includeScript(writer, "v5/jcl/ui/component/base/select.js");
				includeScript(writer, "scripts/csserv/common/date/dateutils.js");
				addScriptContent(writer, scriptContent);
			}
			else
			{
				this.getPage().addResAfterBodyBegin("merch/component/offer/modifyorderoffertime.js");
				this.getPage().addResAfterBodyBegin("v5/jcl/ui/component/base/select.js");
				this.getPage().addResBeforeBodyEnd("scripts/csserv/common/date/dateutils.js");
				this.getPage().addScriptBeforeBodyEnd(id, scriptContent);
			}
		}
		else
		{
			if ("CAL_ENDDATE".equals(action))
			{
				calEndDate(data);
			}
		}
	}
	
	private void calEndDate(IData data) throws Exception {
		IDataset ttinfos = CSViewCall.call(this, "SS.MerchChangeProductSVC.calElementEndDate", data);
		getPage().setAjax(ttinfos.getData(0));
	}

}
