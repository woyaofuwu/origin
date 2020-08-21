
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeUserDAO
{
    /**
     * 对于trade_type_code是415(跨区入网服务确认)的业务，需要展示详细信息
     * 
     * @param param
     * @return
     */
    public IDataset getNetServiceInfos(IData param, Pagination pagination) throws Exception
    {
        String trade_id = param.getString("TRADE_ID", "");
        if ("".equals(trade_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" Select rsrv_str1, rsrv_str2, rsrv_str3, decode(rsrv_str4,'0','发起方','1','落地方','') rsrv_str4 From tf_bh_trade ");
        parser.addSQL(" Where trade_id=:TRADE_ID");
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 查询用户相关信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset getUserInfos(IData param) throws Exception
    {
        String trade_id = param.getString("TRADE_ID", "");
        if ("".equals(trade_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from TF_B_TRADE_USER ");
        parser.addSQL(" where TRADE_ID = :TRADE_ID ");
        parser.addSQL(" AND ACCEPT_MONTH = :ACCEPT_MONTH ");
        return Dao.qryByParse(parser, Route.getJourDb());
    }

    /**
     * 查询用户相关信息 历史库
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset getUserInfosByCg(IData param) throws Exception
    {
        String trade_id = param.getString("TRADE_ID", "");
        if ("".equals(trade_id))
        {
            return new DatasetList();
        }
        String year_id = param.getString("QUERY_YEAR","");
        if(StringUtils.isBlank(year_id)){
        	return new DatasetList();
        }
        String tableName = "TF_BHB_TRADE_USER_"+year_id;
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from "+tableName+" ");
        parser.addSQL(" where TRADE_ID = :TRADE_ID");
        return Dao.qryByParse(parser, Route.CONN_CRM_HIS);
    }
    
    public IDataset getTradeInfosByHis(IData param) throws Exception
    {
    	String trade_id = param.getString("TRADE_ID", "");
        if ("".equals(trade_id))
        {
            return new DatasetList();
        }
        String year_id = param.getString("QUERY_YEAR","");
        if(StringUtils.isBlank(year_id)){
        	return new DatasetList();
        }
        String tableName = "TF_BHB_TRADE_"+year_id;
        SQLParser sql = new SQLParser(param);
        sql.addSQL("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID, A.ACCEPT_MONTH, ");
        sql.addSQL("TO_CHAR(A.BATCH_ID) BATCH_ID, TO_CHAR(A.ORDER_ID) ORDER_ID, ");
        sql.addSQL("TO_CHAR(A.PROD_ORDER_ID) PROD_ORDER_ID, A.BPM_ID, ");
        sql.addSQL("TO_CHAR(A.CAMPN_ID) CAMPN_ID, A.TRADE_TYPE_CODE, A.PRIORITY, ");
        sql.addSQL("A.SUBSCRIBE_TYPE, A.SUBSCRIBE_STATE, A.NEXT_DEAL_TAG, A.IN_MODE_CODE, ");
        sql.addSQL("TO_CHAR(A.CUST_ID) CUST_ID, A.CUST_NAME, TO_CHAR(A.USER_ID) USER_ID, ");
        sql.addSQL("TO_CHAR(A.ACCT_ID) ACCT_ID, A.SERIAL_NUMBER, A.NET_TYPE_CODE, ");
        sql.addSQL("A.EPARCHY_CODE, A.CITY_CODE, A.PRODUCT_ID, A.BRAND_CODE, ");
        sql.addSQL("TO_CHAR(A.CUST_ID_B) CUST_ID_B, TO_CHAR(A.USER_ID_B) USER_ID_B, ");
        sql.addSQL("TO_CHAR(A.ACCT_ID_B) ACCT_ID_B, A.SERIAL_NUMBER_B, A.CUST_CONTACT_ID, ");
        sql.addSQL("A.SERV_REQ_ID, A.INTF_ID, ");
        sql.addSQL("TO_CHAR(A.ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE, ");
        sql.addSQL("A.TRADE_STAFF_ID, A.TRADE_DEPART_ID, A.TRADE_CITY_CODE, ");
        sql.addSQL("A.TRADE_EPARCHY_CODE, A.TERM_IP, TO_CHAR(A.OPER_FEE) OPER_FEE, ");
        sql.addSQL("TO_CHAR(A.FOREGIFT) FOREGIFT, TO_CHAR(A.ADVANCE_PAY) ADVANCE_PAY, ");
        sql.addSQL("A.INVOICE_NO, A.FEE_STATE, ");
        sql.addSQL("TO_CHAR(A.FEE_TIME, 'YYYY-MM-DD HH24:MI:SS') FEE_TIME, A.FEE_STAFF_ID, ");
        sql.addSQL("A.PROCESS_TAG_SET, A.OLCOM_TAG, ");
        sql.addSQL("TO_CHAR(A.FINISH_DATE, 'YYYY-MM-DD HH24:MI:SS') FINISH_DATE, ");
        sql.addSQL("TO_CHAR(A.EXEC_TIME, 'YYYY-MM-DD HH24:MI:SS') EXEC_TIME, A.EXEC_ACTION, ");
        sql.addSQL("A.EXEC_RESULT, A.EXEC_DESC, A.CANCEL_TAG, ");
        sql.addSQL("TO_CHAR(A.CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS') CANCEL_DATE, ");
        sql.addSQL("A.CANCEL_STAFF_ID, A.CANCEL_DEPART_ID, A.CANCEL_CITY_CODE, ");
        sql.addSQL("A.CANCEL_EPARCHY_CODE, ");
        sql.addSQL("TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        sql.addSQL("A.UPDATE_STAFF_ID, A.UPDATE_DEPART_ID, A.REMARK, A.RSRV_STR1, A.RSRV_STR2, ");
        sql.addSQL("A.RSRV_STR3, A.RSRV_STR4, A.RSRV_STR5, A.RSRV_STR6, A.RSRV_STR7, ");
        sql.addSQL("A.RSRV_STR8, A.RSRV_STR9, A.RSRV_STR10, PF_WAIT ");
        sql.addSQL("FROM "+tableName+" A ");
        sql.addSQL("WHERE A.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        sql.addSQL("AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        sql.addSQL("AND A.CANCEL_TAG = 0 ");
        return Dao.qryByParse(sql, Route.CONN_CRM_HIS);
    }

}
