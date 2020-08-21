
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ProDistinctJTcodeQry
{
    public static IDataset QryProDistinctJTcodeInfos(String discntCode, String discntName, String jtCode, Pagination page) throws Exception
    {
        IData param = new DataMap();

        param.put("DISCNT_CODE", discntCode);
        param.put("DISCNT_NAME", discntName);
        param.put("JT_CODE", jtCode);

        return Dao.qryByCode("TD_B_HUNAN_JTCODE", "SEL_BY_DISCNT_CODE", param, page, Route.CONN_CRM_CEN);
    }
}
