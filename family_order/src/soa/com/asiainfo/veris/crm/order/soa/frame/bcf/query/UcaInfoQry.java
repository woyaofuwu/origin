
package com.asiainfo.veris.crm.order.soa.frame.bcf.query;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UUserTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;

public final class UcaInfoQry
{
    private static final Logger logger = Logger.getLogger(UcaInfoQry.class);

    /**
     * 根据ACCT_ID查询账户信息,默认路由
     *
     * @param acctId
     * @return
     * @throws Exception
     */
    public static IData qryAcctInfoByAcctId(String acctId) throws Exception
    {
        IData map = qryAcctInfoByAcctId(acctId, null);

        return map;
    }

    /**
     * 根据ACCT_ID查询账户信息,带路由
     *
     * @param acctId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryAcctInfoByAcctId(String acctId, String routeId) throws Exception
    {
        //是否缓存
        boolean isCache = BizEnv.getEnvBoolean("crm.uca.cache", true);

        if (isCache == false)
        {
            IData map = qryAcctInfoByAcctIdFromDb(acctId, routeId);

            return map;
        }

        // 得到缓存key
        String cacheKey = CacheKey.getUcaKeyActByAcctId(acctId, routeId);

        // 从缓存中取数据
        Object cacheObj = null;
        boolean cacheOK = true;

        try
        {
            cacheObj = SharedCache.get(cacheKey);
        }
        catch (Exception e)
        {
            cacheOK = false;
            cacheObj = null;

            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据失败[").append(cacheKey).append("]");

            logger.error(sb);
        }

        // 缓存无，从数据库中查
        if (cacheObj == null)
        {
            IData map = qryAcctInfoByAcctIdFromDb(acctId, routeId);

            // 将数据放入缓存
            if (cacheOK == true)
            {
                SharedCache.set(cacheKey, map, 600);
            }

            return map;
        }
        else
        {
            // 往后顺延
            SharedCache.touch(cacheKey, 600);
        }

        // 缓存有，直接返回缓存对象
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据成功[").append(cacheKey).append("]");
            logger.debug(sb);
        }

        IData map = (IData) cacheObj;

        return map;
    }

    /**
     * 根据ACCT_ID查询集团账户信息
     *
     * @param acctId
     * @return
     * @throws Exception
     */
    public static IData qryAcctInfoByAcctIdForGrp(String acctId) throws Exception
    {
        IData map = qryAcctInfoByAcctId(acctId, Route.CONN_CRM_CG);

        return map;
    }

    /**
     * 根据ACCT_ID和routeId查询账户信息
     *
     * @param acctId
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IData qryAcctInfoByAcctIdFromDb(String acctId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, TO_CHAR(ACCT_ID) ACCT_ID, TO_CHAR(CUST_ID) CUST_ID, ");
        sql.append("PAY_NAME, PAY_MODE_CODE, ACCT_DIFF_CODE, ACCT_PASSWD, ACCT_TAG, ");
        sql.append("NET_TYPE_CODE, EPARCHY_CODE, CITY_CODE, BANK_CODE, BANK_ACCT_NO, ");
        sql.append("TO_CHAR(SCORE_VALUE) SCORE_VALUE, CREDIT_CLASS_ID, ");
        sql.append("TO_CHAR(BASIC_CREDIT_VALUE) BASIC_CREDIT_VALUE, ");
        sql.append("TO_CHAR(CREDIT_VALUE) CREDIT_VALUE, ");
        sql.append("TO_CHAR(DEBUTY_USER_ID) DEBUTY_USER_ID, DEBUTY_CODE, CONTRACT_NO, ");
        sql.append("DEPOSIT_PRIOR_RULE_ID, ITEM_PRIOR_RULE_ID, ");
        sql.append("TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, REMOVE_TAG, ");
        sql.append("TO_CHAR(REMOVE_DATE, 'yyyy-mm-dd hh24:mi:ss') REMOVE_DATE, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, REMARK, ");
        sql.append("RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
        sql.append("RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10 ");
        sql.append("FROM TF_F_ACCOUNT ");
        sql.append("WHERE ACCT_ID = TO_NUMBER(:ACCT_ID) ");
        sql.append("AND PARTITION_ID = MOD(TO_NUMBER(:ACCT_ID), 10000) ");

        IDataset ids = Dao.qryBySql(sql, param, routeId);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        IData map = ids.getData(0);

        map.put("PAY_MODE_NAME", StaticUtil.getStaticValue("TD_S_PAYMODE", map.getString("PAY_MODE_CODE")));
        map.put("EPARCHY_NAME", StaticUtil.getStaticValue("AREA_CODE", map.getString("EPARCHY_CODE")));

        String payModeCode = map.getString("PAY_MODE_CODE", "");

        if (!"0".equals(payModeCode))
        {
            // 银行信息
            String bankCode = map.getString("BANK_CODE");

            String bank = UBankInfoQry.getBankNameByBankCode(bankCode);

            // 上级银行信息
            String superBankCode = UBankInfoQry.getSuperBankCodeByBankCode(bankCode);

            String superBank = UBankInfoQry.getSuperBankNameBySuperBankCode(superBankCode);

            map.put("BANK", bank);
            map.put("SUPER_BANK_CODE", superBankCode);
            map.put("SUPER_BANK", superBank);
        }

        map.put("PRINT_NAME", StaticUtil.getStaticValue("PRINT_MODE", map.getString("RSRV_STR8")));
        map.put("NODE_NAME", StaticUtil.getStaticValue("NODE_MODE", map.getString("RSRV_STR9")));

        return map;
    }

    /**
     * 根据用户标识查询账户信息,没有默认付费账户,则查最后一条
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryAcctInfoByUserId(String userId) throws Exception
    {
        IData payrela = qryPayRelaByUserId(userId);

        if (IDataUtil.isEmpty(payrela))
        {
            return null;
        }

        String acctId = payrela.getString("ACCT_ID");

        return qryAcctInfoByAcctId(acctId);
    }

    /**
     * 根据用户标识查询集团用户默认付费账户信息,没有默认付费账户,则查最后一条
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryAcctInfoByUserIdForGrp(String user_id) throws Exception
    {
        IData datas = qryPayRelaByUserIdForGrp(user_id);

        if (IDataUtil.isEmpty(datas))
        {
            return new DataMap();
        }

        String acctId = datas.getString("ACCT_ID");

        return qryAcctInfoByAcctIdForGrp(acctId);
    }

    /**
     * 根据客户标识查询客户信息,如果个人客户信息没有,则查询集团客户信息
     *
     * @param custId
     * @return
     * @throws Exception
     */
    public static IData qryCustInfoByCustId(String custId) throws Exception
    {
        // 个人客户
        IData custPerson = qryPerInfoByCustId(custId);

        if (IDataUtil.isNotEmpty(custPerson))
        {
            return custPerson;
        }

        // 集团客户
        IData custGroup = qryGrpInfoByCustId(custId);

        if (IDataUtil.isNotEmpty(custGroup))
        {
            return custGroup;
        }

        return null;
    }

    /**
     * 根据客户标识查询customer客户信息,不带路由
     *
     * @param custId
     * @return
     * @throws Exception
     */
    public static IData qryCustomerInfoByCustId(String custId) throws Exception
    {
        IData map = qryCustomerInfoByCustId(custId, null);

        return map;
    }

    /**
     * 根据客户标识查询customer客户信息
     *
     * @param custId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryCustomerInfoByCustId(String custId, String routeId) throws Exception
    {
        // 是否缓存
        boolean isCache = BizEnv.getEnvBoolean("crm.uca.cache", true);

        if (isCache == false)
        {
            IData map = qryCustomerInfoByCustIdFromDB(custId, routeId);

            return map;
        }

        // 得到缓存key
        String cacheKey = CacheKey.getUcaKeyCustomerByCustId(custId, routeId);

        // 从缓存中取数据
        Object cacheObj = null;
        boolean cacheOK = true;

        try
        {
            cacheObj = SharedCache.get(cacheKey);
        }
        catch (Exception e)
        {
            cacheOK = false;
            cacheObj = null;

            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据失败[").append(cacheKey).append("]");

            logger.error(sb);
        }

        // 缓存无，从数据库中查
        if (cacheObj == null)
        {
            IData map = qryCustomerInfoByCustIdFromDB(custId, routeId);

            // 将数据放入缓存
            if (cacheOK == true)
            {
                SharedCache.set(cacheKey, map, 600);
            }

            return map;
        }
        else
        {
            // 往后顺延
            SharedCache.touch(cacheKey, 600);
        }

        // 缓存有，直接返回缓存对象
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据成功[").append(cacheKey).append("]");
            logger.debug(sb);
        }

        IData map = (IData) cacheObj;

        return map;
    }

    /**
     * 根据客户标识查询集团customer信息
     *
     * @param custId
     * @return
     * @throws Exception
     */
    public static IData qryCustomerInfoByCustIdForGrp(String custId) throws Exception
    {
        IData map = qryCustomerInfoByCustId(custId, Route.CONN_CRM_CG);

        return map;
    }

