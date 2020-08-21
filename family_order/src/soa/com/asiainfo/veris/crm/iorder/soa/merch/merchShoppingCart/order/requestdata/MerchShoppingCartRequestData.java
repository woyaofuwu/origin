package com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class MerchShoppingCartRequestData extends BaseReqData {
	private String shoppingCartId;
	
	private String detailShoppingOrderIds;

	private boolean isTwoCheckSmsFlag = false;

	private List<MerchShoppingDetailData> shoppingDetailList;

	public String getShoppingCartId() {
		return shoppingCartId;
	}

	public boolean isTwoCheckSmsFlag() {
		return isTwoCheckSmsFlag;
	}

	public void setTwoCheckSmsFlag(boolean isTwoCheckSmsFlag) {
		this.isTwoCheckSmsFlag = isTwoCheckSmsFlag;
	}

	public void setShoppingCartId(String shoppingCartId) {
		this.shoppingCartId = shoppingCartId;
	}

	public String getDetailShoppingOrderIds() {
		return detailShoppingOrderIds;
	}

	public void setDetailShoppingOrderIds(String detailShoppingOrderIds) {
		this.detailShoppingOrderIds = detailShoppingOrderIds;
	}

	public List<MerchShoppingDetailData> getShoppingDetailList() {
		if (CollectionUtils.isEmpty(shoppingDetailList)) {
			shoppingDetailList = new ArrayList<MerchShoppingDetailData>();
		}
		return shoppingDetailList;
	}

	public void setShoppingDetailList(List<MerchShoppingDetailData> shoppingDetailList) {
		this.shoppingDetailList = shoppingDetailList;
	}
}
