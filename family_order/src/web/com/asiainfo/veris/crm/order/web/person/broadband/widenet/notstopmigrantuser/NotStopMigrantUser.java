package com.asiainfo.veris.crm.order.web.person.broadband.widenet.notstopmigrantuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NotStopMigrantUser extends PersonBasePage 
{
	/**
     * 查询是否登记
     */
	public void queryInfo(IRequestCycle cycle) throws Exception 
	{
		IData pagedata = getData();
		IData inputData = new DataMap();
		inputData.put("SERIAL_NUMBER", pagedata.getString("SERIAL_NUMBER", "0"));
		inputData.put("USER_ID", pagedata.getString("USER_ID", "0"));
		inputData.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
		IData data = CSViewCall.callone(this, "SS.NotStopMigrantUserSVC.queryInfo", inputData);
		setMigrantStateInfo(data);
		setAjax(data);
	}
	
	/**
     * 提交登记
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

    	IData pagedata = getData();
		IData inputData = new DataMap();
		inputData.put("SERIAL_NUMBER", pagedata.getString("SERIAL_NUMBER", "0"));
		inputData.put("USER_ID", pagedata.getString("USER_ID", "0"));
		inputData.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
		IData data = CSViewCall.callone(this, "SS.NotStopMigrantUserSVC.onTradeSubmit", inputData);
        setAjax(data);
    }
    
    public abstract void setMigrantStateInfo(IData data);

}