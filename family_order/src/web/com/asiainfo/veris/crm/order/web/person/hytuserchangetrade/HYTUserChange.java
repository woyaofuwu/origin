
package com.asiainfo.veris.crm.order.web.person.hytuserchangetrade;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class HYTUserChange extends PersonBasePage
{
	private static transient final Logger logger = Logger.getLogger(HYTUserChange.class);
    
    /**
     * 
     * @Description：初始化方法
     * @param:@param clcle
     * @param:@throws Exception
     * @return void
     * @throws
     * @Author :tanzheng
     * @date :2018-5-28下午05:09:34
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
        setTradeTypeCode(data.getString("TRADE_TYPE_CODE"));
        setAuthType(data.getString("AUTH_TYPE", "00"));
    }
    /**
     * 
     * @Description：校验用户是否为海洋通用户
     * @param:@param cycle
     * @param:@throws Exception
     * @return void
     * @throws
     * @Author :tanzheng
     * @date :2018-6-4下午03:54:50
     */
    public void checkIsHYTUser(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
     
        IData resultData = CSViewCall.callone(this, "SS.HYTUserChangeSVC.checkIsHYTUser", data);
        IDataset dataset = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndEparchyCode(this, "CSM", "313", "ZZZZ");
        IDataset dataset2 = dealSelectData(dataset);
        setDistInfo(dataset2);
        setAjax(resultData);
    }
    
    
    
    /**
     * 
     * @Description：校验用户是否可以办理该套餐
     * @param:@param cycle
     * @param:@throws Exception
     * @return void
     * @throws
     * @Author :tanzheng
     * @date :2018-5-28下午05:09:27
     */
    public void checkValidDiscnt(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
     
        IData resultData = CSViewCall.callone(this, "SS.CreateHYTPersonUserSVC.checkValidDiscnt", data);
        setAjax(resultData);
    }
    
    /**
	 * @Description：TODO
	 * @param:@param dataset
	 * @param:@return
	 * @return IDataset
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-5-25上午08:45:42
	 */
	private IDataset dealSelectData(IDataset dataset) {
		IDataset result = new DatasetList();
		for(Object obj:dataset){
			IData data = (IData)obj;
			IData newData = new DataMap();
			newData.put("DISCNT_CODE", data.get("PARAM_CODE"));
			newData.put("DISCNT_NAME", data.get("PARA_CODE1"));
			result.add(newData);
		}
		
		
		return result;
	}
	/**
	 * 
	 * @Description：提交
	 * @param:@param cycle
	 * @param:@throws Exception
	 * @return void
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-5-29下午03:26:51
	 */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = null;
        IData resultData = CSViewCall.callone(this, "SS.CreateHYTPersonUserSVC.checkValidDiscnt", data);
        if("0000".equals(resultData.getString("CODE"))){
        	data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        	data.put("SERIAL_NUMBER",data.getString("AUTH_SERIAL_NUMBER"));
        	data.put("SKIP_RULE","TRUE");
        	dataset = CSViewCall.call(this, "SS.HYTUserChangeRegSVC.tradeReg", data);
        }
        
        setAjax(dataset);
    }
    
    public abstract void setInfo(IData info);
    public abstract void setEditInfo(IData editInfo);
    public abstract void setDistInfo(IDataset dataset);
    public abstract void setProductTypeList(IDataset productTypeList);
    public abstract void setProductList(IDataset productList);
    
    public abstract void setAuthType(String authType);


    public abstract void setTradeTypeCode(String tradeTypeCode);

}
