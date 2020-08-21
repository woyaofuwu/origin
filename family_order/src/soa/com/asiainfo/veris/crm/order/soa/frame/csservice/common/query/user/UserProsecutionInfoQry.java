
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserProsecutionInfoQry
{

    public static IDataset queryProsecution(String serialNumber, String prosecuteeNum, String prosecutionWay, String startDate, String endDate, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("PARA_CODE1", serialNumber);
        data.put("PARA_CODE4", prosecuteeNum);
        data.put("PARA_CODE5", prosecutionWay);
        data.put("PARA_CODE2", startDate);
        data.put("PARA_CODE3", endDate);

        return Dao.qryByCode("TF_F_USER_PROSECUTION", "SEL_PROSECUTION_BY_SN", data);
    }
}
