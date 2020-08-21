
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserDandelionRecvQry
{

    public static IDataset getRecvMessageInfo(String forceObject, String serialNumber) throws Exception
    {
        IData params = new DataMap();
        params.put("FORCE_OBJECT", forceObject);
        params.put("SERIAL_NUMBER_B", serialNumber);
        return Dao.qryByCode("TF_F_USER_DANDELION_RECV", "SEL_RECVMESSAGE_BY_FORCEOBJECT", params);
    }

    public static IDataset getValidRecvMessageInfo(String forceObject, String serialNumber) throws Exception
    {
        IData params = new DataMap();
        params.put("FORCE_OBJECT", forceObject);
        params.put("SERIAL_NUMBER_B", serialNumber);
        return Dao.qryByCode("TF_F_USER_DANDELION_RECV", "SEL_VALID_RECVMESSAGE", params);
    }

}
