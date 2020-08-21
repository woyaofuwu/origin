package com.asiainfo.veris.crm.order.web.person.apnusermgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ApnUserBindingForOlcom extends PersonBasePage{

	/**
     * 菜单点击执行的事件
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData pgData = this.getData();
        
        IData inParam = new DataMap();
		inParam.put("REMOVE_TAG", "0");
		IDataset apnInfos = CSViewCall.call(this, "SS.ApnUserBindingForOlcomSVC.queryAllUserApn", inParam);
		this.setApnInfos(apnInfos);
		
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
    	
    	IData inParam = new DataMap();
 		inParam.put("REMOVE_TAG", "0");
 		IDataset apnInfos = CSViewCall.call(this, "SS.ApnUserBindingForOlcomSVC.queryAllUserApn", inParam);
 		this.setApnInfos(apnInfos);
 		
    	this.setInfo(pgData);
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
    	
    	IData inParam = new DataMap();
 		inParam.put("REMOVE_TAG", "0");
 		IDataset apnInfos = CSViewCall.call(this, "SS.ApnUserBindingForOlcomSVC.queryAllUserApn", inParam);
 		this.setApnInfos(apnInfos);
 		
    }
    
    /**
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryUserApnByName(IRequestCycle cycle) throws Exception 
    {
    	IData pageData = this.getData();
    	String apnName = pageData.getString("APN_NAME","");
    	IData data = new DataMap();
    	if(StringUtils.isNotBlank(apnName))
    	{
    		IData inParam = new DataMap();
     		inParam.put("REMOVE_TAG", "0");
     		inParam.put("APN_NAME", apnName);
     		IDataset apnInfos = CSViewCall.call(this, "SS.ApnUserBindingForOlcomSVC.queryUserApnByName", inParam);
     		if(IDataUtil.isNotEmpty(apnInfos))
     		{
     			data = apnInfos.getData(0);
     		}
    	}
    	setAjax(data);
    }
    
    
    /**
     * 业务提交
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
    	IData pageData = getData();
    	
    	String serialNumber = pageData.getString("AUTH_SERIAL_NUMBER","");
    	
    	if(StringUtils.isBlank(serialNumber))
    	{
    		CSViewException.apperr(CrmCommException.CRM_COMM_103,"请输入办理业务的手机号码!");
    	}
    	
    	String userId = "";
    	IData paramQry = new DataMap();
		paramQry.put("SERIAL_NUMBER", serialNumber);
		IDataset userInfos = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserInfoBySn", paramQry);
		if(IDataUtil.isNotEmpty(userInfos))
		{
			userId = userInfos.getData(0).getString("USER_ID","");
	    	if(StringUtils.isBlank(userId))
	    	{
	    		String errInfo = "未获取到该手机号码" + serialNumber + "的用户信息!";
	    		CSViewException.apperr(CrmCommException.CRM_COMM_103,errInfo);
	    	}
		}
		
		String anpName = pageData.getString("APN_NAME","");
		String apnCnxId = pageData.getString("APN_CNTXID","");
		String apnTplId = pageData.getString("APN_TPLID","");
		paramQry.clear();
		paramQry.put("USER_ID", userId);
		paramQry.put("APN_NAME", anpName);
		paramQry.put("APN_CNTXID", apnCnxId);
		paramQry.put("APN_TPLID", apnTplId);
		paramQry.put("REMOVE_TAG", "0");
		IDataset infoResults = CSViewCall.call(this, "SS.ApnUserBindingForOlcomSVC.queryUserApnInfoByOther", paramQry);
		if(IDataUtil.isNotEmpty(infoResults))
		{
			String errInfo = "该用户已经存在了APN信息,请重新选择!";
    		CSViewException.apperr(CrmCommException.CRM_COMM_103,errInfo);
		}
		
		
        IDataset rtDataset = CSViewCall.call(this, "SS.ApnUserBindingForOlcomRegSVC.tradeReg", pageData);
        this.setAjax(rtDataset);
    }
    
    public abstract void setUserApnInfos(IDataset userApnInfos);
    
    public abstract void setUserApnInfo(IData userApnInfo);

    public abstract void setRecordCount(long recordCount); 
    
    public abstract void setInfo(IData info);
    
    public abstract void setApnInfos(IDataset apnInfos);
}
