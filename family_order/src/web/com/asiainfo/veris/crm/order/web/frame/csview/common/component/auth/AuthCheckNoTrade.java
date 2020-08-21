package com.asiainfo.veris.crm.order.web.frame.csview.common.component.auth;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeTypeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 无业务类型权限认证
 * @author zyc
 *
 */
public abstract class AuthCheckNoTrade extends PersonBasePage{
	
	public abstract IDataset getCheckMode();
    public abstract void setCheckMode(IDataset checkMode);
    public abstract void setInfo(IData info);
    
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
    
    /**
     * 认证弹框初始化
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        String identity = data.getString("IDENTITY_CHECK_TAG");
        String tmp = "";
        String noUserPassWd = data.getString("NO_USER_PASSWD", "false");
        IDataset checkMode = new DatasetList();

        if (getVisit().getStaffId().startsWith("HNYD"))
        {
            // 海南异地用户只有服务密码认证方式
            IData temp = new DataMap();
            temp.put("key", "1");
            temp.put("value", "服务密码");
            checkMode.add(0, temp);
        }
        else if (identity.length() > 4)
        {
            tmp = identity.substring(0, 1);

            if ("1".equals(tmp))
            {
                IData temp = new DataMap();
                temp.put("key", "0");
                temp.put("value", "客户证件");
                checkMode.add(temp);
            }

            tmp = identity.substring(1, 2);

            if ("1".equals(tmp) && !"true".equals(noUserPassWd))
            {
                IData temp = new DataMap();
                temp.put("key", "1");
                temp.put("value", "服务密码");
                checkMode.add(0, temp);
            }

            tmp = identity.substring(2, 3);
            if ("1".equals(tmp))
            {
                IData temp = new DataMap();
                temp.put("key", "2");
                temp.put("value", "SIM卡号(或白卡号)+密码");
                checkMode.add(temp);
            }

            tmp = identity.substring(3, 4);
            if ("1".equals(tmp))
            {
                IData temp = new DataMap();
                temp.put("key", "3");
                temp.put("value", "服务号码+证件");
                checkMode.add(temp);
            }

            tmp = identity.substring(4, 5);
            if ("1".equals(tmp))
            {
                IData temp = new DataMap();
                temp.put("key", "4");
                temp.put("value", "证件号码+服务密码");
                checkMode.add(temp);
            }
            
            if (identity.length() > 5){
	            tmp = identity.substring(5, 6);
				if ("1".equals(tmp)){
					IData temp = new DataMap();
					temp.put("key", "5");
					temp.put("value", "SIM卡号(或白卡号)+验证码");
					checkMode.add(temp);
				}
            }
            
            if (identity.length() > 6){
	            tmp = identity.substring(6, 7);
				if ("1".equals(tmp)){
					IData temp = new DataMap();
					temp.put("key", "6");
					temp.put("value", "服务密码+验证码");
					checkMode.add(temp);
				}
            }
        }
        this.setCheckMode(checkMode);
    
        if (checkMode.size() == 0)
        {
            CSViewException.apperr(TradeTypeException.CRM_TRADETYPE_2);
        }

    }
     
    
    public void sendVerifyCode(IRequestCycle cycle) throws Exception{
    	IData data = getData();
    	IDataset infos = CSViewCall.call(this, "CS.AuthCheckSVC.sendSmsVerifyCode", data);
    	setAjax(infos.getData(0));
    }

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
            //if (!rightFlag && !"true".equals(data.getString("IVR_PASS_SUCC")) && !"true".equals(data.getString("DISABLED_AUTH")))
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
}
