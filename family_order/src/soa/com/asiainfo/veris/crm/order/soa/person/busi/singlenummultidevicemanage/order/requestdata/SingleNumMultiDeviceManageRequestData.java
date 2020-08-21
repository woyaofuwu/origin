
package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultidevicemanage.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class SingleNumMultiDeviceManageRequestData extends BaseReqData
{
    /**
     * 副设备昵称
     */
    private String auxNickName;

    /**
     * 副号类型 1：eSIM卡  2：实体卡
     */
    private String auxType; 

    /**
     * 副设备ICCID
     */
    private String auxICCID;
    
    /**
     * 副设备eSIM卡EID 当AUX_TYPE为1时必填
     */
    private String auxEID;

    /**
     * 副设备IMEI  当AUX_TYPE为1时必填
     */
    private String auxIMEI; 

    /**
     * 操作类型：01-业务添加 02-业务删除 04-业务暂停 05-业务恢复
     */
    private String operCode;
    
    /**
     * 副设备号码
     */
    private String serialNmberB;
    
    /**
     * 副设备sim卡号
     */
    private String simCardNoB;

	private String reqType;

	public String getReqType() {
		return reqType;
	}

	public void setReqType(String reqType) {
		this.reqType = reqType;
	}

	public String getAuxNickName() {
		return auxNickName;
	}



	public void setAuxNickName(String auxNickName) {
		this.auxNickName = auxNickName;
	}



	public String getAuxType() {
		return auxType;
	}



	public void setAuxType(String auxType) {
		this.auxType = auxType;
	}



	public String getAuxICCID() {
		return auxICCID;
	}



	public void setAuxICCID(String auxICCID) {
		this.auxICCID = auxICCID;
	}



	public String getAuxEID() {
		return auxEID;
	}



	public void setAuxEID(String auxEID) {
		this.auxEID = auxEID;
	}



	public String getAuxIMEI() {
		return auxIMEI;
	}



	public void setAuxIMEI(String auxIMEI) {
		this.auxIMEI = auxIMEI;
	}



	public String getOperCode() {
		return operCode;
	}



	public void setOperCode(String operCode) {
		this.operCode = operCode;
	}


	public String getSerialNmberB() {
		return serialNmberB;
	}



	public void setSerialNmberB(String serialNmberB) {
		this.serialNmberB = serialNmberB;
	}



	public String getSimCardNoB() {
		return simCardNoB;
	}



	public void setSimCardNoB(String simCardNoB) {
		this.simCardNoB = simCardNoB;
	}
	
}
