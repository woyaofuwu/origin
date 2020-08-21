
package com.asiainfo.veris.crm.iorder.web.person.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;


public abstract class CreditPurchasesSaleActiveTrade extends PersonBasePage
{
    
    /**
     * 用户鉴权后初始化方法
     * @param clcle
     * @throws Exception
     * @author yuyj3
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	data.put("EPARCHY_CODE", data.getString("ROUTE_EPARCHY_CODE"));
    	IDataset campnTypes = CSViewCall.call(this, "CS.SaleActiveQuerySVC.getCampnTypes", data);
    	data.put("CREDIT_PURCHASES", "1");
        IDataset products = CSViewCall.call(this, "CS.SaleActiveQuerySVC.queryAllAvailableProducts", data);
        setProducts(products);
    	setCampnTypes(campnTypes);
    	this.setAjax(campnTypes);
    }   

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData initParam = new DataMap();
        initParam.put("authType", data.getString("authType", "00"));
        initParam.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE", "240"));
        initParam.put("LABEL_ID", data.getString("LABEL_ID"));
        setInfo(initParam);
        
    }
    
    /**
     * 查询用户可以办理的所有营销方案
     * @throws Exception
     */
    public void queryAllProducts(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("EPARCHY_CODE", data.getString("ROUTE_EPARCHY_CODE"));
        data.put("CREDIT_PURCHASES", "1");
        IDataset products = CSViewCall.call(this, "CS.SaleActiveQuerySVC.queryAllAvailableProducts", data);
        setProducts(products);
        this.setAjax(products);
    }
    
    /**
     * 校验要办理的营销方案
     * @throws Exception
     */
    public void cheackProduct(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	String userId = data.getString("USER_ID", "");
        String custId = data.getString("CUST_ID", "");
        String productId = data.getString("PRODUCT_ID", "");
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("CUST_ID", custId);
        param.put("PRODUCT_ID", productId);
        IDataset results = CSViewCall.call(this, "CS.SaleActiveQuerySVC.choiceProductNode", param);
    }
    
    public void renderByActiveQry(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("EPARCHY_CODE", data.getString("ROUTE_EPARCHY_CODE"));
        data.put("CREDIT_PURCHASES", "1");
        
        String productId = data.getString("PRODUCT_ID");
        String newImei = data.getString("NEW_IMEI");
        IDataset saleactives = new DatasetList();
        String querySaleActivesSvc = "CS.SaleActiveQuerySVC.querySaleActives";
        if (StringUtils.isNotBlank(productId) && StringUtils.isNotBlank(newImei)) {
            querySaleActivesSvc = "CS.SaleActiveQuerySVC.querySaleActivesByImei";
            
        }
        saleactives = CSViewCall.call(this, querySaleActivesSvc, data);
        
        IDataset saleactives2 = new DatasetList();
        for (int i =0;i< saleactives.size();  i++) {
        	if(!"1".equals(saleactives.getData(i).getString("ERROR_FLAG",""))){
        		saleactives2.add(saleactives.getData(i));
        	}
        }
        if(IDataUtil.isNotEmpty(saleactives2)){
        	saleactives.clear();
        	saleactives.addAll(saleactives2);
        }

        IData info = new DataMap();
        for (int i = saleactives.size() - 1; i >= 0; i--) {
            IData saleactive = saleactives.getData(i);
            if ("true".equals(saleactive.getString("OTHER_INFO"))) {
                saleactives.remove(i);
                continue;
            }
            saleactive.put("PACKAGE_NAME", saleactive.getString("PACKAGE_NAME") + "|" + saleactive.getString("PACKAGE_ID"));
            //saleactive.put("PACKAGE_TITLE", saleactive.getData("GOODS_INFO").getString("SALE_PRICE"));
        }

        if (IDataUtil.isEmpty(saleactives)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "没有查到符合条件的营销包");
        }
        // 20160111 by songlm 冼乃捷 紧急需求，暂无需求名，作用：将终端类型传进规则内
        String deviceModelCode = saleactives.getData(0).getString("DEVICE_MODEL_CODE","");
        if (StringUtils.isNotBlank(deviceModelCode)) {
            info.put("DEVICE_MODEL_CODE", deviceModelCode);
        }
        // end
        
        if (StringUtils.isNotBlank(newImei)) {
            info.put("IMEI", newImei);
            setInfo(info);
        }
        
        setPackages(saleactives);
        this.setAjax(saleactives);
    }
    
    public void configSaleActives(IRequestCycle cycle) throws Exception
    {
    	 IData data = getData();
    	 String productId = data.getString("PRODUCT_ID");
    	 String pakageId = data.getString("PACKAGE_ID");
    	 IData param = new DataMap();
    	 param.put("SUBSYS_CODE", "CSM");
         param.put("PARAM_ATTR", "3119");
     	 param.put("PARAM_CODE", productId);
    	 param.put("EPARCHY_CODE", getTradeEparchyCode());
         IDataset configSaleActives = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommpara", param);
         for(int i=0;i<configSaleActives.size();i++){
        	 if(pakageId.equals(configSaleActives.getData(i).getString("PARA_CODE4"))){
        		 this.setAjax(configSaleActives.getData(i));
        		 break;
        	 }
         }
    }
    

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset rtDataset = CSViewCall.call(this, "SS.SaleActiveRegSVC.tradeReg4Intf", data);
        this.setAjax(rtDataset);
    }

    public void checktradeFeeInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("EPARCHY_CODE", "0898");
        IData tradeFeeInfo = CSViewCall.callone(this, "CS.SaleActiveQuerySVC.queryFeeInfo", data);
        this.setAjax(tradeFeeInfo);
    }
    
    
    public abstract void setCustInfo(IData custInfo);

    public abstract void setGoods(IDataset goods);

    public abstract void setInfo(IData info);

    public abstract void setProducts(IDataset products);

    public abstract void setPackages(IDataset Packages);
    
    
    public abstract String getEparchyCodeCompId();

    public abstract String getNeedCheck();

    public abstract String getNeedUserId();
    
    public abstract void setAfterSelectPackageEvent(String afterSelectPackageEvent);

    public abstract void setCampnTypes(IDataset campnTypes);

    public abstract void setHotRecommSales(IDataset hotRecommSales);

    public abstract void setSaleInfos(IDataset saleInfos);

    public abstract void setTradeTypeCode(String tradeTypeCode);
}
