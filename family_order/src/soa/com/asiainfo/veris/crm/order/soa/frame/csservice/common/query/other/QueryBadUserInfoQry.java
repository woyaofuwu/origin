
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryBadUserInfoQry extends CSBizBean
{
    public static IDataset queryBadUserInfoByBatchID(String batchID, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("EPARCHY_CODE", routeEparchyCode);
        indata.put("BATCH_ID", batchID);
        return Dao.qryByCode("TF_F_USER", "SEL_BY_SN_BATCHID_NEW", indata, pagination, routeEparchyCode);
    }

    public static IDataset queryBadUserInfoBySN(String serialNum, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("EPARCHY_CODE", routeEparchyCode);
        indata.put("SERIAL_NUMBER", serialNum);
        return Dao.qryByCode("TF_F_USER", "SEL_BAD_USER_BY_SN_NEW", indata, pagination, routeEparchyCode);
    }
}
