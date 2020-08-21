
package com.asiainfo.veris.crm.order.soa.person.busi.remotewritecardtrade.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class RemoteWriteCardRequestData extends BaseReqData
{
    private String simCardNo; // sim卡号

    // 资源其他信息

    private String imsi;
    
    private String emptyCardId;// 白卡id

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