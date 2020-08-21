
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SuperUserInfoDAO
{

    public IData getAccountByAid(IData data, Pagination pagination) throws Exception
    {
        if (("".equals(data.getString("ACCT_ID", ""))) && ("".equals(data.getString("PARTITION_ID", ""))))
        {
            return null;
        }
        else
        {
            SQLParser parser = new SQLParser(data);
            parser.addSQL("SELECT PARTITION_ID,ACCT_ID,CUST_ID,PAY_NAME,PAY_MODE_CODE,ACCT_DIFF_CODE,ACCT_PASSWD,ACCT_TAG," + "NET_TYPE_CODE,EPARCHY_CODE,CITY_CODE,BANK_CODE,BANK_ACCT_NO,SCORE_VALUE,CREDIT_CLASS_ID,"
                    + "BASIC_CREDIT_VALUE,CREDIT_VALUE,DEBUTY_USER_ID,DEBUTY_CODE,CONTRACT_NO,DEPOSIT_PRIOR_RULE_ID," + "ITEM_PRIOR_RULE_ID,OPEN_DATE,REMOVE_TAG,REMOVE_DATE,UPDATE_STAFF_ID,UPDATE_DEPART_ID,UPDATE_TIME,"
                    + "REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7," + "RSRV_STR8,RSRV_STR9,RSRV_STR10");
            parser.addSQL(" FROM TF_F_ACCOUNT ");
            parser.addSQL(" WHERE ACCT_ID = :ACCT_ID ");
            parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
            parser.addSQL(" and rownum <= 1 ");
            IDataset outlist = Dao.qryByParse(parser, pagination);
            if (outlist.size() == 0)
                return null;
            else
                return (IData) outlist.get(0);
        }
    }

    /**
     * 获取支付平台业务信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getBankbandInfos(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        if ("".equals(user_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);

        parser.addSQL("SELECT to_char(user_band_id) user_band_id, ");
        parser.addSQL("       to_char(user_id) user_id, ");
        parser.addSQL("       bank_card_no, ");
        parser.addSQL("       update_staff_id, ");
        parser.addSQL("       update_depart_id, ");
        parser.addSQL("       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time, ");
        parser.addSQL("       to_char(start_time, 'yyyy-mm-dd hh24:mi:ss') start_time, ");
        parser.addSQL("       to_char(end_time, 'yyyy-mm-dd hh24:mi:ss') end_time, ");
        parser.addSQL("       reck_tag ");
        parser.addSQL("  FROM tf_f_bankband ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("   AND user_id = :USER_ID ");
        parser.addSQL("   AND sysdate BETWEEN start_time AND end_time ");

        IDataset outlist = Dao.qryByParse(parser, pagination);
        return outlist;
    }

    public IData getCustGroup(IData data, Pagination pagination) throws Exception
    {
        if ("".equals(data.getString("CUST_ID", "")))
        {
            return null;
        }
        else
        {
            SQLParser parser = new SQLParser(data);
            parser.addSQL("SELECT CUST_ID,GROUP_ID,CUST_NAME,GROUP_TYPE,GROUP_ROLE,CLASS_ID,CLASS_ID2," + "LAST_CLASS_ID,CLASS_CHANGE_DATE,CUST_CLASS_TYPE,GROUP_ATTR,GROUP_STATUS,GROUP_ADDR,"
                    + "GROUP_SOURCE,PROVINCE_CODE,EPARCHY_CODE,CITY_CODE,CITY_CODE_U,SUPER_GROUP_ID,SUPER_GROUP_NAME," + "PNATIONAL_GROUP_ID,PNATIONAL_GROUP_NAME,MP_GROUP_CUST_CODE,UNIFY_PAY_CODE,ORG_STRUCT_CODE,"
                    + "CUST_MANAGER_ID,CUST_MANAGER_APPR,ASSIGN_DATE,ASSIGN_STAFF_ID,ORG_TYPE_A,ORG_TYPE_B,ORG_TYPE_C," + "CALLING_TYPE_CODE,SUB_CALLING_TYPE_CODE,CALLING_AREA_CODE,CALL_TYPE,ACCEPT_CHANNEL,AGREEMENT,"
                    + "BUSI_TYPE,GROUP_CONTACT_PHONE,ENTERPRISE_TYPE_CODE,ENTERPRISE_SIZE_CODE,ENTERPRISE_SCOPE," + "JURISTIC_TYPE_CODE,JURISTIC_CUST_ID,JURISTIC_NAME,BUSI_LICENCE_TYPE,BUSI_LICENCE_NO,"
                    + "BUSI_LICENCE_VALID_DATE,GROUP_MEMO,BANK_ACCT,BANK_NAME,REG_MONEY,REG_DATE,CUST_AIM,SCOPE,MAIN_BUSI," + "MAIN_TRADE,EMP_LSAVE,LATENCY_FEE_SUM,YEAR_GAIN,TURNOVER,CONSUME,COMM_BUDGET,GTEL_BUDGET,LTEL_BUDGET,"
                    + "GROUP_ADVERSARY,VPMN_GROUP_ID,VPMN_NUM,USER_NUM,EMP_NUM_LOCAL,EMP_NUM_CHINA,EMP_NUM_ALL,TELECOM_NUM_GH," + "TELECOM_NUM_XLT,MOBILE_NUM_CHINAGO,MOBILE_NUM_GLOBAL,MOBILE_NUM_MZONE,MOBILE_NUM_LOCAL,UNICOM_NUM_G,"
                    + "UNICOM_NUM_C,UNICOM_NUM_GC,PRODUCT_NUM_LOCAL,PRODUCT_NUM_OTHER,PRODUCT_NUM_USE,EMPLOYEE_ARPU,NETRENT_PAYOUT," + "MOBILE_PAYOUT,UNICOM_PAYOUT,TELECOM_PAYOUT_XLT,GROUP_PAY_MODE,PAYFOR_WAY_CODE,WRITEFEE_COUNT,WRITEFEE_SUM,"
                    + "USER_NUM_FULLFREE,USER_NUM_WRITEOFF,BOSS_FEE_SUM,DOYEN_STAFF_ID,NEWTRADE_COMMENT,LIKE_MOBILE_TRADE," + "LIKE_DISCNT_MODE,FINANCE_EARNING,EARNING_ORDER,CALLING_POLICY_FORCE,SUBCLASS_ID,WEBSITE,FAX_NBR,EMAIL,"
                    + "POST_CODE,GROUP_VALID_SCORE,GROUP_SUM_SCORE,GROUP_MGR_SN,GROUP_MGR_USER_ID,GROUP_MGR_CUST_NAME," + "BASE_ACCESS_NO,BASE_ACCESS_NO_KIND,CUST_SERV_NBR,EC_CODE,IF_SHORT_PIN,AUDIT_STATE,AUDIT_DATE,"
                    + "AUDIT_STAFF_ID,AUDIT_NOTE,IN_DATE,IN_STAFF_ID,IN_DEPART_ID,OUT_DATE,REMOVE_TAG,REMOVE_FLAG,REMOVE_METHOD," + "REMOVE_REASON_CODE,REMOVE_CHANGE,REMOVE_DATE,REMOVE_STAFF_ID,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,"
                    + "REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7," + "RSRV_STR8,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3");
            parser.addSQL(" FROM TF_F_CUST_GROUP ");
            parser.addSQL(" WHERE REMOVE_TAG='0' ");
            parser.addSQL(" AND CUST_ID = :CUST_ID ");
            parser.addSQL(" and rownum <= 1 ");
            IDataset outlist = Dao.qryByParse(parser, pagination);
            if (outlist.size() == 0)
                return null;
            else
                return (IData) outlist.get(0);
        }
    }

    public IDataset getCustGroupMemberBySN(IData data, Pagination pagination) throws Exception
    {
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            return new DatasetList();
        }
        else
        {
            SQLParser parser = new SQLParser(data);
            parser.addSQL("SELECT PARTITION_ID,USER_ID,CUST_ID,GROUP_ID,GROUP_CUST_NAME,GROUP_TYPE,MEMBER_BELONG," + "MEMBER_KIND,MEMBER_RELA,MEMBER_CUST_ID,USECUST_ID,SERIAL_NUMBER,NET_TYPE_CODE,CUST_NAME,USECUST_NAME,"
                    + "USEPSPT_TYPE_CODE,USEPSPT_ID,USEPSPT_END_DATE,USEPSPT_ADDR,USEPHONE,USEPOST_ADDR,EPARCHY_CODE,CITY_CODE," + "STRU_ID,VPMN_GROUP_ID,VIP_ID,VIP_TYPE_CODE,VIP_CLASS_ID,VIP_TYPE_CODE_B,VIP_CLASS_ID_B,ASSIGN_DATE,"
                    + "ASSIGN_STAFF_ID,JOIN_TYPE,JOIN_DATE,JOIN_STAFF_ID,JOIN_DEPART_ID,CONTRACT_NO,MONTH_FEE,FEE_PAYMODE," + "DEPART,DUTY,HVALUE_TAG,CLUB_ID,BIRTHDAY,BIRTHDAY_LUNAR,BIRTHDAY_FLAG,CUST_MANAGER_ID,CUST_MANAGER_APPR,"
                    + "LIKE_MOBILE_BUSI,LIKE_DISCNT_TYPE,LIKE_ACT,SVC_MODE_CODE,CONTACT_ORDER,GROUP_BRAND_CODE,BRAND_CODE," + "PRODUCT_ID,USER_TYPE_CODE,USER_STATE_CODESET,OPEN_DATE,FIRST_CALL_TIME,LAST_STOP_TIME,SCORE_VALUE,"
                    + "CREDIT_CLASS,CREDIT_VALUE,CTAG_SET,CHECK_NO,TRADE_ID,CANCEL_TAG,REMOVE_TAG,REMOVE_DATE,REMOVE_STAFF_ID," + "REMOVE_REASON,SYNC_TIME,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,"
                    + "RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8," + "RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_DATE4,RSRV_DATE5,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_TAG4,RSRV_TAG5");
            parser.addSQL(" FROM TF_F_CUST_GROUPMEMBER ");
            parser.addSQL(" WHERE REMOVE_TAG='0' ");
            parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
            return Dao.qryByParse(parser, pagination);
        }
    }

    public IDataset getCustomer(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        if (("".equals(data.getString("CUST_ID", ""))) && ("".equals(data.getString("PARTITION_ID", ""))))
        {
            return new DatasetList();
        }
        else
        {
            parser.addSQL("SELECT PARTITION_ID,CUST_ID,CUST_NAME,SIMPLE_SPELL,CUST_TYPE,CUST_KIND," + "CUST_STATE,PSPT_TYPE_CODE,PSPT_ID,OPEN_LIMIT,EPARCHY_CODE,CITY_CODE,IS_REAL_NAME,"
                    + "CITY_CODE_A,CUST_PASSWD,SCORE_VALUE,CREDIT_CLASS,BASIC_CREDIT_VALUE,CREDIT_VALUE," + "DEVELOP_STAFF_ID,DEVELOP_DEPART_ID,IN_DATE,IN_STAFF_ID,IN_DEPART_ID,REMOVE_TAG,"
                    + "REMOVE_DATE,REMOVE_STAFF_ID,REMOVE_CHANGE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID," + "REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,"
                    + "RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10,RSRV_DATE1," + "RSRV_DATE2,RSRV_DATE3,RSRV_DATE4,RSRV_DATE5,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_TAG4," + "RSRV_TAG5");
            parser.addSQL(" FROM TF_F_CUSTOMER ");
            parser.addSQL(" WHERE 1=1 ");
            parser.addSQL(" AND cust_id = :CUST_ID ");
            parser.addSQL(" AND partition_id = :PARTITION_ID ");
            return Dao.qryByParse(parser, pagination);
        }
    }

    public IDataset getCustVip(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        if ("".equals(user_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT a.VIP_ID,a.CUST_ID,a.USECUST_ID,a.USER_ID,a.SERIAL_NUMBER,a.NET_TYPE_CODE,a.CUST_NAME,  " + "a.USECUST_NAME,a.USEPSPT_TYPE_CODE,a.USEPSPT_ID,a.USEPSPT_END_DATE,a.USEPSPT_ADDR,a.USEPHONE,  "
                + "a.USEPOST_ADDR,a.EPARCHY_CODE,a.CITY_CODE,a.VIP_TYPE_CODE,a.VIP_CLASS_ID,a.LAST_VIP_TYPE_CODE, " + "a.LAST_VIP_CLASS_ID,a.CLASS_CHANGE_DATE,a.VIP_TYPE_CODE_B,a.VIP_CLASS_ID_B,                    "
                + "a.LAST_VIP_TYPE_CODE_B,a.LAST_VIP_CLASS_ID_B,a.CLASS_CHANGE_DATE_B,a.VIP_CARD_NO,              " + "a.VIP_CARD_TYPE,a.VIP_CARD_PASSWD,a.VIP_CARD_STATE,a.VIP_CARD_SPELL,a.VIP_CARD_INFO,           "
                + "a.VIP_CARD_SEND_TYPE,a.VIP_CARD_SEND_DATE,a.VIP_CARD_POST_ADDR,a.VIP_CARD_START_DATE,          " + "a.VIP_CARD_END_DATE,a.VIP_CARD_CHANGE_DATE,a.VIP_CARD_CHANGE_REASON,a.CUST_MANAGER_ID,         "
                + "a.CUST_MANAGER_ID_B,a.CUST_MANAGER_APPR,                                                       " + "a.ASSIGN_DATE,a.ASSIGN_STAFF_ID,a.JOIN_TYPE,a.JOIN_DATE,a.JOIN_DATE_B,a.JOIN_STAFF_ID,         "
                + "a.JOIN_DEPART_ID,a.IDENTITY_CHK_DATE,a.IDENTITY_CHK_SCORE,a.IDENTITY_PRI,a.IDENTITY_EFF_DATE,  " + "a.IDENTITY_EXP_DATE,a.GROUP_ID,a.GROUP_CUST_NAME,a.MONTH_FEE,a.HVALUE_TAG,a.CLUB_ID,           "
                + "a.VISIT_NUM,a.SVC_NUM,a.SVC_NUM_B,a.INNET_NUM,a.SVC_MODE_CODE,a.SVC_CYCLE_CODE,                " + "a.BIRTHDAY,a.BIRTHDAY_LUNAR,a.BIRTHDAY_FLAG,a.APPROVAL_FLAG,a.APPROVAL_STAFF_ID,               "
                + "a.APPROVAL_TIME,a.APPROVAL_DESC,a.GROUP_BRAND_CODE,a.BRAND_CODE,a.PRODUCT_ID,                  " + "a.USER_TYPE_CODE,a.USER_STATE_CODESET,a.OPEN_DATE,a.FIRST_CALL_TIME,                           "
                + "a.LAST_STOP_TIME,a.SCORE_VALUE,a.CREDIT_CLASS,a.CREDIT_VALUE,a.CTAG_SET,                       " + "a.CHECK_NO,a.TRADE_ID,a.CANCEL_TAG,a.REMOVE_TAG,a.REMOVE_DATE,a.REMOVE_STAFF_ID,               "
                + "a.REMOVE_REASON,a.SYNC_TIME,a.UPDATE_TIME,a.UPDATE_STAFF_ID,a.UPDATE_DEPART_ID,                " + "a.REMARK,a.RSRV_NUM1,a.RSRV_NUM2,a.RSRV_NUM3,a.RSRV_NUM4,a.RSRV_NUM5,a.RSRV_STR1,              "
                + "a.RSRV_STR2,a.RSRV_STR3,a.RSRV_STR4,a.RSRV_STR5,a.RSRV_STR6,a.RSRV_STR7,a.RSRV_STR8,           " + "a.RSRV_DATE1,a.RSRV_DATE2,a.RSRV_DATE3,a.RSRV_DATE4,a.RSRV_DATE5,a.RSRV_TAG1,                  "
                + "a.RSRV_TAG2,a.RSRV_TAG3,a.RSRV_TAG4,a.RSRV_TAG5,b.depart_id cust_depart_id                     ");
        parser.addSQL(" FROM tf_f_cust_vip a left join TF_F_CUST_MANAGER_STAFF b on a.cust_manager_id = b.cust_manager_id ");
        parser.addSQL(" WHERE a.REMOVE_TAG='0' ");
        parser.addSQL(" AND a.USER_ID = :USER_ID ");
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 获取个人代扣账户信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getDeductInfos(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        if ("".equals(user_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);

        parser.addSQL("SELECT to_char(register_id) register_id, ");
        parser.addSQL("       eparchy_code, ");
        parser.addSQL("       channel_id, ");
        parser.addSQL("       to_char(user_id) user_id, ");
        parser.addSQL("       to_char(acct_id) acct_id, ");
        parser.addSQL("       bank_account_no, ");
        parser.addSQL("       bank_usrp_id, ");
        parser.addSQL("       pay_name, ");
        parser.addSQL("       pay_address, ");
        parser.addSQL("       pay_post_code, ");
        parser.addSQL("       destroy_tag, ");
        parser.addSQL("       inmode_code, ");
        parser.addSQL("       deduct_type_code, ");
        parser.addSQL("       to_char(deduct_money) deduct_money, ");
        parser.addSQL("       to_char(deduct_step) deduct_step, ");
        parser.addSQL("       to_char(open_time, 'yyyy-mm-dd hh24:mi:ss') open_time, ");
        parser.addSQL("       open_city_code, ");
        parser.addSQL("       open_depart_id, ");
        parser.addSQL("       open_staff_id, ");
        parser.addSQL("       to_char(destroy_time, 'yyyy-mm-dd hh24:mi:ss') destroy_time, ");
        parser.addSQL("       destroy_city_code, ");
        parser.addSQL("       destroy_depart_id, ");
        parser.addSQL("       destroy_staff_id, ");
        parser.addSQL("       remark ");
        parser.addSQL("  FROM tf_f_user_deduct ");
        parser.addSQL(" WHERE user_id = :USER_ID ");
        parser.addSQL("  AND destroy_tag = :DESTROY_TAG ");

        IDataset outlist = Dao.qryByParse(parser, pagination);
        return outlist;
    }

    public String getDiscntName(String discnt_code, Pagination pagination) throws Exception
    {
        if ("".equals(discnt_code))
        {
            return null;
        }
        else
        {
            IData in = new DataMap();
            in.put("DISCNT_CODE", discnt_code);
            SQLParser parser = new SQLParser(in);
            parser.addSQL("SELECT DISCNT_CODE,DISCNT_NAME,DISCNT_EXPLAIN,C_DISCNT_CODE," + "B_DISCNT_CODE,A_DISCNT_CODE,OBJ_TYPE_CODE,ENABLE_TAG,DEFINE_MONTHS," + "MONTHS,START_DATE,END_DATE,TAG_SET,EPARCHY_CODE,UPDATE_TIME,UPDATE_STAFF_ID,"
                    + "UPDATE_DEPART_ID,REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5");
            parser.addSQL(" FROM td_b_discnt ");
            parser.addSQL(" WHERE DISCNT_CODE = :DISCNT_CODE ");
            parser.addSQL(" and rownum <= 1 ");
            IDataset outlist = Dao.qryByParse(parser, pagination);
            if (outlist == null)
                return null;
            else
                return ((IData) outlist.get(0)).getString("DISCNT_NAME");
        }
    }

    public IDataset getPayrelationByUid(IData data, Pagination pagination) throws Exception
    {

        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT PARTITION_ID,USER_ID,ACCT_ID,PAYITEM_CODE,ACCT_PRIORITY,USER_PRIORITY,ADDUP_MONTHS,ADDUP_METHOD," + "BIND_TYPE,DEFAULT_TAG,ACT_TAG,LIMIT_TYPE,LIMIT,COMPLEMENT_TAG,INST_ID,START_CYCLE_ID,END_CYCLE_ID,UPDATE_TIME,"
                + "UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7," + "RSRV_STR8,RSRV_STR9,RSRV_STR10");
        parser.addSQL(" FROM tf_a_payrelation ");
        parser.addSQL(" WHERE    default_tag='1' AND act_tag='1' ");

        // modify by caiy
        // 因为表结构变化，所以改动
        parser.addSQL(" AND to_char(SYSDATE,'YYYYMM') between START_CYCLE_ID and END_CYCLE_ID ");
        // parser.addSQL("    AND (select acyc_id from TD_A_ACYCPARA where acyc_start_time <= SYSDATE and acyc_end_time >= SYSDATE) "
        // +
        // "between  start_cycle_id and end_cycle_id ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        IDataset outlist = Dao.qryByParse(parser, pagination);
        return outlist;
    }

    public IDataset getPerson(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        if (("".equals(data.getString("CUST_ID", ""))) && ("".equals(data.getString("PARTITION_ID", ""))))
        {
            return new DatasetList();
        }
        else
        {
            parser.addSQL(" SELECT PARTITION_ID,CUST_ID,PSPT_TYPE_CODE,PSPT_ID,PSPT_END_DATE,");
            parser.addSQL(" PSPT_ADDR,CUST_NAME,SEX,EPARCHY_CODE,CITY_CODE,BIRTHDAY,");
            parser.addSQL(" BIRTHDAY_LUNAR,BIRTHDAY_FLAG,POST_ADDRESS,POST_CODE,POST_PERSON,");
            parser.addSQL(" PHONE,FAX_NBR,EMAIL,HOME_ADDRESS,HOME_PHONE,WORK_NAME,WORK_KIND,");
            parser.addSQL(" WORK_ADDRESS,WORK_PHONE,WORK_POST_CODE,CALLING_TYPE_CODE,");
            parser.addSQL(" SUB_CALLING_TYPE_CODE,WORK_DEPART,JOB,CONTACT,CONTACT_PHONE,");
            parser.addSQL(" CONTACT_TYPE_CODE,CONTACT_FREQ,NATIONALITY_CODE,LOCAL_NATIVE_CODE,");
            parser.addSQL(" POPULATION,LANGUAGE_CODE,FOLK_CODE,RELIGION_CODE,JOB_TYPE_CODE,");
            parser.addSQL(" REVENUE_LEVEL_CODE,EDUCATE_DEGREE_CODE,EDUCATE_GRADE_CODE,");
            parser.addSQL(" GRADUATE_SCHOOL,SPECIALITY,CHARACTER_TYPE_CODE,HEALTH_STATE_CODE,");
            parser.addSQL(" MARRIAGE,WEBUSER_ID,WEB_PASSWD,COMMUNITY_ID,REMOVE_TAG,REMOVE_DATE,");
            parser.addSQL(" REMOVE_STAFF_ID,REMOVE_CHANGE,UPDATE_TIME,UPDATE_STAFF_ID,");
            parser.addSQL(" UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_STR1,");
            parser.addSQL(" RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,");
            parser.addSQL(" RSRV_STR8,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3");
            parser.addSQL(" FROM TF_F_CUST_PERSON ");
            parser.addSQL(" WHERE 1=1 ");
            parser.addSQL(" AND cust_id = :CUST_ID ");
            parser.addSQL(" AND partition_id = :PARTITION_ID ");
            return Dao.qryByParse(parser, pagination);
        }
    }

    /**
     * 查询手机开通信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getPhoneOpenInfo(IData data, Pagination pagination) throws Exception
    {

        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT a.partition_id partition_id, ");
        parser.addSQL("       to_char(a.user_id) user_id,  ");
        parser.addSQL("      a.rsrv_value_code rsrv_value_code,  ");
        parser.addSQL("       a.rsrv_value rsrv_value,  ");
        parser.addSQL("       a.rsrv_str1 rsrv_str1,  ");
        parser.addSQL("       a.rsrv_str2 rsrv_str2, ");
        parser.addSQL("       a.rsrv_str3 rsrv_str3, ");
        parser.addSQL("       a.rsrv_str4 rsrv_str4, ");
        parser.addSQL("       a.rsrv_str5 rsrv_str5, ");
        parser.addSQL("       a.rsrv_str6 rsrv_str6, ");
        parser.addSQL("       a.rsrv_str7 rsrv_str7, ");
        // parser.addSQL("       c.chnl_name || '[' || a.rsrv_str1 || ']' rsrv_str8, ");
        parser.addSQL("       a.rsrv_str9 rsrv_str9, ");
        parser.addSQL("       a.rsrv_str10 rsrv_str10, ");
        parser.addSQL("       to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
        parser.addSQL("       to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date ");
        parser.addSQL("  FROM tf_f_user_other a ");
        parser.addSQL(" WHERE a.user_id = :USER_ID ");
        parser.addSQL("   AND a.partition_id = :PARTITION_ID ");
        parser.addSQL("   AND a.rsrv_value_code = :RSRV_VALUE_CODE ");
        // parser.addSQL("   AND c.chnl_code = a.rsrv_str1  ");
        parser.addSQL("   AND sysdate BETWEEN a.start_date AND a.end_date ");
        IDataset outlist = Dao.qryByParse(parser, pagination);
        return outlist;
    }

    public IDataset getRelationUUByUida(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID_A", "");
        if ("".equals(user_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT PARTITION_ID,USER_ID_A,SERIAL_NUMBER_A,USER_ID_B,SERIAL_NUMBER_B," + "RELATION_TYPE_CODE,ROLE_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,ORDERNO,SHORT_CODE,"
                + "INST_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK," + "RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,"
                + "RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3");
        parser.addSQL(" FROM tf_f_relation_uu ");
        parser.addSQL(" WHERE end_date>start_date ");
        parser.addSQL(" AND end_date>sysdate ");
        parser.addSQL(" AND USER_ID_A = :USER_ID_A ");
        parser.addSQL(" AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
        return Dao.qryByParse(parser, pagination);
    }

    public IDataset getRelationUUByUidb(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID_B", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT PARTITION_ID,USER_ID_A,SERIAL_NUMBER_A,USER_ID_B,SERIAL_NUMBER_B," + "RELATION_TYPE_CODE,ROLE_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,ORDERNO,SHORT_CODE,"
                + "INST_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK," + "RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,"
                + "RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3");
        parser.addSQL(" FROM tf_f_relation_uu ");
        parser.addSQL(" WHERE sysdate < end_date ");
        parser.addSQL(" AND USER_ID_B = :USER_ID_B ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        parser.addSQL(" AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
        return Dao.qryByParse(parser, pagination);
    }

    public String getServiceName(String service_id, Pagination pagination) throws Exception
    {
        if ("".equals(service_id))
        {
            return null;
        }
        else
        {
            IData in = new DataMap();
            in.put("SERVICE_ID", service_id);

            SQLParser parser = new SQLParser(in);
            parser.addSQL("SELECT SERVICE_ID,SERVICE_NAME,NET_TYPE_CODE,INTF_MODE," + "PARENT_TYPE_CODE,SERVICE_MODE,SERVICE_BRAND_CODE,SERVICE_LEVEL," + "SERVICE_STATE,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,"
                    + "UPDATE_DEPART_ID,REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4," + "RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3");
            parser.addSQL(" FROM td_b_service ");
            parser.addSQL(" WHERE service_id = :SERVICE_ID ");
            parser.addSQL(" and rownum <= 1 ");
            IDataset outlist = Dao.qryByParse(parser, pagination);
            if (outlist == null)
                return null;
            else
                return ((IData) outlist.get(0)).getString("SERVICE_NAME");
        }
    }

    // 获取SIM卡类型
    public IDataset getSimCardType(IData qry_param) throws Exception
    {
        SQLParser parser = new SQLParser(qry_param);
        parser.addSQL(" Select f_res_getcodename('res_kind_code','1','ZZZZ', :SIM_TYPE_CODE) SIM_TYPE From dual ");
        return Dao.qryByParse(parser);
    }

    public IDataset getSimCardUse(IData data, Pagination pagination) throws Exception
    {
        String imsi = data.getString("IMSI", "");
        if ("".equals(imsi))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT IMSI,DOUBLE_MODE_IMSI,NET_TYPE_CODE,SIM_CARD_NO,EMPTY_CARD_ID,FACTORY_CODE," + "IMEI,ESN,SIM_TYPE_CODE,CAPACITY_TYPE_CODE,CARD_KIND_CODE,USE_TAG,PIN,PUK,PIN2,PUK2,KI,OPC,"
                + "MSIN,UIM_ID,A_KEY,MOFFICE_ID,SIM_STATE_CODE,KI_STATE,KI_DEAL_TIME,BATCH_CODE,SERIAL_NUMBER," + "VALUE_CARD_NO,EPARCHY_CODE,CITY_CODE,STOCK_ID_O,STOCK_ID,DEPOT_CODE,STOCK_ID_P,STAFF_ID,"
                + "STOCK_LEVEL,AGENT_ID,ASSIGN_TAG,ASSIGN_TIME,ASSIGN_STAFF_ID,DATE_TIME_P,TIME_IN,STAFF_ID_IN," + "PRECODE_TAG,SERIAL_NUMBER_CODE,STAFF_ID_CODE,TIME_CODE,PREOPEN_TAG,DATE_TIME_OPEN,STAFF_ID_OPEN,"
                + "DESTORY_TIME,DOUBLE_TAG,LOG_ID,OPER_TIME,OPER_STAFF_ID,OPEN_TIME,OPEN_MODE,USER_ID,FEE_TAG," + "PACKAGE_FLAG,PACKAGE_TIME,PACKAGE_CODE,BOX_TIME,BOX_CODE,CONTRACT_ID,COST,SALE_MONEY,ADVANCE_PAY,"
                + "SALE_LOG_ID,REMARK,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_DATE1," + "RSRV_DATE2,RSRV_DATE3,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7");
        parser.addSQL("   FROM tf_r_simcard_use ");
        parser.addSQL(" WHERE imsi=:IMSI");
        return Dao.qryByParse(parser, pagination);
    }

    public IData getTagInfo(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT eparchy_code,tag_code,tag_name,subsys_code,tag_info,tag_char,");
        parser.addSQL("tag_number,to_char(tag_date, 'yyyy-mm-dd hh24:mi:ss') tag_date,");
        parser.addSQL("tag_sequid,use_tag,to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,");
        parser.addSQL("to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,");
        parser.addSQL("remark,to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,");
        parser.addSQL("update_staff_id,update_depart_id ");
        parser.addSQL(" FROM td_s_tag ");
        parser.addSQL(" WHERE start_date +0 < sysdate AND end_date+0 >= sysdate ");
        parser.addSQL(" AND eparchy_code = :EPARCHY_CODE ");
        parser.addSQL(" AND tag_code = :TAG_CODE ");
        parser.addSQL(" AND subsys_code = :SUBSYS_CODE ");
        parser.addSQL(" AND use_tag = :USE_TAG ");
        parser.addSQL(" and rownum <= 1 ");
        IDataset outlist = Dao.qryByParse(parser, pagination);
        if (outlist.size() == 0)
            return null;
        else
            return (IData) outlist.get(0);
    }

    /**
     * 获取下月产品信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getTradeByUidTradeTypeCode(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        if ("".equals(user_id))
        {
            return new DatasetList();
        }

        SQLParser parser = new SQLParser(data);

        parser.addSQL("SELECT TRADE_ID,ACCEPT_MONTH,BATCH_ID,ORDER_ID,PROD_ORDER_ID,BPM_ID,CAMPN_ID," + "TRADE_TYPE_CODE,PRIORITY,SUBSCRIBE_TYPE,SUBSCRIBE_STATE,NEXT_DEAL_TAG,IN_MODE_CODE,"
                + "CUST_ID,CUST_NAME,USER_ID,ACCT_ID,SERIAL_NUMBER,NET_TYPE_CODE,EPARCHY_CODE,CITY_CODE," + "PRODUCT_ID,BRAND_CODE,ACCEPT_DATE,TRADE_STAFF_ID,TRADE_DEPART_ID,TRADE_CITY_CODE,"
                + "TRADE_EPARCHY_CODE,TERM_IP,OPER_FEE,FOREGIFT,ADVANCE_PAY,INVOICE_NO,FEE_STATE," + "FEE_TIME,FEE_STAFF_ID,PROCESS_TAG_SET,OLCOM_TAG,FINISH_DATE,EXEC_TIME,EXEC_ACTION,"
                + "EXEC_RESULT,EXEC_DESC,CANCEL_TAG,CANCEL_DATE,CANCEL_STAFF_ID,CANCEL_DEPART_ID," + "CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,"
                + "REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7," + "RSRV_STR8,RSRV_STR9,RSRV_STR10");
        parser.addSQL(" FROM tf_b_trade ");
        parser.addSQL("WHERE (trade_id,accept_month) = ");
        parser.addSQL("     (SELECT MAX(trade_id),to_number(substr(to_char(max(trade_id)),5,2)) ");
        parser.addSQL("        FROM tf_b_trade ");
        parser.addSQL("       WHERE trade_type_code=:TRADE_TYPE_CODE");
        parser.addSQL("         AND user_id=:USER_ID )		");
        IDataset outlist = Dao.qryByParse(parser, pagination);
        return outlist;
    }

    public IDataset getUserBrandChange(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT PARTITION_ID,USER_ID,TRADE_TYPE_CODE,RELATION_TRADE_ID," + "BRAND_CODE,BRAND_NO,VIP_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID," + "UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,"
                + "RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2," + "RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3");
        parser.addSQL(" FROM TF_F_USER_BRANDCHANGE ");
        parser.addSQL(" WHERE SYSDATE BETWEEN start_date AND end_date ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        return Dao.qryByParse(parser, pagination);
    }

    public IDataset getUserDiscnt(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT PARTITION_ID,USER_ID,USER_ID_A,DISCNT_CODE," + "SPEC_TAG,RELATION_TYPE_CODE,INST_ID,CAMPN_ID,START_DATE,END_DATE,UPDATE_TIME,"
                + "UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4," + "RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2," + "RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3");
        parser.addSQL(" FROM tf_f_user_discnt ");
        parser.addSQL(" WHERE END_DATE > SYSDATE");
        parser.addSQL(" AND CAMPN_ID IS NOT NULL ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 获取本月产品信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getUserInfoChange(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }

        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT PARTITION_ID,USER_ID,TRADE_TYPE_CODE,RELATION_TRADE_ID,PRODUCT_ID," + "BRAND_CODE,SERIAL_NUMBER,IMSI,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,"
                + "UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1," + "RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1," + "RSRV_TAG2,RSRV_TAG3");
        parser.addSQL(" FROM tf_f_user_infochange ");
        parser.addSQL(" WHERE sysdate between start_date and end_date ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        return Dao.qryByParse(parser, pagination);
    }

    public IData getUserInfoChangeNext(IData data, Pagination pagination) throws Exception
    {
        if ("".equals(data.getString("USER_ID", "")))
        { // 强校验，防止返回大数据结果集和不正确结果
            return null;
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT PARTITION_ID,USER_ID,TRADE_TYPE_CODE,RELATION_TRADE_ID,PRODUCT_ID," + "BRAND_CODE,SERIAL_NUMBER,IMSI,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,"
                + "UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1," + "RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1," + "RSRV_TAG2,RSRV_TAG3");
        parser.addSQL(" FROM tf_f_user_infochange ");
        parser.addSQL(" WHERE end_date >= TRUNC(LAST_DAY(SYSDATE))+1 ");
        parser.addSQL(" AND end_date > start_date ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        parser.addSQL(" and rownum <= 1 ");
        IDataset outlist = Dao.qryByParse(parser, pagination);
        if (outlist.size() == 0)
            return null;
        else
            return (IData) outlist.get(0);

    }

    public IDataset getUserLastSvc(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT PARTITION_ID,USER_ID,USER_ID_A,SERVICE_ID," + "MAIN_TAG,INST_ID,CAMPN_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,"
                + "UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5," + "RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2," + "RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_STR6,RSRV_STR7,RSRV_STR8,"
                + "RSRV_STR9,RSRV_STR10 ");
        parser.addSQL(" FROM tf_f_user_svc ");
        parser.addSQL(" WHERE main_tag='1' ");
        parser.addSQL(" AND start_date < end_date ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        parser.addSQL(" AND start_date = (SELECT MAX(start_date) FROM tf_f_user_svc ");
        parser.addSQL("    WHERE main_tag = '1' ");
        parser.addSQL("    AND partition_id = :PARTITION_ID ");
        parser.addSQL("    AND user_id = :USER_ID ");
        parser.addSQL("    AND start_date < end_date )");
        return Dao.qryByParse(parser, pagination);
    }

    public IDataset getUserRes(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT PARTITION_ID,USER_ID,USER_ID_A,RES_TYPE_CODE,RES_CODE,IMSI,KI,INST_ID," + "CAMPN_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,"
                + "RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3," + "RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3");
        parser.addSQL(" FROM tf_f_user_res ");
        parser.addSQL(" WHERE SYSDATE BETWEEN start_date AND end_date ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        parser.addSQL(" AND RES_TYPE_CODE = :RES_TYPE_CODE ");
        return Dao.qryByParse(parser, pagination);
    }

    public IDataset getUserSvc(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT PARTITION_ID,USER_ID,USER_ID_A,SERVICE_ID," + "MAIN_TAG,INST_ID,CAMPN_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,"
                + "UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5," + "RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2," + "RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_STR6,RSRV_STR7,RSRV_STR8,"
                + "RSRV_STR9,RSRV_STR10 ");
        parser.addSQL(" FROM tf_f_user_svc ");
        parser.addSQL(" WHERE main_tag = '1' ");
        parser.addSQL(" AND SYSDATE BETWEEN start_date + 0 AND end_date + 0 ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        return Dao.qryByParse(parser, pagination);
    }

    public IDataset getUserSvcCt(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT PARTITION_ID,USER_ID,USER_ID_A,SERVICE_ID,MAIN_TAG," + "INST_ID,CAMPN_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,"
                + "REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3," + "RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,"
                + "RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10");
        parser.addSQL(" FROM tf_f_user_svc ");
        parser.addSQL(" WHERE service_id IN ('13','14','15') ");
        parser.addSQL(" AND SYSDATE BETWEEN start_date AND end_date ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        return Dao.qryByParse(parser, pagination);
    }

    public IDataset getUserSvcMy(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT PARTITION_ID,USER_ID,USER_ID_A,SERVICE_ID,MAIN_TAG," + "INST_ID,CAMPN_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,"
                + "REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3," + "RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,"
                + "RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10");
        parser.addSQL(" FROM tf_f_user_svc ");
        parser.addSQL(" WHERE service_id IN ('16','17','18','19') ");
        parser.addSQL(" AND SYSDATE BETWEEN start_date AND end_date ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        return Dao.qryByParse(parser, pagination);
    }

    public IDataset getUserSvcState(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT PARTITION_ID,USER_ID,SERVICE_ID,MAIN_TAG,STATE_CODE,START_DATE," + "END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1," + "RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,"
                + "RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3");
        parser.addSQL("  FROM tf_f_user_svcstate");
        parser.addSQL("  WHERE SYSDATE BETWEEN start_date AND end_date ");
        parser.addSQL("  AND USER_ID = :USER_ID ");
        parser.addSQL("  AND PARTITION_ID = :PARTITION_ID ");
        parser.addSQL("  AND SERVICE_ID = :SERVICE_ID ");
        return Dao.qryByParse(parser, pagination);
    }

    public IDataset getUserValidChange(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT PARTITION_ID,USER_ID,START_DATE,END_DATE,UPDATE_TIME," + "UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3," + "RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,"
                + "RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3");
        parser.addSQL(" FROM tf_f_user_validchange ");
        parser.addSQL(" WHERE PARTITION_ID = :PARTITION_ID ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND END_DATE = ( SELECT MAX(END_DATE) FROM TF_F_USER_VALIDCHANGE ");
        parser.addSQL("     WHERE PARTITION_ID = :PARTITION_ID ");
        parser.addSQL("     AND USER_ID = :USER_ID ) ");
        IDataset outlist = Dao.qryByParse(parser, pagination);
        return outlist;
    }

    /**
     * 判断是否是加入了黑名单
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset isBlackUser(IData data, Pagination pagination) throws Exception
    {

        String psptid = data.getString("PSPT_ID", "").trim();
        if ("".equals(psptid))
        {
            return new DatasetList();
        }
        else
        {
            // String sql =
            // "SELECT BLACKUSER_ID  FROM td_o_blackuser  WHERE START_DATE <= sysdate  AND END_DATE   >= sysdate AND PSPT_ID = ? ";
            // IDataset outlist = queryList(sql, new Object[] {psptid},new Pagination());
            String str = "SELECT 1 FROM td_o_blackuser  WHERE START_DATE <= sysdate AND END_DATE >= sysdate AND ROWNUM=1 AND PSPT_ID = ? ";
            StringBuilder sql = new StringBuilder();
            sql.append(str);
            IDataset outlist = null;// Dao.qryBySql(sql, new Object[] {psptid});

            // sql +=" AND PSPT_ID ='"+psptid+"' ";
            // log.debug("sql=["+sql+"]");
            // IDataset outlist = queryList(sql,new Pagination());
            // SQLParser parser = new SQLParser(data);
            // parser.addSQL("SELECT BLACKUSER_ID ");
            // parser.addSQL("  FROM td_o_blackuser ");
            // parser.addSQL(" WHERE START_DATE <= sysdate");
            // parser.addSQL(" AND PSPT_ID = :PSPT_ID  ");
            // parser.addSQL(" AND END_DATE   >= sysdate ");
            // IDataset outlist = queryList(parser,);
            return outlist;
        }
    }

    /**
     * 判断是否积分体验客户
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset isScoreUser(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT partition_id, ");
        parser.addSQL("        user_id, ");
        parser.addSQL("        rsrv_value_code, ");
        parser.addSQL("        rsrv_value, ");
        parser.addSQL("        rsrv_str1, ");
        parser.addSQL("        rsrv_str2, ");
        parser.addSQL("        rsrv_str3, ");
        parser.addSQL("        rsrv_str4, ");
        parser.addSQL("        rsrv_str5, ");
        parser.addSQL("        rsrv_str6, ");
        parser.addSQL("        rsrv_str7, ");
        parser.addSQL("        rsrv_str8, ");
        parser.addSQL("        rsrv_str9, ");
        parser.addSQL("        rsrv_str10, ");
        parser.addSQL("        start_date, ");
        parser.addSQL("        end_date ");
        parser.addSQL("   FROM tf_f_user_other ");
        parser.addSQL("  WHERE sysdate BETWEEN start_date  AND end_date ");
        parser.addSQL("    AND user_id = :USER_ID ");
        parser.addSQL("    AND rsrv_value_code = :RSRV_VALUE_CODE ");
        parser.addSQL("    AND partition_id = :PARTITION_ID  ");

        IDataset outlist = Dao.qryByParse(parser, pagination);
        return outlist;
    }

    // 根据serial_number查询非正常用户
    public IDataset qryDestroyUserInfoBySN(IData data, Pagination pagination) throws Exception
    {
        String serial_number = data.getString("SERIAL_NUMBER", "");
        if ("".equals(serial_number))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT partition_id,user_id,cust_id,usecust_id,brand_code,product_id,eparchy_code,");
        parser.addSQL("city_code,user_passwd,user_type_code,serial_number,score_value,credit_class,basic_credit_value,");
        parser.addSQL("credit_value,acct_tag,prepay_tag,to_char(in_date, 'yyyy-mm-dd hh24:mi:ss') in_date,");
        parser.addSQL("to_char(open_date, 'yyyy-mm-dd hh24:mi:ss') open_date,remove_tag,");
        parser.addSQL("to_char(destroy_time, 'yyyy-mm-dd hh24:mi:ss') destroy_time,");
        parser.addSQL("to_char(pre_destroy_time, 'yyyy-mm-dd hh24:mi:ss') pre_destroy_time,");
        parser.addSQL("to_char(first_call_time, 'yyyy-mm-dd hh24:mi:ss') first_call_time,");
        parser.addSQL("to_char(last_stop_time, 'yyyy-mm-dd hh24:mi:ss') last_stop_time,");
        parser.addSQL("open_mode,user_state_codeset,mpute_month_fee,");
        parser.addSQL("to_char(mpute_date, 'yyyy-mm-dd hh24:mi:ss') mpute_date,");
        parser.addSQL("rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,");
        parser.addSQL("rsrv_str8,rsrv_str9,rsrv_str10,");
        parser.addSQL("to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,");
        parser.addSQL("assure_cust_id,assure_type_code,to_char(assure_date, 'yyyy-mm-dd hh24:mi:ss') assure_date,");
        parser.addSQL("develop_eparchy_code,develop_city_code,develop_depart_id,develop_staff_id,");
        parser.addSQL("to_char(develop_date, 'yyyy-mm-dd hh24:mi:ss') develop_date,");
        parser.addSQL("develop_no,in_depart_id,in_staff_id,remove_eparchy_code,remove_city_code,remove_depart_id,");
        parser.addSQL("remove_reason_code,remark ");
        parser.addSQL(" FROM tf_f_user");
        parser.addSQL(" WHERE remove_tag not in ('0','5') ");
        parser.addSQL(" AND serial_number = :SERIAL_NUMBER ");

        return Dao.qryByParse(parser, pagination);
    }

    // 根据serial_number查询最后注销的用户
    public IDataset qryMaxDestroyUserInfoBySN(IData data, Pagination pagination) throws Exception
    {
        String serial_number = data.getString("SERIAL_NUMBER", "");
        if ("".equals(serial_number))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT partition_id,user_id,cust_id,usecust_id,brand_code,product_id,eparchy_code,");
        parser.addSQL("city_code,user_passwd,user_type_code,serial_number,score_value,credit_class,basic_credit_value,");
        parser.addSQL("credit_value,acct_tag,prepay_tag,to_char(in_date, 'yyyy-mm-dd hh24:mi:ss') in_date,");
        parser.addSQL("to_char(open_date, 'yyyy-mm-dd hh24:mi:ss') open_date,remove_tag,");
        parser.addSQL("to_char(destroy_time, 'yyyy-mm-dd hh24:mi:ss') destroy_time,");
        parser.addSQL("to_char(pre_destroy_time, 'yyyy-mm-dd hh24:mi:ss') pre_destroy_time,");
        parser.addSQL("to_char(first_call_time, 'yyyy-mm-dd hh24:mi:ss') first_call_time,");
        parser.addSQL("to_char(last_stop_time, 'yyyy-mm-dd hh24:mi:ss') last_stop_time,");
        parser.addSQL("open_mode,user_state_codeset,mpute_month_fee,");
        parser.addSQL("to_char(mpute_date, 'yyyy-mm-dd hh24:mi:ss') mpute_date,");
        parser.addSQL("rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,");
        parser.addSQL("rsrv_str8,rsrv_str9,rsrv_str10,");
        parser.addSQL("to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,");
        parser.addSQL("assure_cust_id,assure_type_code,to_char(assure_date, 'yyyy-mm-dd hh24:mi:ss') assure_date,");
        parser.addSQL("develop_eparchy_code,develop_city_code,develop_depart_id,develop_staff_id,");
        parser.addSQL("to_char(develop_date, 'yyyy-mm-dd hh24:mi:ss') develop_date,");
        parser.addSQL("develop_no,in_depart_id,in_staff_id,remove_eparchy_code,remove_city_code,remove_depart_id,");
        parser.addSQL("remove_reason_code,remark ");
        parser.addSQL(" FROM tf_f_user");
        parser.addSQL(" WHERE remove_tag != '0' ");
        parser.addSQL(" AND serial_number = :SERIAL_NUMBER ");
        parser.addSQL(" AND destroy_time = (SELECT MAX(destroy_time) FROM tf_f_user ");
        parser.addSQL("                        WHERE serial_number = :SERIAL_NUMBER ");
        parser.addSQL("                          AND remove_tag != '0') ");
        parser.addSQL(" AND NOT EXISTS (SELECT 1 FROM tf_f_user ");
        parser.addSQL("                    WHERE serial_number = :SERIAL_NUMBER ");
        parser.addSQL("                      AND remove_tag = '0')");

        return Dao.qryByParse(parser, pagination);
    }

    // 根据acct_id查询付费关系
    public IDataset qryPayRelationByAid(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT partition_id,user_id,acct_id,payitem_code,acct_priority,");
        parser.addSQL(" user_priority,bind_type,START_CYCLE_ID,END_CYCLE_ID,default_tag,act_tag,limit_type,limit,");
        parser.addSQL(" complement_tag,update_staff_id,update_depart_id,update_time ");
        parser.addSQL("  FROM tf_a_payrelation a ");
        parser.addSQL("  WHERE a.acct_id= :ACCT_ID ");
        parser.addSQL("  AND a.default_tag= :DEFAULT_TAG ");
        parser.addSQL("  AND a.act_tag= :ACT_TAG ");

        // modify by caiy , td_a_acycpara表里面没有数据，不能用来做条件
        parser.addSQL(" AND to_char(sysdate,'yyyymm') between START_CYCLE_ID and END_CYCLE_ID ");
        // parser.addSQL("  AND (select acyc_id from TD_A_ACYCPARA ");
        // parser.addSQL("      where acyc_start_time <= SYSDATE and acyc_end_time >= SYSDATE) ");
        // parser.addSQL("      between  a.start_acyc_id and a.end_acyc_id ");
        parser.addSQL("   AND NOT EXISTS (SELECT 1 FROM tf_f_user ");
        parser.addSQL("                   WHERE user_id = a.user_id ");
        parser.addSQL("                   AND partition_id = MOD(a.user_id,10000) ");
        parser.addSQL("                   AND brand_code LIKE '%IP%')");
        return Dao.qryByParse(parser, pagination);
    }

    // ----------------------------------------------------------------------------------------------------------------------------------
    // 根据user_id查询付费关系
    public IData qryPayRelationByUid(IData data, Pagination pagination) throws Exception
    {
        if ("".equals(data.getString("USER_ID", "")))
        { // 强校验，防止返回大数据结果集和不正确结果
            return null;
        }
        SQLParser parser = new SQLParser(data);
        // modify by caiy ，表结构有改动
        // parser.addSQL(" SELECT partition_id,user_id,acct_id,payitem_code,acct_priority,user_priority,bind_type,start_acyc_id, ");
        // parser.addSQL(" end_acyc_id,default_tag,act_tag,limit_type,limit,complement_tag,update_staff_id,update_depart_id, ");
        parser.addSQL(" SELECT partition_id,user_id,acct_id,payitem_code,acct_priority,user_priority,bind_type,START_CYCLE_ID, ");
        parser.addSQL(" END_CYCLE_ID,default_tag,act_tag,limit_type,limit,complement_tag,update_staff_id,update_depart_id, ");
        parser.addSQL(" update_time ");
        parser.addSQL(" FROM tf_a_payrelation ");
        parser.addSQL(" WHERE default_tag='1' ");// 默认帐户
        parser.addSQL(" AND user_id= :USER_ID ");
        parser.addSQL(" AND partition_id=MOD(:USER_ID,10000) ");
        parser.addSQL(" AND act_tag='1' "); // 作用标识，1作用
        // modify by caiy , td_a_acycpara表里面没有数据，不能用来做条件
        parser.addSQL(" AND to_char(sysdate,'yyyymm') between START_CYCLE_ID and END_CYCLE_ID ");
        parser.addSQL(" and rownum <= 1 ");

        // parser.addSQL(" AND (select acyc_id from TD_A_ACYCPARA ");
        // parser.addSQL("          where acyc_start_time <= SYSDATE and acyc_end_time >= SYSDATE) ");
        // parser.addSQL("          between  start_acyc_id and end_acyc_id ");
        IDataset outlist = Dao.qryByParse(parser, pagination);
        if (outlist.size() == 0)
            return null;
        else
            return (IData) outlist.get(0);

    }

    // 根据cust_id查询正常的用户
    public IDataset qryUserInfoByCid(IData data, Pagination pagination) throws Exception
    {
        String cust_id = data.getString("CUST_ID", "");
        if ("".equals(cust_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT partition_id,user_id,cust_id,usecust_id,brand_code,product_id,eparchy_code,");
        parser.addSQL("city_code,user_passwd,user_type_code,serial_number,score_value,credit_class,basic_credit_value,");
        parser.addSQL("credit_value,acct_tag,prepay_tag,to_char(in_date, 'yyyy-mm-dd hh24:mi:ss') in_date,");
        parser.addSQL("to_char(open_date, 'yyyy-mm-dd hh24:mi:ss') open_date,remove_tag,");
        parser.addSQL("to_char(destroy_time, 'yyyy-mm-dd hh24:mi:ss') destroy_time,");
        parser.addSQL("to_char(pre_destroy_time, 'yyyy-mm-dd hh24:mi:ss') pre_destroy_time,");
        parser.addSQL("to_char(first_call_time, 'yyyy-mm-dd hh24:mi:ss') first_call_time,");
        parser.addSQL("to_char(last_stop_time, 'yyyy-mm-dd hh24:mi:ss') last_stop_time,");
        parser.addSQL("open_mode,user_state_codeset,mpute_month_fee,");
        parser.addSQL("to_char(mpute_date, 'yyyy-mm-dd hh24:mi:ss') mpute_date,");
        parser.addSQL("rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,");
        parser.addSQL("rsrv_str8,rsrv_str9,rsrv_str10,");
        parser.addSQL("to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,");
        parser.addSQL("assure_cust_id,assure_type_code,to_char(assure_date, 'yyyy-mm-dd hh24:mi:ss') assure_date,");
        parser.addSQL("develop_eparchy_code,develop_city_code,develop_depart_id,develop_staff_id,");
        parser.addSQL("to_char(develop_date, 'yyyy-mm-dd hh24:mi:ss') develop_date,");
        parser.addSQL("develop_no,in_depart_id,in_staff_id,remove_eparchy_code,remove_city_code,remove_depart_id,");
        parser.addSQL("remove_reason_code,remark ");
        parser.addSQL(" FROM tf_f_user");
        parser.addSQL(" WHERE remove_tag='0' ");
        parser.addSQL(" AND cust_id = :CUST_ID ");

        return Dao.qryByParse(parser, pagination);
    }

    // 根据serial_number和remove_tag查询用户信息
    public IDataset qryUserInfoBySN(IData data, Pagination pagination) throws Exception
    {
        String serial_number = data.getString("SERIAL_NUMBER", "");
        if ("".equals(serial_number))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT partition_id,user_id,cust_id,usecust_id,brand_code,product_id,eparchy_code,");
        parser.addSQL("city_code,user_passwd,user_type_code,serial_number,score_value,credit_class,basic_credit_value,");
        parser.addSQL("credit_value,acct_tag,prepay_tag,to_char(in_date, 'yyyy-mm-dd hh24:mi:ss') in_date,");
        parser.addSQL("to_char(open_date, 'yyyy-mm-dd hh24:mi:ss') open_date,remove_tag,");
        parser.addSQL("to_char(destroy_time, 'yyyy-mm-dd hh24:mi:ss') destroy_time,");
        parser.addSQL("to_char(pre_destroy_time, 'yyyy-mm-dd hh24:mi:ss') pre_destroy_time,");
        parser.addSQL("to_char(first_call_time, 'yyyy-mm-dd hh24:mi:ss') first_call_time,");
        parser.addSQL("to_char(last_stop_time, 'yyyy-mm-dd hh24:mi:ss') last_stop_time,");
        parser.addSQL("open_mode,user_state_codeset,mpute_month_fee,");
        parser.addSQL("to_char(mpute_date, 'yyyy-mm-dd hh24:mi:ss') mpute_date,");
        parser.addSQL("rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,");
        parser.addSQL("rsrv_str8,rsrv_str9,rsrv_str10,");
        parser.addSQL("to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,");
        parser.addSQL("assure_cust_id,assure_type_code,to_char(assure_date, 'yyyy-mm-dd hh24:mi:ss') assure_date,");
        parser.addSQL("develop_eparchy_code,develop_city_code,develop_depart_id,develop_staff_id,");
        parser.addSQL("to_char(develop_date, 'yyyy-mm-dd hh24:mi:ss') develop_date,");
        parser.addSQL("develop_no,in_depart_id,in_staff_id,remove_eparchy_code,remove_city_code,remove_depart_id,");
        parser.addSQL("remove_reason_code,remark ");
        parser.addSQL(" FROM tf_f_user");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND serial_number=:SERIAL_NUMBER ");
        parser.addSQL(" AND remove_tag=:REMOVE_TAG ");

        return Dao.qryByParse(parser, pagination);
    }

    // 根据user_id查询用户
    public IDataset qryUserInfoByUid(IData data, Pagination pagination) throws Exception
    {
        if (("".equals(data.getString("USER_ID", ""))) && ("".equals(data.getString("PARTITION_ID", ""))))
        {
            return new DatasetList();
        }
        else
        {
            /*
             * SQLParser parser = new SQLParser(data);
             * parser.addSQL("SELECT partition_id,user_id,cust_id,usecust_id,brand_code,product_id,eparchy_code,");
             * parser.addSQL("city_code,user_passwd,user_type_code,serial_number,");
             * parser.addSQL("acct_tag,prepay_tag,to_char(in_date, 'yyyy-mm-dd hh24:mi:ss') in_date,");
             * parser.addSQL("to_char(open_date, 'yyyy-mm-dd hh24:mi:ss') open_date,remove_tag,");
             * parser.addSQL("to_char(destroy_time, 'yyyy-mm-dd hh24:mi:ss') destroy_time,");
             * parser.addSQL("to_char(pre_destroy_time, 'yyyy-mm-dd hh24:mi:ss') pre_destroy_time,");
             * parser.addSQL("to_char(first_call_time, 'yyyy-mm-dd hh24:mi:ss') first_call_time,");
             * parser.addSQL("to_char(last_stop_time, 'yyyy-mm-dd hh24:mi:ss') last_stop_time,");
             * parser.addSQL("open_mode,user_state_codeset,mpute_month_fee,");
             * parser.addSQL("to_char(mpute_date, 'yyyy-mm-dd hh24:mi:ss') mpute_date,");
             * parser.addSQL("rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,");
             * parser.addSQL("rsrv_str8,rsrv_str9,rsrv_str10,");
             * parser.addSQL("to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,");
             * parser.addSQL("assure_cust_id,assure_type_code,to_char(assure_date, 'yyyy-mm-dd hh24:mi:ss') assure_date,"
             * ); parser.addSQL("develop_eparchy_code,develop_city_code,develop_depart_id,develop_staff_id,");
             * parser.addSQL("to_char(develop_date, 'yyyy-mm-dd hh24:mi:ss') develop_date,");
             * parser.addSQL("develop_no,in_depart_id,in_staff_id,remove_eparchy_code,remove_city_code,remove_depart_id,"
             * ); parser.addSQL("remove_reason_code,remark "); parser.addSQL(" FROM tf_f_user");
             * parser.addSQL(" WHERE 1=1 "); parser.addSQL(" AND user_id=:USER_ID ");
             * parser.addSQL(" AND partition_id=:PARTITION_ID ");
             */

            return Dao.qryByCodeParser("TF_F_USER", "SEL_BY_USERID_FOR_360", data);
        }
    }

    /*
     * 查询客户信用度最大截止日期
     */
    public IDataset queryCreditMaxEndDate(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" select to_char(max(end_date),'yyyy-MM-dd hh24:mi:ss') END_DATE from ( ");
        parser.addSQL(" 	select max(end_date) end_date from  TF_O_ADDCREDIT_USER  ");
        parser.addSQL("			 where end_date > sysdate ");
        parser.addSQL("			 and user_id = :USER_ID  ");
        parser.addSQL("     union all select max(end_date) end_date from  TF_OH_ADDCREDIT_USER  ");
        parser.addSQL("			 where end_date > sysdate ");
        parser.addSQL("          and user_id = :USER_ID ");
        parser.addSQL("  ) ");

        return Dao.qryByParse(parser);
    }

    /*
     * 查询SIM卡号、卡类型、PUK
     */
    public IDataset querySIMtype(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);

        if (("".equals(param.getString("EPARCHY_CODE", ""))) && ("".equals(param.getString("USER_ID", ""))))
        {
            return new DatasetList();
        }
        else
        {
            parser.addSQL(" Select Puk, Sim_Type_Code, Sim_Card_No, Imsi From Tf_r_Simcard_Use ");
            parser.addSQL(" Where Sim_Card_No = (Select Res_Code From Tf_f_User_Res  ");
            parser.addSQL(" Where Res_Type_Code = '1'  ");
            parser.addSQL(" And User_Id = :USER_ID  ");
            parser.addSQL(" And Partition_Id = mod(:USER_ID,10000)  ");
            parser.addSQL(" And Sysdate Between Start_Date And End_Date  ");
            parser.addSQL(" And Rownum = 1)  ");
            parser.addSQL(" and EPARCHY_CODE = :EPARCHY_CODE ");

            return Dao.qryByParse(parser, pagination);
        }
    }
}
