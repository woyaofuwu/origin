
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PlatInfoQry
{

    public static IDataset getPlatAttrByServiceId(String serviceId) throws Exception
    {

        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        return Dao.qryByCode("TD_B_PLATSVC_ATTR", "SEL_BY_SVCID", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getPlatInfoByServiceId(String serviceId) throws Exception
    {

        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SERVICE_ID", param, Route.CONN_CRM_CEN);
    }
}
