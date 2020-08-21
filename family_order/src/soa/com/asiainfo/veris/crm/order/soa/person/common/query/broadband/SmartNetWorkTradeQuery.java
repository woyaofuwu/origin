
package com.asiainfo.veris.crm.order.soa.person.common.query.broadband;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SmartNetWorkTradeQuery
{
    public static IDataset queryUserCancelTrade(String serialNumber, String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_TYPE_CODE", "870");
        return Dao.qryByCode("TF_B_TRADE", "SEL_BY_TRADETYPECODE_SN", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    public static IDataset queryWidenet(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_WIDENET", "SEL_BY_ADDRESS_TRADEID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
    
    public static IDataset queryWidenetTrade(String cityCode, String serialnumber, String tradeId, String subscribeType, String subscribeState, String startDate, String endDate, Pagination pagination) throws Exception
    {
        IData inData = new DataMap();
        inData.put("INSTALL_ADDRESS_CITY_CODE", cityCode);
        inData.put("SERIAL_NUMBER", serialnumber);
        inData.put("TRADE_ID", tradeId);
        inData.put("SUBSCRIBE_TYPE", subscribeType);
        inData.put("SUBSCRIBE_STATE", subscribeState);
        inData.put("ACCEPT_START_DATE", startDate);
        inData.put("ACCEPT_END_DATE", endDate);
        SQLParser parser = new SQLParser(inData);
        parser.addSQL("SELECT S.REMARK SUBSCRIBE_TYPE_NAME, S.DATA_NAME SUBSCRIBE_STATE_NAME, A.DATA_NAME INSTALL_ADDRESS_CITY_NAME, P.TRADE_ID, ");
        parser.addSQL("  P.SERIAL_NUMBER, P.INSTALL_ADDRESS, P.INSTALL_DETAIL_ADDRESS,P.CUST_NAME,P.CUST_CONTACT,P.CUST_CONTACT_PHONE,P.ACCEPT_MONTH,P.ACCEPT_DATE,P.TRADE_STAFF_ID,P.FINISH_DATE ");
        parser.addSQL(" FROM TF_F_PBOSS_TRADE P, TD_S_STATIC S, TD_S_STATIC A");
        parser.addSQL(" WHERE S.TYPE_ID = P.SUBSCRIBE_TYPE AND S.DATA_ID = P.SUBSCRIBE_STATE AND A.TYPE_ID = 'JOB_CALL_CITYCODE' AND P.INSTALL_ADDRESS_CITY_CODE = A.DATA_ID");
        parser.addSQL(" AND P.INSTALL_ADDRESS_CITY_CODE=:INSTALL_ADDRESS_CITY_CODE");
        parser.addSQL(" AND P.SERIAL_NUMBER=:SERIAL_NUMBER");
        parser.addSQL(" AND P.TRADE_ID=:TRADE_ID");
        parser.addSQL(" AND P.SUBSCRIBE_TYPE=:SUBSCRIBE_TYPE");
        parser.addSQL(" AND P.SUBSCRIBE_STATE=:SUBSCRIBE_STATE");
        parser.addSQL(" AND P.ACCEPT_MONTH=TO_NUMBER(TO_CHAR(TO_DATE(:ACCEPT_START_DATE, 'YYYY-MM-DD HH24:MI:SS'),'MM'))");
        parser.addSQL(" AND P.ACCEPT_DATE>=TO_DATE(:ACCEPT_START_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        parser.addSQL(" AND P.ACCEPT_DATE<=TO_DATE(:ACCEPT_END_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        return Dao.qryByParse(parser, pagination);
    }
    
    public static IDataset queryStaffB(IData data, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TD_M_STAFF", "SEL_BY_BSTAFFINFO", data, pagination, Route.CONN_CRM_CEN);
    }
    
    public static IDataset queryStaffInfo(IData data) throws Exception
    {
        return Dao.qryByCode("TD_M_STAFF", "SEL_BY_STAFFINFO", data, Route.CONN_CRM_CEN);
    }
}
