package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class SingleNumMultiDeviceStatusChangeReqData extends BaseReqData {
	private String serialNumber;// 主号码
	
	private String oprFlag;// 1:前台暂停 2：前台恢复 3：欠费暂停 4：缴费恢复  5.其他停机：主动停机 6.其他开机：主动开机
	
	List<AuxDeviceData> auxDeviceDataList = new ArrayList<AuxDeviceData>();//副设备信息

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getOprFlag() {
		return oprFlag;
	}

	public void setOprFlag(String oprFlag) {
		this.oprFlag = oprFlag;
	}

	public List<AuxDeviceData> getAuxDeviceDataList() {
		return auxDeviceDataList;
	}

	public void setAuxDeviceDataList(List<AuxDeviceData> auxDeviceDataList) {
		this.auxDeviceDataList = auxDeviceDataList;
	}
	 
}