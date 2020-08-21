
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradePbossFinishInfoQry
{

    /**
     * 查询PBOSS竣工时间
     * 
     * @author chenzm
     * @param tradeId
     * @param cancel_tag
     * @return
     * @throws Exception
     */
    public static IDataset getTradePbossFinish(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_PBOSS_FINISH", "SEL_BY_TRADEID", params, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

}
