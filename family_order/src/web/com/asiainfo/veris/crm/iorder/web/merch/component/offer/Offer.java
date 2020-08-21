
package com.asiainfo.veris.crm.iorder.web.merch.component.offer;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class Offer extends CSBizTempComponent
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
			id = "offer";
		}

		String type = getType();
		boolean isShowOrder = isShowOrder();
		boolean isOpenPage = isOpenPage();
		boolean isShowAddShoppingCart = isShowAddShoppingCart();

		boolean isCalcDate = isCalcDate();
		String offersTpl = getOffersTpl();
		String labelsTpl = getLabelsTpl();

		StringBuilder config = new StringBuilder();
		config.append("{");
		config.append("type:'" + type + "'");
		config.append(",");
		config.append("isShowOrder:" + isShowOrder);
		config.append(",");
		config.append("isShowAddShoppingCart:" + isShowAddShoppingCart);
		config.append(",");
		config.append("isOpenPage:" + isOpenPage);
		config.append(",");
		config.append("isCalcDate:" + isCalcDate);
		if (StringUtils.isNotBlank(offersTpl))
		{
			config.append(",");
			config.append("offersTpl:'" + offersTpl + "'");
		}
		if (StringUtils.isNotBlank(labelsTpl))
		{
			config.append(",");
			config.append("labelsTpl:'" + labelsTpl + "'");
		}
		config.append("}");

		String scriptContent = "window[\"" + id + "\"] = Offer('" + id + "'," + config + "); \n";
		       scriptContent += "window[\"offerAttr\"] = OfferAttr('offerAttr'); \n";
		if (isAjax)
		{
			includeScript(writer, "merch/component/offer/offer.js");
			includeScript(writer, "merch/component/offer/pagingcomponent.js");
			addScriptContent(writer, scriptContent);
		}
		else
		{
			this.getPage().addResAfterBodyBegin("merch/component/offer/offer.js");
			this.getPage().addResAfterBodyBegin("merch/component/offer/pagingcomponent.js");
			this.getPage().addScriptBeforeBodyEnd(id, scriptContent);
		}
	}

	public abstract String getChangeAction();

	public abstract void setChangeAction(String changeAction);

	public abstract String getResetAction();

	public abstract void setResetAction(String resetAction);

	public abstract String getType();

	public abstract void setType(String type);

	public abstract boolean isShowOrder();

	public abstract boolean isShowAddShoppingCart();

	public abstract boolean isOpenPage();

	public abstract boolean isCalcDate();

	public abstract String getOffersTpl();

	public abstract String getLabelsTpl();

}
