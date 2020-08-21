package com.asiainfo.veris.crm.order.soa.person.busi.interroamday.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class InterRoamDayReqData extends BaseReqData{
	
	 private String userType;

	 public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getProvCode() {
		return provCode;
	}

	public void setProvCode(String provCode) {
		this.provCode = provCode;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getProdInsId() {
		return prodInsId;
	}

	public void setProdInsId(String prodInsId) {
		this.prodInsId = prodInsId;
	}

	public String getProdType() {
		return prodType;
	}

	public void setProdType(String prodType) {
		this.prodType = prodType;
	}

	public String getProdId() {
		return prodId;
	}

	public void setProdId(String prodId) {
		this.prodId = prodId;
	}

	public String getProdStat() {
		return prodStat;
	}

	public void setProdStat(String prodStat) {
		this.prodStat = prodStat;
	}

	public String getValidDate() {
		return validDate;
	}

	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}

	public String getFirstTime() {
		return firstTime;
	}

	public void setFirstTime(String firstTime) {
		this.firstTime = firstTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getRelation_stat() {
		return relation_stat;
	}

	public void setRelation_stat(String relation_stat) {
		this.relation_stat = relation_stat;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getFeeCycle() {
		return feeCycle;
	}

	public void setFeeCycle(String feeCycle) {
		this.feeCycle = feeCycle;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public List getParaInfo() {
		return paraInfo;
	}

	public void setParaInfo(List paraInfo) {
		this.paraInfo = paraInfo;
	}

	public List getParaValue() {
		return paraValue;
	}

	public void setParaValue(List paraValue) {
		this.paraValue = paraValue;
	}

	private String groupName;

	 private String provCode;
	 
	 private String updateTime;
	 
	 private String prodInsId;
	 
	 private String prodType;
	 
	 private String prodId;
	 
	 private String prodStat;
	 
	 private String validDate;
	 
	 private String expireDate;
	 
	 private String firstTime;
	 
	 private String endTime;
	 
	 private String relation_stat;
	 
	 private String feeType;
	 
	 private String feeCycle;
	 
	 private String fee;
	 
	 private String channel;
	 
	 private String activeInstId;
	 
	 public String getActiveInstId() {
		return activeInstId;
	}

	public void setActiveInstId(String activeInstId) {
		this.activeInstId = activeInstId;
	}

	private List paraInfo;
	 
	 private List paraValue;
	 
}
