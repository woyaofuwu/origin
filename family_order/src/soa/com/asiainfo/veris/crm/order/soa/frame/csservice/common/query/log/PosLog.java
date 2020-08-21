
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.log;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PosLog
{

    public static IDataset queryPosLog(String tradeId) throws Exception
    {

        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("STATUS", "0");
        IDataset logSet = Dao.qryByCode("TL_B_POS_LOG", "SEL_BY_ALL_TRADEID", param);
        return logSet;
    }

    public static int queryPosLogNotPositive(String tradeId) throws Exception
    {

        int fee = 0;
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("STATUS", "0");
        IDataset logSet = Dao.qryByCode("TL_B_POS_LOG", "SEL_BY_TRADEID_SUM_NOTS", param);
        if (logSet != null && logSet.size() > 0)
        {
            fee = logSet.getData(0).getInt("AMOUNT");
        }
        return fee;
    }

}
