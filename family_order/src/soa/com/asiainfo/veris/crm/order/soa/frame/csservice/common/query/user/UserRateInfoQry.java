
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserRateInfoQry
{
    
    /**
     * 查询用户的历史资源信息
     * @param user_id
     * @param res_type_code
     * @return
     * @throws Exception
     */
    public static IDataset getUserRateByUserIdDate(String userid, String timePoint) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userid);
        param.put("TIME_POINT", timePoint);
        return Dao.qryByCode("TF_F_USER_RATE", "SEL_BY_UID_DATE", param);
    }
}
