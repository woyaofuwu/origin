package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
 
public class NoPhoneModemLoseReqData extends BaseReqData
{
	private String instId; //Inst_id
	
	private String modermId;//光猫串号

	public String getInstId() {
		return instId;
	}

	public void setInstId(String instId) {
		this.instId = instId;
	}

	public String getModermId() {
		return modermId;
	}

	public void setModermId(String modermId) {
		this.modermId = modermId;
	}
	
	
}
