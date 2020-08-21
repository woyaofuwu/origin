
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;

public class TradeGrpMerchMebInfoQry
{
    /**
     * @Description:根据用户的user_id 找商品关系台帐编码(带route)
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchMebInfoByUserIdOfferIdRouteId(String userId, String productOfferId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_OFFER_ID", productOfferId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select b.* ");
        parser.addSQL("from tf_b_trade_grp_merch_meb a, tf_b_trade b ");
        parser.addSQL("where a.trade_id = b.trade_id ");
        parser.addSQL("and a.accept_month = b.accept_month ");
        parser.addSQL("and b.USER_ID = :USER_ID ");
        parser.addSQL("and a.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");

        return Dao.qryByParse(parser, Route.getJourDb(routeId));
    }
    
    /**
     * @Description:根据用户的user_id 找商品关系台帐编码
     * @author shixb修改
     * @date
     * @param USER_ID
     * @param PRODUCT_OFFER_ID
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getMerchMebInfoByUserIdOfferId(String userId, String productOfferId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_OFFER_ID", productOfferId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select /*+ ordered use_nl(a b)*/ b.*");
        parser.addSQL("  from tf_b_trade_grp_merch_meb  a  ,tf_b_trade b");
        parser.addSQL(" where a.trade_id = b.trade_id");
        parser.addSQL(" and b.USER_ID = :USER_ID ");
        parser.addSQL(" and a.PRODUCT_OFFER_ID =:PRODUCT_OFFER_ID");
        parser.addSQL(" and rownum = 1");
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * @Description:根据用户的user_id 找商品关系台帐编码
     * @author shixb修改
     * @date
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getMerchpMebTradeInfo(String serialNumber, String productOfferId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("PRODUCT_OFFER_ID", productOfferId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select /*+ ordered use_nl(a b)*/ b.*");
        parser.addSQL("  from tf_b_trade_grp_merch_meb  a  ,tf_b_trade b");
        parser.addSQL(" where a.trade_id = b.trade_id");
        parser.addSQL(" and b.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" and a.PRODUCT_OFFER_ID =:PRODUCT_OFFER_ID");
        parser.addSQL(" and rownum = 1");
        return Dao.qryByParse(parser, pagination,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * @Description
     * @author hud
     * @date
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getMerchpMebTradeInfoByUserId(String userId, String productOfferId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_OFFER_ID", productOfferId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select /*+ leading(b)*/ b.*");
        parser.addSQL("  from tf_b_trade_grp_merch_meb  a  ,tf_b_trade b");
        parser.addSQL(" where a.trade_id = b.trade_id ");
        parser.addSQL(" and a.accept_month = b.accept_month ");
        parser.addSQL(" and b.USER_ID = :USER_ID ");
        parser.addSQL(" and a.PRODUCT_OFFER_ID =:PRODUCT_OFFER_ID");
        parser.addSQL(" and rownum = 1");
        return Dao.qryByParse(parser);
    }

    /**
     * 获取未完工的台帐信息
     * 
     * @param param
     * @return
     * @throws Exception
     * @author chenl
     * @date 2010-7-1
     */
    public static IDataset getTradeGrpMerchMeb(String userId, String serialNumber, String productOfferId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("PRODUCT_OFFER_ID", productOfferId);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t.TRADE_ID FROM TF_B_TRADE_GRP_MERCH_MEB t ");
        sql.append("WHERE 1=1 ");
        sql.append("AND t.USER_ID=to_number(:USER_ID) ");
        sql.append("AND t.SERIAL_NUMBER=to_char(:SERIAL_NUMBER) ");
        sql.append("AND t.PRODUCT_OFFER_ID=to_char(:PRODUCT_OFFER_ID) ");
        sql.append("AND t.MODIFY_TAG='0' ");
        sql.append("AND SYSDATE BETWEEN t.START_DATE AND t.END_DATE ");
        sql.append("AND EXISTS(SELECT 1 FROM tf_b_trade a WHERE a.TRADE_ID=t.TRADE_ID and a.TRADE_TYPE_CODE='3514') ");
        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }

    /**
     * @Description:一级BBOSS业务成员签约关系订购状态查询
     * @author jch
     * @date 2009-8-10
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossBizMebQy(String productOfferId, String serialNumber, String ecSerialNumber, String state, String groupId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_OFFER_ID", productOfferId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("EC_SERIAL_NUMBER", ecSerialNumber);
        param.put("STATE", state);
        param.put("GROUP_ID", groupId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT cg.group_id,cg.cust_name AS jt_name,t2.cust_name AS user_name,cg.city_code,");
        parser.addSQL(" t2.start_date,t2.end_date,t2.trade_staff_id,t2.cust_id,t2.ec_serial_number,");
        parser.addSQL(" t2.product_offer_id,t2.serial_number,case when t2.status='A' then '正常订购'");
        parser.addSQL(" when t2.status='N' then '销户' when t2.status='X' then '暂停' when t2.status='Y'");
        parser.addSQL(" then '恢复' end status ,t2.cust_id_b FROM (");
        parser.addSQL(" SELECT t.inst_id,t.start_date,t.end_date,t.update_staff_id,t1.cust_id,t.ec_serial_number,");
        parser.addSQL("t.product_offer_id,t.serial_number,t.status,t1.trade_staff_id,t1.cust_id_b,t1.cust_name");
        parser.addSQL(" FROM tf_b_trade_grp_merch_meb t,tf_b_trade t1");
        parser.addSQL(" WHERE t.trade_id=t1.trade_id ");
        parser.addSQL(" AND t.product_offer_id=:PRODUCT_OFFER_ID");
        parser.addSQL(" AND t.serial_number=:SERIAL_NUMBER");
        parser.addSQL(" AND t.ec_serial_number=:EC_SERIAL_NUMBER");

        if (null != param.get("STATE") && param.get("STATE") == "N")
        {
            parser.addSQL(" AND t.status='N'");
            parser.addSQL(" AND SYSDATE>t.end_date");

        }
        else if (null != param.get("STATE") && param.get("STATE") == "X")
        {

            parser.addSQL(" AND t.status='N'");
            parser.addSQL(" AND SYSDATE<=t.end_date");
        }
        else
        {
            parser.addSQL(" AND t.status=:STATE");
            parser.addSQL(" AND sysdate>=t.start_date");
            parser.addSQL(" AND sysdate<=t.end_date");

        }
        parser.addSQL(" ) t2, tf_f_cust_group cg");
        parser.addSQL(" WHERE t2.cust_id_b=cg.cust_id");

        parser.addSQL(" AND cg.group_id=:GROUP_ID");

        IDataset dataset = Dao.qryByParse(parser, pagination);

        for (int i = 0; i < dataset.size(); i++)
        {
            String staff_name = UStaffInfoQry.getStaffNameByStaffId(dataset.getData(i).getString("TRADE_STAFF_ID"));

            dataset.getData(i).put("STAFF_NAME", staff_name);
        }

        return dataset;
    }

    /**
     * @Description:一级BBOSS业务成员订购处理查询 ---关联td_bh_trade表
     * @author liuxx3
     * @date 2014-08-04
     */
    public static IDataset qryBBossMebAll(String group_id, String serial_number, String product_offer_id, String ec_serial_number, String product_order_id, String start_date, String end_date, Pagination pagination) throws Exception
    {

        String cust_id = "";
        if(group_id!=null&&!group_id.equals("")){
        	IDataset groupInfos = GrpInfoQry.queryGroupCustInfoByGroupId(group_id);
        	if(!groupInfos.isEmpty()){
        		cust_id = groupInfos.getData(0).getString("CUST_ID","");
        	}
        }
    	
    	IData param = new DataMap();
        param.put("CUST_ID", cust_id);
        param.put("SERIAL_NUMBER", serial_number);
        param.put("PRODUCT_OFFER_ID", product_offer_id);
        param.put("EC_SERIAL_NUMBER", ec_serial_number);
        param.put("PRODUCT_ORDER_ID", product_order_id);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT ");
        parser.addSQL("T.TRADE_ID, ");
        parser.addSQL("B.ORDER_ID, ");
        parser.addSQL("T.MODIFY_TAG AS MODIFY_TAG_NUM, ");
        parser.addSQL("T.PRODUCT_ORDER_ID, ");
        parser.addSQL("T.SERIAL_NUMBER, ");
        parser.addSQL("B.CUST_ID, ");
        parser.addSQL("B.CUST_ID_B, ");
        parser.addSQL("B.CUST_NAME AS SER_CUST_NAME, ");
        parser.addSQL("T.EC_SERIAL_NUMBER, ");
        parser.addSQL("T.PRODUCT_OFFER_ID, ");
        parser.addSQL("B.TRADE_STAFF_ID, ");
        parser.addSQL("B.FINISH_DATE, ");
        parser.addSQL("B.ACCEPT_DATE, ");
        parser.addSQL("T.STATUS, ");
//        parser.addSQL("P.CUST_NAME AS JT_CUST_NAME, ");
//        parser.addSQL("P.GROUP_ID, ");
        parser.addSQL("P1.MERCH_SPEC_CODE, ");
        parser.addSQL("P1.PRODUCT_SPEC_CODE, ");
        parser.addSQL("B.SUBSCRIBE_STATE ");
        parser.addSQL("FROM  ");
        parser.addSQL("TF_B_TRADE_GRP_MERCH_MEB T, ");
        parser.addSQL("TF_BH_TRADE              B, ");
//        parser.addSQL("TF_F_CUST_GROUP          P, ");
        parser.addSQL("TF_B_TRADE_GRP_MERCHP    P1 ");
        parser.addSQL("WHERE T.TRADE_ID = B.TRADE_ID ");
        parser.addSQL("AND B.CUST_ID_B = :CUST_ID ");
        parser.addSQL("AND T.PRODUCT_OFFER_ID = P1.PRODUCT_OFFER_ID ");
        parser.addSQL("AND B.ACCEPT_DATE > TO_DATE(:START_DATE, 'YYYY-MM-DD') ");
        parser.addSQL("AND B.ACCEPT_DATE < TO_DATE(:END_DATE, 'YYYY-MM-DD') ");
        parser.addSQL("AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL("AND T.EC_SERIAL_NUMBER = :EC_SERIAL_NUMBER ");
        parser.addSQL("AND B.ORDER_ID = :PRODUCT_ORDER_ID ");
        parser.addSQL("AND T.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
//        parser.addSQL("AND P.GROUP_ID = :GROUP_ID ");;
        parser.addSQL("AND SYSDATE >= T.START_DATE ");
        parser.addSQL("AND SYSDATE <= T.END_DATE ");
        parser.addSQL("AND SYSDATE >= P1.START_DATE ");
        parser.addSQL("AND SYSDATE <= P1.END_DATE ");
        parser.addSQL("AND B.SUBSCRIBE_STATE = '9' ");

        parser.addSQL("UNION ALL ");

        parser.addSQL("SELECT ");
        parser.addSQL("T.TRADE_ID, ");
        parser.addSQL("B.ORDER_ID, ");
        parser.addSQL("T.MODIFY_TAG AS MODIFY_TAG_NUM, ");
        parser.addSQL("T.PRODUCT_ORDER_ID, ");
        parser.addSQL("T.SERIAL_NUMBER, ");
        parser.addSQL("B.CUST_ID, ");
        parser.addSQL("B.CUST_ID_B, ");
        parser.addSQL("B.CUST_NAME AS SER_CUST_NAME, ");
        parser.addSQL("T.EC_SERIAL_NUMBER, ");
        parser.addSQL("T.PRODUCT_OFFER_ID, ");
        parser.addSQL("B.TRADE_STAFF_ID, ");
        parser.addSQL("B.FINISH_DATE, ");
        parser.addSQL("B.ACCEPT_DATE, ");
        parser.addSQL("T.STATUS, ");
//        parser.addSQL("P.CUST_NAME AS JT_CUST_NAME, ");
//        parser.addSQL("P.GROUP_ID, ");
        parser.addSQL("P1.MERCH_SPEC_CODE, ");
        parser.addSQL("P1.PRODUCT_SPEC_CODE, ");
        parser.addSQL("B.SUBSCRIBE_STATE ");
        parser.addSQL("FROM  ");
        parser.addSQL("TF_B_TRADE_GRP_MERCH_MEB T, ");
        parser.addSQL("TF_B_TRADE               B, ");
//        parser.addSQL("TF_F_CUST_GROUP          P, ");
        parser.addSQL("TF_B_TRADE_GRP_MERCHP    P1 ");
        parser.addSQL("WHERE T.TRADE_ID = B.TRADE_ID ");
        parser.addSQL("AND B.CUST_ID_B = :CUST_ID ");
        parser.addSQL("AND T.PRODUCT_OFFER_ID = P1.PRODUCT_OFFER_ID ");
        parser.addSQL("AND B.ACCEPT_DATE > TO_DATE(:START_DATE, 'YYYY-MM-DD') ");
        parser.addSQL("AND B.ACCEPT_DATE < TO_DATE(:END_DATE, 'YYYY-MM-DD') ");
        parser.addSQL("AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL("AND T.EC_SERIAL_NUMBER = :EC_SERIAL_NUMBER ");
        parser.addSQL("AND B.ORDER_ID = :PRODUCT_ORDER_ID ");
        parser.addSQL("AND T.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
//        parser.addSQL("AND P.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND SYSDATE >= T.START_DATE ");
        parser.addSQL("AND SYSDATE <= T.END_DATE ");
        parser.addSQL("AND SYSDATE >= P1.START_DATE ");
        parser.addSQL("AND SYSDATE <= P1.END_DATE ");
        parser.addSQL("AND (B.SUBSCRIBE_STATE = 'M' OR B.SUBSCRIBE_STATE = '0' OR B.SUBSCRIBE_STATE = 'W')");

        IDataset infos = Dao.qryByParse(parser, pagination,Route.getJourDb(BizRoute.getTradeEparchyCode()));
        
        for (int i = 0; i < infos.size(); i++)
        {
            String custId = infos.getData(i).getString("CUST_ID_B","");
            IData groupInfo = UcaInfoQry.qryCustInfoByCustId(custId);
            infos.getData(i).put("JT_CUST_NAME", groupInfo.getString("CUST_NAME", ""));
            infos.getData(i).put("GROUP_ID", groupInfo.getString("GROUP_ID", ""));
        } 
        
        return infos;
    }
    

    /**
     * @Description:一级BBOSS业务成员订购处理查询---关联td_b_trade表
     * @author liuxx3
     * @date 2014-08-04
     */
    public static IDataset qryBBossMebB(String group_id, String serial_number, String product_offer_id, String ec_serial_number, String product_order_id, String state, String start_date, String end_date, Pagination pagination) throws Exception
    {

        String cust_id = "";
        if(group_id!=null&&!group_id.equals("")){
        	IDataset groupInfos = GrpInfoQry.queryGroupCustInfoByGroupId(group_id);
        	if(!groupInfos.isEmpty()){
        		cust_id = groupInfos.getData(0).getString("CUST_ID","");
        	}
        }
    	
    	IData param = new DataMap();
        param.put("CUST_ID", cust_id);
        param.put("SERIAL_NUMBER", serial_number);
        param.put("PRODUCT_OFFER_ID", product_offer_id);
        param.put("EC_SERIAL_NUMBER", ec_serial_number);
        param.put("PRODUCT_ORDER_ID", product_order_id);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT ");
        parser.addSQL("T.TRADE_ID, ");
        parser.addSQL("B.ORDER_ID, ");
        parser.addSQL("T.MODIFY_TAG AS MODIFY_TAG_NUM, ");
        parser.addSQL("T.PRODUCT_ORDER_ID, ");
        parser.addSQL("T.SERIAL_NUMBER, ");
        parser.addSQL("B.CUST_ID, ");
        parser.addSQL("B.CUST_ID_B, ");
        parser.addSQL("B.CUST_NAME AS SER_CUST_NAME, ");
        parser.addSQL("T.EC_SERIAL_NUMBER, ");
        parser.addSQL("T.PRODUCT_OFFER_ID, ");
        parser.addSQL("B.TRADE_STAFF_ID, ");
        parser.addSQL("B.FINISH_DATE, ");
        parser.addSQL("B.ACCEPT_DATE, ");
        parser.addSQL("T.STATUS, ");
//        parser.addSQL("P.CUST_NAME AS JT_CUST_NAME, ");
//        parser.addSQL("P.GROUP_ID, ");
        parser.addSQL("P1.MERCH_SPEC_CODE, ");
        parser.addSQL("P1.PRODUCT_SPEC_CODE, ");
        parser.addSQL("B.SUBSCRIBE_STATE ");
        parser.addSQL("FROM  ");
        parser.addSQL("TF_B_TRADE_GRP_MERCH_MEB T, ");
        parser.addSQL("TF_B_TRADE               B, ");
//        parser.addSQL("TF_F_CUST_GROUP          P, ");
        parser.addSQL("TF_B_TRADE_GRP_MERCHP    P1 ");
        parser.addSQL("WHERE T.TRADE_ID = B.TRADE_ID ");
        parser.addSQL("AND B.CUST_ID_B = :CUST_ID ");
        parser.addSQL("AND T.PRODUCT_OFFER_ID = P1.PRODUCT_OFFER_ID ");
        parser.addSQL("AND B.ACCEPT_DATE > TO_DATE(:START_DATE, 'YYYY-MM-DD') ");
        parser.addSQL("AND B.ACCEPT_DATE < TO_DATE(:END_DATE, 'YYYY-MM-DD') ");
        parser.addSQL("AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL("AND T.EC_SERIAL_NUMBER = :EC_SERIAL_NUMBER ");
        parser.addSQL("AND B.ORDER_ID = :PRODUCT_ORDER_ID ");
        parser.addSQL("AND T.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
//        parser.addSQL("AND P.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND SYSDATE >= T.START_DATE ");
        parser.addSQL("AND SYSDATE <= T.END_DATE ");
        parser.addSQL("AND SYSDATE >= P1.START_DATE ");
        parser.addSQL("AND SYSDATE <= P1.END_DATE ");

        if ("1".equals(state))
        {
            parser.addSQL("AND B.SUBSCRIBE_STATE = 'M' ");
        }
        else if ("2".equals(state))
        {
            parser.addSQL("AND B.SUBSCRIBE_STATE = '0' ");
        }
        else if ("3".equals(state))
        {
            parser.addSQL("AND B.SUBSCRIBE_STATE = 'W' ");
        }

        IDataset infos = Dao.qryByParse(parser, pagination,Route.getJourDb(BizRoute.getTradeEparchyCode()));
        
        for (int i = 0; i < infos.size(); i++)
        {
            String custId = infos.getData(i).getString("CUST_ID_B","");
            IData groupInfo = UcaInfoQry.qryCustInfoByCustId(custId);
            infos.getData(i).put("JT_CUST_NAME", groupInfo.getString("CUST_NAME", ""));
            infos.getData(i).put("GROUP_ID", groupInfo.getString("GROUP_ID", ""));
        } 
        
        return infos;
    }
    

    /**
     * @Description:一级BBOSS业务成员订购处理查询 ---关联td_bh_trade表
     * @author liuxx3
     * @date 2014-08-04
     */
    public static IDataset qryBBossMebBH(String group_id, String serial_number, String product_offer_id, String ec_serial_number, String product_order_id, String start_date, String end_date, Pagination pagination) throws Exception
    {

        String cust_id = "";
        if(group_id!=null&&!group_id.equals("")){
        	IDataset groupInfos = GrpInfoQry.queryGroupCustInfoByGroupId(group_id);
        	if(!groupInfos.isEmpty()){
        		cust_id = groupInfos.getData(0).getString("CUST_ID","");
        	}
        }

        IData param = new DataMap();
        param.put("CUST_ID", cust_id);
        param.put("SERIAL_NUMBER", serial_number);
        param.put("PRODUCT_OFFER_ID", product_offer_id);
        param.put("EC_SERIAL_NUMBER", ec_serial_number);
        param.put("PRODUCT_ORDER_ID", product_order_id);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT ");
        parser.addSQL("T.TRADE_ID, ");
        parser.addSQL("B.ORDER_ID, ");
        parser.addSQL("T.MODIFY_TAG AS MODIFY_TAG_NUM, ");
        parser.addSQL("T.PRODUCT_ORDER_ID, ");
        parser.addSQL("T.SERIAL_NUMBER, ");
        parser.addSQL("B.CUST_ID, ");
        parser.addSQL("B.CUST_ID_B, ");
        parser.addSQL("B.CUST_NAME AS SER_CUST_NAME, ");
        parser.addSQL("T.EC_SERIAL_NUMBER, ");
        parser.addSQL("T.PRODUCT_OFFER_ID, ");
        parser.addSQL("B.TRADE_STAFF_ID, ");
        parser.addSQL("B.FINISH_DATE, ");
        parser.addSQL("B.ACCEPT_DATE, ");
        parser.addSQL("T.STATUS, ");
//        parser.addSQL("P.CUST_NAME AS JT_CUST_NAME, ");
//        parser.addSQL("P.GROUP_ID, ");
        parser.addSQL("P1.MERCH_SPEC_CODE, ");
        parser.addSQL("P1.PRODUCT_SPEC_CODE, ");
        parser.addSQL("B.SUBSCRIBE_STATE  ");
        parser.addSQL("FROM  ");
        parser.addSQL("TF_B_TRADE_GRP_MERCH_MEB T, ");
        parser.addSQL("TF_BH_TRADE              B, ");
//        parser.addSQL("TF_F_CUST_GROUP          P, ");
        parser.addSQL("TF_B_TRADE_GRP_MERCHP    P1 ");
        parser.addSQL("WHERE T.TRADE_ID = B.TRADE_ID ");
        parser.addSQL("AND B.CUST_ID_B = :CUST_ID ");
        parser.addSQL("AND T.PRODUCT_OFFER_ID = P1.PRODUCT_OFFER_ID ");
        parser.addSQL("AND B.ACCEPT_DATE > TO_DATE(:START_DATE, 'YYYY-MM-DD') ");
        parser.addSQL("AND B.ACCEPT_DATE < TO_DATE(:END_DATE, 'YYYY-MM-DD') ");
        parser.addSQL("AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL("AND T.EC_SERIAL_NUMBER = :EC_SERIAL_NUMBER ");
        parser.addSQL("AND B.ORDER_ID = :PRODUCT_ORDER_ID ");
        parser.addSQL("AND T.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
//        parser.addSQL("AND P.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND SYSDATE >= T.START_DATE ");
        parser.addSQL("AND SYSDATE <= T.END_DATE ");
        parser.addSQL("AND SYSDATE >= P1.START_DATE ");
        parser.addSQL("AND SYSDATE <= P1.END_DATE ");
        parser.addSQL("AND B.SUBSCRIBE_STATE = '9' ");

        IDataset infos = Dao.qryByParse(parser, pagination,Route.getJourDb(BizRoute.getTradeEparchyCode()));
        
        for (int i = 0; i < infos.size(); i++)
        {
            String custId = infos.getData(i).getString("CUST_ID_B","");
            IData groupInfo = UcaInfoQry.qryCustInfoByCustId(custId);
            infos.getData(i).put("JT_CUST_NAME", groupInfo.getString("CUST_NAME", ""));
            infos.getData(i).put("GROUP_ID", groupInfo.getString("GROUP_ID", ""));
        } 
        
        return infos;
    }

    /**
     * @Description:一级BBOSS业务成员订购处理查询
     * @author jch
     * @date 2009-8-10
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossMebOld(String state, String serialNumber, String startDate, String endDate, String ecSerialNumber, String orderId, String productOfferId, String groupId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("STATE", state);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("EC_SERIAL_NUMBER", ecSerialNumber);
        param.put("ORDER_ID", orderId);
        param.put("PRODUCT_OFFER_ID", productOfferId);
        param.put("GROUP_ID", groupId);

        SQLParser parser = new SQLParser(param);

        parser
                .addSQL(" SELECT t2.group_id,t2.trade_id,t2.order_id,case when t2.modify_tag='0' then '增加' when t2.modify_tag='1' then '删除' when t2.modify_tag='2' then '修改' end modify_tag,t2.product_order_id,t2.cust_id,t2.cust_id_b,t2.serial_number,t2.jt_cust_name,t2.ec_serial_number,t2.product_offer_id,t2.trade_staff_id,t2.finish_date,t2.accept_date");
        parser.addSQL(" ,t2.ser_cust_name,t2.status,t2.merch_spec_code,t2.product_spec_code,p2.pospecname,p3.productspecname,case when t2.state='0' then '处理成功' when t2.state='1' then '处理失败' else '处理进行中' end  state ");
        parser.addSQL(" FROM ( ");
        parser.addSQL(" SELECT t1.trade_id,t1.order_id,t1.modify_tag,t1.product_order_id,t1.cust_id,t1.cust_id_b,t1.serial_number,t1.ec_serial_number,t1.product_offer_id,t1.trade_staff_id,t1.finish_date,t1.accept_date");
        parser.addSQL(" ,t1.ser_cust_name,t1.status,p1.merch_spec_code,p1.product_spec_code,p.cust_name as jt_cust_name,t1.state,p.group_id ");
        parser.addSQL(" FROM ( ");
        parser.addSQL(" SELECT t.inst_id,t.trade_id,b.order_id,t.modify_tag,t.product_order_id,t.serial_number,b.cust_id,b.cust_id_b,b.cust_name as ser_cust_name ,t.ec_serial_number,t.product_offer_id,b.trade_staff_id,b.finish_date,b.accept_date");
        parser.addSQL("  ,t.status,:STATE as state");
        parser.addSQL("  FROM tf_b_trade_grp_merch_meb t,tf_b_trade b");
        parser.addSQL(" WHERE  t.trade_id=b.trade_id");
        if (null != param.get("STATE") && param.get("STATE") == "0")
        {
            parser.addSQL(" AND sysdate>=t.start_date");
            parser.addSQL(" AND sysdate<=t.end_date");

        }
        else if (null != param.get("STATE") && param.get("STATE") == "1")
        {

            parser.addSQL(" AND sysdate>t.end_date");
            parser.addSQL(" AND  b.cancel_tag=1 ");

        }
        else if (null != param.get("STATE") && param.get("STATE") == "2")
        {
            parser.addSQL(" AND sysdate<t.end_date");
            parser.addSQL(" AND  b.cancel_tag=0 ");
        }

        parser.addSQL(" AND b.accept_date>to_date(:START_DATE,'yyyy-mm-dd')");
        parser.addSQL(" AND b.accept_date<to_date(:END_DATE,'yyyy-mm-dd')");

        parser.addSQL(" AND t.serial_number=:SERIAL_NUMBER");

        parser.addSQL(" AND t.ec_serial_number=:EC_SERIAL_NUMBER");
        parser.addSQL(" AND b.order_id=:ORDER_ID");
        parser.addSQL(" AND t.product_offer_id=:PRODUCT_OFFER_ID");

        parser.addSQL(" ) t1,tf_f_cust_group p,tf_b_trade_grp_merchp p1");
        parser.addSQL(" WHERE t1.cust_id_b=p.cust_id");
        parser.addSQL("  AND  t1.trade_id=p1.trade_id");
        parser.addSQL(" AND p.group_id=:GROUP_ID");
        parser.addSQL(" )   t2,td_f_po p2,td_f_poproduct p3");
        parser.addSQL(" WHERE t2.merch_spec_code=p2.pospecnumber");
        parser.addSQL(" AND t2.product_spec_code=p3.productspecnumber");

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * @Description:一级BBOSS业务成员签约关系订购状态查询
     * @author liuxx3
     * @date 2014-07-14
     */
    public static IDataset qryMerchMebInfoBySnPoofferIdStateEcSnCustId(String groupId, String serial_number, String product_offer_id, String state, String ec_serial_number, Pagination pagination) throws Exception
    {

        String cust_id = "";
        if(groupId!=null&&!groupId.equals("")){
        	IDataset groupInfos = GrpInfoQry.queryGroupCustInfoByGroupId(groupId);
        	if(!groupInfos.isEmpty()){
        		cust_id = groupInfos.getData(0).getString("CUST_ID","");
        	}
        }
    	
    	IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("PRODUCT_OFFER_ID", product_offer_id);
        param.put("STATE", state);
        param.put("EC_SERIAL_NUMBER", ec_serial_number);
        param.put("CUST_ID", cust_id);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT  ");
        parser.addSQL("t.start_date, ");
        parser.addSQL("t.end_date, ");
        parser.addSQL("t.update_staff_id, ");
        parser.addSQL("tt.cust_id, ");
        parser.addSQL("t.ec_serial_number, ");
        parser.addSQL("t.product_offer_id, ");
        parser.addSQL("t.serial_number, ");
        parser.addSQL("case ");
        parser.addSQL("when t.status = 'A' then ");
        parser.addSQL("'正常订购' ");
        parser.addSQL("when t.status = 'N' then ");
        parser.addSQL("'暂停' ");
        parser.addSQL("when t.status = 'Z' then ");
        parser.addSQL("'销户' ");
        parser.addSQL("end status, ");
        parser.addSQL("tt.trade_staff_id, ");
        parser.addSQL("tt.cust_id_b, ");
        parser.addSQL("tt.cust_name AS user_name ");
//        parser.addSQL("cg.group_id, ");
//        parser.addSQL("cg.CITY_CODE, ");
//        parser.addSQL("cg.CUST_NAME AS JT_CUST_NAME ");
        parser.addSQL("FROM tf_b_trade_grp_merch_meb t, tf_bh_trade tt ");
        parser.addSQL("WHERE t.trade_id = tt.trade_id ");
        parser.addSQL("AND t.product_offer_id = :PRODUCT_OFFER_ID ");
        parser.addSQL("AND t.serial_number = :SERIAL_NUMBER ");
        parser.addSQL("AND t.ec_serial_number = :EC_SERIAL_NUMBER ");
        parser.addSQL("AND tt.cust_id_b = :CUST_ID ");
//        parser.addSQL("AND cg.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND sysdate >= t.start_date ");
        parser.addSQL("AND t.accept_month = tt.accept_month ");

        if (null != param.get("STATE") && "N".equals(param.getString("STATE"))) // 暂停
        {
            parser.addSQL(" AND t.status='N'");
            parser.addSQL(" AND sysdate<=t.end_date");
        }
        else if (null != param.get("STATE") && "Z".equals(param.getString("STATE")))// 销户
        {
            parser.addSQL(" AND t.status='Z'");
        }
        else if (null != param.get("STATE") && "A".equals(param.getString("STATE")))// 正常商用
        {
            parser.addSQL(" AND t.status='A'");
            parser.addSQL(" AND sysdate<=t.end_date");
        }
        else
        {
            parser.addSQL(" AND sysdate<=t.end_date");
        }

        IDataset MebQyInfos = Dao.qryByParse(parser, pagination,Route.getJourDb(BizRoute.getTradeEparchyCode()));
        
        for (int i = 0; i < MebQyInfos.size(); i++)
        {    
            String custId = MebQyInfos.getData(i).getString("CUST_ID_B","");
            IData groupInfo = UcaInfoQry.qryCustInfoByCustId(custId);
            MebQyInfos.getData(i).put("JT_CUST_NAME", groupInfo.getString("CUST_NAME", ""));
            MebQyInfos.getData(i).put("GROUP_ID", groupInfo.getString("GROUP_ID", ""));
            MebQyInfos.getData(i).put("CITY_CODE", groupInfo.getString("CITY_CODE", ""));     
        }
        return MebQyInfos;
     }
}
