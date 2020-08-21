
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * @author wusf
 */
public class GroupBBossManageDao
{

    /**
     * chenyi 查看预受理阶段的trade数据
     * 
     * @param param
     * @throws Exception
     */
    public static IDataset queryBbossTradeByEsop(String product_id, String group_id, String uids) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", product_id);
        param.put("GROUP_ID", group_id);
        param.put("UIDS", uids);
        return Dao.qryByCode("TF_B_TRADE", "SEL_TRADE_GRPIDPID", param,Route.getJourDb());

    }

    /**
     * @Description 根据user表中更改属性表中的状态
     * @author chenyi
     * @date
     * @param param
     * @return IDataset
     * @throws Exception
     */
    public static void updateBbossAttrStateByUID(String rsrv_str1, String user_id, String attr_code, String modify_tag, String attr_value) throws Exception
    {

        IData param = new DataMap();
        param.put("RSRV_STR1", rsrv_str1);
        param.put("USER_ID", user_id);
        param.put("ATTR_CODE", attr_code);
        param.put("MODIFY_TAG", modify_tag);
        param.put("ATTR_VALUE", attr_value);

        Dao.executeUpdateByCodeCode("TF_B_TRADE_ATTR", "UPDATE_STATE_BYUSERID", param,Route.getJourDb());
    }

    /**
     * 构造函数
     * 
     * @throws Exception
     */
    public GroupBBossManageDao() throws Exception
    {

        super();
    }

    /**
     * @param param
     * @throws Exception
     */
    public int copyTradeAcct(IData param) throws Exception
    {

        param.put("MODIFY_TAG", "0");
        // 如果商品用户已经生成，设置tradeMerch的modifyTag为2
        if ("true".equals(param.getString("MERCH_EXISTS", "")))
        {
            param.put("MODIFY_TAG", "2");
        }

        SQLParser parser = new SQLParser(param);
        parser.addSQL("  insert into tf_b_trade_account (TRADE_ID, ACCEPT_MONTH, ACCT_ID, CUST_ID, PAY_NAME, PAY_MODE_CODE, ACCT_DIFF_CODE, ACCT_PASSWD, ACCT_TAG,");
        parser.addSQL("  NET_TYPE_CODE, EPARCHY_CODE, CITY_CODE, BANK_CODE, BANK_ACCT_NO, SCORE_VALUE, CREDIT_CLASS_ID, BASIC_CREDIT_VALUE, CREDIT_VALUE, DEBUTY_USER_ID,");
        parser.addSQL("  DEBUTY_CODE, CONTRACT_NO, DEPOSIT_PRIOR_RULE_ID, ITEM_PRIOR_RULE_ID, OPEN_DATE, REMOVE_TAG, REMOVE_DATE, MODIFY_TAG, UPDATE_STAFF_ID,");
        parser.addSQL("  UPDATE_DEPART_ID, UPDATE_TIME, REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10)");
        parser.addSQL("  SELECT :NEW_TRADE_ID, TO_NUMBER(SUBSTR(:NEW_TRADE_ID, 5, 2)), ACCT_ID, CUST_ID, PAY_NAME, PAY_MODE_CODE, ACCT_DIFF_CODE, ACCT_PASSWD, ACCT_TAG,");
        parser.addSQL("  NET_TYPE_CODE, EPARCHY_CODE, CITY_CODE, BANK_CODE, BANK_ACCT_NO, SCORE_VALUE, CREDIT_CLASS_ID, BASIC_CREDIT_VALUE, CREDIT_VALUE, DEBUTY_USER_ID,");
        parser.addSQL("  DEBUTY_CODE, CONTRACT_NO, DEPOSIT_PRIOR_RULE_ID, ITEM_PRIOR_RULE_ID, OPEN_DATE, REMOVE_TAG, REMOVE_DATE, :MODIFY_TAG, UPDATE_STAFF_ID, ");
        parser.addSQL("  UPDATE_DEPART_ID, UPDATE_TIME, REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10");
        parser.addSQL("  from tf_b_trade_account t WHERE t.TRADE_ID=:TRADE_ID");

        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 生成主台账，trade_type_code为变更
     * 
     * @param param
     * @throws Exception
     */
    public int copyTradeMain(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("INSERT INTO TF_B_TRADE(TRADE_ID, ACCEPT_MONTH, BATCH_ID, ORDER_ID, PROD_ORDER_ID, BPM_ID, CAMPN_ID, TRADE_TYPE_CODE, PRIORITY, SUBSCRIBE_TYPE, "
                + "SUBSCRIBE_STATE, NEXT_DEAL_TAG, IN_MODE_CODE, CUST_ID, CUST_NAME, USER_ID, ACCT_ID, SERIAL_NUMBER, NET_TYPE_CODE, EPARCHY_CODE, CITY_CODE, "
                + "PRODUCT_ID, BRAND_CODE,CUST_ID_B, USER_ID_B, ACCT_ID_B, SERIAL_NUMBER_B, CUST_CONTACT_ID, SERV_REQ_ID,INTF_ID,ACCEPT_DATE, TRADE_STAFF_ID, "
                + "TRADE_DEPART_ID, TRADE_CITY_CODE, TRADE_EPARCHY_CODE, TERM_IP, OPER_FEE, FOREGIFT, ADVANCE_PAY, INVOICE_NO, FEE_STATE, FEE_TIME, FEE_STAFF_ID, "
                + "PROCESS_TAG_SET, OLCOM_TAG, FINISH_DATE, EXEC_TIME, EXEC_ACTION, EXEC_RESULT, EXEC_DESC, CANCEL_TAG, CANCEL_DATE, CANCEL_STAFF_ID, CANCEL_DEPART_ID, "
                + "CANCEL_CITY_CODE, CANCEL_EPARCHY_CODE, UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, " + "RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10)");

        parser.addSQL(" SELECT :NEW_TRADE_ID, TO_NUMBER(SUBSTR(:NEW_TRADE_ID, 5, 2)), BATCH_ID, ORDER_ID, PROD_ORDER_ID, BPM_ID, CAMPN_ID, :NEW_TRADE_TYPE_CODE, PRIORITY, SUBSCRIBE_TYPE, "
                + "SUBSCRIBE_STATE, NEXT_DEAL_TAG, IN_MODE_CODE, CUST_ID, CUST_NAME, USER_ID, ACCT_ID, SERIAL_NUMBER, NET_TYPE_CODE, EPARCHY_CODE, CITY_CODE, "
                + "PRODUCT_ID, BRAND_CODE,CUST_ID_B, USER_ID_B, ACCT_ID_B, SERIAL_NUMBER_B, CUST_CONTACT_ID, SERV_REQ_ID,INTF_ID,ACCEPT_DATE, TRADE_STAFF_ID, "
                + "TRADE_DEPART_ID, TRADE_CITY_CODE, TRADE_EPARCHY_CODE, TERM_IP, OPER_FEE, FOREGIFT, ADVANCE_PAY, INVOICE_NO, FEE_STATE, FEE_TIME, FEE_STAFF_ID, "
                + "PROCESS_TAG_SET, OLCOM_TAG, FINISH_DATE, EXEC_TIME, EXEC_ACTION, EXEC_RESULT, EXEC_DESC, CANCEL_TAG, CANCEL_DATE, CANCEL_STAFF_ID, CANCEL_DEPART_ID, "
                + "CANCEL_CITY_CODE, CANCEL_EPARCHY_CODE, sysdate, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5," + " RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10");

        parser.addSQL(" FROM TF_B_TRADE t WHERE t.TRADE_ID=:TRADE_ID");

        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 生成一个修改的merch
     * 
     * @param param
     * @throws Exception
     */
    public int copyTradeMerch(IData param) throws Exception
    {

        param.put("MODIFY_TAG", "0");
        // 如果商品用户已经生成，设置tradeMerch的modifyTag为2
        if ("true".equals(param.getString("MERCH_EXISTS", "")))
        {
            param.put("MODIFY_TAG", "2");
        }
        SQLParser parser = new SQLParser(param);
        parser.addSQL("INSERT INTO TF_B_TRADE_GRP_MERCH(INST_ID, ACCEPT_MONTH, TRADE_ID, USER_ID, SERIAL_NUMBER, MERCH_SPEC_CODE, MERCH_ORDER_ID, MERCH_OFFER_ID, "
                + "GROUP_ID, OPR_SOURCE, BIZ_MODE, STATUS, HOST_COMPANY, START_DATE, END_DATE, MODIFY_TAG, UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, "
                + "REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_DATE1, RSRV_DATE2, " + "RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3)");
        parser.addSQL("SELECT INST_ID, TO_NUMBER(SUBSTR(:NEW_TRADE_ID, 5, 2)), :NEW_TRADE_ID, USER_ID, SERIAL_NUMBER, MERCH_SPEC_CODE, MERCH_ORDER_ID, MERCH_OFFER_ID, GROUP_ID, OPR_SOURCE, "
                + "BIZ_MODE, STATUS, HOST_COMPANY, START_DATE, END_DATE, :MODIFY_TAG, sysdate, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, "
                + "RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_TAG1, " + "RSRV_TAG2, RSRV_TAG3");
        parser.addSQL(" FROM TF_B_TRADE_GRP_MERCH t2 WHERE t2.TRADE_ID=:TRADE_ID");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 生成修改的merch对应的USER
     * 
     * @param param
     * @throws Exception
     */
    public int copyTradeMerchUser(IData param) throws Exception
    {

        param.put("MODIFY_TAG", "0");
        // 如果商品用户已经生成，设置tradeMerch的modifyTag为2
        if ("true".equals(param.getString("MERCH_EXISTS", "")))
        {
            param.put("MODIFY_TAG", "2");
        }
        SQLParser parser = new SQLParser(param);
        parser.addSQL("INSERT INTO TF_B_TRADE_USER(TRADE_ID, ACCEPT_MONTH, USER_ID, CUST_ID, USECUST_ID, BRAND_CODE, PRODUCT_ID, EPARCHY_CODE, CITY_CODE, "
                + "CITY_CODE_A, USER_PASSWD, USER_DIFF_CODE, USER_TYPE_CODE, USER_TAG_SET, USER_STATE_CODESET, NET_TYPE_CODE, SERIAL_NUMBER, SCORE_VALUE, CONTRACT_ID, "
                + "CREDIT_CLASS, BASIC_CREDIT_VALUE, CREDIT_VALUE, CREDIT_CONTROL_ID, ACCT_TAG, PREPAY_TAG, MPUTE_MONTH_FEE, MPUTE_DATE, FIRST_CALL_TIME, LAST_STOP_TIME,"
                + " CHANGEUSER_DATE, IN_NET_MODE, IN_DATE, IN_STAFF_ID, IN_DEPART_ID, OPEN_MODE, OPEN_DATE, OPEN_STAFF_ID, OPEN_DEPART_ID, DEVELOP_STAFF_ID, DEVELOP_DATE, "
                + "DEVELOP_DEPART_ID, DEVELOP_CITY_CODE, DEVELOP_EPARCHY_CODE, DEVELOP_NO, ASSURE_CUST_ID, ASSURE_TYPE_CODE, ASSURE_DATE, REMOVE_TAG, PRE_DESTROY_TIME, "
                + "DESTROY_TIME, REMOVE_EPARCHY_CODE, REMOVE_CITY_CODE, REMOVE_DEPART_ID, REMOVE_REASON_CODE, MODIFY_TAG, UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID,"
                + " REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, "
                + "RSRV_STR8, RSRV_STR9, RSRV_STR10, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3)");

        parser.addSQL(" SELECT :NEW_TRADE_ID, TO_NUMBER(SUBSTR(:NEW_TRADE_ID, 5, 2)), USER_ID, CUST_ID, USECUST_ID, BRAND_CODE, PRODUCT_ID, EPARCHY_CODE, CITY_CODE, CITY_CODE_A, USER_PASSWD, "
                + "USER_DIFF_CODE, USER_TYPE_CODE, USER_TAG_SET, USER_STATE_CODESET, NET_TYPE_CODE, SERIAL_NUMBER, SCORE_VALUE, CONTRACT_ID, CREDIT_CLASS, "
                + "BASIC_CREDIT_VALUE, CREDIT_VALUE, CREDIT_CONTROL_ID, ACCT_TAG, PREPAY_TAG, MPUTE_MONTH_FEE, MPUTE_DATE, FIRST_CALL_TIME, LAST_STOP_TIME, "
                + "CHANGEUSER_DATE, IN_NET_MODE, IN_DATE, IN_STAFF_ID, IN_DEPART_ID, OPEN_MODE, OPEN_DATE, OPEN_STAFF_ID, OPEN_DEPART_ID, DEVELOP_STAFF_ID, "
                + "DEVELOP_DATE, DEVELOP_DEPART_ID, DEVELOP_CITY_CODE, DEVELOP_EPARCHY_CODE, DEVELOP_NO, ASSURE_CUST_ID, ASSURE_TYPE_CODE, ASSURE_DATE, REMOVE_TAG, "
                + "PRE_DESTROY_TIME, DESTROY_TIME, REMOVE_EPARCHY_CODE, REMOVE_CITY_CODE, REMOVE_DEPART_ID, REMOVE_REASON_CODE,  :MODIFY_TAG, sysdate, "
                + "UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, "
                + "RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3");

        parser.addSQL(" FROM TF_B_TRADE_USER t WHERE t.TRADE_ID=:TRADE_ID ");
        parser.addSQL(" AND EXISTS (SELECT 1 FROM TF_B_TRADE_GRP_MERCH t2 WHERE T2.TRADE_ID=:TRADE_ID AND T2.USER_ID=T.USER_ID)");

        return Dao.executeUpdate(parser,Route.getJourDb());

    }

    /**
     * 生成合同,审批,联系人信息 trade_type_code为变更
     * 
     * @param param
     * @throws Exception
     */
    public int copyTradeOther(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("insert into tf_b_trade_other (TRADE_ID, ACCEPT_MONTH, USER_ID, RSRV_VALUE_CODE, RSRV_VALUE, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_NUM6, RSRV_NUM7, "
                + "RSRV_NUM8, RSRV_NUM9, RSRV_NUM10, RSRV_NUM11, RSRV_NUM12, RSRV_NUM13, RSRV_NUM14, RSRV_NUM15, RSRV_NUM16, RSRV_NUM17, RSRV_NUM18, RSRV_NUM19, RSRV_NUM20, RSRV_STR1, RSRV_STR2, RSRV_STR3, "
                + "RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10, RSRV_STR11, RSRV_STR12, RSRV_STR13, RSRV_STR14, RSRV_STR15, RSRV_STR16, RSRV_STR17, RSRV_STR18, RSRV_STR19, RSRV_STR20,"
                + " RSRV_STR21, RSRV_STR22, RSRV_STR23, RSRV_STR24, RSRV_STR25, RSRV_STR26, RSRV_STR27, RSRV_STR28, RSRV_STR29, RSRV_STR30, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_DATE4, RSRV_DATE5, RSRV_DATE6, RSRV_DATE7, "
                + "RSRV_DATE8, RSRV_DATE9, RSRV_DATE10, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3, RSRV_TAG4, RSRV_TAG5, RSRV_TAG6, RSRV_TAG7, RSRV_TAG8, RSRV_TAG9, RSRV_TAG10, PROCESS_TAG, STAFF_ID, DEPART_ID, START_DATE, END_DATE,"
                + " MODIFY_TAG, UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, INST_ID)");

        parser.addSQL(" SELECT :NEW_TRADE_ID, TO_NUMBER(SUBSTR(:NEW_TRADE_ID, 5, 2)), USER_ID, RSRV_VALUE_CODE, RSRV_VALUE, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_NUM6, RSRV_NUM7, "
                + "RSRV_NUM8, RSRV_NUM9, RSRV_NUM10, RSRV_NUM11, RSRV_NUM12, RSRV_NUM13, RSRV_NUM14, RSRV_NUM15, RSRV_NUM16, RSRV_NUM17, RSRV_NUM18, RSRV_NUM19, RSRV_NUM20, RSRV_STR1, RSRV_STR2, RSRV_STR3, "
                + "RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10, RSRV_STR11, RSRV_STR12, RSRV_STR13, RSRV_STR14, RSRV_STR15, RSRV_STR16, RSRV_STR17, RSRV_STR18, RSRV_STR19, RSRV_STR20,"
                + " RSRV_STR21, RSRV_STR22, RSRV_STR23, RSRV_STR24, RSRV_STR25, RSRV_STR26, RSRV_STR27, RSRV_STR28, RSRV_STR29, RSRV_STR30, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_DATE4, RSRV_DATE5, RSRV_DATE6, RSRV_DATE7, "
                + "RSRV_DATE8, RSRV_DATE9, RSRV_DATE10, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3, RSRV_TAG4, RSRV_TAG5, RSRV_TAG6, RSRV_TAG7, RSRV_TAG8, RSRV_TAG9, RSRV_TAG10, PROCESS_TAG, STAFF_ID, DEPART_ID, START_DATE, END_DATE,"
                + " MODIFY_TAG, sysdate, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, INST_ID");

        parser.addSQL(" FROM TF_B_TRADE_OTHER t WHERE t.TRADE_ID=:TRADE_ID AND T.RSRV_VALUE_CODE IN('ATT_INFOS','AUDITOR_INFOS','CONTACTOR_INFOS')");

        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * @Description 更改属性表中的数据
     * @author jch
     * @date
     * @param param
     * @return IDataset
     * @throws Exception
     */

    public void updateBbossAttr(IData param) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_B_TRADE_ATTR", "UPDATE_BY_PK", param,Route.getJourDb());
    }

    /**
     * 更新没选中的merchp的trade_id
     * 
     * @param param
     * @throws Exception
     */
    public int updateTradeAcct(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("update TF_B_TRADE_ACCOUNT T");
        parser.addSQL(" set T.Modify_Tag =:MODIFY_TAG");
        parser.addSQL(" where 1=1");
        parser.addSQL(" AND T.TRADE_ID=:TRADE_ID");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 更新没选中USER对应的USER_ATTR
     * 
     * @param param
     * @throws Exception
     */
    public int updateTradeAttr(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_ATTR t SET T.TRADE_ID=:NEW_TRADE_ID WHERE T.TRADE_ID=:TRADE_ID");
        parser.addSQL(" AND T.USER_ID NOT IN(" + param.getString("USER_IDS") + ")");
        parser.addSQL(" AND NOT EXISTS (SELECT 1 FROM TF_B_TRADE_GRP_MERCH t2 WHERE T2.TRADE_ID=:TRADE_ID AND T2.USER_ID=T.USER_ID)");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 更新没选中USER对应的DISCNT
     * 
     * @param param
     * @throws Exception
     */
    public int updateTradeDiscnt(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_DISCNT t SET T.TRADE_ID=:NEW_TRADE_ID WHERE T.TRADE_ID=:TRADE_ID");
        parser.addSQL(" AND T.USER_ID NOT IN(" + param.getString("USER_IDS") + ")");
        parser.addSQL(" AND NOT EXISTS (SELECT 1 FROM TF_B_TRADE_GRP_MERCH t2 WHERE T2.TRADE_ID=:TRADE_ID AND T2.USER_ID=T.USER_ID)");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 更新没选中的merchp的trade_id
     * 
     * @param param
     * @throws Exception
     */
    public int updateTradeMerch(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_GRP_MERCH t ");
        parser.addSQL(" set T.Modify_Tag =:MODIFY_TAG");
        parser.addSQL(" where 1=1");
        parser.addSQL(" AND T.TRADE_ID=:TRADE_ID");
        parser.addSQL(" AND T.USER_ID=:USER_ID");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 更新没选中的merchp的trade_id
     * 
     * @param param
     * @throws Exception
     */
    public int updateTradeMerchp(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_GRP_MERCHP t SET T.TRADE_ID=:NEW_TRADE_ID WHERE T.TRADE_ID=:TRADE_ID");
        parser.addSQL(" AND T.USER_ID NOT IN(" + param.getString("USER_IDS") + ")");
        parser.addSQL(" AND NOT EXISTS (SELECT 1 FROM TF_B_TRADE_GRP_MERCH t2 WHERE T2.TRADE_ID=:TRADE_ID AND T2.USER_ID=T.USER_ID)");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 更新没选中的merchp的merchp_discnt
     * 
     * @param param
     * @throws Exception
     */
    public int updateTradeMerchpDiscnt(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_GRP_MERCHP_DISCNT t SET T.TRADE_ID=:NEW_TRADE_ID WHERE T.TRADE_ID=:TRADE_ID");
        parser.addSQL(" AND T.USER_ID NOT IN(" + param.getString("USER_IDS") + ")");
        parser.addSQL(" AND NOT EXISTS (SELECT 1 FROM TF_B_TRADE_GRP_MERCH t2 WHERE T2.TRADE_ID=:TRADE_ID AND T2.USER_ID=T.USER_ID)");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 更新没选中的merchp的trade_id
     * 
     * @param param
     * @throws Exception
     */
    public int updateTradeMerchUser(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("update TF_B_TRADE_USER T");
        parser.addSQL(" set T.Modify_Tag =:MODIFY_TAG");
        parser.addSQL(" where 1=1");
        parser.addSQL(" AND T.TRADE_ID=:TRADE_ID");
        parser.addSQL(" AND T.USER_ID=:USER_ID");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 更新没选中USER对应的PayRelaction
     * 
     * @param param
     * @throws Exception
     */
    public int updateTradePayRelaction(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_PAYRELATION t SET T.TRADE_ID=:NEW_TRADE_ID WHERE T.TRADE_ID=:TRADE_ID");
        parser.addSQL(" AND T.USER_ID NOT IN(" + param.getString("USER_IDS") + ")");
        parser.addSQL(" AND NOT EXISTS (SELECT 1 FROM TF_B_TRADE_GRP_MERCH t2 WHERE T2.TRADE_ID=:TRADE_ID AND T2.USER_ID=T.USER_ID)");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 更新没选中USER对应的RES
     * 
     * @param param
     * @throws Exception
     */
    public int updateTradeProduct(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_PRODUCT t SET T.TRADE_ID=:NEW_TRADE_ID WHERE T.TRADE_ID=:TRADE_ID");
        parser.addSQL(" AND T.USER_ID NOT IN(" + param.getString("USER_IDS") + ")");
        parser.addSQL(" AND NOT EXISTS (SELECT 1 FROM TF_B_TRADE_GRP_MERCH t2 WHERE T2.TRADE_ID=:TRADE_ID AND T2.USER_ID=T.USER_ID)");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 更新没选中USER对应的RELATION
     * 
     * @param param
     * @throws Exception
     */
    public int updateTradeRelaction(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_RELATION t SET T.TRADE_ID=:NEW_TRADE_ID WHERE T.TRADE_ID=:TRADE_ID");
        parser.addSQL(" AND T.USER_ID_B NOT IN(" + param.getString("USER_IDS") + ")");
        parser.addSQL(" AND EXISTS (SELECT 1 FROM TF_B_TRADE_GRP_MERCH t2 WHERE T2.TRADE_ID=:TRADE_ID AND T.USER_ID_A = T2.USER_ID)");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 更新没选中USER对应的RES
     * 
     * @param param
     * @throws Exception
     */
    public int updateTradeRes(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_RES t SET T.TRADE_ID=:NEW_TRADE_ID WHERE T.TRADE_ID=:TRADE_ID");
        parser.addSQL(" AND T.USER_ID NOT IN(" + param.getString("USER_IDS") + ")");
        parser.addSQL(" AND NOT EXISTS (SELECT 1 FROM TF_B_TRADE_GRP_MERCH t2 WHERE T2.TRADE_ID=:TRADE_ID AND T2.USER_ID=T.USER_ID)");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 更新没选中USER对应的SVC
     * 
     * @param param
     * @throws Exception
     */
    public int updateTradeSvc(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_SVC t SET T.TRADE_ID=:NEW_TRADE_ID WHERE T.TRADE_ID=:TRADE_ID");
        parser.addSQL(" AND T.USER_ID NOT IN(" + param.getString("USER_IDS") + ")");
        parser.addSQL(" AND NOT EXISTS (SELECT 1 FROM TF_B_TRADE_GRP_MERCH t2 WHERE T2.TRADE_ID=:TRADE_ID AND T2.USER_ID=T.USER_ID)");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 更新没选中USER对应的SVCSTATE
     * 
     * @param param
     * @throws Exception
     */
    public int updateTradeSvcState(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_SVCSTATE t SET T.TRADE_ID=:NEW_TRADE_ID WHERE T.TRADE_ID=:TRADE_ID");
        parser.addSQL(" AND T.USER_ID NOT IN(" + param.getString("USER_IDS") + ")");
        parser.addSQL(" AND NOT EXISTS (SELECT 1 FROM TF_B_TRADE_GRP_MERCH t2 WHERE T2.TRADE_ID=:TRADE_ID AND T2.USER_ID=T.USER_ID)");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 更新没选中的merchp的trade_id
     * 
     * @param param
     * @throws Exception
     */
    public int updateTradeTypeCode(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("update TF_B_TRADE T");
        parser.addSQL(" set T.TRADE_TYPE_CODE =:TRADE_TYPE_CODE");
        parser.addSQL(" where 1=1");
        parser.addSQL(" AND T.TRADE_ID=:TRADE_ID");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

    /**
     * 给没有选中的trade_user新的trade_id
     * 
     * @param param
     * @throws Exception
     */
    public int updateTradeUser(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_B_TRADE_USER t SET T.TRADE_ID=:NEW_TRADE_ID WHERE T.TRADE_ID=:TRADE_ID");
        parser.addSQL(" AND T.USER_ID NOT IN(" + param.getString("USER_IDS") + ")");
        parser.addSQL(" AND NOT EXISTS (SELECT 1 FROM TF_B_TRADE_GRP_MERCH t2 WHERE T2.TRADE_ID=:TRADE_ID AND T2.USER_ID=T.USER_ID)");
        return Dao.executeUpdate(parser,Route.getJourDb());
    }

}
