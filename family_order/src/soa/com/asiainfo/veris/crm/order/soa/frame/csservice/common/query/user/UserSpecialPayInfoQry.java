
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.SqlUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserSpecialPayInfoQry
{
    public static IDataset getUserSpecPayByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("END_CYCLE_ID", SysDateMgr.getSysDateYYYYMMDD());

        return Dao.qryByCodeParser("TF_F_USER_SPECIALEPAY", "SEL_BY_USERID", param);
    }

    /**
     * 成员代付关系
     * 
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */

    public static IDataset getUserSpecPayByUserId(String userId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT a.acct_id para_code2, a.acct_id_b para_code3, a.payitem_code para_code4, a.PAYITEM_CODE para_code5, ");
        parser.addSQL(" a.start_cycle_id para_code6,  a.end_cycle_id para_code7, a.update_staff_id para_code8, a.update_time para_code9, ");
        parser.addSQL(" decode(a.limit_type, '0', '全部费用', '1', '部分费用', '2', '限定比例') para_code10, a.limit / 100 para_code11, a.user_id_a ");
        parser.addSQL(" FROM tf_f_user_specialepay a ");
        parser.addSQL(" WHERE a.user_id = :USER_ID ");
        parser.addSQL(" AND a.partition_id =  mod(to_number(:USER_ID),10000) ");
        parser.addSQL(" AND to_date(end_cycle_id,'yyyyMMdd')>sysdate ");
        IDataset ids = Dao.qryByParse(parser, routeId);
        for (int i = 0, cout = ids.size(); i < cout; i++)
        {
            IData map = ids.getData(i);
            map.put("PARA_CODE5", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ITEM", "ITEM_ID", "ITEM_NAME", map.getString("PARA_CODE5")));

        }
        return ids;
    }

    /**
     * 成员代付关系
     * 
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */

    public static IDataset getUserSpecPayByUserIdAB(String userIdB, String userIdA, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userIdB);
        param.put("USER_ID_A", userIdA);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT a.acct_id para_code2, a.acct_id_b para_code3, a.payitem_code para_code4, a.PAYITEM_CODE para_code5, ");
        parser.addSQL(" a.start_cycle_id para_code6,  a.end_cycle_id para_code7, a.update_staff_id para_code8, a.update_time para_code9, ");
        parser.addSQL(" decode(a.limit_type, '0', '全部费用', '1', '部分费用', '2', '限定比例') para_code10, a.limit / 100 para_code11 ");
        parser.addSQL(" FROM tf_f_user_specialepay a ");
        parser.addSQL(" WHERE a.user_id = :USER_ID ");
        parser.addSQL(" AND a.partition_id =  mod(to_number(:USER_ID),10000) ");
        parser.addSQL(" AND a.user_id_a = :USER_ID_A ");
        parser.addSQL(" AND to_date(end_cycle_id,'yyyyMMdd')>sysdate ");
        IDataset ids = Dao.qryByParse(parser, routeId);
        for (int i = 0, cout = ids.size(); i < cout; i++)
        {
            IData map = ids.getData(i);
            map.put("PARA_CODE5", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ITEM", "ITEM_ID", "ITEM_NAME", map.getString("PARA_CODE5")));

        }
        return ids;
    }

    /**
     * 查询用户代付关系信息
     * 
     * @param userId
     * @param userIdA
     * @return
     * @throws Exception
     */
    public static IDataset qryUserSpecialPay(String userId, String userIdA) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);
        param.put("USER_ID_A", userIdA);

        return Dao.qryByCode("TF_F_USER_SPECIALEPAY", "SEL_BY_USERID_USERIDA", param);
    }

    /**
     * 查询用户代付关系信息
     * 
     * @param userId
     * @param acctId
     * @param payItemCode
     * @return
     * @throws Exception
     */
    public static IDataset qryUserSpecialPay(String userId, String acctId, String payItemCode) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);
        param.put("ACCT_ID", acctId);
        param.put("PAYITEM_CODE", payItemCode);

        return Dao.qryByCode("TF_F_USER_SPECIALEPAY", "SEL_BY_USERID_ACCTID_PAYITEMCODE", param);
    }

    /**
     * 查询用户代付关系信息
     * 
     * @param userId
     * @param userIdA
     * @param acctId
     * @param payItemCode
     * @return
     * @throws Exception
     */
    public static IDataset qryUserSpecialPay(String userId, String userIdA, String acctId, String payItemCode) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);
        param.put("USER_ID_A", userIdA);
        param.put("ACCT_ID", acctId);
        param.put("PAYITEM_CODE", payItemCode);

        return Dao.qryByCode("TF_F_USER_SPECIALEPAY", "SEL_BY_USERID_USERIDA_ACCTID_PAYITEMCODE", param);
    }
    
    /**
     * 查询用户代付关系信息
     * 
     * @param userId
     * @param acctId
     * @param payItemCode
     * @return
     * @throws Exception
     */
    public static IDataset qryUserSpecialPayForUPGP(String userId, String acctId, String payItemCode) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);
        param.put("ACCT_ID", acctId);
        param.put("PAYITEM_CODE", payItemCode);

        return Dao.qryByCode("TF_F_USER_SPECIALEPAY", "SEL_BY_USERID_ACCTID_PAYITEMCODE", param);
    }
    
    /**
     * 根据用户ID+集团用户ID+账户ID获取可暂停的代付关系
     * @param userId
     * @param userIdA
     * @param acctId
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-12-7
     */
    public static IDataset qryUserSpecialPayByUGAID(String userId, String userIdA, String acctId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("USER_ID_A", userIdA);
        param.put("ACCT_ID", acctId);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlUtil.trimSql("SELECT PARTITION_ID,													"));
        sql.append(SqlUtil.trimSql("       TO_CHAR(USER_ID) USER_ID,                                        "));
        sql.append(SqlUtil.trimSql("       TO_CHAR(USER_ID_A) USER_ID_A,                                    "));
        sql.append(SqlUtil.trimSql("       TO_CHAR(ACCT_ID) ACCT_ID,                                        "));
        sql.append(SqlUtil.trimSql("       TO_CHAR(ACCT_ID_B) ACCT_ID_B,                                    "));
        sql.append(SqlUtil.trimSql("       PAYITEM_CODE,                                                    "));
        sql.append(SqlUtil.trimSql("       START_CYCLE_ID,                                                  "));
        sql.append(SqlUtil.trimSql("       END_CYCLE_ID,                                                    "));
        sql.append(SqlUtil.trimSql("       BIND_TYPE,                                                       "));
        sql.append(SqlUtil.trimSql("       LIMIT_TYPE,                                                      "));
        sql.append(SqlUtil.trimSql("       TO_CHAR(LIMIT) LIMIT,                                            "));
        sql.append(SqlUtil.trimSql("       COMPLEMENT_TAG,                                                  "));
        sql.append(SqlUtil.trimSql("       RSRV_STR1,                                                       "));
        sql.append(SqlUtil.trimSql("       RSRV_STR2,                                                       "));
        sql.append(SqlUtil.trimSql("       RSRV_STR3,                                                       "));
        sql.append(SqlUtil.trimSql("       UPDATE_STAFF_ID,                                                 "));
        sql.append(SqlUtil.trimSql("       UPDATE_DEPART_ID,                                                "));
        sql.append(SqlUtil.trimSql("       TO_CHAR(UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,       "));
        sql.append(SqlUtil.trimSql("       INST_ID                                                          "));
        sql.append(SqlUtil.trimSql("  FROM TF_F_USER_SPECIALEPAY                                            "));
        sql.append(SqlUtil.trimSql(" WHERE USER_ID = TO_NUMBER(:USER_ID)                                    "));
        sql.append(SqlUtil.trimSql("   AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)                   "));
        sql.append(SqlUtil.trimSql("   AND USER_ID_A = TO_NUMBER(:USER_ID_A)                                "));
        sql.append(SqlUtil.trimSql("   AND ACCT_ID = TO_NUMBER(:ACCT_ID)                                    "));
        sql.append(SqlUtil.trimSql("   AND END_CYCLE_ID > START_CYCLE_ID                                    "));
        sql.append(SqlUtil.trimSql("   AND END_CYCLE_ID >= TO_NUMBER(TO_CHAR(SYSDATE, 'YYYYMMDD'))          "));
        return Dao.qryBySql(sql, param);
    }
    
    /**
     * 根据用户ID+集团用户ID+账户ID获取可恢复的代付关系
     * @param userId
     * @param userIdA
     * @param acctId
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-12-7
     */
    public static IDataset qryUserSpecialPayByUGAIDForRestart(String userId, String userIdA, String acctId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("USER_ID_A", userIdA);
        param.put("ACCT_ID", acctId);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlUtil.trimSql("SELECT PARTITION_ID,														"));
        sql.append(SqlUtil.trimSql("       TO_CHAR(USER_ID) USER_ID,                                            "));
        sql.append(SqlUtil.trimSql("       TO_CHAR(USER_ID_A) USER_ID_A,                                        "));
        sql.append(SqlUtil.trimSql("       TO_CHAR(ACCT_ID) ACCT_ID,                                            "));
        sql.append(SqlUtil.trimSql("       TO_CHAR(ACCT_ID_B) ACCT_ID_B,                                        "));
        sql.append(SqlUtil.trimSql("       PAYITEM_CODE,                                                        "));
        sql.append(SqlUtil.trimSql("       START_CYCLE_ID,                                                      "));
        sql.append(SqlUtil.trimSql("       END_CYCLE_ID,                                                        "));
        sql.append(SqlUtil.trimSql("       BIND_TYPE,                                                           "));
        sql.append(SqlUtil.trimSql("       LIMIT_TYPE,                                                          "));
        sql.append(SqlUtil.trimSql("       TO_CHAR(LIMIT) LIMIT,                                                "));
        sql.append(SqlUtil.trimSql("       COMPLEMENT_TAG,                                                      "));
        sql.append(SqlUtil.trimSql("       RSRV_STR1,                                                           "));
        sql.append(SqlUtil.trimSql("       RSRV_STR2,                                                           "));
        sql.append(SqlUtil.trimSql("       RSRV_STR3,                                                           "));
        sql.append(SqlUtil.trimSql("       UPDATE_STAFF_ID,                                                     "));
        sql.append(SqlUtil.trimSql("       UPDATE_DEPART_ID,                                                    "));
        sql.append(SqlUtil.trimSql("       TO_CHAR(UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,INST_ID    "));
        sql.append(SqlUtil.trimSql("  FROM TF_F_USER_SPECIALEPAY                                                "));
        sql.append(SqlUtil.trimSql(" WHERE USER_ID = TO_NUMBER(:USER_ID)                                        "));
        sql.append(SqlUtil.trimSql("   AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)                       "));
        sql.append(SqlUtil.trimSql("   AND USER_ID_A = TO_NUMBER(:USER_ID_A)                                    "));
        sql.append(SqlUtil.trimSql("   AND ACCT_ID = TO_NUMBER(:ACCT_ID)                                        "));
        sql.append(SqlUtil.trimSql("   AND RSRV_STR2 LIKE 'CREDIT_STOP%'                                        "));
        return Dao.qryBySql(sql, param);
    }
    
}
