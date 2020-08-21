
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserRentInfoQry
{

    /**
     * 查找所有有效的租机信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserRentByUserId(String userId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_RENT", "SEL_RENT_BY_USERID", cond);
    }

    public static IDataset queryUserRentInfo(String userId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_RENTINFO", cond);
    }

    public static IDataset queryUserRentInfo(String userId, String rentSerialNumber) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("RENT_SERIAL_NUMBER", rentSerialNumber);

        return Dao.qryByCode("TF_F_USER_RENT", "SEL_ALLRENT_BY_USERID_SN", cond);
    }
}
