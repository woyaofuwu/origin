package com.asiainfo.veris.crm.iorder.soa.group.layout;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TempletGroupBizBean extends CSBizBean 
{
	public IDataset queryByTempletNodeId(IData param) throws Exception
	{
		IDataset templetNodeInfos = Dao.qryByCode("TD_B_EWE_NODE_TEMPLET", "SEL_BY_BPMTEMPLETID_NODEID", param,Route.CONN_CRM_CEN);
		return templetNodeInfos;
	}
	
	public IDataset queryByTempletNodeType(IData param) throws Exception
	{
		IDataset templetNodeInfos = Dao.qryByCode("TD_B_EWE_NODE_TEMPLET", "SEL_BY_BPMTEMPLETID_NODETYPE", param,Route.CONN_CRM_CEN);
		return templetNodeInfos;
	}
}
