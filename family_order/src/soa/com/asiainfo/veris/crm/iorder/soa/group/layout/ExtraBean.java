package com.asiainfo.veris.crm.iorder.soa.group.layout;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ExtraBean extends CSBizBean 
{
	public IDataset queryByExtraIdTypeKey(IData param) throws Exception
	{
		IDataset templetInfos = Dao.qryByCode("TD_B_EOP_EXTRA", "SEL_BY_EXTRA_ID_TYPE_KEY", param,Route.CONN_CRM_CEN);
		return templetInfos;
	}
	
	public IDataset queryByExtraIdType(IData param) throws Exception
	{
		IDataset templetInfos = Dao.qryByCode("TD_B_EOP_EXTRA", "SEL_BY_EXTRA_ID_TYPE", param,Route.CONN_CRM_CEN);
		return templetInfos;
	}
}
