package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class DriveIdcBean {
	
	public static void updAutoTime(String busiformNodeId, String autoTime) throws Exception
    {
    	IData param = new DataMap();
    	param.put("BUSIFORM_NODE_ID", busiformNodeId);
    	param.put("AUTO_TIME", autoTime);
		Dao.executeUpdateByCodeCode("TF_B_EWE_NODE", "UPD_AUTO_BY_BUSIFORMNODEID", param,Route.getJourDb(Route.getCrmDefaultDb()));
    }

}
