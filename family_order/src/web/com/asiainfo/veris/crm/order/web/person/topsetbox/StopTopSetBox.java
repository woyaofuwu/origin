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


public abstract class StopTopSetBox extends PersonBasePage{

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
        
        IDataset dataset = CSViewCall.call(this, "SS.TopSetBoxSVC.checkUserValidForStopTopsetbox", data);
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
//    	/*
//    	 * 获取用户的平台服务信息
//    	 */
//        IData data = getData();
//        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
//        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
//        IDataset dataset = CSViewCall.call(this, "SS.TopSetBoxSVC.obtainUserTopSetPlatSvc", data);
//        
//        IData svcInfo=dataset.getData(0);
//        
//        String baseService=svcInfo.getString("BASE_SERVICE","");
//        String optionService=svcInfo.getString("OPTION_SERVICE","");
//        
//        
//        /*
//         * 封装套餐信息
//         */
//        IData param=new DataMap();
//        param.put("TRADE_TYPE_CODE", "3700");
//        param.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
//        param.put("AUTH_SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
//        
//        IDataset serviceDatas=new DatasetList();
//        /*
//         * 基础服务
//         */
//        IData serviceDataBase=new DataMap();
//        serviceDataBase.put("SERVICE_ID", baseService);
//        serviceDataBase.put("BIZ_STATE_CODE", "N");	//暂停状态
//        serviceDataBase.put("MODIFY_TAG", "2");
//        serviceDataBase.put("OPER_CODE", "04");
//        serviceDatas.add(serviceDataBase);
//        /*
//         * 可选服务
//         */
//        if(optionService!=null&&!optionService.equals("")){
//        	IData serviceDataOption=new DataMap();
//            serviceDataOption.put("SERVICE_ID", optionService);
//            serviceDataOption.put("BIZ_STATE_CODE", "N");	//暂停状态
//            serviceDataOption.put("MODIFY_TAG", "2");
//            serviceDataOption.put("OPER_CODE", "04");
//            serviceDatas.add(serviceDataOption); 
//        }
//        
//        param.put("SELECTED_ELEMENTS", serviceDatas);
//        
//        
//        IDataset result = CSViewCall.call(this, "SS.PlatRegSVC.tradeReg", param);
//        this.setAjax(result);
    	
    	
    	IData data = getData();
        if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put("INTERNET_TV_SOURCE", "TOPSETBOX_STOP");		//用来标记是做报停
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.StopTopSetBoxRegSVC.tradeReg", data);
        setAjax(dataset);
    	
    }

}
