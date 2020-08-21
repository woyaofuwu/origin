
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class TradeAcctInfoQry
{
    /**
     * 根据tradeId查询所有的账户信息备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakAccountByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_ACCOUNT_BAK", "SEL_ALL_BAK_BY_TRADE", params);
    }

    // todo
    /**
     * 获取业务台帐帐户资料
     * 
     * @param iData
     * @return
     */
    public static IDataset getTradeAccount(String tradeId, Pagination pagination) throws Exception
    {

        if (tradeId == null || "".equals(tradeId))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_84);
        }
        IData params = new DataMap();
        params.put("VTRADE_ID", tradeId);
        try
        {
            IDataset iDataset = Dao.qryByCodeParser("TF_B_TRADE_ACCOUNT", "SEL_TRADE_ACCOUNT", params, pagination, Route.CONN_CRM_CEN);
            return iDataset;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            CSAppException.apperr(TradeException.CRM_TRADE_94);
            return null;
        }
    }

    /**
     * 根据tradeId查询所有的账户信息台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeAccountByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_ACCOUNT", "SEL_TRADE_ACCOUNT", params, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    public static void updateStartDate(String tradeId, String startDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("OPEN_DATE", startDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_ACCOUNT", "UPD_STARTCYCLEID", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    public static IDataset getFreePayTradeAccountByCustId(String custId,String acctId) throws Exception
    {
        IData params = new DataMap();
        params.put("CUST_ID", custId);
        params.put("ACCT_ID", acctId);
        SQLParser parser = new SQLParser(params);
        parser.addSQL("SELECT COUNT(1) AS COUNT FROM TF_F_ACCOUNT t ");
        parser.addSQL("WHERE t.cust_id =:CUST_ID ");
        parser.addSQL("AND t.acct_id =:ACCT_ID ");
        parser.addSQL("AND t.rsrv_str5 = 'FreePayProduct' ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }
    
    /**
     * 集团统一付费产品专有账户查询
     * @param custId
     * @param acctId
     * @return
     * @throws Exception
     */
    public static IDataset getUnifyPayTradeAcctByCustId(String custId,String acctId) throws Exception
    {
    	IData params = new DataMap();
        params.put("CUST_ID", custId);
        params.put("ACCT_ID", acctId);
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT COUNT(1) AS COUNT FROM TF_F_ACCOUNT T ");
        parser.addSQL(" WHERE T.CUST_ID = :CUST_ID ");
        parser.addSQL(" 	AND T.ACCT_ID = :ACCT_ID ");
        parser.addSQL(" 	AND T.REMOVE_TAG = '0' ");
        parser.addSQL(" 	AND T.RSRV_STR5 = 'UnifyPayProduct' ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }
    
    /**
     * 集团客户预缴款(虚拟)产品专有账户查询
     * @param custId
     * @param acctId
     * @return
     * @throws Exception
     */
    public static IDataset getPrepayTradeAcctByCustId(String custId,String acctId) throws Exception
    {
    	IData params = new DataMap();
        params.put("CUST_ID", custId);
        params.put("ACCT_ID", acctId);
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT COUNT(1) AS COUNT FROM TF_F_ACCOUNT T ");
        parser.addSQL(" WHERE T.CUST_ID = :CUST_ID ");
        parser.addSQL(" 	AND T.ACCT_ID = :ACCT_ID ");
        parser.addSQL(" 	AND T.REMOVE_TAG = '0' ");
        parser.addSQL(" 	AND T.RSRV_STR5 = 'PrepayProduct' ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }
    
}
