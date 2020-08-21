package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remoteresetpswd.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class RemoteResetPswdRequestData  extends BaseReqData{
	private String serialNumber;
	private String custName;
	private String psptId;
	private String custTypeCode;
	private String transactionId;
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getPsptId() {
		return psptId;
	}
	public void setPsptId(String psptId) {
		this.psptId = psptId;
	}
	public String getCustTypeCode() {
		return custTypeCode;
	}
	public void setCustTypeCode(String custTypeCode) {
		this.custTypeCode = custTypeCode;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
}
