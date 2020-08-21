
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeHintInfoQry
{
    public static IDataset qryHintInfoByTrade(String tag, String eparchyCode, IData data) throws Exception
    {
        data.put("START_REG_DATE", tag);
        data.put("END_REG_DATE", eparchyCode);
        return Dao.qryByCodeParser("TD_B_TRADE_HINT", "SEL_HINTINFO_BY_TRADE", data);
    }

}
