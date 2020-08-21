package com.asiainfo.veris.crm.iorder.web.igroup.esop.voicelinemanage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class VoicelineAnnualReview extends CSBasePage{

	public void queryByCondition(IRequestCycle cycle) throws Exception{
		IData data= getData();
		data.put("GROUP_NAME", data.getString("CUST_NAME",""));
		IDataset infos= CSViewCall.call(this, "SS.BusiCheckVoiceSVC.queryVoicelineAnnualInfo", data);
		setInfos(infos);
		setCondition(data);
		setTotalCount(infos.size());
	 }
	
	public void submitInfos(IRequestCycle cycle) throws Exception{
		IData data=getData();
		data.put("ORDER_ID", data.getString("parr_ORDER_ID",""));
		data.put("GROUP_ID", data.getString("parr_GROUP_ID",""));
		data.put("ANNUAL_REVIEW_DATE", data.getString("AUTH_DATE",""));
		data.put("VIOLATION_RECORD", data.getString("VIOLATION_RECORDS",""));
		
		data.put("IN_STAFF_ID",getVisit().getStaffId());
		data.put("IN_DEPART_ID", getVisit().getDepartId());
		IData result= CSViewCall.callone(this, "SS.BusiCheckVoiceSVC.submitAnnualReviewInfo", data);
				
		setAnnualReviewInfo(data);
		//redirectToMsg("提交成功");
	 }
	
	
	public abstract void setRowIndex(int rowIndex);
	public abstract void setTotalCount(long totalCount);
	public abstract void setInfo(IData info);
	public abstract void setInfos(IDataset infos);
	public abstract void setGroupInfo(IData groupInfo);
	public abstract void setProductInfo(IData productInfo);
	public abstract void setCustInfo(IData custInfo);
	public abstract void setUserInfo(IData suerInfo);
	public abstract void setOrderInfo(IData orderInfo);
	public abstract void setAnnualReviewInfo(IData annualReviewInfo);
	public abstract void setCondition(IData condition);	
}
