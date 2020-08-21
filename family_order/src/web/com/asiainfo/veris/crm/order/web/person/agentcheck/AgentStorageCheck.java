package com.asiainfo.veris.crm.order.web.person.agentcheck;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;


public abstract class AgentStorageCheck extends PersonBasePage
{
 
    public void init(IRequestCycle cycle) throws Exception
    {}
    
    
    public void queryCardStorage(IRequestCycle cycle) throws Exception
    {

        IData cond = this.getData();
        Pagination page = getPagination("listnav");
        
        String departInfo=cond.getString("AGENT_DEPART_ID1");
        String departId=departInfo.substring(0,5); 
        cond.put("DEPART_ID", departId);
        IDataOutput result = CSViewCall.callPage(this, "SS.AgentStorageCheckSVC.checkCardStorage", cond, page);
        long dataCount=result.getDataCount();
        setRecordCount(dataCount);
        setInfos(result.getData()); 
    }
    
    public abstract void setCond(IData cond);

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setRecordCount(long recordCount);
}