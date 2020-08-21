package com.asiainfo.veris.crm.order.web.person.coupons;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData; 
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CreateCouponsUser extends PersonBasePage
{ 
    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode()); 
        String feeInfo = data.getString("TICKET_INFO", "[]");
        data.put("TICKET_LIST", new DatasetList(feeInfo));
        data.remove("TICKET_INFO");
        IDataset dataset = CSViewCall.call(this, "SS.CouponsTradeSVC.insertCouponsUser", data);
        setAjax(dataset.getData(0));
    }
    public void checkSn(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	data.put("SERIAL_NUMBER", data.getString("cond_SERIAL_NUMBER", ""));
    	IDataset dataset = CSViewCall.call(this, "SS.CouponsTradeSVC.checkSn", data);
        setAjax(dataset.getData(0));
    }
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset results = CSViewCall.call(this, "SS.CouponsQuotaMgrSVC.getWorkOrders", data);
        if(!results.isEmpty() && results.size()!=0){
        	this.setAuditInfos(results);
        	String typeId = "CCUFZ";
        	IDataset ids = StaticUtil.getStaticList(typeId);
        	if(IDataUtil.isNotEmpty(ids))
        	{
        		results.getData(0).put("TTICKET_VALUE", ids.getData(0).getString("DATA_ID", "0"));
        	}else
        	{
        		results.getData(0).put("TTICKET_VALUE", "0");
        	}
        	
        	this.setInfo(results.getData(0));
        }
    }
    public abstract void setAuditInfos(IDataset dataset);
    public abstract void setInfo(IData data);
    
}