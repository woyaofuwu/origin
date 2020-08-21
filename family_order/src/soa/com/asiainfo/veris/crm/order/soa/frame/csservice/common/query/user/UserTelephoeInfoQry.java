
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserTelephoeInfoQry
{
    public static IDataset getUserTelephoneByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_TELEPHONE", "SEL_BY_USERID", param);
    }

    public static IDataset getUserTrunkInfoByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER", "SEL_USER_TRUNK", param);
    }

    public static IDataset qryUserTelephoneByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_TELEPHONE", "SEL_USER_TELEPHONE_BY_PK", param);
    }
    
    public static IDataset qryUserTelephoneByUserIdDate(String userId, String timePoint) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TIME_POINT", timePoint);
        
        return Dao.qryByCode("TF_F_USER_TELEPHONE", "SEL_BY_UID_DATE", param);
    }
}
