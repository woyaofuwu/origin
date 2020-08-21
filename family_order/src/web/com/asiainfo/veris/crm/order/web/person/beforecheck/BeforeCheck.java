package com.asiainfo.veris.crm.order.web.person.beforecheck;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;


public abstract class BeforeCheck extends PersonBasePage
{
 
    public void init(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.BeforeCheckSVC.getBeforeCheckList", data);
        setInfos(dataset);    	
        setRecordCount(dataset.size());
    } 
    
    
    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setRecordCount(long recordCount);
}