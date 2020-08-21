
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserNpInfoQry
{

    public static IDataset qryUserNpInfosBySerialNumber(String serialNumber) throws Exception
    {

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_USER", "SEL_ALL_BY_NPRESTORE", param);

    }

    public static IDataset qryUserNpInfosBySn(String sn) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        return Dao.qryByCode("TF_F_USER_NP", "SEL_BY_SN", param);

    }

    public static IDataset qryUserNpInfosByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_NP", "SEL_BY_ID", param);

    }

    public static IDataset qryUserNpInfosByUserIdLimitTime(String userId, String limitTime) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("LIMIT_TIME", limitTime);
        return Dao.qryByCode("TF_F_USER_NP", "SEL_BY_ID_B", param);

    }

}
