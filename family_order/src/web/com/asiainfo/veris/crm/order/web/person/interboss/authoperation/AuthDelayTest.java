package com.asiainfo.veris.crm.order.web.person.interboss.authoperation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class AuthDelayTest extends PersonBasePage{
	
	public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IData param = new DataMap();
        param.put("ID_VALUE", pageData.getString("ID_VALUE"));
        param.put("IDENT_CODE", pageData.getString("IDENT_CODE"));
        param.put("EFFECTIVE_TIME", pageData.getString("EFFECTIVE_TIME"));
        IData data = CSViewCall.callone(this, "SS.AuthDelayTestSVC.authDelay", param);
        setInfo(data);
        setAjax(data);
    }
	
	public abstract void setInfo(IData info);
}
