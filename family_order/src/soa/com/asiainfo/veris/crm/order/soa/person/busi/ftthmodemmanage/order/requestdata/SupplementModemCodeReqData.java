
package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class SupplementModemCodeReqData extends BaseReqData
{
	private String instId; //Inst_id
	
	private String modemId;//光猫串号
	
	private String modemType;//光猫型号
	
	private String supplementType;//补录方式
	
	private String operType;//光猫补录类型 1：FTTH光猫补录 2：FTTH商务光猫补录

	public String getInstId() {
		return instId;
	}

	public void setInstId(String instId) {
		this.instId = instId;
	}

	public String getModemId() {
		return modemId;
	}

	public void setModemId(String modemId) {
		this.modemId = modemId;
	}

	public String getModemType() {
		return modemType;
	}

	public void setModemType(String modemType) {
		this.modemType = modemType;
	}

	public String getSupplementType() {
		return supplementType;
	}

	public void setSupplementType(String supplementType) {
		this.supplementType = supplementType;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}
	
	
}
