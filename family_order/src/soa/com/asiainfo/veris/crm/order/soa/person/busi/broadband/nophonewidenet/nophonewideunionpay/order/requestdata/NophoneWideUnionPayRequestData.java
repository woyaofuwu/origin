package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideunionpay.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class NophoneWideUnionPayRequestData extends BaseReqData{
	private String paySerialNumber;
	private String payUserId;
	private String payAcctId;
	public String getPaySerialNumber() {
		return paySerialNumber;
	}
	public void setPaySerialNumber(String paySerialNumber) {
		this.paySerialNumber = paySerialNumber;
	}
	public String getPayUserId() {
		return payUserId;
	}
	public void setPayUserId(String payUserId) {
		this.payUserId = payUserId;
	}
	public String getPayAcctId() {
		return payAcctId;
	}
	public void setPayAcctId(String payAcctId) {
		this.payAcctId = payAcctId;
	}
	
	
	
}
