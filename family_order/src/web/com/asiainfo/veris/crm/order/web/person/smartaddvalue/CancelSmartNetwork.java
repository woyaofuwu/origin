package com.asiainfo.veris.crm.order.web.person.smartaddvalue;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CancelSmartNetwork extends PersonBasePage {

	/**
     * 判断是否免认证，有权限无需认证
     * @param cycle
     * @throws Exception
     */
    public void isNeedAuth(IRequestCycle cycle) throws Exception
    {    	 
    	 boolean isNeedAuth = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS009");
    	 IData authData = new DataMap();
    	 authData.put("IS_NEED_AUTH", !isNeedAuth ? "YES" : "NO");
    	 setAjax(authData);
    	 
    }
    
   /* public void queryAcctType(IRequestCycle cycle) throws Exception
    {
        IData input = this.getData();
        IDataset dataset = CSViewCall.call(this, "SS.CancelSmartNetworkRegSVC.queryAcctType", input);
        setAjax(dataset);
    }   */ 
    
	/**
     * 认证校验
     * 
     * @param data
     * @throws Exception
     */
    public void authCheck(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        IData authData = new DataMap();
        authData.put("RESULT_CODE", "0");
        data.remove("ACTION");
        IData params = new DataMap();
        params.putAll(data);

        // 除客服，营业厅接入方做校验

        String inModeCode = getVisit().getInModeCode();
        if ("0".equals(inModeCode) || "1".equals(inModeCode) || "3".equals(inModeCode))
        {
        	if(data.getString("SERIAL_NUMBER") == null || "".equals(data.getString("SERIAL_NUMBER","")))
        	{
        		CSViewException.apperr(CrmUserException.CRM_USER_373);
        	}
        	
        	IDataset userInfo = CSViewCall.call(this, "CS.UserInfoQrySVC.getNormalUserInfoBySN", params);
        	
        	if(IDataUtil.isEmpty(userInfo))
            {
        		CSViewException.apperr(CrmUserException.CRM_USER_389, data.getString("SERIAL_NUMBER"));
            }
        	
        	params.put("USER_ID", userInfo.getData(0).getString("USER_ID"));
        	
            boolean rightFlag = false;
            
            //更新时放开
            if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS009") && !"347".equals(data.getString("TRADE_TYPE_CODE")) && !"343".equals(data.getString("TRADE_TYPE_CODE")))
            {
                rightFlag = true;
            }
            // 统一认证密码校验
            if (!rightFlag )
            {
            	 
            	
                String svcName = "CS.AuthCheckSVC.authCheck";
                String authType = data.getString("AUTH_TYPE", "0");
                if (authType.equals("04"))
                {
                    params.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
                    svcName = "CS.AuthCheckSVC.authBroadbandCheck";
                }
                IDataset outputData = CSViewCall.call(this, svcName, params);
                if (null != outputData && outputData.size() > 0)
                {
                    authData = outputData.getData(0);
                }
            }
        }
        authData.put("CHECK_MODE", data.getString("CHECK_MODE"));
        setAjax(authData);
    }    
    
    public void submitCancelTrade(IRequestCycle cycle) throws Exception
    {
        IData input = this.getData();
        IDataset dataset = CSViewCall.call(this, "SS.CancelSmartNetworkRegSVC.cancelTradeReg", input);
        setAjax(dataset);
    }
    
    public void  qrySecondLevelTypeInfo(IRequestCycle cycle)throws Exception{
          //获取 撤单原因  编码
          String  pdataId=this.getData().getString("CANCEL_REASON");          
          IDataset iDataset=StaticUtil.getStaticListByParent("WIDE_CANCEL_REASON_RELATION", pdataId);
          
          setCancelReasonSecondLevel(iDataset);
    }
    
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        data.put("SUBSYS_CODE", "CSM");
        data.put("PARAM_ATTR", "9905");
        data.put("PARAM_CODE", "1");
        data.put("EPARCHY_CODE", getTradeEparchyCode());
        IDataset cancelTradeTypes = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommpara", data);
        setCancelTradeTypeList(cancelTradeTypes);
    }

    public void queryTradeBackFee(IRequestCycle cycle) throws Exception
    {
        IData input = this.getData();
        IDataset dataset = CSViewCall.call(this, "SS.CancelSmartNetworkRegSVC.queryTradeBackFee", input);
        setAjax(dataset);
    }

    public abstract void setCancelTradeTypeList(IDataset list);
    
    public abstract void setCancelReasonSecondLevel(IDataset dataset);
}
