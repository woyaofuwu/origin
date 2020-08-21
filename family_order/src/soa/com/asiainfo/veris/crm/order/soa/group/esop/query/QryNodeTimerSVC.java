package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QryNodeTimerSVC extends CSBizService{

	private static final long serialVersionUID = 1L;
	
	public static IDataset qryPresentNodeTime(IData param) throws Exception
    {
		String busiform_node_id = param.getString("BUSIFORM_NODE_ID");
        return QryNodeTimerBean.qryPresentNodeTime(busiform_node_id);
    }
	
	public static IDataset qryNotFinishHistoryNodeTime(IData param) throws Exception
    {
		String busiform_node_id = param.getString("BUSIFORM_NODE_ID");
        return QryNodeTimerBean.qryNotFinishHistoryNodeTime(busiform_node_id);
    }
	
	public static IDataset qryFinishHistoryNodeTime(IData param) throws Exception
    {
		String busiform_node_id = param.getString("BUSIFORM_NODE_ID");
        return QryNodeTimerBean.qryFinishHistoryNodeTime(busiform_node_id);
    }

}
