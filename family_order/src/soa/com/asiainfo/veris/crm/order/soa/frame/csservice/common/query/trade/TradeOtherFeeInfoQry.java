
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.bean.BizBean;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeOtherFeeInfoQry
{

    /**
     * 查询转账费用子台帐
     * 
     * @param trade_id
     * @param oper_type
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getTradeOtherFeeByPK(String trade_id, String oper_type) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("OPER_TYPE", oper_type);
        return Dao.qryByCode("TF_B_TRADEFEE_OTHERFEE", "SEL_BY_TRADE_TYPE", param,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 根据TRADE_ID查询转账费用子台帐
     * 
     * @param trade_id
     * @param oper_type
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getTradeOtherFeeByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADEFEE_OTHERFEE", "SEL_BY_TRADEID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 修改转账费用子台帐
     * 
     * @param trade_id
     * @param oper_type
     * @return IDataset
     * @throws Exception
     */
    public static void updateTradeOtherFeeByPK(String trade_id, String oper_type, String charge_id) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("OPER_TYPE", oper_type);
        param.put("REQUEST_ID", charge_id);
        Dao.executeUpdateByCodeCode("TF_B_TRADEFEE_OTHERFEE", "UPDATE_BY_TRADE_TYPE", param,Route.getJourDb(BizRoute.getRouteId()));
    }

}
