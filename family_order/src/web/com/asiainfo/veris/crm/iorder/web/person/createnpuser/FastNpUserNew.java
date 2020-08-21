
package com.asiainfo.veris.crm.iorder.web.person.createnpuser;


import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FastNpUserNew extends PersonBasePage
{
	    
	    /**
	     * 查询后设置页面信息
	     */
	    public void loadChildInfo(IRequestCycle cycle) throws Exception
	    {
	    	IData data = getData();
	        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
	        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
	        IData result = CSViewCall.call(this, "SS.FastNpUserNewSVC.checkSnNPInfo", userInfo).getData(0);
	        setCustInfoView(custInfo); 
	        setUserInfoView(userInfo);
	        setAjax(result);
	    }
	    
	    public void onTradeSubmit(IRequestCycle cycle) throws Exception {
			IData pageData = getData();
			pageData.put("SERIAL_NUMBER",pageData.getString("AUTH_SERIAL_NUMBER"));
			CSViewCall.call(this, "SS.FastNpUserNewSVC.insUserOther", pageData).getData(0);
		}
	    
	    public abstract void setCustInfoView(IData custInfo);
	    public abstract void setUserInfoView(IData custInfo);
}
