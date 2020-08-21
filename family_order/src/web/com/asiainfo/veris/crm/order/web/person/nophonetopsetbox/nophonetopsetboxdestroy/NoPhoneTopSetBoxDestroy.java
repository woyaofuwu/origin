package com.asiainfo.veris.crm.order.web.person.nophonetopsetbox.nophonetopsetboxdestroy;

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

public abstract class NoPhoneTopSetBoxDestroy extends PersonBasePage{

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
        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneTopSetBoxDestroySVC.checkUserValid", data);
        
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset commissioningFee = CSViewCall.call(this, "SS.GponWidenetOrderDestorySVC.queryCommissioningFee", data);
        
        if(IDataUtil.isNotEmpty(dataset)){
        	IData retData = dataset.first();
        	
        	if(IDataUtil.isNotEmpty(dataset)){
        		retData.getData("RES_INFO").putAll(commissioningFee.getData(0));
        	}
        	
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
        
        String serialNumber=data.getString("AUTH_SERIAL_NUMBER");
		
		if (!serialNumber.startsWith("KD_"))
		{
			serialNumber = "KD_" + serialNumber; 
		}
		
        data.put("SERIAL_NUMBER", serialNumber);
        
		//通过后台获取 147号码（魔百和绑定在147手机号码上）
		IData relaUU = CSViewCall.callone(this, "SS.NoPhoneTopSetBoxSVC.getRelaUUInfoBySnB", data);
		data.put("SERIAL_NUMBER", relaUU.getString("SERIAL_NUMBER_A"));   //147号码
		data.put("SERIAL_NUMBER_B", serialNumber);   //宽带号码
		
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneTopSetBoxDestroyRegSVC.tradeReg", data);
        setAjax(dataset);
    }
    
}
