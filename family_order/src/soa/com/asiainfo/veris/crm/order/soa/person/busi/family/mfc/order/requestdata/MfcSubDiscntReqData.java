package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class MfcSubDiscntReqData extends BaseReqData 
{
	
	private String modifyTag;

    private String userId;
    
    private String userIdA;

	private String roleCode;

	private String productCode;
    
    private String productOfferingID;
   
	private String startDate;
    
    private String endDate;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
    public String getUserIdA() {
		return userIdA;
	}

	public void setUserIdA(String userIdA) {
		this.userIdA = userIdA;
	}

	public String getModifyTag() {
		return modifyTag;
	}

	public void setModifyTag(String modifyTag) {
		this.modifyTag = modifyTag;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

    public String getProductOfferingID() {
		return productOfferingID;
	}

	public void setProductOfferingID(String productOfferingID) {
		this.productOfferingID = productOfferingID;
	}

}
