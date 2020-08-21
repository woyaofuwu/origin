
package com.asiainfo.veris.crm.order.web.person.badness;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BadInfoComplainDeal extends PersonBasePage
{

    public void dadInfoActive(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond");
        IDataset result = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.badInfoActive", data);
        setAjax(result);
    }

    public void getServRequestType(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond");
        IDataset result = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getServRequestType", data);
        setServRequestTypes(result);
        setSevenTypeCodes(null);
    }
    
    public void getFourthTypeCodes(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond");
        IDataset result = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getFourthTypeCodes", data);
        setFourthTypeCodes(result);
        setFifthTypeCodes(null);
        setServRequestTypes(null);
        setSevenTypeCodes(null);
    }
    
    public void getFifthTypeCodes(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond");
        IDataset result = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getFifthTypeCodes", data);
        setFifthTypeCodes(result);
        setServRequestTypes(null);
        setSevenTypeCodes(null);
    }
    
    public void getSevenTypeCodes(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond");
        IDataset result = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getSevenTypeCodes", data);
        setSevenTypeCodes(result);
    }

    public abstract void setEditInfo(IData data);

    public abstract void setServRequestTypes(IDataset dataset);
    
    public abstract void setFourthTypeCodes(IDataset dataset);
    
    public abstract void setFifthTypeCodes(IDataset dataset);
    
    public abstract void setSevenTypeCodes(IDataset dataset);
}
