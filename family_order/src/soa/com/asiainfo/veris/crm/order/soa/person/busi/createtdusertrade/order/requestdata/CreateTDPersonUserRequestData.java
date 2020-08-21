
package com.asiainfo.veris.crm.order.soa.person.busi.createtdusertrade.order.requestdata;

import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;

public class CreateTDPersonUserRequestData extends CreatePersonUserRequestData
{
	private String openType = "";//受理类型
	
    private String isTTtransfer;//是否铁通迁移固话,1是

	public String getOpenType() {
		return openType;
	}

	public void setOpenType(String openType) {
		this.openType = openType;
	}

	public void setIsTTtransfer(String isTTtransfer) {
		this.isTTtransfer = isTTtransfer;
	}

	public String getIsTTtransfer() {
		return isTTtransfer;
	}
}
