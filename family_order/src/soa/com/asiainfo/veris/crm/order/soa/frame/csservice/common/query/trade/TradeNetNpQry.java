
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeNetNpQry
{

    /**
     * 根据tradeId查询所有的用户携转备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakNetNpByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_NETNP_BAK", "SEL_ALL_BAK_BY_TRADE", params);
    }

    /**
     * 根据业务类型获取客户历史携出工单
     * 
     * @param sn
     *            手机号码
     * @param routeEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getInNpBhTradeInfo(String sn, String tradeTypeCode, String routeEparchyCode) throws Exception
    {

        IData iparams = new DataMap();
        iparams.put("SERIAL_NUMBER", sn);
        iparams.put("TRADE_TYPE_CODE", tradeTypeCode);

        return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_SN_TRADE_TYPE_CODE", iparams, routeEparchyCode);
    }

    /**
     * 根据业务类型获取客户携出申请工单
     * 
     * @param sn
     *            手机号码
     * @param routeEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getInNpTradeInfo(String sn, String tradeTypeCode, String routeEparchyCode) throws Exception
    {

        IData iparams = new DataMap();
        iparams.put("SERIAL_NUMBER", sn);
        iparams.put("CANCEL_TAG", "0");
        iparams.put("TRADE_TYPE_CODE", tradeTypeCode);
        iparams.put("SUBSCRIBE_TYPE", "0");

        return Dao.qryByCode("TF_B_TRADE", "SEL_BY_SN_TRADE_TYPE_CODE", iparams, routeEparchyCode);
    }

    /**
     * 查询携转信息
     * 
     * @param sn
     * @return
     * @throws Exception
     */
    public static IDataset getRouteInInfo(String sn, String routeEparchyCode) throws Exception
    {
        IData iparams = new DataMap();
        iparams.put("SERIAL_NUMBER", sn);
        iparams.put("CANCEL_TAG", "0");
        return Dao.qryByCode("TF_B_TRADE_NETNP", "SEL_ROUTE_IN", iparams, routeEparchyCode);
    }

    /**
     * 根据tradeId查询所有的用户携转台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeNetNpByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_NETNP", "SEL_BY_TRADEID", params);
    }

    /**
     * 根据tradeId查询所有的用户携转台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeNetNpByTradeId(String tradeId, String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_NETNP", "SEL_BY_TRADEID", params, eparchyCode);
    }
}
