package com.asiainfo.veris.crm.order.web.person.sparkplansmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class StaffSelect extends PersonBasePage{

	public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setCount(long count);
    
    public void queryStaffs(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	
    	IDataOutput dataCount = CSViewCall.callPage(this, "CS.StaffDeptInfoQrySVC.getStaffList", data, getPagination("nav"));
        setCount(dataCount.getDataCount());
        setInfos(dataCount.getData());
    }
}
