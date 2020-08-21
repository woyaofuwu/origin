package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformEomsSubBean {

	public static IDataset getEmosSubInfoByibsysId(IData param)throws Exception {
		
		return Dao.qryByCode("TF_B_EOP_EOMS_SUB", "SEL_BY_IBSYSID_RECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
	}

}
