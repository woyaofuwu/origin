
package com.asiainfo.veris.crm.order.soa.person.busi.replacecard.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ReplaceCardReqData extends BaseReqData
{
	private String simCardNo;
	private String imsi;
	private String emptyCardId;
   
	public String getSimCardNo() {
		return simCardNo;
	}
	public void setSimCardNo(String simCardNo) {
		this.simCardNo = simCardNo;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getEmptyCardId() {
		return emptyCardId;
	}
	public void setEmptyCardId(String emptyCardId) {
		this.emptyCardId = emptyCardId;
	}
   
   
}
