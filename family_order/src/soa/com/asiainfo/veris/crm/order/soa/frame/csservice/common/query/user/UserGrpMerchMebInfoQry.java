
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserGrpMerchMebInfoQry
{

    /**
     * 根据userID和ecUserId查询TF_F_USER_GRP_MERCH_MEB表信息
     * 
     * @author ft
     * @param user_id
     * @param grpUserId
     * @param mem_eparchy_code
     * @return
     * @throws Exception
     */
    public static IDataset getSEL_BY_USERID_USERIDA(String user_id, String grpUserId, String mem_eparchy_code) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("EC_USER_ID", grpUserId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT INST_ID, ");
        parser.addSQL("to_char(USER_ID) USER_ID, ");
        parser.addSQL("SERIAL_NUMBER, ");
        parser.addSQL("SERVICE_ID, ");
        parser.addSQL("to_char(EC_USER_ID) EC_USER_ID, ");
        parser.addSQL("EC_SERIAL_NUMBER, ");
        parser.addSQL("PRODUCT_ORDER_ID, ");
        parser.addSQL("PRODUCT_OFFER_ID, ");
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
        parser.addSQL("RSRV_TAG3, ");
        parser.addSQL("STATUS ");
        parser.addSQL("FROM TF_F_USER_GRP_MERCH_MEB ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL("AND EC_USER_ID = :EC_USER_ID ");
        parser.addSQL("AND USER_ID = TO_NUMBER(:USER_ID) ");
        parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");

        return Dao.qryByParse(parser, mem_eparchy_code);
    }

    /**
     * 一级BBOSS业务成员用户清单查询
     * 
     * @author liuxx3
     * @date 2014 -07 -14
     */

    public static IDataset qryBBossBizMeb(String status, String serial_number, String ec_serial_number, String product_offer_id, String ec_user_id, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("EC_SERIAL_NUMBER", ec_serial_number);
        param.put("PRODUCT_OFFER_ID", product_offer_id);
        param.put("EC_USER_ID", ec_user_id);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT t.user_id, ");
        parser.addSQL("        t.ec_user_id, ");
        parser.addSQL("        t.product_offer_id, ");
        parser.addSQL("        t.ec_serial_number, ");
        parser.addSQL("        t.serial_number, ");
        parser.addSQL("        case ");
        parser.addSQL("          when t.status = 'N' then ");
        parser.addSQL("           '暂停' ");
        parser.addSQL("          when t.status = 'Z' then ");
        parser.addSQL("           '销户' ");
        parser.addSQL("          when t.status = 'A' then ");
        parser.addSQL("           '正常商用' ");
        parser.addSQL("        end status, ");
        parser.addSQL("        t.start_date, ");
        parser.addSQL("        t.end_date ");
        parser.addSQL("   FROM tf_f_user_grp_merch_meb t ");
        parser.addSQL("  WHERE 1 = 1 ");
        parser.addSQL("    AND t.serial_number = :SERIAL_NUMBER ");
        parser.addSQL("    AND t.ec_serial_number = :EC_SERIAL_NUMBER ");
        parser.addSQL("    AND t.PRODUCT_OFFER_ID = :PRODUCT_OFFER_ID ");
        parser.addSQL("    AND t.EC_USER_ID = :EC_USER_ID ");

        if ("N".equals(status))// 暂停
        {
            parser.addSQL(" AND t.status='N' ");
            parser.addSQL(" AND sysdate>=t.start_date ");
            parser.addSQL(" AND sysdate<=t.end_date ");
        }
        else if ("Z".equals(status))// 销户
        {
            parser.addSQL(" AND t.status='Z' ");
            parser.addSQL(" AND sysdate>=t.start_date ");
        }
        else if ("A".equals(status))// 正常商用
        {
            parser.addSQL(" AND t.status='A' ");
            parser.addSQL(" AND sysdate>=t.start_date ");
            parser.addSQL(" AND sysdate<=t.end_date ");
        }

        return Dao.qryByParse(parser, pg);
    }

    /**
     * 根据ecUserId、serialNumber、userId查询tf_f_user_grp_merch_meb信息
     * 
     * @Function: qryBBossMerchpMeb
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-4-26 上午9:54:13 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 lijm3 v1.0.0 修改原因
     */
    public static IDataset qryMerchMebInfoByEcUserIdSnUserId(String user_id, String serial_number, String user_id_a, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERIAL_NUMBER", serial_number);
        param.put("EC_USER_ID", user_id_a);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select m.inst_id, m.status, m.user_id, m.serial_number,  m.ec_user_id,  m.ec_serial_number,  m.product_offer_id");
        parser.addSQL(" from tf_f_user_grp_merch_meb m ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and m.serial_number = :SERIAL_NUMBER");
        parser.addSQL(" and m.ec_user_id = :EC_USER_ID");
        parser.addSQL(" and user_id = TO_NUMBER(:USER_ID)");
        parser.addSQL(" and partition_id = MOD(TO_NUMBER(:USER_ID), 10000)");
        parser.addSQL(" and  sysdate between m.start_date and m.end_date");

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 根据GROUP_ID、MERCH_SPEC_CODE、PRODUCT_SPEC_CODE查询TF_F_USER_GRP_MERCH_MEB表信息
     * 
     * @param group_id
     * @param merch_spec_code
     * @param product_spec_code
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchMebInfoByGroupIdMerchScProductSc(String group_id, String merch_spec_code, String product_spec_code) throws Exception
    {

        IData param = new DataMap();
        param.put("GROUP_ID", group_id);
        param.put("MERCH_SPEC_CODE", merch_spec_code);
        param.put("PRODUCT_SPEC_CODE", product_spec_code);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("Select ugmm.user_id, ");
        parser.addSQL("ugmm.serial_number, ");
        parser.addSQL("ugmm.product_order_id, ");
        parser.addSQL("ugmm.start_date, ");
        parser.addSQL("ugmm.end_date, ");
        parser.addSQL("ugmm.update_staff_id ");
        parser.addSQL("From tf_f_user_grp_merch_meb ugmm, TF_F_USER_GRP_MERCHP ugmp ");
        parser.addSQL("Where 1 = 1 ");
        parser.addSQL("And ugmp.group_id = :GROUP_ID ");
        parser.addSQL("and ugmp.merch_spec_code = :MERCH_SPEC_CODE ");
        parser.addSQL("And ugmp.product_spec_code = :PRODUCT_SPEC_CODE ");
        parser.addSQL("And ugmm.RSRV_TAG1 = :RSRV_TAG1 ");
        parser.addSQL("And ugmp.user_id = ugmm.ec_user_id ");

        return Dao.qryByParse(parser);
    }

    /**
     * 根据userId查询TF_F_USER_GRP_MERCH_MEB表信息
     * 
     * @author ft
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchMebInfoByUserid(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT PARTITION_ID, ");
        parser.addSQL("to_char(USER_ID) USER_ID, ");
        parser.addSQL("SERIAL_NUMBER, ");
        parser.addSQL("SERVICE_ID, ");
        parser.addSQL("to_char(EC_USER_ID) EC_USER_ID, ");
        parser.addSQL("EC_SERIAL_NUMBER, ");
        parser.addSQL("PRODUCT_ORDER_ID, ");
        parser.addSQL("PRODUCT_OFFER_ID, ");
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
        parser.addSQL("RSRV_TAG3, ");
        parser.addSQL("INST_ID ");
        parser.addSQL("FROM TF_F_USER_GRP_MERCH_MEB ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL("AND USER_ID = TO_NUMBER(:USER_ID) ");
        parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");

        return Dao.qryByParse(parser);
    }  
    /**
     * 查询账期内有效成员信息
     * 根据userId,productoffid查询TF_F_USER_GRP_MERCH_MEB表信息
     * 
     * @author chenyi
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchMebInfoByUseridProdOff(String userId,String productOffid,String relaTypeCode,String acctMonth) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_OFFER_ID", productOffid);
        param.put("RELATION_TYPE_CODE", relaTypeCode);

        //这地方要查询是当月内生效过的成员信息，哪怕是该月内有一天时间有效也算入
        String firstTime = SysDateMgr.decodeTimestamp(acctMonth, SysDateMgr.PATTERN_STAND);
        String lastTime = SysDateMgr.getAddMonthsLastDay(0, firstTime); 
        param.put("FIRST_TIME", firstTime);
        param.put("LAST_TIME", lastTime);
        
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT T.PARTITION_ID, ");
        parser.addSQL("to_char(T.USER_ID) USER_ID, ");
        parser.addSQL("T.SERIAL_NUMBER, ");
        parser.addSQL("T.SERVICE_ID, ");
        parser.addSQL("to_char(T.EC_USER_ID) EC_USER_ID, ");
        parser.addSQL("T.EC_SERIAL_NUMBER, ");
        parser.addSQL("T.PRODUCT_ORDER_ID, ");
        parser.addSQL("T.PRODUCT_OFFER_ID, ");
        parser.addSQL("T.STATUS, ");
        parser.addSQL("to_char(T.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL("to_char(T.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        parser.addSQL("to_char(T.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL("T.UPDATE_STAFF_ID, ");
        parser.addSQL("T.UPDATE_DEPART_ID, ");
        parser.addSQL("T.REMARK, ");
        parser.addSQL("T.RSRV_NUM1, ");
        parser.addSQL("T.RSRV_NUM2, ");
        parser.addSQL("T.RSRV_NUM3, ");
        parser.addSQL("to_char(T.RSRV_NUM4) RSRV_NUM4, ");
        parser.addSQL("to_char(T.RSRV_NUM5) RSRV_NUM5, ");
        parser.addSQL("T.RSRV_STR1, ");
        parser.addSQL("T.RSRV_STR2, ");
        parser.addSQL("T.RSRV_STR3, ");
        parser.addSQL("T.RSRV_STR4, ");
        parser.addSQL("T.RSRV_STR5, ");
        parser.addSQL("to_char(T.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL("to_char(T.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL("to_char(T.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL("T.RSRV_TAG1, ");
        parser.addSQL("T.RSRV_TAG2, ");
        parser.addSQL("T.RSRV_TAG3, ");
        parser.addSQL("T.INST_ID ");
        parser.addSQL("FROM TF_F_USER_GRP_MERCH_MEB T ,TF_F_RELATION_BB  BB ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND T.PARTITION_ID =BB.PARTITION_ID ");
        parser.addSQL("AND T.USER_ID =BB.USER_ID_B ");
        parser.addSQL("AND BB.START_DATE < to_date(:LAST_TIME, 'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("AND BB.END_DATE >= to_date(:FIRST_TIME, 'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL("AND BB.RELATION_TYPE_CODE =(:RELATION_TYPE_CODE) ");
        parser.addSQL("AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL("AND T.USER_ID = TO_NUMBER(:USER_ID) ");
        parser.addSQL("AND T.PRODUCT_OFFER_ID = TO_NUMBER(:PRODUCT_OFFER_ID) ");
      

        return Dao.qryByParse(parser);
    }
}
