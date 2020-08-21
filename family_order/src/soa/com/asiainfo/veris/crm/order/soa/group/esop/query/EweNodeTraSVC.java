package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class EweNodeTraSVC extends CSBizService{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IData qryEweNodeTraByBusiformIdAndNodeId(IData param) throws Exception {
		String busiformId = param.getString("BUSIFORM_ID");
		String nodeId = param.getString("NODE_ID");
		IDataset traIDataset  = EweNodeTraQry.qryEweNodeTraByBusiformIdAndNodeId(busiformId, nodeId);
		IData data = new DataMap();
		if (IDataUtil.isNotEmpty(traIDataset)) 
		{
			String subIbsysId = traIDataset.getData(0).getString("SUB_BI_SN");
	    	
	    	IDataset attrDataset = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysId, "0");
	    	
	    	if (IDataUtil.isNotEmpty(attrDataset)) {
				for (int i = 0; i < attrDataset.size(); i++) {
					
					data.put(attrDataset.getData(i).getString("ATTR_CODE"), attrDataset.getData(i).getString("ATTR_VALUE"));
				}
			}
		}
    	
    	return data;
	}
	
	public IData getEweNodeTraByBusiformIdAndNodeId(IData param) throws Exception 
	{
		String busiformId = param.getString("BUSIFORM_ID");
		String nodeId = param.getString("NODE_ID");
		
		IDataset traIDataset  = EweNodeTraQry.qryEweNodeTraByBusiformIdAndNodeId(busiformId, nodeId);
    	return IDataUtil.isNotEmpty(traIDataset)? traIDataset.first():new DataMap();
	}
	
	public IDataset qryEweNodeTraByBusiformNodeId(IData param) throws Exception 
	{
		String busiformNodeId = param.getString("BUSIFORM_NODE_ID");
		
		IDataset traIDataset  = EweNodeTraQry.qryEweNodeTraByBusiformNodeId(busiformNodeId);
    	return traIDataset;
	}
	
	
}