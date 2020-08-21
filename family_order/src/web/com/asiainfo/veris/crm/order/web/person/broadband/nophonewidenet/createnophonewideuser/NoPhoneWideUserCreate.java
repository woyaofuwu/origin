
package com.asiainfo.veris.crm.order.web.person.broadband.nophonewidenet.createnophonewideuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NoPhoneWideUserCreate extends PersonBasePage
{
    /**
     * 初始化方法
     * 
     * @author yuyj3
     * @param clcle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
        IData param = new DataMap();
        
        param.put("TRADE_TYPE_CODE", "680");
        
        //预约施工时间只能选择48小时之后
        String minDate = SysDateMgr.getAddHoursDate(SysDateMgr.getSysTime(), 48);//SysDateMgr.addDays(2);
        
        param.put("MIN", minDate);
        
        setInfo(param);
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
     * 获取产品费用
     * 
     * @author yuyj3
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
     * 根据产品类型获得产品信息
     * @param clcle
     * @throws Exception
     * @author yuyj3
     */
    public void changeWideProductType(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        
        IData resultData = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.getWidenetProductInfoByWideType", data);
        
        IDataset wideModemStyle  = resultData.getDataset("WIDE_MODEM_STYLE");
        
        IDataset productList  = resultData.getDataset("PRODUCT_LSIT");
        
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), productList);
        setProductList(productList);
        setWideModemStyleList(wideModemStyle);
        setAjax(resultData);
    }

    public void getCreateWideUserStyle(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        //刷新开户方式
        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneWideUserCreateSVC.getCreateWideUserStyle", data);
        setAjax(dataset);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        String routeId = data.getString("EPARCHY_CODE");
        
        // 客服工号，HAIN, 则默认到0898
        if (StringUtils.isBlank(routeId) || routeId.length() != 4 || !StringUtils.isNumeric(routeId))
        {
            data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        }
        
        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneWideUserCreateRegSVC.tradeReg", data);
        setAjax(dataset);
    }
    
    
    /**
     * 获得光猫费用
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void getModemDeposit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData result = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.getModemDeposit", data);
       
        this.setAjax(result);
    }
    

    public abstract void setInfo(IData info);
    
    public abstract void setProductList(IDataset datas);
    
    public abstract void setWideModemStyleList(IDataset datas);

}
