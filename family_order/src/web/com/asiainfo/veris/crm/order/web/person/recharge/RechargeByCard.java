
package com.asiainfo.veris.crm.order.web.person.recharge;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RechargeByCard extends PersonBasePage
{

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        // {"CARD_PASSWORD":"21312312312312","CHECK_MODE":"F","SUBMIT_TYPE":"submit","SERIAL_NUMBER":"13976297577","page":"recharge.RechargeByCard","service":"ajax","listener":"onTradeSubmit"}
        IData pageData = getData();

        pageData.put("IN_MODE_CODE", "0");
        pageData.put("TRADE_EPARCHY_CODE", this.getTradeEparchyCode());
        pageData.put("TRADE_CITY_CODE", this.getVisit().getCityCode());
        pageData.put("TRADE_DEPART_ID", this.getVisit().getDepartId());
        pageData.put("TRADE_STAFF_ID", this.getVisit().getStaffId());
        pageData.put("X_CALL_EDMPHONECODE", pageData.getString("SERIAL_NUMBER"));
        pageData.put("CARD_PASSWD", pageData.getString("CARD_PASSWORD"));

        IDataset rtDataset = CSViewCall.call(this, "SS.ReceiveMobileRechargeSVC.receiveMobileRecharge", pageData);
	    
        IData retData = new DataMap();
        if (IDataUtil.isNotEmpty(rtDataset))
	    {
	    	IData data = rtDataset.getData(0);
	    	String retCode = data.getString("X_RESULTCODE");
	    	String retInfo = data.getString("X_RESULTINFO");
	    	if (StringUtils.equals(retCode, "2005") || StringUtils.equals(retCode, "1001"))
	    	{
	    		retInfo = "充值卡密码错误";
			}
	    	retData.put("RESULT_CODE", retCode);
	    	retData.put("RESULT_INFO", retInfo);
		} 
	    else
	    {
	    	retData.put("RESULT_CODE", "-1");
	    	retData.put("RESULT_INFO", "未知错误");
	    }
	    this.setAjax(retData);
    }

    public abstract void setCommInfo(IData commInfo);

}
