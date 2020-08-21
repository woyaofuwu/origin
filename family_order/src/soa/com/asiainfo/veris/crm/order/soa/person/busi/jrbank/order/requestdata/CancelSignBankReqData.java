package com.asiainfo.veris.crm.order.soa.person.busi.jrbank.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class CancelSignBankReqData extends BaseReqData {
	
	private String selectValues;
	private String isIntf;

	public String getSelectValues() {
		return selectValues;
	}

	public void setSelectValues(String selectValues) {
		this.selectValues = selectValues;
	}

	public String getIsIntf() {
		return isIntf;
	}

	public void setIsIntf(String isIntf) {
		this.isIntf = isIntf;
	}
	
}
