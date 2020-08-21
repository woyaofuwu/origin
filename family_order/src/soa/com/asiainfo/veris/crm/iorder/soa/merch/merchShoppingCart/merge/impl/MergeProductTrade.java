package com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.merge.impl;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.merge.intf.IMerge;
import com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.order.requestdata.MerchShoppingDetailData;

public class MergeProductTrade implements IMerge {

	@Override
	public List<MerchShoppingDetailData> executeMerge(List<MerchShoppingDetailData> merchList) throws Exception {
		MerchShoppingDetailData retuProdDetailData = null;
		IDataset selectElementSet = new DatasetList();
		for (int i = 0, size = merchList.size(); i < size; i++) {
			MerchShoppingDetailData prodDetailData = merchList.get(i);
			IData requestData = prodDetailData.getDetailRequestData();
			// 主产品变更做主
			if (StringUtils.isNotBlank(requestData.getString("NEW_PRODUCT_ID")))
				retuProdDetailData = prodDetailData;
			IDataset selectElementSetTmp = new DatasetList(requestData.getString("SELECTED_ELEMENTS"));
			selectElementSet.addAll(selectElementSetTmp);
		}
		if (retuProdDetailData == null) {
			retuProdDetailData = merchList.get(0);
		}
		IData newRequestData = retuProdDetailData.getDetailRequestData();
		newRequestData.put("SELECTED_ELEMENTS", selectElementSet);
		retuProdDetailData.setDetailRequestData(newRequestData);

		List<MerchShoppingDetailData> mechList = new ArrayList<MerchShoppingDetailData>();
		mechList.add(retuProdDetailData);
		return mechList;
	}
}
