
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserSertrackLogInfoQry
{

    public static IDataset queryByUserId(String userId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);

        StringBuilder strSql = new StringBuilder();
        strSql.append(" select * from TL_F_USER_SERTRACK_LOG ");
        strSql.append(" where user_id = :USER_ID");
        strSql.append(" and MODIFY_TAG = 0 ");
        strSql.append(" and SERV_TYPE in('0','2') ");
        strSql.append(" and state_code=0 ");
        strSql.append(" and sysdate between START_DATE and END_DATE ");

        return Dao.qryBySql(strSql, cond);
    }
}
