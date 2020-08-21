
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CustmgrCommparaQry
{

    // 
    public static IDataset getErrPasswdCount(String EPARCHY_CODE) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", EPARCHY_CODE);
        return Dao.qryByCode("TD_O_CUSTMGR_COMMPARA", "SEL_ERRPASSWD_COUNT", param, Route.CONN_CRM_CEN);
    }
}
