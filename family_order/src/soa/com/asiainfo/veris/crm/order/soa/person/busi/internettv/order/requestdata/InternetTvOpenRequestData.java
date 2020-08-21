package com.asiainfo.veris.crm.order.soa.person.busi.internettv.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class InternetTvOpenRequestData extends BaseReqData
{
	/**
     * 魔百和 产品ID
     */
    private String topSetBoxProductId;
	
    /**
     * 手机号码
     */
    private String normalSerialNumber;
	
    /**
     * 手机用户userId
     */
    private String normalUserId;
    
    /**
     * 魔百和 基础包
     */
    private String topSetBoxBasePkgs;
    
    /**
     * 魔百和 必选包
     */
    private String topSetBoxPlatSvcPkgs;
    
    /**
     * 魔百和 可选包
     */
    private String topSetBoxOptionPkgs;
    
    /**
     * 魔百和营销活动序号
     */
    private String topSetBoxSaleActiveId;
    
    /**
     * 魔百和营销活动产品编码
     */
    private String moProductId;
    
    /**
     * 魔百和营销活动包编码
     */
    private String moPackageId;
    
    /**
     * 魔百和押金
     */
    private int topSetBoxDeposit;
    
    /**
     * 魔百和是否需要上门服务
     */
    private String artificialServices;
    
    /**
     * 魔百和营销活动预存
     */
    private String moFee;
    
    /**
     * 详细地址
     */
    private String detailAddress;
    
    /**
     * 给PBOSS自动预约派单与回单用
     */
    private String rsrvStr4;
    
    
    /**
     * 0：预约上门 1：现场施工
     */
    private String workType;

	public String getTopSetBoxProductId() {
		return topSetBoxProductId;
	}

	public void setTopSetBoxProductId(String topSetBoxProductId) {
		this.topSetBoxProductId = topSetBoxProductId;
	}

	public String getNormalSerialNumber() {
		return normalSerialNumber;
	}

	public void setNormalSerialNumber(String normalSerialNumber) {
		this.normalSerialNumber = normalSerialNumber;
	}

	public String getNormalUserId() {
		return normalUserId;
	}

	public void setNormalUserId(String normalUserId) {
		this.normalUserId = normalUserId;
	}

	public String getTopSetBoxBasePkgs() {
		return topSetBoxBasePkgs;
	}

	public void setTopSetBoxBasePkgs(String topSetBoxBasePkgs) {
		this.topSetBoxBasePkgs = topSetBoxBasePkgs;
	}

	public String getTopSetBoxPlatSvcPkgs() {
		return topSetBoxPlatSvcPkgs;
	}

	public void setTopSetBoxPlatSvcPkgs(String topSetBoxPlatSvcPkgs) {
		this.topSetBoxPlatSvcPkgs = topSetBoxPlatSvcPkgs;
	}

	public String getTopSetBoxOptionPkgs() {
		return topSetBoxOptionPkgs;
	}

	public void setTopSetBoxOptionPkgs(String topSetBoxOptionPkgs) {
		this.topSetBoxOptionPkgs = topSetBoxOptionPkgs;
	}

	public int getTopSetBoxDeposit() {
		return topSetBoxDeposit;
	}

	public void setTopSetBoxDeposit(int topSetBoxDeposit) {
		this.topSetBoxDeposit = topSetBoxDeposit;
	}

	public String getArtificialServices() {
		return artificialServices;
	}

	public void setArtificialServices(String artificialServices) {
		this.artificialServices = artificialServices;
	}

	public String getTopSetBoxSaleActiveId() {
		return topSetBoxSaleActiveId;
	}

	public void setTopSetBoxSaleActiveId(String topSetBoxSaleActiveId) {
		this.topSetBoxSaleActiveId = topSetBoxSaleActiveId;
	}

	public String getMoProductId() {
		return moProductId;
	}

	public void setMoProductId(String moProductId) {
		this.moProductId = moProductId;
	}

	public String getMoPackageId() {
		return moPackageId;
	}

	public void setMoPackageId(String moPackageId) {
		this.moPackageId = moPackageId;
	}

	public String getDetailAddress() {
		return detailAddress;
	}

	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}

	public String getMoFee() {
		return moFee;
	}

	public void setMoFee(String moFee) {
		this.moFee = moFee;
	}

	public String getRsrvStr4() {
		return rsrvStr4;
	}

	public void setRsrvStr4(String rsrvStr4) {
		this.rsrvStr4 = rsrvStr4;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}
	
	
	//BUS201907310012关于开发家庭终端调测费的需求	
    private String topSetBoxSaleActiveId2;
    private String moProductId2;
    private String moPackageId2;
    private String moFee2;
    
	public String getTopSetBoxSaleActiveId2() {
		return topSetBoxSaleActiveId2;
	}

	public void setTopSetBoxSaleActiveId2(String topSetBoxSaleActiveId2) {
		this.topSetBoxSaleActiveId2 = topSetBoxSaleActiveId2;
	}

	public String getMoProductId2() {
		return moProductId2;
	}

	public void setMoProductId2(String moProductId2) {
		this.moProductId2 = moProductId2;
	}

	public String getMoPackageId2() {
		return moPackageId2;
	}

	public void setMoPackageId2(String moPackageId2) {
		this.moPackageId2 = moPackageId2;
	}
	public String getMoFee2() {
		return moFee2;
	}

	public void setMoFee2(String moFee2) {
		this.moFee2 = moFee2;
	}
	//BUS201907310012关于开发家庭终端调测费的需求
}
