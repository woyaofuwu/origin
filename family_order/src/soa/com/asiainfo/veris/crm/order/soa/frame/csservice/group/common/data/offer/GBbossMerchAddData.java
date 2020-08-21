package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;

public class GBbossMerchAddData
{
    private String merchOperCode;//1商品操作类型
    
    private String location;//2发送方与落地方
    
    private String payMode;//3套餐生效方式
  
    private String offerId; //4商品ID
    
    private String bizMode;//5业务开展模式
    
    private String busNeedDegree;//6设置业务保障等级
 
    private String hostCompany;   //7主办省
    
	private String merchOfferId;   //8商品订单号
    
    private String merchOrderId;   //9商品订购关系ID
    
    private IDataset attInfos = new DatasetList();//10附件信息
    
    private IDataset auditorInfos = new DatasetList();//11审批信息
    
    private IDataset contactorInfos = new DatasetList();//12联系人信息
    
    private String bbossManageCreate;//13管理节点
    
    private String isCredit;//14信控暂停标志
    
	public String getIsCredit() {
		return isCredit;
	}

	public void setIsCredit(String isCredit) {
		this.isCredit = isCredit;
	}

	public String getMerchOfferId() {
		return merchOfferId;
	}

	public void setMerchOfferId(String merchOfferId) {
		this.merchOfferId = merchOfferId;
	}

	public String getMerchOrderId() {
		return merchOrderId;
	}

	public void setMerchOrderId(String merchOrderId) {
		this.merchOrderId = merchOrderId;
	}

	public String getBbossManageCreate() {
		return bbossManageCreate;
	}

	public void setBbossManageCreate(String bbossManageCreate) {
		this.bbossManageCreate = bbossManageCreate;
	}



	public String getMerchOperCode() {
		return merchOperCode;
	}

	public void setMerchOperCode(String merchOperCode) {
		this.merchOperCode = merchOperCode;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public String getBizMode() {
		return bizMode;
	}

	public void setBizMode(String bizMode) {
		this.bizMode = bizMode;
	}

	public String getBusNeedDegree() {
		return busNeedDegree;
	}

	public void setBusNeedDegree(String busNeedDegree) {
		this.busNeedDegree = busNeedDegree;
	}

	public String getHostCompany() {
		return hostCompany;
	}

	public void setHostCompany(String hostCompany) {
		this.hostCompany = hostCompany;
	}

	public IDataset getAttInfos() {
		return attInfos;
	}

	public void setAttInfos(IDataset attInfos) {
		this.attInfos = attInfos;
	}

	public IDataset getAuditorInfos() {
		return auditorInfos;
	}

	public void setAuditorInfos(IDataset auditorInfos) {
		this.auditorInfos = auditorInfos;
	}

	public IDataset getContactorInfos() {
		return contactorInfos;
	}

	public void setContactorInfos(IDataset contactorInfos) {
		this.contactorInfos = contactorInfos;
	}



}
