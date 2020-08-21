package com.asiainfo.veris.crm.iorder.soa.group.layout;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PageElementBean extends CSBizBean 
{
	public IDataset queryByElementId(IData param) throws Exception
	{
		IDataset templetInfos = Dao.qryByCode("TD_B_EOP_PAGE_ELEMENT", "SEL_BY_ELEMENT_ID", param,Route.CONN_CRM_CEN);
		return templetInfos;
	}
}
