package com.asiainfo.veris.crm.iorder.web.igroup.esop.eoms;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSBasePage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class Reminder extends CSBasePage
{
    public abstract void setInfos(IDataset infos);
        
    public void queryInfos(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        IDataset siInfos = CSViewCall.call(this, "SS.EopAttrInfoSVC.qryInfosForEoms", data);
        
    	setInfos(siInfos);
    }
}
