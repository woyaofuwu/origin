
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeFeePayMoneyInfoQry
{

    public static String getPayFeeModeCode(String payMoneyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PAY_MONEY_CODE", payMoneyCode);
        IDataset result = Dao.qryByCodeParser("TD_SD_PAYFEEMODE", "SEL_CODE2_BY_PAYMONEYCODE", param, Route.CONN_CRM_CEN);
        if (IDataUtil.isNotEmpty(result))
        {
            return result.getData(0).getString("CODE2", "0");
        }
        else
        {
            return "";
        }
    }

    /**
     * 根据业务流水号查询业务台帐付款方式子表信息
     * 
     * @param trade_id
     *            业务流水号
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getPayMoneyByRoute(String trade_id, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        return Dao.qryByCodeParser("TF_B_TRADEFEE_PAYMONEY", "SEL_BY_TRADE", param, Route.getJourDb(eparchyCode));
    }
    
    public static IDataset queryByTradeIdPayMoneyCode(String trade_id,String payMoneyCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("PAY_MONEY_CODE", payMoneyCode);
        return Dao.qryByCodeParser("TF_B_TRADEFEE_PAYMONEY", "SEL_BY_TRADE_PAYMONEYCODE", param, Route.getJourDb(eparchyCode));
    }

    /**
     * 返回TF_B_TRADEFEE_PAYMONEY.PAY_MONEY_CODE
     * 
     * @param tradeId
     *            业务流水号
     * @return 付款类型编码
     * @throws Exception
     */
    public static String getPayMoneyCode(String tradeId) throws Exception
    {

        IData payMoneyInfo = getPayMoneyInfo(tradeId);
        String payMoneyCode = "0";
        if (payMoneyInfo != null)
        {
            payMoneyCode = payMoneyInfo.getString("PAY_MONEY_CODE");
        }
        return payMoneyCode;
    }

    /**
     * 根据业务流水号 得到该笔台账的payMoney信息
     * 
     * @param tradeId
     *            业务流水号
     * @return
     * @throws Exception
     */
    public static IData getPayMoneyInfo(String tradeId) throws Exception
    {

        IData data = null;
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        IDataset temps = Dao.qryByCode("TF_B_TRADEFEE_PAYMONEY", "SEL_BY_TRADE", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        if (!temps.isEmpty() && temps.size() > 0)
        {
            data = temps.getData(0);
        }
        return data;
    }

    /**
     * 根据业务流水号查询业务台帐付款方式子表信息
     * 
     * @param trade_id
     *            业务流水号
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getPayMoneyInfo(String trade_id, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        return Dao.qryByCodeParser("TF_B_TRADEFEE_PAYMONEY", "SEL_BY_TRADE", param, pagination,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset getPayMoneyInfoByOrderId(String orderId, String acceptMonth) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        param.put("ACCEPT_MONTH", acceptMonth);
        return Dao.qryByCodeParser("TF_B_TRADEFEE_PAYMONEY", "SEL_BY_ORDER_ID", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
}
