
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserNetNpQry
{

    public static IDataset qryTradeInfoByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TRADE_TYPE_CODE", "402");
        return Dao.qryByCode("TF_B_TRADE", "SEL_TRADEINFO_BY_USERID_NETNP", param);
    }

    public static IDataset qryUserNetNpLog(String serial_number) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);

        return Dao.qryByCode("TF_B_USER_NETNP_LOG", "SEL_NETNP_LOG", param);
    }
}
