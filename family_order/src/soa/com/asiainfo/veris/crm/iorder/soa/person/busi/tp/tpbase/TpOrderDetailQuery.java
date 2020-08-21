package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpbase;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TpOrderDetailQuery {
	public static IData getOrderDetail(IData input) throws Exception{
		String detailId = input.getString("DETAIL_ID");
		String orderId = input.getString("ORDER_ID");
		IData inparam = new DataMap();
		inparam.put("DETAIL_ID", detailId);
		inparam.put("ORDER_ID", orderId);
		IDataset dataset = Dao.qryByCodeParser("TP_ORDER_DETAIL", "SEL_BY_PKS", inparam);
		if(DataUtils.isNotEmpty(dataset)){
			return dataset.first();
		}
		return null;
	}
}