    /**
     * 根据客户标识和路由标识查询客户信息
     *
     * @param custId
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IData qryCustomerInfoByCustIdFromDB(String custId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, TO_CHAR(CUST_ID) CUST_ID, CUST_NAME, CUST_TYPE, CUST_STATE, ");
        sql.append("PSPT_TYPE_CODE, PSPT_ID, OPEN_LIMIT, EPARCHY_CODE, CITY_CODE, CUST_PASSWD, ");
        sql.append("TO_CHAR(SCORE_VALUE) SCORE_VALUE, CREDIT_CLASS, ");
        sql.append("TO_CHAR(BASIC_CREDIT_VALUE) BASIC_CREDIT_VALUE, ");
        sql.append("TO_CHAR(CREDIT_VALUE) CREDIT_VALUE, REMOVE_TAG, ");
        sql.append("TO_CHAR(REMOVE_DATE, 'yyyy-mm-dd hh24:mi:ss') REMOVE_DATE, ");
        sql.append("DEVELOP_DEPART_ID, DEVELOP_STAFF_ID, IN_DEPART_ID, IN_STAFF_ID, ");
        sql.append("TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, REMARK, RSRV_STR1, ");
        sql.append("RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, ");
        sql.append("RSRV_STR8, RSRV_STR9, RSRV_STR10, REMOVE_STAFF_ID, REMOVE_CHANGE, ");
        sql.append("RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, TO_CHAR(RSRV_NUM4) RSRV_NUM4, ");
        sql.append("TO_CHAR(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("TO_CHAR(RSRV_DATE4, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE4, ");
        sql.append("TO_CHAR(RSRV_DATE5, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE5, RSRV_TAG1, ");
        sql.append("RSRV_TAG2, RSRV_TAG3, RSRV_TAG4, RSRV_TAG5, CUST_KIND, CITY_CODE_A, ");
        sql.append("IS_REAL_NAME, SIMPLE_SPELL ");
        sql.append("FROM TF_F_CUSTOMER ");
        sql.append("WHERE CUST_ID = TO_NUMBER(:CUST_ID) ");
        sql.append("AND PARTITION_ID = MOD(TO_NUMBER(:CUST_ID), 10000) ");

        IDataset ids = Dao.qryBySql(sql, param, routeId);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        IData map = ids.getData(0);

        map.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(map.getString("EPARCHY_CODE")));
        if(ProvinceUtil.isProvince(ProvinceUtil.SHXI)){
            map.put("PSPT_TYPE_NAME", StaticUtil.getStaticValue(CSBizBean
                            .getVisit(), "TD_S_PASSPORTTYPE", new String[] {
                            "PSPT_TYPE_CODE", "EPARCHY_CODE" }, "PSPT_TYPE",
                    new String[] { map.getString("PSPT_TYPE_CODE"),
                            map.getString("EPARCHY_CODE") }));
        }else if(ProvinceUtil.isProvince(ProvinceUtil.QHAI)){
            map.put("PSPT_TYPE_NAME",
                    StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA",
                            new String[] { "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE", "EPARCHY_CODE" }, "PARAM_NAME",
                            new String[] { "CSM", "1987", map.getString("PSPT_TYPE_CODE"), "ZZZZ" }));
        }else{
            map.put("PSPT_TYPE_NAME", StaticUtil.getStaticValue("PSPT_TYPE_CODE", map.getString("PSPT_TYPE_CODE")));
        }

        return map;
    }

    /**
     * 根据用户标识查询默认付费关系,不带路由
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryDefaultPayRelaByUserId(String userId) throws Exception
    {
        return qryDefaultPayRelaByUserId(userId, null);
    }

    /**
     * 根据用户标识查询默认付费关系,带路由
     *
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryDefaultPayRelaByUserId(String userId, String routeId) throws Exception
    {
        // 是否缓存
        boolean isCache = BizEnv.getEnvBoolean("crm.uca.cache", true);

        if (isCache == false)
        {
            IData map = qryDefaultPayRelaByUserIdFromDb(userId, routeId);

            return map;
        }

        // 得到缓存key
        String cacheKey = CacheKey.getUcaKeyDefaultPayRelaByUserId(userId, routeId);

        // 从缓存中取数据
        Object cacheObj = null;
        boolean cacheOK = true;

        try
        {
            cacheObj = SharedCache.get(cacheKey);
        }
        catch (Exception e)
        {
            cacheOK = false;
            cacheObj = null;

            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据失败[").append(cacheKey).append("]");

            logger.error(sb);
        }

        // 缓存无，从数据库中查
        if (cacheObj == null)
        {
            IData map = qryDefaultPayRelaByUserIdFromDb(userId, routeId);

            // 将数据放入缓存
            if (cacheOK == true)
            {
                SharedCache.set(cacheKey, map, 600);
            }

            return map;
        }
        else
        {
            // 往后顺延
            SharedCache.touch(cacheKey, 600);
        }

        // 缓存有，直接返回缓存对象
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据成功[").append(cacheKey).append("]");
            logger.debug(sb);
        }

        IData map = (IData) cacheObj;

        return map;
    }

    /**
     * 根据用户标识查询集团用户默认付费关系
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryDefaultPayRelaByUserIdForGrp(String userId) throws Exception
    {
        return qryDefaultPayRelaByUserId(userId, Route.CONN_CRM_CG);
    }

    /**
     * 根据用户标识查询默认付费关系,带路由
     *
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IData qryDefaultPayRelaByUserIdFromDb(String userId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT  /*+index(a IDX_TF_A_PAYRELATION_UID)*/PARTITION_ID, USER_ID, ACCT_ID, PAYITEM_CODE, ACCT_PRIORITY, ");
        sql.append("USER_PRIORITY, BIND_TYPE, START_CYCLE_ID, END_CYCLE_ID, DEFAULT_TAG, ");
        sql.append("ACT_TAG, LIMIT_TYPE, LIMIT, COMPLEMENT_TAG, INST_ID, UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, UPDATE_TIME ");
        sql.append("FROM TF_A_PAYRELATION a ");
        sql.append("WHERE USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND DEFAULT_TAG = '1' ");
        sql.append("AND ACT_TAG = '1' ");
        sql.append("AND TO_NUMBER(TO_CHAR(SYSDATE, 'yyyymmdd')) BETWEEN START_CYCLE_ID AND END_CYCLE_ID ");

