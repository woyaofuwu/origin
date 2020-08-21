package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder;

import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.web.view.component.AbstractComponent;
import com.ailk.web.view.component.ComponentWraperFactory;
import com.ailk.web.view.component.IComponentWraper;

public class EsopBaseComponent extends AbstractComponent implements IBizCommon
{
	public IComponentWraper getComponentWrap()
	{
		return ComponentWraperFactory.getWraper();
	}

	public BizPage getPage()
	{
		return (BizPage) super.getPage();
	}

	public String getPageName()
	{
		return getPage().getPageName();
	}

	public IData getRequestData()
	{
		IData data = null;
		try
		{
			data = getPage().getData();
		}
		catch (Exception e)
		{
		}
		return data;
	}
}
