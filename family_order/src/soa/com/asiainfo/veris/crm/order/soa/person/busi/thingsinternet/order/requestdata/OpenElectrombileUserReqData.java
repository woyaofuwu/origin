package com.asiainfo.veris.crm.order.soa.person.busi.thingsinternet.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class OpenElectrombileUserReqData extends BaseReqData{

	private String operType;//操作类型
	private String isPayFee;//是否主号付费
	private String serviceId;//平台服务编码
	private String discntCode;//优惠编码
	private UcaData mainUca;//主号码
	
	public String getOperType() {
		return operType;
	}
	public void setOperType(String operType) {
		this.operType = operType;
	}
	public String getIsPayFee() {
		return isPayFee;
	}
	public void setIsPayFee(String isPayFee) {
		this.isPayFee = isPayFee;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getDiscntCode() {
		return discntCode;
	}
	public void setDiscntCode(String discntCode) {
		this.discntCode = discntCode;
	}
	public UcaData getMainUca() {
		return mainUca;
	}
	public void setMainUca(UcaData mainUca) {
		this.mainUca = mainUca;
	}
}
