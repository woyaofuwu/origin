
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserUpmsOrderQry
{
    public static IDataset queryUserUpmsOrderInfo(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_USER_UPMS_ORDER", "SEL_BY_ORDER", param, pagination);
    }
}
