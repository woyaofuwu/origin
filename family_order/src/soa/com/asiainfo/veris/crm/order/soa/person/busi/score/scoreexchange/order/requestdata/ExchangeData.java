package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.requestdata;

public class ExchangeData {

	private String ruleId;

	private String count;

	private String evalue;

	private String hbevalue;// 2017/6/9新增 ，为了不影响其他的接口，新增字段   REQ201703030013
	
	private String reqId;// 2017/6/9新增   请求业务流水号   REQ201703030013 接口

	private String actionId;// 2017/6/20 新增   活动编号   REQ201703030013 接口

	private String proId;// 2017/6/20 新增  券别编号   REQ201703030013 接口

	

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public String getCount() {
		return count;
	}

	public String getEvalue() {
		return evalue;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setEvalue(String evalue) {
		this.evalue = evalue;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getHbevalue() {
		return hbevalue;
	}

	public void setHbevalue(String hbevalue) {
		this.hbevalue = hbevalue;
	}

	
	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}
	
}
