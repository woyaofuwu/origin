package com.asiainfo.veris.crm.iorder.web.igroup.mobilecloudinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizPage;
import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;

public abstract class MobileCloudInfo extends BizPage {
	
    public abstract void setPattr(IData pattr) throws Exception;
    public abstract void setPattrs(IDataset pattrs) throws Exception;
    public abstract void setInfoCount(long infoCount) throws Exception;

    public void initPage(IRequestCycle cycle) throws Exception
    {
    	
    }
    public void qryMobileCloudInfos(IRequestCycle cycle) throws Exception {
        IData data = getData("cond");
    	IDataOutput output = CSViewCall.callPage(this, "SS.MobileCloudInfoSVC.selMobileCloudInfoByConditionByPagination", data, this.getPagination("navbar1"));
        setInfoCount(output.getDataCount());
        setPattrs(output.getData());
    }

    
}