        IDataset ids = Dao.qryBySql(sql, param, routeId);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        return ids.getData(0);
    }

    /**
     * 根据客户标识查询客户信息
     *
     * @param custId
     * @return
     * @throws Exception
     */
    public static IData qryGrpInfoByCustId(String custId) throws Exception
    {
        // 是否缓存
        boolean isCache = BizEnv.getEnvBoolean("crm.uca.cache", true);

        if (isCache == false)
        {
            IData map = qryGrpInfoByCustIdFromDB(custId);

            return map;
        }

        // 得到缓存key
        String cacheKey = CacheKey.getUcaKeyGroupByCustId(custId, Route.CONN_CRM_CG);

        // 从缓存中取数据
        Object cacheObj = null;
        boolean cacheOK = true;

        try
        {
            cacheObj = SharedCache.get(cacheKey);
        }
        catch (Exception e)
        {
            cacheOK = false;
            cacheObj = null;

            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据失败[").append(cacheKey).append("]");

            logger.error(sb);
        }

        // 缓存无，从数据库中查
        if (cacheObj == null)
        {
            IData map = qryGrpInfoByCustIdFromDB(custId);

            // 将数据放入缓存
            if (cacheOK == true)
            {
                SharedCache.set(cacheKey, map, 600);

                if (IDataUtil.isNotEmpty(map))
                {
                    // 同时放groupid的
                    String groupId = map.getString("GROUP_ID");

                    String cacheKeyGroupId = CacheKey.getUcaKeyGroupByGrpId(groupId, Route.CONN_CRM_CG);

                    SharedCache.set(cacheKeyGroupId, map, 600);
                }
            }

            return map;
        }
        else
        {
            // 往后顺延
            SharedCache.touch(cacheKey, 600);
        }

        // 缓存有，直接返回缓存对象
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据成功[").append(cacheKey).append("]");
            logger.debug(sb);
        }

        IData map = (IData) cacheObj;

        return map;
    }

    /**
     * 根据客户标识查询集团客户信息
     *
     * @param custId
     * @return
     * @throws Exception
     */
    private static IData qryGrpInfoByCustIdFromDB(String custId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT TO_CHAR(CUST_ID) CUST_ID, GROUP_ID, CUST_NAME, GROUP_TYPE, GROUP_ROLE, ");
        sql.append("CLASS_ID, CLASS_ID2, LAST_CLASS_ID, ");
        sql.append("TO_CHAR(CLASS_CHANGE_DATE, 'yyyy-mm-dd hh24:mi:ss') CLASS_CHANGE_DATE, ");
        sql.append("CUST_CLASS_TYPE, GROUP_ATTR, GROUP_STATUS, GROUP_ADDR, GROUP_SOURCE, ");
        sql.append("PROVINCE_CODE, EPARCHY_CODE, CITY_CODE, CITY_CODE_U, SUPER_GROUP_ID, ");
        sql.append("SUPER_GROUP_NAME, PNATIONAL_GROUP_ID, PNATIONAL_GROUP_NAME, ");
        sql.append("MP_GROUP_CUST_CODE, UNIFY_PAY_CODE, ORG_STRUCT_CODE, CUST_MANAGER_ID, ");
        sql.append("CUST_MANAGER_APPR, ");
        sql.append("TO_CHAR(ASSIGN_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSIGN_DATE, ");
        sql.append("ASSIGN_STAFF_ID, CALLING_TYPE_CODE, SUB_CALLING_TYPE_CODE, ");
        sql.append("CALLING_AREA_CODE, CALL_TYPE, ACCEPT_CHANNEL, AGREEMENT, BUSI_TYPE, ");
        sql.append("GROUP_CONTACT_PHONE, ENTERPRISE_TYPE_CODE, ENTERPRISE_SIZE_CODE, ");
        sql.append("ENTERPRISE_SCOPE, JURISTIC_TYPE_CODE, ");
        sql.append("TO_CHAR(JURISTIC_CUST_ID) JURISTIC_CUST_ID, JURISTIC_NAME, ");
        sql.append("BUSI_LICENCE_TYPE, BUSI_LICENCE_NO, ");
        sql.append("TO_CHAR(BUSI_LICENCE_VALID_DATE, 'yyyy-mm-dd hh24:mi:ss') BUSI_LICENCE_VALID_DATE, ");
        sql.append("GROUP_MEMO, BANK_ACCT, BANK_NAME, TO_CHAR(REG_MONEY) REG_MONEY, ");
        sql.append("TO_CHAR(REG_DATE, 'yyyy-mm-dd hh24:mi:ss') REG_DATE, CUST_AIM, SCOPE, ");
        sql.append("MAIN_BUSI, MAIN_TRADE, EMP_LSAVE, ");
        sql.append("TO_CHAR(LATENCY_FEE_SUM) LATENCY_FEE_SUM, TO_CHAR(YEAR_GAIN) YEAR_GAIN, ");
        sql.append("TO_CHAR(TURNOVER) TURNOVER, CONSUME, TO_CHAR(COMM_BUDGET) COMM_BUDGET, ");
        sql.append("TO_CHAR(GTEL_BUDGET) GTEL_BUDGET, TO_CHAR(LTEL_BUDGET) LTEL_BUDGET, ");
        sql.append("GROUP_ADVERSARY, VPMN_GROUP_ID, TO_CHAR(VPMN_NUM) VPMN_NUM, ");
        sql.append("TO_CHAR(USER_NUM) USER_NUM, TO_CHAR(EMP_NUM_LOCAL) EMP_NUM_LOCAL, ");
        sql.append("TO_CHAR(EMP_NUM_CHINA) EMP_NUM_CHINA, TO_CHAR(EMP_NUM_ALL) EMP_NUM_ALL, ");
        sql.append("TO_CHAR(TELECOM_NUM_GH) TELECOM_NUM_GH, ");
        sql.append("TO_CHAR(TELECOM_NUM_XLT) TELECOM_NUM_XLT, ");
        sql.append("TO_CHAR(MOBILE_NUM_CHINAGO) MOBILE_NUM_CHINAGO, ");
        sql.append("TO_CHAR(MOBILE_NUM_GLOBAL) MOBILE_NUM_GLOBAL, ");
        sql.append("TO_CHAR(MOBILE_NUM_MZONE) MOBILE_NUM_MZONE, ");
        sql.append("TO_CHAR(MOBILE_NUM_LOCAL) MOBILE_NUM_LOCAL, ");
        sql.append("TO_CHAR(UNICOM_NUM_G) UNICOM_NUM_G, TO_CHAR(UNICOM_NUM_C) UNICOM_NUM_C, ");
        sql.append("TO_CHAR(UNICOM_NUM_GC) UNICOM_NUM_GC, ");
        sql.append("TO_CHAR(PRODUCT_NUM_LOCAL) PRODUCT_NUM_LOCAL, ");
        sql.append("TO_CHAR(PRODUCT_NUM_OTHER) PRODUCT_NUM_OTHER, ");
        sql.append("TO_CHAR(PRODUCT_NUM_USE) PRODUCT_NUM_USE, ");
        sql.append("TO_CHAR(EMPLOYEE_ARPU) EMPLOYEE_ARPU, ");
        sql.append("TO_CHAR(NETRENT_PAYOUT) NETRENT_PAYOUT, ");
        sql.append("TO_CHAR(MOBILE_PAYOUT) MOBILE_PAYOUT, ");
        sql.append("TO_CHAR(UNICOM_PAYOUT) UNICOM_PAYOUT, ");
        sql.append("TO_CHAR(TELECOM_PAYOUT_XLT) TELECOM_PAYOUT_XLT, GROUP_PAY_MODE, ");
        sql.append("PAYFOR_WAY_CODE, WRITEFEE_COUNT, TO_CHAR(WRITEFEE_SUM) WRITEFEE_SUM, ");
        sql.append("TO_CHAR(USER_NUM_FULLFREE) USER_NUM_FULLFREE, ");
        sql.append("TO_CHAR(USER_NUM_WRITEOFF) USER_NUM_WRITEOFF, ");
        sql.append("TO_CHAR(BOSS_FEE_SUM) BOSS_FEE_SUM, DOYEN_STAFF_ID, NEWTRADE_COMMENT, ");
        sql.append("LIKE_MOBILE_TRADE, LIKE_DISCNT_MODE, ");
        sql.append("TO_CHAR(FINANCE_EARNING) FINANCE_EARNING, EARNING_ORDER, ");
        sql.append("CALLING_POLICY_FORCE, SUBCLASS_ID, WEBSITE, FAX_NBR, EMAIL, POST_CODE, ");
        sql.append("TO_CHAR(GROUP_VALID_SCORE) GROUP_VALID_SCORE, ");
        sql.append("TO_CHAR(GROUP_SUM_SCORE) GROUP_SUM_SCORE, GROUP_MGR_SN, ");
        sql.append("TO_CHAR(GROUP_MGR_USER_ID) GROUP_MGR_USER_ID, GROUP_MGR_CUST_NAME, ");
        sql.append("BASE_ACCESS_NO, BASE_ACCESS_NO_KIND, CUST_SERV_NBR, EC_CODE, IF_SHORT_PIN, ");
        sql.append("AUDIT_STATE, TO_CHAR(AUDIT_DATE, 'yyyy-mm-dd hh24:mi:ss') AUDIT_DATE, ");
        sql.append("AUDIT_STAFF_ID, AUDIT_NOTE, REMOVE_FLAG, REMOVE_METHOD, ");
        sql.append("REMOVE_REASON_CODE, TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, ");
        sql.append("IN_STAFF_ID, IN_DEPART_ID, REMOVE_TAG, ");
        sql.append("TO_CHAR(REMOVE_DATE, 'yyyy-mm-dd hh24:mi:ss') REMOVE_DATE, ");
        sql.append("REMOVE_STAFF_ID, REMOVE_CHANGE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, ");
        sql.append("RSRV_NUM3, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, ");
        sql.append("RSRV_STR6, RSRV_STR7, RSRV_STR8, ");
        sql.append("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, ");
        sql.append("RSRV_TAG2, RSRV_TAG3 ");
        sql.append("FROM TF_F_CUST_GROUP ");
        sql.append("WHERE CUST_ID = :CUST_ID ");

        IDataset ids = Dao.qryBySql(sql, param, Route.CONN_CRM_CG);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        IData map = ids.getData(0);

        map.put("GROUP_TYPE_NAME", StaticUtil.getStaticValue("CUSTGROUP_GROUPTYPE", map.getString("GROUP_TYPE")));
        map.put("CLASS_NAME", StaticUtil.getStaticValue("CUSTGROUP_CLASSID", map.getString("CLASS_ID")));
        map.put("ENTERPRISE_TYPE_NAME", StaticUtil.getStaticValue("TD_S_ENTERPRISETYPE", map.getString("ENTERPRISE_TYPE_CODE")));
        map.put("JURISTIC_TYPE_NAME", StaticUtil.getStaticValue("TD_S_JURISTIC_TYPE", map.getString("JURISTIC_TYPE_CODE")));
        map.put("CALLING_TYPE_NAME", StaticUtil.getStaticValue("TD_S_CALLINGTYPE", map.getString("CALLING_TYPE_CODE")));
        map.put("SUB_CALLING_TYPE_NAME", StaticUtil.getStaticValue("TD_S_CALLINGSUBTYPE", map.getString("SUB_CALLING_TYPE_CODE")));
        map.put("ENTERPRISE_SIZE_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_ENTERPRISE_SIZE",
                "ENTERPRISE_SIZE_CODE", "ENTERPRISE_SIZE_NAME", map.getString("ENTERPRISE_SIZE_CODE")));

        String eparchyCode = map.getString("EPARCHY_CODE", "");
        String cityCode = map.getString("CITY_CODE", "");

        map.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(eparchyCode));
        map.put("CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(cityCode));

        return map;
    }

    /**
     * 根据GROUP_ID查询集团客户信息
     *
     * @param groupId
     * @return
     * @throws Exception
     */
    public static IData qryGrpInfoByGrpId(String groupId) throws Exception
    {
        // 是否缓存
        boolean isCache = BizEnv.getEnvBoolean("crm.uca.cache", true);

        if (isCache == false)
        {
            IData ids = qryGrpInfoByGrpIdFromDB(groupId);

            return ids;
        }

        // 得到缓存key
        String cacheKey = CacheKey.getUcaKeyGroupByGrpId(groupId, Route.CONN_CRM_CG);

        // 从缓存中取数据
        Object cacheObj = null;
        boolean cacheOK = true;

        try
        {
            cacheObj = SharedCache.get(cacheKey);
        }
        catch (Exception e)
        {
            cacheOK = false;
            cacheObj = null;

            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据失败[").append(cacheKey).append("]");

            logger.error(sb);
        }

        // 缓存无，从数据库中查
        if (cacheObj == null)
        {
            IData map = qryGrpInfoByGrpIdFromDB(groupId);

            // 将数据放入缓存
            if (cacheOK == true)
            {
                SharedCache.set(cacheKey, map, 600);

                if (IDataUtil.isNotEmpty(map))
                {
                    // 同时放custid的
                    String custId = map.getString("CUST_ID");

                    String cacheKeyCustId = CacheKey.getUcaKeyGroupByCustId(custId, Route.CONN_CRM_CG);

                    SharedCache.set(cacheKeyCustId, map, 600);
                }
            }

            return map;
        }
        else
        {
            // 往后顺延
            SharedCache.touch(cacheKey, 600);
        }

        // 缓存有，直接返回缓存对象
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据成功[").append(cacheKey).append("]");
            logger.debug(sb);
        }

        IData map = (IData) cacheObj;

        return map;
    }

    /**
     * 根据GROUP_ID查询集团客户信息
     *
     * @param groupId
     * @return
     * @throws Exception
     */
    private static IData qryGrpInfoByGrpIdFromDB(String groupId) throws Exception
    {
        IData data = new DataMap();
        data.put("GROUP_ID", groupId);

        StringBuilder sql = new StringBuilder(4000);

        sql.append("SELECT TO_CHAR(CUST_ID) CUST_ID, GROUP_ID, CUST_NAME, GROUP_TYPE, GROUP_ROLE, ");
        sql.append("CLASS_ID, CLASS_ID2, LAST_CLASS_ID, ");
        sql.append("TO_CHAR(CLASS_CHANGE_DATE, 'yyyy-mm-dd hh24:mi:ss') CLASS_CHANGE_DATE, ");
        sql.append("CUST_CLASS_TYPE, GROUP_ATTR, GROUP_STATUS, GROUP_ADDR, GROUP_SOURCE, ");
        sql.append("PROVINCE_CODE, EPARCHY_CODE, CITY_CODE, CITY_CODE_U, SUPER_GROUP_ID,SERV_LEVEL, ");
        sql.append("SUPER_GROUP_NAME, PNATIONAL_GROUP_ID, PNATIONAL_GROUP_NAME, ");
        sql.append("MP_GROUP_CUST_CODE, UNIFY_PAY_CODE, ORG_STRUCT_CODE, CUST_MANAGER_ID, ");
        sql.append("CUST_MANAGER_APPR, ");
        sql.append("TO_CHAR(ASSIGN_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSIGN_DATE, ");
        sql.append("ASSIGN_STAFF_ID, CALLING_TYPE_CODE, SUB_CALLING_TYPE_CODE, ");
        sql.append("CALLING_AREA_CODE, CALL_TYPE, ACCEPT_CHANNEL, AGREEMENT, BUSI_TYPE, ");
        sql.append("GROUP_CONTACT_PHONE, ENTERPRISE_TYPE_CODE, ENTERPRISE_SIZE_CODE, ");
        sql.append("ENTERPRISE_SCOPE, JURISTIC_TYPE_CODE, ");
        sql.append("TO_CHAR(JURISTIC_CUST_ID) JURISTIC_CUST_ID, JURISTIC_NAME, ");
        sql.append("BUSI_LICENCE_TYPE, BUSI_LICENCE_NO, ");
        sql.append("TO_CHAR(BUSI_LICENCE_VALID_DATE, 'yyyy-mm-dd hh24:mi:ss') BUSI_LICENCE_VALID_DATE, ");
        sql.append("GROUP_MEMO, BANK_ACCT, BANK_NAME, TO_CHAR(REG_MONEY) REG_MONEY, ");
        sql.append("TO_CHAR(REG_DATE, 'yyyy-mm-dd hh24:mi:ss') REG_DATE, CUST_AIM, SCOPE, ");
        sql.append("MAIN_BUSI, MAIN_TRADE, EMP_LSAVE, ");
        sql.append("TO_CHAR(LATENCY_FEE_SUM) LATENCY_FEE_SUM, TO_CHAR(YEAR_GAIN) YEAR_GAIN, ");
        sql.append("TO_CHAR(TURNOVER) TURNOVER, CONSUME, TO_CHAR(COMM_BUDGET) COMM_BUDGET, ");
        sql.append("TO_CHAR(GTEL_BUDGET) GTEL_BUDGET, TO_CHAR(LTEL_BUDGET) LTEL_BUDGET, ");
        sql.append("GROUP_ADVERSARY, VPMN_GROUP_ID, TO_CHAR(VPMN_NUM) VPMN_NUM, ");
        sql.append("TO_CHAR(USER_NUM) USER_NUM, TO_CHAR(EMP_NUM_LOCAL) EMP_NUM_LOCAL, ");
        sql.append("TO_CHAR(EMP_NUM_CHINA) EMP_NUM_CHINA, TO_CHAR(EMP_NUM_ALL) EMP_NUM_ALL, ");
        sql.append("TO_CHAR(TELECOM_NUM_GH) TELECOM_NUM_GH, ");
        sql.append("TO_CHAR(TELECOM_NUM_XLT) TELECOM_NUM_XLT, ");
        sql.append("TO_CHAR(MOBILE_NUM_CHINAGO) MOBILE_NUM_CHINAGO, ");
        sql.append("TO_CHAR(MOBILE_NUM_GLOBAL) MOBILE_NUM_GLOBAL, ");
        sql.append("TO_CHAR(MOBILE_NUM_MZONE) MOBILE_NUM_MZONE, ");
        sql.append("TO_CHAR(MOBILE_NUM_LOCAL) MOBILE_NUM_LOCAL, ");
        sql.append("TO_CHAR(UNICOM_NUM_G) UNICOM_NUM_G, TO_CHAR(UNICOM_NUM_C) UNICOM_NUM_C, ");
        sql.append("TO_CHAR(UNICOM_NUM_GC) UNICOM_NUM_GC, ");
        sql.append("TO_CHAR(PRODUCT_NUM_LOCAL) PRODUCT_NUM_LOCAL, ");
        sql.append("TO_CHAR(PRODUCT_NUM_OTHER) PRODUCT_NUM_OTHER, ");
        sql.append("TO_CHAR(PRODUCT_NUM_USE) PRODUCT_NUM_USE, ");
        sql.append("TO_CHAR(EMPLOYEE_ARPU) EMPLOYEE_ARPU, ");
        sql.append("TO_CHAR(NETRENT_PAYOUT) NETRENT_PAYOUT, ");
        sql.append("TO_CHAR(MOBILE_PAYOUT) MOBILE_PAYOUT, ");
        sql.append("TO_CHAR(UNICOM_PAYOUT) UNICOM_PAYOUT, ");
        sql.append("TO_CHAR(TELECOM_PAYOUT_XLT) TELECOM_PAYOUT_XLT, GROUP_PAY_MODE, ");
        sql.append("PAYFOR_WAY_CODE, WRITEFEE_COUNT, TO_CHAR(WRITEFEE_SUM) WRITEFEE_SUM, ");
        sql.append("TO_CHAR(USER_NUM_FULLFREE) USER_NUM_FULLFREE, ");
        sql.append("TO_CHAR(USER_NUM_WRITEOFF) USER_NUM_WRITEOFF, ");
        sql.append("TO_CHAR(BOSS_FEE_SUM) BOSS_FEE_SUM, DOYEN_STAFF_ID, NEWTRADE_COMMENT, ");
        sql.append("LIKE_MOBILE_TRADE, LIKE_DISCNT_MODE, ");
        sql.append("TO_CHAR(FINANCE_EARNING) FINANCE_EARNING, EARNING_ORDER, ");
        sql.append("CALLING_POLICY_FORCE, SUBCLASS_ID, WEBSITE, FAX_NBR, EMAIL, POST_CODE, ");
        sql.append("TO_CHAR(GROUP_VALID_SCORE) GROUP_VALID_SCORE, ");
        sql.append("TO_CHAR(GROUP_SUM_SCORE) GROUP_SUM_SCORE, GROUP_MGR_SN, ");
        sql.append("TO_CHAR(GROUP_MGR_USER_ID) GROUP_MGR_USER_ID, GROUP_MGR_CUST_NAME, ");
        sql.append("BASE_ACCESS_NO, BASE_ACCESS_NO_KIND, CUST_SERV_NBR, EC_CODE, IF_SHORT_PIN, ");
        sql.append("AUDIT_STATE, TO_CHAR(AUDIT_DATE, 'yyyy-mm-dd hh24:mi:ss') AUDIT_DATE, ");
        sql.append("AUDIT_STAFF_ID, AUDIT_NOTE, REMOVE_FLAG, REMOVE_METHOD, ");
        sql.append("REMOVE_REASON_CODE, TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, ");
        sql.append("IN_STAFF_ID, IN_DEPART_ID, REMOVE_TAG, ");
        sql.append("TO_CHAR(REMOVE_DATE, 'yyyy-mm-dd hh24:mi:ss') REMOVE_DATE, ");
        sql.append("REMOVE_STAFF_ID, REMOVE_CHANGE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, ");
        if (ProvinceUtil.isProvince(ProvinceUtil.SHXI) == false && ProvinceUtil.isProvince(ProvinceUtil.QHAI) == false)
        {
            sql.append("GROUP_CHECK, SUPER_FLAG, PROVINCE_FLAG, IMPORT_FLAG, DANGER_FLAG, ");
        }
        if (ProvinceUtil.isProvince(ProvinceUtil.QHAI) == true)
        {
            sql.append("PHOTO_TAG, ");
        }
        sql.append("RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, ");
        sql.append("RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, ");
        sql.append("RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, ");
        sql.append("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("RSRV_TAG1, RSRV_TAG2, RSRV_TAG3 ");
        sql.append("FROM TF_F_CUST_GROUP ");
        sql.append("WHERE GROUP_ID = :GROUP_ID ");
        sql.append("AND REMOVE_TAG = '0' ");

        IDataset ids = Dao.qryBySql(sql, data, Route.CONN_CRM_CG);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        IData map = ids.getData(0);

        map.put("GROUP_TYPE_NAME", StaticUtil.getStaticValue("CUSTGROUP_GROUPTYPE", map.getString("GROUP_TYPE")));
        map.put("CLASS_NAME", StaticUtil.getStaticValue("CUSTGROUP_CLASSID", map.getString("CLASS_ID")));
        map.put("ENTERPRISE_TYPE_NAME", StaticUtil.getStaticValue("TD_S_ENTERPRISETYPE", map.getString("ENTERPRISE_TYPE_CODE")));
        map.put("CALLING_TYPE_NAME", StaticUtil.getStaticValue("TD_S_CALLINGTYPE", map.getString("CALLING_TYPE_CODE")));
        map.put("SUB_CALLING_TYPE_NAME", StaticUtil.getStaticValue("TD_S_CALLINGSUBTYPE", map.getString("SUB_CALLING_TYPE_CODE")));
        map.put("ENTERPRISE_SIZE_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_ENTERPRISE_SIZE",
                "ENTERPRISE_SIZE_CODE", "ENTERPRISE_SIZE_NAME", map.getString("ENTERPRISE_SIZE_CODE")));

        String eparchyCode = map.getString("EPARCHY_CODE", "");
        String cityCode = map.getString("CITY_CODE", "");

        map.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(eparchyCode));
        map.put("CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(cityCode));

        return map;
    }

    /**
     * 根据USER_ID查询集团客户信息
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryGrpInfoByUserId(String userId) throws Exception
    {
        IData map = qryUserInfoByUserIdForGrp(userId);

        if (IDataUtil.isEmpty(map))
        {
            return map;
        }

        String custId = map.getString("CUST_ID");

        map = qryGrpInfoByCustId(custId);

        return map;
    }

    /**
     * 根据USER_ID查询最后一条付费关系信息,不带路由
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryLastPayRelaByUserId(String userId) throws Exception
    {
        return qryLastPayRelaByUserId(userId, null);
    }

    /**
     * 根据USER_ID查询最后一条付费关系信息,带路由
     *
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryLastPayRelaByUserId(String userId, String routeId) throws Exception
    {
        return qryLastPayRelaByUserIdFromDb(userId, routeId);
    }

    /**
     * 根据USER_ID查询集团用户最后一条付费关系信息
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryLastPayRelaByUserIdForGrp(String userId) throws Exception
    {
        return qryLastPayRelaByUserId(userId, Route.CONN_CRM_CG);
    }

    /**
     * 根据USER_ID查询最后一条付费关系信息,带路由
     *
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IData qryLastPayRelaByUserIdFromDb(String userId, String routeId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("DEFAULT_TAG", "1");

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, TO_CHAR(USER_ID) USER_ID, ");
        sql.append("TO_CHAR(ACCT_ID) ACCT_ID, PAYITEM_CODE, ACCT_PRIORITY, ");
        sql.append("USER_PRIORITY, TO_CHAR(ADDUP_MONTHS) ADDUP_MONTHS, ");
        sql.append("ADDUP_METHOD, BIND_TYPE, DEFAULT_TAG, ACT_TAG, ");
        sql.append("LIMIT_TYPE, TO_CHAR(LIMIT) LIMIT, COMPLEMENT_TAG, ");
        sql.append("TO_CHAR(INST_ID) INST_ID, START_CYCLE_ID, ");
        sql.append("END_CYCLE_ID, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, ");
        sql.append("RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
        sql.append("RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10 ");
        sql.append("FROM TF_A_PAYRELATION ");
        sql.append("WHERE USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND DEFAULT_TAG = :DEFAULT_TAG ");
        sql.append("AND ACT_TAG = '1' ");
        sql.append("AND START_CYCLE_ID <= END_CYCLE_ID ");
        sql.append("AND END_CYCLE_ID = ");
        sql.append("(SELECT MAX(A.END_CYCLE_ID) ");
        sql.append("FROM TF_A_PAYRELATION A ");
        sql.append("WHERE A.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND A.PARTITION_ID = ");
        sql.append("MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND A.DEFAULT_TAG = :DEFAULT_TAG ");
        sql.append("AND ACT_TAG = '1' ");
        sql.append("AND START_CYCLE_ID <= END_CYCLE_ID) ");

        IDataset ids = Dao.qryBySql(sql, data, routeId);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        return ids.getData(0);
    }

    /**
     * 根据USER_ID查询用户信息(产品),默认路由
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryMainProdInfoByUserId(String userId) throws Exception
    {

        IData map = qryMainProdInfoByUserId(userId, null);

        return map;
    }

    /**
     * 根据USER_ID查询用户信息(产品),指定路由
     *
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryMainProdInfoByUserId(String userId, String routeId) throws Exception
    {
        // 是否缓存
        boolean isCache = BizEnv.getEnvBoolean("crm.uca.cache", true);

        if (isCache == false)
        {
            IData map = qryMainProdInfoByUserIdFromDB(userId, routeId);

            return map;
        }

        // 得到缓存key
        String cacheKey = CacheKey.getUcaKeyMainProdByUserId(userId, routeId);

        // 从缓存中取数据
        Object cacheObj = null;
        boolean cacheOK = true;

        try
        {
            cacheObj = SharedCache.get(cacheKey);
        }
        catch (Exception e)
        {
            cacheOK = false;
            cacheObj = null;

            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据失败[").append(cacheKey).append("]");

            logger.error(sb);
        }

        // 缓存无，从数据库中查
        if (cacheObj == null)
        {
            IData map = qryMainProdInfoByUserIdFromDB(userId, routeId);

            // 将数据放入缓存
            if (cacheOK == true)
            {
                SharedCache.set(cacheKey, map, 600);
            }

            return map;
        }
        else
        {
            // 往后顺延
            SharedCache.touch(cacheKey, 600);
        }

        // 缓存有，直接返回缓存对象
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据成功[").append(cacheKey).append("]");
            logger.debug(sb);
        }

        IData map = (IData) cacheObj;

        return map;
    }

    /**
     * 根据USER_ID查询集团用户信息(产品)
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryMainProdInfoByUserIdForGrp(String userId) throws Exception
    {

        IData map = qryMainProdInfoByUserId(userId, Route.CONN_CRM_CG);

        return map;
    }

    /**
     * 根据USER_ID查询集团用户信息(产品),带路由
     *
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IData qryMainProdInfoByUserIdFromDB(String userId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, TO_CHAR(USER_ID) USER_ID, TO_CHAR(USER_ID_A) USER_ID_A, ");
        sql.append("PRODUCT_ID, PRODUCT_MODE, BRAND_CODE, TO_CHAR(INST_ID) INST_ID, ");
        sql.append("TO_CHAR(CAMPN_ID) CAMPN_ID, ");
        sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, ");
        sql.append("RSRV_NUM3, TO_CHAR(RSRV_NUM4) RSRV_NUM4, TO_CHAR(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, ");
        sql.append("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, ");
        sql.append("RSRV_TAG2, RSRV_TAG3, MAIN_TAG ");
        sql.append("FROM TF_F_USER_PRODUCT T ");
        sql.append("WHERE T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND T.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND T.MAIN_TAG = '1' ");
        sql.append("AND SYSDATE BETWEEN START_DATE AND END_DATE ");

        IDataset ids = Dao.qryBySql(sql, param, routeId);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        IData map = ids.getData(0);

        map.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(map.getString("PRODUCT_ID")));
        map.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(map.getString("BRAND_CODE")));

        return map;
    }

    /**
     * 根据USER_ID查询用户付费关系信息,默认路由
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryPayRelaByUserId(String userId) throws Exception
    {
        return qryPayRelaByUserId(userId, null);
    }

    /**
     * 根据USER_ID查询用户付费关系信息,指定路由
     *
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryPayRelaByUserId(String userId, String routeId) throws Exception
    {
        // 得到当前有效的
        IData payrela = qryDefaultPayRelaByUserId(userId, routeId);

        if (IDataUtil.isNotEmpty(payrela))
        {
            return payrela;
        }

        // 得到最后的
        payrela = qryLastPayRelaByUserId(userId, routeId);

        return payrela;
    }

    /**
     * 根据USER_ID查询集团用户付费关系信息
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryPayRelaByUserIdForGrp(String userId) throws Exception
    {
        return qryPayRelaByUserId(userId, Route.CONN_CRM_CG);
    }

    /**
     * 根据CUST_ID查询个人客户信息,默认路由
     *
     * @param custId
     * @return
     * @throws Exception
     */
    public static IData qryPerInfoByCustId(String custId) throws Exception
    {
        IData map = qryPerInfoByCustId(custId, null);

        return map;
    }

    /**
     * 根据CUST_ID查询个人客户信息,带路由
     *
     * @param custId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryPerInfoByCustId(String custId, String routeId) throws Exception
    {
        // 是否缓存
        boolean isCache = BizEnv.getEnvBoolean("crm.uca.cache", true);

        if (isCache == false)
        {
            IData map = qryPerInfoByCustIdFromDb(custId, routeId);

            return map;
        }

        // 得到缓存key
        String cacheKey = CacheKey.getUcaKeyPersonByCustId(custId, routeId);

        // 从缓存中取数据
        Object cacheObj = null;
        boolean cacheOK = true;

        try
        {
            cacheObj = SharedCache.get(cacheKey);
        }
        catch (Exception e)
        {
            cacheOK = false;
            cacheObj = null;

            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据失败[").append(cacheKey).append("]");

            logger.error(sb);
        }

        // 缓存无，从数据库中查
        if (cacheObj == null)
        {
            IData map = qryPerInfoByCustIdFromDb(custId, routeId);

            // 将数据放入缓存
            if (cacheOK == true)
            {
                SharedCache.set(cacheKey, map, 600);
            }

            return map;
        }
        else
        {
            // 往后顺延
            SharedCache.touch(cacheKey, 600);
        }

        // 缓存有，直接返回缓存对象
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据成功[").append(cacheKey).append("]");
            logger.debug(sb);
        }

        IData map = (IData) cacheObj;

        return map;
    }

    /**
     * 根据CUST_ID查询个人客户信息,带路由
     *
     * @param custId
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IData qryPerInfoByCustIdFromDb(String custId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, TO_CHAR(CUST_ID) CUST_ID, PSPT_TYPE_CODE, PSPT_ID, ");
        sql.append("TO_CHAR(PSPT_END_DATE, 'YYYY-MM-DD HH24:MI:SS') PSPT_END_DATE, PSPT_ADDR, ");
        sql.append("CUST_NAME, SEX, EPARCHY_CODE, CITY_CODE, ");
        sql.append("TO_CHAR(BIRTHDAY, 'YYYY-MM-DD') BIRTHDAY, BIRTHDAY_LUNAR, ");
        sql.append("BIRTHDAY_FLAG, POST_ADDRESS, POST_CODE, POST_PERSON, PHONE, FAX_NBR, ");
        sql.append("EMAIL, HOME_ADDRESS, HOME_PHONE, WORK_NAME, WORK_KIND, WORK_ADDRESS, ");
        sql.append("WORK_PHONE, WORK_POST_CODE, CALLING_TYPE_CODE, SUB_CALLING_TYPE_CODE, ");
        sql.append("WORK_DEPART, JOB, CONTACT, CONTACT_PHONE, CONTACT_TYPE_CODE, CONTACT_FREQ, ");
        sql.append("NATIONALITY_CODE,LOCAL_NATIVE_CODE, POPULATION, LANGUAGE_CODE, FOLK_CODE, ");
        sql.append("RELIGION_CODE, JOB_TYPE_CODE, REVENUE_LEVEL_CODE, EDUCATE_DEGREE_CODE, ");
        sql.append("EDUCATE_GRADE_CODE, GRADUATE_SCHOOL, SPECIALITY, CHARACTER_TYPE_CODE, ");
        sql.append("HEALTH_STATE_CODE, MARRIAGE, WEBUSER_ID, WEB_PASSWD, COMMUNITY_ID, ");
        sql.append("REMOVE_TAG, TO_CHAR(REMOVE_DATE, 'YYYY-MM-DD HH24:MI:SS') REMOVE_DATE, ");
        sql.append("REMOVE_STAFF_ID, REMOVE_CHANGE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, ");
        sql.append("RSRV_NUM3, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, ");
        sql.append("RSRV_STR6, RSRV_STR7, RSRV_STR8, ");
        sql.append("TO_CHAR(RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3, RSRV_TAG1, ");
        if(ProvinceUtil.isProvince(ProvinceUtil.QHAI)){
            sql.append("RSRV_TAG2, RSRV_TAG3,COMM_NAME,COMM_PSPTTYPE,COMM_PSPTNO,COMM_ADDR,COMM_PHONE ");
        }else{
            sql.append("RSRV_TAG2, RSRV_TAG3 ");
        }
        sql.append("FROM TF_F_CUST_PERSON ");
        sql.append("WHERE CUST_ID = TO_NUMBER(:CUST_ID) ");
        sql.append("AND PARTITION_ID = MOD(TO_NUMBER(:CUST_ID), 10000) ");

        IDataset ids = Dao.qryBySql(sql, param, routeId);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        IData map = ids.getData(0);

        map.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(map.getString("EPARCHY_CODE")));
        if(ProvinceUtil.isProvince(ProvinceUtil.QHAI)){
            map.put("PSPT_TYPE_NAME",
                    StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA",
                            new String[] { "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE", "EPARCHY_CODE" }, "PARAM_NAME",
                            new String[] { "CSM", "1987", map.getString("PSPT_TYPE_CODE"), "ZZZZ" }));
        }else{
            map.put("PSPT_TYPE_NAME", StaticUtil.getStaticValue("PSPT_TYPE_CODE", map.getString("PSPT_TYPE_CODE")));
        }
        return map;
    }

    /**
     * 根据SN查询个人客户信息
     *
     * @param sn
     * @return
     * @throws Exception
     */
    private static IData qryPerInfoBySn(String sn) throws Exception
    {
        IData idUser = qryUserInfoBySn(sn);

        if (IDataUtil.isEmpty(idUser))
        {
            return null;
        }

        String custId = idUser.getString("CUST_ID");

        IData idCust = qryPerInfoByCustId(custId);

        return idCust;
    }

    /**
     * 根据USER_ID查询个人客户信息
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryPerInfoByUserId(String userId) throws Exception
    {
        IData idUser = qryUserInfoByUserId(userId);

        if (IDataUtil.isEmpty(idUser))
        {
            return null;
        }

        String custId = idUser.getString("CUST_ID");

        IData idCust = qryPerInfoByCustId(custId);

        return idCust;
    }

    /**
     * 查询联通转接号码
     *
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset qryUnionTransPhone(IData params) throws Exception
    {
        return Dao.qryByCodeParser("TI_B_TRANS_PHONE", "SEL_BY_PHONE_A_INTF_NG", params);
    }

    /**
     * 查询联通转接号码
     *
     * @param phoneCodeB
     * @return
     * @throws Exception
     */
    public static IDataset qryUnionTransPhone(String phoneCodeB) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("PHONE_CODE_B", phoneCodeB);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PHONE_CODE_A, PHONE_CODE_B,");
        sql.append(" TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append(" TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append(" TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME ");
        sql.append(" FROM TF_F_TRANS_PHONE ");
        sql.append(" WHERE PHONE_CODE_B = :PHONE_CODE_B ");
        sql.append(" AND SYSDATE < END_DATE");

        return Dao.qryBySql(sql, inparams);
    }

    /**
     * 根据USER_ID查询用户账期信息,默认路由
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserAcctDaysByUserId(String userId) throws Exception
    {
        return qryUserAcctDaysByUserId(userId, null);
    }

    /**
     * 根据USER_ID查询用户账期信息,指定路由
     *
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserAcctDaysByUserId(String userId, String routeId) throws Exception
    {
        // 得到缓存key
        String cacheKey = CacheKey.getUcaKeyAcctDaysByUserId(userId, routeId);

        // 从缓存中取数据
        Object cacheObj = null;
        boolean cacheOK = true;

        try
        {
            cacheObj = SharedCache.get(cacheKey);
        }
        catch (Exception e)
        {
            cacheOK = false;
            cacheObj = null;

            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据失败[").append(cacheKey).append("]");

            logger.error(sb);
        }

        // 缓存无，从数据库中查
        if (cacheObj == null)
        {
            IDataset idataset = qryUserAcctDaysByUserIdFromDB(userId, routeId);

            // 将数据放入缓存
            if (cacheOK == true && IDataUtil.isNotEmpty(idataset))
            {
                SharedCache.set(cacheKey, idataset, 600);
            }

            return idataset;
        }
        else
        {
            // 往后顺延
            SharedCache.touch(cacheKey, 600);
        }

        // 缓存有，直接返回缓存对象
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据成功[").append(cacheKey).append("]");
            logger.debug(sb);
        }

        IDataset idataset = (IDataset) cacheObj;

        return idataset;
    }

    /**
     * 根据USER_ID查询用户账期信息,指定路由
     *
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IDataset qryUserAcctDaysByUserIdFromDB(String userId, String routeId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT TO_CHAR(T.USER_ID) USER_ID, T.PARTITION_ID, ");
        sql.append("T.ACCT_DAY, T.CHG_TYPE, T.CHG_MODE, ");
        sql.append("TO_CHAR(T.CHG_DATE, 'yyyy-mm-dd hh24:mi:ss') CHG_DATE, ");
        sql.append("TO_CHAR(T.FIRST_DATE, 'yyyy-mm-dd') FIRST_DATE, ");
        sql.append("T.INST_ID, ");
        sql.append("TO_CHAR(T.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("TO_CHAR(T.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("TO_CHAR(T.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("T.UPDATE_STAFF_ID, T.UPDATE_DEPART_ID, T.REMARK, ");
        sql.append("T.RSRV_NUM1, T.RSRV_NUM2, T.RSRV_NUM3, T.RSRV_NUM4, ");
        sql.append("T.RSRV_NUM5, T.RSRV_STR1, T.RSRV_STR2, T.RSRV_STR3, ");
        sql.append("T.RSRV_STR4, T.RSRV_STR5, ");
        sql.append("TO_CHAR(T.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(T.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(T.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("T.RSRV_TAG1, T.RSRV_TAG2, T.RSRV_TAG3 ");
        sql.append("FROM TF_F_USER_ACCTDAY T ");
        sql.append("WHERE T.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("AND T.END_DATE > T.START_DATE ");
        sql.append("AND T.END_DATE > SYSDATE ");
        sql.append("ORDER BY T.START_DATE ASC ");

        return Dao.qryBySql(sql, inparams, routeId);
    }

    /**
     * 根据SN查询有效用户信息
     *
     * @param sn
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoBySn(String sn) throws Exception
    {
        IData map = qryUserInfoBySn(sn, null);

        return map;
    }

    /**
     * 根据SN查询所有用户信息
     *
     * @param sn
     * @return
     * @throws Exception
     */
    public static IDataset qryAllUserInfoBySn(String sn) throws Exception
    {
        IDataset map = qryUserInfoBySnAll(sn, null);

        return map;
    }

    /**
     * 根据SN查询有效用户信息,指定路由
     *
     * @param sn
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoBySn(String sn, String routeId) throws Exception
    {
        // 是否缓存
        boolean isCache = BizEnv.getEnvBoolean("crm.uca.cache", true);

        if (isCache == false)
        {
            IData map = qryUserInfoBySnFromDB(sn, routeId);

            return map;
        }

        // 得到缓存key
        String cacheKey = CacheKey.getUcaKeyUserBySn(sn, routeId);

        // 从缓存中取数据
        Object cacheObj = null;
        boolean cacheOK = true;

        try
        {
            cacheObj = SharedCache.get(cacheKey);
        }
        catch (Exception e)
        {
            cacheOK = false;
            cacheObj = null;

            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据失败[").append(cacheKey).append("]");

            logger.error(sb);
        }

        // 缓存无，从数据库中查
        if (cacheObj == null)
        {
            IData map = qryUserInfoBySnFromDB(sn, routeId);

            // 将数据放入缓存
            if (cacheOK == true)
            {
                SharedCache.set(cacheKey, map, 600);

                if (IDataUtil.isNotEmpty(map))
                {
                    // 同时放userid的
                    String userId = map.getString("USER_ID");

                    String cacheKeyUser = CacheKey.getUcaKeyUserByUserId(userId, routeId);

                    SharedCache.set(cacheKeyUser, map, 600);
                }
            }

            return map;
        }
        else
        {
            // 往后顺延
            SharedCache.touch(cacheKey, 600);
        }

        // 缓存有，直接返回缓存对象
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据成功[").append(cacheKey).append("]");
            logger.debug(sb);
        }

        IData map = (IData) cacheObj;

        return map;
    }

    /**
     * 根据SN查询有效用户信息，查询所有库
     *
     * @param sn
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoBySnAllCrm(String sn) throws Exception
    {
        IData map = null;

        for (String routeId : Route.getAllCrmDb())
        {
            map = qryUserInfoBySn(sn, routeId);

            if (IDataUtil.isNotEmpty(map))
            {
                break;
            }
        }

        return map;
    }

    /**
     * 根据SN查询集团用户信息
     *
     * @param sn
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoBySnForGrp(String sn) throws Exception
    {

        IData map = qryUserInfoBySn(sn, Route.CONN_CRM_CG);

        return map;
    }

    /**
     * 根据SN查询集团用户信息,指定路由
     *
     * @param sn
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IData qryUserInfoBySnFromDB(String sn, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);

        StringBuilder sql = new StringBuilder(3000);

        sql.append("SELECT ");
        sql.append("U.PARTITION_ID, TO_CHAR(U.USER_ID) USER_ID, TO_CHAR(U.CUST_ID) CUST_ID, ");
        sql.append("TO_CHAR(U.USECUST_ID) USECUST_ID, EPARCHY_CODE, CITY_CODE, CITY_CODE_A, ");
        sql.append("USER_PASSWD, USER_DIFF_CODE, USER_TYPE_CODE, USER_TAG_SET, ");
        sql.append("USER_STATE_CODESET, NET_TYPE_CODE, SERIAL_NUMBER, CONTRACT_ID, ACCT_TAG, ");
        sql.append("PREPAY_TAG, MPUTE_MONTH_FEE, ");
        sql.append("TO_CHAR(MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE, ");
        sql.append("TO_CHAR(FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME, ");
        sql.append("TO_CHAR(LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME, ");
        sql.append("TO_CHAR(CHANGEUSER_DATE, 'yyyy-mm-dd hh24:mi:ss') CHANGEUSER_DATE, ");
        sql.append("IN_NET_MODE, TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, ");
        sql.append("IN_STAFF_ID, IN_DEPART_ID, OPEN_MODE, ");
        sql.append("TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, OPEN_STAFF_ID, ");
        sql.append("OPEN_DEPART_ID, DEVELOP_STAFF_ID, ");
        sql.append("TO_CHAR(DEVELOP_DATE, 'yyyy-mm-dd hh24:mi:ss') DEVELOP_DATE, ");
        sql.append("DEVELOP_DEPART_ID, DEVELOP_CITY_CODE, DEVELOP_EPARCHY_CODE, DEVELOP_NO, ");
        sql.append("TO_CHAR(ASSURE_CUST_ID) ASSURE_CUST_ID, ASSURE_TYPE_CODE, ");
        sql.append("TO_CHAR(ASSURE_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSURE_DATE, REMOVE_TAG, ");
        sql.append("TO_CHAR(PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME, ");
        sql.append("TO_CHAR(DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME, ");
        sql.append("REMOVE_EPARCHY_CODE, REMOVE_CITY_CODE, REMOVE_DEPART_ID, ");
        sql.append("REMOVE_REASON_CODE, ");
        sql.append("TO_CHAR(U.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("U.UPDATE_STAFF_ID, U.UPDATE_DEPART_ID, U.REMARK, U.RSRV_NUM1, U.RSRV_NUM2, ");
        sql.append("U.RSRV_NUM3, TO_CHAR(U.RSRV_NUM4) RSRV_NUM4, ");
        sql.append("TO_CHAR(U.RSRV_NUM5) RSRV_NUM5, U.RSRV_STR1, U.RSRV_STR2, U.RSRV_STR3, ");
        sql.append("U.RSRV_STR4, U.RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, ");
        sql.append("RSRV_STR10, TO_CHAR(U.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(U.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(U.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, U.RSRV_TAG1, ");
        sql.append("U.RSRV_TAG2, U.RSRV_TAG3 ");
        sql.append("FROM TF_F_USER U ");
        sql.append("WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.append("AND REMOVE_TAG = '0' ");

        IDataset ids = Dao.qryBySql(sql, param, routeId);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        IData userInfo = ids.getData(0);

        userInfo.put("USER_TYPE", UUserTypeInfoQry.getUserTypeByUserTypeCode(userInfo.getString("USER_TYPE_CODE")));
        userInfo.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(userInfo.getString("EPARCHY_CODE")));
        userInfo.put("CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(userInfo.getString("CITY_CODE")));

        return userInfo;
    }

    /**
     * 根据SN查询所有用户信息,指定路由
     *
     * @param sn
     * @param routeId
     * @return
     * @throws Exception
     */
    private static IDataset qryUserInfoBySnAll(String sn, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);

        StringBuilder sql = new StringBuilder(3000);

        sql.append("SELECT ");
        sql.append("U.PARTITION_ID, TO_CHAR(U.USER_ID) USER_ID, TO_CHAR(U.CUST_ID) CUST_ID, ");
        sql.append("TO_CHAR(U.USECUST_ID) USECUST_ID, EPARCHY_CODE, CITY_CODE, CITY_CODE_A, ");
        sql.append("USER_PASSWD, USER_DIFF_CODE, USER_TYPE_CODE, USER_TAG_SET, ");
        sql.append("USER_STATE_CODESET, NET_TYPE_CODE, SERIAL_NUMBER, CONTRACT_ID, ACCT_TAG, ");
        sql.append("PREPAY_TAG, MPUTE_MONTH_FEE, ");
        sql.append("TO_CHAR(MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE, ");
        sql.append("TO_CHAR(FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME, ");
        sql.append("TO_CHAR(LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME, ");
        sql.append("TO_CHAR(CHANGEUSER_DATE, 'yyyy-mm-dd hh24:mi:ss') CHANGEUSER_DATE, ");
        sql.append("IN_NET_MODE, TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, ");
        sql.append("IN_STAFF_ID, IN_DEPART_ID, OPEN_MODE, ");
        sql.append("TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, OPEN_STAFF_ID, ");
        sql.append("OPEN_DEPART_ID, DEVELOP_STAFF_ID, ");
        sql.append("TO_CHAR(DEVELOP_DATE, 'yyyy-mm-dd hh24:mi:ss') DEVELOP_DATE, ");
        sql.append("DEVELOP_DEPART_ID, DEVELOP_CITY_CODE, DEVELOP_EPARCHY_CODE, DEVELOP_NO, ");
        sql.append("TO_CHAR(ASSURE_CUST_ID) ASSURE_CUST_ID, ASSURE_TYPE_CODE, ");
        sql.append("TO_CHAR(ASSURE_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSURE_DATE, REMOVE_TAG, ");
        sql.append("TO_CHAR(PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME, ");
        sql.append("TO_CHAR(DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME, ");
        sql.append("REMOVE_EPARCHY_CODE, REMOVE_CITY_CODE, REMOVE_DEPART_ID, ");
        sql.append("REMOVE_REASON_CODE, ");
        sql.append("TO_CHAR(U.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("U.UPDATE_STAFF_ID, U.UPDATE_DEPART_ID, U.REMARK, U.RSRV_NUM1, U.RSRV_NUM2, ");
        sql.append("U.RSRV_NUM3, TO_CHAR(U.RSRV_NUM4) RSRV_NUM4, ");
        sql.append("TO_CHAR(U.RSRV_NUM5) RSRV_NUM5, U.RSRV_STR1, U.RSRV_STR2, U.RSRV_STR3, ");
        sql.append("U.RSRV_STR4, U.RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, ");
        sql.append("RSRV_STR10, TO_CHAR(U.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(U.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(U.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, U.RSRV_TAG1, ");
        sql.append("U.RSRV_TAG2, U.RSRV_TAG3 ");
        sql.append("FROM TF_F_USER U ");
        sql.append("WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");

        IDataset ids = Dao.qryBySql(sql, param, routeId);

        return ids;
    }

    /**
     * 根据USER_ID查询集团用户信息,默认路由
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoByUserId(String userId) throws Exception
    {

        IData map = qryUserInfoByUserId(userId, null);

        return map;
    }

    /**
     * 根据USER_ID查询用户信息,指定路由
     *
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoByUserId(String userId, String routeId) throws Exception
    {
        // 是否缓存
        boolean isCache = BizEnv.getEnvBoolean("crm.uca.cache", true);

        if (isCache == false)
        {
            IData map = qryUserInfoByUserIdFromDB(userId, routeId);

            if (IDataUtil.isEmpty(map))
            {
                return null;
            }

            IData idProduct = qryMainProdInfoByUserId(userId, routeId);

            if (IDataUtil.isNotEmpty(idProduct))
            {
                map.put("PRODUCT_ID", idProduct.getString("PRODUCT_ID"));
                map.put("PRODUCT_NAME", idProduct.getString("PRODUCT_NAME"));
                map.put("BRAND_CODE", idProduct.getString("BRAND_CODE"));
                map.put("BRAND_NAME", idProduct.getString("BRAND_NAME"));
                map.put("PRODUCT_MODE", idProduct.getString("PRODUCT_MODE"));
            }

            return map;
        }

        // 得到缓存key
        String cacheKey = CacheKey.getUcaKeyUserByUserId(userId, routeId);

        // 从缓存中取数据
        Object cacheObj = null;
        boolean cacheOK = true;

        try
        {
            cacheObj = SharedCache.get(cacheKey);
        }
        catch (Exception e)
        {
            cacheOK = false;
            cacheObj = null;

            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据失败[").append(cacheKey).append("]");

            logger.error(sb);
        }

        // 缓存无，从数据库中查
        if (cacheObj == null)
        {
            IData map = qryUserInfoByUserIdFromDB(userId, routeId);

            if (IDataUtil.isEmpty(map))
            {
                return null;
            }

            IData idProduct = qryMainProdInfoByUserId(userId, routeId);

            if (IDataUtil.isNotEmpty(idProduct))
            {
                map.put("PRODUCT_ID", idProduct.getString("PRODUCT_ID"));
                map.put("PRODUCT_NAME", idProduct.getString("PRODUCT_NAME"));
                map.put("BRAND_CODE", idProduct.getString("BRAND_CODE"));
                map.put("BRAND_NAME", idProduct.getString("BRAND_NAME"));
                map.put("PRODUCT_MODE", idProduct.getString("PRODUCT_MODE"));
            }

            // 将数据放入缓存
            if (cacheOK == true)
            {
                SharedCache.set(cacheKey, map, 600);
            }

            return map;
        }
        else
        {
            // 往后顺延
            SharedCache.touch(cacheKey, 600);
        }

        // 缓存有，直接返回缓存对象
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据成功[").append(cacheKey).append("]");
            logger.debug(sb);
        }

        IData map = (IData) cacheObj;

        return map;
    }

    /**
     * 根据USER_ID查询集团用户信息
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoByUserIdForGrp(String userId) throws Exception
    {

        IData map = qryUserInfoByUserId(userId, Route.CONN_CRM_CG);

        return map;
    }

    /**
     * 根据USER_ID查询用户信息,指定路由
     *
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoByUserIdFromDB(String userId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(2500);

        sql.append("SELECT U.PARTITION_ID, TO_CHAR(U.USER_ID) USER_ID, TO_CHAR(CUST_ID) CUST_ID, ");
        sql.append("TO_CHAR(USECUST_ID) USECUST_ID, EPARCHY_CODE, CITY_CODE, CITY_CODE_A, ");
        sql.append("USER_PASSWD, USER_DIFF_CODE, USER_TYPE_CODE, USER_TAG_SET, ");
        sql.append("USER_STATE_CODESET, NET_TYPE_CODE, SERIAL_NUMBER, CONTRACT_ID, ACCT_TAG, ");
        sql.append("PREPAY_TAG, MPUTE_MONTH_FEE, ");
        sql.append("TO_CHAR(MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE, ");
        sql.append("TO_CHAR(FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME, ");
        sql.append("TO_CHAR(LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME, ");
        sql.append("TO_CHAR(CHANGEUSER_DATE, 'yyyy-mm-dd hh24:mi:ss') CHANGEUSER_DATE, ");
        sql.append("IN_NET_MODE, TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, ");
        sql.append("IN_STAFF_ID, IN_DEPART_ID, OPEN_MODE, ");
        sql.append("TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, OPEN_STAFF_ID, ");
        sql.append("OPEN_DEPART_ID, DEVELOP_STAFF_ID, ");
        sql.append("TO_CHAR(DEVELOP_DATE, 'yyyy-mm-dd hh24:mi:ss') DEVELOP_DATE, ");
        sql.append("DEVELOP_DEPART_ID, DEVELOP_CITY_CODE, DEVELOP_EPARCHY_CODE, DEVELOP_NO, ");
        sql.append("TO_CHAR(ASSURE_CUST_ID) ASSURE_CUST_ID, ASSURE_TYPE_CODE, ");
        sql.append("TO_CHAR(ASSURE_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSURE_DATE, REMOVE_TAG, ");
        sql.append("TO_CHAR(PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME, ");
        sql.append("TO_CHAR(DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME, ");
        sql.append("REMOVE_EPARCHY_CODE, REMOVE_CITY_CODE, REMOVE_DEPART_ID, ");
        sql.append("REMOVE_REASON_CODE, ");
        sql.append("TO_CHAR(U.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("U.UPDATE_STAFF_ID, U.UPDATE_DEPART_ID, U.REMARK, U.RSRV_NUM1, U.RSRV_NUM2, ");
        sql.append("U.RSRV_NUM3, TO_CHAR(U.RSRV_NUM4) RSRV_NUM4, ");
        sql.append("TO_CHAR(U.RSRV_NUM5) RSRV_NUM5, U.RSRV_STR1, U.RSRV_STR2, U.RSRV_STR3, ");
        sql.append("U.RSRV_STR4, U.RSRV_STR5, U.RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, ");
        sql.append("RSRV_STR10, TO_CHAR(U.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(U.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(U.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, U.RSRV_TAG1, ");
        sql.append("U.RSRV_TAG2, U.RSRV_TAG3 ");
        sql.append("FROM TF_F_USER U ");
        sql.append("WHERE U.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND U.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");

        IDataset ids = Dao.qryBySql(sql, param, routeId);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        IData userInfo = ids.getData(0);

        userInfo.put("USER_TYPE", UUserTypeInfoQry.getUserTypeByUserTypeCode(userInfo.getString("USER_TYPE_CODE")));
        userInfo.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(userInfo.getString("EPARCHY_CODE")));
        userInfo.put("CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(userInfo.getString("CITY_CODE")));
        userInfo.put("STATE_NAME", USvcStateInfoQry.getSvcStateNameBySvcIdStateCode("0", userInfo.getString("USER_STATE_CODESET", "")));

        return userInfo;
    }

    public static IData getUserInfos(String SERIAL_NUMBER, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", SERIAL_NUMBER);

        StringBuilder sql = new StringBuilder(2500);
        sql.append("SELECT U.ACCT_TAG,T.IS_REAL_NAME ");
        sql.append("FROM TF_F_USER U ,TF_F_CUSTOMER T ");
        sql.append("WHERE U.CUST_ID = T.CUST_ID ");
        sql.append("AND U.SERIAL_NUMBER=:SERIAL_NUMBER ");

        IDataset ids = Dao.qryBySql(sql, param, routeId);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }
        IData userInfo = ids.getData(0);
        return userInfo;
    }

    /**
     * 根据SN查询用户(产品)信息
     *
     * @param sn
     * @return
     * @throws Exception
     */
    public static IData qryUserMainProdInfoBySn(String sn) throws Exception
    {
        IData map = qryUserMainProdInfoBySn(sn, null);

        return map;
    }

    /**
     * 根据SN查询用户(产品)信息,带路由
     *
     * @param sn
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryUserMainProdInfoBySn(String sn, String routeId) throws Exception
    {
        // 得到用户信息
        IData idUser = qryUserInfoBySn(sn, routeId);

        if (IDataUtil.isEmpty(idUser))
        {
            return null;
        }

        // 得到用户产品信息
        String userId = "";

        userId = idUser.getString("USER_ID");

        IData idProduct = qryMainProdInfoByUserId(userId, routeId);

        if (IDataUtil.isEmpty(idProduct))
        {
            return null;
        }

        idUser.put("PRODUCT_ID", idProduct.getString("PRODUCT_ID"));
        idUser.put("PRODUCT_NAME", idProduct.getString("PRODUCT_NAME"));
        idUser.put("BRAND_CODE", idProduct.getString("BRAND_CODE"));
        idUser.put("BRAND_NAME", idProduct.getString("BRAND_NAME"));
        idUser.put("PRODUCT_MODE", idProduct.getString("PRODUCT_MODE"));

        return idUser;
    }

    /**
     * 根据SN查询集团用户(产品)信息
     *
     * @param sn
     * @return
     * @throws Exception
     */
    public static IData qryUserMainProdInfoBySnForGrp(String sn) throws Exception
    {

        IData map = qryUserMainProdInfoBySn(sn, Route.CONN_CRM_CG);

        return map;
    }

    /**
     * 根据USER_ID查询用户(产品)信息
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryUserMainProdInfoByUserId(String userId) throws Exception
    {

        IData map = qryUserMainProdInfoByUserId(userId, null);

        return map;
    }

    /**
     * 根据USER_ID查询用户(产品)信息,指定路由
     *
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryUserMainProdInfoByUserId(String userId, String routeId) throws Exception
    {
        // 得到用户信息
        IData idUser = qryUserInfoByUserId(userId, routeId);

        if (IDataUtil.isEmpty(idUser))
        {
            return null;
        }

        // 得到用户产品信息
        IData idProduct = null;

        idProduct = qryMainProdInfoByUserId(userId, routeId);

        if (IDataUtil.isEmpty(idProduct))
        {
            return null;
        }

        idUser.put("PRODUCT_ID", idProduct.getString("PRODUCT_ID"));
        idUser.put("PRODUCT_MODE", idProduct.getString("PRODUCT_MODE"));
        idUser.put("PRODUCT_NAME", idProduct.getString("PRODUCT_NAME"));
        idUser.put("BRAND_CODE", idProduct.getString("BRAND_CODE"));
        idUser.put("BRAND_NAME", idProduct.getString("BRAND_NAME"));
        //modi by lim 2018/3/13
        idUser.put("PROD_INST_ID", idProduct.getString("INST_ID"));
        return idUser;
    }

    /**
     * 根据用户id查询用户信息，判断是否实名制和激活
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData getUserInfo(String SERIAL_NUMBER, String routeId) throws Exception{

        IData map = getUserInfos(SERIAL_NUMBER, routeId);
        return map;

    }

    /**
     * 根据USER_ID查询集团用户(产品)信息
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryUserMainProdInfoByUserIdForGrp(String userId) throws Exception
    {

        IData map = qryUserMainProdInfoByUserId(userId, Route.CONN_CRM_CG);

        return map;
    }
    /**
     * 根据用户id去查找数据，判断是否已经实名制和已经激活
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData getUserInfo(String SERIAL_NUMBER) throws Exception
    {

        IData map = getUserInfo(SERIAL_NUMBER, Route.CONN_CRM_CG);

        return map;
    }

    /**
     * 根据USER_ID查询用户VIP信息,默认路由
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData qryVipInfoByUserId(String userId) throws Exception
    {

        IData map = qryVipInfoByUserId(userId, null);

        return map;
    }

    /**
     * 根据USER_ID查询用户VIP信息,指定路由
     *
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryVipInfoByUserId(String userId, String routeId) throws Exception
    {
        // 得到缓存key
        String cacheKey = CacheKey.getUcaKeyVipByUserId(userId, routeId);

        // 从缓存中取数据
        Object cacheObj = null;
        boolean cacheOK = true;

        try
        {
            cacheObj = SharedCache.get(cacheKey);
        }
        catch (Exception e)
        {
            cacheOK = false;
            cacheObj = null;

            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据失败[").append(cacheKey).append("]");

            logger.error(sb);
        }

        // 缓存无，从数据库中查
        if (cacheObj == null)
        {
            IData map = qryVipInfoByUserIdFromDB(userId, routeId);

            // 将数据放入缓存
            if (cacheOK == true)
            {
                SharedCache.set(cacheKey, map, 600);
            }

            return map;
        }
        else
        {
            // 往后顺延
            SharedCache.touch(cacheKey, 600);
        }

        // 缓存有，直接返回缓存对象
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据成功[").append(cacheKey).append("]");
            logger.debug(sb);
        }

        IData map = (IData) cacheObj;

        return map;
    }

    private static IData qryVipInfoByUserIdFromDB(String userId, String routeId) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT VIP_ID, TO_CHAR(CUST_ID) CUST_ID, TO_CHAR(USECUST_ID) USECUST_ID, ");
        sql.append("TO_CHAR(USER_ID) USER_ID, SERIAL_NUMBER, NET_TYPE_CODE, CUST_NAME, ");
        sql.append("USECUST_NAME, USEPSPT_TYPE_CODE, USEPSPT_ID, ");
        sql.append("TO_CHAR(USEPSPT_END_DATE, 'yyyy-mm-dd hh24:mi:ss') USEPSPT_END_DATE, ");
        sql.append("USEPSPT_ADDR, USEPHONE, USEPOST_ADDR, EPARCHY_CODE, CITY_CODE, ");
        sql.append("VIP_TYPE_CODE, VIP_CLASS_ID, LAST_VIP_TYPE_CODE, LAST_VIP_CLASS_ID, ");
        sql.append("TO_CHAR(CLASS_CHANGE_DATE, 'yyyy-mm-dd hh24:mi:ss') CLASS_CHANGE_DATE, ");
        sql.append("VIP_TYPE_CODE_B, VIP_CLASS_ID_B, LAST_VIP_TYPE_CODE_B, ");
        sql.append("LAST_VIP_CLASS_ID_B, ");
        sql.append("TO_CHAR(CLASS_CHANGE_DATE_B, 'yyyy-mm-dd hh24:mi:ss') CLASS_CHANGE_DATE_B, ");
        sql.append("VIP_CARD_NO, VIP_CARD_TYPE, VIP_CARD_PASSWD, VIP_CARD_STATE, ");
        sql.append("VIP_CARD_SPELL, VIP_CARD_INFO, VIP_CARD_SEND_TYPE, ");
        sql.append("TO_CHAR(VIP_CARD_SEND_DATE, 'yyyy-mm-dd hh24:mi:ss') VIP_CARD_SEND_DATE, ");
        sql.append("VIP_CARD_POST_ADDR, ");
        sql.append("TO_CHAR(VIP_CARD_START_DATE, 'yyyy-mm-dd hh24:mi:ss') VIP_CARD_START_DATE, ");
        sql.append("TO_CHAR(VIP_CARD_END_DATE, 'yyyy-mm-dd hh24:mi:ss') VIP_CARD_END_DATE, ");
        sql.append("TO_CHAR(VIP_CARD_CHANGE_DATE, 'yyyy-mm-dd hh24:mi:ss') VIP_CARD_CHANGE_DATE, ");
        sql.append("VIP_CARD_CHANGE_REASON, CUST_MANAGER_ID, CUST_MANAGER_ID_B, ");
        sql.append("CUST_MANAGER_APPR, ");
        sql.append("TO_CHAR(ASSIGN_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSIGN_DATE, ");
        sql.append("ASSIGN_STAFF_ID, JOIN_TYPE, ");
        sql.append("TO_CHAR(JOIN_DATE, 'yyyy-mm-dd hh24:mi:ss') JOIN_DATE, ");
        sql.append("TO_CHAR(JOIN_DATE_B, 'yyyy-mm-dd hh24:mi:ss') JOIN_DATE_B, JOIN_STAFF_ID, ");
        sql.append("JOIN_DEPART_ID, ");
        sql.append("TO_CHAR(IDENTITY_CHK_DATE, 'yyyy-mm-dd hh24:mi:ss') IDENTITY_CHK_DATE, ");
        sql.append("TO_CHAR(IDENTITY_CHK_SCORE, 'yyyy-mm-dd hh24:mi:ss') IDENTITY_CHK_SCORE, ");
        sql.append("IDENTITY_PRI, ");
        sql.append("TO_CHAR(IDENTITY_EFF_DATE, 'yyyy-mm-dd hh24:mi:ss') IDENTITY_EFF_DATE, ");
        sql.append("TO_CHAR(IDENTITY_EXP_DATE, 'yyyy-mm-dd hh24:mi:ss') IDENTITY_EXP_DATE, ");
        sql.append("GROUP_ID, GROUP_CUST_NAME, TO_CHAR(MONTH_FEE) MONTH_FEE, HVALUE_TAG, ");
        sql.append("CLUB_ID, VISIT_NUM, SVC_NUM, SVC_NUM_B, INNET_NUM, SVC_MODE_CODE, ");
        sql.append("SVC_CYCLE_CODE, TO_CHAR(BIRTHDAY, 'yyyy-mm-dd') BIRTHDAY, ");
        sql.append("BIRTHDAY_LUNAR, BIRTHDAY_FLAG, APPROVAL_FLAG, APPROVAL_STAFF_ID, ");
        sql.append("TO_CHAR(APPROVAL_TIME, 'yyyy-mm-dd hh24:mi:ss') APPROVAL_TIME, ");
        sql.append("APPROVAL_DESC, GROUP_BRAND_CODE, BRAND_CODE, PRODUCT_ID, USER_TYPE_CODE, ");
        sql.append("USER_STATE_CODESET, TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, ");
        sql.append("TO_CHAR(FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME, ");
        sql.append("TO_CHAR(LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME, ");
        sql.append("TO_CHAR(SCORE_VALUE) SCORE_VALUE, CREDIT_CLASS, ");
        sql.append("TO_CHAR(CREDIT_VALUE) CREDIT_VALUE, CTAG_SET, CHECK_NO, ");
        sql.append("TO_CHAR(TRADE_ID) TRADE_ID, CANCEL_TAG, REMOVE_TAG, ");
        sql.append("TO_CHAR(REMOVE_DATE, 'yyyy-mm-dd hh24:mi:ss') REMOVE_DATE, ");
        sql.append("REMOVE_STAFF_ID, REMOVE_REASON, ");
        sql.append("TO_CHAR(SYNC_TIME, 'yyyy-mm-dd hh24:mi:ss') SYNC_TIME, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK ");
        sql.append("FROM TF_F_CUST_VIP ");
        sql.append("WHERE USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("AND REMOVE_TAG = '0' ");

        IDataset ids = Dao.qryBySql(sql, params, routeId);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        IData vipInfo = ids.getData(0);

        return vipInfo;
    }

    /**
     * 根据SN查询有效用户VIP信息
     *
     * @param sn
     * @return
     * @throws Exception
     */
    public static IData qryVipInfoBySn(String sn) throws Exception
    {
        IData map = qryVipInfoBySn(sn, null);

        return map;
    }

    /**
     * 根据SN查询有效用户VIP信息,指定路由
     *
     * @param sn
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryVipInfoBySn(String sn, String routeId) throws Exception
    {
        // 得到缓存key
        String cacheKey = CacheKey.getUcaKeyVipBySn(sn, routeId);

        // 从缓存中取数据
        Object cacheObj = null;
        boolean cacheOK = true;

        try
        {
            cacheObj = SharedCache.get(cacheKey);
        }
        catch (Exception e)
        {
            cacheOK = false;
            cacheObj = null;

            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据失败[").append(cacheKey).append("]");

            logger.error(sb);
        }

        // 缓存无，从数据库中查
        if (cacheObj == null)
        {
            IData userInfo = qryUserInfoBySn(sn, routeId);

            if (IDataUtil.isEmpty(userInfo))
            {
                return null;
            }

            String userId = userInfo.getString("USER_ID");
            IData map = qryVipInfoByUserId(userId, routeId);

            // 将数据放入缓存
            if (cacheOK == true)
            {
                SharedCache.set(cacheKey, map, 600);
            }

            return map;
        }
        else
        {
            // 往后顺延
            SharedCache.touch(cacheKey, 600);
        }

        // 缓存有，直接返回缓存对象
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder("从SharedCache获取UCA缓存数据成功[").append(cacheKey).append("]");
            logger.debug(sb);
        }

        IData map = (IData) cacheObj;

        return map;
    }

    /**
     * 根据SN查询有效用户VIP信息，查询所有库
     *
     * @param sn
     * @return
     * @throws Exception
     */
    public static IData qryVipInfoBySnAllCrm(String sn) throws Exception
    {
        IData map = null;

        for (String routeId : Route.getAllCrmDb())
        {
            map = qryVipInfoBySn(sn, routeId);

            if (IDataUtil.isNotEmpty(map))
            {
                break;
            }
        }

        return map;
    }

    public static String qryEcOrderId() throws Exception {
        IData param = new DataMap();

        StringBuilder stringBuffer = new StringBuilder(1000);

        stringBuffer.append("select TF_B_TRADE_ACCOUNT_EC_TID.nextval ");
        stringBuffer.append("from dual");
        IDataset objects = Dao.qryBySql(stringBuffer, param);
        IData data = objects.getData(0);

        return data.getString("NEXTVAL");
    }
	 /**
     * 在出库界面与入库界面都进行终端串号与和商务TV产品成员关系的校验。
     * @param sn
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryUserInfoBySerialnumber(String sn, String routeId) throws Exception
    {
        
         IData map = qryUserInfoBySerialnumberDb(sn, routeId);
        
         return map;
         
    }
	 /**
     * 
     * @param sn
     * @param routeId
     * @return
     * @throws Exception
     */
    
    private static IData qryUserInfoBySerialnumberDb(String sn, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        StringBuilder sql = new StringBuilder(3000);
        sql.append("SELECT ");
        sql.append("* ");
        sql.append("FROM TF_F_USER U ");
        sql.append("WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.append("AND REMOVE_TAG = '0' ");
        sql.append("AND U.USER_ID IN (SELECT B.USER_ID FROM  TF_F_USER_PRODUCT B WHERE B.PRODUCT_ID='380700' AND B.END_DATE>SYSDATE) ");

        IDataset ids = Dao.qryBySql(sql, param, routeId);
        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }
        return ids.getData(0);
    }
}
