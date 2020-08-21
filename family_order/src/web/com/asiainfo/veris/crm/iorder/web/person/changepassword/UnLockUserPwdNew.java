
package com.asiainfo.veris.crm.iorder.web.person.changepassword;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UnLockUserPwdNew extends PersonBasePage
{
    /**
     * 页面初始化方法
     * 
     * @param cycle
     * @throws Exception
     */
	public void init(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
		setTradeTypeCode(data.getString("TRADE_TYPE_CODE"));
		setAuthType(data.getString("AUTH_TYPE", "00"));
        setIsUserPwdMix(data.getString("IS_USER_PWD_MIX"));
	}
    
	/**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData("PASSWORD", true);
        IData inparam = getData("AUTH", true);
        inparam.put("CHECK_MODE", getData().getString("CHECK_MODE"));// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱
        data.putAll(inparam);
        IDataset dataset = CSViewCall.call(this, "SS.UnLockUserPwdRegSVC.tradeReg", data);
        setAjax(dataset);
    }
    
    public abstract void setAuthType(String authType);

    public abstract void setTradeTypeCode(String tradeTypeCode);

    public abstract void setIsUserPwdMix(String isUserPwdMix);
}
