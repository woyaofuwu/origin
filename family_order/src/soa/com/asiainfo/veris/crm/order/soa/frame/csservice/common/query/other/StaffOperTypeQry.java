
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class StaffOperTypeQry
{
    public static IDataset queryByObjectOper(String OBJECT_ID, String DEAL_TYPE) throws Exception
    {
        IData param = new DataMap();
        param.put("OBJECTID", OBJECT_ID);
        param.put("DEAL_TYPE", DEAL_TYPE);
        IDataset set = Dao.qryByCodeParser("TD_B_STAFFOPERTYPE", "SEL_BY_OBJECT_DEALTYPE", param, Route.CONN_CRM_CEN);
        return set;
    }

}
