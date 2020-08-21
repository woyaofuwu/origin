package com.asiainfo.veris.crm.order.soa.group.famnumsyn;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class XfkFamNumSynReqData extends MemberReqData {
	private String operCode;
	private String fumNub;

	public String getOperCode() {
		return operCode;
	}

	public void setOperCode(String operCode) {
		this.operCode = operCode;
	}

	public String getFumNub() {
		return fumNub;
	}

	public void setFumNub(String fumNub) {
		this.fumNub = fumNub;
	}

}
