
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeWideNetOtherInfoQry
{

    public static IDataset queryTradeWideNetOther(String trade_id) throws Exception
    {

        IData param = new DataMap();

        param.put("TRADE_ID", trade_id);

        return Dao.qryByCode("TF_B_TRADE_WIDENET_OTHER", "SEL_WIDENET_BY_TRADEID", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

}
