package com.asiainfo.veris.crm.order.soa.person.busi.onepsptmultiUser.order.requestdata;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class OnePsptMultiUserRequestData extends BaseReqData{
	//用户号码
	private String SerialNumber;
	//证件号码
	private String psptId;
	//工单编号
	private String staffId;
	//证件类型
	private String psptTypeCode;
	//用户列
	private IDataset userList;
	//是否单位证件类型
	private String isUnitPsptType;
	
	public String getIsUnitPsptType() {
		return isUnitPsptType;
	}
	public void setIsUnitPsptType(String isUnitPsptType) {
		this.isUnitPsptType = isUnitPsptType;
	}
	public IDataset getUserList() {
		return userList;
	}
	public void setUserList(IDataset userList) {
		this.userList = userList;
	}
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	public String getPsptTypeCode() {
		return psptTypeCode;
	}
	public void setPsptTypeCode(String psptTypeCode) {
		this.psptTypeCode = psptTypeCode;
	}
	public String getSerialNumber() {
		return SerialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}
	public String getPsptId() {
		return psptId;
	}
	public void setPsptId(String psptId) {
		this.psptId = psptId;
	}
}
