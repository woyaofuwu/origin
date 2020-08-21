
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.Qry360UserViewException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UUserTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UVipClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserCommUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class QryUserInfoDao
{
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

        IDataset outlist = UserCommUtil.qryByParse(parser, pagination);
        return outlist;
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
            return UserCommUtil.qryByParse(parser, pagination);
        }
    }

    public IDataset getCustVip(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        if (StringUtils.isBlank(user_id))
        {
            return new DatasetList();
        }

        // start of luoz_20130720 因去中心库的mv，需要拆sql。
        return CustVipInfoQry.qryVipInfoByUserId(user_id);
        // end of luoz_20130720 因去中心库的mv，需要拆sql。
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
        parser.addSQL("       destroy_staff_id ");
        parser.addSQL("  FROM tf_f_user_deduct ");
        parser.addSQL(" WHERE user_id = :USER_ID ");
        parser.addSQL("  AND destroy_tag = :DESTROY_TAG ");

        IDataset outlist = UserCommUtil.qryByParse(parser, pagination);
        return outlist;
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
        // parser.addSQL(" AND (select acyc_id from TD_A_ACYCPARA where
        // acyc_start_time <= SYSDATE and acyc_end_time >=
        // SYSDATE) " +
        // "between start_cycle_id and end_cycle_id ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        IDataset outlist = UserCommUtil.qryByParse(parser, pagination);
        return outlist;
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
        parser.addSQL("       a.rsrv_str9 rsrv_str9, ");
        parser.addSQL("       a.rsrv_str10 rsrv_str10, ");
        parser.addSQL("       to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
        parser.addSQL("       to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date ");
        parser.addSQL("  FROM tf_f_user_other a  ");
        parser.addSQL(" WHERE a.user_id = :USER_ID ");
        parser.addSQL("   AND a.partition_id = :PARTITION_ID ");
        parser.addSQL("   AND a.rsrv_value_code = :RSRV_VALUE_CODE ");
        // parser.addSQL("   AND c.chnl_code = a.rsrv_str1  ");
        parser.addSQL("   AND sysdate BETWEEN a.start_date AND a.end_date ");
        IDataset outlist = UserCommUtil.qryByParse(parser, pagination);
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
        return UserCommUtil.qryByParse(parser, pagination);
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
        return UserCommUtil.qryByParse(parser, pagination);
    }

    public String getServiceName(String service_id, Pagination pagination) throws Exception
    {
        if ("".equals(service_id))
        {
            return "";
        }
        else
        {
// 	 modify by lijun17  需调产商品接口查询 
//            IData in = new DataMap();
//            in.put("SERVICE_ID", service_id);
//
//            SQLParser parser = new SQLParser(in);
//            parser.addSQL("SELECT SERVICE_ID,SERVICE_NAME,NET_TYPE_CODE,INTF_MODE," + "PARENT_TYPE_CODE,SERVICE_MODE,SERVICE_BRAND_CODE,SERVICE_LEVEL," + "SERVICE_STATE,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,"
//                    + "UPDATE_DEPART_ID,REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4," + "RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3");
//            parser.addSQL(" FROM td_b_service ");
//            parser.addSQL(" WHERE service_id = :SERVICE_ID ");
//            parser.addSQL(" and rownum <= 1 ");
//            IDataset outlist = UserCommUtil.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
        	IData serviceInfo = UpcCall.queryOfferByOfferId("S", service_id);
            if (IDataUtil.isEmpty(serviceInfo))
                return "";
            else
                return serviceInfo.getString("OFFER_NAME");
        }
    }

    public IDataset getSimCardUse(IData data, Pagination pagination) throws Exception
    {
        String imsi = data.getString("IMSI", "");
        if ("".equals(imsi))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);
        // modify by huanghui 表字段被删除所以修改sql 2014/07/24
        // parser.addSQL(" SELECT TO_CHAR(PARTITION_ID) PARTITION_ID, SIM_CARD_NO, NET_TYPE_CODE, RES_TYPE_CODE, RES_KIND_CODE, CAPACITY_TYPE_CODE, IMSI,  "
        // + " EMPTY_CARD_ID, FACTORY_CODE, IMEI, ESN, USE_TAG, PIN, PUK, PIN2, PUK2, KI, MSIN, UIM_ID, OPC, KIND  ");
        parser.addSQL(" SELECT  SIM_CARD_NO, NET_TYPE_CODE, CAPACITY_TYPE_CODE, IMSI,  " + " EMPTY_CARD_ID, FACTORY_CODE, IMEI, ESN, USE_TAG, PIN, PUK, PIN2, PUK2, KI, MSIN, UIM_ID, OPC  ");

        parser.addSQL("   FROM tf_r_simcard_use ");
        parser.addSQL(" WHERE imsi=:IMSI");
        //return UserCommUtil.qryByParse(parser, pagination, Route.CONN_RES);
        
        return ResCall.getSimCardInfo("1", "",imsi, "1");
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
        IDataset outlist = UserCommUtil.qryByParse(parser, pagination, Route.getJourDb(BizRoute.getTradeEparchyCode()));
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
        return UserCommUtil.qryByParse(parser, pagination);
    }

    public IDataset getUserDiscnt(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);//  producnt_id  PACKAGE_ID  已经去掉  modify  by duhj
        parser.addSQL("SELECT PARTITION_ID,USER_ID,USER_ID_A,DISCNT_CODE," + "SPEC_TAG,RELATION_TYPE_CODE,INST_ID,CAMPN_ID,START_DATE,END_DATE,UPDATE_TIME,"
                + "UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4," + "RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2," + "RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3");
        parser.addSQL(" FROM tf_f_user_discnt ");
        parser.addSQL(" WHERE END_DATE > SYSDATE");
        parser.addSQL(" AND CAMPN_ID IS NOT NULL ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        return UserCommUtil.qryByParse(parser, pagination);
        
        
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
        return UserCommUtil.qryByParse(parser, pagination);
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
        IDataset outlist = UserCommUtil.qryByParse(parser, pagination);
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
        SQLParser parser = new SQLParser(data);//PRODUCT_ID,PACKAGE_ID  去掉 duhj
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
        return UserCommUtil.qryByParse(parser, pagination);
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
        return UserCommUtil.qryByParse(parser, pagination);
    }

    public IData getUserSuperInfo(IData pd, Pagination pagination, IData param, int serviceId) throws Exception
    {
        IData qryData = new DataMap();
        String userType = UUserTypeInfoQry.getUserTypeByUserTypeCode(param.getString("USER_TYPE_CODE"));
        param.put("USER_TYPE", userType);

        String user_state_codeset = param.getString("USER_STATE_CODESET", "");
        String stateExplain = "";
        /*
         * if ("0".equals(user_state_codeset)) { stateExplain = "开通"; param.put("X_SVCSTATE_EXPLAIN", "开通"); }
         */

        String startDate = "";
        String str = "";

        String user_id = param.getString("USER_ID", "");
        String cust_id = param.getString("CUST_ID", "");
        IData productInfo = UcaInfoQry.qryUserMainProdInfoByUserId(user_id);
        if (IDataUtil.isNotEmpty(productInfo))
        {
            param.put("BRAND_CODE", productInfo.getString("BRAND_CODE", ""));
            param.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID", ""));//当月的product_id duhj 

        }
        // add by caiy
        // 查询tf_f_user_brandchange表
        qryData.put("USER_ID", user_id);
        qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
        IDataset oxy = getUserBrandChange(qryData, pagination);
        if (oxy != null && oxy.size() > 0)
        {
            param.put("BRAND_NO", oxy.getData(0).getString("BRAND_NO", "1"));
        }

        IData outData = new DataMap();
        if (serviceId < 0) // 先根据用户标识获取主体服务标识
        {
            if ("".equals(user_id))
            {
                CSAppException.apperr(Qry360UserViewException.CRM_UserView_1);
            }
            qryData.put("USER_ID", user_id);
            qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
            IDataset outset = getUserSvc(qryData, pagination);
            if (outset.size() <= 0)
            {
                // 获取用户最后的主体服务标识
                outset = new DatasetList();
                outset = getUserLastSvc(qryData, pagination);
                // add by caiy 修改代码漏洞
                if (outset.size() > 0)
                    outData = (IData) outset.get(0);
            }
            else
            {
                outData = (IData) outset.get(0);
            }
            serviceId = outData.getInt("SERVICE_ID");
            startDate = outData.getString("START_DATE");
        }

        if (user_state_codeset.length() == 0) // 根据用户标识和服务标识获取服务状态编码列表
        {
            if ("".equals(user_id))
            {
                CSAppException.apperr(Qry360UserViewException.CRM_UserView_1);
            }
            qryData = new DataMap();
            qryData.put("USER_ID", user_id);
            qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
            qryData.put("SERVICE_ID", "" + serviceId);
            IDataset outset = getUserSvcState(qryData, pagination);
            for (int i = 0; i < outset.size(); i++)
            {
                user_state_codeset += ((IData) outset.get(i)).getString("STATE_CODE");
            }
        }

        // //获取服务状态集对应状态名称

        IDataset ox1 = USvcStateInfoQry.qryStateNameBySvcIdStateCode(String.valueOf(serviceId), user_state_codeset);
        boolean firstState = true;
        stateExplain = "";
        for (int i = 0; i < ox1.size(); i++)
        {
            if (!firstState)
                stateExplain += ",";
            stateExplain += ((IData) ox1.get(i)).getString("STATE_NAME");
            firstState = false;
        }

        qryData = new DataMap();
        qryData.put("CUST_ID", cust_id);
        qryData.put("PARTITION_ID", StrUtil.getPartition4ById(cust_id));
        String psptId = "";

        IData o1x = UcaInfoQry.qryCustomerInfoByCustId(cust_id);
        if (IDataUtil.isNotEmpty(o1x))
        {
            param.put("CUST_NAME", o1x.getString("CUST_NAME"));
            param.put("PSPT_TYPE_CODE", o1x.getString("PSPT_TYPE_CODE"));
            param.put("PSPT_ID", o1x.getString("PSPT_ID"));
            psptId = o1x.getString("PSPT_ID", "");
        }

        // 获取生日
        IData oo = UcaInfoQry.qryPerInfoByCustId(cust_id);
        if (IDataUtil.isNotEmpty(oo))
        {
            param.put("BIRTHDAY", oo.getString("BIRTHDAY"));
        }

        String assure_cust_id = param.getString("ASSURE_CUST_ID", "");
        // 获取担保人姓名 海南集团客户排除“0”haiNan modify by huanghui
        if (StringUtils.isNotBlank(assure_cust_id)&&!"0".equals(assure_cust_id))
        {
            qryData = new DataMap();
            qryData.put("CUST_ID", assure_cust_id);
            qryData.put("PARTITION_ID", StrUtil.getPartition4ById(assure_cust_id));
            IData o2x = UcaInfoQry.qryCustomerInfoByCustId(assure_cust_id);
            if (IDataUtil.isNotEmpty(o2x))
            {
                param.put("ASSURE_NAME", o2x.getString("CUST_NAME"));
            }
        }

        if ("".equals(user_id))
        {
            CSAppException.apperr(Qry360UserViewException.CRM_UserView_1);
        }

        // 获取本月产品
        qryData = new DataMap();
        qryData.put("USER_ID", user_id);
        qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
        //modify by duhj  2017/5/19  查询产品以tf_f_user_product 表为主
//        IDataset o3 = getUserInfoChange(qryData, pagination);
//        if (o3.size() > 0)
//        {
//            IData o3x = (IData) o3.get(0);
//            param.put("PRODUCT_ID", o3x.getString("PRODUCT_ID"));
//            // jade前台数据转换
//        }

        // 获取下月产品public static final String OPERTYPE_PRODUCT_CHANGE = "110";
        qryData = new DataMap();
        qryData.put("USER_ID", user_id);
        qryData.put("TRADE_TYPE_CODE", "110");// 产品变更
        IDataset o4 = getTradeByUidTradeTypeCode(qryData, pagination);
        if (o4.size() == 0)
        {
        	
            //2017/5/19 查询产品不在查tf_f_user_infochange ，o4为空，说明没有预约产品变更，下月产品品牌与当前月一样 modify by duhj
//            qryData = new DataMap();
//            qryData.put("USER_ID", user_id);
//            qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
//            IData o5 = getUserInfoChangeNext(qryData, pagination);
//
//            if (o5 != null)
//            {
//                param.put("B_PRODUCT_ID", o5.getString("PRODUCT_ID"));
//                // jade前台数据转换 param.put("NEXT_PRODUCT_NAME",
//
//                param.put("B_BRAND_CODE", o5.getString("BRAND_CODE"));
//            }
            param.put("B_PRODUCT_ID", param.getString("PRODUCT_ID"));
            param.put("B_BRAND_CODE", param.getString("BRAND_CODE"));

        	
        }
        else
        {
            IData ox = (IData) o4.get(0);
            param.put("B_PRODUCT_ID", ox.getString("PRODUCT_ID"));
            // jade前台数据转换 param.put("NEXT_PRODUCT_NAME",
            param.put("B_BRAND_CODE", ox.getString("BRAND_CODE"));
        }

        // 获取SIM卡信息
        qryData = new DataMap();
        qryData.put("USER_ID", user_id);
        qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
        qryData.put("RES_TYPE_CODE", "1");
        IDataset o6 = getUserRes(qryData, pagination);
        if (o6.size() > 0)
        {
            IData o6x = (IData) o6.get(0);
            param.put("SIM_CARD_NO", o6x.getString("RES_CODE"));
            // 获取PUK信息
            if (!"".equals(o6x.getString("IMSI", "")))
            { // 数据表中IMSI不是非空属性，如果该字段无值，会查询大结果集
                qryData = new DataMap();
                qryData.put("IMSI", o6x.getString("IMSI"));
                IDataset o7 = getSimCardUse(qryData, pagination);
                if (o7.size() > 0)
                {
                    IData o7x = (IData) o7.get(0);
                    param.put("PUK", o7x.getString("PUK"));
                    param.put("RES_KIND_CODE", o7x.getString("RES_KIND_CODE", ""));
                    // 2009-05-22 增加特权SYS320判断，puk码查询特权，有特权则显示，无则显示星号
                    // Add by Caiy
                    /*
                     * jade if(!pd.getContext().hasPriv("SYS320")) { param.put("PUK", "******"); }
                     */
                }
            }
        }

        // 获取漫游级别
        qryData = new DataMap();
        qryData.put("USER_ID", user_id);
        qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
        IDataset ox2 = getUserSvcMy(qryData, pagination);
        str = "";
        for (int i = 0; i < ox2.size(); i++)
        {
            if (str.equals(""))
            {
                str = getServiceName(ox2.getData(i).getString("SERVICE_ID"), pagination);
            }
            else
            {
                str = str + "," + getServiceName(ox2.getData(i).getString("SERVICE_ID"), pagination);
            }
        }
        param.put("SERVICE_NAME", str);

        // 获取长权级别
        qryData = new DataMap();
        qryData.put("USER_ID", user_id);
        qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
        IDataset ox3 = getUserSvcCt(qryData, pagination);
        str = "";
        for (int i = 0; i < ox3.size(); i++)
        {
            if (str.equals(""))
            {
                str = getServiceName(ox3.getData(i).getString("SERVICE_ID"), pagination);
            }
            else
            {
                str = str + "," + getServiceName(ox3.getData(i).getString("SERVICE_ID"), pagination);
            }
        }
        param.put("SERVICE_TYPE", str);

        // 获取用户营销包
        qryData = new DataMap();
        qryData.put("USER_ID", user_id);
        qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
       // IDataset ox4 = getUserDiscnt(qryData, pagination);
        IDataset ox4 = UserDiscntInfoQry.getUserDiscntSByUserId(qryData, pagination);//modify  by duhj

        str = "";
        for (int i = 0; i < ox4.size(); i++)
        {
            if (str.equals(""))
            {
                str = UDiscntInfoQry.getDiscntNameByDiscntCode((ox4.getData(i).getString("DISCNT_CODE")));
            }
            else
            {
                str = str + "," + UDiscntInfoQry.getDiscntNameByDiscntCode((ox4.getData(i).getString("DISCNT_CODE")));
            }
        }
        param.put("DISCNT_NAME", str);
        // 查询实时话费信息（掉账务的接口）
        // cclib::queryLeaveRealFee(userId,leaveRealfee,realFee,updateTime);
        // 这句需要个人业务给出解释

        // 获取用户预存有效期
        qryData = new DataMap();
        qryData.put("USER_ID", user_id);
        qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
        IDataset o8 = getUserValidChange(qryData, pagination);
        if (o8.size() > 0)
        {
            IData o8x = (IData) o8.get(0);
            param.put("END_DATE", o8x.getString("END_DATE"));
        }

        // 获取大客户信息
        qryData = new DataMap();
        qryData.put("USER_ID", user_id);
        IDataset o9 = getCustVip(qryData, pagination);
        if (o9.size() > 0)
        {
            IData o9x = (IData) o9.get(0);
            // add by xiezhenglong 要求在用户基本信息里面显示大客户类型与级别
            String vip_type_code = o9x.getString("VIP_TYPE_CODE", "");
            String vip_class_id = o9x.getString("VIP_CLASS_ID", "");
            // jade String vip_type_name=Utility.getStaticValue(pd,
            // "td_m_viptype", "VIP_TYPE_CODE", "VIP_TYPE",
            // vip_class_id);
            // jade param.put("CLASS_NAME", "是大客户 "+vip_type_name);

            // if("0".equals(vip_type_code) || "2".equals(vip_type_code) ||
            // "3".equals(vip_type_code))
            // {
            // add by caiy 2009-6-22 湖南不需要判断, yijb
            param.put("USECUST_NAME", o9x.getString("USECUST_NAME", ""));
            // add end
            param.put("VIP_ID", o9x.getString("VIP_ID"));
            param.put("VIP_CARD_NO", o9x.getString("VIP_CARD_NO"));
            param.put("VIP_TYPE_CODE", o9x.getString("VIP_TYPE_CODE"));
            param.put("VIP_CLASS_ID", o9x.getString("VIP_CLASS_ID"));
            param.put("CUST_MANAGER_ID", o9x.getString("CUST_MANAGER_ID", ""));
            // param.put("CLASS_NAME", "是大客户 ");
            String className = UVipClassInfoQry.getVipClassNameByVipTypeCodeClassId(o9x.getString("VIP_TYPE_CODE"), o9x.getString("VIP_CLASS_ID"));
            param.put("CLASS_NAME", className);
            // param.put("CUST_DEPART_ID", o9x.getString("CUST_DEPART_ID", ""));
            String custMagId = o9x.getString("CUST_MANAGER_ID", "");
            IData custMagData = UStaffInfoQry.qryCustManagerInfoByCustManagerId(custMagId);
            param.put("CUST_DEPART_ID", custMagData.getString("DEPART_ID", ""));
            // jade前台数据转换 param.put("LINK_PHONE",
            // com.linkage.component.util.Utility.getStaticValue(pd,
            // o9x.getString("CUST_MANAGER_ID","")));
            // }
        }
        else
        {
            param.put("CLASS_NAME", "非大客户");
            // param.put("VIP_TYPE_CODE", "0");
        }

        // 获取IP捆绑号码
        qryData = new DataMap();
        qryData.put("USER_ID_B", user_id);
        qryData.put("RELATION_TYPE_CODE", "50");
        qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
        IDataset ox5 = getRelationUUByUidb(qryData, pagination);
        if (ox5.size() > 0)
        {
            String user_id_a = ox5.getData(0).getString("USER_ID_A", "");
            if (!"".equals(user_id_a))
            {
                qryData = new DataMap();
                qryData.put("USER_ID_A", user_id_a);
                qryData.put("RELATION_TYPE_CODE", "50");
                IDataset ox6 = getRelationUUByUida(qryData, pagination);
                str = "";
                for (int i = 0; i < ox6.size(); i++)
                {
                    if (!(((ox6.getData(i)).getString("USER_ID_B", "")).equals(user_id)))
                    {
                        qryData = new DataMap();
                        qryData.put("USER_ID", ox6.getData(i).getString("USER_ID_B", ""));
                        qryData.put("PARTITION_ID", StrUtil.getPartition4ById(ox6.getData(i).getString("USER_ID_B")));
                        IData xy = UcaInfoQry.qryUserMainProdInfoByUserId(qryData.getString("USER_ID"));
                        if (IDataUtil.isNotEmpty(xy))
                        {
                            IData o10 = xy;
                            if (o10 != null)
                            {
                                if (str.equals(""))
                                {
                                    str = o10.getString("SERIAL_NUMBER");
                                }
                                else
                                {
                                    str = str + "," + o10.getString("SERIAL_NUMBER");
                                }
                            }
                        }
                    }
                }
                param.put("BIND_SERIAL_NUMBER", str);
            }
        }

        // 获取用户归属集团信息
        qryData = new DataMap();
        qryData.put("USER_ID_B", user_id);
        qryData.put("RELATION_TYPE_CODE", "20");
        qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
        IDataset ox6 = getRelationUUByUidb(qryData, pagination);
        if (ox6.size() == 0)// *******************
        {
            qryData = new DataMap();
            qryData.put("USER_ID_B", user_id);
            qryData.put("RELATION_TYPE_CODE", "21");
            qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
            IDataset ox7 = getRelationUUByUidb(qryData, pagination);
            if (ox7.size() == 0)
            {
                qryData = new DataMap();
                qryData.put("USER_ID_B", user_id);
                qryData.put("RELATION_TYPE_CODE", "22");
                qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
                IDataset ox8 = getRelationUUByUidb(qryData, pagination);
                if (ox8.size() > 0)
                {
                    qryData = new DataMap();
                    qryData.put("USER_ID", ox8.getData(0).getString("USER_ID_A", ""));
                    qryData.put("PARTITION_ID", StrUtil.getPartition4ById(ox8.getData(0).getString("USER_ID_A", "")));
                    IData yy = UcaInfoQry.qryUserMainProdInfoByUserId(qryData.getString("USER_ID"));
                    if (IDataUtil.isNotEmpty(yy))
                    {
                        IData o11 = yy;
                        param.put("VPMN_ID", o11.getString("SERIAL_NUMBER"));// 为什么原来的程序是
                        // vpmnId=objUser.serialNumber;

                        qryData = new DataMap();
                        String grpCustId = o11.getString("CUST_ID", "");
                        IData o12 = UcaInfoQry.qryGrpInfoByCustId(grpCustId); // 从cg库查询数据
                        if (o12 != null)
                        {
                            param.put("VPMN_GROUP_NAME", o12.getString("CUST_NAME"));
                            param.put("MANAGER_STAFF_ID", o12.getString("CUST_MANAGER_ID"));
                            // param.put("GROUP_CONTACT_PHONE",
                            // com.linkage.component.util.Utility.getStaticValue(pd,
                            // "TF_F_CUST_MANAGER_STAFF", "CUST_MANAGER_ID",
                            // "SERIAL_NUMBER",
                            // o12.getString("CUST_MANAGER_ID","")));
                            param.put("RELATION_TYPE_CODE", "22");
                        }
                    }
                }
            }
            else if (ox7.size() > 0)
            {
                qryData = new DataMap();
                qryData.put("USER_ID", ox7.getData(0).getString("USER_ID_A", ""));
                qryData.put("PARTITION_ID", StrUtil.getPartition4ById(ox7.getData(0).getString("USER_ID_A", "")));
                IData zz = UcaInfoQry.qryUserMainProdInfoByUserId(qryData.getString("USER_ID"));
                if (IDataUtil.isNotEmpty(zz))
                {
                    IData o11 = zz;
                    if (o11 != null)
                        param.put("VPMN_ID", o11.getString("SERIAL_NUMBER"));// 为什么原来的程序是
                    // vpmnId=objUser.serialNumber;

                    qryData = new DataMap();
                    qryData.put("CUST_ID", o11.getString("CUST_ID", ""));
                    String grpCustId = o11.getString("CUST_ID", "");
                    IData o12 = UcaInfoQry.qryGrpInfoByCustId(grpCustId); // 从cg库查询数据
                    if (o12 != null)
                    {
                        param.put("VPMN_GROUP_NAME", o12.getString("CUST_NAME"));
                        param.put("MANAGER_STAFF_ID", o12.getString("CUST_MANAGER_ID"));
                        // jade param.put("GROUP_CONTACT_PHONE",
                        // com.linkage.component.util.Utility.getStaticValue(pd,
                        // "TF_F_CUST_MANAGER_STAFF", "CUST_MANAGER_ID",
                        // "SERIAL_NUMBER",
                        // jade o12.getString("CUST_MANAGER_ID","")));
                        param.put("RELATION_TYPE_CODE", "21");
                    }

                }
            }
        }
        else if (ox6.size() > 0)
        {
            qryData = new DataMap();
            qryData.put("USER_ID", ox6.getData(0).getString("USER_ID_A", ""));
            qryData.put("PARTITION_ID", StrUtil.getPartition4ById(ox6.getData(0).getString("USER_ID_A", "")));
            IData xx = UcaInfoQry.qryUserMainProdInfoByUserId(qryData.getString("USER_ID"));
            if (IDataUtil.isNotEmpty(xx))
            {
                IData o11 = xx;
                if (o11 != null)
                    param.put("VPMN_ID", o11.getString("SERIAL_NUMBER"));// 为什么原来的程序是
                // vpmnId=objUser.serialNumber;

                qryData = new DataMap();
                qryData.put("CUST_ID", o11.getString("CUST_ID", ""));

                String grpCustId = o11.getString("CUST_ID", "");

                IData o12 = UcaInfoQry.qryGrpInfoByCustId(grpCustId); // 从cg库查询数据
                if (o12 != null)
                {
                    param.put("VPMN_GROUP_NAME", o12.getString("CUST_NAME"));
                    param.put("MANAGER_STAFF_ID", o12.getString("CUST_MANAGER_ID"));
                    // jade param.put("GROUP_CONTACT_PHONE",
                    // com.linkage.component.util.Utility.getStaticValue(pd,
                    // "TF_F_CUST_MANAGER_STAFF", "CUST_MANAGER_ID",
                    // "SERIAL_NUMBER",
                    // jade o12.getString("CUST_MANAGER_ID","")));
                    param.put("RELATION_TYPE_CODE", "20");
                }
            }
        }

        // 获取集团成员表信息
        qryData = new DataMap();
        qryData.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));
        IDataset ox9 = getCustGroupMemberBySN(qryData, pagination);
        if (ox9.size() > 0)
        {
            if (ox9.getData(0).getString("USER_ID", "").equals(user_id))// 个人觉得这个地方有问题，现在一个号码可以在多个集团中
            {
                qryData = new DataMap();
                qryData.put("CUST_ID", ox9.getData(0).getString("CUST_ID", ""));

                String grpCustId = ox9.getData(0).getString("CUST_ID", "");
                IData o13 = UcaInfoQry.qryGrpInfoByCustId(grpCustId);

                if (o13 != null)
                {
                    param.put("X_USER_GROUP_NAME", o13.getString("CUST_NAME"));
                    param.put("ASSIGN_STAFF_ID", o13.getString("CUST_MANAGER_ID"));//
                    // jade param.put("MANAGER_PHONE",
                    // com.linkage.component.util.Utility.getStaticValue(pd,
                    // "TF_F_CUST_MANAGER_STAFF", "CUST_MANAGER_ID",
                    // "SERIAL_NUMBER",
                    // jade o13.getString("CUST_MANAGER_ID","")));
                }
            }
        }

        // 获取用户亲情号码
        qryData = new DataMap();
        qryData.put("USER_ID_B", user_id);
        qryData.put("RELATION_TYPE_CODE", "10");
        qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
        IDataset ox10 = getRelationUUByUidb(qryData, pagination);
        if (ox10.size() > 0)
        {
            qryData = new DataMap();
            qryData.put("USER_ID_A", ox10.getData(0).getString("USER_ID_A", ""));
            qryData.put("RELATION_TYPE_CODE", "10");
            qryData.put("PARTITION_ID", StrUtil.getPartition4ById(ox10.getData(0).getString("USER_ID_A", "")));
            IDataset ox11 = getRelationUUByUida(qryData, pagination);
            str = "";
            for (int i = 0; i < ox11.size(); i++)
            {
                if (!(ox11.getData(i).getString("USER_ID_B", "").equals(user_id)))
                {
                    qryData = new DataMap();
                    qryData.put("USER_ID", ox11.getData(i).getString("USER_ID_B", ""));
                    qryData.put("PARTITION_ID", StrUtil.getPartition4ById(ox11.getData(i).getString("USER_ID_B", "")));
                    IData zzz = UcaInfoQry.qryUserMainProdInfoByUserId(qryData.getString("USER_ID"));
                    if (IDataUtil.isNotEmpty(zzz))
                    {
                        IData o10 = zzz;
                        if (str.equals(""))
                        {
                            str = o10.getString("SERIAL_NUMBER");
                        }
                        else
                        {
                            str = str + "," + o10.getString("SERIAL_NUMBER");
                        }
                    }
                }
            }
            param.put("SERIAL_NUMBER_CODE", str);
        }

        // 获取用户帐户信息
        qryData = new DataMap();
        qryData.put("USER_ID", user_id);
        qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
        IDataset o14 = getPayrelationByUid(qryData, pagination);
        if (o14.size() > 0)
        {
            IData o14x = (IData) o14.get(0);

            String acctId = o14x.getString("ACCT_ID", "");
            IData o15 = UcaInfoQry.qryAcctInfoByAcctId(acctId);
            if (o15 != null)
            {
                param.put("PAY_NAME", o15.getString("PAY_NAME"));
                param.put("BANK_CODE", o15.getString("BANK_CODE"));
                param.put("BANK_ACCT_NO", o15.getString("BANK_ACCT_NO"));
                param.put("CONTRACT_NO", o15.getString("CONTRACT_NO"));
                param.put("PAY_MODE_CODE", o15.getString("PAY_MODE_CODE"));
            }
        }
        // 获取手机开通信息
        qryData = new DataMap();
        qryData.put("USER_ID", user_id);
        qryData.put("RSRV_VALUE_CODE", "PHOP");
        qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
        IDataset o15 = getPhoneOpenInfo(qryData, pagination);
        if (o15.size() > 0)
        {
            IData o15x = (IData) o15.get(0);
            param.put("PHONE_OPEN_CODE", o15x.getString("RSRV_STR1"));
            param.put("PHONE_OPEN_TIME", o15x.getString("START_DATE"));
        }
        // 判断是否是黑名单psptId
        if (!"".equals(psptId))
        {
            qryData = new DataMap();
            qryData.put("PSPT_ID", psptId);
            IDataset o16 = isBlackUser(qryData, pagination);
            // log.debug("是否是黑名单用户：：："+o16.size());
            if (o16.size() == 0)
                param.put("IS_BLACKUSER", "否");
            else
                param.put("IS_BLACKUSER", "是");
        }

        // 判断是否积分体验客户
        qryData = new DataMap();
        qryData.put("USER_ID", user_id);
        qryData.put("RSRV_VALUE_CODE", "2005");
        qryData.put("PARTITION_ID", StrUtil.getPartition4ById(user_id));
        IDataset scoreInfos = isScoreUser(qryData, pagination);
        if (scoreInfos.size() > 0)
            param.put("IS_SCOREUSER", "是");
        else
            param.put("IS_SCOREUSER", "否");

        // 判断是否是红名单
        // add by zhoumx 2009.5.24
        // add by caiyu 2010.11.18
        // 修改查询是否红名单用户，改为通过JDBC方式访问账务库。
        IData user_param = new DataMap();
        user_param.put("USER_ID", user_id);
        /*
         * 因个人代码里面不能直接访问账务数据库，只能通过调接口的方式实现。 AcctDAO adao = new AcctDAO(pd,EparchyDBHelper.getAcctEparchyDBName(pd.
         * getRouteEparchy())); boolean flag = adao.isRedUser(user_param, pd.getPagination(false)); if(flag) {
         * param.put("IS_REDUSER", "是"); } else {param.put("IS_REDUSER", "否");}
         */
        // IData user_param = new DataMap();
        // user_param.put("USER_ID", param.getString("USER_ID", ""));
        try
        {
            // IDataset red_users = TuxedoHelper.callTuxSvc(pd, "QCC_IsRedUser",user_param);
            // IDataset red_users = (IDataset) HttpHelper.callHttpSvc(pd, "QCC_IsRedUser_H", user_param);
            boolean red_users = AcctCall.checkIsRedUser(user_id);

            if (red_users)
            {
                param.put("IS_REDUSER", "是");
            }
            else
            {
                param.put("IS_REDUSER", "否");
            }
        }
        catch (Exception e)
        {
            param.put("IS_REDUSER", "");// 出了异常置空就可以了
        }

        // 增加积分接口调用
        // add by caiyu 2010.11.18
        IData score_param = new DataMap();
        score_param.put("USER_ID", user_id);
        IData userDataset = UcaInfoQry.qryUserMainProdInfoByUserId(user_id);
        if (IDataUtil.isNotEmpty(userDataset))
        {
            IData serialnumber = userDataset;
            if (serialnumber != null && !"".equals(serialnumber.getString("SERIAL_NUMBER", "")))
            {
                score_param.put("SERIAL_NUMBER", serialnumber.getString("SERIAL_NUMBER", ""));
                param.put("SCORE_VALUE", "0");
            }

        }
        String normalUserCheck = pd.getString("NORMAL_USER_CHECK", "");
        // 对于销户用户的查询过滤掉部分
        if (!normalUserCheck.equals("false"))
        {
            // 信用度截止日期
            IDataset createEndDate = AcctCall.getCreditServEndDate(user_id);
            if (createEndDate.size() > 0)
            {
                param.put("CREDIT_VALUE_ENDDATE", createEndDate.getData(0).getString("END_DATE", ""));
            }
            // 实时结余
            IData oweFee = AcctCall.getOweFeeByUserId(user_id);
            param.put("LAST_OWE_FEE", oweFee.getString("LAST_OWE_FEE", ""));
            param.put("REAL_FEE", oweFee.getString("REAL_FEE", ""));
            param.put("ACCT_BALANCE", oweFee.getString("ACCT_BALANCE", ""));
        }

        /*
         * IData createInfo = CreditCall.queryUserCreditInfos(user_id); param.put("CREDIT_CLASS", "CREDIT_CLASS");
         * param.put("CREDIT_VALUE", "CREDIT_VALUE");
         */

        // 获取用户赠送分钟
        // 原程序没有做操作
        
        

        
        
        return param;
    }

    public IDataset getUserSvc(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);//PRODUCT_ID,PACKAGE_ID 字段已经去掉了  duhj 
        parser.addSQL("SELECT PARTITION_ID,USER_ID,USER_ID_A,SERVICE_ID," + "MAIN_TAG,INST_ID,CAMPN_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,"
                + "UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5," + "RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2," + "RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_STR6,RSRV_STR7,RSRV_STR8,"
                + "RSRV_STR9,RSRV_STR10 ");
        parser.addSQL(" FROM tf_f_user_svc ");
        parser.addSQL(" WHERE main_tag = '1' ");
        parser.addSQL(" AND SYSDATE BETWEEN start_date + 0 AND end_date + 0 ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        return UserCommUtil.qryByParse(parser, pagination);
    }

    public IDataset getUserSvcCt(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);// PRODUCT_ID,PACKAGE_ID  去掉 duhj
        parser.addSQL("SELECT PARTITION_ID,USER_ID,USER_ID_A,SERVICE_ID,MAIN_TAG," + "INST_ID,CAMPN_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,"
                + "REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3," + "RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,"
                + "RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10");
        parser.addSQL(" FROM tf_f_user_svc ");
        parser.addSQL(" WHERE service_id IN ('13','14','15') ");
        parser.addSQL(" AND SYSDATE BETWEEN start_date AND end_date ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        return UserCommUtil.qryByParse(parser, pagination);
    }

    public IDataset getUserSvcMy(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");
        String partition_id = data.getString("PARTITION_ID", "");
        if ("".equals(user_id) || "".equals(partition_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(data);//PRODUCT_ID,PACKAGE_ID  去掉 duhj
        parser.addSQL("SELECT PARTITION_ID,USER_ID,USER_ID_A,SERVICE_ID,MAIN_TAG," + "INST_ID,CAMPN_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,"
                + "REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3," + "RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,"
                + "RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10");
        parser.addSQL(" FROM tf_f_user_svc ");
        parser.addSQL(" WHERE service_id IN ('16','17','18','19') ");
        parser.addSQL(" AND SYSDATE BETWEEN start_date AND end_date ");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND PARTITION_ID = :PARTITION_ID ");
        return UserCommUtil.qryByParse(parser, pagination);
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
        return UserCommUtil.qryByParse(parser, pagination);
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
        IDataset outlist = UserCommUtil.qryByParse(parser, pagination);
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
        // log.debug("****************************************************");

        String psptid = data.getString("PSPT_ID", "").trim();

        // log.debug("****************************************************");
        if ("".equals(psptid))
        {
            return new DatasetList();
        }
        else
        {
            data.put("PSPT_ID", psptid);
            SQLParser parser = new SQLParser(data);
            parser.addSQL("SELECT 1 FROM td_o_blackuser ");
            parser.addSQL(" WHERE START_DATE <= sysdate AND END_DATE >= sysdate ");
            parser.addSQL(" AND ROWNUM=1 AND PSPT_ID = :PSPT_ID ");
            IDataset outlist = UserCommUtil.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
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

        parser.addSQL("SELECT partition_id, user_id, rsrv_value_code, rsrv_value, rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5, rsrv_str6, rsrv_str7, rsrv_str8, rsrv_str9, rsrv_str10, start_date, end_date");
        parser.addSQL("   FROM tf_f_user_other ");
        parser.addSQL("  WHERE sysdate BETWEEN start_date  AND end_date ");
        parser.addSQL("    AND user_id = :USER_ID ");
        parser.addSQL("    AND rsrv_value_code = :RSRV_VALUE_CODE ");
        parser.addSQL("    AND partition_id = :PARTITION_ID  ");

        IDataset outlist = UserCommUtil.qryByParse(parser, pagination);
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
        parser.addSQL("remove_reason_code ");
        parser.addSQL(" FROM tf_f_user");
        parser.addSQL(" WHERE remove_tag not in ('0','5') ");
        parser.addSQL(" AND serial_number = :SERIAL_NUMBER ");

        return UserCommUtil.qryByParse(parser, pagination);
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
        parser.addSQL("remove_reason_code ");
        parser.addSQL(" FROM tf_f_user");
        parser.addSQL(" WHERE remove_tag != '0' ");
        parser.addSQL(" AND serial_number = :SERIAL_NUMBER ");
        parser.addSQL(" AND destroy_time = (SELECT MAX(destroy_time) FROM tf_f_user ");
        parser.addSQL("                        WHERE serial_number = :SERIAL_NUMBER ");
        parser.addSQL("                          AND remove_tag != '0') ");
        parser.addSQL(" AND NOT EXISTS (SELECT 1 FROM tf_f_user ");
        parser.addSQL("                    WHERE serial_number = :SERIAL_NUMBER ");
        parser.addSQL("                      AND remove_tag = '0')");

        return UserCommUtil.qryByParse(parser, pagination);
    }

    // 根据acct_id查询付费关系
    public IDataset qryPayRelationByAid(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT partition_id,user_id,acct_id,payitem_code,acct_priority,");
        parser.addSQL(" user_priority,bind_type,START_CYCLE_ID,END_CYCLE_ID,default_tag,act_tag,limit_type,limit,");
        parser.addSQL(" complement_tag ");
        parser.addSQL("  FROM tf_a_payrelation a ");
        parser.addSQL("  WHERE a.acct_id= :ACCT_ID ");
        parser.addSQL("  AND a.default_tag= :DEFAULT_TAG ");
        parser.addSQL("  AND a.act_tag= :ACT_TAG ");

        // modify by caiy , td_a_acycpara表里面没有数据，不能用来做条件
        parser.addSQL(" AND to_char(sysdate,'yyyymm') between START_CYCLE_ID and END_CYCLE_ID ");
        // parser.addSQL(" AND (select acyc_id from TD_A_ACYCPARA ");
        // parser.addSQL(" where acyc_start_time <= SYSDATE and acyc_end_time >= SYSDATE) ");
        // parser.addSQL(" between a.start_acyc_id and a.end_acyc_id ");
        parser.addSQL("   AND NOT EXISTS (SELECT 1 FROM tf_f_user ");
        parser.addSQL("                   WHERE user_id = a.user_id ");
        parser.addSQL("                   AND partition_id = MOD(a.user_id,10000) ");
        parser.addSQL("                   AND brand_code LIKE '%IP%')");
        return UserCommUtil.qryByParse(parser, pagination);
    }

    // 根据user_id查询付费关系
    public IData qryPayRelationByUid(IData data, Pagination pagination) throws Exception
    {
        if ("".equals(data.getString("USER_ID", "")))
        { // 强校验，防止返回大数据结果集和不正确结果
            return null;
        }
        SQLParser parser = new SQLParser(data);

        parser.addSQL("SELECT partition_id,user_id,acct_id,payitem_code,acct_priority,user_priority,bind_type,START_CYCLE_ID, ");
        parser.addSQL(" END_CYCLE_ID,default_tag,act_tag,limit_type,limit,complement_tag ");
        parser.addSQL(" FROM tf_a_payrelation ");
        parser.addSQL(" WHERE default_tag='1' ");// 默认帐户
        parser.addSQL(" AND user_id= :USER_ID ");
        parser.addSQL(" AND partition_id=MOD(:USER_ID,10000) ");
        parser.addSQL(" AND act_tag='1' "); // 作用标识，1作用
        // modify by caiy , td_a_acycpara表里面没有数据，不能用来做条件
        parser.addSQL(" AND to_char(sysdate,'yyyymm') between START_CYCLE_ID and END_CYCLE_ID ");
        parser.addSQL(" and rownum <= 1 ");
        IDataset outlist = UserCommUtil.qryByParse(parser, pagination);
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
        parser.addSQL("remove_reason_code ");
        parser.addSQL(" FROM tf_f_user");
        parser.addSQL(" WHERE remove_tag='0' ");
        parser.addSQL(" AND cust_id = :CUST_ID ");

        return UserCommUtil.qryByParse(parser, pagination);
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
        parser.addSQL("remove_reason_code ");
        parser.addSQL(" FROM tf_f_user");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND serial_number=:SERIAL_NUMBER ");
        parser.addSQL(" AND remove_tag=:REMOVE_TAG ");

        return UserCommUtil.qryByParse(parser, pagination);
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

        return UserCommUtil.qryByParse(parser);
    }
    
    public IDataset qryUserInfoFromHis(String userId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT a.partition_id,a.user_id,a.cust_id,a.usecust_id,a.eparchy_code,a.city_code,a.city_code_a,a.user_passwd,a.user_diff_code, ");
        parser.addSQL(" a.user_type_code,a.user_tag_set,a.user_state_codeset,a.net_type_code,a.serial_number,a.contract_id,a.acct_tag, ");
        parser.addSQL(" a.prepay_tag,a.mpute_month_fee,a.mpute_date,a.first_call_time,a.last_stop_time,a.changeuser_date,a.in_net_mode, ");
        parser.addSQL(" a.in_date,a.in_staff_id,a.in_depart_id,a.open_mode,a.open_date,a.open_staff_id,a.open_depart_id,a.develop_staff_id, ");
        parser.addSQL(" a.develop_date,a.develop_depart_id,a.develop_city_code,a.develop_eparchy_code,a.develop_no,a.assure_cust_id, ");
        parser.addSQL(" a.assure_type_code,a.assure_date,a.remove_tag,a.pre_destroy_time,a.destroy_time,a.remove_eparchy_code,a.remove_city_code,a.remove_depart_id, ");
        parser.addSQL(" a.remove_reason_code,a.update_time,a.update_staff_id,a.update_depart_id,a.remark,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3 ");
        parser.addSQL(" ,a.rsrv_num4,a.rsrv_num5,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_str6,a.rsrv_str7,a.rsrv_str8  ");
        parser.addSQL(" ,a.rsrv_str9,a.rsrv_str10,a.rsrv_date1,a.rsrv_date2,a.rsrv_date3,a.rsrv_tag1,a.rsrv_tag2,a.rsrv_tag3  ");
        parser.addSQL(" from tf_fh_user a ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" AND a.USER_ID = :USER_ID ");
        parser.addSQL(" AND a.partition_id = MOD(:USER_ID,10000) ");
        return UserCommUtil.qryByParse(parser);
    }
    
}
