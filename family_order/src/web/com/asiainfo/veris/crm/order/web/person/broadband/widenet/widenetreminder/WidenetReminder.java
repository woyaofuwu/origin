package com.asiainfo.veris.crm.order.web.person.broadband.widenet.widenetreminder;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class WidenetReminder extends PersonBasePage
{
    
	
    /**
	 * 查询未完工宽带工单
	 * */
    public void queryTradeList(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	
        IDataset result = CSViewCall.call(this, "SS.WidenetReminderSVC.queryTradeList", data); 
         
        setInfos(result);
    }
    
    public void submitTrade(IRequestCycle cycle) throws Exception
    {
        IData input = this.getData();
        IDataset dataset = CSViewCall.call(this, "SS.WidenetReminderSVC.submitTrade", input);
        setAjax(dataset);
    }
    
    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
