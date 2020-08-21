
package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange.order.requestdata;

public class AuxDeviceData
{
	 private String userIdB;//副设备userID
	 
	 private String serialNumberB;//副设备号码
	 
	 private String orderno;//副设备序号
	 
	 private String instId;//
	 
	 private String channlCode;//业务暂停方式：1--业务主动发起的业务暂停操作 对应UU表RSRV_NUM1 
	 
	 private String oprCode;//操作类型：01-业务添加 02-业务删除 04-业务暂停 05-业务恢复 对应UU表RSRV_STR1
	 
	 private String auxNickName;//副设备昵称 	对应UU表RSRV_STR5 

	public String getUserIdB() {
		return userIdB;
	}

	public void setUserIdB(String userIdB) {
		this.userIdB = userIdB;
	}

	public String getSerialNumberB() {
		return serialNumberB;
	}

	public void setSerialNumberB(String serialNumberB) {
		this.serialNumberB = serialNumberB;
	}

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public String getInstId() {
		return instId;
	}

	public void setInstId(String instId) {
		this.instId = instId;
	}

	public String getChannlCode() {
		return channlCode;
	}

	public void setChannlCode(String channlCode) {
		this.channlCode = channlCode;
	}

	public String getOprCode() {
		return oprCode;
	}

	public void setOprCode(String oprCode) {
		this.oprCode = oprCode;
	}

	public String getAuxNickName() {
		return auxNickName;
	}

	public void setAuxNickName(String auxNickName) {
		this.auxNickName = auxNickName;
	}
	 
}
