package com.asiainfo.veris.crm.order.soa.person.busi.resale.order.requestdata;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BaseInterResaleRequestData extends BaseReqData {

	private String serialNumber ; //", data.getString("MSISDN", ""));
    private String userId;
	private String tradeStaffId;
	private String tradeDepartId;
	private String tradeCityCode;
	private String tradeEparchyCode;
	private String imsi ; //存放imsi
	private String oprCode;  //操作代码
	private String newImsi; //新imsi
	private String oprNumb;
	private String lteUserId;
	private String updateTime;
	
	
	private IDataset svcInfos;
	private IDataset resData;
	
	

	public IDataset getResData() {
		return resData;
	}

	public void setResData(IDataset resData) {
		this.resData = resData;
	}

	public IDataset getSvcInfos() {
		return svcInfos;
	}
	
	public void setSvcInfos(IDataset svcInfos) {
		this.svcInfos = svcInfos;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}
	
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	public String getTradeStaffId() {
		return tradeStaffId;
	}
	public void setTradeStaffId(String tradeStaffId) {
		this.tradeStaffId = tradeStaffId;
	}
	
	public String getTradeDepartId() {
		return tradeDepartId;
	}
	
	public void setTradeDepartId(String tradeDepartId) {
		this.tradeDepartId = tradeDepartId;
	}
	
	public String getTradeCityCode() {
		return tradeCityCode;
	}
	
	public void setTradeCityCode(String tradeCityCode) {
		this.tradeCityCode = tradeCityCode;
	}
	
	public String getTradeEparchyCode() {
		return tradeEparchyCode;
	}
	
	public void setTradeEparchyCode(String tradeEparchyCode) {
		this.tradeEparchyCode = tradeEparchyCode;
	}
	
	public String getImsi() {
		return imsi;
	}
	
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	
	public String getOprCode() {
		return oprCode;
	}
	
	public void setOprCode(String oprCode) {
		this.oprCode = oprCode;
	}
	
	public String getNewImsi() {
		return newImsi;
	}
	
	public void setNewImsi(String newImsi) {
		this.newImsi = newImsi;
	}

	public String getOprNumb() {
		return oprNumb;
	}

	public void setOprNumb(String oprNumb) {
		this.oprNumb = oprNumb;
	}

	public String getLteUserId() {
		return lteUserId;
	}

	public void setLteUserId(String lteUserId) {
		this.lteUserId = lteUserId;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

}
