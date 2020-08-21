
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/*
 * 异网转接查询
 */
public class QueryUnionTransQry extends CSBizBean
{
    public static IDataset queryUnionTrans(String routeEparchyCode, String startSerialNumber, String endSerialNumber, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("EPARCHY_CODE", routeEparchyCode);
        indata.put("START_SERIALNUMBER", startSerialNumber);
        indata.put("END_SERIALNUMBER", endSerialNumber);

        return Dao.qryByCode("TI_B_TRANS_PHONE", "SEL_BY_PHONE_A_NG", indata, pagination, routeEparchyCode);
    }
}
