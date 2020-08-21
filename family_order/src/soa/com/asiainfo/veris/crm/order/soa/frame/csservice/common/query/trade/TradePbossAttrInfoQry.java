
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradePbossAttrInfoQry
{

    /**
     * 查询服开返回新的安装地址
     * 
     * @author chenzm
     * @param tradeId
     * @param attr
     * @return
     * @throws Exception
     */
    public static IDataset getTradePbossAttr(String tradeId, String attr) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        params.put("ATTR_CODE", attr);
        return Dao.qryByCode("TF_B_TRADEMGRPBOSS_ATTR", "SEL_BY_ID_CODE", params, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    public static IDataset getTradePbossAttrByTrade(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADEMGRPBOSS_ATTR", "SEL_TRADEATTR_BY_TRADE", params, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
}
