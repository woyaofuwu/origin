
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserCheck4BuyIstead
{

    /**
     * 查询最近半小时内无密码校验成功的记录
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset queryInfoInHalf(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        IDataset out = Dao.qryByCode("TF_F_USER_CHECK4BUYISTEAD", "SEL_Info_InHalf", param);
        return out;
    }
}
