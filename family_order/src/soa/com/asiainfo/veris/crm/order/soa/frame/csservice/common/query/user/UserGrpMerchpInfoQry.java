
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoProductQry;

public class UserGrpMerchpInfoQry
{

    /**
     * @Function: qryBBossBizEcMeb
     * @Description: 从集团库 集团客户订购一级BBOSS产品清单查询
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 上午9:43:35 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset qryBBossBizEcMeb(String group_id, String cust_name, String state, String product_offer_id, String productspecnumber, String pospecnumber, String ec_serial_number, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", group_id);
        param.put("CUST_NAME", cust_name);
        param.put("STATE", state);
        param.put("PRODUCT_OFFER_ID", product_offer_id);
        param.put("PRODUCTSPECNUMBER", productspecnumber);
        param.put("POSPECNUMBER", pospecnumber);
        param.put("EC_SERIAL_NUMBER", ec_serial_number);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT t.inst_id,t.group_id,cg.cust_id,cg.cust_name,t.merch_spec_code,t.product_spec_code,t.product_offer_id,cg.city_code,t.start_date,t.end_date,t.update_staff_id,u.serial_number,t.status");
        parser.addSQL(" FROM tf_f_user_grp_merchp t,tf_f_cust_group cg,tf_f_user u");
        parser.addSQL(" WHERE t.group_id=cg.group_id");
        parser.addSQL(" AND t.user_id=u.user_id ");
        parser.addSQL(" AND t.group_id=:GROUP_ID ");

        if ("N".equals(param.get("STATE")))
        {
            parser.addSQL(" AND t.status='N'");
            parser.addSQL(" AND sysdate>=t.start_date");
            parser.addSQL(" AND SYSDATE<=t.end_date");

        }
        else if ("D".equals(param.get("STATE")))
        {

            parser.addSQL(" AND t.status='D'");
            parser.addSQL(" AND sysdate>=t.start_date");
        }
        else
        {
            parser.addSQL(" AND t.status=:STATE");
            parser.addSQL(" AND sysdate>=t.start_date");
            parser.addSQL(" AND sysdate<=t.end_date");

        }

        // parser.addSQL(" AND cg.CUST_ID=:CUST_ID");
        parser.addSQL(" AND cg.cust_name LIKE '%' || :CUST_NAME || '%'");
        parser.addSQL(" AND t.product_offer_id=:PRODUCT_OFFER_ID");
        parser.addSQL(" AND t.product_spec_code=:PRODUCTSPECNUMBER");
        parser.addSQL(" AND t.merch_spec_code=:POSPECNUMBER");
        parser.addSQL(" AND u.serial_number=:EC_SERIAL_NUMBER");
        IDataset resultset = Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(resultset))
            return resultset;

        for (int i = 0; i < resultset.size(); i++)
        {
            IData result = resultset.getData(i);
            result.put("MERCH_SPEC_NAME", PoInfoQry.getPOSpecNameByPoSpecNumber(result.getString("MERCH_SPEC_CODE")));
            result.put("STATUS_NAME", PoInfoQry.getMerchStateNameByStateCode(result.getString("STATUS")));
            result.put("UPDATE_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(result.getString("UPDATE_STAFF_ID")));
            IDataset proInfos = PoProductQry.qryProductInfosByProductSpecNumber(result.getString("MERCH_SPEC_CODE"), result.getString("PRODUCT_SPEC_CODE"));
            if (IDataUtil.isEmpty(proInfos))
            {
                result.put("PRODUCT_SPEC_NAME", "");
            }
            else
            {
                IData proInfo = proInfos.getData(0);
                result.put("PRODUCT_SPEC_NAME", proInfo.getString("PRODUCTSPECNAME", ""));
            }

        }
        return resultset;
    }

    /**
     * 根据GROUP_ID、MERCH_SPEC_CODE、PRDOUCT_SPEC_CODE、USER_ID查询tf_f_user_grp_merchp表信息
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchpInfoByGroupIdMerchScProductScUserId(String userId, String groupId, String merchSpecCode, String productSpecCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("GROUP_ID", groupId);
        param.put("MERCH_SPEC_CODE", merchSpecCode);
        param.put("PRODUCT_SPEC_CODE", productSpecCode);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("select t.product_order_id,t.product_offer_id ");
        sql.append("FROM tf_f_user_grp_merchp t ");
        sql.append("WHERE 1=1 ");
        sql.append("AND t.GROUP_ID = :GROUP_ID ");
        sql.append("AND t.MERCH_SPEC_CODE=:MERCH_SPEC_CODE ");
        sql.append("AND t.PRODUCT_SPEC_CODE=:PRODUCT_SPEC_CODE ");
        sql.append("AND t.USER_ID=:USER_ID ");
        sql.append("AND SYSDATE > t.START_DATE AND SYSDATE<t.END_DATE ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }

    /**
     * @Description:一级BBOSS业务成员用户清单查询
     * @author liuxx3 modify by ft 2014-08-01
     * @date 2014 -07-14
     */
    public static IDataset qryMerchpInfoByGroupIdUserIdPoSpecProductSpec(String group_id, String userId, String pospecnumber, String productspecnumber, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", group_id);
        param.put("USER_ID", userId);
        param.put("POSPECNUMBER", pospecnumber);
        param.put("PRODUCTSPECNUMBER", productspecnumber);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT PARTITION_ID, ");
        parser.addSQL("to_char(USER_ID) USER_ID, ");
        parser.addSQL("MERCH_SPEC_CODE, ");
        parser.addSQL("PRODUCT_SPEC_CODE, ");
        parser.addSQL("PRODUCT_ORDER_ID, ");
        parser.addSQL("PRODUCT_OFFER_ID, ");
        parser.addSQL("GROUP_ID, ");
        parser.addSQL("SERV_CODE, ");
        parser.addSQL("BIZ_ATTR, ");
        parser.addSQL("STATUS, ");
        parser.addSQL("to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL("to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        parser.addSQL("to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL("UPDATE_STAFF_ID, ");
        parser.addSQL("UPDATE_DEPART_ID, ");
        parser.addSQL("REMARK, ");
        parser.addSQL("RSRV_NUM1, ");
        parser.addSQL("RSRV_NUM2, ");
        parser.addSQL("RSRV_NUM3, ");
        parser.addSQL("to_char(RSRV_NUM4) RSRV_NUM4, ");
        parser.addSQL("to_char(RSRV_NUM5) RSRV_NUM5, ");
        parser.addSQL("RSRV_STR1, ");
        parser.addSQL("RSRV_STR2, ");
        parser.addSQL("RSRV_STR3, ");
        parser.addSQL("RSRV_STR4, ");
        parser.addSQL("RSRV_STR5, ");
        parser.addSQL("to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL("to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL("to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL("RSRV_TAG1, ");
        parser.addSQL("RSRV_TAG2, ");
        parser.addSQL("RSRV_TAG3 ");
        parser.addSQL("FROM TF_F_USER_GRP_MERCHP t ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND t.USER_ID = :USER_ID ");
        parser.addSQL("AND t.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND t.MERCH_SPEC_CODE = :POSPECNUMBER ");
        parser.addSQL("AND t.PRODUCT_SPEC_CODE = :PRODUCTSPECNUMBER ");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }

    /**
     * 从集团库 查询用户订购产品信息
     * 
     * @param group_id
     * @param product_spec_code
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchpInfoByGrpIDProductSpecStatus(String groupId, String product_spec_code, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_SPEC_CODE", product_spec_code);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("select a.INST_ID, ");
        sql.append("a.PARTITION_ID, ");
        sql.append("to_char(a.USER_ID) USER_ID, ");
        sql.append("a.MERCH_SPEC_CODE, ");
        sql.append("a.PRODUCT_SPEC_CODE, ");
        sql.append("a.PRODUCT_ORDER_ID, ");
        sql.append("a.PRODUCT_OFFER_ID, ");
        sql.append("a.GROUP_ID, ");
        sql.append("a.SERV_CODE, ");
        sql.append("a.BIZ_ATTR, ");
        sql.append("a.STATUS, ");
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
        sql.append("from TF_F_USER_GRP_MERCHP a ");
        sql.append(" where 1=1 ");
        sql.append("and (a.GROUP_ID = :GROUP_ID or :GROUP_ID IS NULL) ");
        sql.append("and (a.PRODUCT_SPEC_CODE = :PRODUCT_SPEC_CODE or ");
        sql.append(":PRODUCT_SPEC_CODE IS NULL) ");
        sql.append("and a.end_date > sysdate ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }

    /**
     * 从集团库 通过产品订购关系编码查询用户订购产品信息
     * 
     * @param product_offer_id
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchpInfoByProductOfferId(String product_offer_id) throws Exception
    {

        IData param = new DataMap();
        param.put("PRODUCT_OFFER_ID", product_offer_id);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, ");
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
        sql.append("FROM TF_F_USER_GRP_MERCHP t ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND t.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
        sql.append("AND SYSDATE BETWEEN t.START_DATE AND t.END_DATE ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }

    /**
     * 从集团库 查询用户订购产品信息
     * 
     * @param user_id
     * @param merch_spec_code
     * @param status
     * @param product_spec_code
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchpInfoByUserIdMerchSpecProductSpecStatus(String user_id, String merch_spec_code, String status, String product_spec_code, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("MERCH_SPEC_CODE", merch_spec_code);
        param.put("STATUS", status);
        param.put("PRODUCT_SPEC_CODE", product_spec_code);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("select a.INST_ID, ");
        sql.append("a.PARTITION_ID, ");
        sql.append("to_char(a.USER_ID) USER_ID, ");
        sql.append("a.MERCH_SPEC_CODE, ");
        sql.append("a.PRODUCT_SPEC_CODE, ");
        sql.append("a.PRODUCT_ORDER_ID, ");
        sql.append("a.PRODUCT_OFFER_ID, ");
        sql.append("a.GROUP_ID, ");
        sql.append("a.SERV_CODE, ");
        sql.append("a.BIZ_ATTR, ");
        sql.append("a.STATUS, ");
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
        sql.append("from TF_F_USER_GRP_MERCHP a ");
        sql.append("where a.USER_ID = :USER_ID ");
        sql.append("AND a.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("and (a.MERCH_SPEC_CODE = :MERCH_SPEC_CODE or :MERCH_SPEC_CODE IS NULL) ");
        sql.append("and (a.PRODUCT_SPEC_CODE = :PRODUCT_SPEC_CODE or ");
        sql.append(":PRODUCT_SPEC_CODE IS NULL) ");
        sql.append("and (a.status = :STATUS or :STATUS IS NULL) ");
        sql.append("and a.end_date > sysdate ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }

    /**
     * 查询商品下产品信息
     * 
     * @param userId
     * @param merchSpecCode
     * @param status
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchpInfoByUserIdMerchSpecStatus(String userId, String merchSpecCode, String status, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("MERCH_SPEC_CODE", merchSpecCode);
        param.put("STATUS", status);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("select ");
        sql.append("a.INST_ID, ");
        sql.append("a.PARTITION_ID, ");
        sql.append("to_char(a.USER_ID) USER_ID, ");
        sql.append("a.MERCH_SPEC_CODE, ");
        sql.append("a.PRODUCT_SPEC_CODE, ");
        sql.append("a.PRODUCT_ORDER_ID, ");
        sql.append("a.PRODUCT_OFFER_ID, ");
        sql.append("a.GROUP_ID, ");
        sql.append("a.SERV_CODE, ");
        sql.append("a.BIZ_ATTR, ");
        sql.append("a.STATUS, ");
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
        sql.append("from TF_F_USER_GRP_MERCHP a ");
        sql.append("where a.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("and a.MERCH_SPEC_CODE = :MERCH_SPEC_CODE ");
        sql.append("and a.status = :STATUS ");
        sql.append("and a.end_date > sysdate ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }

    /**
     * 关联查询merchP的产品状态 modify_liuxx3_20140524_01
     */

    public static IDataset selProMerchinfoStatus(String groupId, String productId) throws Exception
    {

        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_ID", productId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT ");
        sql.append("C.PRODUCT_ID,A.GROUP_ID,A.CUST_ID, ");
        sql.append("B.USER_ID,D.STATUS,D.USER_ID ");
        sql.append("FROM TF_F_CUST_GROUP A, ");
        sql.append("TF_F_USER B, ");
        sql.append("TF_F_USER_PRODUCT C , ");
        sql.append("TF_F_USER_GRP_MERCHP D ");
        sql.append("WHERE A.GROUP_ID = :GROUP_ID ");
        sql.append("AND C.PRODUCT_ID = :PRODUCT_ID ");
        sql.append("AND A.CUST_ID = B.CUST_ID ");
        sql.append("AND B.USER_ID = C.USER_ID ");
        sql.append("AND D.USER_ID = C.USER_ID ");
        sql.append("AND SYSDATE BETWEEN C.START_DATE AND C.END_DATE ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }
    
    public static IDataset qryMerchpInfoByUserId(String user_id) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, ");
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
        sql.append("FROM TF_F_USER_GRP_MERCHP t ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND t.USER_ID = :USER_ID ");
        sql.append("AND SYSDATE BETWEEN t.START_DATE AND t.END_DATE ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }
    public static IDataset qryJkdtMerchpInfoByUserId(String user_id) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, ");
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
        sql.append("FROM TF_F_USER_GRP_MERCHP t ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND t.USER_ID = :USER_ID ");
        sql.append("AND SYSDATE BETWEEN t.START_DATE AND t.END_DATE ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }
    
    /**
     * 根据集团产品订单号查询
     * @author liuxx3
     * @date 2015- 1-21
     */
    
    public static IDataset qryMerchpInfosByPro(String proNumber,Pagination pg)throws Exception{

        IData param = new DataMap();
        param.put("PRODUCT_ORDER_ID", proNumber);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, ");
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
        sql.append("FROM TF_F_USER_GRP_MERCHP t ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND t.PRODUCT_ORDER_ID = :PRODUCT_ORDER_ID ");
        sql.append("AND SYSDATE BETWEEN t.START_DATE AND t.END_DATE ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }
}
