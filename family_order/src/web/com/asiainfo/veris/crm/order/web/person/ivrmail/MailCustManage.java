package com.asiainfo.veris.crm.order.web.person.ivrmail;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MailCustManage extends PersonBasePage {

	public void init(IRequestCycle cycle) throws Exception
	{
		
	}
	
	public void queryList(IRequestCycle cycle) throws Exception
	{
		
		
		IData pageData = getData();
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", pageData.getString("SERIAL_NUMBER", ""));
        inparam.put("SECTION_ID", pageData.getString("SECTION_ID", ""));
        inparam.put("MONTH_LIMIT", pageData.getString("MONTH_LIMIT", ""));
        inparam.put("SEND_COUNT", pageData.getString("SEND_COUNT", ""));
        inparam.put("START_DATE", pageData.getString("START_DATE", ""));
        inparam.put("END_DATE", pageData.getString("END_DATE", ""));
        
        IDataOutput out= CSViewCall.callPage(this, "SS.MailCustManageSVC.QueryList", inparam, getPagination("page"));
        // String result = dataset.getData(0).getString("TIP");
        setInfos(out.getData());
        
        setListCount(out.getDataCount());
	}
	
	public void modifyRecord(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", pageData.getString("MODIFY_SERIAL_NUMBER", ""));
        inparam.put("SECTION_ID", pageData.getString("MODIFY_SECTION_ID", ""));
        inparam.put("MONTH_LIMIT", pageData.getString("MODIFY_MONTH_LIMIT", ""));
        inparam.put("START_DATE", pageData.getString("MODIFY_LM_STARTDATE", ""));
        inparam.put("END_DATE", pageData.getString("MODIFY_LM_ENDDATE", ""));
        
        IDataset result = CSViewCall.call(this, "SS.MailCustManageSVC.UpdateRecord", inparam);
        IData resultinfo = result.getData(0);
        setAjax(resultinfo);
        if( null == resultinfo || 0 != resultinfo.getInt("X_RESULTCODE", -1)){//update failed
        	
        }else{
        	IDataset dataset = CSViewCall.call(this, "SS.MailCustManageSVC.QueryList", pageData);
            setInfos(dataset);
        }
	}
	
	public void addRecord(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", pageData.getString("MODIFY_SERIAL_NUMBER", ""));
        inparam.put("SECTION_ID", pageData.getString("MODIFY_SECTION_ID", ""));
        inparam.put("MONTH_LIMIT", pageData.getString("MODIFY_MONTH_LIMIT", ""));
        inparam.put("START_DATE", pageData.getString("MODIFY_LM_STARTDATE", ""));
        inparam.put("END_DATE", pageData.getString("MODIFY_LM_ENDDATE", ""));
        
        IDataset result = new DatasetList();
        String section_id = inparam.getString("SECTION_ID", "");
        if( !"".equals(section_id) ){
        	result = CSViewCall.call(this, "SS.MailCustManageSVC.AddRecord", inparam);
        }else{
        	result = CSViewCall.call(this, "SS.MailCustManageSVC.AddRecordList", inparam);
        }
        IData resultinfo = result.getData(0);
        setAjax(resultinfo);
	}
	
	public void delRecord(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", pageData.getString("MODIFY_SERIAL_NUMBER", ""));
        inparam.put("SECTION_ID", pageData.getString("MODIFY_SECTION_ID", ""));
        inparam.put("MONTH_LIMIT", pageData.getString("MODIFY_MONTH_LIMIT", ""));
        
        IDataset result = CSViewCall.call(this, "SS.MailCustManageSVC.DelRecord", inparam);
        IData resultinfo = result.getData(0);
        setAjax(resultinfo);
	}
	
	public void deleteAllRecord(IRequestCycle cycle) throws Exception
	{
		IData inparam = new DataMap();
        IDataset result = CSViewCall.call(this, "SS.MailCustManageSVC.DelAllRecord", inparam);
        IData resultinfo = result.getData(0);
        setAjax(resultinfo);
	}
	
	
	public abstract void setCond(IData cond);
	
	public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setListCount(long listCount);

}
