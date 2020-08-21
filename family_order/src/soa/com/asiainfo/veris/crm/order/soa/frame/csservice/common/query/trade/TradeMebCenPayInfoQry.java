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
public class TradeMebCenPayInfoQry{
    //private static final Logger log = Logger.getLogger(TradeMebCenPayInfoQry.class);

	/**
     * 根据tradeId查询特殊业务台账记录
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeMebCenPayByTradeId(String tradeId) throws Exception{
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_MEB_CENPAY", "SEL_TRD_ID", params,Route.getJourDb());
    }
}

