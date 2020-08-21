
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeFeeTaxInfoQry
{

    public static IDataset getTradeOperFeeTaxInfos(IData inparams) throws Exception
    {

        return Dao.qryByCode("TF_B_TRADEFEE_TAX", "SEL_BY_TRADEID_MODE", inparams);
    }

    public static IDataset getTradeOperFeeTaxInfosForCG(IData inparams) throws Exception
    {

        return Dao.qryByCode("TF_B_TRADEFEE_TAX", "SEL_BY_TRADEID_MODE", inparams, Route.CONN_CRM_CG);
    }

    /**
     * 根据TRADE_ID查询增值税信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeFeeTaxByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();

        param.put("TRADE_ID", tradeId);

        return Dao.qryByCode("TF_B_TRADEFEE_TAX", "SEL_BY_TRADE_ID", param,Route.getJourDb(Route.CONN_CRM_CG));
    }

}
