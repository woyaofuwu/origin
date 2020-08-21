package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.applyparam;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class AuditTapMarketingLineInfo extends BizTempComponent
{
	public abstract void setInfo(IData info);
	public abstract void setPattr(IData pattr);
	public abstract void setPattrs(IDataset pattrs);
	public abstract void setRowIndex(int rowIndex);
	public abstract void setFileLists(IDataset fileLists);
	public abstract String getOperCode();
	
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = "";
//        		"iorder/igroup/esop/applyrequirement/script/applyrequirement.js";

        if (isAjax)
        {
            includeScript(writer, jsFile, false, false);
        }
        else
        {
            getPage().addResAfterBodyBegin(jsFile, false, false);
        }
        IData param = getPage().getData();
        // 查询附件
        IData input = new DataMap();
        input.put("IBSYSID", param.getString("IBSYSID"));
        IDataset  filesets = CSViewCall.call(this, "SS.WorkformAttachSVC.qryContractAttach", input);

        setFileLists(filesets);
    }
}



