
package com.asiainfo.veris.crm.order.soa.person.busi.credittrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: NpOpenPhoneCancelTradeAction.java
 * @Description: NP 缴开取消欠停工单 只有45-46 走这个action
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014年7月26日 上午10:33:18 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014年7月26日 lijm3 v1.0.0 修改原因
 */
public class NpOpenPhoneCancelTradeAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String iTradeTypeCode = btd.getTradeTypeCode();
        String userId = btd.getMainTradeData().getUserId();
        String tradeTypeCode = "";
        IData param = new DataMap();
        if ("45".equals(iTradeTypeCode))
        {
            IDataset ids = TradeNpQry.getTradeNpByUserIdTradeTypeCodeCancelTag(userId, "43", "0");
            if (IDataUtil.isNotEmpty(ids) && !"030".equals(ids.getData(0).getString("STATE")))
            {
                String tradeId = ids.getData(0).getString("TRADE_ID");

                IDataset trades = TradeInfoQry.getTradeInfosBySubscribeState(tradeId, "0", "43", "0");

                if (IDataUtil.isNotEmpty(trades))
                {
                    param.put("TRADE_ID", tradeId);
                    param.put("UPDATE_STAFF_ID", "SOA");
                    param.put("CANCEL_TAG", "1");
                    Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_CANCELTAG_BY_TID", param,Route.getJourDb());
                    param.clear();
                    param.put("TRADE_ID", tradeId);
                    param.put("CANCEL_TAG", "1");
                    param.put("CANCEL_STAFF_ID", "SOA");
                    param.put("CANCEL_DEPART_ID", "SOA");
                    Dao.executeUpdateByCodeCode("TF_BH_TRADE", "INS_BY_TRADE_CANCEL", param,Route.getJourDb());
                    param.clear();
                    param.put("TRADE_ID", tradeId);
                    Dao.executeUpdateByCodeCode("TF_B_TRADE", "DEL_CANCEL_BOOK", param,Route.getJourDb());
                    param.clear();
                    param.put("TRADE_ID", tradeId);
                    param.put("CANCEL_TAG", "1");
                    Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_NP_CANCEL_TAG", param,Route.getJourDb());

                    String orderId = trades.getData(0).getString("ORDER_ID");
                    IData orderInfo = UOrderInfoQry.qryOrderByOrderId(orderId, btd.getMainTradeData().getEparchyCode());
                    moveBhOrder(orderInfo);
                }

            }
        }
        else if ("46".equals(iTradeTypeCode))
        {
            IDataset trades = TradeInfoQry.getTradeInfosByCancelTag(userId, "1", "44");
            if (IDataUtil.isNotEmpty(trades))
            {
                String tradeId = trades.getData(0).getString("TRADE_ID");
                param.clear();
                param.put("TRADE_ID", tradeId);
                param.put("CANCEL_STAFF_ID", "SOA");
                param.put("CANCEL_DEPART_ID", "SOA");
                param.put("CANCEL_TAG", "1");
                Dao.executeUpdateByCodeCode("TF_BH_TRADE", "INS_BY_TRADE_CANCEL", param);

                param.clear();
                param.put("TRADE_ID", tradeId);
                Dao.executeUpdateByCodeCode("TF_B_TRADE", "DEL_CANCEL_BOOK", param);
                String orderId = trades.getData(0).getString("ORDER_ID");
                IData orderInfo = UOrderInfoQry.qryOrderByOrderId(orderId, btd.getMainTradeData().getEparchyCode());
                moveBhOrder(orderInfo);
            }
        }
    }

    private void moveBhOrder(IData orderInfo) throws Exception
    {
        if (IDataUtil.isNotEmpty(orderInfo))
        {
            orderInfo.put("CANCEL_TAG", "1");
            orderInfo.put("CANCEL_STAFF_ID", "SOA");
            orderInfo.put("CANCEL_DEPART_ID", "SOA");
            Dao.insert("TF_BH_ORDER", orderInfo);
            orderInfo.put("CANCEL_TAG", "0");
            String[] keys = new String[]
            { "ORDER_ID", "ACCEPT_MONTH", "CANCEL_TAG" };
            Dao.delete("TF_B_ORDER", orderInfo, keys,Route.getJourDb());
        }

    }
}
