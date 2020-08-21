
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserCreditInfoQry
{
    public static IDataset queryCreditMinStartDate(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT to_char(MIN(start_Date),'yyyymmddhh24miss') start_Date FROM tf_o_addcredit_user where user_id = :USER_ID ");
        return Dao.qryByParse(parser);
    }

}
