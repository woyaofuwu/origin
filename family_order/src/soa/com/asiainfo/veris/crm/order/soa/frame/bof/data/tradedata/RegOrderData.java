
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata;

import java.util.ArrayList;
import java.util.List;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class RegOrderData
{

    private RegTradeData mainRegData;

    private List<RegTradeData> otherRegData = new ArrayList<RegTradeData>();

    public RegOrderData(RegTradeData mainRegData)
    {
        this.mainRegData = mainRegData;
    }

    public RegOrderData(String orderId) throws Exception
    {
        IData orderData = UOrderInfoQry.qryOrderAllByOrderId(orderId);
        if (IDataUtil.isNotEmpty(orderData))
        {
            MainOrderData mainOrderData = new MainOrderData(orderData);
            IDataset orderSubs = null;
            if ("1".equals(mainOrderData.getOrderKindCode()))
            {
                orderSubs = UOrderSubInfoQry.qryOrderSubByOrderId(mainOrderData.getOrderId());
            }

            IDataset allMainTrades = new DatasetList();
            if (IDataUtil.isNotEmpty(orderSubs))
            {
                for (Object sub : orderSubs)
                {
                    IData tradeSub = (IData) sub;
                    String tradeId = tradeSub.getString("TRADE_ID");
                    String routeId = tradeSub.getString("ROUTE_ID");
                    String mainTag = tradeSub.getString("MAIN_TAG", "");
                    IDataset mainTrades = IDataUtil.idToIds(UTradeInfoQry.qryTradeByTradeId(tradeId, "0", routeId));
                    allMainTrades.addAll(mainTrades);
                    if (IDataUtil.isEmpty(mainTrades))
                    {
                        mainTrades = IDataUtil.idToIds(UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", routeId));
                        allMainTrades.addAll(mainTrades);
                    }
                    for (Object main : mainTrades)
                    {
                        IData mainTrade = (IData) main;
                        mainTrade.put("MAIN_TAG", mainTag);
                    }
                }
            }
            else
            {
                IDataset mainTrades = TradeInfoQry.getMainTradeByOrderId(orderId, "0", BizRoute.getRouteId());
                allMainTrades.addAll(mainTrades);
                if (IDataUtil.isEmpty(mainTrades))
                {
                    mainTrades = TradeHistoryInfoQry.qryTradeByOrderId(orderId, "0", BizRoute.getRouteId());
                    allMainTrades.addAll(mainTrades);
                }
            }

            String tempTradeId = "";
            // DataHelper.sort(allMainTrades, "TRADE_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
            if (IDataUtil.isNotEmpty(allMainTrades))
            {
                int size = allMainTrades.size();
                for (int i = 0; i < size; i++)
                {
                    IData mainTrade = allMainTrades.getData(i);
                    if (tempTradeId.indexOf(mainTrade.getString("TRADE_ID")) < 0)
                    {
                        tempTradeId += mainTrade.getString("TRADE_ID") + ",";
                        RegTradeData regTradeData = new RegTradeData(mainTrade);
                        if ((i == 0 && size == 1) || "1".equals(mainTrade.getString("MAIN_TAG")))
                        {
                            this.mainRegData = regTradeData;
                        }
                        else
                        {
                            this.otherRegData.add(regTradeData);
                        }
                    }
                }
            }
        }
    }

    public RegTradeData getMainRegData()
    {
        return mainRegData;
    }

    public List<RegTradeData> getOtherRegData()
    {
        return otherRegData;
    }

    public RegTradeData getRegTradeData(String tradeId)
    {
        if (tradeId.equals(this.mainRegData.getTradeId()))
        {
            return this.mainRegData;
        }
        else
        {
            for (RegTradeData regTradeData : otherRegData)
            {
                if (tradeId.equals(regTradeData.getTradeId()))
                {
                    return regTradeData;
                }
            }
        }
        return null;
    }

    public void setMainRegData(RegTradeData mainRegData)
    {
        this.mainRegData = mainRegData;
    }

    public void setOtherRegData(List<RegTradeData> otherRegData)
    {
        this.otherRegData = otherRegData;
    }

    public void setOtherRegData(RegTradeData... regTradeDatas)
    {
        for (RegTradeData regTradeData : regTradeDatas)
        {
            this.otherRegData.add(regTradeData);
        }
    }
}
