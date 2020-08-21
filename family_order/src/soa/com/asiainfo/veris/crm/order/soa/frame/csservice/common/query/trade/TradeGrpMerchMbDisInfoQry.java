package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * @author chenmw
 * @Date 2016-11-28
 */
public class TradeGrpMerchMbDisInfoQry{
    //private static final Logger log = Logger.getLogger(TradeGrpMerchMbDisInfoQry.class);

	/**
     * 根据tradeId查询特殊业务台账记录
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeGrpMerchMbDisByTradeId(String tradeId) throws Exception{
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_GRP_MERCH_MB_DIS", "SEL_BY_PK", params,Route.getJourDb());
    }
    /**
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeGrpMerchMbByTradeId(String tradeId) throws Exception{
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_ECRECEP_MEB", "SEL_BY_TRADEID", params,Route.getJourDb());
    }
}

