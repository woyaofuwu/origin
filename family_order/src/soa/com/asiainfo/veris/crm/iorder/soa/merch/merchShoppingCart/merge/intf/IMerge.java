package com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.merge.intf;

import java.util.List;

import com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.order.requestdata.MerchShoppingDetailData;

public interface IMerge {

	public List<MerchShoppingDetailData> executeMerge(List<MerchShoppingDetailData> merchList) throws Exception;

}
