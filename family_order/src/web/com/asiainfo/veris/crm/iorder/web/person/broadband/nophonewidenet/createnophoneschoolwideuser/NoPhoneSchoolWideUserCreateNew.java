
package com.asiainfo.veris.crm.iorder.web.person.broadband.nophonewidenet.createnophoneschoolwideuser;


import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NoPhoneSchoolWideUserCreateNew extends PersonBasePage
{
	
    public void checkSerialNumber(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WideUserCreateSVC.checkSerialNumber", data);
        IDataset acctInfos = dataset.getData(0).getDataset("ALL_ACCT");
        setAllAcct(acctInfos);
        setAjax(dataset);
    }

    /**
     * 获取产品费用
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void getProductFeeInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        data.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        data.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        data.put("PACKAGE_ID", "");
        data.put("ELEMENT_ID", data.getString("ELEMENT_ID"));
        data.put("ELEMENT_TYPE_CODE", "P");
        data.put("TRADE_FEE_TYPE", "3");
        data.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "CS.ProductFeeInfoQrySVC.getProductFeeInfo", data);
        setAjax(dataset);
    }

    /**
     * @param clcle
     * @throws Exception
     * @author chenzm
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WideUserCreateSVC.getWidenetProductInfo", data);
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), dataset);
        
        setProductList(dataset);
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
        
        param.put("TRADE_TYPE_CODE", "650");
        
        setInfo(param);
    }
    
    /**
     * 校验宽带账号
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void checkWidenetAcctId(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData resultData = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.checkWidenetAcctId", data);
        setAjax(resultData);
    }
    
    /**
     * 获得可用的宽带账号
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void getValidWideNetAccountId(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData resultData = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.getValidWideNetAccountId", data);
        setAjax(resultData);
    }
    
    /**
     * 释放宽带账号资源
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void releaseWideNetAcct(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        
        IData resultData = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.releaseWideNetAcct", data);
        setAjax(resultData);
    }
    
    /**
     * 获得可以开户证件类型
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void queryPsptTypeList(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData resultData = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.queryPsptTypeList", data);
        setAjax(resultData);
    }
    
    /**
     * 人像信息比对员工信息
     *
     * @author dengyi
     * @param cycle
     * @throws Exception
     */
    public void isCmpPic(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();

        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isCmpPic", param);
        setAjax(dataset.getData(0));
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        String routeId = data.getString("EPARCHY_CODE");
        // 客服工号，HAIN, 则默认到0898
        if (StringUtils.isBlank(routeId) || routeId.length() != 4 || !StringUtils.isNumeric(routeId))
        {
            data.put("EPARCHY_CODE", "0898");
        }
        data.put("WIDE_SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        data.put("CUST_NAME", data.getString("CONTACT"));
        data.put("WIDE_PSPT_ID", data.getString("PSPT_ID"));
        
        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneSchoolWideUserCreateRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setAllAcct(IDataset datas);

    public abstract void setInfo(IData info);

    public abstract void setProductList(IDataset datas);

}
