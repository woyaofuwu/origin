package com.asiainfo.veris.crm.order.web.person.ivrmail;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MailCustShield extends PersonBasePage {

	public void init(IRequestCycle cycle) throws Exception
	{
		
	}
	
	public void queryList(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", pageData.getString("SERIAL_NUMBER", ""));
        inparam.put("START_DATE", pageData.getString("START_DATE", ""));
        inparam.put("END_DATE", pageData.getString("END_DATE", ""));
        
        IDataOutput out= CSViewCall.callPage(this, "SS.MailCustShieldSVC.QueryList", inparam, getPagination("page"));
        setInfos(out.getData());
        setListCount(out.getDataCount());
	}
	
	public void addRecord(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", pageData.getString("MODIFY_SERIAL_NUMBER", ""));
        IDataset result = new DatasetList();
    	result = CSViewCall.call(this, "SS.MailCustShieldSVC.AddRecord", inparam);
        IData resultinfo = result.getData(0);
        setAjax(resultinfo);
	}
	
	public void delRecord(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", pageData.getString("MODIFY_SERIAL_NUMBER", ""));
        IDataset result = CSViewCall.call(this, "SS.MailCustShieldSVC.DelRecord", inparam);
        IData resultinfo = result.getData(0);
        setAjax(resultinfo);
	}
	
	public abstract void setCond(IData cond);
	
	public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setListCount(long listCount);

}
