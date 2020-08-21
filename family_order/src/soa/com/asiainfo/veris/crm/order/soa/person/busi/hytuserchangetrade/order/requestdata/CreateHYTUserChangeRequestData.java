package com.asiainfo.veris.crm.order.soa.person.busi.hytuserchangetrade.order.requestdata;

import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreateUserRequestData;

public class CreateHYTUserChangeRequestData extends CreateUserRequestData {
	private String isShipOwner;//是否船东
	private String discntCode;//优惠编码
	private String shipId;//船只编号
	private String isShipOwnerDiscnt;//是否为船东套餐
	private int monthOffset; //月份偏移
	private String discntName;//优惠编码
	private String isInterface;//是否接口调用
	/**
	 * @return the isShipOwner
	 */
	public String getIsShipOwner() {
		return isShipOwner;
	}
	/**
	 * @param isShipOwner the isShipOwner to set
	 */
	public void setIsShipOwner(String isShipOwner) {
		this.isShipOwner = isShipOwner;
	}
	/**
	 * @return the discntCode
	 */
	public String getDiscntCode() {
		return discntCode;
	}
	/**
	 * @param discntCode the discntCode to set
	 */
	public void setDiscntCode(String discntCode) {
		this.discntCode = discntCode;
	}
	/**
	 * @return the shipId
	 */
	public String getShipId() {
		return shipId;
	}
	/**
	 * @param shipId the shipId to set
	 */
	public void setShipId(String shipId) {
		this.shipId = shipId;
	}
	/**
	 * @return the isShipOwnerDiscnt
	 */
	public String getIsShipOwnerDiscnt() {
		return isShipOwnerDiscnt;
	}
	/**
	 * @param isShipOwnerDiscnt the isShipOwnerDiscnt to set
	 */
	public void setIsShipOwnerDiscnt(String isShipOwnerDiscnt) {
		this.isShipOwnerDiscnt = isShipOwnerDiscnt;
	}
	/**
	 * @return the monthOffset
	 */
	public int getMonthOffset() {
		return monthOffset;
	}
	/**
	 * @param monthOffset the monthOffset to set
	 */
	public void setMonthOffset(int monthOffset) {
		this.monthOffset = monthOffset;
	}
	/**
	 * @return the discntName
	 */
	public String getDiscntName() {
		return discntName;
	}
	/**
	 * @param discntName the discntName to set
	 */
	public void setDiscntName(String discntName) {
		this.discntName = discntName;
	}
	/**
	 * @return the isInterface
	 */
	public String getIsInterface() {
		return isInterface;
	}
	/**
	 * @param isInterface the isInterface to set
	 */
	public void setIsInterface(String isInterface) {
		this.isInterface = isInterface;
	}
	
}
