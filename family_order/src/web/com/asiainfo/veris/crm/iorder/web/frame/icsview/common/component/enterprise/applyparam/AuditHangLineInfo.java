package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.applyparam;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;

public abstract class AuditHangLineInfo extends BizTempComponent
{
	public abstract void setInfo(IData info);
	
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = "iorder/igroup/esop/acceptanceperiod/script/acceptanceperiod.js";
    	if (isAjax)
        {
        }
        else
        {
            getPage().addResAfterBodyBegin(jsFile, false, false);
        }	
        
    }
}



