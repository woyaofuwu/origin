
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class TradeCustomerInfoQry extends CSBizBean
{
    /**
     * 根据tradeId查询所有的客户信息备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakCustomerByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_CUSTOMER", "SEL_TRADE_CUSTOMER_BAK", params);
    }

    public static IDataset getcontractSubMode(IData inparams) throws Exception
    {

        return Dao.qryByCode("TF_B_CUST_CONTACT_TRACE", "SEL_CUST_CONTACT", inparams);

    }

    public static IDataset getCustContactByCustID(String custId, Pagination pagination) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("CUST_ID", custId);
        return Dao.qryByCodeParser("TF_B_CUST_CONTACT", "SEL_BY_CUST", inparams, pagination);

    }

    public static IDataset getCustContactByPk(IData inparams, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_B_CUST_CONTACT", "SEL_BY_PK", inparams, pagination);

    }

    public static IDataset getCustContactDate(IData inparams, Pagination pagination) throws Exception
    {

        return Dao.qryByCodeParser("TF_B_CUST_CONTACT", "SEL_BY_CUST_DATE", inparams, pagination);

    }

    /**
     * 根据CUST_ID 查询该客户资料台账已经存在
     * 
     * @author lic
     * @version 20101217
     */
    public static IDataset getTradeCustInfoByCustId(IData params) throws Exception
    {

        // 根据Group_id获取客户经理信息
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT B.CUST_ID,B.CUST_NAME,B.CUST_TYPE,B.CUST_STATE,B.PSPT_TYPE_CODE,B.PSPT_ID,B.OPEN_LIMIT,B.EPARCHY_CODE,B.CITY_CODE,B.CUST_PASSWD,to_char(B.SCORE_VALUE) SCORE_VALUE,B.CREDIT_CLASS,TO_CHAR(B.BASIC_CREDIT_VALUE) BASIC_CREDIT_VALUE,TO_CHAR(B.CREDIT_VALUE) CREDIT_VALUE,B.REMOVE_TAG,to_char(B.REMOVE_DATE,'yyyy-mm-dd hh24:mi:ss') REMOVE_DATE,");
        parser.addSQL("B.DEVELOP_DEPART_ID,B.DEVELOP_STAFF_ID,B.IN_DEPART_ID,B.IN_STAFF_ID,TO_CHAR(B.IN_DATE,'YYYY-MM-DD HH24:MI:SS') IN_DATE,B.REMARK,B.RSRV_STR1,B.RSRV_STR2,B.RSRV_STR3,B.RSRV_STR4,B.RSRV_STR5,B.RSRV_STR6,B.RSRV_STR7,B.RSRV_STR8,B.RSRV_STR9,B.RSRV_STR10");
        parser.addSQL(" FROM TF_B_TRADE A,TF_B_TRADE_CUSTOMER B ");
        parser.addSQL(" Where A.TRADE_ID=B.TRADE_ID");
        parser.addSQL(" And B.MODIFY_TAG='0'");
        parser.addSQL(" AND B.CUST_ID=:CUST_ID");
        IDataset cusInfo = Dao.qryByParse(parser,Route.getJourDb());
        if (cusInfo == null)
        {
            return null;
        }
        if (cusInfo.size() == 0)
        {
            return null;
        }
        return cusInfo;
    }

    // todo
    /**
     * 根据CUST_ID 查询该客户资料台账已经存在
     * 
     * @author lic
     * @version 20101217
     */
    public static IDataset getTradeCustInfoByCustId(String custId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);

        // 根据Group_id获取客户经理信息
        SQLParser parser = new SQLParser(param);
        // String eparchyCode=params.getString("EPARCHY_CODE");

        parser
                .addSQL(" SELECT B.CUST_ID,B.CUST_NAME,B.CUST_TYPE,B.CUST_STATE,B.PSPT_TYPE_CODE,B.PSPT_ID,B.OPEN_LIMIT,B.EPARCHY_CODE,B.CITY_CODE,B.CUST_PASSWD,to_char(B.SCORE_VALUE) SCORE_VALUE,B.CREDIT_CLASS,TO_CHAR(B.BASIC_CREDIT_VALUE) BASIC_CREDIT_VALUE,TO_CHAR(B.CREDIT_VALUE) CREDIT_VALUE,B.REMOVE_TAG,to_char(B.REMOVE_DATE,'yyyy-mm-dd hh24:mi:ss') REMOVE_DATE,");
        parser
                .addSQL("B.DEVELOP_DEPART_ID,B.DEVELOP_STAFF_ID,B.IN_DEPART_ID,B.IN_STAFF_ID,TO_CHAR(B.IN_DATE,'YYYY-MM-DD HH24:MI:SS') IN_DATE,B.REMARK,B.RSRV_STR1,B.RSRV_STR2,B.RSRV_STR3,B.RSRV_STR4,B.RSRV_STR5,B.RSRV_STR6,B.RSRV_STR7,B.RSRV_STR8,B.RSRV_STR9,B.RSRV_STR10");
        parser.addSQL(" FROM  TF_B_TRADE_CUSTOMER B,TF_B_TRADE A ");
        parser.addSQL(" Where A.TRADE_ID=B.TRADE_ID");
        parser.addSQL(" And B.MODIFY_TAG='0'");
        if (!ProvinceUtil.isProvince(ProvinceUtil.XINJ))
        {
            parser.addSQL(" And A.CANCEL_TAG='0'");
        }
        parser.addSQL(" AND B.CUST_ID=:CUST_ID");
        IDataset cusInfo = Dao.qryByParse(parser, pagination);
        if (cusInfo == null)
        {
            return null;
        }
        if (cusInfo.size() == 0)
        {
            return null;
        }
        return cusInfo;
    }

    // todo
    /**
     * 获取客户台账信息
     * 
     * @param iData
     * @return
     * @throws Exception
     */
    public static IDataset getTradeCustomer(String tradeId, Pagination pagination) throws Exception
    {

        if (tradeId == null || "".equals(tradeId))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_84);
        }
        try
        {
            IData params = new DataMap();
            params.put("TRADE_ID", tradeId);
            return Dao.qryByCodeParser("TF_B_TRADE_CUSTOMER", "SEL_TRADE_CUSTOMER", params, pagination, Route.CONN_CRM_CEN);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            CSAppException.apperr(TradeException.CRM_TRADE_94);
            return null;
        }
    }

    /**
     * 根据tradeId查询所有的客户信息台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeCustomerByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
//        return Dao.qryByCodeParser("TF_B_TRADE_CUSTOMER", "SEL_TRADE_CUSTOMER", params);
        return Dao.qryByCodeParser("TF_B_TRADE_CUSTOMER", "SEL_TRADE_CUSTOMER", params,Route.getJourDb(BizRoute.getRouteId()));
        		
    }
    
    
    /**
     * 根据PSPT_ID查询该证件号码存在的未完工工单，30分钟内
     * 
     * @param PSPT_ID
     * @return
     * @throws Exception
     */
    public static IDataset getTradeByPsptId(String psptId, String month) throws Exception
    {
        IData params = new DataMap();
        params.put("PSPT_ID", psptId);
        params.put("ACCEPT_MONTH", month);
        return Dao.qryByCodeParser("TF_B_TRADE", "SEL_TRADE_BY_PSPT_ID", params, Route.getJourDb());
    }

}
