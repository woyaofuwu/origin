package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class EweNodeTempletReleInfoQry 
{
	/**
	 * 查询信息 
	 * @param bpmTempletId
	 * @param nodeId
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryInfoByBpmTempletNode(String bpmTempletId, String nodeId, String validTag) throws Exception
	{
		IData param = new DataMap();
		param.put("BPM_TEMPLET_ID", bpmTempletId);
		param.put("NODE_ID", nodeId);
		param.put("VALID_TAG", validTag);
        return Dao.qryByCode("TD_B_EWE_NODE_TEMPLET_RELA", "SEL_BY_BPMTEMPLETID_NODEID", param, Route.CONN_CRM_CEN);
	}
	
	public static IDataset qryByBpmTempletNodePerNodeId(IData param) throws Exception
	{
        return Dao.qryByCode("TD_B_EWE_NODE_TEMPLET_RELA", "SEL_BY_BPM_PRENODEID_NODEID", param, Route.CONN_CRM_CEN);
	}
    

}
