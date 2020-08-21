
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class WasteSmsCancelQry
{
    public static IDataset queryIsBlackKf(IData params) throws Exception
    {
        return Dao.qryByCode("TI_BI_BLACK_KF", "SEL_BY_NUMBER_TIME", params);
    }

    /**
     * 根据是否在黑名单
     */
    public static IDataset querymaxtimes(IData params) throws Exception
    {
        return Dao.qryByCode("TI_BI_BLACK_KF", "SEL_BY_MAXTIME", params);
    }
}
