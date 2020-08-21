package com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.order.requestdata;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;

public class MerchShoppingDetailData {
	private String detailOrderId;
	
	private String detailStateCode;
	
	private String detailTypeCode;
	
	private IData detailRequestData;

	public MerchShoppingDetailData() {
	}

	public MerchShoppingDetailData(IData data) {
		this.detailOrderId = data.getString("DETAIL_ORDER_ID");
		this.detailStateCode = data.getString("DETAIL_STATE_CODE");
		this.detailTypeCode = data.getString("DETAIL_TYPE_CODE");
		if (StringUtils.isNotBlank(data.getString("REQUEST_DATA"))) {
			this.detailRequestData = new DataMap(data.getString("REQUEST_DATA"));
		}
	}

	public String getDetailOrderId() {
		return detailOrderId;
	}

	public void setDetailOrderId(String detailOrderId) {
		this.detailOrderId = detailOrderId;
	}

	public String getDetailStateCode() {
		return detailStateCode;
	}

	public void setDetailStateCode(String detailStateCode) {
		this.detailStateCode = detailStateCode;
	}

	public String getDetailTypeCode() {
		return detailTypeCode;
	}

	public void setDetailTypeCode(String detailTypeCode) {
		this.detailTypeCode = detailTypeCode;
	}

	public IData getDetailRequestData() {
		return detailRequestData;
	}

	public void setDetailRequestData(IData detailRequestData) {
		this.detailRequestData = detailRequestData;
	}
}
