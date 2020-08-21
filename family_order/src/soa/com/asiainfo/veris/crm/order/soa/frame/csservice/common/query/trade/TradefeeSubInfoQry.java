
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradefeeSubInfoQry
{
    /**
     * 根据业务流水号，获取台账费用列表
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getFeeListByTrade(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        IDataset feeList = Dao.qryByCode("TF_B_TRADEFEE_SUB", "SEL_BY_TRADE_ID", param,Route.getJourDb());
        if (null == feeList)
        {
            return new DatasetList();
        }
        return feeList;
    }

    /**
     * 查询费用子台帐
     * 
     * @param trade_id
     * @param fee_mode
     *            费用类型
     * @return
     * @throws Exception
     */
    public static IDataset getTradefeeSubByTradeMode(String trade_id, String fee_mode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("FEE_MODE", fee_mode);
        return Dao.qryByCode("TF_B_TRADEFEE_SUB", "SEL_BY_TRADE_FEEMOD", param, Route.getJourDb());
    }

    /**
     * 根据业务流水号，受理月份业务台帐费用子表信息
     * 
     * @param trade_id
     *            业务流水号
     * @param accept_month
     *            受理月份
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getTradefeeSubInfoByPk(String trade_id, String accept_month, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("ACCEPT_MONTH", accept_month);
        return Dao.qryByCode("TF_B_TRADEFEE_SUB", "SEL_BY_TRADE_1", param, pagination, Route.getJourDb());
    }

    /**
     * 查询台账费用信息
     * 
     * @param tradeId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeFeeSubByTradeId(String tradeId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        return Dao.qryByCode("TF_B_TRADEFEE_SUB", "SEL_BY_TRADE_ID", param, Route.getJourDb(routeId));
    }
    
    public static IDataset getTradeFeeSubByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        return Dao.qryByCode("TF_B_TRADEFEE_SUB", "SEL_FEE_BY_TRADE_ID", param, Route.getJourDb());
    }
}
