
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.interboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class DMBusiInfoQry
{
    public static IDataset getDMBusiInfo(IData param) throws Exception
    {
        return Dao.qryByCode("TI_B_DM_BUSI", "SEL_DM_DATA", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getDMBusiSubInfo(String operID) throws Exception
    {
        IData param = new DataMap();
        param.put("OPERATE_ID1", operID);

        return Dao.qryByCode("TI_B_DM_BUSISUB", "SEL_DM_BUSISUB_DATA", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getDMdataforreq(IData param) throws Exception
    {
        return Dao.qryByCode("TI_B_DM_BUSI", "SEL_DM_DATAFORREQ", param, Route.CONN_CRM_CEN);
    }
}
