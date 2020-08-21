
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.speservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class FixParamQry
{

    public static IDataset queryBusimanage(IData data) throws Exception
    {
        return Dao.qryByCode("TF_SM_BI_BUSIMANAGE", "SEL_BY_SALEACTTYPE", data);
    }

}
