
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserBindInfoQry
{
    public static IDataset queryMaxSaleActiveByUserId(String userId) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_BIND_USER", "SEL_MAXSTARTDATE_BY_USERID", param);
    }

    public static IDataset queryUserBindByUserId(String userId) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_BIND_USER", "SEL_BY_USERID", param);
    }
}
