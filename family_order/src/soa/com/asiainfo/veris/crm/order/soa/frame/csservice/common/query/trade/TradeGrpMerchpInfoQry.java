
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeGrpMerchpInfoQry
{

    /**
     * 查询BBOSS产品订单号
     * 
     * @author liuxx3
     * @date 2014-08-13
     */
    public static IDataset getBBossMerchPInfoByOrderId(String orderId, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT MP.PRODUCT_ORDER_ID ");
        parser.addSQL(" FROM TF_B_TRADE_GRP_MERCHP MP,TF_B_TRADE T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.ORDER_ID = :ORDER_ID");
        parser.addSQL(" AND MP.TRADE_ID = T.TRADE_ID ");

        parser.addSQL(" UNION ALL");

        parser.addSQL(" SELECT MP.PRODUCT_ORDER_ID ");
        parser.addSQL(" FROM TF_B_TRADE_GRP_MERCHP MP,TF_BH_TRADE T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.ORDER_ID = TO_NUMBER(:ORDER_ID) ");
        parser.addSQL(" AND MP.TRADE_ID = T.TRADE_ID ");

        return Dao.qryByParse(parser, pg, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @Description:根据产品订购关系查询在线的台帐信息
     * @author ft
     * @date
     * @param productOfferId
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getMerchpOnlineByProductofferId(String productOfferId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_OFFER_ID", productOfferId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select a.trade_id, ");
        parser.addSQL("a.user_id, ");
        parser.addSQL("a.product_order_id, ");
        parser.addSQL("a.product_offer_id, ");
        parser.addSQL("b.order_id, ");
        parser.addSQL("b.cust_id, ");
        parser.addSQL("b.rsrv_str10, ");
        parser.addSQL("b.product_id, ");
        parser.addSQL("a.inst_id, ");
        parser.addSQL("a.PRODUCT_SPEC_CODE, ");
        parser.addSQL("a.GROUP_ID ");
        parser.addSQL("from tf_b_trade_grp_merchp a, tf_b_trade b ");
        parser.addSQL("where 1 = 1 ");
        parser.addSQL("and a.trade_id = b.trade_id ");
        parser.addSQL("and a.ACCEPT_MONTH = b.accept_month ");
        parser.addSQL("and PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
        parser.addSQL("and rownum = 1 ");

        return Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据产品订单号查询在想的merchp表信息
     * 
     * @author ft
     * @param productOrderId
     * @return
     * @throws Exception
     */
    public static IDataset getMerchpOnlineByProductOrderId(String productOrderId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ORDER_ID", productOrderId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT A.TRADE_ID, ");
        parser.addSQL("A.USER_ID, ");
        parser.addSQL("A.inst_id, ");
        parser.addSQL("A.PRODUCT_ORDER_ID, ");
        parser.addSQL("A.PRODUCT_OFFER_ID ");
        parser.addSQL("FROM TF_B_TRADE_GRP_MERCHP A, TF_B_TRADE B ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND A.TRADE_ID = B.TRADE_ID ");
        parser.addSQL("AND A.ACCEPT_MONTH = B.ACCEPT_MONTH ");
        parser.addSQL("AND PRODUCT_ORDER_ID = :PRODUCT_ORDER_ID ");
        parser.addSQL("AND ROWNUM = 1 ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset qryBBossAllTradeInfo(String groupId, String custName, String productSpecNumber, String ProductOrderId, String poSpecNumber, Pagination pg) throws Exception
    {
    	IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("CUST_NAME", custName);
        param.put("PRODUCTSPECNUMBER", productSpecNumber);
        param.put("PRODUCT_ORDER_ID", ProductOrderId);
        param.put("POSPECNUMBER", poSpecNumber);
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT T.PRODUCT_ORDER_ID, ");
        parser.addSQL("CASE ");
        parser.addSQL("WHEN T.MODIFY_TAG = '0' THEN ");
        parser.addSQL("'增加' ");
        parser.addSQL("WHEN T.MODIFY_TAG = '1' THEN ");
        parser.addSQL("'删除' ");
        parser.addSQL("WHEN T.MODIFY_TAG = '2' THEN ");
        parser.addSQL("'修改' ");
        parser.addSQL("END MODIFY_TAG, ");
        parser.addSQL("'等待归档' AS STATE, ");
        parser.addSQL("T.GROUP_ID, ");
        parser.addSQL("T.MERCH_SPEC_CODE, ");
        parser.addSQL("T.PRODUCT_SPEC_CODE, ");
        parser.addSQL("T1.ACCEPT_DATE, ");
        parser.addSQL("T1.FINISH_DATE, ");
        parser.addSQL("T1.TRADE_STAFF_ID, T1.CUST_NAME ");
        parser.addSQL("FROM TF_B_TRADE_GRP_MERCHP T, TF_B_TRADE T1 ");
        parser.addSQL("WHERE T.TRADE_ID = T1.TRADE_ID ");
        parser.addSQL("AND T.PRODUCT_SPEC_CODE = :PRODUCTSPECNUMBER ");
        parser.addSQL("AND T.MERCH_SPEC_CODE = :POSPECNUMBER ");
        parser.addSQL("AND T.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND T.PRODUCT_ORDER_ID = :PRODUCT_ORDER_ID ");
        parser.addSQL("AND T1.CUST_NAME LIKE '%' || :CUST_NAME || '%' ");
//        parser.addSQL("AND T2.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND SYSDATE >= T.START_DATE ");
        parser.addSQL("AND SYSDATE <= T.END_DATE ");

        parser.addSQL("UNION ALL ");

        parser.addSQL("SELECT T.PRODUCT_ORDER_ID, ");
        parser.addSQL("CASE ");
        parser.addSQL("WHEN T.MODIFY_TAG = '0' THEN ");
        parser.addSQL("'增加' ");
        parser.addSQL("WHEN T.MODIFY_TAG = '1' THEN ");
        parser.addSQL("'删除' ");
        parser.addSQL("WHEN T.MODIFY_TAG = '2' THEN ");
        parser.addSQL("'修改' ");
        parser.addSQL("END MODIFY_TAG, ");
        parser.addSQL("'处理成功' AS STATE, ");
        parser.addSQL("T.GROUP_ID, ");
        parser.addSQL("T.MERCH_SPEC_CODE, ");
        parser.addSQL("T.PRODUCT_SPEC_CODE, ");
        parser.addSQL("T1.ACCEPT_DATE, ");
        parser.addSQL("T1.FINISH_DATE, ");
        parser.addSQL("T1.TRADE_STAFF_ID, ");
        parser.addSQL("T1.CUST_NAME ");
        parser.addSQL("FROM TF_B_TRADE_GRP_MERCHP T, TF_BH_TRADE T1 ");
        parser.addSQL("WHERE T.TRADE_ID = T1.TRADE_ID ");
        parser.addSQL("AND T.PRODUCT_SPEC_CODE = :PRODUCTSPECNUMBER ");
        parser.addSQL("AND T.MERCH_SPEC_CODE = :POSPECNUMBER ");
        parser.addSQL("AND T.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND T.PRODUCT_ORDER_ID = :PRODUCT_ORDER_ID ");
        parser.addSQL("AND T1.CUST_NAME LIKE '%' || :CUST_NAME || '%' ");
//        parser.addSQL("AND T2.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND SYSDATE >= T.START_DATE ");
        parser.addSQL("AND SYSDATE <= T.END_DATE ");

        return Dao.qryByParse(parser, pg,Route.getJourDb(BizRoute.getRouteId()));
    }
    

    /**
     * 一级BBOSS业务集团客户订购处理查询 关联td_bh_trade表
     * 
     * @author liuxx3
     * @date 2014-07-08
     */
    public static IDataset qryBBossBHTradeInfo(String groupId, String custName, String productSpecNumber, String ProductOrderId, String poSpecNumber, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("CUST_NAME", custName);
        param.put("PRODUCTSPECNUMBER", productSpecNumber);
        param.put("PRODUCT_ORDER_ID", ProductOrderId);
        param.put("POSPECNUMBER", poSpecNumber);
        
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT T.PRODUCT_ORDER_ID, ");
        parser.addSQL("CASE ");
        parser.addSQL("WHEN T.MODIFY_TAG = '0' THEN ");
        parser.addSQL("'增加' ");
        parser.addSQL("WHEN T.MODIFY_TAG = '1' THEN ");
        parser.addSQL("'删除' ");
        parser.addSQL("WHEN T.MODIFY_TAG = '2' THEN ");
        parser.addSQL("'修改' ");
        parser.addSQL("END MODIFY_TAG, ");
        parser.addSQL("'处理成功' AS STATE, ");
        parser.addSQL("T.GROUP_ID, ");
        parser.addSQL("T.MERCH_SPEC_CODE, ");
        parser.addSQL("T.PRODUCT_SPEC_CODE, ");
        parser.addSQL("T1.ACCEPT_DATE, ");
        parser.addSQL("T1.FINISH_DATE, ");
        parser.addSQL("T1.TRADE_STAFF_ID, ");
        parser.addSQL("T1.CUST_NAME ");
        parser.addSQL("FROM TF_B_TRADE_GRP_MERCHP T, TF_BH_TRADE T1 ");
        parser.addSQL("WHERE T.TRADE_ID = T1.TRADE_ID ");
        parser.addSQL("AND T.PRODUCT_SPEC_CODE = :PRODUCTSPECNUMBER ");
        parser.addSQL("AND T.MERCH_SPEC_CODE = :POSPECNUMBER ");
        parser.addSQL("AND T.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND T.PRODUCT_ORDER_ID = :PRODUCT_ORDER_ID ");
        parser.addSQL("AND T1.CUST_NAME LIKE '%' || :CUST_NAME || '%' ");
//        parser.addSQL("AND T2.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND SYSDATE >= T.START_DATE ");
        parser.addSQL("AND SYSDATE <= T.END_DATE ");

        return Dao.qryByParse(parser, pg,Route.getJourDb(BizRoute.getRouteId()));
    }
    

    /**
     * @Description:一级BBOSS业务产品订购状态查询
     * @author liuxx3
     * @date 2014-07-10
     */
    public static IDataset qryBBossBizProdDgMeb(String productOfferId, String serialNumber, String custName, String pospecnumber, String productspecnumber, String state, String groupId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_OFFER_ID", productOfferId);
        param.put("CUST_NAME", custName);
        param.put("GROUP_ID", groupId);
        param.put("POSPECNUMBER", pospecnumber);
        param.put("PRODUCTSPECNUMBER", productspecnumber);
        param.put("STATE", state);
        param.put("SERIAL_NUMBER", serialNumber);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT  ");
        parser.addSQL("F.CITY_CODE, ");
        parser.addSQL("F.CUST_NAME, ");
        parser.addSQL("T.USER_ID, ");
        parser.addSQL("T.GROUP_ID, ");
        parser.addSQL("T.MERCH_SPEC_CODE, ");
        parser.addSQL("T.PRODUCT_SPEC_CODE, ");
        parser.addSQL("T.PRODUCT_OFFER_ID, ");
        parser.addSQL("T.START_DATE, ");
        parser.addSQL("T.END_DATE, ");
        parser.addSQL("T.UPDATE_STAFF_ID, ");
        parser.addSQL("CASE ");
        parser.addSQL("WHEN T.STATUS = 'A' THEN ");
        parser.addSQL("'正常订购' ");
        parser.addSQL("WHEN T.STATUS = 'N' THEN ");
        parser.addSQL("'暂停' ");
        parser.addSQL("WHEN T.STATUS = 'D' THEN ");
        parser.addSQL("'销户' ");
        parser.addSQL("END STATUS, ");
        parser.addSQL("U.CUST_ID, ");
        parser.addSQL("U.SERIAL_NUMBER ");
        parser.addSQL("FROM TF_F_CUST_GROUP F, TF_F_USER_GRP_MERCHP T, TF_F_USER U ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND F.GROUP_ID = T.GROUP_ID ");
        parser.addSQL("AND T.USER_ID = U.USER_ID ");
        parser.addSQL("AND T.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
        parser.addSQL("AND T.MERCH_SPEC_CODE = :POSPECNUMBER ");
        parser.addSQL("AND T.PRODUCT_SPEC_CODE = :PRODUCTSPECNUMBER ");
        parser.addSQL("AND T.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND F.CUST_NAME LIKE '%' || :CUST_NAME || '%' ");
        parser.addSQL("AND U.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL("AND SYSDATE >= T.START_DATE ");
        parser.addSQL("AND F.REMOVE_TAG = '0' ");
        parser.addSQL("AND U.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");

        if (StringUtils.isNotEmpty(param.getString("STATE")) && "N".equals(param.getString("STATE"))) // 暂停
        {
            parser.addSQL("AND T.status='N' ");
            parser.addSQL("AND sysdate<=t.end_date ");
        }
        else if (StringUtils.isNotEmpty(param.getString("STATE")) && "D".equals(param.getString("STATE")))// 销户
        {
            parser.addSQL("AND T.status='D' ");
        }
        else if (StringUtils.isNotEmpty(param.getString("STATE")) && "A".equals(param.getString("STATE")))// 正常商用
        {
            parser.addSQL("AND T.status='A' ");
            parser.addSQL("AND sysdate<=T.end_date ");
        }

        return Dao.qryByParse(parser, pagination);
    }
    
 
    
    /**
     * @Description:一级BBOSS业务产品订购状态查询
     * @author liuxx3
     * @date 2014-07-10
     */
    public static IDataset qryBBossCustUserInfoHAIN(String serialNumber, String custName, String groupId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_NAME", custName);
        param.put("GROUP_ID", groupId);
        param.put("SERIAL_NUMBER", serialNumber);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT  ");
        parser.addSQL("F.CITY_CODE, ");
        parser.addSQL("F.CUST_NAME, ");
        parser.addSQL("F.GROUP_ID, ");
        parser.addSQL("U.USER_ID, ");
        parser.addSQL("U.CUST_ID, ");
        parser.addSQL("U.SERIAL_NUMBER ");
        parser.addSQL("FROM TF_F_CUST_GROUP F, TF_F_USER U ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND F.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND F.CUST_ID = U.CUST_ID ");
        parser.addSQL("AND F.CUST_NAME LIKE '%' || :CUST_NAME || '%' ");
        parser.addSQL("AND U.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL("AND F.REMOVE_TAG = '0' ");
        parser.addSQL("AND U.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");

        return Dao.qryByParse(parser);
    }

    /**
     * 一级BBOSS业务集团客户订购处理查询 关联td_b_trade表
     * 
     * @author liuxx3
     * @date 2014-07-09
     */
    public static IDataset qryBBossBTradeInfo(String groupId, String custName, String productSpecNumber, String ProductOrderId, String poSpecNumber, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("CUST_NAME", custName);
        param.put("PRODUCTSPECNUMBER", productSpecNumber);
        param.put("PRODUCT_ORDER_ID", ProductOrderId);
        param.put("POSPECNUMBER", poSpecNumber);
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT T.PRODUCT_ORDER_ID, ");
        parser.addSQL("CASE ");
        parser.addSQL("WHEN T.MODIFY_TAG = '0' THEN ");
        parser.addSQL("'增加' ");
        parser.addSQL("WHEN T.MODIFY_TAG = '1' THEN ");
        parser.addSQL("'删除' ");
        parser.addSQL("WHEN T.MODIFY_TAG = '2' THEN ");
        parser.addSQL("'修改' ");
        parser.addSQL("END MODIFY_TAG, ");
        parser.addSQL("'等待归档' AS STATE, ");
        parser.addSQL("T.GROUP_ID, ");
        parser.addSQL("T.MERCH_SPEC_CODE, ");
        parser.addSQL("T.PRODUCT_SPEC_CODE, ");
        parser.addSQL("T1.ACCEPT_DATE, ");
        parser.addSQL("T1.FINISH_DATE, ");
        parser.addSQL("T1.TRADE_STAFF_ID, T1.CUST_NAME ");
        parser.addSQL("FROM TF_B_TRADE_GRP_MERCHP T, TF_B_TRADE T1 ");
        parser.addSQL("WHERE T.TRADE_ID = T1.TRADE_ID ");
        parser.addSQL("AND T.PRODUCT_SPEC_CODE = :PRODUCTSPECNUMBER ");
        parser.addSQL("AND T.MERCH_SPEC_CODE = :POSPECNUMBER ");
        parser.addSQL("AND T.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND T.PRODUCT_ORDER_ID = :PRODUCT_ORDER_ID ");
        parser.addSQL("AND T1.CUST_NAME LIKE '%' || :CUST_NAME || '%' ");
//        parser.addSQL("AND T2.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND SYSDATE >= T.START_DATE ");
        parser.addSQL("AND SYSDATE <= T.END_DATE ");

        return Dao.qryByParse(parser, pg,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    /**
     * 根据tradeId查询产品订购台账信息 2014-08-08 code_code翻译
     * 
     * @author liuxx3
     * @throws Exception
     */
    public static IDataset qryGrpMerchpByOrderId(String orderId, Pagination pg) throws Exception
    {
        IData param = new DataMap();

        param.put("ORDER_ID", orderId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select a.inst_id, ");
        parser.addSQL("TO_CHAR(a.trade_id) trade_id, ");
        parser.addSQL("a.accept_month, ");
        parser.addSQL("a.user_id, ");
        parser.addSQL("a.merch_spec_code, ");
        parser.addSQL("a.product_spec_code, ");
        parser.addSQL("a.product_order_id, ");
        parser.addSQL("a.product_offer_id, ");
        parser.addSQL("a.group_id, ");
        parser.addSQL("a.serv_code, ");
        parser.addSQL("a.biz_attr, ");
        parser.addSQL("a.status, ");
        parser.addSQL("a.start_date, ");
        parser.addSQL("a.end_date, ");
        parser.addSQL("a.modify_tag, ");
        parser.addSQL("a.update_staff_id, ");
        parser.addSQL("a.update_depart_id, ");
        parser.addSQL("a.remark ");
        parser.addSQL("FROM tf_b_trade_grp_merchp a,tf_b_trade t ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("and a.trade_id = t.TRADE_ID ");
        parser.addSQL("and t.ORDER_ID = TO_NUMBER(:ORDER_ID) ");
        parser.addSQL("and a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        parser.addSQL("and sysdate between a.start_date and a.end_date ");

        return Dao.qryByParse(parser, pg, Route.getJourDb(Route.CONN_CRM_CG));

    }

    /**
     * 根据产品订购关系ID查询merchp表信息 2014-7-24 code_code翻译
     * 
     * @author ft
     * @param productOfferId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMerchpByProductOfferId(String productOfferId) throws Exception
    {

        IData param = new DataMap();
        param.put("PRODUCT_OFFER_ID", productOfferId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select a.inst_id, ");
        parser.addSQL("TO_CHAR(a.trade_id) trade_id, ");
        parser.addSQL("a.accept_month, ");
        parser.addSQL("a.user_id, ");
        parser.addSQL("a.merch_spec_code, ");
        parser.addSQL("a.product_spec_code, ");
        parser.addSQL("a.product_order_id, ");
        parser.addSQL("a.product_offer_id, ");
        parser.addSQL("a.group_id, ");
        parser.addSQL("a.serv_code, ");
        parser.addSQL("a.biz_attr, ");
        parser.addSQL("a.status, ");
        parser.addSQL("a.start_date, ");
        parser.addSQL("a.end_date, ");
        parser.addSQL("a.modify_tag, ");
        parser.addSQL("a.update_staff_id, ");
        parser.addSQL("a.update_depart_id, ");
        parser.addSQL("a.remark ");
        parser.addSQL("FROM tf_b_trade_grp_merchp a ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("and a.product_order_id = :PRODUCT_OFFER_ID ");
        parser.addSQL("and sysdate between a.start_date and a.end_date ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据产品订单号查询BBOSS merchp表信息 2014-7-24 code_code翻译
     * 
     * @author ft
     * @param productOrderId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMerchpByProductOrderId(String productOrderId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ORDER_ID", productOrderId);

        SQLParser parser = new SQLParser(inparam);

        parser.addSQL("select a.inst_id, ");
        parser.addSQL("TO_CHAR(a.trade_id) trade_id, ");
        parser.addSQL("a.accept_month, ");
        parser.addSQL("a.user_id, ");
        parser.addSQL("a.merch_spec_code, ");
        parser.addSQL("a.product_spec_code, ");
        parser.addSQL("a.product_order_id, ");
        parser.addSQL("a.product_offer_id, ");
        parser.addSQL("a.group_id, ");
        parser.addSQL("a.serv_code, ");
        parser.addSQL("a.biz_attr, ");
        parser.addSQL("a.status, ");
        parser.addSQL("a.start_date, ");
        parser.addSQL("a.end_date, ");
        parser.addSQL("a.modify_tag, ");
        parser.addSQL("a.update_staff_id, ");
        parser.addSQL("a.update_depart_id, ");
        parser.addSQL("a.remark ");
        parser.addSQL("FROM tf_b_trade_grp_merchp a ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("and a.product_order_id = :PRODUCT_ORDER_ID ");
        parser.addSQL("and sysdate between a.start_date and a.end_date ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

    }

    /**
     * 根据tradeId查询产品订购台账信息 2014-7-24 code_code翻译
     * 
     * @author ft
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMerchpByTradeId(String tradeId, Pagination pg) throws Exception
    {
        IData param = new DataMap();

        param.put("TRADE_ID", tradeId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select a.inst_id, ");
        parser.addSQL("TO_CHAR(a.trade_id) trade_id, ");
        parser.addSQL("a.accept_month, ");
        parser.addSQL("a.user_id, ");
        parser.addSQL("a.merch_spec_code, ");
        parser.addSQL("a.product_spec_code, ");
        parser.addSQL("a.product_order_id, ");
        parser.addSQL("a.product_offer_id, ");
        parser.addSQL("a.group_id, ");
        parser.addSQL("a.serv_code, ");
        parser.addSQL("a.biz_attr, ");
        parser.addSQL("a.status, ");
        parser.addSQL("a.start_date, ");
        parser.addSQL("a.end_date, ");
        parser.addSQL("a.modify_tag, ");
        parser.addSQL("a.update_staff_id, ");
        parser.addSQL("a.update_depart_id, ");
        parser.addSQL("a.remark ");
        parser.addSQL("FROM tf_b_trade_grp_merchp a ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("and a.trade_id = TO_NUMBER(:TRADE_ID) ");
        parser.addSQL("and a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        parser.addSQL("and sysdate between a.start_date and a.end_date ");

        return Dao.qryByParse(parser, pg, Route.getJourDb(BizRoute.getRouteId()));

    }

    /**
     * 根据user_id和trade_id查询 merchp表信息,不带Pagination 2014-7-24 code_code翻译
     * 
     * @author ft
     * @param productOrderId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMerchpByUserIdAndTradeId(String user_id, String trade_id) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", user_id);
        inparam.put("TRADE_ID", trade_id);

        SQLParser parser = new SQLParser(inparam);

        parser.addSQL("select  TO_CHAR(a.trade_id) trade_id, ");
        parser.addSQL("a.accept_month, ");
        parser.addSQL("a.user_id, ");
        parser.addSQL("a.merch_spec_code, ");
        parser.addSQL("a.product_spec_code, ");
        parser.addSQL("a.product_order_id, ");
        parser.addSQL("a.product_offer_id, ");
        parser.addSQL("a.group_id, ");
        parser.addSQL("a.serv_code, ");
        parser.addSQL("a.biz_attr, ");
        parser.addSQL("a.status, ");
        parser.addSQL("a.start_date, ");
        parser.addSQL("a.end_date, ");
        parser.addSQL("a.modify_tag, ");
        parser.addSQL("a.update_staff_id, ");
        parser.addSQL("a.update_depart_id, ");
        parser.addSQL("a.remark ");
        parser.addSQL("from tf_b_trade_grp_merchp a ");
        parser.addSQL("where 1 = 1 ");
        parser.addSQL("and a.trade_id = TO_NUMBER(:TRADE_ID) ");
        parser.addSQL("and a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) ");
        parser.addSQL("and a.user_id=:USER_ID ");
        parser.addSQL("and (a.MODIFY_TAG=:MODIFY_TAG OR :MODIFY_TAG IS NULL) ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @param
     * @desciption 根据offerId查询merchp表信息
     * @author fanti
     * @version 创建时间：2014年9月3日 下午4:17:01
     */
    public static IDataset qryMerchpInfoByProductOfferId(String productOfferId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_OFFER_ID", productOfferId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* ");
        parser.addSQL(" FROM TF_B_TRADE_GRP_MERCHP T ");
        parser.addSQL(" WHERE T.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
        parser.addSQL(" AND SYSDATE > T.START_DATE ");
        parser.addSQL(" AND SYSDATE < T.END_DATE ");

        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    /**
     * 根据tradeId查询tf_b_trade_grp_merchp表信息
     * 
     * @author ft
     * @param inparams
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchpInfoByTradeId(String trade_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("select INST_ID, ");
        sql.append("to_char(TRADE_ID) TRADE_ID, ");
        sql.append("ACCEPT_MONTH, ");
        sql.append("to_char(USER_ID) USER_ID, ");
        sql.append("MERCH_SPEC_CODE, ");
        sql.append("PRODUCT_SPEC_CODE, ");
        sql.append("PRODUCT_ORDER_ID, ");
        sql.append("PRODUCT_OFFER_ID, ");
        sql.append("GROUP_ID, ");
        sql.append("SERV_CODE, ");
        sql.append("BIZ_ATTR, ");
        sql.append("STATUS, ");
        sql.append("to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("MODIFY_TAG, ");
        sql.append("to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, ");
        sql.append("REMARK, ");
        sql.append("RSRV_NUM1, ");
        sql.append("RSRV_NUM2, ");
        sql.append("RSRV_NUM3, ");
        sql.append("to_char(RSRV_NUM4) RSRV_NUM4, ");
        sql.append("to_char(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("RSRV_STR1, ");
        sql.append("RSRV_STR2, ");
        sql.append("RSRV_STR3, ");
        sql.append("RSRV_STR4, ");
        sql.append("RSRV_STR5, ");
        sql.append("to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("RSRV_TAG1, ");
        sql.append("RSRV_TAG2, ");
        sql.append("RSRV_TAG3 ");
        sql.append("FROM tf_b_trade_grp_merchp ");
        sql.append("WHERE trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");

        return Dao.qryBySql(sql, inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据台账编号和产品订单编号查询BBOSS侧产品信息表
     * 
     * @param tradeId
     * @param productOrderId
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchPInfoByTradeIdOrderId(String tradeId, String productOrderId, Integer index) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        inparams.put("PRODUCT_ORDER_ID", productOrderId);
        inparams.put("INDEX", index);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT TRADE_ID, ");
        sql.append("ACCEPT_MONTH, ");
        sql.append("'BBOSS_' || to_char(sysdate, 'MMDDhh24mmss') || :INDEX as ATTR_CODE, ");
        sql.append("T.PRODUCT_SPEC_CODE ");
        sql.append("FROM TF_B_TRADE_GRP_MERCHP T ");
        sql.append("WHERE T.PRODUCT_ORDER_ID = :PRODUCT_ORDER_ID ");
        sql.append("and T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        sql.append("and T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");

        return Dao.qryBySql(sql, inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据trade_id和userIds（eg:1114073022800966,1114073022800967）查询TF_B_TRADE_GRP_MERCHP表信息，带Pagination
     * 
     * @author ft
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchpInfoByTradeIdUserIds(String tradeId, String userIds, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("USER_IDS", userIds);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT * ");
        parser.addSQL("FROM TF_B_TRADE_GRP_MERCHP T ");
        parser.addSQL("WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        parser.addSQL("AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        parser.addSQL("AND T.USER_ID IN (:USER_IDS) ");

        return Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 更新grpcenpay表
     * 
     * @Function:
     * @Description:
     * @param：
     * @return：
     * @throws：
     * @version:
     * @author:chenyi
     * @date: 下午3:32:50 2013-10-23
     */
    public static void updateGrpCenpayByTradeId(String tradeId, String bizMode, String serviceID, String startDate, String endDate, String productOfferingId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("BIZ_MODE", bizMode);
        param.put("SERVICE_ID", serviceID);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("PRODUCT_OFFER_ID", productOfferingId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE tf_b_trade_grp_cenpay  t  SET  t.BIZ_MODE = :BIZ_MODE , ");
        parser.addSQL(" t.SERVICE_ID = :SERVICE_ID , ");
        parser.addSQL(" t.START_DATE = TO_DATE(:START_DATE,'YYYYMMDDHH24MISS') ,");
        parser.addSQL(" t.END_DATE = TO_DATE(:END_DATE,'YYYYMMDD'), ");
        parser.addSQL(" t.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
        parser.addSQL(" where t.TRADE_ID = TO_NUMBER( :TRADE_ID) ");
        parser.addSQL(" and  t.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))  ");

        Dao.executeUpdate(parser, Route.getJourDb(Route.CONN_CRM_CG));//modify by xusf

    }

    /**
     * 根据TRADE_ID 更新TF_B_TRADE_GRP_MERCHP表productorderid字段
     * 
     * @author ft
     * @param inparams
     * @param page
     * @return
     * @throws Exception
     */
    public static int updateMerchpOrderIdByTradeId(String trade_id, String product_order_id) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("PRODUCT_ORDER_ID", product_order_id);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_GRP_MERCHP m SET m.PRODUCT_ORDER_ID= :PRODUCT_ORDER_ID WHERE m.TRADE_ID= TO_NUMBER(:TRADE_ID) AND m.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))");

        return Dao.executeUpdate(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据PRODUCT_OFFER_ID、TRADE_ID 更新TF_B_TRADE_GRP_MERCHP表PRODUCT_ORDER_ID字段
     * 
     * @author ft
     * @param inparams
     * @param page
     * @return
     * @throws Exception
     */
    public static int updateMerchpOrderIdByTradeIdOfferId(IData inparams) throws Exception
    {
        StringBuilder sql = new StringBuilder(1000);

        sql.append("update TF_B_TRADE_GRP_MERCHP m ");
        sql.append("set m.product_order_id = :PRODUCT_ORDER_ID ");
        sql.append("WHERE m.trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND m.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        sql.append("and m.product_offer_id = :PRODUCT_OFFER_ID ");

        return Dao.executeUpdate(sql, inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据TRADE_ID 更新TF_B_TRADE_GRP_MERCHP表STATUS字段
     * 
     * @author ft
     * @param inparams
     * @param page
     * @return
     * @throws Exception
     */
    public static int updateMerchpStatusByTradeId(String trade_id, String status) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("STATUS", status);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_GRP_MERCHP m SET m.STATUS= :STATUS WHERE m.TRADE_ID= TO_NUMBER(:TRADE_ID) AND m.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");

        return Dao.executeUpdate(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 更新rsrv_str1字段，存储的为支付省 根据TRADE_ID、USER_ID 更新TF_B_TRADE_GRP_MERCHP 表RSRV_STR1字段
     * 
     * @param param
     * @throws Exception
     */
    public static int updateTradeMerchpRsrvstr1(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_GRP_MERCHP T ");
        parser.addSQL("SET T.RSRV_STR1 = :RSRV_STR1 ");
        parser.addSQL("WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        parser.addSQL("AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) ");
        parser.addSQL("AND USER_ID = :USER_ID");
        return Dao.executeUpdate(parser, Route.getJourDb());
    }
    
    //集客大厅
    public static IDataset getJKDTMerchpOnlineByProductofferId(String productOfferId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_OFFER_ID", productOfferId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select a.trade_id, ");
        parser.addSQL("a.user_id, ");
        parser.addSQL("a.product_order_id, ");
        parser.addSQL("a.product_offer_id, ");
        parser.addSQL("b.order_id, ");
        parser.addSQL("b.cust_id, ");
        parser.addSQL("b.rsrv_str10, ");
        parser.addSQL("b.product_id, ");
        parser.addSQL("a.inst_id, ");
        parser.addSQL("a.PRODUCT_SPEC_CODE, ");
        parser.addSQL("a.GROUP_ID ");
        parser.addSQL("from TF_B_TRADE_ECRECEP_PRODUCT a, tf_b_trade b ");
        parser.addSQL("where 1 = 1 ");
        parser.addSQL("and a.trade_id = b.trade_id ");
        parser.addSQL("and a.ACCEPT_MONTH = b.accept_month ");
        parser.addSQL("and PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
        parser.addSQL("and rownum = 1 ");

        return Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset qryJKDTMerchpInfoByProductOfferId(String productOfferId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_OFFER_ID", productOfferId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* ");
        parser.addSQL(" FROM TF_B_TRADE_ECRECEP_PRODUCT T ");
        parser.addSQL(" WHERE T.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
        parser.addSQL(" AND SYSDATE > T.START_DATE ");
        parser.addSQL(" AND SYSDATE < T.END_DATE ");

        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    public static IDataset qryJKDTMerchpInfoByTradeId(String trade_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("select INST_ID, ");
        sql.append("to_char(TRADE_ID) TRADE_ID, ");
        sql.append("ACCEPT_MONTH, ");
        sql.append("to_char(USER_ID) USER_ID, ");
        sql.append("MERCH_SPEC_CODE, ");
        sql.append("PRODUCT_SPEC_CODE, ");
        sql.append("PRODUCT_ORDER_ID, ");
        sql.append("PRODUCT_OFFER_ID, ");
        sql.append("GROUP_ID, ");
        sql.append("SERV_CODE, ");
        sql.append("BIZ_ATTR, ");
        sql.append("to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("MODIFY_TAG, ");
        sql.append("to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, ");
        sql.append("REMARK, ");
        sql.append("RSRV_NUM1, ");
        sql.append("RSRV_NUM2, ");
        sql.append("RSRV_NUM3, ");
        sql.append("to_char(RSRV_NUM4) RSRV_NUM4, ");
        sql.append("to_char(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("RSRV_STR1, ");
        sql.append("RSRV_STR2, ");
        sql.append("RSRV_STR3, ");
        sql.append("RSRV_STR4, ");
        sql.append("RSRV_STR5, ");
        sql.append("to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("RSRV_TAG1, ");
        sql.append("RSRV_TAG2, ");
        sql.append("RSRV_TAG3 ");
        sql.append("FROM TF_B_TRADE_ECRECEP_PRODUCT ");
        sql.append("WHERE trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");

        return Dao.qryBySql(sql, inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }
}
