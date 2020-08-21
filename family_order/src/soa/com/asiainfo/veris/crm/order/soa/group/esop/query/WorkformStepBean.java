package com.asiainfo.veris.crm.order.soa.group.esop.query;


import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class WorkformStepBean
{
    public static void updByBusiformNodeIdStepId(String busiformNodeId, String step, String state, String extId) throws Exception
    {
        IData param = new DataMap();
        param.put("STEP_ID", step);
        param.put("BUSIFORM_NODE_ID", busiformNodeId);
        param.put("STATE", state);
        param.put("EXT_ID", extId);
        Dao.executeUpdateByCodeCode("TF_B_EWE_STEP", "UPD_STATE_BY_BUSIFORMNODEID_STEP", param, Route.getJourDb(Route.getCrmDefaultDb()));
    }
}
