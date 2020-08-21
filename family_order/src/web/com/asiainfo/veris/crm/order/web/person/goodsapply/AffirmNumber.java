
package com.asiainfo.veris.crm.order.web.person.goodsapply;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData; 
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class AffirmNumber extends PersonBasePage
{ 
	public void initHidden(IRequestCycle cycle) throws Exception{
		IData data=getData();
		setInfo(data);
	}
    /**
	 * 查询用户礼品信息
	 * */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    { 
    	IData data=getData(); 
        IDataset result = CSViewCall.call(this, "SS.GoodsApplySVC.resendSMSforGoods", data); 
         
        setAjax(result.getData(0));
    } 
    public abstract void setInfo(IData info); 
    public abstract void setRtnInfo(IData rtnInfo); 
}
