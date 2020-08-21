package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remotedestroyuser.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class RemoteDestroyUserRequestData extends BaseReqData
{
    private String contactPhone; //联系人电话

    private String contactName; //联系人
    
    private String giftSerialNumber;//转账现金号码
    
    private String giftCustName;//转账现金客户名称
    
    private String giftSerialNumberB;//转账非现金号码
    
    private String giftCustNameB;//转账非现金客户名称
    
    private String createPhone;//建单人电话
    
    private String createContact;//建单人姓名
    
    private String createOrgName;//建单人营业厅

	private String homeProv;//客户归属省代码

	private String homeProvName;//客户归属省名称

	private String remoteOrderId;//销户操作流水号

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getGiftSerialNumber() {
		return giftSerialNumber;
	}

	public void setGiftSerialNumber(String giftSerialNumber) {
		this.giftSerialNumber = giftSerialNumber;
	}

	public String getGiftCustName() {
		return giftCustName;
	}

	public void setGiftCustName(String giftCustName) {
		this.giftCustName = giftCustName;
	}

	public String getGiftSerialNumberB() {
		return giftSerialNumberB;
	}

	public void setGiftSerialNumberB(String giftSerialNumberB) {
		this.giftSerialNumberB = giftSerialNumberB;
	}

	public String getGiftCustNameB() {
		return giftCustNameB;
	}

	public void setGiftCustNameB(String giftCustNameB) {
		this.giftCustNameB = giftCustNameB;
	}

	public String getCreatePhone() {
		return createPhone;
	}

	public void setCreatePhone(String createPhone) {
		this.createPhone = createPhone;
	}

	public String getCreateContact() {
		return createContact;
	}

	public void setCreateContact(String createContact) {
		this.createContact = createContact;
	}

	public String getCreateOrgName() {
		return createOrgName;
	}

	public void setCreateOrgName(String createOrgName) {
		this.createOrgName = createOrgName;
	}

	public String getHomeProvName() {
		return homeProvName;
	}

	public void setHomeProvName(String homeProvName) {
		this.homeProvName = homeProvName;
	}

	public String getHomeProv() {
		return homeProv;
	}

	public void setHomeProv(String homeProv) {
		this.homeProv = homeProv;
	}

	public String getRemoteOrderId() {
		return remoteOrderId;
	}

	public void setRemoteOrderId(String remoteOrderId) {
		this.remoteOrderId = remoteOrderId;
	}
}