package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.wideprereg.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class WidePreRegRequestData extends BaseReqData{
	
	private String cust_name; //联系人姓名
	
	private String contact_sn; //联系人电话
	
	private String wbbw; //宽带带宽
	
	private String pre_cause;//预装原因
	
	private String area_code;//地区
	
	private String set_addr;//安装地址
	
	private String reg_status;//登记状态
	
	private String addr_code;//五级地址编码，每一级地址之间用 "," 分隔
	
	private String home_addr;//五级地址
	

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String custName) {
		cust_name = custName;
	}

	public String getContact_sn() {
		return contact_sn;
	}

	public void setContact_sn(String contactSn) {
		contact_sn = contactSn;
	}

	public String getWbbw() {
		return wbbw;
	}

	public void setWbbw(String wbbw) {
		this.wbbw = wbbw;
	}

	public String getPre_cause() {
		return pre_cause;
	}

	public void setPre_cause(String preCause) {
		pre_cause = preCause;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String areaCode) {
		area_code = areaCode;
	}

	public String getSet_addr() {
		return set_addr;
	}

	public void setSet_addr(String setAddr) {
		set_addr = setAddr;
	}

	public String getReg_status() {
		return reg_status;
	}

	public void setReg_status(String regStatus) {
		reg_status = regStatus;
	}

	

	public String getAddr_code() {
		return addr_code;
	}

	public void setAddr_code(String addrCode) {
		addr_code = addrCode;
	}

	public String getHome_addr() {
		return home_addr;
	}

	public void setHome_addr(String homeAddr) {
		home_addr = homeAddr;
	}

}
