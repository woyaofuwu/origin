package com.asiainfo.veris.crm.order.soa.person.busi.commonserviceintegrate;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SvcColleConfigBean {

	public static IDataset getServiceCollectionByIntfName(String intfName, String eparchyCode) throws Exception {
		IData qryParam = new DataMap();

		qryParam.put("INTF_NAME", intfName);
		qryParam.put("EPARCHY_CODE", eparchyCode);
		IDataset dataset = Dao.qryByCodeParser("TD_S_SERVICE_COLLECTION_CONFIG", "SEL_BY_INTF_NAME", qryParam,
				Route.CONN_CRM_CEN);

		return dataset;
	}

}
