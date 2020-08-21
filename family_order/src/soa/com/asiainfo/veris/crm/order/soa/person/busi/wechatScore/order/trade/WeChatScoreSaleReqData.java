package com.asiainfo.veris.crm.order.soa.person.busi.wechatScore.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class WeChatScoreSaleReqData extends BaseReqData {
	
	private String scoreOrderId;

	private String addDate;

	private String addTime;

	private String msisdn;

	private String givePoint;

	private String periodOfValidity;

	private String activityId;
	
	private String activityTitle;

	private String remarks;

	
	
	
	public String getScoreOrderId() {
		return scoreOrderId;
	}

	public void setScoreOrderId(String scoreOrderId) {
		this.scoreOrderId = scoreOrderId;
	}

	public String getActivityTitle() {
		return activityTitle;
	}

	public void setActivityTitle(String activityTitle) {
		this.activityTitle = activityTitle;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getGivePoint() {
		return givePoint;
	}

	public void setGivePoint(String givePoint) {
		this.givePoint = givePoint;
	}

	public String getPeriodOfValidity() {
		return periodOfValidity;
	}

	public void setPeriodOfValidity(String periodOfValidity) {
		this.periodOfValidity = periodOfValidity;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
