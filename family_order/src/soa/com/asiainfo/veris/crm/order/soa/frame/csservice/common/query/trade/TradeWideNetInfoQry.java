
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeWideNetInfoQry
{
    public static IDataset queryTradeWideNet(String trade_id) throws Exception
    {

        IData param = new DataMap();

        param.put("TRADE_ID", trade_id);

        return Dao.qryByCode("TF_B_TRADE_WIDENET", "SEL_BY_TRADEID", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    /**
     * 更新用户宽带资料表时间
     * 
     * @author chenzm
     * @param trade_id
     * @param start_date
     * @throws Exception
     */
    public static void updateStartDate(String trade_id, String start_date) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("START_DATE", start_date);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_WIDENET", "UPD_STARTDATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    public static int updateTradeWidenetPboss(String tradeId, String portNum, String roomName, String areaName) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("CF_PORT", portNum);
        param.put("CF_ROOM", roomName);
        param.put("CF_TTAREA", areaName);
        return Dao.executeUpdateByCodeCode("TF_B_TRADE_WIDENET", "UPD_TRADEWIDENET_PBOSS", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
}
