package com.asiainfo.veris.crm.order.soa.person.busi.jrbank.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class CreateSignBankReqData extends BaseReqData {
	
	private String payType;
	private String recvBank;
	private String userAcct;
	private String rechThreshold;
	private String rechAmount;
	private String isIntf;
	
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getRecvBank() {
		return recvBank;
	}
	public void setRecvBank(String recvBank) {
		this.recvBank = recvBank;
	}
	public String getUserAcct() {
		return userAcct;
	}
	public void setUserAcct(String userAcct) {
		this.userAcct = userAcct;
	}
	public String getRechThreshold() {
		return rechThreshold;
	}
	public void setRechThreshold(String rechThreshold) {
		this.rechThreshold = rechThreshold;
	}
	public String getRechAmount() {
		return rechAmount;
	}
	public void setRechAmount(String rechAmount) {
		this.rechAmount = rechAmount;
	}
	public String getIsIntf() {
		return isIntf;
	}
	public void setIsIntf(String isIntf) {
		this.isIntf = isIntf;
	}
	
	
}
