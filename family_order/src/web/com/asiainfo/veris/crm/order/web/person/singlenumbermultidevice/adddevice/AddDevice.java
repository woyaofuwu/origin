
package com.asiainfo.veris.crm.order.web.person.singlenumbermultidevice.adddevice;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.person.simcardmgr.SimCardBasePage;

public abstract class AddDevice extends SimCardBasePage
{
    /**
     * 输入新开户号码后的校验，获取开户信息
     * 
     * @author yuyj3
     * @param clcle
     * @throws Exception
     */
    public void checkSerialNumber(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkSerialNumber", data);
        
        setEditInfo(dataset.first());
        setAjax(dataset);
    }

    /**
     * 输入SIM卡后的校验，获取卡信息
     * 
     * @author yuyj3
     * @param clcle
     * @throws Exception
     */
    public void checkSimCardNo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkSimCardNo", data);
        
        setEditInfo(dataset.first());
        setAjax(dataset);
    }
    
    /**
     * 人像信息比对员工信息
     * 
     * @author yuyj3
     * @param clcle
     * @throws Exception
     */
    public void isCmpPic(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	IData param = new DataMap();
    	
    	param.putAll(data);
    	param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	
    	IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isCmpPic", param);
    	setAjax(dataset.getData(0));
    }



    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
    	 IData data = getData();
    	 
    	 IData resultData = CSViewCall.callone(this, "SS.SingleNumMultiDeviceManageSVC.loadChildInfo", data);
    	 
         // 用户资料
         IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
         IData userInfo = new DataMap(data.getString("USER_INFO", ""));
         setCustInfo(custInfo);
         setUserInfo(userInfo);
         setAjax(resultData);
    }
    
    /**
     * 业务提交
     * 
     * @author yuyj3
     * @param clcle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        //前台只能开实体卡
        data.put("AUX_TYPE", "2");
        
        // 操作类型：01-业务添加 02-业务删除 04-业务暂停 05-业务恢复
        data.put("OPER_CODE", "01");
        
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        
        IDataset result = CSViewCall.call(this, "SS.SingleNumMultiDeviceManageRegSVC.tradeReg", data);
        setAjax(result);
    }

    /**
     * 资源释放
     * 
     * @author yuyj3
     * @param clcle
     * @throws Exception
     */
    public void releaseSingleRes(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.releaseSingleRes", data);
        setAjax(dataset);
    }


    public abstract void setCustInfo(IData custInfo);
    public abstract void setUserInfo(IData userInfo);
    public abstract void setEditInfo(IData editInfo);

    public abstract void setReturnInfo(IData returnInfo);
}
