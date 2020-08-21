package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class EweNodeTaskRelaInfoQry 
{
    public static boolean insertNodeTaskRela(IData param) throws Exception
    {
    	return Dao.insert("TD_B_EWE_NODE_TASK_RELA", param, Route.CONN_CRM_CEN);
    }
    
    public static IDataset qryByTimerId(String timerId,String timerType, String status) throws Exception
    {
        IData param = new DataMap();
        param.put("TIMER_ID", timerId);
        param.put("VALID_TAG", status);
        param.put("TIMER_TYPE", timerType);
        return Dao.qryByCode("TD_B_EWE_TASK_TIMER", "SEL_BY_TIMERID", param, Route.CONN_CRM_CEN);
    }
}
