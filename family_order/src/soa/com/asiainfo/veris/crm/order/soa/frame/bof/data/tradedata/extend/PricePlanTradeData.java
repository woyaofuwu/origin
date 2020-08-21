package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class PricePlanTradeData extends BaseTradeData {
	
	private String userId;
	private String userIdA;
	private String pricePlanId;
	private String instId;
	private String pricePlanType;
	private String offerInsId;
	private String offerType;
	private String priceId;
	private String billingCode;
	private String feeType;
	private String feeTypeCode;
	private String fee;
	private String specTag;
	private String relationTypeCode;
	private String startDate;
	private String endDate;
	private String modifyTag;
	private String remark;
	
	public PricePlanTradeData(){
		
	}
	
	public PricePlanTradeData(IData data){
		this.userId = data.getString("USER_ID");
		this.userIdA = data.getString("USER_ID_A");
		this.pricePlanId = data.getString("PRICE_PLAN_ID");
		this.instId = data.getString("INST_ID");
		this.pricePlanType = data.getString("PRICE_PLAN_TYPE");
		this.priceId = data.getString("PRICE_ID");
		this.billingCode = data.getString("BILLING_CODE");
		this.feeType = data.getString("FEE_TYPE");
		this.feeTypeCode = data.getString("FEE_TYPE_CODE");
		this.fee = data.getString("FEE");
		this.specTag = data.getString("SPEC_TAG");
		this.relationTypeCode = data.getString("RELATION_TYPE_CODE");
		this.startDate = data.getString("START_DATE");
		this.endDate = data.getString("END_DATE");
		this.modifyTag = data.getString("MODIFY_TAG");
		this.remark = data.getString("REMARK");
		this.offerInsId = data.getString("OFFER_INS_ID");
		this.offerType = data.getString("OFFER_TYPE");
	}
	
	public PricePlanTradeData clone(){
		PricePlanTradeData pricePlan = new PricePlanTradeData();
		pricePlan.userId = this.userId;
		pricePlan.userIdA = this.userIdA;
		pricePlan.pricePlanId = this.pricePlanId;
		pricePlan.instId = this.instId;
		pricePlan.pricePlanType = this.pricePlanType;
		pricePlan.priceId = this.priceId;
		pricePlan.billingCode = this.billingCode;
		pricePlan.feeType = this.feeType;
		pricePlan.feeTypeCode = this.feeTypeCode;
		pricePlan.fee = this.fee;
		pricePlan.specTag = this.specTag;
		pricePlan.relationTypeCode = this.relationTypeCode;
		pricePlan.startDate = this.startDate;
		pricePlan.endDate = this.endDate;
		pricePlan.modifyTag = this.modifyTag;
		pricePlan.remark = this.remark;
		pricePlan.offerInsId = this.offerInsId;
		pricePlan.offerType = this.offerType;
		return pricePlan;
	}
	
	public IData toData(){
		IData data = new DataMap();
		data.put("USER_ID", this.userId);
		data.put("USER_ID_A", this.userIdA);
		data.put("PRICE_PLAN_ID", this.pricePlanId);
		data.put("INST_ID", this.instId);
		data.put("PRICE_PLAN_TYPE", this.pricePlanType);
		data.put("PRICE_ID", this.priceId);
		data.put("BILLING_CODE", this.billingCode);
		data.put("FEE_TYPE", this.feeType);
		data.put("FEE_TYPE_CODE", this.feeTypeCode);
		data.put("FEE", this.fee);
		data.put("SPEC_TAG", this.specTag);
		data.put("RELATION_TYPE_CODE", this.relationTypeCode);
		data.put("MODIFY_TAG", this.modifyTag);
		data.put("START_DATE", this.startDate);
		data.put("END_DATE", this.endDate);
		data.put("REMARK", this.remark);
		data.put("OFFER_INS_ID", this.offerInsId);
		data.put("OFFER_TYPE", this.offerType);
		return data;
	}
	
	@Override
    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }

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

	public String getPricePlanId() {
		return pricePlanId;
	}

	public void setPricePlanId(String pricePlanId) {
		this.pricePlanId = pricePlanId;
	}

	public String getInstId() {
		return instId;
	}

	public void setInstId(String instId) {
		this.instId = instId;
	}

	public String getPricePlanType() {
		return pricePlanType;
	}

	public void setPricePlanType(String pricePlanType) {
		this.pricePlanType = pricePlanType;
	}

	public String getPriceId() {
		return priceId;
	}

	public void setPriceId(String priceId) {
		this.priceId = priceId;
	}

	public String getBillingCode() {
		return billingCode;
	}

	public void setBillingCode(String billingCode) {
		this.billingCode = billingCode;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getSpecTag() {
		return specTag;
	}

	public void setSpecTag(String specTag) {
		this.specTag = specTag;
	}

	public String getRelationTypeCode() {
		return relationTypeCode;
	}

	public void setRelationTypeCode(String relationTypeCode) {
		this.relationTypeCode = relationTypeCode;
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

	public String getModifyTag() {
		return modifyTag;
	}

	public void setModifyTag(String modifyTag) {
		this.modifyTag = modifyTag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOfferInsId() {
		return offerInsId;
	}

	public void setOfferInsId(String offerInsId) {
		this.offerInsId = offerInsId;
	}

	public String getOfferType() {
		return offerType;
	}

	public void setOfferType(String offerType) {
		this.offerType = offerType;
	}

	@Override
	public String getTableName() {
		return "TF_B_TRADE_PRICE_PLAN";
	}
}
