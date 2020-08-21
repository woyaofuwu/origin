
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeSaleDepositInfoQry
{
    /**
     * 获取赠送子台帐
     * 
     * @param trade_id
     * @param discnt_gift_id
     * @param modify_tag
     * @return
     * @throws Exception
     */
    public static IDataset getSaleDepositByTradeGiftMtag(String trade_id, String discnt_gift_id, String modify_tag) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);
        inparams.put("DISCNT_GIFT_ID", discnt_gift_id);
        inparams.put("MODIFY_TAG", modify_tag);

        return Dao.qryByCode("TF_B_TRADE_SALE_DEPOSIT", "SEL_BY_DEPOSIT_DISCNTID", inparams, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    /**
     * 根据tradeId查询所有的用户活动预存台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeSaleDepositByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_SALE_DEPOSIT", "SEL_ALL_BY_TRADE", params, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset getTransSaleDepositByTradeId(String tradeId, String modifyTag, String startDate, String endDate) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        inparams.put("MODIFY_TAG", modifyTag);
        inparams.put("START_DATE", startDate);
        inparams.put("END_DATE", endDate);
        return Dao.qryByCode("TF_B_TRADE_SALE_DEPOSIT", "SEL_TRANS_DEPOSIT", inparams);
    }
}
