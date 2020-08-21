
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class HighDangerInfoQry extends CSBizBean
{
    public static IDataset queryHighDangerUser(IData data, Pagination pagination) throws Exception
    {

        return Dao.qryByCodeParser("TI_BI_HIGH_DANGER", "SEL_BY_SERIALNUMBER2", data, pagination);
    }
}
