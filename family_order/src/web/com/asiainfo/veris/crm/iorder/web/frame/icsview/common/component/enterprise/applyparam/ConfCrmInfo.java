package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.applyparam;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;

public abstract class ConfCrmInfo extends BizTempComponent
{
	public abstract void setInfo(IData info);
	
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = "scripts/iorder/icsserv/component/enterprise/applyparam/ConfCrm.js";
        if (isAjax)
        {
            includeScript(writer, jsFile, false, false);
        }
        else
        {
            getPage().addResAfterBodyBegin(jsFile, false, false);
        }
    }
}



