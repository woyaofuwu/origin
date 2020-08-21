package com.asiainfo.veris.crm.order.soa.group.grpcautionfee;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class GrpCautionFeeReqData extends GroupReqData {
	
	private String auditOrder;
	private String depositFee;
	private String depositType;
	private String acctDepositFee;
	
	public String getAuditOrder() 
	{
		return auditOrder;
	}

	public void setAuditOrder(String auditOrder) 
	{
		this.auditOrder = auditOrder;
	}

	public String getDepositFee() 
	{
		return depositFee;
	}

	public void setDepositFee(String depositFee) 
	{
		this.depositFee = depositFee;
	}

	public String getDepositType() 
	{
		return depositType;
	}

	public void setDepositType(String depositType) 
	{
		this.depositType = depositType;
	}

	public String getAcctDepositFee() {
		return acctDepositFee;
	}

	public void setAcctDepositFee(String acctDepositFee) {
		this.acctDepositFee = acctDepositFee;
	}
	
}
