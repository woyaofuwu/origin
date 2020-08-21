
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserExpandInfoQry
{

    public static IDataset getUserExpandByUserIdUpdType(String user_id, String update_type) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("UPDATE_TYPE", update_type);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT A.* FROM TF_F_USER_EXPAND A WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
        parser.addSQL(" AND A.USER_ID = :USER_ID AND A.UPDATE_TYPE = :UPDATE_TYPE ");
        return Dao.qryByParse(parser);
    }

}
