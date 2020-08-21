package com.asiainfo.veris.crm.iorder.web.igroup.esop.urlSender;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class SerialHttpSender extends EopBasePage {
    public abstract void setPattr(IData pattr);
	public abstract String getContent();
	public abstract void setContent(String content);


	
	@Override
    public void initPage(IRequestCycle cycle) throws Exception
    {

    }
	
    public void submit(IRequestCycle cycle) throws Exception {
        IData pattr = getData("pattr");
        IDataset pattrs = CSViewCall.call(this, "SS.SerialHttpSenderSVC.serialHttpSender", pattr);
        setAjax(pattrs);
    }
}
