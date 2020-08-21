
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BbossTradeQueryDAO
{

    /**
     * @Description:根据用户的user_id 找商品关系台帐编码
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchMebTradeInfo(String serialNumber, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select a.* from tf_b_trade a, tf_f_user u ");
        parser.addSQL(" where a.user_id = u.user_id ");
        parser.addSQL(" and a.product_id = u.product_id ");
        parser.addSQL(" and a.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" and u.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" and  u.remove_tag = '0'");
        parser.addSQL(" and rownum = 1");
        return Dao.qryByParse(parser, null, routeId);
    }

    /**
     * @Description:根据用户的user_id 找商品关系台帐编码
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchpMebTradeInfo(String serialNumber, String productOfferId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("PRODUCT_OFFER_ID", productOfferId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select /*+ ordered use_nl(a b)*/ b.*");
        parser.addSQL("  from tf_b_trade_grp_merch_meb  a  ,tf_b_trade b");
        parser.addSQL(" where a.trade_id = b.trade_id");
        parser.addSQL(" and a.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" and a.PRODUCT_OFFER_ID =:PRODUCT_OFFER_ID");
        parser.addSQL(" and rownum = 1");
        return Dao.qryByParse(parser, null, Route.getJourDb(routeId));
    }

    /**
     * @Description:根据用户的user_id 找商品关系台帐编码
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchpMebTradeInfoByUserId(String userId, String productOfferId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_OFFER_ID", productOfferId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select b.* ");
        parser.addSQL("  from tf_b_trade_grp_merch_meb a, tf_b_trade b ");
        parser.addSQL(" where a.trade_id = b.trade_id ");
        parser.addSQL("   and a.accept_month = b.accept_month ");
        parser.addSQL("   and b.USER_ID = :USER_ID ");
        parser.addSQL("   and a.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
        parser.addSQL("   and rownum < 2");

        return Dao.qryByParse(parser, null, Route.getJourDb(routeId));
    }

    /**
     * @Description:根据商品订单号找台帐编码
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchpTradeInfo(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select /*+ ordered use_nl(a b)*/ a.trade_id,a.user_id,b.order_id,b.cust_id,b.rsrv_str10,b.product_id,b.trade_eparchy_code,b.trade_city_code,b.trade_depart_id,b.trade_staff_id");
        parser.addSQL(" from tf_b_trade_grp_merchp a,tf_b_trade b ");
        parser.addSQL(" where a.trade_id = b.trade_id");
        parser.addSQL(" and a.ACCEPT_MONTH = b.accept_month");
        parser.addSQL(" and PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
        parser.addSQL(" and rownum = 1");
        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @Description:根据产品订购关系编码获取集团产品台帐信息
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchpTradeInfoByProductOrder(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select /*+ ordered use_nl(a b)*/ a.trade_id,a.user_id,b.order_id,b.cust_id,b.rsrv_str10,b.product_id,b.trade_eparchy_code,b.trade_city_code,b.trade_depart_id,b.trade_staff_id");
        parser.addSQL(" from tf_b_trade_grp_merchp a,tf_b_trade b ");
        parser.addSQL(" where a.trade_id = b.trade_id");
        parser.addSQL(" and a.ACCEPT_MONTH = b.accept_month");
        parser.addSQL(" and PRODUCT_ORDER_ID = :PRODUCT_ORDER_ID ");
        parser.addSQL(" and rownum = 1");
        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @Description:根据产品用户的user_id 找产品的服务信息
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchpUserSvc(String userId, String productId, String userIdA, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
        param.put("USER_ID_A", userIdA);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("  select us.SERVICE_ID ELEMENT_ID, us.USER_ID,  us.PRODUCT_ID,  us.PACKAGE_ID,");
        parser.addSQL("  'S' ELEMENT_TYPE_CODE, s.service_name ELEMENT_NAME,  us.INST_ID,");
        parser.addSQL("  to_char(us.START_DATE, 'yyyy-mm-dd') START_DATE, to_char(us.END_DATE, 'yyyy-mm-dd') END_DATE");
        parser.addSQL("  from tf_f_user_svc us, td_b_service s ");
        parser.addSQL("  where us.service_id = s.service_id");
        parser.addSQL("  and user_id = :USER_ID");
        parser.addSQL("  and product_id = :PRODUCT_ID");
        parser.addSQL("  and user_id_a = :USER_ID_A");
        parser.addSQL("  AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)");
        parser.addSQL("  AND SYSDATE < us.END_DATE");
        return Dao.qryByParse(parser, null, routeId);
    }

    /**
     * @Description:根据商品订单号找台帐历史编码
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchTradehInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select /*+ ordered use_nl(a b)*/ a.merch_spec_code,a.trade_id,b.order_id,b.cust_id,b.cust_name,b.trade_staff_id,b.trade_depart_id,b.trade_city_code,trade_eparchy_code ");// mod
        // by
        // liuzz
        parser.addSQL(" from tf_b_trade_grp_merch a,tf_bh_trade b ");
        parser.addSQL(" where a.trade_id = b.trade_id");
        parser.addSQL(" and a.ACCEPT_MONTH = b.accept_month");
        parser.addSQL(" and MERCH_ORDER_ID = :MERCH_ORDER_ID ");
        parser.addSQL(" and rownum = 1");
        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @Description:根据商品订单号找台帐编码
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchTradeInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select /*+ ordered use_nl(a b)*/ a.merch_spec_code,a.trade_id,b.order_id,b.cust_id,b.cust_name,b.trade_eparchy_code,b.trade_city_code,b.trade_depart_id,b.trade_staff_id ");// mod
        // by
        // liuzz
        parser.addSQL(" from tf_b_trade_grp_merch a,tf_b_trade b ");
        parser.addSQL(" where a.trade_id = b.trade_id");
        parser.addSQL(" and a.ACCEPT_MONTH = b.accept_month");
        parser.addSQL(" and MERCH_ORDER_ID = :MERCH_ORDER_ID ");
        parser.addSQL(" and rownum = 1");
        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @Description:根据商品订单号找台帐编码
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchTradeInfoByOfferId(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select /*+ ordered use_nl(a b)*/ a.trade_id,b.order_id,b.cust_id,b.rsrv_str10,b.product_id"); // add
        // by
        // lijie9,
        // 2010-11-07,
        // add
        // b.rsrv_str10,b.product_id以判断走哪个BBOSS处理页面
        parser.addSQL(" from tf_b_trade_grp_merch a,tf_b_trade b ");
        parser.addSQL(" where a.trade_id = b.trade_id");
        parser.addSQL(" and a.ACCEPT_MONTH = b.accept_month");
        parser.addSQL(" and MERCH_OFFER_ID = :MERCH_OFFER_ID ");
        parser.addSQL(" and rownum = 1");
        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @Description:根据商品USER_ID 查找 relationUU台帐中用户和商品关系是否存在
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchTradeRelationUU(String userId, String men_user_id, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_A", userId);
        param.put("USER_ID_B", men_user_id);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select /*+ ordered use_nl(a b)*/  a.trade_id ");
        parser.addSQL(" from tf_b_trade_relation a,tf_b_trade b ");
        parser.addSQL(" where a.trade_id = b.trade_id ");
        parser.addSQL(" AND a.user_id_a = :USER_ID_A");
        parser.addSQL(" AND a.user_id_b = :USER_ID_B");
        parser.addSQL(" AND a.MODIFY_TAG = '0'");
        parser.addSQL(" and b.exec_time <= sysdate");
        parser.addSQL(" AND rownum = 1");
        return Dao.qryByParse(parser, null, Route.getJourDb(routeId));
    }
}
