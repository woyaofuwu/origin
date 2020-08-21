
package com.asiainfo.veris.crm.order.web.person.cancelchangeproduct;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: TradeInfo.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: liuke
 * @date: 上午11:16:20 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-8-17 liuke v1.0.0 修改原因
 */
public abstract class TradeInfo extends PersonBasePage
{
    /**
     * 初始化查询宽带台账详细信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void initQueryCancelTrade(IRequestCycle cycle) throws Exception
    {
        IData input = new DataMap();
        input.put("TRADE_ID", getData().getString("TRADE_ID"));
        input.put("CANCEL_TAG", "0");
        input.put(Route.ROUTE_EPARCHY_CODE, getData().getString("EPARCHY_CODE"));
        IDataset tradeInfos = CSViewCall.call(this, "CS.TradeInfoQrySVC.queryCancelTradeByTradeId", input);
        if (IDataUtil.isNotEmpty(tradeInfos))
        {
            setTrade(tradeInfos.getData(0));
        }
    }

    /**
     * 初始化查询台账详细信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void initQueryTrade(IRequestCycle cycle) throws Exception
    {
        IData input = new DataMap();
        input.put("TRADE_ID", getData().getString("TRADE_ID"));
        input.put("CANCEL_TAG", "0");
        input.put(Route.ROUTE_EPARCHY_CODE, getData().getString("EPARCHY_CODE"));
        IDataset tradeInfos = CSViewCall.call(this, "CS.TradeHistoryInfoQrySVC.queryTradeHistoryInfos", input);
        if (IDataUtil.isNotEmpty(tradeInfos))
        {
            setTrade(tradeInfos.getData(0));
        }

        // 产品变更信息
        IDataset tradeProductInfos = CSViewCall.call(this, "SS.CancelChangeProductSVC.queryTradeProductInfos", input);
        if (IDataUtil.isNotEmpty(tradeProductInfos))
        {
            setTradeProductInfos(tradeProductInfos);
        }

        // 服务变更信息
        IDataset tradeSvcInfos = CSViewCall.call(this, "SS.CancelChangeProductSVC.queryTradeSvcInfos", input);
        if (IDataUtil.isNotEmpty(tradeSvcInfos))
        {
            setTradeSvcInfos(tradeSvcInfos);
        }

        // 优惠变更信息
        IDataset tradeDiscntInfos = CSViewCall.call(this, "SS.CancelChangeProductSVC.queryTradeDiscntInfos", input);
        if (IDataUtil.isNotEmpty(tradeDiscntInfos))
        {
            setTradeDiscntInfos(tradeDiscntInfos);
        }

    }

    public abstract void setTrade(IData trade);

    public abstract void setTradeDiscntInfos(IDataset tradeDiscntInfos);

    public abstract void setTradeProductInfos(IDataset tradeProductInfos);

    public abstract void setTradeSvcInfos(IDataset tradeSvcInfos);
}
