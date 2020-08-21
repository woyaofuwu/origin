package com.asiainfo.veris.crm.iorder.soa.family.busi.cancel.data;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;

/**
 * @desc TF_B_TRADE表对象（所有字段）
 * @author danglt
 * @date 2019年3月23日
 * @version v1.0
 */
public class FullMainTradeData extends MainTradeData {

	private static final long serialVersionUID = -8479713778690133168L;

	private String tradeId;

	private String acceptMonth;

	private String acceptDate;

	private String tradeStaffId;

	private String tradeDepartId;

	private String tradeCityCode;

	private String tradeEparchyCode;

	private String updateTime;

	private String updateStaffId;

	private String updateDepartId;
	
	//额外附加字段（表示在B或者BH表）
	private String fromTableName;
	
	public FullMainTradeData() {}

	public FullMainTradeData(IData data) {
		super(data);
		this.tradeId = data.getString("TRADE_ID");
		this.acceptMonth = data.getString("ACCEPT_MONTH");
		this.acceptDate = data.getString("ACCEPT_DATE");
		this.tradeStaffId = data.getString("TRADE_STAFF_ID");
		this.tradeDepartId = data.getString("TRADE_DEPART_ID");
		this.tradeCityCode = data.getString("TRADE_CITY_CODE");
		this.tradeEparchyCode = data.getString("TRADE_EPARCHY_CODE");
		this.updateTime = data.getString("UPDATE_TIME");
		this.updateStaffId = data.getString("UPDATE_STAFF_ID");
		this.updateDepartId = data.getString("UPDATE_DEPART_ID");
		this.fromTableName = data.getString("TABLE_NAME");
	}

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public String getAcceptMonth() {
		return acceptMonth;
	}

	public void setAcceptMonth(String acceptMonth) {
		this.acceptMonth = acceptMonth;
	}

	public String getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(String acceptDate) {
		this.acceptDate = acceptDate;
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

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
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
	
	public String getFromTableName() {
		return fromTableName;
	}

	public void setFromTableName(String fromTableName) {
		this.fromTableName = fromTableName;
	}

	public IData transeToData() {
		IData data = new DataMap(this.toData());
		data.put("TRADE_ID", this.tradeId);
		data.put("ACCEPT_MONTH", this.acceptMonth);
		data.put("ACCEPT_DATE", this.acceptDate);
		data.put("TRADE_STAFF_ID", this.tradeStaffId);
		data.put("TRADE_DEPART_ID", this.tradeDepartId);
		data.put("TRADE_CITY_CODE", this.tradeCityCode);
		data.put("TRADE_EPARCHY_CODE", this.tradeEparchyCode);
		data.put("UPDATE_TIME", this.updateTime);
		data.put("UPDATE_STAFF_ID", this.updateStaffId);
		data.put("UPDATE_DEPART_ID", this.updateDepartId);
		return data;
	}

	public FullMainTradeData cloneObject() {
		FullMainTradeData mainTradeDataClone = new FullMainTradeData(this.clone().toData());
		mainTradeDataClone.setTradeId(this.tradeId);
		mainTradeDataClone.setAcceptMonth(this.acceptMonth);
		mainTradeDataClone.setAcceptDate(this.acceptDate);
		mainTradeDataClone.setTradeStaffId(this.tradeStaffId);
		mainTradeDataClone.setTradeDepartId(this.tradeDepartId);
		mainTradeDataClone.setTradeCityCode(this.tradeCityCode);
		mainTradeDataClone.setTradeEparchyCode(this.tradeEparchyCode);
		mainTradeDataClone.setUpdateTime(this.updateTime);
		mainTradeDataClone.setUpdateStaffId(this.updateStaffId);
		mainTradeDataClone.setUpdateDepartId(this.updateDepartId);
		return mainTradeDataClone;
	}

	public String toString() {
		return this.transeToData().toString();
	}

}

