
package com.asiainfo.veris.crm.iorder.web.person.cancelchangeproduct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

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
public abstract class TradeInfoNew extends PersonBasePage
{
    /**
     * 初始化查询宽带台账详细信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void initQueryCancelTrade(IRequestCycle cycle) throws Exception {
        IData input = new DataMap();
        input.put("TRADE_ID", getData().getString("TRADE_ID"));
        input.put("CANCEL_TAG", "0");
        input.put(Route.ROUTE_EPARCHY_CODE, getData().getString("EPARCHY_CODE"));
        IDataset tradeInfos = CSViewCall.call(this, "CS.TradeInfoQrySVC.queryCancelTradeByTradeId", input);
        if (IDataUtil.isNotEmpty(tradeInfos)) {
            setTrade(tradeInfos.getData(0));
        }
    }

    /**
     * 初始化查询台账详细信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void initQueryTrade(IRequestCycle cycle) throws Exception {
        IData input = new DataMap();
        input.put("TRADE_ID", getData().getString("TRADE_ID"));
        input.put("CANCEL_TAG", "0");
        input.put(Route.ROUTE_EPARCHY_CODE, getData().getString("EPARCHY_CODE"));
        IDataset tradeInfos = CSViewCall.call(this, "CS.TradeHistoryInfoQrySVC.queryTradeHistoryInfos", input);
        if (IDataUtil.isNotEmpty(tradeInfos)) {
            for (Object obj : tradeInfos) {
                IData trade = (IData) obj;
                String brandName = UpcViewCall.getBrandNameByBrandCode(this, trade.getString("BRAND_CODE", ""));
                String productName = UpcViewCall.queryOfferNameByOfferId(this, PersonConst.ELEMENT_TYPE_CODE_PRODUCT, trade.getString("PRODUCT_ID", ""));
                trade.put("BRAND_NAME", brandName);
                trade.put("PRODUCT_NAME", productName);
            }
            setTrade(tradeInfos.getData(0));
        }

        // 产品变更信息
        IDataset tradeProductInfos = CSViewCall.call(this, "SS.CancelChangeProductSVC.queryTradeProductInfos", input);
        if (IDataUtil.isNotEmpty(tradeProductInfos)) {
            for (Object obj : tradeProductInfos) {
                IData info = (IData) obj;
                String productName = UpcViewCall.queryOfferNameByOfferId(this, PersonConst.ELEMENT_TYPE_CODE_PRODUCT, info.getString("PRODUCT_ID", ""));
                String brandName = UpcViewCall.getBrandNameByBrandCode(this, info.getString("BRAND_CODE", ""));
                String oldProductName = UpcViewCall.queryOfferNameByOfferId(this, PersonConst.ELEMENT_TYPE_CODE_PRODUCT, info.getString("OLD_PRODUCT_ID", ""));
                String oldBrandName = UpcViewCall.getBrandNameByBrandCode(this, info.getString("OLD_BRAND_CODE", ""));
                info.put("PRODUCT_NAME", productName);
                info.put("BRAND_NAME", brandName);
                info.put("OLD_PRODUCT_NAME", oldProductName);
                info.put("OLD_BRAND_NAME", oldBrandName);
            }
            setTradeProductInfos(tradeProductInfos);
        }

        // 服务变更信息
        IDataset tradeSvcInfos = CSViewCall.call(this, "SS.CancelChangeProductSVC.queryTradeSvcInfos", input);
        if (IDataUtil.isNotEmpty(tradeSvcInfos)) {
            for (Object obj : tradeSvcInfos) {
                IData info = (IData) obj;
                String serviceName = UpcViewCall.queryOfferNameByOfferId(this, PersonConst.ELEMENT_TYPE_CODE_SVC, info.getString("SERVICE_ID", ""));
                info.put("SERVICE_NAME", serviceName);
            }
            setTradeSvcInfos(tradeSvcInfos);
        }

        // 优惠变更信息
        IDataset tradeDiscntInfos = CSViewCall.call(this, "SS.CancelChangeProductSVC.queryTradeDiscntInfos", input);
        if (IDataUtil.isNotEmpty(tradeDiscntInfos)) {
            for (Object obj : tradeDiscntInfos) {
                IData info = (IData) obj;
                String discntName = UpcViewCall.queryOfferNameByOfferId(this, PersonConst.ELEMENT_TYPE_CODE_DISCNT, info.getString("DISCNT_CODE", ""));
                info.put("DISCNT_NAME", discntName);
            }
            setTradeDiscntInfos(tradeDiscntInfos);
        }
    }

    public abstract void setTrade(IData trade);

    public abstract void setTradeDiscntInfos(IDataset tradeDiscntInfos);

    public abstract void setTradeProductInfos(IDataset tradeProductInfos);

    public abstract void setTradeSvcInfos(IDataset tradeSvcInfos);
}
