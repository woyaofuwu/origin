
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserGrpMerchInfoQry
{

    /**
     * 根据商品订购关系查询非暂停状态（N）的user_merch表信息
     * 
     * @param MerchOfferId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMerchInfoByMerchOfferId(String MerchOfferId) throws Exception
    {
        IData param = new DataMap();
        param.put("MERCH_OFFER_ID", MerchOfferId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, ");
        sql.append("to_char(USER_ID) USER_ID, ");
        sql.append("MERCH_SPEC_CODE, ");
        sql.append("MERCH_ORDER_ID, ");
        sql.append("MERCH_OFFER_ID, ");
        sql.append("GROUP_ID, ");
        sql.append("STATUS, ");
        sql.append("to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
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
        sql.append("FROM TF_F_USER_GRP_MERCH T ");
        sql.append("WHERE t.MERCH_OFFER_ID = :MERCH_OFFER_ID ");
        sql.append("AND SYSDATE > END_DATE ");
        sql.append("AND SYSDATE < END_DATE ");
        sql.append("AND STATUS <> 'N' ");

        return Dao.qryBySql(sql, param);
    }

    /**
     * @Description:根据商品订单号查询归档商品台帐信息
     * @author chenyi
     * @date
     * @param merchOfferId
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchBHtradeInfoByMerchOfferId(String merchOfferId, String orderid, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("MERCH_OFFER_ID", merchOfferId);
        param.put("ORDER_ID", orderid);
        String userId = "";

        SQLParser parser = new SQLParser(param);
        
        parser.addSQL(" select a.* ");
        parser.addSQL(" from tf_f_user_grp_merch a ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.MERCH_OFFER_ID = :MERCH_OFFER_ID ");
        parser.addSQL(" and rownum = 1");
        IDataset merchs = Dao.qryByParse(parser,Route.CONN_CRM_CG);
        
        if(!merchs.isEmpty()){
        	userId = merchs.getData(0).getString("USER_ID", "");
        	param.put("USER_ID", userId);
        } 
        
        SQLParser newparser = new SQLParser(param);
        newparser.addSQL(" select b.* ");
        newparser.addSQL(" from tf_bh_trade b ");
        newparser.addSQL(" where b.user_id = :USER_ID");
        newparser.addSQL(" and b.order_id = :ORDER_ID ");
        newparser.addSQL(" and rownum = 1");
        return Dao.qryByParse(newparser,Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @Description: 从集团库 通过产品订购关系编码查询用户订购产品信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 上午9:31:26 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset qryMerchInfoByMerchOfferId(String merch_offer_id) throws Exception
    {
        IData param = new DataMap();
        param.put("MERCH_OFFER_ID", merch_offer_id);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, ");
        sql.append("to_char(USER_ID) USER_ID, ");
        sql.append("MERCH_SPEC_CODE, ");
        sql.append("MERCH_ORDER_ID, ");
        sql.append("MERCH_OFFER_ID, ");
        sql.append("GROUP_ID, ");
        sql.append("STATUS, ");
        sql.append("to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
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
        sql.append("FROM TF_F_USER_GRP_MERCH T ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND t.Merch_Offer_Id = :MERCH_OFFER_ID ");
        sql.append("AND SYSDATE BETWEEN t.START_DATE AND t.END_DATE ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }

    /**
     * @Function: getUserPurchase
     * @Description: 从集团库 查询用户订购商品信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 上午9:38:37 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset qryMerchInfoByUserIdMerchSpecStatus(String user_id, String merch_spec_code, String status, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("MERCH_SPEC_CODE", merch_spec_code);
        param.put("STATUS", status);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("select a.INST_ID, ");
        sql.append("a.PARTITION_ID, ");
        sql.append("to_char(a.USER_ID) USER_ID, ");
        sql.append("a.SERIAL_NUMBER, ");
        sql.append("a.MERCH_SPEC_CODE, ");
        sql.append("a.MERCH_ORDER_ID, ");
        sql.append("a.MERCH_OFFER_ID, ");
        sql.append("a.GROUP_ID, ");
        sql.append("a.OPR_SOURCE, ");
        sql.append("a.BIZ_MODE, ");
        sql.append("a.STATUS, ");
        sql.append("a.HOST_COMPANY, ");
        sql.append("to_char(a.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(a.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("to_char(a.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("a.UPDATE_STAFF_ID, ");
        sql.append("a.UPDATE_DEPART_ID, ");
        sql.append("a.REMARK, ");
        sql.append("a.RSRV_NUM1, ");
        sql.append("a.RSRV_NUM2, ");
        sql.append("a.RSRV_NUM3, ");
        sql.append("to_char(a.RSRV_NUM4) RSRV_NUM4, ");
        sql.append("to_char(a.RSRV_NUM5) RSRV_NUM5, ");
        sql.append("a.RSRV_STR1, ");
        sql.append("a.RSRV_STR2, ");
        sql.append("a.RSRV_STR3, ");
        sql.append("a.RSRV_STR4, ");
        sql.append("a.RSRV_STR5, ");
        sql.append("to_char(a.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("to_char(a.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("to_char(a.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("a.RSRV_TAG1, ");
        sql.append("a.RSRV_TAG2, ");
        sql.append("a.RSRV_TAG3 ");
        sql.append("from TF_F_USER_GRP_MERCH a ");
        sql.append("where a.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("and (a.MERCH_SPEC_CODE = :MERCH_SPEC_CODE or :MERCH_SPEC_CODE IS NULL) ");
        sql.append("and (a.STATUS = :STATUS or :STATUS IS NULL) ");
        sql.append("and a.end_date > sysdate ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }
    /**
     * @Function: getUserPurchase
     * @Description: 从集团库 查询用户订购商品信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 上午9:38:37 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset qryMerchInfoByUserIdMerchSpecStatusJkdt(String user_id, String merch_spec_code, String status, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("MERCH_SPEC_CODE", merch_spec_code);
        param.put("STATUS", status);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("select a.INST_ID, ");
        sql.append("a.PARTITION_ID, ");
        sql.append("to_char(a.USER_ID) USER_ID, ");
        sql.append("a.SERIAL_NUMBER, ");
        sql.append("a.MERCH_SPEC_CODE, ");
        sql.append("a.MERCH_ORDER_ID, ");
        sql.append("a.MERCH_OFFER_ID, ");
        sql.append("a.GROUP_ID, ");
        sql.append("a.OPR_SOURCE, ");
        sql.append("a.BIZ_MODE, ");
        sql.append("b.svc_STATUS AS STATUS, ");
        sql.append("a.HOST_COMPANY, ");
        sql.append("to_char(a.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(a.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("to_char(a.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("a.UPDATE_STAFF_ID, ");
        sql.append("a.UPDATE_DEPART_ID, ");
        sql.append("a.REMARK, ");
        sql.append("a.RSRV_NUM1, ");
        sql.append("a.RSRV_NUM2, ");
        sql.append("a.RSRV_NUM3, ");
        sql.append("to_char(a.RSRV_NUM4) RSRV_NUM4, ");
        sql.append("to_char(a.RSRV_NUM5) RSRV_NUM5, ");
        sql.append("a.RSRV_STR1, ");
        sql.append("a.RSRV_STR2, ");
        sql.append("a.RSRV_STR3, ");
        sql.append("a.RSRV_STR4, ");
        sql.append("a.RSRV_STR5, ");
        sql.append("to_char(a.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("to_char(a.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("to_char(a.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("a.RSRV_TAG1, ");
        sql.append("a.RSRV_TAG2, ");
        sql.append("a.RSRV_TAG3 ");
        sql.append("from TF_F_USER_ECRECEP_OFFER a,TF_F_USER_ECRECEP_PROCEDURE b ");
        sql.append("where a.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("and a.USER_ID = b.USER_ID ");
        sql.append("and (a.MERCH_SPEC_CODE = :MERCH_SPEC_CODE or :MERCH_SPEC_CODE IS NULL) ");
        sql.append("and a.end_date > sysdate ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }

    /**
     * @Function: qryUserMerchInfo
     * @Description: 根据集团ID和BOSS侧产品ID查询 TF_F_USER_GRP_MERCH 数据
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 上午9:44:40 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset qryUserMerchInfo(String groupId, String productId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_ID", productId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("select ");
        sql.append("um.* ");
        sql.append("from tf_f_cust_group c, ");
        sql.append("tf_f_user u, ");
        sql.append("tf_f_user_product   up, ");
        sql.append("tf_f_user_grp_merch um ");
        sql.append("where c.cust_id = u.cust_id ");
        sql.append("and c.group_id = :GROUP_ID ");
        sql.append("and u.user_id = up.user_id ");
        sql.append("and u.user_id = um.user_id ");
        sql.append("and u.partition_id = up.partition_id ");
        sql.append("and u.partition_id = um.partition_id ");
        sql.append("and u.partition_id = mod(u.user_id, 10000) ");
        sql.append("and up.product_id = :PRODUCT_ID ");
        sql.append("and c.remove_tag = '0' ");
        sql.append("and u.remove_tag = '0' ");
        sql.append("and up.main_tag = '1' ");
        sql.append("and (sysdate between up.start_date and up.end_date) ");
        sql.append("and um.status = 'A' ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }

    /**
     * 提供给端对端调用，进行集团变更或是集团注销时，查询用户的相关信息，资费、资费参数、产品属性
     * 
     * @author liuxx3
     * @date 2014 08-28
     */
    public static IDataset qryUserMerchInfoNew(String groupId, String productId, String userId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_ID", productId);
        param.put("USER_ID", userId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select ");
        parser.addSQL("um.* ");
        parser.addSQL("from tf_f_cust_group c, ");
        parser.addSQL("tf_f_user u, ");
        parser.addSQL("tf_f_user_product   up, ");
        parser.addSQL("tf_f_user_grp_merch um ");
        parser.addSQL("where c.cust_id = u.cust_id ");
        parser.addSQL("and c.group_id = :GROUP_ID ");
        parser.addSQL("and u.user_id = up.user_id ");
        parser.addSQL("and u.user_id = um.user_id ");
        parser.addSQL("and u.partition_id = up.partition_id ");
        parser.addSQL("and u.partition_id = um.partition_id ");
        parser.addSQL("and u.partition_id = mod(u.user_id, 10000) ");
        parser.addSQL("and up.product_id = :PRODUCT_ID ");
        parser.addSQL("and c.remove_tag = '0' ");
        parser.addSQL("and u.remove_tag = '0' ");
        parser.addSQL("and up.main_tag = '1' ");
        parser.addSQL("and (sysdate between up.start_date and up.end_date) ");
        parser.addSQL("and um.status = 'A' ");
        parser.addSQL("and u.user_id = :USER_ID ");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }

    /**
     * todo code_code 表里没有SEL_BY_OFFERID 根据merch_offer_id查询商品订购关系信息
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset selMerchinfoByPk(IData param) throws Exception
    {
        StringBuilder sql = new StringBuilder(1000);

        sql.append("Select ugmp.merch_spec_code, ");
        sql.append("ugmp.product_offer_id, ");
        sql.append("ugmp.product_spec_code, ");
        sql.append("ugmp.status, ");
        sql.append("ugmp.start_date, ");
        sql.append("ugmp.end_date, ");
        sql.append("ugm.merch_order_id ");
        sql.append("From tf_f_user_grp_merch ugm, TF_F_USER_GRP_MERCHP ugmp ");
        sql.append("Where 1 = 1 ");
        sql.append("And ugmp.group_id = :GROUP_ID ");
        sql.append("And ugmp.MERCH_SPEC_CODE = :MERCH_SPEC_CODE ");
        sql.append("And ugmp.PRODUCT_SPEC_CODE = :PRODUCT_SPEC_CODE ");
        sql.append("And ugm.group_id = ugmp.group_id ");
        sql.append("And ugmp.MERCH_SPEC_CODE = ugm.MERCH_SPEC_CODE ");
        sql.append("And Sysdate between ugmp.start_date And ugmp.end_date ");
        sql.append("And Sysdate between ugm.start_date And ugm.end_date ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }

}
