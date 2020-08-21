package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class EweAsynSVC extends CSBizService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static IDataset saveAsynInfo(IData param) throws Exception
	{
		EweAsynBean.saveAsynInfo(param);
		return new DatasetList();
	}
	
	public static IDataset queryByBusiformNode(IData param) throws Exception
	{
		String busiformId = param.getString("BUSIFORM_ID");
		String nodeId = param.getString("NODE_ID");
		return EweAsynBean.queryByBusiformNode(busiformId,nodeId);
	}
	
	public static IDataset qryInfosByBusiformNodeId(IData data) throws Exception
	{
		String busiformNodeId = data.getString("BUSIFORM_NODE_ID");
		IDataset ewe =  EweAsynInfoQry.qryInfosByBusiformNodeId(busiformNodeId);
		return ewe;
	}

}
