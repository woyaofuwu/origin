
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class RedUserInfoQry
{

    public static IDataset qryRedUserByUserId(String userId) throws Exception
    {
        StringBuilder sql = new StringBuilder(500);
        sql.append("SELECT * FROM TF_O_REDUSER  WHERE 1 = 1 AND USER_ID = :USER_ID AND END_DATE > SYSDATE");
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryBySql(sql, param);
    }
}
