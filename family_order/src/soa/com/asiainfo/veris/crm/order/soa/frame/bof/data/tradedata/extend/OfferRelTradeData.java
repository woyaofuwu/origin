package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class OfferRelTradeData extends BaseTradeData {
	
	private String offerCode;
	private String offerType;
	private String offerInsId;
	private String userId;
	private String groupId;
	private String relOfferCode;
	private String relOfferType;
	private String relOfferInsId;
	private String relUserId;
	private String relType;
	private String startDate;
	private String endDate;
	private String modifyTag;
	private String instId;
	private String remark;
	
	public OfferRelTradeData(){
		
	}
	
	public OfferRelTradeData(IData data){
		this.offerCode = data.getString("OFFER_CODE");
		this.offerType = data.getString("OFFER_TYPE");
		this.offerInsId = data.getString("OFFER_INS_ID");
		this.userId = data.getString("USER_ID");
		this.groupId = data.getString("GROUP_ID");
		this.relOfferCode = data.getString("REL_OFFER_CODE");
		this.relOfferType = data.getString("REL_OFFER_TYPE");
		this.relOfferInsId = data.getString("REL_OFFER_INS_ID");
		this.relUserId = data.getString("REL_USER_ID");
		this.relType = data.getString("REL_TYPE");
		this.startDate = data.getString("START_DATE");
		this.endDate = data.getString("END_DATE");
		this.modifyTag = data.getString("MODIFY_TAG");
		this.instId = data.getString("INST_ID");
		this.remark = data.getString("REMARK");
	}
	
	public OfferRelTradeData clone(){
		OfferRelTradeData offerRel = new OfferRelTradeData();
		offerRel.offerCode = this.offerCode;
		offerRel.offerType = this.offerType;
		offerRel.offerInsId = this.offerInsId;
		offerRel.userId = this.userId;
		offerRel.groupId = this.groupId;
		offerRel.relOfferCode = this.relOfferCode;
		offerRel.relOfferType = this.relOfferType;
		offerRel.relOfferInsId = this.relOfferInsId;
		offerRel.relUserId = this.relUserId;
		offerRel.relType = this.relType;
		offerRel.startDate = this.startDate;
		offerRel.endDate = this.endDate;
		offerRel.modifyTag = this.modifyTag;
		offerRel.instId = this.instId;
		offerRel.remark = this.remark;
		return offerRel;
	}
	
	public IData toData(){
		IData data = new DataMap();
		data.put("OFFER_CODE", this.offerCode);
		data.put("OFFER_TYPE", this.offerType);
		data.put("OFFER_INS_ID", this.offerInsId);
		data.put("USER_ID", this.userId);
		data.put("GROUP_ID", this.groupId);
		data.put("REL_OFFER_CODE", this.relOfferCode);
		data.put("REL_OFFER_TYPE", this.relOfferType);
		data.put("REL_OFFER_INS_ID", this.relOfferInsId);
		data.put("REL_USER_ID", this.relUserId);
		data.put("REL_TYPE", this.relType);
		data.put("START_DATE", this.startDate);
		data.put("END_DATE", this.endDate);
		data.put("MODIFY_TAG", this.modifyTag);
		data.put("INST_ID", this.instId);
		data.put("REMARK", this.remark);
		return data;
	}
	
	@Override
    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }

	public String getOfferCode() {
		return offerCode;
	}

	public void setOfferCode(String offerCode) {
		this.offerCode = offerCode;
	}

	public String getOfferType() {
		return offerType;
	}

	public void setOfferType(String offerType) {
		this.offerType = offerType;
	}

	public String getOfferInsId() {
		return offerInsId;
	}

	public void setOfferInsId(String offerInsId) {
		this.offerInsId = offerInsId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getRelOfferCode() {
		return relOfferCode;
	}

	public void setRelOfferCode(String relOfferCode) {
		this.relOfferCode = relOfferCode;
	}

	public String getRelOfferType() {
		return relOfferType;
	}

	public void setRelOfferType(String relOfferType) {
		this.relOfferType = relOfferType;
	}

	public String getRelOfferInsId() {
		return relOfferInsId;
	}

	public void setRelOfferInsId(String relOfferInsId) {
		this.relOfferInsId = relOfferInsId;
	}

	public String getRelUserId() {
		return relUserId;
	}

	public void setRelUserId(String relUserId) {
		this.relUserId = relUserId;
	}

	public String getRelType() {
		return relType;
	}

	public void setRelType(String relType) {
		this.relType = relType;
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
	
	public String getInstId() {
		return instId;
	}

	public void setInstId(String instId) {
		this.instId = instId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String getTableName() {
		return "TF_B_TRADE_OFFER_REL";
	}
}
