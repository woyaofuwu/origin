package com.asiainfo.veris.crm.order.soa.group.esop.query;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class WorkformNodeSVC extends CSBizService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IDataset qryNodeByIbsysidNodeDesc(IData param) throws Exception{
		String ibsysid = param.getString("IBSYSID");
		String nodeId = param.getString("NODE_ID");		
		IDataset dataset = WorkformNodeBean.qryNodeByIbsysidNodeDesc(ibsysid, nodeId);
		return dataset;
	}
	
	public IDataset qryNodeByIbsysidNodeTimeDesc(IData param) throws Exception{
		String ibsysid = param.getString("IBSYSID");
		String nodeId = param.getString("NODE_ID");		
		IDataset dataset = WorkformNodeBean.qryNodeByIbsysidNodeTimeDesc(ibsysid, nodeId);
		return dataset;
	}
	
}