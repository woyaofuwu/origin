package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxcreate.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class NoPhoneTopSetBoxCreateRequestData extends BaseReqData
{
	/**
     * 魔百和 产品ID
     */
    private String topSetBoxProductId;
	
    /**
     * 魔百和 基础包
     */
    private String topSetBoxBasePkgs;
    
    /**
     * 魔百和 可选包
     */
    private String topSetBoxOptionPkgs;
    
    
    /**
     * 魔百和押金
     */
    private int topSetBoxDeposit;
    
    /**
     * 魔百和是否需要上门服务
     */
    private String artificialServices;
    
    /**
     * 详细地址
     */
    private String detailAddress;
    
    /**
     * 给PBOSS自动预约派单与回单用
     */
    private String rsrvStr4;
    
    //147号码
    private String serialNumberB;
    
    //魔百和受理时长
    private String topSetBoxTime;
    
    //魔百和受理时长费用
    private String topSetBoxFee;
    
    // 0：预约上门 1：现场施工
    private String workType;
    
	public String getTopSetBoxProductId() {
		return topSetBoxProductId;
	}

	public void setTopSetBoxProductId(String topSetBoxProductId) {
		this.topSetBoxProductId = topSetBoxProductId;
	}


	public String getTopSetBoxBasePkgs() {
		return topSetBoxBasePkgs;
	}

	public void setTopSetBoxBasePkgs(String topSetBoxBasePkgs) {
		this.topSetBoxBasePkgs = topSetBoxBasePkgs;
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


	public String getDetailAddress() {
		return detailAddress;
	}

	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}

	public String getRsrvStr4() {
		return rsrvStr4;
	}

	public void setRsrvStr4(String rsrvStr4) {
		this.rsrvStr4 = rsrvStr4;
	}

	public String getSerialNumberB() {
		return serialNumberB;
	}

	public void setSerialNumberB(String serialNumberB) {
		this.serialNumberB = serialNumberB;
	}

	public String getTopSetBoxTime() {
		return topSetBoxTime;
	}

	public void setTopSetBoxTime(String topSetBoxTime) {
		this.topSetBoxTime = topSetBoxTime;
	}

	public String getTopSetBoxFee() {
		return topSetBoxFee;
	}

	public void setTopSetBoxFee(String topSetBoxFee) {
		this.topSetBoxFee = topSetBoxFee;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}
	
}
