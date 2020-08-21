package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.applyparam;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public abstract class ContractInfo extends BizTempComponent
{
	public abstract void setInfo(IData info);
	public abstract void setReason(IData reason);
	public abstract void setReasonList(IDataset reasonList);
	public abstract void setRowIndex(int rowIndex);
	public abstract String getOperCode();
	public abstract void setDirectInfo(IData directInfo);
	public abstract void setUserInfo(IData userInfo);
	public abstract void setDirectLineList(IDataset directLineList);
	
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = "iorder/igroup/esop/applyrequirement/script/applyrequirement.js";

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



