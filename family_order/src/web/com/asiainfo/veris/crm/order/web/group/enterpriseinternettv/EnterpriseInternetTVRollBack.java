
package com.asiainfo.veris.crm.order.web.group.enterpriseinternettv;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class EnterpriseInternetTVRollBack extends GroupBasePage
{

    public abstract void setUserInfo(IData userInfo);
    
    public abstract void setResInfo(IData resInfo);
    
    public abstract void setCondition(IData condition);
    
    
    public void loadPageInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData userInfo = new DataMap();
        IData custInfo = new DataMap();
        
     // 查询集团客户资料
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", data.getString("cond_SERIAL_NUMBER", ""));
        IDataset userInfos = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserInfoBySn", param);
        

        
        if(IDataUtil.isNotEmpty(userInfos))
        {
            userInfo = userInfos.getData(0);
            data.put("cond_USER_ID_A", userInfo.getString("USER_ID",""));
        }else
        {
            CSViewException.apperr(CrmUserException.CRM_USER_117,data.getString("cond_SERIAL_NUMBER", ""));
        }
        
        userInfo.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset custInfos = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryCustomerInfoByCustId", userInfo);
        if(IDataUtil.isNotEmpty(userInfos))
        {
            custInfo = custInfos.getData(0);
        }else
        {
            CSViewException.apperr(CrmUserException.CRM_USER_178,data.getString("cond_SERIAL_NUMBER", ""));
        }
        
        
        IDataset dataset = CSViewCall.call(this, "SS.EnterpriseInternetTVRollBackSVC.checkResOtherInfo", userInfo);
        if(IDataUtil.isNotEmpty(dataset)){
            IData retData = dataset.getData(0);
            
            retData.put("RES_ID", retData.getString("RSRV_STR3", ""));
            retData.put("RES_NO", retData.getString("RSRV_STR3", ""));
            retData.put("RES_TYPE_CODE", retData.getString("RSRV_STR16", ""));
            retData.put("RES_BRAND_CODE", retData.getString("RSRV_STR15", ""));
            retData.put("RES_BRAND_NAME", retData.getString("RSRV_STR5", ""));
            retData.put("RES_KIND_CODE", retData.getString("RSRV_STR17", ""));
            retData.put("RES_KIND_NAME", retData.getString("RSRV_STR6", ""));
            retData.put("RES_STATE_CODE", retData.getString("RSRV_STR18", ""));
            retData.put("RES_STATE_NAME", retData.getString("RSRV_STR9", ""));
            retData.put("RES_SUPPLY_COOPID", retData.getString("RSRV_STR2", ""));
            retData.put("RES_FEE", retData.getString("RSRV_STR7", ""));
            retData.put("ARTIFICIAL_SERVICES", retData.getString("RSRV_TAG1", ""));
            if("1".equals(retData.getString("RSRV_TAG1", "")))
            {
                retData.put("ARTIFICIAL_SERVICES_NAME", "是");
            }else
            {
                retData.put("ARTIFICIAL_SERVICES_NAME", "否");
            }
            
            retData.put("cond_SERIAL_NUMBER", data.getString("cond_SERIAL_NUMBER", ""));
            retData.put("cond_USER_ID_A", userInfo.getString("USER_ID",""));
            
            this.setAjax(retData);
            this.setResInfo(retData);
            this.setCondition(data);
        }else
        {
            CSViewException.apperr(CrmUserException.CRM_USER_783,"获取不到申领机顶盒信息！");
        }
        
    }
    
    /**
     * 提交方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("cond_SERIAL_NUMBER"));
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IDataset dataset = CSViewCall.call(this, "SS.EnterpriseInternetTVRollBackSVC.crtTrade", data);
        setAjax(dataset);
    }
    
}
