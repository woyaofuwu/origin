package com.asiainfo.veris.crm.order.web.person.changeservice;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChangeService extends PersonBasePage {
    
    public abstract void setHasUserSvcs(IDataset infos);

    public abstract void setHasUserSvc(IData info);
    
    public abstract void setNewUserSvcs(IDataset infos);

    public abstract void setNewUserSvc(IData info);
    
    public abstract void setShowDelInfo(IData info);
    
    public abstract void setAddSvcs(IDataset addSvcs);
    
    public abstract void setDelSvcs(IDataset delSvcs);
    
    public abstract void setTcs(IDataset infos);
    
    public void loadChildInfo(IRequestCycle cycle) throws Exception{
        IData data = getData();
         
        IDataset ids = CSViewCall.call(this, "SS.ChangeServiceSVC.loadChildInfo", data);
        
        setHasUserSvcs(ids.getData(0).getDataset("USER_SVCS")); 
        setAddSvcs(ids.getData(0).getDataset("ADD_SVCS"));
        setDelSvcs(ids.getData(0).getDataset("DEL_SVCS"));
 
    }
    public void loadFeeInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset feeList = CSViewCall.call(this, "SS.ChangeServiceSVC.loadFeeInfo", data);
        setAjax(feeList);
    }
    
    
    
    public void getRoamEndDate(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
       String startDate = data.getString("NEW_START_DATE");
        
       String endDateSel = data.getString("NEW_END_DATE_SEL");
       
       String endDate = SysDateMgr.END_DATE_FOREVER;
       
       if ("0".equals(endDateSel))
       {
           endDate  = SysDateMgr.addDays(startDate, 30);
       }
       else  if ("1".equals(endDateSel))
       {
         
           endDate  = SysDateMgr.addDays(startDate, 180);
       } 
        
        data.put("END_DATE", SysDateMgr.suffixDate(endDate,1));
        setAjax(data);
    }
  
    
    public void submitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();  
        IDataset result = CSViewCall.call(this, "SS.ChangeServiceSVC.submitTradeInfo", data);
        this.setAjax(result);
    }   
}
