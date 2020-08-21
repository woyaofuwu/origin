package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class EweNodeTempletReleInfoQrySVC extends CSBizService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static IDataset qryInfoByBpmTempletNode(IData param) throws Exception
	{ 
		String bpmTempletId = param.getString("BPM_TEMPLET_ID");
		String nodeId = param.getString("NODE_ID");
		String validTag = param.getString("VALID_TAG");
		
		return EweNodeTempletReleInfoQry.qryInfoByBpmTempletNode(bpmTempletId, nodeId, validTag);
	}
	
	public static IDataset qryByBpmTempletNodePerNodeId(IData param) throws Exception
	{ 
		return EweNodeTempletReleInfoQry.qryByBpmTempletNodePerNodeId(param);
	}
}