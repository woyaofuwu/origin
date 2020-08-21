package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QryFlowNodeDescSVC extends CSBizService {

	private static final long serialVersionUID = 1L;

	public static IDataset qryFlowDescByTempletId(IData param) throws Exception
    {
		String bpmTempletId = param.getString("BPM_TEMPLET_ID");
		String status = param.getString("VALID_TAG");
        return QryFlowNodeDescBean.qryFlowDescBy(bpmTempletId, status);
    }
	
	public static IDataset qryNodeDescByTempletId(IData param) throws Exception
	{
		String bpmTempletId = param.getString("BPM_TEMPLET_ID");
		String nodeId = param.getString("NODE_ID");
		String status = param.getString("VALID_TAG");
		return QryFlowNodeDescBean.qryNodeDesc(bpmTempletId, nodeId, status);
	}
	public static IDataset qryFlowDescByTemplettype(IData param) throws Exception
	{
		String templettype = param.getString("TEMPLET_TYPE");
		String status = param.getString("VALID_TAG");
		return QryFlowNodeDescBean.qryFlowDescByTemplettype(templettype, status);
	}
	
	
}
