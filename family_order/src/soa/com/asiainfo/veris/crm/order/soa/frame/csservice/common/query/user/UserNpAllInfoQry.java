
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserNpAllInfoQry
{

    public static IDataset queryUserNpAll(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_USER_NP_ALL", "SEL_BY_SERIALNUMBER", param);
    }

}
