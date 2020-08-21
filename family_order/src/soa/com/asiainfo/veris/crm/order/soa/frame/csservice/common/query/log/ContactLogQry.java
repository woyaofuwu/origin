
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.log;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ContactLogQry
{
    public static IDataset getContactInfosById(String custContactId, Pagination pagination) throws Exception
    {
        IData input = new DataMap();
        input.put("CUST_CONTACT_ID", custContactId);
        return Dao.qryByCode("TL_B_CUSTCONTACT_TRADELOG", "SEL_TRADELOGBYID", input, pagination);
    }

    public static IDataset getContactInfosByLog(String updateStaffId, String startTime, String endTime, Pagination pagination) throws Exception
    {
        IData input = new DataMap();
        input.put("UPDATE_STAFF_ID", updateStaffId);
        input.put("START_TIME", startTime);
        input.put("END_TIME", endTime);
        return Dao.qryByCode("TL_B_CUSTCONTACT_TRADELOG", "SEL_TRADELOG", input, pagination);
    }
}
