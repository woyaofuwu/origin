package com.asiainfo.veris.crm.order.soa.person.busi.changeTeleNber;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;

public class ChangeTeleNbrSVC extends CSBizService
{
	final static String RES_TYPE_CODE_PHONE = "N";

	final static String NOTICE_FEE_TYPE_CODE = "9004";

	final static String NOTICE_SERVICE_ID = "347";

	public IDataset chooseTeleInfo(IData input) throws Exception
	{

		IData data = new DataMap();
		data.put("TRADE_TYPE_CODE", "9710");
		data.put("PRODUCT_ID", "-1");
		data.put("PACKAGE_ID", "-1");
		data.put("ELEMENT_ID", "347"); // 改号通知费
		data.put("ELEMENT_TYPE_CODE", "S"); // 服务
		data.put("EPARCHY_CODE", "0898");
		data.put("TRADE_FEE_TYPE", "3");
		// UPC.Out.PriceQueryFSV.qryDynamicPrice 没有调用此费用的地方，如果需要再修改调产商品
		// IDataset results = UpcCall.qryDynamicPrice(null, null, "S", "347",
		// "9710", null, null, null);

		return ProductFeeInfoQry.getElementFee("9701", null, null, "S", "-1", "-1", null, "347", "0898", "3");

	}
}
