
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class WapSessionInfoQry
{

    public static boolean insertData(String sessionId, String cust_contact_id, String idType, String serialNumber, String channelId, String crenedceNo, String accept_start, String accept_end, String auth_code) throws Exception
    {
        IData param = new DataMap();
        param.put("SESSION_ID", sessionId);
        param.put("CUST_CONTACT_ID", cust_contact_id);
        param.put("ID_TYPE", idType);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("CHANNEL_ID", channelId);
        param.put("CREDENCE_NO", crenedceNo);
        param.put("ACCEPT_START", accept_start);
        param.put("ACCEPT_END", accept_end);
        param.put("AUTH_CODE", auth_code);

        return Dao.insert("TF_B_WAP_SESSION", param);
    }

    public static IDataset queryWapSession(String sessId, String serNum) throws Exception
    {
        IData data = new DataMap();
        data.put("SESSION_ID", sessId);
        data.put("SERIAL_NUMBER", serNum);

        return Dao.qryByCode("TF_B_WAP_SESSION", "SEL_CHECK_USER_VOUCHER", data);
    }

    public static int updateDataByHanghalf(String sessionId, String idType, String serialNumber, String credenceNo) throws Exception
    {
        IData param = new DataMap();
        param.put("ID_TYPE", idType);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("CREDENCE_NO", credenceNo);
        param.put("SESSION_ID", sessionId);
        int index = Dao.executeUpdateByCodeCode("TF_B_WAP_SESSION", "UPD_ACCEPT_END_HALF", param);

        return index;
    }

    public static int updateDataByOut(String sessionId, String idType, String serialNumber, String credenceNo) throws Exception
    {
        IData param = new DataMap();
        param.put("ID_TYPE", idType);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("CREDENCE_NO", credenceNo);
        param.put("SESSION_ID", sessionId);
        int index = Dao.executeUpdateByCodeCode("TF_B_WAP_SESSION", "UPD_ACCEPT_END", param);

        return index;
    }
}
