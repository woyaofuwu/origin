
package com.asiainfo.veris.crm.iorder.web.merch.component.merchandise.ordered;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class ElementAttr extends CSBizTempComponent
{
	@Override
	protected void cleanupAfterRender(IRequestCycle cycle)
	{
		super.cleanupAfterRender(cycle);
		this.setAttr(null);
		this.setAttrs(null);
		this.setItemIndex(null);
		this.setDisplayCondition(null);
//		this.setConfirmHandler(null);
//		this.setCancelHandler(null);
	}

	@Override
	public void renderComponent(StringBuilder builder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
	{

		boolean isAjax = isAjaxServiceReuqest(cycle);
		if (isAjax)
		{
			includeScript(writer, "merch/component/merchandise/ordered/elementattr.js");
		}
		else
		{
			this.getPage().addResAfterBodyBegin("merch/component/merchandise/ordered/elementattr.js");
		}
		IData param = this.getPage().getData();
		if (param.getString("ELEMENT_ID", "").equals("") || param.getString("ELEMENT_TYPE_CODE", "").equals(""))
		{
			String id = getId();
			if (id == null || "".equals(id))
			{
				id = "elementAttr";
			}
			String scrollId = getScrollId();
			String scriptContent = "window[\"" + id + "\"] = ElementAttr('" + id + "'";
			if (StringUtils.isNotEmpty(scrollId))
			{
				scriptContent += ",'" + scrollId + "'";
			}
			scriptContent += ");";

			if (isAjax)
				addScriptContent(writer, scriptContent);
			else
				this.getPage().addScriptBeforeBodyEnd(id, scriptContent);

			return;
		}
		this.setItemIndex(param.getString("ITEM_INDEX"));
		this.setDisplayCondition(param.getString("DISPLAY_CONDITION"));
		IDataset attrs = CSViewCall.call(this, "CS.SelectedElementSVC.getElementAttrs", param);
		if (attrs != null && attrs.size() > 0)
		{
			IDataset attrsResult = new DatasetList();
			IData tempAttr = null;
			int size = attrs.size();
			String attrCode = "";
			for (int i = 0; i < size; i++)
			{
				IData attr = attrs.getData(i);
				if (!attrCode.equals(attr.getString("ATTR_CODE")))
				{
					// 如果是视频会议，下拉列表数据从OTHER表中获取
					if ("CONF_ID".equals(attr.getString("ATTR_CODE")) && "98003201".equals(attr.getString("ID")))
					{
						if (!"".equals(param.getString("USER_ID", "")))
						{
							IData queryParam = new DataMap();
							queryParam.put("USER_ID", param.getString("USER_ID"));
							queryParam.put(Route.ROUTE_EPARCHY_CODE, param.getString("EPARCHY_CODE"));
							IDataset meetings = CSViewCall.call(this, "CS.PlatComponentSVC.getBookingViedoMeeting", queryParam);
							tempAttr = attr;
							tempAttr.put("PARAMS", new DatasetList());

							for (int mcount = 0; mcount < meetings.size(); mcount++)
							{
								IData attrParam = new DataMap();
								attrParam.put("ATTR_FIELD_NAME", meetings.getData(mcount).getString("RSRV_STR10"));
								attrParam.put("ATTR_FIELD_CODE", meetings.getData(mcount).getString("RSRV_STR10"));
								tempAttr.getDataset("PARAMS").add(attrParam);
							}
						}

						continue;
					}

					if (attr.getString("ATTR_TYPE_CODE").equals("1") || attr.getString("ATTR_TYPE_CODE").equals("3") || attr.getString("ATTR_TYPE_CODE").equals("9")
							|| attr.getString("ATTR_TYPE_CODE").equals("5"))
					{
						if (tempAttr != null)
						{
							attrsResult.add(tempAttr);
						}
						tempAttr = attr;
						tempAttr.put("PARAMS", new DatasetList());
						if (StringUtils.isNotEmpty(attr.getString("ATTR_FIELD_NAME")) && StringUtils.isNotEmpty(attr.getString("ATTR_FIELD_CODE")))
						{
							IData attrParam = new DataMap();
							attrParam.put("ATTR_FIELD_NAME", attr.getString("ATTR_FIELD_NAME"));
							attrParam.put("ATTR_FIELD_CODE", attr.getString("ATTR_FIELD_CODE"));
							if (attr.getString("ATTR_TYPE_CODE").equals("9"))
							{
								String attrProductId = attr.getString("ATTR_CODE", "");
								if (StringUtils.isEmpty(attrProductId))
								{
									attrProductId = attr.getString("RSRV_STR5", "");
								}
								attrParam.put("PRODUCT_ID", attrProductId);
								attrParam.put("WIDTH", attr.getString("RSRV_STR3"));
								attrParam.put("HEIGHT", attr.getString("RSRV_STR4"));
								attrParam.put("TITLE", attr.getString("TITLE"));
							}
							tempAttr.getDataset("PARAMS").add(attrParam);
						}
						attrCode = attr.getString("ATTR_CODE");
						if (i == size - 1)
						{
							attrsResult.add(tempAttr);
						}
					}
					else if (attr.getString("ATTR_TYPE_CODE").equals("8"))
					{// 自定义弹出框提交的验证JS方法
						String attrScript = attr.getString("SELFFUNC", "");
						if (StringUtils.isNotBlank(attrScript))
						{
							setConfirmHandler(attrScript);
						}
					}
					else
					{
						if (tempAttr != null)
						{
							attrsResult.add(tempAttr);
							tempAttr = null;
						}
						attrsResult.add(attr);
					}
				}
				else
				{
					if (attr.getString("ATTR_TYPE_CODE").equals("1") || attr.getString("ATTR_TYPE_CODE").equals("3") || attr.getString("ATTR_TYPE_CODE").equals("9")
							|| attr.getString("ATTR_TYPE_CODE").equals("5"))
					{
						IData attrParam = new DataMap();
						attrParam.put("ATTR_FIELD_NAME", attr.getString("ATTR_FIELD_NAME"));
						attrParam.put("ATTR_FIELD_CODE", attr.getString("ATTR_FIELD_CODE"));
						if (attr.getString("ATTR_TYPE_CODE").equals("9"))
						{
							String attrProductId = attr.getString("ATTR_CODE", "");
							if (StringUtils.isEmpty(attrProductId))
							{
								attrProductId = attr.getString("RSRV_STR5", "");
							}
							attrParam.put("PRODUCT_ID", attrProductId);
							attrParam.put("WIDTH", attr.getString("RSRV_STR3"));
							attrParam.put("HEIGHT", attr.getString("RSRV_STR4"));
							attrParam.put("TITLE", attr.getString("TITLE"));
						}
						tempAttr.getDataset("PARAMS").add(attrParam);
						if (i == size - 1)
						{
							attrsResult.add(tempAttr);
						}
					}
				}
				attrCode = attr.getString("ATTR_CODE");
			}
			int attrSize = attrsResult.size();
			boolean isNeedPopupSelfWindow = false;
			IData needPopupAttr = null;
			for (int i = 0; i < attrSize; i++)
			{
				IData attr = attrsResult.getData(i);
				if ("9".equals(attr.getString("ATTR_TYPE_CODE")))
				{
					isNeedPopupSelfWindow = true;
					needPopupAttr = attr;
					break;
				}
			}
			if (isNeedPopupSelfWindow)
			{
				this.getPage().setAjax(needPopupAttr.getDataset("PARAMS"));
				this.setRenderContent(false);
				return;
			}

			this.setAttrs(attrsResult);
		}
	}

	public abstract String getConfirmHandler();

	public abstract String getCancelHandler();

	public abstract String getBindedComponent();

	public abstract void setAttr(IData attr);

	public abstract void setBindedComponent(String bindedComponentId);

	public abstract void setAttrs(IDataset attrs);

	public abstract void setConfirmHandler(String confirmHandler);

	public abstract void setCancelHandler(String cancelHandler);

	public abstract void setDisplayCondition(String displayCondition);

	public abstract void setItemIndex(String itemIndex);

	public abstract String getScrollId();

}
