
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ServiceResInfoQry extends CSBizBean
{

    public static IDataset queryServiceResInfo(String element_id) throws Exception
    {
        IData data = new DataMap();
        data.put("SERVICE_ID", element_id);
        return Dao.qryByCode("TD_S_SERVICE_RES", "SEL_SERVICE_RES", data, Route.CONN_CRM_CEN);
    }
}
