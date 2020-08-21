
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TdCustInfoQry
{

    public static IDataset qryTdCustInfos(String serial_number, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        return Dao.qryByCode("TF_F_TDCUST", "SEL_BY_SERIAL_NUMBER", param, page);
    }

}
