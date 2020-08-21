
package com.asiainfo.veris.crm.order.web.person.renewhusertrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 校园异网号码用户续约
 */
public abstract class RenewHPersonUser extends PersonBasePage
{
    /**
     * 
     */
    public void checkBeforeTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String brand_code = data.getString("BRAND_CODE");
        if(!("XYYW".equalsIgnoreCase(brand_code))){
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "非和校园异网用户无法续费！");
        }
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
        
        IDataset infos = CSViewCall.call(this, "CS.CheckTradeSVC.checkBeforeTrade", data);
        setAjax(infos.getData(0));
    }
    
    /**
     * 查询客户资料后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("AUTH_SERIAL_NUMBER", data.getString("H_SERIAL_NUMBER"));

        //欠费信息
        IDataset busiInfos = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryBusiInfo", data);
         
        IData info = new DataMap();

        info.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        info.putAll(busiInfos.getData(0));

        IDataset productInfo = CSViewCall.call(this, "SS.RenewHPersonUserSVC.getProductInfo", data);
		if (productInfo != null && productInfo.size() > 0) {
			info.putAll(productInfo.getData(0));
		}
		
        IData feeData = CSViewCall.callone(this, "SS.RenewHPersonUserSVC.getTradeFee", data);
        
        if (IDataUtil.isNotEmpty(feeData)) {
        	info.putAll(feeData);
        }
        
        setInfo(info);
    	setAjax(info);
    }
   
    /**
     * 初始化方法
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {    	
        IData param = new DataMap();
        
        param.put("TRADE_TYPE_CODE", "7511");
        setInfo(param);
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
        data.put("SERIAL_NUMBER", data.getString("H_SERIAL_NUMBER"));
        IDataset dataset = new DatasetList();
        								
        dataset = CSViewCall.call(this, "SS.RenewHPersonUserRegSVC.tradeReg", data);
       
        setAjax(dataset);
    }

    public abstract void setInfo(IData info);
}
