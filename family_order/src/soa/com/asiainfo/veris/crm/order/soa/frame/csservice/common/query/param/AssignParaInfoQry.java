
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class AssignParaInfoQry
{

    // 
    public static IDataset getUSIM4GInfo(String simTypeCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("PARA_CODE2", simTypeCode);
        return Dao.qryByCode("TD_M_ASSIGNPARA", "SEL_BY_USIMTYPECODE", param, Route.CONN_CRM_CEN);
    }
}
