package com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.order.requestdata.MerchShoppingCartRequestData;
import com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.order.requestdata.MerchShoppingDetailData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shopping.MerchShoppingCartQry;

public class BuildMerchShoppingCartReqData extends BaseBuilder implements IBuilder {

	public void buildBusiRequestData(IData pageInputParams, BaseReqData brd) throws Exception {
		MerchShoppingCartRequestData shoppingCartReqData = (MerchShoppingCartRequestData) brd;
		String shoppingCartId = pageInputParams.getString("SHOPPING_CART_ID");
		String detailShppingOrderIds = pageInputParams.getString("DETAIL_ORDER_IDS");
		shoppingCartReqData.setShoppingCartId(shoppingCartId);
		shoppingCartReqData.setDetailShoppingOrderIds(detailShppingOrderIds);
		String feeFlag = pageInputParams.getString("SHOPPING_CART_HAS_FEE");
		if(StringUtils.isNotBlank(feeFlag) && StringUtils.equalsIgnoreCase(feeFlag, "true"))
			shoppingCartReqData.setTwoCheckSmsFlag(false);
		else
			shoppingCartReqData.setTwoCheckSmsFlag(true);
		// 去数据库查询所有订单数据
		IDataset shoppingDetailSet = MerchShoppingCartQry.getEnableShoppingDetailByCartId(shoppingCartId);
		for (int i = 0, size = shoppingDetailSet.size(); i < size; i++) {
			IData shoppingCartDetail = shoppingDetailSet.getData(i);
			MerchShoppingDetailData detailData = new MerchShoppingDetailData(shoppingCartDetail);
			String detailOrderId = detailData.getDetailOrderId();
			if (detailShppingOrderIds.indexOf(detailOrderId) == -1)
				continue;
			IData detailRequestData = detailData.getDetailRequestData();
			if (IDataUtil.isEmpty(detailRequestData)) {
				continue;
			}
			detailRequestData.remove("SUBMIT_TYPE");
			shoppingCartReqData.getShoppingDetailList().add(detailData);
		}
	}

	public BaseReqData getBlankRequestDataInstance() {
		return new MerchShoppingCartRequestData();
	}
}
