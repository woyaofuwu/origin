
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;

public class TradeTwoCheckQry
{

    public static IDataset queryDataMapByRequestId(String flowId) throws Exception
    {
        IData data = new DataMap();
        data.put("REQUEST_ID", flowId);

        // 最后2位为月份
        String acctMonth = flowId.substring(flowId.length() - 2);

        data.put("ACCEPT_MONTH", acctMonth);

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT PRE_TYPE,SVC_NAME, ACCEPT_DATA1, ");
        parser.addSQL(" ACCEPT_DATA2, ");
        parser.addSQL(" ACCEPT_DATA3, ");
        parser.addSQL(" ACCEPT_DATA4, ");
        parser.addSQL(" ACCEPT_DATA5, ");
        parser.addSQL(" RSRV_STR1, ");
        parser.addSQL(" ACCEPT_STATE ");
        parser.addSQL(" FROM TF_B_ORDER_PRE  ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND REQUEST_ID = :REQUEST_ID ");
        parser.addSQL(" AND ACCEPT_MONTH = :ACCEPT_MONTH ");
        parser.addSQL(" AND SYSDATE BETWEEN START_DATE AND END_DATE ");//dujt  判断是否生效时间限制
        // parser.addSQL(" AND ACCEPT_MONTH =TO_NUMBER(SUBSTR(:REQUEST_ID,9,2)) ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset querySecondConfirm(IData data, Pagination pagination) throws Exception
    {

        /**
         * input-->data cond_START_DATE cond_END_DATE cond_SERIAL_NUMBER cond_REPLY_STATE cond_ACCEPT_TIME_DAY
         * cond_ACCEPT_TIME_MONTH QUERY_FLAG
         */

        SQLParser parser = new SQLParser(data);

        parser.addSQL("  SELECT    ORDER_ID, ");
        parser.addSQL(" ACCEPT_MONTH, ");
        parser.addSQL(" START_DATE, ");
        parser.addSQL(" TRADE_TYPE_CODE, ");
        parser.addSQL(" SERIAL_NUMBER, ");
        parser.addSQL(" REPLY_TIME, ");
        parser.addSQL(" REPLY_STATE, ");
        parser.addSQL(" REMARK ");
        parser.addSQL(" FROM TF_B_ORDER_PRE  ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND REPLY_STATE = :REPLY_STATE ");
        parser.addSQL(" AND ACCEPT_MONTH = :ACCEPT_MONTH ");
        parser.addSQL(" AND START_DATE >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
        parser.addSQL(" AND START_DATE <= TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') + 1 ");

        IDataset dataset = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isEmpty(dataset))
            return dataset;

        for (int i = 0; i < dataset.size(); i++)
        {
            IData result = dataset.getData(i);
            result.put("TRADE_TYPE", UTradeTypeInfoQry.getTradeTypeName(result.getString("TRADE_TYPE_CODE")));
        }
        return dataset;
    }

    public static IDataset querySecondConfirm(IData data, Pagination pagination, String routeId) throws Exception
    {

        return Dao.qryByCode("TF_B_TRADE_TWOCHECK", "SEL_BY_SN", data, pagination, Route.getJourDb(Route.CONN_CRM_CG));

    }

    public static IDataset querySecondConfirmFromALL(IData data, Pagination pagination) throws Exception
    {

        return null;

    }

    public static IDataset querySecondConfirmFromALL(IData data, Pagination pagination, String routeId) throws Exception
    {

        return Dao.qryByCode("TF_B_ORDER_PRE", "SEL_ALL_BY_SN", data, pagination, Route.getJourDb(Route.CONN_CRM_CG));

    }

    public static IDataset querySecondConfirmFromBH(IData data, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT ORDER_ID, ");
        parser.addSQL(" ACCEPT_MONTH, ");
        parser.addSQL(" START_DATE, ");
        parser.addSQL(" TRADE_TYPE_CODE, ");
        parser.addSQL(" SERIAL_NUMBER, ");
        parser.addSQL(" REPLY_TIME, ");
        parser.addSQL(" REPLY_STATE, ");
        parser.addSQL(" REMARK ");
        parser.addSQL(" FROM TF_BH_ORDER_PRE ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND REPLY_STATE = :REPLY_STATE ");
        parser.addSQL(" AND ACCEPT_MONTH = :ACCEPT_MONTH ");
        parser.addSQL(" AND START_DATE >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
        parser.addSQL(" AND START_DATE <= TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') + 1 ");

        IDataset dataset = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isEmpty(dataset))
            return dataset;

        for (int i = 0; i < dataset.size(); i++)
        {
            IData result = dataset.getData(i);
            result.put("TRADE_TYPE", UTradeTypeInfoQry.getTradeTypeName(result.getString("TRADE_TYPE_CODE")));
        }
        return dataset;

    }

    public static IDataset querySecondConfirmFromBH(IData data, Pagination pagination, String routeId) throws Exception
    {

        return Dao.qryByCode("TF_BH_ORDER_PRE", "SEL_BH_BY_SN", data, pagination, Route.getJourDb(Route.CONN_CRM_CG));

    }

    public static IDataset queryTwoCheckInfoByRequestId(String request_id) throws Exception
    {
        IData data = new DataMap();
        data.put("REQUEST_ID", request_id);

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM TF_B_ORDER_PRE  ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND REQUEST_ID = :REQUEST_ID ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
}
