
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class UserBankMainSignInfoQry
{

    /**
     * 根据 主号码 黑名单号码 黑名单状态 获得用户的黑名单
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static IData getBlackUser(String serial_number, String black_serial_number, String black_status) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("BLACK_STATUS", black_status);
        param.put("BLACK_SERIAL_NUMBER", black_serial_number);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT t.* FROM TF_F_BLACKUSER_MANAGER t WHERE 1=1");
        parser.addSQL(" AND t.SERIAL_NUMBER=:SERIAL_NUMBER");
        parser.addSQL(" AND t.BLACK_SERIAL_NUMBER=:BLACK_SERIAL_NUMBER");
        parser.addSQL(" AND t.black_status=:BLACK_STATUS");
        IDataset dataset = Dao.qryByParse(parser);

        IData blackUser = new DataMap();
        if (!dataset.isEmpty())
        {
            blackUser = (IData) dataset.get(0);
            return blackUser;
        }
        return blackUser;
    }

    public static IDataset getInfoByUser(String usertype, String uservalue) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_TYPE", usertype);
        param.put("USER_VALUE", uservalue);
        return Dao.qryByCode("TF_F_BANK_MAINSIGN", "SEL_VALIDATE_SIGNINFO", param);
    }

    public static boolean insertBankSignFile(IData param) throws Exception
    {
        return Dao.insert("TI_O_BANK_SIGN_FILE", param);
    }

    public static IDataset isBlackUser(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_BLACKUSER_MANAGER", "SEL_BLACKINFO", param);
    }

    /**
     * 获取TF_F_BANK数据
     * 
     * @param serialNumber
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset qryBankByPk(String serialNumber, String userId) throws Exception
    {
        IData param = new DataMap();

        param.put("SERIAL_NUMBER", serialNumber);
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_BANK", "SEL_BY_PK", param);
    }

    public static IDataset qryBankInfoByAsvc(String userId) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_BANK", "SEL_BY_UB_ASVC", param);
    }

    public static IDataset qryBankInfoList(String serial_number) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select * from tf_f_bank t WHERE 1=1");
        parser.addSQL(" AND t.SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL(" order by end_time desc");
        return Dao.qryByParse(parser);
    }

    public static IDataset qryBankPaymentInfoList(String user_id) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", user_id);
        String sql = "SELECT * FROM TF_F_BANK WHERE USER_ID =:USER_ID  AND END_TIME > SYSDATE  AND RECK_TAG = '0' ";

        return Dao.qryBySql(new StringBuilder(sql), param);
    }

    public static IDataset qryBaseGroupBySN(String user_id, String serialNumber) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", user_id);
        param.put("SERIAL_NUMBER", serialNumber);
        String sql = "SELECT * FROM TF_F_BANK WHERE USER_ID =:USER_ID  AND serial_number = :SERIAL_NUMBER   AND RECK_TAG = '0' ";

        return Dao.qryBySql(new StringBuilder(sql), param);// ,Route.CONN_CRM_CG海南没有CG
    }

    /**
     * 根据 主号码 黑名单状态 获得用户的黑名单list
     * 
     * @param pd
     * @param cond
     * @return
     * @throws Exception
     */
    public static IDataset qryBlackUserList(String serial_number, String black_status) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("BLACK_STATUS", black_status);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT t.* FROM TF_F_BLACKUSER_MANAGER t WHERE 1=1");
        parser.addSQL(" AND t.SERIAL_NUMBER=:SERIAL_NUMBER");
        parser.addSQL(" AND t.black_status=:BLACK_STATUS");
        return Dao.qryByParse(parser);
    }

    /**
     * 查询配置接口
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-23
     */
    public static IDataset qryCommpara1() throws Exception
    {

        String sql = " SELECT * FROM td_s_commpara a WHERE a.param_name = '银行卡签约缴费平台' and a.param_attr = '1'";

        return Dao.qryBySql(new StringBuilder(sql), new DataMap(), Route.CONN_CRM_CG);
    }

    public static IDataset qryTradeMainsignInfoByPK(IData param) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_BANK_MAINSIGN", "SEL_BY_PK_J", param);
    }

    public static IDataset qryTradeMainsignInfoByPK(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        return Dao.qryByCode("TF_B_TRADE_BANK_MAINSIGN", "SEL_BY_PK_J", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    public static IDataset qryTradeSubsignInfoByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        return Dao.qryByCode("TF_B_TRADE_BANK_SUBSIGN", "SEL_BY_TRADE_ID", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    public static IDataset queryNetNp(IData param) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_NETNP", "SEL_BY_NETNP_NUM", param, Route.CONN_CRM_CEN);
    }

    public static IDataset querySubSignInfoByMain(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_BANK_SUBSIGN", "SEL_VALIDATE_SIGNINFO_BY_MAIN", param);
    }

    /**
     * 通过主卡关联号码查询,返回全部成员信息,没有成员则返回主卡信息
     * 
     * @param user_value
     * @return
     * @throws Exception
     */

    public static IDataset queryUserBankAllSignByID(String user_value) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_VALUE", user_value);

        return Dao.qryByCode("TF_F_BANK_MAINSIGN", "SEL_ALLINFO_BY_MAINVALUE", param);
    }

    /**
     * 通过副卡关联号码查询,返回全部成员信息,没有成员则返回主卡信息
     * 
     * @param user_value
     * @return
     * @throws Exception
     */
    public static IDataset queryUserBankAllSignBySubID(String user_value) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_VALUE", user_value);

        return Dao.qryByCode("TF_F_BANK_SUBSIGN", "SEL_ALLINFO_BY_SUBVALUE", param);
    }

    /**
     * @param user_value
     * @return
     * @throws Exception
     */

    public static IDataset queryUserBankMainSignByMainID(String user_value) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_VALUE", user_value);

        return Dao.qryByCode("TF_F_BANK_MAINSIGN", "SEL_MAININFO_BY_MAINVALUE", param);
    }

    public static IDataset queryUserBankMainSignBySn(String serial_number) throws Exception
    {
        IData param = new DataMap();

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serial_number);

        if (IDataUtil.isNotEmpty(userInfo))
        {
            return queryUserBankMainSignByUID(userInfo.getString("SERIAL_NUMBER"));
        }

        return new DatasetList();
    }

    public static IDataset queryUserBankMainSignByUID(String user_value) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_VALUE", user_value);
        param.put("USER_TYPE", "01");
        return Dao.qryByCode("TF_F_BANK_MAINSIGN", "SEL_VALIDATE_SIGNINFO", param);
    }

    /**
     * @param userType
     *            01
     * @param serialNumber
     * @return
     * @throws Exception
     *             EDIT BY GONGP@2014-6-20
     */
    public static IDataset queryUserBankMainSignByUID(String userType, String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_TYPE", userType);
        param.put("USER_VALUE", serialNumber);

        return Dao.qryByCode("TF_F_BANK_MAINSIGN", "SEL_VALIDATE_SIGNINFO", param);
    }

    /**
     * @param serial_number
     * @return
     * @throws Exception
     */
    public static IDataset queryUserBankSubCountByUID(String main_user_value, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("MAIN_USER_TYPE", "01");
        param.put("MAIN_USER_VALUE", main_user_value);
        param.put("EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TF_F_BANK_SUBSIGN", "SEL_VALIDATE_SIGNINFO_BY_MAIN", param);
    }

    public static IDataset queryUserBankSubSignByUID(String user_value) throws Exception
    {
        IData param = new DataMap();
        param.put("SUB_USER_TYPE", "01");
        param.put("SUB_USER_VALUE", user_value);
        return Dao.qryByCode("TF_F_BANK_SUBSIGN", "SEL_VALIDATE_SIGNINFO", param);
    }

    /**
     * @param subUserType
     *            01
     * @param serialNumber
     * @return
     * @throws Exception
     *             EDIT BY GONGP@2014-6-20
     */
    public static IDataset queryUserBankSubSignByUID(String subUserType, String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SUB_USER_TYPE", subUserType);
        param.put("SUB_USER_VALUE", serialNumber);

        return Dao.qryByCode("TF_F_BANK_SUBSIGN", "SEL_VALIDATE_SIGNINFO", param);
    }

    public static IDataset queryUserisBlackByUID(IData param) throws Exception
    {

        return Dao.qryByCode("TF_F_BLACKUSER_MANAGER", "SEL_BLACKINFO", param);
    }

    public static IDataset queryUserisBlackByUID(String mainUserValue, String subNum) throws Exception
    {
        IData param = new DataMap();
        param.put("FORB_BADNESS_NUMBER", mainUserValue);
        param.put("FORB_REPORT_NUMBER", subNum);
        return Dao.qryByCode("TF_F_BLACKUSER_MANAGER", "SEL_BLACKINFO", param);
    }
    
    /**
     * @param USER_ID
     * @param BANK_CARD_NO
     * @return
     * @throws Exception
     * @author wukw3
     */
    public static IDataset querySnBindBankByCardNo(IData param) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE", param);
    }

    public static int saveTradeBankMainsign(IData param) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_B_TRADE_BANK_MAINSIGN", "UPD_TRADE_BANK_MAINSIGN", param);
    }

    public static int updTradeExecTime(IData param) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_EXEC_TIME_BY_TRADE_ID", param);
    }
}
