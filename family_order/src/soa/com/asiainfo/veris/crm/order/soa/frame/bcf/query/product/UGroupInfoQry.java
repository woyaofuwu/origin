package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class UGroupInfoQry {

	public static IDataset queryGroupSpecialTagRsrvStr1(String relId) throws Exception{
		IDataset dataset = UpcCall.queryTempChaByCond(relId, "TD_B_PRODUCT_PACKAGE", "RSRV_STR1");
		return dataset;
	}
	
	public static String querySpecialTagRsrvStr1(String productId, String groupId) throws Exception{
		IDataset rst = UpcCall.queryOfferGroupRelOfferIdGroupId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, groupId);
		if(IDataUtil.isEmpty(rst)){
			return null;
		}
		
		IData result = rst.getData(0);
		String relId = result.getString("REL_ID");
		IDataset dataset = queryGroupSpecialTagRsrvStr1(relId);
		if(IDataUtil.isEmpty(dataset)){
			return "";
		}
		return dataset.getData(0).getString("FIELD_VALUE");
	}
}
