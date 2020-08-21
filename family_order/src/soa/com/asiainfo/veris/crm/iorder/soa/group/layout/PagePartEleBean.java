package com.asiainfo.veris.crm.iorder.soa.group.layout;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PagePartEleBean extends CSBizBean
{
	public IDataset queryByPartId(IData param) throws Exception
	{
		IDataset templetInfos = Dao.qryByCode("TD_B_EOP_PAGE_PART_ELE", "SEL_BY_PART_ID", param,Route.CONN_CRM_CEN);
		return templetInfos;
	}
}
