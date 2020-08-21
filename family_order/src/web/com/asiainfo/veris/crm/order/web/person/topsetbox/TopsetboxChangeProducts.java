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

public abstract class TopsetboxChangeProducts extends PersonBasePage{

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
        
        IDataset dataset = CSViewCall.call(this, "SS.TopSetBoxSVC.checkUserValidForChangeProduct", data);
        IData retData = dataset.first();
        IData resInfo = retData.getData("OLD_RES_INFO");
        IData internetTvUserInfo = retData.getData("USER_INFO");
        
        this.setOldResInfo(resInfo);
        this.setCustInfo(custInfo);
        this.setUserInfo(userInfo);
        this.setUserOldInfo(internetTvUserInfo);
        
    }
    
    /**
     * 查询产品信息
     * @param cycle
     * @throws Exception
     */
    public void queryProductInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.TopSetBoxSVC.queryTopsetboxProductInfo", data);
        this.setProducts(dataset);
    }
    
    
    /**
     * @Function: queryDiscntPackagesByPID()
     * @Description: 查询互联网电视机顶盒基础优惠包（0）和可选优惠包（2）
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-4 下午8:04:33 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-4 yxd v1.0.0 修改原因
     */
    public void queryDiscntPackagesByPID(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.TopSetBoxSVC.queryDiscntPackagesByPID", data);
        IData retData = dataset.first();
        IDataset basePackages = retData.getDataset("B_P");
        IDataset optionPackages = retData.getDataset("O_P");
        this.setBasePackages(basePackages);
        this.setOptionPackages(optionPackages);
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
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.TopSetBoxChangeProductRegSVC.tradeReg", data);
        setAjax(dataset);
    }
    
    /**
     * @Function: checkTerminal()
     * @Description: 终端校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-2 下午4:58:49 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-2 yxd v1.0.0 修改原因
     */
    public void checkTerminal(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.TopSetBoxSVC.checkTerminal", data);
        IData retData = dataset.first();
        IDataset products = retData.getDataset("PRODUCT_INFO_SET");
        this.setResInfo(retData);
        this.setProducts(products);
        this.setAjax(retData);
    }
     
    	
}
