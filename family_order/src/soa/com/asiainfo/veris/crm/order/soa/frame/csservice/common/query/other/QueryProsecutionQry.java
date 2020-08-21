
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryProsecutionQry extends CSBizBean
{
    public static IDataset queryProsecution(String serialNum, String routeEparchyCode, String startDate, String endDate, String prosecuteeNum, String tagProsecution, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("EPARCHY_CODE", routeEparchyCode);
        indata.put("PARA_CODE1", serialNum);
        indata.put("PARA_CODE2", startDate);
        indata.put("PARA_CODE3", endDate);
        indata.put("PARA_CODE4", prosecuteeNum);
        indata.put("PARA_CODE5", tagProsecution);
        indata.put("PARA_CODE6", "");
        indata.put("PARA_CODE7", "");
        indata.put("PARA_CODE8", "");
        indata.put("PARA_CODE9", "");
        indata.put("PARA_CODE10", "");
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_USER_PROSECUTION_EVER_HAIN", indata, pagination, routeEparchyCode);
    }
}
