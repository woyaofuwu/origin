
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QuerySimcardMissbillQry extends CSBizBean
{
    public static IDataset querySimcardMissbill(String routeEparchyCode, String acceptDate, String cityCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("CITY_CODE", cityCode);
        indata.put("ACCEPT_DATE", acceptDate);
        return Dao.qryByCode("TD_S_COMMPARA", "SQL_SIMCARDMISSBILL", indata, pagination);
    }

    public static IDataset querySimcardType(String routeEparchyCode) throws Exception
    {
        IData indata = new DataMap();
        indata.put("PARA_CODE1", routeEparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_CITY", indata, Route.CONN_CRM_CEN);
    }
}
