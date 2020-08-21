
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class VpmnGroupMember
{

    public static int updateSNByUserId(String newSerialNumber, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", newSerialNumber);
        param.put("USER_ID", userId);
        return Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "UPD_SN_BY_ID", param);
    }
}
