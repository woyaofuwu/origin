package com.asiainfo.veris.crm.order.web.person.topsetbox;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;


public abstract class StartTopSetBox extends PersonBasePage{

public abstract void setUserInfo(IData userInfo);
	
	public abstract void setCustInfo(IData custInfo);
	
	public abstract void setResInfo(IData resInfo);
	
	public abstract void setOldResInfo(IData oldResInfo);
	
	public abstract void setBasePackages(IDataset basePackages);

    public abstract void setOptionPackages(IDataset optionPackages);
    
    public abstract void setProducts(IDataset products);

    public abstract void setUserOldInfo(IData userOldInfo);
	
    
	public void loadPageInfo(IRequestCycle cycle) throws Exception
    {

		IData data = getData();
        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        
        if (StringUtils.isNotBlank(userInfo.getString("PRODUCT_ID")))
        {
            userInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, PersonConst.ELEMENT_TYPE_CODE_PRODUCT, userInfo.getString("PRODUCT_ID")));
        }
        
        IDataset dataset = CSViewCall.call(this, "SS.TopSetBoxSVC.checkUserValidForStartTopsetbox", data);
        IData retData = dataset.first();
        IData resInfo = retData.getData("OLD_RES_INFO");
        IData internetTvUserInfo = retData.getData("USER_INFO");
        
        this.setOldResInfo(resInfo);
        this.setCustInfo(custInfo);
        this.setUserInfo(userInfo);
        this.setUserOldInfo(internetTvUserInfo);
        
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
        if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put("INTERNET_TV_SOURCE", "TOPSETBOX_STOP");		//用来标记是做报停
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.StartTopSetBoxRegSVC.tradeReg", data);
        setAjax(dataset);
    	
    }

}
