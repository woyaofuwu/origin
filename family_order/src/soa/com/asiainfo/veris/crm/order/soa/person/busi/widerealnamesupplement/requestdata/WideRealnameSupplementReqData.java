package com.asiainfo.veris.crm.order.soa.person.busi.widerealnamesupplement.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;


public class WideRealnameSupplementReqData extends BaseReqData
{
	private String IsRealName;//实名制

    private String serialNumber;// 手机号码
    
    private String custName;// 客户名称

    private String psptId;// 证件号码

    private String psptAddr;// 证件地址

    private String psptTypeCode;// 证件类型

    private String agentCustName;// 经办人名称

    private String agentPsptTypeCode;// 经办人证件类型

    private String agentPsptId;// 经办人证件号码

    private String agentPsptAddr;// 经办人证件地址
    
    private String RSRV_STR2;// 责任人姓名
    
    private String RSRV_STR3;// 责任人证件类型
    
    private String RSRV_STR4;// 责任人证件号码
    
    private String RSRV_STR5;// 责任人证件地址

    
    public void setIsRealName(String IsRealName) {
		this.IsRealName = IsRealName;
	}
	
	public String getIsRealName() {
		return IsRealName;
	}
    
	public void setserialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	public String getserialNumber() {
		return serialNumber;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getPsptId() {
		return psptId;
	}

	public void setPsptId(String psptId) {
		this.psptId = psptId;
	}

	public String getPsptAddr() {
		return psptAddr;
	}

	public void setPsptAddr(String psptAddr) {
		this.psptAddr = psptAddr;
	}

	public String getPsptTypeCode() {
		return psptTypeCode;
	}

	public void setPsptTypeCode(String psptTypeCode) {
		this.psptTypeCode = psptTypeCode;
	}

	public String getAgentCustName() {
		return agentCustName;
	}

	public void setAgentCustName(String agentCustName) {
		this.agentCustName = agentCustName;
	}

	public String getAgentPsptTypeCode() {
		return agentPsptTypeCode;
	}

	public void setAgentPsptTypeCode(String agentPsptTypeCode) {
		this.agentPsptTypeCode = agentPsptTypeCode;
	}

	public String getAgentPsptId() {
		return agentPsptId;
	}

	public void setAgentPsptId(String agentPsptId) {
		this.agentPsptId = agentPsptId;
	}

	public String getAgentPsptAddr() {
		return agentPsptAddr;
	}

	public void setAgentPsptAddr(String agentPsptAddr) {
		this.agentPsptAddr = agentPsptAddr;
	}

	public String getRSRV_STR2() {
		return RSRV_STR2;
	}

	public void setRSRV_STR2(String RSRV_STR2) {
		this.RSRV_STR2 = RSRV_STR2;
	}

	public String getRSRV_STR3() {
		return RSRV_STR3;
	}

	public void setRSRV_STR3(String RSRV_STR3) {
		this.RSRV_STR3 = RSRV_STR3;
	}

	public String getRSRV_STR4() {
		return RSRV_STR4;
	}

	public void setRSRV_STR4(String RSRV_STR4) {
		this.RSRV_STR4 = RSRV_STR4;
	}

	public String getRSRV_STR5() {
		return RSRV_STR5;
	}

	public void setRSRV_STR5(String RSRV_STR5) {
		this.RSRV_STR5 = RSRV_STR5;
	}
}