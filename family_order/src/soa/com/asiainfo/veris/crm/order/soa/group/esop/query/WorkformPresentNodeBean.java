package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformPresentNodeBean {

	public static IDataset qryPresentNodeByIbsysid(String ibsysid,String bpm_templet_id) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("BPM_TEMPLET_ID", bpm_templet_id);
        return Dao.qryByCodeParser("TF_B_EWE_NODE", "SEL_PRESENT_NODE_BY_IBSYSID", param, Route.getJourDb(Route.CONN_CRM_CG));
    }
}
