
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class AreaParamInfoQry
{
    public static IDataset queryAreaCode(String parentAreaCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PARENT_AREA_CODE", parentAreaCode);
        return Dao.qryByCode("TD_M_AREA", "SEL_BY_PARENTAREACODE", param, Route.CONN_CRM_CEN);
    }
}
