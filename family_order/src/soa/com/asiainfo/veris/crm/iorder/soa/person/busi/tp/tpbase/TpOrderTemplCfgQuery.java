package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpbase;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TpOrderTemplCfgQuery {
	
	public static IData getOrderTempl(IData input) throws Exception{
		String templId = input.getString("TEMPL_ID");
		String withType = input.getString("WITH_TYPE");
		String withObj = input.getString("WITH_OBJ");
		IData inparam = new DataMap();
		inparam.put("TEMPL_ID", templId);
		inparam.put("WITH_OBJ", withObj);
		inparam.put("WITH_TYPE", withType);
		IDataset dataset = Dao.qryByCodeParser("TP_ORDER_TEMPL_CFG", "SEL_BY_PKS", inparam);
		if(DataUtils.isNotEmpty(dataset)){
			return dataset.first();
		}
		return null;
	}
}
