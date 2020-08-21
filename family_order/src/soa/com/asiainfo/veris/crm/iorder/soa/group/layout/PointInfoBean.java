package com.asiainfo.veris.crm.iorder.soa.group.layout;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PointInfoBean extends CSBizBean  
{
	/**
	 * 查询
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset queryByPointOneTwo(IData param) throws Exception
	{
		IDataset pointInfos = Dao.qryByCode("TD_B_EOP_POINT", "SEL_BY_POINT_ONE_TWO", param,Route.CONN_CRM_CEN);
		return pointInfos;
	}
}
