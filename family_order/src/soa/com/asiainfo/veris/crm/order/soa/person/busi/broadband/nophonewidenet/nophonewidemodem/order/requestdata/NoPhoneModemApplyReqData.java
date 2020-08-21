
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
 

public class NoPhoneModemApplyReqData extends BaseReqData
{
	private String apply_type; // 申领模式
	
	private String deposit;//押金
	
	private String modermId;//光猫串号
	
	private String modermType;//光猫型号
	
	private String returnDate;//预计归还时间
	
	private String depositTradeId;//BOSS押金转移流水

	public String getApply_type() {
		return apply_type;
	}

	public void setApply_type(String applyType) {
		apply_type = applyType;
	}

	public String getDeposit() {
		return deposit;
	}

	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}

	public String getModermId() {
		return modermId;
	}

	public void setModermId(String modermId) {
		this.modermId = modermId;
	}

	public String getModermType() {
		return modermType;
	}

	public void setModermType(String modermType) {
		this.modermType = modermType;
	}

	public String getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}

	public String getDepositTradeId() {
		return depositTradeId;
	}

	public void setDepositTradeId(String depositTradeId) {
		this.depositTradeId = depositTradeId;
	}
	
	
}
