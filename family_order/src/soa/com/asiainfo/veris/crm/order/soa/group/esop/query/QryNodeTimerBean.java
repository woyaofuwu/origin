package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QryNodeTimerBean {

	//查询当前节点的超时情况
	public static IDataset qryPresentNodeTime(String busiform_node_id) throws Exception
    {
		IData param = new DataMap();
		param.put("BUSIFORM_NODE_ID", busiform_node_id);
		return Dao.qryByCodeParser("TF_B_EWE_TIMER", "SEL_BY_BUSIFORMNODEID", param, Route.getJourDb(Route.CONN_CRM_CG));
    }
	//查询未完成的历史节点的超时情况
	public static IDataset qryNotFinishHistoryNodeTime(String busiform_node_id) throws Exception
	{
		IData param = new DataMap();
		param.put("BUSIFORM_NODE_ID", busiform_node_id);
		return Dao.qryByCodeParser("TF_B_EWE_TIMER_TRA", "SEL_BY_BUSIFORMNODEID", param, Route.getJourDb(Route.CONN_CRM_CG));
	}
	
	//查询已经完成的工单的历史节点的超时情况
	public static IDataset qryFinishHistoryNodeTime(String busiform_node_id) throws Exception
	{
		IData param = new DataMap();
		param.put("BUSIFORM_NODE_ID", busiform_node_id);
		return Dao.qryByCodeParser("TF_BH_EWE_TIMER", "SEL_BY_BUSIFORMNODEID", param, Route.getJourDb(Route.CONN_CRM_CG));
	}
}
