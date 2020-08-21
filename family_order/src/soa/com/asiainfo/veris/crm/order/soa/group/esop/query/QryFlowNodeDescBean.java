package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class QryFlowNodeDescBean {

	public static IDataset qryFlowDescBy(String bpmTempletId,String status) throws Exception
    {
		IData param = new DataMap();
		param.put("BPM_TEMPLET_ID", bpmTempletId);
		param.put("VALID_TAG", status);
        return Dao.qryByCode("TD_B_EWE_FLOW_TEMPLET", "SEL_BY_BPMTEMPLETID_VALID", param, Route.CONN_CRM_CEN);
    }
	
	//通过模板id和节点id查询节点名称
	public static IDataset qryNodeDesc(String bpmTempletId,String nodeId,String status) throws Exception
	{
	    IData param = new DataMap();
	    param.put("BPM_TEMPLET_ID", bpmTempletId);
	    param.put("NODE_ID", nodeId);
	    param.put("VALID_TAG", status);
	    return Dao.qryByCode("TD_B_EWE_NODE_TEMPLET", "SEL_BY_BPMTEMPLETID_NODEID", param, Route.CONN_CRM_CEN);	
	}
	
	public static IDataset qryNodeTempletByBpmTempIdNodeType(String bpmTempletId, String nodeType, String status) throws Exception
	{
		IData param = new DataMap();
		param.put("BPM_TEMPLET_ID", bpmTempletId);
		param.put("NODE_TYPE", nodeType);
		param.put("VALID_TAG", status);
        return Dao.qryByCode("TD_B_EWE_NODE_TEMPLET", "SEL_BY_BPMTEMPLETID_NODETYPE", param, Route.CONN_CRM_CEN);	
    }
	
	public static IDataset qryFlowDescByTemplettype(String templettype,String status) throws Exception
    {
		IData param = new DataMap();
		param.put("TEMPLET_TYPE", templettype);
		param.put("VALID_TAG", status);
        return Dao.qryByCode("TD_B_EWE_FLOW_TEMPLET", "SEL_BY_EMPLETTYPE_VALID", param, Route.CONN_CRM_CEN);
    }
}
