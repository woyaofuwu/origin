
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeSaleGoodsInfoQry
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
    public static IDataset getTradeSaleGoods(String tradeId, String modifyTag) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        inparams.put("MODIFY_TAG", modifyTag);

        return Dao.qryByCode("TF_B_TRADE_SALE_GOODS", "SEL_BY_TRADE_ID", inparams, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    public static IDataset getTradeSaleGoodsByResTypeCode(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("MODIFY_TAG", "0");
        params.put("RES_TYPE_CODE", "4");// 手机终端

        return Dao.qryByCode("TF_B_TRADE_SALE_GOODS", "SEL_BY_RESTYPECODE", params, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    /**
     * 根据tradeId查询所有的用户活动实物台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeSaleGoodsByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_SALE_GOODS", "SEL_ALL_BY_TRADE", params, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset getTransTradeSaleGoods(String tradeId, String modifyTag, String startDate, String endDate) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        inparams.put("MODIFY_TAG", modifyTag);
        inparams.put("START_DATE", startDate);
        inparams.put("END_DATE", endDate);

        return Dao.qryByCode("TF_B_TRADE_SALE_GOODS", "SEL_TRANS_GOODS", inparams);
    }

}
