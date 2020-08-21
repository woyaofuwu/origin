
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeGrpMerchInfoQry
{
    /**
     * 查询BBOSS商品订单号
     * 
     * @author liuxx3
     * @date 2014-08-13
     */
    public static IDataset getBBossMerchInfoByOrderId(String merchtradeId, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", merchtradeId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT M.MERCH_ORDER_ID,T.ORDER_ID ");
        parser.addSQL(" FROM TF_B_TRADE_GRP_MERCH M,TF_B_TRADE T");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND M.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        parser.addSQL(" AND M.TRADE_ID = T.TRADE_ID ");

        parser.addSQL(" UNION ALL");

        parser.addSQL(" SELECT M.MERCH_ORDER_ID,T.ORDER_ID ");
        parser.addSQL(" FROM TF_B_TRADE_GRP_MERCH M,TF_BH_TRADE T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND M.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        parser.addSQL(" AND M.TRADE_ID = T.TRADE_ID ");

        return Dao.qryByParse(parser, pg, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据业务流水号查询BBOSS商品用户订购表子表信息
     * 
     * @author ft
     * @param trade_id
     *            业务流水号
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryAllMerchInfoByTradeId(String trade_id, Pagination pagination) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("select  TO_CHAR(a.trade_id) trade_id, ");
        sql.append("a.accept_month, ");
        sql.append("a.inst_id, ");
        sql.append("a.user_id, ");
        sql.append("a.serial_number, ");
        sql.append("a.merch_spec_code, ");
        sql.append("a.merch_order_id, ");
        sql.append("a.merch_offer_id, ");
        sql.append("a.group_id, ");
        sql.append("a.opr_source, ");
        sql.append("a.biz_mode, ");
        sql.append("a.status, ");
        sql.append("a.host_company, ");
        sql.append("a.RSRV_STR5, ");
        sql.append("a.modify_tag, ");
        sql.append("a.start_date, ");
        sql.append("a.end_date, ");
        sql.append("a.RSRV_NUM1	, ");
        sql.append("a.RSRV_NUM2	, ");
        sql.append("a.RSRV_NUM3	, ");
        sql.append("a.RSRV_NUM4	, ");
        sql.append("a.RSRV_NUM5	, ");
        sql.append("a.RSRV_STR1	, ");
        sql.append("a.RSRV_STR2	, ");
        sql.append("a.RSRV_STR3	, ");
        sql.append("a.RSRV_STR4	, ");
        sql.append("a.RSRV_STR5	, ");
        sql.append("a.RSRV_DATE1, ");
        sql.append("a.RSRV_DATE2, ");
        sql.append("a.RSRV_DATE3, ");
        sql.append("a.RSRV_TAG1	, ");
        sql.append("a.RSRV_TAG2	, ");
        sql.append("a.RSRV_TAG3 ");
        sql.append("FROM TF_B_TRADE_GRP_MERCH a ");
        sql.append("WHERE a.trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) ");

        return Dao.qryBySql(sql, inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @Description:一级BBOSS业务集团客户订购处理查询
     * @author jch
     * @date 2009-8-10
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossTradeInfo(String productspecnumber, String groupId, String custName, String orderId, String state, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCTSPECNUMBER", productspecnumber);
        param.put("GROUP_ID", groupId);
        param.put("CUST_NAME", custName);
        param.put("ORDER_ID", orderId);
        param.put("STATE", state);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT b.order_id,b.accept_date,b.finish_date,b.trade_staff_id,p1.pospecname,p2.productspecname,b.cust_id_b,t2.trade_id,t2.merch_spec_code,t2.merch_order_id");
        parser
                .addSQL(" ,t2.user_id,t2.serial_number,t2.group_id,t2.product_spec_code,t2.product_order_id,case when t2.modify_tag='0' then '增加' when t2.modify_tag='1' then '删除' when t2.modify_tag='2' then '修改' end modify_tag,cg.cust_id AS jt_cust_id,cg.cust_name AS jt_cust_name,case when t2.state='0' then '处理成功' when t2.state='1' then '处理失败' when t2.state='2' then '处理进行中' end state ");
        parser.addSQL(" FROM (");
        parser.addSQL(" SELECT t.trade_id,t.merch_spec_code,t.merch_order_id,t.user_id,t.serial_number,t.group_id,t1.product_spec_code,t1.product_order_id,t1.modify_tag,t.start_date,t.end_date,:STATE state");
        parser.addSQL(" FROM tf_b_trade_grp_merch t,tf_b_trade_grp_merchp t1");
        parser.addSQL("  WHERE t.trade_id=t1.trade_id");
        parser.addSQL(" AND t1.product_spec_code=:PRODUCTSPECNUMBER");
        parser.addSQL(" AND t.group_id=:GROUP_ID");
        parser.addSQL(" ) t2,tf_b_trade b,td_f_po p1,td_f_poproduct p2,tf_f_cust_group cg");
        parser.addSQL(" WHERE t2.trade_id=b.trade_id");
        parser.addSQL("  AND t2.product_spec_code=p2.productspecnumber");
        parser.addSQL("  AND t2.merch_spec_code=p1.pospecnumber");
        parser.addSQL("  AND b.cust_id=cg.cust_id");
        // parser.addSQL(" AND b.cust_id_b=:CUST_ID");
        parser.addSQL(" AND cg.cust_name=:CUST_NAME");
        parser.addSQL(" AND t2.group_id=cg.group_id");
        parser.addSQL(" AND b.order_id=:ORDER_ID");
        if (null != param.get("STATE") && param.get("STATE") == "0")
        {
            parser.addSQL(" AND sysdate>=t2.start_date");
            parser.addSQL(" AND sysdate<=t2.end_date");

        }
        else if (null != param.get("STATE") && param.get("STATE") == "1")
        {

            parser.addSQL(" AND sysdate>t2.end_date");
            parser.addSQL(" AND  b.cancel_tag=1 ");

        }
        else if (null != param.get("STATE") && param.get("STATE") == "2")
        {
            parser.addSQL(" AND sysdate<t2.end_date");
            parser.addSQL(" AND  b.cancel_tag=0 ");
        }

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 根据商品订单号关联主台账表查询在线的merch表信息 2014-7-24 code_code翻译
     * 
     * @author ft
     * @param merchOrderId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMerchByMerchOrderIdUnionTrade(String merchOrderId) throws Exception
    {

        IData param = new DataMap();
        param.put("MERCH_ORDER_ID", merchOrderId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select a.merch_spec_code, ");
        parser.addSQL("a.inst_id, ");
        parser.addSQL("a.trade_id, ");
        parser.addSQL("a.merch_order_id, ");
        parser.addSQL("a.merch_offer_id, ");
        parser.addSQL("b.order_id, ");
        parser.addSQL("b.cust_id, ");
        parser.addSQL("b.cust_name, ");
        parser.addSQL("b.trade_eparchy_code, ");
        parser.addSQL("b.trade_city_code, ");
        parser.addSQL("b.trade_depart_id, ");
        parser.addSQL("b.trade_staff_id ");
        parser.addSQL("from tf_b_trade_grp_merch a, tf_b_trade b ");
        parser.addSQL("where a.trade_id = b.trade_id ");
        parser.addSQL("and a.ACCEPT_MONTH = b.accept_month ");
        parser.addSQL("and MERCH_ORDER_ID = :MERCH_ORDER_ID ");
        parser.addSQL("and sysdate between a.start_date and a.end_date ");
        parser.addSQL("and rownum = 1 ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @Function:
     * @Description:根据用户id集团编码全网商品编码查询商品信息
     * @param：
     * @return：
     * @throws：
     * @version:
     * @author:chenyi
     * @throws Exception
     * @date: 下午3:32:50 2013-10-23
     */
    public static IDataset qryMerchInfoByGrpUid(String user_id, String groupid, String merch_spec_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("GROUP_ID", groupid);
        param.put("MERCH_SPEC_CODE", merch_spec_code);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT  ACCEPT_MONTH  ,TRADE_ID,USER_ID,SERIAL_NUMBER,MERCH_SPEC_CODE,MERCH_ORDER_ID,MERCH_OFFER_ID,GROUP_ID,OPR_SOURCE,BIZ_MODE,STATUS,HOST_COMPANY,START_DATE,END_DATE,MODIFY_TAG,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,INST_ID ");
        parser.addSQL(" from tf_b_trade_grp_merch");
        parser.addSQL(" where 1=1");
        parser.addSQL(" and USER_ID = :USER_ID");
        parser.addSQL(" and GROUP_ID = :GROUP_ID");
        parser.addSQL(" and MERCH_SPEC_CODE = :MERCH_SPEC_CODE");

        return Dao.qryByParse(parser, Route.getJourDb());

    }

    /**
     * 根据主台账表的order_id查询子台账merch表tradeId和groupId 2014-7-23从CODE_CODE翻译为SQL
     * 
     * @author ft
     * @param mainOrderId
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchInfoByMainTradeOrderId(String mainTradeOrderId) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("ORDER_ID", mainTradeOrderId);

        SQLParser parser = new SQLParser(inparam);

        parser.addSQL("select merch.trade_id, ");
        parser.addSQL("merch.group_id, ");
        parser.addSQL("merch.merch_order_id, ");
        parser.addSQL("merch.merch_offer_id ");
        parser.addSQL("from tf_b_trade_grp_merch merch, tf_b_trade trade ");
        parser.addSQL("where 1 = 1 ");
        parser.addSQL("and merch.trade_id = trade.trade_id ");
        parser.addSQL("and trade.order_id = TO_NUMBER(:ORDER_ID) ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据商品订单号查询商品台账信息
     * 
     * @param merchOrderId
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchInfoByMerchOfferId(String merchOfferId) throws Exception
    {
        IData param = new DataMap();
        param.put("MERCH_OFFER_ID", merchOfferId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* ");
        parser.addSQL(" FROM TF_B_TRADE_GRP_MERCH T ");
        parser.addSQL(" WHERE T.MERCH_OFFER_ID = :MERCH_OFFER_ID ");
        parser.addSQL(" AND SYSDATE > T.START_DATE ");
        parser.addSQL(" AND SYSDATE < T.END_DATE ");

        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 根据TRADE_ID 查询出 sysdate>start_date and sysdate<end_date 有TF_B_TEADE_GRP_MERCH表的信息
     * 
     * @author ft
     * @param tradeId
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchInfoByTradeId(String tradeId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select * ");
        parser.addSQL("from tf_b_trade_grp_merch ");
        parser.addSQL("where TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        parser.addSQL("and ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        parser.addSQL("and START_DATE < sysdate ");
        parser.addSQL("and END_DATE > sysdate ");

        return Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @Description:根据商品订单号找台帐编码
     * @author ft
     * @date
     * @param merchOrderId
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchInfoOnlineByMerchOrderId(String merchOrderId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("MERCH_ORDER_ID", merchOrderId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select a.merch_spec_code, ");
        parser.addSQL("a.trade_id, ");
        parser.addSQL("b.order_id, ");
        parser.addSQL("b.cust_id, ");
        parser.addSQL("b.cust_name, ");
        parser.addSQL("a.inst_id ");
        parser.addSQL("from tf_b_trade_grp_merch a, tf_b_trade b ");
        parser.addSQL("where 1 = 1 ");
        parser.addSQL("and a.trade_id = b.trade_id ");
        parser.addSQL("and a.ACCEPT_MONTH = b.accept_month ");
        parser.addSQL("and MERCH_ORDER_ID = :MERCH_ORDER_ID ");
        parser.addSQL("and rownum = 1 ");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }

    /**
     * @param
     * @desciption 根据商品订单号查询在线台帐信息
     * @author fanti
     * @version 创建时间：2014年9月3日 下午7:58:02
     */
    public static IDataset qryMerchOnlineInfoByMerchOfferId(String merchOfferId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("MERCH_OFFER_ID", merchOfferId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select a.trade_id, ");
        parser.addSQL("a.merch_order_id, ");
        parser.addSQL("a.merch_offer_id, ");
        parser.addSQL("a.user_id, ");
        parser.addSQL("b.order_id, ");
        parser.addSQL("b.cust_id, ");
        parser.addSQL("b.rsrv_str10, ");
        parser.addSQL("b.product_id ");
        parser.addSQL("from tf_b_trade_grp_merch a, tf_b_trade b ");
        parser.addSQL("where a.trade_id = b.trade_id ");
        parser.addSQL("and a.ACCEPT_MONTH = b.accept_month ");
        parser.addSQL("and MERCH_OFFER_ID = :MERCH_OFFER_ID ");
        parser.addSQL("and rownum = 1 ");

        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    /**
     * @param
     * @desciption 根据商品userId查询在线台账信息
     * @author fanti
     * @version 创建时间：2014年9月3日 下午8:05:57
     */
    public static IDataset qryMerchOnlineInfoByUserId(String merchUserId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", merchUserId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select merch.trade_id, ");
        parser.addSQL("merch.merch_order_id, ");
        parser.addSQL("merch.merch_offer_id, ");
        parser.addSQL("merch.user_id, ");
        parser.addSQL("trade.cust_id, ");
        parser.addSQL("trade.order_id, ");
        parser.addSQL("trade.rsrv_str10, ");
        parser.addSQL("trade.product_id ");
        parser.addSQL("from tf_b_trade trade, tf_b_trade_grp_merch merch ");
        parser.addSQL("where 1 = 1 ");
        parser.addSQL("and trade.trade_id = merch.trade_id ");
        parser.addSQL("and trade.accept_month = merch.accept_month ");
        parser.addSQL("and merch.user_id = :USER_ID ");
        parser.addSQL("and rownum = 1 ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @author ft
     * @Description 查询出TF_B_TRADE中需要有预受理BBOSS的数据
     * @throws Exception
     * @param cycle
     */

    public static IDataset queryBbossTrade(String startDate, String endDate, String pospecnumber, String productspecnumber, String groupId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("POSPECNUMBER", pospecnumber);
        param.put("PRODUCTSPECNUMBER", productspecnumber);
        param.put("GROUP_ID", groupId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT trade.order_id, ");
        parser.addSQL("trade.product_id, ");
        parser.addSQL("merchp.trade_id, ");
        parser.addSQL("merchp.inst_id, ");
        parser.addSQL("merchp.merch_spec_code, ");
        parser.addSQL("merchp.product_spec_code, ");
        parser.addSQL("merchp.product_offer_id, ");
        parser.addSQL("merchp.group_id, ");
        parser.addSQL("merchp.user_id, ");
        parser.addSQL("merchp.product_order_id, ");
        parser.addSQL("merch.merch_order_id ");
        parser.addSQL("from tf_b_trade             trade, ");
        parser.addSQL("tf_b_trade_grp_merch   merch, ");
        parser.addSQL("tf_b_trade_grp_merchp  merchp, ");
        parser.addSQL("tf_b_trade_relation_bb bb ");
        parser.addSQL("where 1 = 1 ");
        parser.addSQL("and merchp.merch_spec_code = :POSPECNUMBER ");
        parser.addSQL("and merchp.product_spec_code = :PRODUCTSPECNUMBER ");
        parser.addSQL("and merchp.group_id = :GROUP_ID ");
        parser.addSQL("and merchp.rsrv_str1 = '1' "); // 产品预受理已归档判断
        parser.addSQL("and trade.trade_id = merchp.trade_id ");
        parser.addSQL("and trade.update_time >= to_date(:START_DATE, 'yyyy-MM-dd') ");
        parser.addSQL("and trade.update_time <= to_date(:END_DATE, 'yyyy-MM-dd') ");
        parser.addSQL("and trade.RSRV_STR10 = '1' "); // 产品预受理已归档判断
        parser.addSQL("and bb.trade_id = trade.trade_id ");
        parser.addSQL("and merch.user_id = bb.user_id_a ");
        parser.addSQL("and merch.trade_id in ");
        parser.addSQL("(SELECT TRADE_ID FROM TF_B_TRADE WHERE ORDER_ID = trade.ORDER_ID ");
        parser.addSQL("union all ");
        parser.addSQL("SELECT TRADE_ID FROM TF_BH_TRADE WHERE ORDER_ID = trade.ORDER_ID) ");
        parser.addSQL("order by trade.trade_id ");

        return Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));

    }

    /**
     * 处理商品级相关附件，更改tf_b_trade_grp_merch 的rsrv_str5字段
     * 
     * @author weixb3
     * @param params
     *            页面传入的参数
     * @return IDataset 执行返回结果4
     * @throws Exception
     */

    public static void updateBusNeedDegree(String busNeedDegree, String merchTradeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("TRADE_ID", merchTradeId);
        inparam.put("RSRV_STR5", busNeedDegree);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("update ");
        sql.append("TF_B_TRADE_GRP_MERCH  m ");
        sql.append("set  m.rsrv_str5=:RSRV_STR5 ");
        sql.append("WHERE m.trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND m.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) ");

        Dao.executeUpdate(sql, inparam, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据TRADE_ID 更新TF_B_TRADE_GRP_MERCH表MERCH_ORDER_ID字段
     * 
     * @param inparams
     * @param page
     * @author weixb3
     * @return
     * @throws Exception
     */
    public static int updateMerchOrderIdByTradeId(String trade_id, String merch_order_id) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("MERCH_ORDER_ID", merch_order_id);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_GRP_MERCH m SET m.MERCH_ORDER_ID= :MERCH_ORDER_ID WHERE m.TRADE_ID= TO_NUMBER(:TRADE_ID) ");

        return Dao.executeUpdate(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据TRADE_ID 更新TF_B_TRADE_GRP_MERCH表STATUS字段
     * 
     * @param inparams
     * @param page
     * @author weixb3
     * @return
     * @throws Exception
     */
    public static int updateMerchStatusByTradeId(String trade_id, String status) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("STATUS", status);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_GRP_MERCH m SET m.STATUS= :STATUS WHERE m.TRADE_ID= TO_NUMBER(:TRADE_ID) ");

        return Dao.executeUpdate(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * chenyi 根据TRADE_ID更新TF_B_TRADE_GRP_MERCH表MERCH_ORDER_ID字段
     * 
     * @param inparams
     * @param page
     * @return
     * @throws Exception
     */
    public static int updateTradeForGrpBBoss(IData inparams) throws Exception
    {
        StringBuilder sql = new StringBuilder(1000);
        sql.append("update TF_B_TRADE_GRP_MERCH  m set  m.merch_order_id=:MERCH_ORDER_ID ");
        sql.append("WHERE m.trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND m.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) ");

        return Dao.executeUpdate(sql, inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    //集客大厅
    public static IDataset qryJKDTMerchOnlineInfoByMerchOfferId(String merchOfferId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("MERCH_OFFER_ID", merchOfferId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select a.trade_id, ");
        parser.addSQL("a.merch_order_id, ");
        parser.addSQL("a.merch_offer_id, ");
        parser.addSQL("a.user_id, ");
        parser.addSQL("b.order_id, ");
        parser.addSQL("b.cust_id, ");
        parser.addSQL("b.rsrv_str10, ");
        parser.addSQL("b.product_id ");
        parser.addSQL("from TF_B_TRADE_ECRECEP_OFFER a, tf_b_trade b ");
        parser.addSQL("where a.trade_id = b.trade_id ");
        parser.addSQL("and a.ACCEPT_MONTH = b.accept_month ");
        parser.addSQL("and MERCH_OFFER_ID = :MERCH_OFFER_ID ");
        parser.addSQL("and rownum = 1 ");

        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    public static IDataset qryJKDTMerchInfoByMerchOfferId(String merchOfferId) throws Exception
    {
        IData param = new DataMap();
        param.put("MERCH_OFFER_ID", merchOfferId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* ");
        parser.addSQL(" FROM TF_B_TRADE_ECRECEP_OFFER T ");
        parser.addSQL(" WHERE T.MERCH_OFFER_ID = :MERCH_OFFER_ID ");
        parser.addSQL(" AND SYSDATE > T.START_DATE ");
        parser.addSQL(" AND SYSDATE < T.END_DATE ");

        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
    }

}
