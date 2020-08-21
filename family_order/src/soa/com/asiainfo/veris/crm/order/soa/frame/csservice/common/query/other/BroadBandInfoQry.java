
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class BroadBandInfoQry
{
    /**
     * 判断改帐号是否在军区帐号表中已经存在
     * 
     * @param STATE
     * @param accountId
     * @return
     * @throws Exception
     */
    public static int checkAccountIdInAccountIp(String STATE, String accountId) throws Exception
    {
        int num = 0;
        IData param = new DataMap();
        param.put("STATE", STATE);
        param.put("ACCOUNT_ID", accountId);
        IDataset result = Dao.qryByCode("TD_B_ACCOUNT_IP", "SEL_COUNT_BY_ACCTID", param, Route.CONN_CRM_CEN);
        if (result != null && result.size() > 0)
        {
            num = Integer.parseInt(result.getData(0).getString("NUM"));
        }
        return num;
    }

    /**
     * 通过绑定的手机号码查询宽带信息
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset getBroadBandAccessAcctBySerialNumber(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT INST_ID,USER_ID,SERIAL_NUMBER,ACCESS_TYPE,ACCESS_ACCT,ACCESS_PWD,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,REMARK,RSRV_STR1 ");
        sql.append(" FROM TF_F_USER_ACCESS_ACCT WHERE SERIAL_NUMBER=:SERIAL_NUMBER AND SYSDATE BETWEEN START_DATE AND END_DATE AND SERIAL_NUMBER<>ACCESS_ACCT");

        return Dao.qryBySql(sql, param);
    }

    public static IDataset getBroadBandAccessAcctByUserId(String userId) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);

        String sql = "SELECT INST_ID,USER_ID,SERIAL_NUMBER,ACCESS_TYPE,ACCESS_ACCT,ACCESS_PWD,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,REMARK,RSRV_STR1 "
                + " FROM TF_F_USER_ACCESS_ACCT WHERE USER_ID=:USER_ID AND PARTITION_ID=MOD(:USER_ID,10000) AND SYSDATE BETWEEN START_DATE AND END_DATE";

        return Dao.qryBySql(new StringBuilder(sql), param);
    }

    public static IDataset getBroadBandAccessAcctDatasByUserId(UcaData ucaData) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", ucaData.getUser().getUserId());

        String sql = "SELECT INST_ID,USER_ID,SERIAL_NUMBER,ACCESS_TYPE,ACCESS_ACCT,ACCESS_PWD,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,REMARK,RSRV_STR1 "
                + " FROM TF_F_USER_ACCESS_ACCT WHERE USER_ID=:USER_ID AND PARTITION_ID=MOD(:USER_ID,10000) AND SYSDATE BETWEEN START_DATE AND END_DATE";

        return Dao.qryBySql(new StringBuilder(sql), param);
    }

    public static IDataset getBroadBandBaseUserInfo(IData param) throws Exception
    {

        String sql = "SELECT P.PSPT_TYPE_CODE,P.PSPT_ID,P.CUST_NAME,P.PSPT_ADDR,P.SEX,P.BIRTHDAY,P.WORK_NAME,P.EMAIL,P.PHONE,P.CONTACT,T.USER_ID,T.SERIAL_NUMBER,T.CITY_CODE " + " FROM TF_F_USER T,TF_F_CUST_PERSON P "
                + " WHERE T.CUST_ID= P.CUST_ID AND P.PARTITION_ID = MOD(P.CUST_ID,10000) " + " AND T.REMOVE_TAG ='0' AND T.NET_TYPE_CODE ='04' ";
        if ("0".equals(param.getString("QUERY_MODE")))
        {
            sql = sql + " AND T.SERIAL_NUMBER =:SERIAL_NUMBER ";
        }
        else
        {
            sql = sql + " AND P.PSPT_ID=:PSPT_ID AND PSPT_TYPE_CODE =:PSPT_TYPE_CODE ";
        }

        return Dao.qryBySql(new StringBuilder(sql), param);
    }

    public static IDataset getBroadBandUserInfo(IData param) throws Exception
    {

        String sql = " SELECT A.ADDR_NAME, A.ADDR_ID,A.ADDR_DESC, B.ACCESS_TYPE,C.RATE||'M' RATE,A.USER_ID,R.RES_CODE,R.RSRV_STR1,R.RSRV_STR2,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.IP_TYPE ";
        sql = sql + " FROM TF_F_USER_ADDR A, TF_F_USER_ACCESS_ACCT B,TF_F_USER_RATE C,TF_F_USER_RES R WHERE A.USER_ID = B.USER_ID AND A.USER_ID =C.USER_ID AND A.USER_ID =R.USER_ID AND A.USER_ID=:USER_ID AND B.ACCESS_ACCT=:SERIAL_NUMBER";

        return Dao.qryBySql(new StringBuilder(sql), param);
    }

    public static IDataset getBroadBandWidenetActByUserId(String userId) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);

        String sql = "SELECT USER_ID,INST_ID,ACCT_ID,ACCT_PASSWD,MAIN_TAG,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 "
                + " FROM TF_F_USER_WIDENET_ACT WHERE USER_ID=:USER_ID AND PARTITION_ID=MOD(:USER_ID,10000) AND SYSDATE BETWEEN START_DATE AND END_DATE";

        return Dao.qryBySql(new StringBuilder(sql), param);
    }

    /**
     * 获取moden租用特殊优惠参数
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public static String getModemDiscntCode() throws Exception
    {
        String discntCode = "";
        // TODO PARAM_ATTR 暂定 1128
        IDataset dataset = CommparaInfoQry.getCommparaCode1("CSM", "1128", "D", "ZZZZ");
        if (dataset != null && !dataset.isEmpty())
        {
            discntCode = dataset.getData(0).getString("PARA_CODE1", "");
        }
        return discntCode;
    }

    /**
     * 查询宽带用户地址，未完工的
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-12-18
     */
    public static IDataset getUnFinishBroadbandAddr(IData param) throws Exception
    {

        String sql = "SELECT A.TRADE_ID,A.ACCEPT_MONTH,A.INST_ID,A.USER_ID,A.ADDR_ID,A.ADDR_NAME,A.ADDR_DESC,A.MOFFICE_ID,A.MODIFY_TAG,A.START_DATE,A.END_DATE,A.UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK,A.IP_TYPE "
                + " FROM TF_B_TRADE_ADDR A ,TF_B_TRADE B " + " WHERE A.TRADE_ID= B.TRADE_ID AND A.USER_ID = B.USER_ID AND A.ACCEPT_MONTH =B.ACCEPT_MONTH " + " AND B.TRADE_TYPE_CODE =:TRADE_TYPE_CODE AND B.SUBSCRIBE_STATE ='0' "
                + " AND B.USER_ID =:USER_ID  AND B.SERIAL_NUMBER =:SERIAL_NUMBER ";
        return Dao.qryBySql(new StringBuilder(sql), param);

    }

    /**
     * 查询未完工的用户基本信息 BH_TAG ==0 的时候不去查完工的工单 （用于宽带地址修改），没有未完工的时候 查询完工用于安装进度查询
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-12-18
     */
    public static IDataset getUnFinishBroadBandUserInfo(IData param) throws Exception
    {
        String sql = "SELECT T.ORDER_ID,T.TRADE_ID,T.SERIAL_NUMBER,T.CUST_ID,O.CUST_NAME,O.PSPT_TYPE_CODE,O.PSPT_ID,O.CITY_CODE,A.ADDR_ID,A.ADDR_NAME,A.ADDR_DESC,A.IP_TYPE,B.ACCESS_TYPE,B.ACCESS_ACCT,C.RATE||'M' RATE,P.PSPT_TYPE_CODE,P.PSPT_ID,P.CUST_NAME,P.PSPT_ADDR,P.SEX,P.BIRTHDAY,P.WORK_NAME,P.EMAIL,P.PHONE,P.CONTACT "
                + " FROM TF_B_TRADE T,TF_B_ORDER O,TF_B_TRADE_ADDR A,TF_B_TRADE_ACCESS_ACCT B,TF_B_TRADE_RATE C,TF_B_TRADE_CUST_PERSON P "
                + " where T.ORDER_ID = O.ORDER_ID AND T.CUST_ID = O.CUST_ID AND T.ACCEPT_MONTH = O.ACCEPT_MONTH "
                + " AND T.TRADE_ID = A.TRADE_ID AND T.TRADE_ID = B.TRADE_ID AND T.TRADE_ID = C.TRADE_ID AND T.TRADE_ID = P.TRADE_ID " + " AND T.TRADE_TYPE_CODE =:TRADE_TYPE_CODE ";
        if ("0".equals(param.getString("QUERY_MODE")))
        {
            sql = sql + " AND T.SERIAL_NUMBER =:SERIAL_NUMBER ";
        }
        else
        {
            sql = sql + " AND O.PSPT_ID=:PSPT_ID AND O.PSPT_TYPE_CODE =:PSPT_TYPE_CODE ";
        }

        IDataset dataset = Dao.qryBySql(new StringBuilder(sql), param);

        if (dataset.size() > 0 || "0".equals(param.getString("BH_TAG")))
        {
            return dataset;
        }
        String sql2 = "SELECT T.ORDER_ID,T.TRADE_ID,T.SERIAL_NUMBER,T.CUST_ID,O.CUST_NAME,O.PSPT_TYPE_CODE,O.PSPT_ID,O.CITY_CODE,A.ADDR_ID,A.ADDR_NAME,A.ADDR_DESC,A.IP_TYPE,B.ACCESS_TYPE,B.ACCESS_ACCT,C.RATE||'M' RATE,P.PSPT_TYPE_CODE,P.PSPT_ID,P.CUST_NAME,P.PSPT_ADDR,P.SEX,P.BIRTHDAY,P.WORK_NAME,P.EMAIL,P.PHONE,P.CONTACT "
                + " FROM TF_BH_TRADE T,TF_BH_ORDER O,TF_B_TRADE_ADDR A,TF_B_TRADE_ACCESS_ACCT B,TF_B_TRADE_RATE C,TF_B_TRADE_CUST_PERSON P "
                + " where T.ORDER_ID = O.ORDER_ID AND T.CUST_ID = O.CUST_ID AND T.ACCEPT_MONTH = O.ACCEPT_MONTH "
                + " AND T.TRADE_ID = A.TRADE_ID AND T.TRADE_ID = B.TRADE_ID AND T.TRADE_ID = C.TRADE_ID AND T.TRADE_ID = P.TRADE_ID " + " AND T.TRADE_TYPE_CODE =:TRADE_TYPE_CODE ";

        if ("0".equals(param.getString("QUERY_MODE")))
        {
            sql2 = sql2 + " AND T.SERIAL_NUMBER =:SERIAL_NUMBER ";
        }
        else
        {
            sql2 = sql2 + " AND O.PSPT_ID=:PSPT_ID AND O.PSPT_TYPE_CODE =:PSPT_TYPE_CODE ";
        }
        dataset = Dao.qryBySql(new StringBuilder(sql2), param);

        return dataset;
    }

    public static void insBhOrderCancel(IData param) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_BH_ORDER", "INS_BY_ORDER_CANCEL", param);
    }

    public static void insBhTradeCancelNew(IData param) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_BH_TRADE", "INS_BY_TRADE_CANCEL_NEW", param);
    }

    public static void insBhTradeCancelSelf(IData param) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_BH_TRADE", "INS_BY_TRADE_CANCEL_SELF", param);
    }

    public static IDataset modifyQueryInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" UPDATE TF_B_TELEWIDE_BOOK T ");
        parser.addSQL(" SET T.SUBSCRIBE_TYPE = :SUBSCRIBE_TYPE, ");
        parser.addSQL(" T.CUST_NAME      = :CUST_NAME, ");
        parser.addSQL(" T.TELE_PHONE     = :TELE_PHONE, ");
        parser.addSQL(" T.MOBILE_PHONE   = :MOBILE_PHONE, ");
        parser.addSQL(" T.PSPT_TYPE_CODE = :PSPT_TYPE_CODE, ");
        parser.addSQL(" T.PSPT_ID        = :PSPT_ID, ");
        parser.addSQL(" T.HOME_ADDR      = :HOME_ADDR, ");
        parser.addSQL(" T.SET_ADDR       = :SET_ADDR, ");
        parser.addSQL(" T.EMAIL          = :EMAIL, ");
        parser.addSQL(" T.POSTAL_CODE    = :POSTAL_CODE, ");
        parser.addSQL(" T.REMARK         = :REMARK, ");
        parser.addSQL(" T.UPDATE_TIME    = to_date(:UPDATE_TIME,'YYYY-MM-DD')");
        parser.addSQL(" WHERE T.SUBSCRIBE_ID = :SUBSCRIBE_ID ");

        Dao.executeUpdate(parser);

        return null;
    }

    /**
     * 通过USER_ID查询宽带资料
     * 
     * @param userId
     * @return
     * @throws Exception
     *             wangjx 2014-1-3
     */
    public static IDataset qryAccessByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_ACCESS_ACCT", "SEL_BY_PK", param);
    }

    public static IDataset qryAccountIp(String STATE, String AREA_CODE) throws Exception
    {
        IData param = new DataMap();
        param.put("STATE", STATE);
        param.put("AREA_CODE", AREA_CODE);
        return Dao.qryByCode("TD_B_ACCOUNT_IP", "SEL_A_ACCOUNT", param, Route.CONN_CRM_CEN);
    }

    /**
     * 通过USER_ID查询宽带用户安装地址
     * 
     * @param userId
     * @return IDataset
     * @throws Exception
     *             wangjx 2014-1-2
     */
    public static IDataset qryAddrByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_ADDR", "SEL_BY_PK", param);
    }

    public static IDataset qryBroadbandTradeInfo(String serialNumber, String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("CANCEL_TAG", "0");
        return Dao.qryByCode("TF_B_TRADE", "SEL_BY_SN", param);
    }

    public static IDataset qryBroadbandTradeInfo2(String serialNumber, String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("CANCEL_TAG", "0");
        return Dao.qryByCode("TF_B_TRADE", "SEL_BY_SN2", param);
    }

    public static IDataset qryisBroadbandMilitary(String serialNumber, String state) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCOUNT_ID", serialNumber);
        param.put("STATE", state);
        return Dao.qryByCode("TD_B_ACCOUNT_IP", "SEL_COUNT_BY_ACCTID", param, Route.CONN_CRM_CEN);
    }

    /**
     * 通过宽带账号查询有效宽带用户
     * 
     * @param accessAcct
     * @return IDataset
     * @throws Exception
     *             wangjx 2014-1-1
     */
    public static IDataset qryNormalAccessByAcct(String accessAcct) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCESS_ACCT", accessAcct);
        return Dao.qryByCode("TF_F_USER_ACCESS_ACCT", "SEL_BY_ACCT", param);
    }

    /**
     * 通过USER_ID查询宽带用户速率
     * 
     * @param userId
     * @return IDataset
     * @throws Exception
     *             wangjx 2014-1-2
     */
    public static IDataset qryRateByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_RATE", "SEL_BY_PK", param);
    }

    public static IDataset qrySendBackTimeFormOther(String userId, String instId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", "REMAIN_TIME");
        param.put("RSRV_STR1", instId);
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_FOR_SENDBACK", param);
    }

    public static IDataset queryBookInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT T.SUBSCRIBE_ID,T.ACCEPT_MONTH,T.SUBSCRIBE_TYPE, ");
        parser.addSQL(" T.CUST_NAME,T.TELE_PHONE,T.MOBILE_PHONE,T.PSPT_TYPE_CODE, ");
        parser.addSQL(" T.PSPT_ID,T.HOME_ADDR,T.SET_ADDR,T.EMAIL,T.POSTAL_CODE, ");
        parser.addSQL(" T.REMARK,T.SUBSCRIBE_STATE,T.RESULT_TYPE,T.RESULT_DESC, ");
        parser.addSQL(" T.REG_DATE,T.UPDATE_TIME,T.UPDATE_STAFF_ID,T.UPDATE_DEPART_ID ");
        parser.addSQL(" FROM TF_B_TELEWIDE_BOOK T WHERE 1 = 1 ");
        parser.addSQL(" AND T.MOBILE_PHONE = :MOBILE_PHONE  ");
        parser.addSQL(" AND T.PSPT_TYPE_CODE = :PSPT_TYPE_CODE  ");
        parser.addSQL(" AND T.PSPT_ID = :PSPT_ID ");

        return Dao.qryByParse(parser);
    }

    public static IDataset queryBroadBandAddressInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT INST_ID,PARTITION_ID,USER_ID,ADDR_ID,ADDR_NAME,ADDR_DESC, ");
        parser.addSQL(" MOFFICE_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,IP_TYPE ");
        parser.addSQL(" FROM TF_F_USER_ADDR T ");
        parser.addSQL(" WHERE 1 = 1 AND USER_ID = TO_NUMBER(:USER_ID) ");
        parser.addSQL(" AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
        parser.addSQL(" AND SYSDATE BETWEEN START_DATE AND END_DATE ");

        IDataset infos = Dao.qryByParse(parser);

        return infos;
    }

    public static IDataset queryCancelOrderByOrderID(String orderId) throws Exception
    {
        IData param = new DataMap();

        param.put("ORDER_ID", orderId);
        param.put("CANCEL_TAG", "0");

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT T.* FROM TF_B_ORDER T WHERE 1 = 1 AND T.ORDER_ID = :ORDER_ID AND T.CANCEL_TAG = :CANCEL_TAG ");

        return Dao.qryByParse(parser);
    }

    public static IDataset queryCancelTradeByPSPT(String psptTypeCode, String psptId, String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();

        param.put("PSPT_TYPE_CODE", psptTypeCode);
        param.put("PSPT_ID", psptId);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT T.TRADE_ID,T.ACCEPT_MONTH,T.BATCH_ID,T.ORDER_ID,T.PROD_ORDER_ID,T.BPM_ID,T.CAMPN_ID,T.TRADE_TYPE_CODE,T.PRIORITY,T.SUBSCRIBE_TYPE, ");
        parser.addSQL(" T.SUBSCRIBE_STATE,T.NEXT_DEAL_TAG,T.IN_MODE_CODE,T.CUST_ID,T.CUST_NAME,T.USER_ID,T.ACCT_ID,T.SERIAL_NUMBER,T.NET_TYPE_CODE,T.EPARCHY_CODE, ");
        parser.addSQL(" T.CITY_CODE,T.PRODUCT_ID,T.BRAND_CODE,T.CUST_ID_B,T.USER_ID_B,T.ACCT_ID_B,T.SERIAL_NUMBER_B,T.CUST_CONTACT_ID,T.SERV_REQ_ID,T.INTF_ID, ");
        parser.addSQL(" T.ACCEPT_DATE,T.TRADE_STAFF_ID,T.TRADE_DEPART_ID,T.TRADE_CITY_CODE,T.TRADE_EPARCHY_CODE,T.TERM_IP,T.OPER_FEE,T.FOREGIFT,T.ADVANCE_PAY, ");
        parser.addSQL(" T.INVOICE_NO,T.FEE_STATE,T.FEE_TIME,T.FEE_STAFF_ID,T.PROCESS_TAG_SET,T.OLCOM_TAG,T.FINISH_DATE,T.EXEC_TIME,T.EXEC_ACTION,T.EXEC_RESULT, ");
        parser.addSQL(" T.EXEC_DESC,T.CANCEL_TAG,T.CANCEL_DATE,T.CANCEL_STAFF_ID,T.CANCEL_DEPART_ID,T.CANCEL_CITY_CODE,T.CANCEL_EPARCHY_CODE,T.UPDATE_TIME, ");
        parser.addSQL(" T.UPDATE_STAFF_ID,T.UPDATE_DEPART_ID,T.REMARK,T.RSRV_STR1,T.RSRV_STR2,T.RSRV_STR3,T.RSRV_STR4,T.RSRV_STR5,T.RSRV_STR6,T.RSRV_STR7, ");
        parser.addSQL(" T.RSRV_STR8,T.RSRV_STR9,T.RSRV_STR10,T.PF_TYPE,T.IS_NEED_HUMANCHECK,T.FREE_RESOURCE_TAG,R.ADDR_DESC,R.IP_TYPE,A.ACCESS_ACCT ");
        parser.addSQL(" FROM TF_B_ORDER O, TF_B_TRADE T, TF_B_TRADE_ADDR R, TF_B_TRADE_ACCESS_ACCT A ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND O.TRADE_TYPE_CODE = T.TRADE_TYPE_CODE ");
        parser.addSQL(" AND T.ORDER_ID = O.ORDER_ID ");
        parser.addSQL(" AND O.TRADE_TYPE_CODE = :TRADE_TYPE_CODE ");
        parser.addSQL(" AND O.PSPT_TYPE_CODE = :PSPT_TYPE_CODE ");
        parser.addSQL(" AND O.PSPT_ID = :PSPT_ID ");
        parser.addSQL(" AND R.TRADE_ID = T.TRADE_ID ");
        parser.addSQL(" AND A.TRADE_ID = T.TRADE_ID ");
        parser.addSQL(" AND A.ACCESS_ACCT = T.SERIAL_NUMBER ");

        return Dao.qryByParse(parser);
    }

    public static IDataset queryCancelTradeBySerialNum(String serialNum) throws Exception
    {
        IData param = new DataMap();

        param.put("SERIAL_NUMBER", serialNum);

        SQLParser parser = new SQLParser(param);

        parser
                .addSQL(" SELECT T.*,R.ADDR_DESC,N.ACCESS_ACCT FROM TF_B_TRADE T, TF_B_TRADE_ACCESS_ACCT N, TF_B_TRADE_ADDR R WHERE 1 = 1 AND T.SERIAL_NUMBER = :SERIAL_NUMBER AND T.TRADE_ID = N.TRADE_ID AND N.ACCESS_ACCT = T.SERIAL_NUMBER AND R.TRADE_ID = N.TRADE_ID");

        return Dao.qryByParse(parser);
    }

    public static IDataset queryCancelTradeByTradeID(String tradeId) throws Exception
    {
        IData param = new DataMap();

        param.put("TRADE_ID", tradeId);
        param.put("CANCEL_TAG", "0");

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT T.* FROM TF_B_TRADE T WHERE 1 = 1 AND T.TRADE_ID = :TRADE_ID AND T.CANCEL_TAG = :CANCEL_TAG ");

        return Dao.qryByParse(parser);
    }

    public static IDataset queryTradeRateByTradeIdAndModifyTag(String tradeId, String modifyTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("MODIFY_TAG", modifyTag);

        String sql = "SELECT TO_CHAR(TRADE_ID) TRADE_ID, ACCEPT_MONTH, RATE, INST_ID, TO_CHAR(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE, TO_CHAR(END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE, MODIFY_TAG, TO_CHAR(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK "
                + " FROM TF_B_TRADE_RATE " + " WHERE TRADE_ID=TO_NUMBER(:TRADE_ID) " + " AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) " + " AND MODIFY_TAG=:MODIFY_TAG ";
        return Dao.qryBySql(new StringBuilder(sql), param);
    }

    public static IDataset queryUserRateByUserId(String USER_ID) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", USER_ID);

        String sql = "SELECT RATE, INST_ID,USER_ID, TO_CHAR(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE, TO_CHAR(END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE, TO_CHAR(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK "
                + " FROM TF_F_USER_RATE " + " WHERE USER_ID=TO_NUMBER(:USER_ID) " + " AND PARTITION_ID = MOD(:USER_ID,10000) " + " AND SYSDATE BETWEEN START_DATE AND END_DATE ";
        return Dao.qryBySql(new StringBuilder(sql), param);
    }

    /**
     * 占用军区用户帐号资源
     * 
     * @param pd
     * @param acctData
     * @return
     * @throws Exception
     */
    public static boolean updateAccountIp(String acctId, String STATE) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCOUNT_ID", acctId);
        param.put("STATE", STATE);
        String sql = "update TD_B_ACCOUNT_IP set STATE = :STATE where ACCOUNT_ID = :ACCOUNT_ID and STATE='0'";
        return Dao.executeUpdate(new StringBuilder(sql), param, Route.CONN_CRM_CEN) > 0 ? true : false;
    }

    public static void UpdateBroadbandMilitary(String serialNumber, String state) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCOUNT_ID", serialNumber);
        param.put("STATE", state);
        Dao.executeUpdateByCodeCode("TD_B_ACCOUNT_IP", "UPD_RELA_WIDENET_ACCOUNT", param, Route.CONN_CRM_CEN);
    }
    /**
     * 查询无手机宽带历史工单
     * @param tradeId
     * @param state
     * @throws Exception
     */
    public static IDataset qryTradeHisInfoBySerialNumber(String serialNumber) throws Exception
    {
    	 IData param = new DataMap();

         param.put("SERIAL_NUMBER", serialNumber);

         SQLParser parser = new SQLParser(param);

         parser.addSQL(" SELECT T.* FROM TF_BH_TRADE T WHERE 1 = 1 AND T.SERIAL_NUMBER = :SERIAL_NUMBER ORDER BY T.ACCEPT_DATE DESC");

         return Dao.qryByParse(parser);
    }
    /**
     * 查询无手机宽带历史工单
     * @param tradeId
     * @param state
     * @throws Exception
     */
    public static IDataset qryTradeHisInfoByTradeId(String tradeId) throws Exception
    {
    	 IData param = new DataMap();

         param.put("TRADE_ID", tradeId);

         SQLParser parser = new SQLParser(param);

         parser.addSQL(" SELECT T.* FROM TF_BH_TRADE T WHERE 1 = 1 AND T.TRADE_ID = :TRADE_ID");

         return Dao.qryByParse(parser);
    }
    /**
     * 查询无手机宽带台账优惠
     * @param tradeId
     * @param state
     * @throws Exception
     */
    public static IDataset qryTradeDiscntInfoByTradeId(String tradeId) throws Exception
    {
    	 IData param = new DataMap();

         param.put("TRADE_ID", tradeId);

         SQLParser parser = new SQLParser(param);

         parser.addSQL(" SELECT T.* FROM TF_B_TRADE_DISCNT T WHERE 1 = 1 AND T.TRADE_ID = :TRADE_ID");

         return Dao.qryByParse(parser, Route.getJourDb());//Route.CONN_CRM_CG
    }
    /**
     * 查询无手机宽带信息台账
     * @param tradeId
     * @param state
     * @throws Exception
     */
    public static IDataset qryTradeWidenetInfoByTradeId(String tradeId) throws Exception
    {
    	 IData param = new DataMap();

         param.put("TRADE_ID", tradeId);

         SQLParser parser = new SQLParser(param);

         parser.addSQL(" SELECT T.* FROM TF_B_TRADE_WIDENET T WHERE 1 = 1 AND T.TRADE_ID = :TRADE_ID");

         return Dao.qryByParse(parser, Route.getJourDb());//Route.CONN_CRM_CG
    }
    /**
     * 查询用户信息
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset qryUserInfoBySerialNumber(String serialNumber) throws Exception
    {
    	 IData param = new DataMap();

         param.put("SERIAL_NUMBER", serialNumber);

         SQLParser parser = new SQLParser(param);

         parser.addSQL(" SELECT * FROM TF_F_USER T WHERE T.SERIAL_NUMBER=:SERIAL_NUMBER AND T.REMOVE_TAG='0'");

         return Dao.qryByParse(parser);
    }
    /**
     * 更新缴费情况
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static void updateTradePayInfo(IDataset otherFees,IDataset tradeList,IData input) throws Exception
    {
    	 IData param = new DataMap();
    	 
    	 if(IDataUtil.isNotEmpty(otherFees)){
    		 
    		 param.put("RSRV_STR1", input.getString("PAY_TAG",""));
    		 param.put("RSRV_STR2", input.getString("PAY_MONEY",""));
    		 param.put("RSRV_STR3", input.getString("SERIAL_NUMBER",""));
    		 param.put("RSRV_STR4", input.getString("BUSINESS_ID",""));
    		 param.put("USER_ID", tradeList.getData(0).getString("USER_ID"));
    		 param.put("RSRV_VALUE_CODE", "NOPHONE_WNET_PAY_TAG");
        	 SQLParser parser = new SQLParser(param);

             parser.addSQL(" UPDATE TF_F_USER_OTHER T ");
             parser.addSQL(" SET RSRV_STR1 = :RSRV_STR1 ");
             parser.addSQL(" ,RSRV_STR2 = :RSRV_STR2 ");
             parser.addSQL(" ,RSRV_STR3 = :RSRV_STR3 ");
             parser.addSQL(" ,RSRV_STR4 = :RSRV_STR4 ");
             parser.addSQL(" where USER_ID = :USER_ID AND RSRV_VALUE_CODE=:RSRV_VALUE_CODE");

             Dao.executeUpdate(parser);
             
    	 }else{
    		 
    		 param.put("PARTITION_ID", Long.parseLong(tradeList.getData(0).getString("USER_ID")) % 10000);
    		 param.put("INST_ID", SeqMgr.getInstId());
    		 param.put("USER_ID", tradeList.getData(0).getString("USER_ID"));
    		 
    		 param.put("RSRV_VALUE_CODE", "NOPHONE_WNET_PAY_TAG");
    		 param.put("RSRV_VALUE", "无手机宽带APP开户缴费记录");
    		 param.put("RSRV_STR1", input.getString("PAY_TAG",""));
    		 param.put("RSRV_STR2", input.getString("PAY_MONEY",""));
    		 param.put("RSRV_STR3", input.getString("SERIAL_NUMBER",""));
    		 param.put("RSRV_STR4", input.getString("BUSINESS_ID",""));
    		 param.put("TRADE_ID", tradeList.getData(0).getString("TRADE_ID"));
    	   
    		 param.put("START_DATE", SysDateMgr.getSysTime());
    		 param.put("END_DATE", SysDateMgr.getTheLastTime());
    		 param.put("REMARK", "无手机宽带APP开户缴费标识记录 ");
    	       

    	     Dao.insert("TF_F_USER_OTHER", param);
    		 
    		 
    	 }
    	 
    	
         
    }
}
