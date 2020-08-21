package com.asiainfo.veris.crm.order.web.person.internettv;

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

/**
 * Copyright: Copyright (c) 2016 Asiainfo-Linkage
 * 
 * @ClassName: InternetTv.java
 * @Description: 魔百和开户
 * @version: v1.0.0
 * @author: songlm
 * @date: 2016-6-12
 */
public abstract class InternetTvOpen extends PersonBasePage
{

	/**
	 * 输入号码后的查询
	 * */
    public void setPageInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        this.setCustInfo(custInfo);//客户名称
        
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        
        userInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, PersonConst.ELEMENT_TYPE_CODE_PRODUCT, userInfo.getString("TD_B_PRODUCT")));
        
        this.setUserInfo(userInfo);//产品名称、开户日期
        
        IDataset dataset = CSViewCall.call(this, "SS.InternetTvOpenSVC.checkUserForOpenInternetTV", data);
        IData retData = dataset.first();
        this.setUserOldInfo(retData);//宽带信息
        
        IDataset products = retData.getDataset("PRODUCT_INFO_SET");
        this.setProducts(products);//魔百和产品列表
    }
	
    /**
     * 选中魔百和产品后
     * */
    public void queryDiscntPackagesByPID(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.InternetTvOpenSVC.queryDiscntPackagesByPID", data);
        IData retData = dataset.first();
        IDataset basePackages = retData.getDataset("B_P");
        IDataset optionPackages = retData.getDataset("O_P");
        IDataset platSvcPackages = retData.getDataset("P_P");
        IDataset topSetBoxSaleActiveList = retData.getDataset("TOP_SET_BOX_SALE_ACTIVE_LIST");
        this.setBasePackages(basePackages);
        this.setOptionPackages(optionPackages);
        this.setTopSetBoxSaleActiveList(topSetBoxSaleActiveList);
        this.setPlatSvcPackages(platSvcPackages);
        //BUS201907310012关于开发家庭终端调测费的需求
        IDataset topSetBoxSaleActiveList2 = retData.getDataset("TOP_SET_BOX_SALE_ACTIVE_LIST2");
        this.setTopSetBoxSaleActiveList2(topSetBoxSaleActiveList2);
        //BUS201907310012关于开发家庭终端调测费的需求
        setAjax(retData);
    }
	
    /**
     * 选中魔百和营销活动后
     */
    public void checkSaleActive(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.InternetTvOpenSVC.checkSaleActive", data);
        this.setAjax(result);
    }
    
    /**
     * 营销活动费用校验
     */
    public void queryCheckSaleActiveFee(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.InternetTvOpenSVC.queryCheckSaleActiveFee", data);
        this.setAjax(result);
    }

    
    /**
     * 提交前费用校验
     * @param cycle
     * @throws Exception
     * @author zhangyc5
     */
    public void checkFeeBeforeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.InternetTvOpenSVC.checkFeeBeforeSubmit", data);
        this.setAjax(result);
    }

	
    /**
     * 业务提交
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.InternetTvOpenRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setBasePackages(IDataset basePackages);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setOptionPackages(IDataset optionPackages);

    public abstract void setProducts(IDataset products);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setUserOldInfo(IData userOldInfo);
    
    public abstract void setTopSetBoxSaleActiveList(IDataset datas);
    
    //BUS201907310012关于开发家庭终端调测费的需求
    public abstract void setTopSetBoxSaleActiveList2(IDataset datas);
    
    public abstract void setPlatSvcPackages(IDataset platSvcPackages);

}
