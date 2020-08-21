package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
 
public class NoPhoneModemChangeReqData extends BaseReqData
{
	private String oldModermId; // 光猫串号
	
	private String newModermId;//新光猫串号
	
	private String newModermType;//新光猫型号
	
	private String instId;//inst_id

	public String getOldModermId() {
		return oldModermId;
	}

	public void setOldModermId(String oldModermId) {
		this.oldModermId = oldModermId;
	}

	public String getNewModermId() {
		return newModermId;
	}

	public void setNewModermId(String newModermId) {
		this.newModermId = newModermId;
	}

	public String getNewModermType() {
		return newModermType;
	}

	public void setNewModermType(String newModermType) {
		this.newModermType = newModermType;
	}

	public String getInstId() {
		return instId;
	}

	public void setInstId(String instId) {
		this.instId = instId;
	}

	
}
