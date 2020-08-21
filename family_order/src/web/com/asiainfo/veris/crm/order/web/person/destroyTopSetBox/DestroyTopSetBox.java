package com.asiainfo.veris.crm.order.web.person.destroyTopSetBox;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class DestroyTopSetBox extends PersonBasePage{

	public abstract void setUserInfo(IData userInfo);
	
	public abstract void setResInfo(IData resInfo);
	
	
	
	public void loadPageInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        
        //封装前台的用户信息
        IData custInfoBack=new DataMap();
        custInfoBack.put("CUST_NAME", custInfo.get("CUST_NAME"));
        custInfoBack.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID",""));
        custInfoBack.put("OPEN_DATE", userInfo.get("OPEN_DATE"));
        
        if (StringUtils.isNotBlank(userInfo.getString("PRODUCT_ID")))
        {
            custInfoBack.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, PersonConst.ELEMENT_TYPE_CODE_PRODUCT, userInfo.getString("PRODUCT_ID")));
        }
        
        this.setUserInfo(custInfoBack);
        
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.DestroyTopSetBoxSVC.checkUserValid", data);
        if(IDataUtil.isNotEmpty(dataset)){
        	IData retData = dataset.first();
        	
            //BUS201907310012关于开发家庭终端调测费的需求
        	String serialNumber=data.getString("AUTH_SERIAL_NUMBER");
        	data.put("SERIAL_NUMBER", serialNumber);
        	data.put("MODEL_TYPE", "TOP_SET_BOX");
            IDataset ids2=CSViewCall.call(this, "SS.GponWidenetOrderDestorySVC.queryWidenetCommissioningFee", data);
        	if (ids2!=null && ids2.size()>0)
        	{
        		retData.getData("RES_INFO").put("COMMISSIONING_FEE_TAG", ids2.getData(0).getString("COMMISSIONING_FEE_TAG",""));
        		retData.getData("RES_INFO").put("COMMISSIONING_FEE", ids2.getData(0).getString("COMMISSIONING_FEE",""));
        		retData.getData("RES_INFO").put("LEFT_MONTHS", ids2.getData(0).getString("LEFT_MONTHS",""));
        		retData.getData("RES_INFO").put("ACTIVE_FEE_TAG", ids2.getData(0).getString("ACTIVE_FEE_TAG","0"));
        	}
        	else
        	{
        		retData.getData("RES_INFO").put("COMMISSIONING_FEE_TAG", "0");
        		retData.getData("RES_INFO").put("COMMISSIONING_FEE", "0");
        		retData.getData("RES_INFO").put("LEFT_MONTHS", "0");

        	}
        	//BUS201907310012关于开发家庭终端调测费的需求
        	
        	this.setResInfo(retData.getData("RES_INFO"));
        	
        	setAjax(retData.getData("RES_INFO"));
        }
    }
	
	
	/**
     * 业务提交（onTradeSubmit cssubmit组件中默认的提交action方法）
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.DestroyTopSetBoxRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    
}
