package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.personlogin;


import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;



import com.ailk.biz.BizConstants;
//import com.ailk.biz.BizConstants;
import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * @author yangxd3
 * @date 2015-11-12 下午4:08:27
 */
public abstract class Login extends BizTempComponent
{
	public abstract void setDisplay(boolean display);

    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
    	boolean isAgent = false;
		IDataInput input = new DataInput();
	    input.getHead().putAll(this.getVisit().getAll());
	    input.getData().put("STAFF_ID", this.getVisit().getStaffId());
	    input.getData().put("DEPART_ID", this.getVisit().getDepartId());
    	IDataOutput agentResult = ServiceFactory.call("CS.LoginSVC.isAgentStaff", input);
    	isAgent = agentResult.getData().getData(0).getBoolean("IS_AGENT");
    	if("1".equals(this.getVisit().getInModeCode()) || isAgent){
    		this.setDisplay(false);
    		return;
    	}else{
    		this.setDisplay(true);
    	}
    	
        IData param = getPage().getData();
      
        String loginNum = param.getString("LOGIN_NUM");
        String action = param.getString("ACTION");
        if (StringUtils.isBlank(action))
        {
            getPage().addResBeforeBodyEnd("frame/login/person/Login.js");
        }
        if (StringUtils.equals("sendVerifyCode", action))
        {
            param.put("SERIAL_NUMBER", loginNum);
            input.getData().putAll(param);
            String loginTypeCode = input.getData().getString("LOGIN_TYPE_CODE");
            if("7".equals(loginTypeCode)){
            	input.getData().put("SUBSYS_CODE", "CSM");
            	input.getData().put("PARAM_ATTR", "1420");
            	input.getData().put("PARAM_CODE", "COMMON_1420");
            	input.getData().put("EPARCHY_CODE", this.getVisit().getLoginEparchyCode());
            	
            	ServiceFactory.call("CS.AuthCheckSVC.sendSmsVerifyCodeCommon", input);
            }else {
            	ServiceFactory.call("CS.AuthCheckSVC.sendSmsVerifyCode", input);
            }            
           
        }
        else if (StringUtils.equals("logout", action))
        {
//            getVisit().set(BizConstants.VERIFY_ACCESS_NUM,"");
//            if (!StringUtils.equals("1", getVisit().get(BizConstants.AUTH_TRACE)))
//            {
//                getVisit().set(BizConstants.AUTH_TRACE,"");
//            }            
        }
        else if (StringUtils.equals("login", action))
        {
            IData loginData = new DataMap();
            String loginTypeCode = param.getString("LOGIN_TYPE_CODE");
            // AUTH_TRACE：0-权限认证 1-不能使用权限 2-非权限认证 为空-未认证。 注销时，非1都清空；
            String authTrace ="";// getVisit().get(BizConstants.AUTH_TRACE);
            param.put("AUTH_TRACE", authTrace);
            if (StringUtils.startsWith(loginTypeCode, "W"))
            {
                param.put("SERIAL_NUMBER", loginNum);
                input.getData().putAll(param);
                IDataOutput output = ServiceFactory.call("CS.LoginSVC.getBroadbandLoginInfo", input);
                loginData = output.getData().first();
            }
            else
            {
                param.put("SERIAL_NUMBER", loginNum);
                input.getData().putAll(param);
                IDataOutput output =ServiceFactory.call("CS.LoginSVC.getLoginInfo", input);
                loginData =  output.getData().first();
            }
            String removeTag = loginData.getString("REMOVE_TAG");
            if (StringUtils.equals("0", removeTag) || StringUtils.startsWith(loginTypeCode, "W"))
            {
//                this.setShopCartCount(param, loginData);
            }
            else
            {
                loginData.put("CART_NUM", "0");
            }
//            this.setLoginAccessNum(loginTypeCode, loginNum, loginData);
            getPage().setAjax(loginData);
        }else if(StringUtils.equals("loginExtend", action)){
        	this.setRenderContent(false); //不刷新
            input.getData().putAll(param);
            IDataOutput output = ServiceFactory.call("CS.LoginSVC.getLoginExtendInfo", input);
            IData loginData = output.getData().first();
            
            getPage().setAjax(loginData);
        }
    }

    /**
     * @author yangxd3
     * @description ： 设置客户购物车订单数量
     * 
     * @date 2016年1月19日 上午11:53:02
     */
    /*private void setShopCartCount(IData param, IData loginData) throws Exception
    {
    	IDataInput input = new DataInput();
        String accessNum = param.getString("LOGIN_NUM");
        String loginTypeCode = param.getString("LOGIN_TYPE_CODE");
        if (StringUtils.startsWith(loginTypeCode, "W"))
        {
            accessNum = loginData.getDataset("SUB_INFO").first().getString("WIDE_NET_ACCESS_NUM");
        }
        IData paramData = new DataMap();
        paramData.put("ACCESS_NUM", accessNum);
        input.getData().putAll(param);
        IDataOutput output =ServiceFactory.call("OC.person.IShopCartQuerySV.queryShopCartByAccessNum", input);
        IData retData =  output.getData().first();
        loginData.put("CART_NUM", retData.getDataset("COMM_DATA").first().getString("COUNT"));
    }*/

    /**
     * @description ：设置认证通过的号码，受理进行比对
     * @param loginTypeCode
     * @param loginNum
     * @param loginData
     * @throws Exception
     * @return void
     * 
     * @author yangxd3
     * @date 2016年4月12日 下午10:17:02
     */
    private void setLoginAccessNum(String loginTypeCode, String loginNum, IData loginData) throws Exception
    {
        if (StringUtils.equals("0", loginData.getDataset("VERIFY_RESULT").first().getString("RESULT_CODE")))
        {
            String accessNum = loginNum;
            if (StringUtils.startsWith(loginTypeCode, "W"))
            {
                accessNum = loginData.getDataset("SUB_INFO").first().getString("WIDE_NET_ACCESS_NUM");
            }
//            getVisit().set(BizConstants.VERIFY_ACCESS_NUM,accessNum);
//            log.debug("pub.component.login CHECK_VISIT" + getVisit().getAll().toString());
//            String authTrace = loginData.getDataset("VERIFY_RESULT").first().getString("AUTH_TRACE");
//            if (StringUtils.equals("0", authTrace) || StringUtils.equals("2", authTrace))
//            {
//                getVisit().set(BizConstants.AUTH_TRACE,authTrace);
//            }
        }
    }
}
