
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CustContactInfoQry
{

    public static boolean insertCustContact(String acceptMonth, String custContactId, String custId, String custName, String userId, String serialNumber, String productId, String eparchyCode, String cityCode, String contactMode, String inModeCode,
            String channelId, String subChannelId, String startTime, String finishTime, String contactState, String sessionId) throws Exception
    {

        IData param = new DataMap();
        param.put("ACCEPT_MONTH", acceptMonth);
        param.put("CUST_CONTACT_ID", custContactId);
        param.put("CUST_ID", custId);
        param.put("CUST_NAME", custName);
        param.put("USER_ID", userId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("PRODUCT_ID", productId);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("ACCEPT_MONTH", acceptMonth);
        param.put("CITY_CODE", cityCode);
        param.put("CONTACT_MODE", contactMode);
        param.put("IN_MODE_CODE", inModeCode);
        param.put("CHANNEL_ID", channelId);
        param.put("SUB_CHANNEL_ID", subChannelId);
        param.put("START_TIME", startTime);
        param.put("FINISH_TIME", finishTime);
        param.put("CONTACT_STATE", contactState);
        param.put("RSRV_STR1", sessionId);

        return Dao.insert("TF_B_CUST_CONTACT", param);
    }

    public static boolean insertInfo(IData data) throws Exception
    {
        return Dao.insert("TF_B_CUST_CONTACT_TRACE", data);
    }

    public static IDataset queryCustInfoByCustIdAndSessionId(String custId, String sessionId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        param.put("RSRV_STR1", sessionId);

        return Dao.qryByCode("TF_B_CUST_CONTACT", "SEL_BY_NUM_SESSIONIDNEW", param);
    }

    public static int updateCustBySnAndSessionId(String finishTime, String serialNumber, String sessionId) throws Exception
    {
        IData param = new DataMap();
        param.put("FINISH_TIME", finishTime);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("RSRV_STR1", sessionId);

        return Dao.executeUpdateByCodeCode("TF_B_CUST_CONTACT", "UPD_TF_B_CUST_CONTACT", param);
    }

}
