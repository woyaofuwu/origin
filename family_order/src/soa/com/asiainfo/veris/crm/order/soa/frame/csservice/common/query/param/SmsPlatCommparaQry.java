package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SmsPlatCommparaQry {

	public static IDataset querySmsPlatInfo(String spCode,String bizCode,String rsrvStr3) throws Exception
	{
		IData smplatData = new DataMap();
		smplatData.put("PARA_CODE1", spCode);
		smplatData.put("PARA_CODE2", bizCode);
		smplatData.put("PARAM_CODE", (StringUtils.isEmpty(rsrvStr3)?"0000":rsrvStr3));
		return Dao.qryByCode("TD_SM_PLAT_COMMPARA", "SEL_BY_SALE_PLAT", smplatData,Route.CONN_CRM_CEN);
	}
}
