package com.asiainfo.veris.crm.order.web.person.apnusermgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ApnUserUnBindingForOlcom extends PersonBasePage{

	/**
     * 菜单点击执行的事件
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData pgData = this.getData();
    }
    
    /**
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
    	IData pgData = this.getData();
    	String userId = pgData.getString("USER_ID","");
    	String serialNumber = pgData.getString("SERIAL_NUMBER","");
    	if(StringUtils.isNotBlank(userId))
    	{
    		IData inParam = new DataMap();
    		inParam.put("USER_ID", userId);
    		inParam.put("SERIAL_NUMBER", serialNumber);
    		inParam.put("REMOVE_TAG", "0");
    		Pagination page = getPagination("recordNav");
    		if(page != null)
    		{
    			page.setPageSize(10);
    		}
    		IDataOutput apnInfos = CSViewCall.callPage(this, 
    				"SS.ApnUserBindingForOlcomSVC.queryUserApnInfoByUserId", inParam, page);
	  
    		long dataCount = apnInfos.getDataCount();
    		setRecordCount(dataCount);
    		this.setUserApnInfos(apnInfos.getData());
    	}
    	
    }
    
    /**
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryUserApnInfo(IRequestCycle cycle) throws Exception 
    {
    	IData pageData = this.getData();
    	String serialNumber = pageData.getString("AUTH_SERIAL_NUMBER","");
    	if(StringUtils.isNotBlank(serialNumber))
    	{
    		IData paramQry = new DataMap();
    		paramQry.put("SERIAL_NUMBER", serialNumber);
			IDataset userInfos = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserInfoBySn", paramQry);
			if(IDataUtil.isNotEmpty(userInfos))
			{
				String userId = userInfos.getData(0).getString("USER_ID","");
				if(StringUtils.isNotBlank(userId))
		    	{
		    		IData inParam = new DataMap();
		    		inParam.put("USER_ID", userId);
		    		inParam.put("REMOVE_TAG", "0");
		    		Pagination page = getPagination("recordNav");
		    		IDataOutput apnInfos = CSViewCall.callPage(this, 
		    				"SS.ApnUserBindingForOlcomSVC.queryUserApnInfoByUserId", inParam, page);
			  
		    		long dataCount = apnInfos.getDataCount();
		    		setRecordCount(dataCount);
		    		this.setUserApnInfos(apnInfos.getData());
		    	}
			}
    	}
    	
    }
    
    /**
     * 业务提交
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
    	IData pageData = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.ApnUserUnBindingForOlcomRegSVC.tradeReg", pageData);
        this.setAjax(rtDataset);
    }
    
    public abstract void setUserApnInfos(IDataset userApnInfos);
    
    public abstract void setUserApnInfo(IData userApnInfo);

    public abstract void setRecordCount(long recordCount); 
    
    public abstract void setInfo(IData info);
}
