package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg;

import com.ailk.common.data.IData;

public class OfferEnableModeCfg {

	private String enableMode;
	
	private String absoluteEnableDate;
	
	private String enableOffset;
	
	private String enableUnit;
	
	private String disableMode;
	
	private String absoluteDisableDate;
	
	private String disableOffset;
	
	private String disableUnit;
	
	private String cancelMode;
	
	private String absoluteCancelDate;
	
	private String cancelOffset;
	
	private String cancelUnit;
	
	public OfferEnableModeCfg(){
		
	}

	public OfferEnableModeCfg(IData data){
		this.enableMode = data.getString("ENABLE_MODE");
		this.absoluteEnableDate = data.getString("ABSOLUTE_ENABLE_DATE");
		this.enableOffset = data.getString("ENABLE_OFFSET");
		this.enableUnit = data.getString("ENABLE_UNIT");
		this.disableMode = data.getString("DISABLE_MODE");
		this.absoluteDisableDate = data.getString("ABSOLUTE_DISABLE_DATE");
		this.disableOffset = data.getString("DISABLE_OFFSET");
		this.disableUnit = data.getString("DISABLE_UNIT");
		this.cancelMode = data.getString("CANCEL_MODE");
		this.absoluteCancelDate = data.getString("ABSOLUTE_CANCEL_DATE");
		this.cancelOffset = data.getString("CANCEL_OFFSET");
		this.cancelUnit = data.getString("CANCEL_UNIT");
	}
	
	public String getEnableMode() {
		return enableMode;
	}

	public void setEnableMode(String enableMode) {
		this.enableMode = enableMode;
	}

	public String getAbsoluteEnableDate() {
		return absoluteEnableDate;
	}

	public void setAbsoluteEnableDate(String absoluteEnableDate) {
		this.absoluteEnableDate = absoluteEnableDate;
	}

	public String getEnableOffset() {
		return enableOffset;
	}

	public void setEnableOffset(String enableOffset) {
		this.enableOffset = enableOffset;
	}

	public String getEnableUnit() {
		return enableUnit;
	}

	public void setEnableUnit(String enableUnit) {
		this.enableUnit = enableUnit;
	}

	public String getDisableMode() {
		return disableMode;
	}

	public void setDisableMode(String disableMode) {
		this.disableMode = disableMode;
	}

	public String getAbsoluteDisableDate() {
		return absoluteDisableDate;
	}

	public void setAbsoluteDisableDate(String absoluteDisableDate) {
		this.absoluteDisableDate = absoluteDisableDate;
	}

	public String getDisableOffset() {
		return disableOffset;
	}

	public void setDisableOffset(String disableOffset) {
		this.disableOffset = disableOffset;
	}

	public String getDisableUnit() {
		return disableUnit;
	}

	public void setDisableUnit(String disableUnit) {
		this.disableUnit = disableUnit;
	}

	public String getCancelMode() {
		return cancelMode;
	}

	public void setCancelMode(String cancelMode) {
		this.cancelMode = cancelMode;
	}

	public String getAbsoluteCancelDate() {
		return absoluteCancelDate;
	}

	public void setAbsoluteCancelDate(String absoluteCancelDate) {
		this.absoluteCancelDate = absoluteCancelDate;
	}

	public String getCancelOffset() {
		return cancelOffset;
	}

	public void setCancelOffset(String cancelOffset) {
		this.cancelOffset = cancelOffset;
	}

	public String getCancelUnit() {
		return cancelUnit;
	}

	public void setCancelUnit(String cancelUnit) {
		this.cancelUnit = cancelUnit;
	}
}
