
package com.asiainfo.veris.crm.order.web.person.createhusertrade;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CreateHPersonUser extends PersonBasePage
{
	private static transient final Logger logger = Logger.getLogger(CreateHPersonUser.class);
    
    /**
     * 初始化方法
     * 
     * @author zhaohj3
     * @param clcle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
        IData param = new DataMap();
        
        param.put("TRADE_TYPE_CODE", "7510");
        param.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        
        setInfo(param);
    }
    
    /**
     * 获得可以开户证件类型
     * @param cycle
     * @throws Exception
     * @author zhaohj3
     */
    public void queryPsptTypeList(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IData resultData = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.queryPsptTypeList", data);
        setAjax(resultData);
    }
    
	/**
	 * 和校园异网号码开户校验
	 * @param cycle
	 * @return
	 * @throws Exception
	 * @date 2018-1-26 16:39:51
	 */
    public void checkHSerialNumber(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IData resultData = CSViewCall.callone(this, "SS.CreateHPersonUserSVC.checkHSerialNumber", data);
        
		IDataset productTypeList = StaticUtil.getStaticList("H_PRODUCT_TYPE"); // 和校园异网用户开户产品类型
        
        setProductTypeList(productTypeList);
        
        setEditInfo(resultData);
        setAjax(resultData);
    }
    
    /**
     * 根据产品类型获得产品信息
     * @param clcle
     * @throws Exception
     * @author yuyj3
     */
    public void getProductInfoByProductType(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        
        IData resultData = CSViewCall.callone(this, "SS.CreateHPersonUserSVC.getProductInfoByProductType", data);
        
        IDataset productList  = resultData.getDataset("PRODUCT_LIST");
        
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), productList);
        
        setProductList(productList);
        setAjax(resultData);
    }
    
    /**
     * 提交
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        
        IDataset dataset = CSViewCall.call(this, "SS.CreateHPersonUserRegSVC.tradeReg", data);
        setAjax(dataset);
    }
    
    public abstract void setInfo(IData info);
    public abstract void setEditInfo(IData editInfo);
    public abstract void setProductTypeList(IDataset productTypeList);
    public abstract void setProductList(IDataset productList);

}
