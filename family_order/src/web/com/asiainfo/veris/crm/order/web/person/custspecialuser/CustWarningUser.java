package com.asiainfo.veris.crm.order.web.person.custspecialuser;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageEvent;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CustWarningUser extends PersonBasePage{

	@Override
	public void pageBeginRender(PageEvent event) {
		// TODO Auto-generated method stub
		super.pageBeginRender(event);
		
		init();
	}

	public void init() 
	{
		IData cond = new DataMap();
		try {
			if( hasPriv("CUST_SPECIALUSER_MODIFYRIGHT") ){//页面增删改权限
				cond.put("CUST_SPECIALUSER_MODIFYRIGHT", "1");
			}else{
				cond.put("CUST_SPECIALUSER_MODIFYRIGHT", "0");
			}
			
			if( hasPriv("CUST_SPECIALUSER_EXPORT") ){
				cond.put("CUST_SPECIALUSER_EXPORT", "1");
			}else{
				cond.put("CUST_SPECIALUSER_EXPORT", "0");
			}
			
			if( hasPriv("CUST_SPECIALUSER_IMPORT") ){
				cond.put("CUST_SPECIALUSER_IMPORT", "1");
			}else{
				cond.put("CUST_SPECIALUSER_IMPORT", "0");
			}
			
			
			
			setCond(cond);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void queryBatchXmlFile (IRequestCycle cycle) throws Exception{
		IData pageData = getData();
		IData cond = new DataMap();
		cond.putAll(pageData);
		String type = pageData.getString("TYPE", "");
		
		
		if( hasPriv("CUST_SPECIALUSER_MODIFYRIGHT") ){//页面增删改权限
			cond.put("CUST_SPECIALUSER_MODIFYRIGHT", "1");
		}else{
			cond.put("CUST_SPECIALUSER_MODIFYRIGHT", "0");
		}
		
		if( hasPriv("CUST_SPECIALUSER_EXPORT") ){
			cond.put("CUST_SPECIALUSER_EXPORT", "1");
		}else{
			cond.put("CUST_SPECIALUSER_EXPORT", "0");
		}
		
		if( hasPriv("CUST_SPECIALUSER_IMPORT") ){
			cond.put("CUST_SPECIALUSER_IMPORT", "1");
		}else{
			cond.put("CUST_SPECIALUSER_IMPORT", "0");
		}
		
		setCond(cond);
	}
	
	public void queryList(IRequestCycle cycle) throws Exception
	{
		
		
		IData pageData = getData();
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", pageData.getString("SERIAL_NUMBER", ""));
        inparam.put("USER_NAME", pageData.getString("USER_NAME", ""));
        inparam.put("TYPE", pageData.getString("TYPE", ""));
        inparam.put("FLAG", pageData.getString("FLAG", ""));
        inparam.put("RESERVE1", pageData.getString("RESERVE1", ""));
        inparam.put("RESERVE2", pageData.getString("RESERVE2", ""));
        
        IDataOutput out= CSViewCall.callPage(this, "SS.CustSpecialUser.QueryList", inparam, getPagination("page"));
        // String result = dataset.getData(0).getString("TIP");
        setInfos(out.getData());
        
        setListCount(out.getDataCount());
	}
	
	public void modifyRecord(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", pageData.getString("MODIFY_SERIAL_NUMBER", ""));
        inparam.put("USER_NAME", pageData.getString("MODIFY_USER_NAME", ""));
        inparam.put("TYPE", pageData.getString("MODIFY_TYPE", ""));
        inparam.put("FLAG", pageData.getString("MODIFY_FLAG", "0"));
        inparam.put("RESERVE1", pageData.getString("MODIFY_RESERVE1", ""));
        inparam.put("RESERVE2", pageData.getString("MODIFY_RESERVE2", ""));
        inparam.put("START_DATE", pageData.getString("MODIFY_START_DATE", ""));
        inparam.put("END_DATE", pageData.getString("MODIFY_END_DATE", ""));
        
        IDataset result = CSViewCall.call(this, "SS.CustSpecialUser.UpdateRecord", inparam);
        IData resultinfo = result.getData(0);
        setAjax(resultinfo);
        if( null == resultinfo || 0 != resultinfo.getInt("X_RESULTCODE", -1)){//update failed
        	
        }else{
        	IDataset dataset = CSViewCall.call(this, "SS.CustSpecialUser.QueryList", pageData);
            setInfos(dataset);
        }
        
	}
	
	public void addRecord(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", pageData.getString("MODIFY_SERIAL_NUMBER", ""));
        inparam.put("USER_NAME", pageData.getString("MODIFY_USER_NAME", ""));
        inparam.put("TYPE", pageData.getString("MODIFY_TYPE", ""));
        inparam.put("FLAG", pageData.getString("MODIFY_FLAG", "0"));
        inparam.put("RESERVE1", pageData.getString("MODIFY_RESERVE1", ""));
        inparam.put("RESERVE2", pageData.getString("MODIFY_RESERVE2", ""));
        inparam.put("START_DATE", pageData.getString("MODIFY_START_DATE", ""));
        inparam.put("END_DATE", pageData.getString("MODIFY_END_DATE", ""));
        
        IDataset result = new DatasetList();
        
        result = CSViewCall.call(this, "SS.CustSpecialUser.AddRecord", inparam);
        
        IData resultinfo = result.getData(0);
        setAjax(resultinfo);
	}
	
	public void delRecord(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", pageData.getString("MODIFY_SERIAL_NUMBER", ""));
        inparam.put("TYPE", pageData.getString("MODIFY_TYPE", ""));
        
        
        IDataset result = CSViewCall.call(this, "SS.CustSpecialUser.DelRecord", inparam);
        IData resultinfo = result.getData(0);
        setAjax(resultinfo);
	}
	
	
	
	
	public abstract void setCond(IData cond);
	
	public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setListCount(long listCount);
    
}
