
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.log;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SimCardLogQry
{
    public static void insRecordBySerialNumber(String serialNumber, String tradeStaffId, String tradeTypeCode, String updateTime, String eparchyCode) throws Exception
    {
        IData input = new DataMap();
        input.put("SERIAL_NUMBER", serialNumber);
        input.put("TRADE_STAFF_ID", tradeStaffId);
        input.put("TRADE_TYPE_CODE", tradeTypeCode);
        input.put("UPDATE_TIME", updateTime);
        input.put("EPARCHY_CODE", eparchyCode);
        Dao.executeUpdateByCodeCode("TL_B_ABNORMAL_OPER", "INS_RECORD_SQL", input);
    }

    public static void updRecordBySerialNumber(String serialNumber, String tradeStaffId, String tradeTypeCode) throws Exception
    {
        IData input = new DataMap();
        input.put("SERIAL_NUMBER", serialNumber);
        input.put("TRADE_STAFF_ID", tradeStaffId);
        input.put("TRADE_TYPE_CODE", tradeTypeCode);
        Dao.executeUpdateByCodeCode("TL_B_ABNORMAL_OPER", "UPD_RECORD_SQL", input);
    }
}
