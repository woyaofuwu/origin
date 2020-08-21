package com.asiainfo.veris.crm.iorder.web.person.saleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;



public abstract class CancelWholeNetCreditPurchases extends PersonBasePage{
	 
	public void queryCancelInfo(IRequestCycle cycle) throws Exception{
		IData input=getData();
		String procTyp=input.getString("procTyp");
		
		IDataset cancelInfo=CSViewCall.call(this, "SS.CancelWholeNetCreditPurchasesSVC.getCancelWholeNetCreditPurchasesInfo", input);
		
		IData param=new DataMap();
		param.put("PROCTYP", procTyp);
		param.put("MPL_ORD_DT", cancelInfo.first().getString("MPL_ORD_DT",""));

		cancelInfo.add(param);
		setAjax(param);
		setResult(param);
		setInfos(cancelInfo);
	}
	
	public void cancelRequest(IRequestCycle cycle) throws Exception{
		IData input=getData();
		IDataset cancelRequestInfo=CSViewCall.call(this, "SS.CancelWholeNetCreditPurchasesSVC.getCancelRequestInfo", input);
		setInfos(cancelRequestInfo);
	}
	
	public abstract void setInfos(IDataset infos);
	public abstract void setResult(IData result);
}
