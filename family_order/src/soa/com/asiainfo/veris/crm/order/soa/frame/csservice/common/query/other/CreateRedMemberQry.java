
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CreateRedMemberQry
{
    /*
     * public static boolean save(IData params) throws Exception { return Dao.save("HN_SMS_REDMEMBER", params); }
     */

    public static IDataset checkRedMemberIsExists(IData data) throws Exception
    {
        IDataset result = Dao.qryByCode("HN_SMS_REDMEMBER", "SEL_BY_SN1", data);

        return result;
    }

    public static int save(IData params) throws Exception
    {
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" UPDATE TF_F_SMS_REDMEMBER ");
        parser.addSQL(" SET END_TIME = to_date(:END_TIME,'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND USER_ID =:USER_ID ");
        return Dao.executeUpdate(parser);
    }
}
