
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeGrpPlatSvcInfoQry
{
    /**
     * 根据tradeId查询所有ADCMAS liaolc 2013-09-03
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeGrpPlatSvcByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_GRP_PLATSVC", "SEL_BY_TRADEID", params, Route.getJourDb());
    }

}
