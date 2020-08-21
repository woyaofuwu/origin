package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;


/**
 * @program: order_hunan
 * @description:
 * @create: 2018-10-09 10:14
 **/

public class UserEcrecepProductInfoQry {
    /**
     * @Function: getUserProductByOfferId
     * @Description: 从集团库 通过产品订购关系编码查询用户订购产品信息
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 上午9:34:38 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getUserEcrecepProductByOfferId(String product_offer_id) throws Exception
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
        sql.append("FROM TF_F_USER_ECRECEP_PRODUCT t ");
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
    public static IDataset qryEcrecepProductInfoByUserIdMerchSpecProductSpecStatus(String user_id, String merch_spec_code, String status, String product_spec_code, Pagination pagination) throws Exception
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
        sql.append("from TF_F_USER_ECRECEP_PRODUCT a ");
        sql.append("where a.USER_ID = :USER_ID ");
        sql.append("AND a.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("and (a.MERCH_SPEC_CODE = :MERCH_SPEC_CODE or :MERCH_SPEC_CODE IS NULL) ");
        sql.append("and (a.PRODUCT_SPEC_CODE = :PRODUCT_SPEC_CODE or ");
        sql.append(":PRODUCT_SPEC_CODE IS NULL) ");
        sql.append("and a.end_date > sysdate ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }

    public static IDataset qryEcrEceppInfosByPro(String proNumber,Pagination pg)throws Exception{

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
        sql.append("FROM TF_F_USER_ECRECEP_PRODUCT t ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND t.PRODUCT_ORDER_ID = :PRODUCT_ORDER_ID ");
        sql.append("AND SYSDATE BETWEEN t.START_DATE AND t.END_DATE ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }
    

    public static IDataset qryEcrEceppInfosByUserId(String userId)throws Exception{

        IData param = new DataMap();
        param.put("USER_ID", userId);

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
        sql.append("FROM TF_F_USER_ECRECEP_PRODUCT t ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND t.USER_ID = :USER_ID ");
        sql.append("AND SYSDATE BETWEEN t.START_DATE AND t.END_DATE ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }


    public static IDataset qryMerchpInfoByUserIdMerchSpecProductSpecStatus(String user_id, String merch_spec_code, String product_spec_code, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("MERCH_SPEC_CODE", merch_spec_code);
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
        sql.append("from TF_F_USER_ECRECEP_PRODUCT a ");
        sql.append("where a.USER_ID = :USER_ID ");
        sql.append("AND a.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("and (a.MERCH_SPEC_CODE = :MERCH_SPEC_CODE or :MERCH_SPEC_CODE IS NULL) ");
        sql.append("and (a.PRODUCT_SPEC_CODE = :PRODUCT_SPEC_CODE or ");
        sql.append(":PRODUCT_SPEC_CODE IS NULL) ");
        sql.append("and a.end_date > sysdate ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }
    
    /**
     * 从集团库 查询用户订购资费信息
     *
     * @param user_id
     * @return
     * @throws Exception
     */
    public static IDataset qryEcrecepDiscntByUserId(String user_id,String nextMonthDay) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("END_DATE", nextMonthDay);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("select a.partition_id, ");
        sql.append("a.user_id, ");
        sql.append("to_char(a.user_id_a) USER_ID_A, ");
        sql.append("a.discnt_code, ");
        sql.append("a.spec_tag, ");
        sql.append("a.relation_type_code, ");
        sql.append("a.inst_id, ");
        sql.append("a.campn_id, ");
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
        sql.append("from TF_F_USER_DISCNT a ");
        sql.append("where a.USER_ID = :USER_ID ");
        sql.append("AND a.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND END_DATE > TO_DATE(:END_DATE,'YYYY-MM-DD')");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }
    
    /**
     * 从集团库 查询用户订购资费信息
     *
     * @param user_id
     * @return
     * @throws Exception
     */
    public static IDataset qryEcrecepDiscntByUserIdAndRsrvNum1(String user_id,String rsrv_num1) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_NUM1", rsrv_num1);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("select a.partition_id, ");
        sql.append("a.user_id, ");
        sql.append("to_char(a.user_id_a) USER_ID_A, ");
        sql.append("a.discnt_code, ");
        sql.append("a.spec_tag, ");
        sql.append("a.relation_type_code, ");
        sql.append("a.inst_id, ");
        sql.append("a.campn_id, ");
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
        sql.append("from TF_F_USER_DISCNT a ");
        sql.append("where a.USER_ID = :USER_ID ");
        sql.append("AND a.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND a.RSRV_NUM1 = :RSRV_NUM1 ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }
}
