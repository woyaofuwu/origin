package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class DeferTradeFeeData extends BaseTradeData {
	private String userId;
	private String feeMode;
	private String feeTypeCode;
	private String deferCycleId;
	private String deferItemCode;
	private String money;
	private String actTag;
	private String updateIime;
	private String updateStaffId;
	private String updateDepartId;
	private String remark;
	private String rsrvStr1;
	private String rsrvStr2;
	private String rsrvStr3;
	private String rsrvStr4;
	private String rsrvStr5;
	private String rsrvStr6;
	private String rsrvStr7;
	private String rsrvStr8;
	private String rsrvStr9;
	private String rsrvStr10;

	public DeferTradeFeeData() {

	}

	public DeferTradeFeeData(IData data) {
		this.userId = data.getString("USER_ID");
		this.feeMode = data.getString("FEE_MODE");
		this.feeTypeCode = data.getString("FEE_TYPE_CODE");
		this.deferCycleId = data.getString("DEFER_CYCLE_ID");
		this.deferItemCode = data.getString("DEFER_ITEM_CODE");
		this.money = data.getString("MONEY");
		this.actTag = data.getString("ACT_TAG");
		this.updateIime = data.getString("UPDATE_TIME");
		this.updateStaffId = data.getString("UPDATE_STAFF_ID");
		this.updateDepartId = data.getString("UPDATE_DEPART_ID");
		this.remark = data.getString("REMARK");
		this.rsrvStr1 = data.getString("RSRV_STR1");
		this.rsrvStr2 = data.getString("RSRV_STR2");
		this.rsrvStr3 = data.getString("RSRV_STR3");
		this.rsrvStr4 = data.getString("RSRV_STR4");
		this.rsrvStr5 = data.getString("RSRV_STR5");
		this.rsrvStr6 = data.getString("RSRV_STR6");
		this.rsrvStr7 = data.getString("RSRV_STR7");
		this.rsrvStr8 = data.getString("RSRV_STR8");
		this.rsrvStr9 = data.getString("RSRV_STR9");
		this.rsrvStr10 = data.getString("RSRV_STR10");

	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFeeMode() {
		return feeMode;
	}

	public void setFeeMode(String feeMode) {
		this.feeMode = feeMode;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public String getDeferCycleId() {
		return deferCycleId;
	}

	public void setDeferCycleId(String deferCycleId) {
		this.deferCycleId = deferCycleId;
	}

	public String getDeferItemCode() {
		return deferItemCode;
	}

	public void setDeferItemCode(String deferItemCode) {
		this.deferItemCode = deferItemCode;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getActTag() {
		return actTag;
	}

	public void setActTag(String actTag) {
		this.actTag = actTag;
	}

	public String getUpdateIime() {
		return updateIime;
	}

	public void setUpdateIime(String updateIime) {
		this.updateIime = updateIime;
	}

	public String getUpdateStaffId() {
		return updateStaffId;
	}

	public void setUpdateStaffId(String updateStaffId) {
		this.updateStaffId = updateStaffId;
	}

	public String getUpdateDepartId() {
		return updateDepartId;
	}

	public void setUpdateDepartId(String updateDepartId) {
		this.updateDepartId = updateDepartId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRsrvStr1() {
		return rsrvStr1;
	}

	public void setRsrvStr1(String rsrvStr1) {
		this.rsrvStr1 = rsrvStr1;
	}

	public String getRsrvStr2() {
		return rsrvStr2;
	}

	public void setRsrvStr2(String rsrvStr2) {
		this.rsrvStr2 = rsrvStr2;
	}

	public String getRsrvStr3() {
		return rsrvStr3;
	}

	public void setRsrvStr3(String rsrvStr3) {
		this.rsrvStr3 = rsrvStr3;
	}

	public String getRsrvStr4() {
		return rsrvStr4;
	}

	public void setRsrvStr4(String rsrvStr4) {
		this.rsrvStr4 = rsrvStr4;
	}

	public String getRsrvStr5() {
		return rsrvStr5;
	}

	public void setRsrvStr5(String rsrvStr5) {
		this.rsrvStr5 = rsrvStr5;
	}

	public String getRsrvStr6() {
		return rsrvStr6;
	}

	public void setRsrvStr6(String rsrvStr6) {
		this.rsrvStr6 = rsrvStr6;
	}

	public String getRsrvStr7() {
		return rsrvStr7;
	}

	public void setRsrvStr7(String rsrvStr7) {
		this.rsrvStr7 = rsrvStr7;
	}

	public String getRsrvStr8() {
		return rsrvStr8;
	}

	public void setRsrvStr8(String rsrvStr8) {
		this.rsrvStr8 = rsrvStr8;
	}

	public String getRsrvStr9() {
		return rsrvStr9;
	}

	public void setRsrvStr9(String rsrvStr9) {
		this.rsrvStr9 = rsrvStr9;
	}

	public String getRsrvStr10() {
		return rsrvStr10;
	}

	public void setRsrvStr10(String rsrvStr10) {
		this.rsrvStr10 = rsrvStr10;
	}

	public DeferTradeFeeData clone() {
		DeferTradeFeeData DeferTradeFeeData = new DeferTradeFeeData();
		DeferTradeFeeData.setUserId(this.getUserId());
		DeferTradeFeeData.setFeeMode(this.getFeeMode());
		DeferTradeFeeData.setFeeTypeCode(this.getFeeTypeCode());
		DeferTradeFeeData.setDeferCycleId(this.getDeferCycleId());
		DeferTradeFeeData.setDeferItemCode(this.getDeferItemCode());
		DeferTradeFeeData.setMoney(this.getMoney());
		DeferTradeFeeData.setActTag(this.getActTag());
		DeferTradeFeeData.setUpdateIime(this.getUpdateIime());
		DeferTradeFeeData.setUpdateStaffId(this.getUpdateStaffId());
		DeferTradeFeeData.setUpdateDepartId(this.getUpdateDepartId());
		DeferTradeFeeData.setRemark(this.getRemark());
		DeferTradeFeeData.setRsrvStr1(this.getRsrvStr1());
		DeferTradeFeeData.setRsrvStr2(this.getRsrvStr2());
		DeferTradeFeeData.setRsrvStr3(this.getRsrvStr3());
		DeferTradeFeeData.setRsrvStr4(this.getRsrvStr4());
		DeferTradeFeeData.setRsrvStr5(this.getRsrvStr5());
		DeferTradeFeeData.setRsrvStr6(this.getRsrvStr6());
		DeferTradeFeeData.setRsrvStr7(this.getRsrvStr7());
		DeferTradeFeeData.setRsrvStr8(this.getRsrvStr8());
		DeferTradeFeeData.setRsrvStr9(this.getRsrvStr9());
		DeferTradeFeeData.setRsrvStr10(this.getRsrvStr10());

		return DeferTradeFeeData;
	}

	@Override
	public String getTableName() {

		return "TF_B_TRADEFEE_DEFER";
	}

	@Override
	public IData toData() {
		IData data = new DataMap();

		data.put("USER_ID", this.userId);
		data.put("USER_ID", this.userId);
		data.put("FEE_MODE", this.feeMode);
		data.put("FEE_TYPE_CODE", this.feeTypeCode);
		data.put("DEFER_CYCLE_ID", this.deferCycleId);
		data.put("DEFER_ITEM_CODE", this.deferItemCode);
		data.put("MONEY", this.money);
		data.put("ACT_TAG", this.actTag);
		data.put("UPDATE_TIME", this.updateIime);
		data.put("UPDATE_STAFF_ID", this.updateStaffId);
		data.put("UPDATE_DEPART_ID", this.updateDepartId);
		data.put("REMARK", this.remark);
		data.put("RSRV_STR1", this.rsrvStr1);
		data.put("RSRV_STR2", this.rsrvStr2);
		data.put("RSRV_STR3", this.rsrvStr3);
		data.put("RSRV_STR4", this.rsrvStr4);
		data.put("RSRV_STR5", this.rsrvStr5);
		data.put("RSRV_STR6", this.rsrvStr6);
		data.put("RSRV_STR7", this.rsrvStr7);
		data.put("RSRV_STR8", this.rsrvStr8);
		data.put("RSRV_STR9", this.rsrvStr9);
		data.put("RSRV_STR10", this.rsrvStr10);

		return data;
	}

	public String toString() {
		IData data = new DataMap();
		data.put(getTableName(), this.toData());
		return data.toString();
	}

}
