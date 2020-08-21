
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.biz.bean.BizBean;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UVipClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UVipTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;

public class CustVipInfoQry
{

    public static IDataset getCustInfoByCustidAndVipid(String custId, String vipId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        param.put("IDCARDNUM", vipId);
        return Dao.qryByCode("TF_F_CUST_VIP", "SEL_BY_CUSTID_VIPID", param);
    }

    /**
     * query by userID
     * 
     * @param userId
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-16
     */
    public static IDataset getCustVipByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_CUST_VIP", "SEL_BY_USERID", param);
    }

    public static IDataset getCustVipByUserId(String userId, String remove_tag) throws Exception
    {
        return getCustVipByUserId(userId, remove_tag, null);
    }

    public static IDataset getCustVipByUserId(String userId, String remove_tag, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("REMOVE_TAG", remove_tag);
        return Dao.qryByCodeParser("TF_F_CUST_VIP", "SEL_BY_UID", param, routeId);
    }

    public static IDataset getCustVipClassByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("REMOVE_TAG", "0");
        return Dao.qryByCode("TF_F_CUST_VIP", "SEL_VIP_CLASS", param);
    }

    /**
     * 根据cust_id 和 remove_tag 获取大客户信息
     * 
     * @param cust_id
     * @param remove_tag
     * @throws Exception
     * @author
     */
    public static IData getCustVipInfo(IData inParams) throws Exception
    {

        IDataset dataset = qryVipInfoByUserId(inParams.getString("USER_ID"));

        if (dataset.size() == 0)
        {
            return null;
        }
        else
        {
            return (IData) dataset.get(0);
        }
    }

    /**
     * 获取一些常用的要传给客服的字段
     * 
     * @Function: getCustVipInfoByCustId
     * @Description: TODO
     * @date Jul 31, 2014 3:59:10 PM
     * @param custId
     * @param removeTag
     * @return
     * @throws Exception
     * @author longtian3
     */
    public static IDataset getCustVipInfoByCustId(String custId, String removeTag) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        param.put("REMOVE_TAG", removeTag);
        return Dao.qryByCode("TF_F_CUST_VIP", "SEL_BY_CUSTID_12", param);
    }

    public static IDataset getCustVipsByCustIdRemoveTag(String custId, String removeTag) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        param.put("REMOVE_TAG", removeTag);
        return Dao.qryByCode("TF_F_CUST_VIP", "SEL_VIP_CLASS", param);
    }

    public static IDataset getHintVipInfoBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_CUST_VIP", "SEL_HINT_VIPINFO", param);
    }

    /**
     * 获取vip备卡信息
     * 
     * @param params
     *            查询所需参数
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getSimBakInfos(IData params) throws Exception
    {

        return Dao.qryByCode("TF_F_CUST_VIPSIMBAK", "SEL_BY_VIPID", params);

    }

    public static IDataset getSimBakInfos(String vipId, String actTag, String eparchyCode) throws Exception
    {

        IData param = new DataMap();
        param.put("VIP_ID", vipId);
        param.put("ACT_TAG", actTag);
        return Dao.qryByCode("TF_F_CUST_VIPSIMBAK", "SEL_BY_VIPID", param, eparchyCode);

    }

    /**
     * 查询当年有没有办过备卡申请
     * 
     * @param params
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-9-2
     */
    public static IDataset getSimBakInfosByDate(IData params) throws Exception
    {
        return Dao.qryByCode("TF_F_CUST_VIPSIMBAK", "SEL_USER_ACTBAK", params);
    }

    public static IDataset getVipCardState(String vipCardNo) throws Exception
    {
        IData params = new DataMap();
        params.put("IV_VIP_CARD_NO", vipCardNo);
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" select * from Tf_f_Vipcard_State");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" and Vip_No = :IV_VIP_CARD_NO");

        return Dao.qryByParse(parser);
    }

    public static IDataset getVipSimbakInfos(String sim_card_no) throws Exception
    {
        IData data = new DataMap();
        data.put("SIM_CARD_NO", sim_card_no);

        return Dao.qryByCode("TF_F_CUST_VIPSIMBAK", "SEL_BY_SIM", data);
    }

    /**
     * 根据user_id获取用户的大客户资料
     * 
     * @param pd
     *            页面数据
     * @throws Exception
     */
    public static IDataset getVipUserByUserID(String userId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_CUST_VIP", "SEL_BY_VIPUSERID", inparam);

    }

    /**
     * @methodName: qryIsCanRecoverVip
     * @Description: 判断用户是否符合恢复大客户身份条件，当年销户当年复机可以恢复大客户身份
     * @version: v1.0.0
     * @author: xiaozb
     * @date: 2014-5-14 下午5:09:48
     */
    public static IData qryIsCanRecoverVip(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        IDataset returnDataset = Dao.qryByCode("TF_F_CUST_VIP", "SEL_ISCAN_RECOVER_VIP", param);
        if (IDataUtil.isNotEmpty(returnDataset))
        {
            return returnDataset.getData(0);
        }
        return new DataMap();
    }

    public static IData qryVipByUserId(String userId) throws Exception
    {
        IDataset datas = qryVipInfoByUserId(userId);

        if (datas != null && datas.size() > 0)
        {
            return datas.getData(0);
        }

        return null;
    }

    /**
     * 根据客户标识CUST_ID获取个人大客户资料表
     * 
     * @param custId
     * @param removeTag
     * @return
     * @throws Exception
     */
    public static IDataset qryVipInfoByCustId(String custId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);

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
        sql.append("TO_CHAR(CLUB_ID) CLUB_ID, VISIT_NUM, SVC_NUM, SVC_NUM_B, INNET_NUM, ");
        sql.append("SVC_MODE_CODE, SVC_CYCLE_CODE, ");
        sql.append("TO_CHAR(BIRTHDAY, 'yyyy-mm-dd hh24:mi:ss') BIRTHDAY, BIRTHDAY_LUNAR, ");
        sql.append("BIRTHDAY_FLAG, APPROVAL_FLAG, APPROVAL_STAFF_ID, ");
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
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, ");
        sql.append("RSRV_NUM3, TO_CHAR(RSRV_NUM4) RSRV_NUM4, TO_CHAR(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
        sql.append("RSRV_STR7, RSRV_STR8, ");
        sql.append("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("TO_CHAR(RSRV_DATE4, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE4, ");
        sql.append("TO_CHAR(RSRV_DATE5, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE5, RSRV_TAG1, ");
        sql.append("RSRV_TAG2, RSRV_TAG3, RSRV_TAG4, RSRV_TAG5 ");
        sql.append("FROM TF_F_CUST_VIP ");
        sql.append("WHERE CUST_ID = TO_NUMBER(:CUST_ID) AND REMOVE_TAG = '0' ");

        return Dao.qryBySql(sql, param);
    }

    /**
     * 根据客户标识CUST_ID获取个人大客户资料表
     * 
     * @param custId
     * @param removeTag
     * @return
     * @throws Exception
     */
    public static IDataset qryVipInfoByCustId(String custId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);

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
        sql.append("TO_CHAR(CLUB_ID) CLUB_ID, VISIT_NUM, SVC_NUM, SVC_NUM_B, INNET_NUM, ");
        sql.append("SVC_MODE_CODE, SVC_CYCLE_CODE, ");
        sql.append("TO_CHAR(BIRTHDAY, 'yyyy-mm-dd hh24:mi:ss') BIRTHDAY, BIRTHDAY_LUNAR, ");
        sql.append("BIRTHDAY_FLAG, APPROVAL_FLAG, APPROVAL_STAFF_ID, ");
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
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, ");
        sql.append("RSRV_NUM3, TO_CHAR(RSRV_NUM4) RSRV_NUM4, TO_CHAR(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
        sql.append("RSRV_STR7, RSRV_STR8, ");
        sql.append("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("TO_CHAR(RSRV_DATE4, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE4, ");
        sql.append("TO_CHAR(RSRV_DATE5, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE5, RSRV_TAG1, ");
        sql.append("RSRV_TAG2, RSRV_TAG3, RSRV_TAG4, RSRV_TAG5 ");
        sql.append("FROM TF_F_CUST_VIP ");
        sql.append("WHERE CUST_ID = TO_NUMBER(:CUST_ID) ");

        return Dao.qryBySql(sql, param, eparchyCode);
    }

    /**
     * 根据SERIAL_NUMBER查询vip信息
     * 
     * @param serialNumber
     * @param removeTag
     * @return
     * @throws Exception
     */
    public static IDataset qryVipInfoBySn(String serialNumber) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT VIP_ID, TO_CHAR(CUST_ID) CUST_ID, ");
        sql.append("TO_CHAR(USECUST_ID) USECUST_ID, ");
        sql.append("TO_CHAR(USER_ID) USER_ID, SERIAL_NUMBER, ");
        sql.append("NET_TYPE_CODE, CUST_NAME, USECUST_NAME, ");
        sql.append("USEPSPT_TYPE_CODE, USEPSPT_ID, ");
        sql.append("TO_CHAR(USEPSPT_END_DATE, 'yyyy-mm-dd hh24:mi:ss') USEPSPT_END_DATE, ");
        sql.append("USEPSPT_ADDR, USEPHONE, USEPOST_ADDR, EPARCHY_CODE, ");
        sql.append("CITY_CODE, VIP_TYPE_CODE, VIP_CLASS_ID, ");
        sql.append("LAST_VIP_TYPE_CODE, LAST_VIP_CLASS_ID, ");
        sql.append("TO_CHAR(CLASS_CHANGE_DATE, 'yyyy-mm-dd hh24:mi:ss') CLASS_CHANGE_DATE, ");
        sql.append("VIP_TYPE_CODE_B, VIP_CLASS_ID_B, LAST_VIP_TYPE_CODE_B, ");
        sql.append("LAST_VIP_CLASS_ID_B, ");
        sql.append("TO_CHAR(CLASS_CHANGE_DATE_B, 'yyyy-mm-dd hh24:mi:ss') CLASS_CHANGE_DATE_B, ");
        sql.append("VIP_CARD_NO, VIP_CARD_TYPE, VIP_CARD_PASSWD, ");
        sql.append("VIP_CARD_STATE, VIP_CARD_SPELL, VIP_CARD_INFO, ");
        sql.append("VIP_CARD_SEND_TYPE, ");
        sql.append("TO_CHAR(VIP_CARD_SEND_DATE, 'yyyy-mm-dd hh24:mi:ss') VIP_CARD_SEND_DATE, ");
        sql.append("VIP_CARD_POST_ADDR, ");
        sql.append("EXEMPT_SCORE_TAG,");
        sql.append("TO_CHAR(VIP_CARD_START_DATE, 'yyyy-mm-dd hh24:mi:ss') VIP_CARD_START_DATE, ");
        sql.append("TO_CHAR(VIP_CARD_END_DATE, 'yyyy-mm-dd hh24:mi:ss') VIP_CARD_END_DATE, ");
        sql.append("TO_CHAR(VIP_CARD_CHANGE_DATE, 'yyyy-mm-dd hh24:mi:ss') VIP_CARD_CHANGE_DATE, ");
        sql.append("VIP_CARD_CHANGE_REASON, CUST_MANAGER_ID, ");
        sql.append("CUST_MANAGER_ID_B, CUST_MANAGER_APPR, ");
        sql.append("TO_CHAR(ASSIGN_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSIGN_DATE, ");
        sql.append("ASSIGN_STAFF_ID, JOIN_TYPE, ");
        sql.append("TO_CHAR(JOIN_DATE, 'yyyy-mm-dd hh24:mi:ss') JOIN_DATE, ");
        sql.append("TO_CHAR(JOIN_DATE_B, 'yyyy-mm-dd hh24:mi:ss') JOIN_DATE_B, ");
        sql.append("JOIN_STAFF_ID, JOIN_DEPART_ID, ");
        sql.append("TO_CHAR(IDENTITY_CHK_DATE, 'yyyy-mm-dd hh24:mi:ss') IDENTITY_CHK_DATE, ");
        sql.append("TO_CHAR(IDENTITY_CHK_SCORE, 'yyyy-mm-dd hh24:mi:ss') IDENTITY_CHK_SCORE, ");
        sql.append("IDENTITY_PRI, ");
        sql.append("TO_CHAR(IDENTITY_EFF_DATE, 'yyyy-mm-dd hh24:mi:ss') IDENTITY_EFF_DATE, ");
        sql.append("TO_CHAR(IDENTITY_EXP_DATE, 'yyyy-mm-dd hh24:mi:ss') IDENTITY_EXP_DATE, ");
        sql.append("GROUP_ID, GROUP_CUST_NAME, ");
        sql.append("TO_CHAR(MONTH_FEE) MONTH_FEE, HVALUE_TAG, ");
        sql.append("TO_CHAR(CLUB_ID) CLUB_ID, VISIT_NUM, SVC_NUM, ");
        sql.append("SVC_NUM_B, INNET_NUM, SVC_MODE_CODE, SVC_CYCLE_CODE, ");
        sql.append("TO_CHAR(BIRTHDAY, 'yyyy-mm-dd hh24:mi:ss') BIRTHDAY, ");
        sql.append("BIRTHDAY_LUNAR, BIRTHDAY_FLAG, APPROVAL_FLAG, ");
        sql.append("APPROVAL_STAFF_ID, ");
        sql.append("TO_CHAR(APPROVAL_TIME, 'yyyy-mm-dd hh24:mi:ss') APPROVAL_TIME, ");
        sql.append("APPROVAL_DESC, GROUP_BRAND_CODE, BRAND_CODE, ");
        sql.append("PRODUCT_ID, USER_TYPE_CODE, USER_STATE_CODESET, ");
        sql.append("TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, ");
        sql.append("TO_CHAR(FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME, ");
        sql.append("TO_CHAR(LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME, ");
        sql.append("TO_CHAR(SCORE_VALUE) SCORE_VALUE, CREDIT_CLASS, ");
        sql.append("TO_CHAR(CREDIT_VALUE) CREDIT_VALUE, CTAG_SET, ");
        sql.append("CHECK_NO, TO_CHAR(TRADE_ID) TRADE_ID, CANCEL_TAG, ");
        sql.append("REMOVE_TAG, ");
        sql.append("TO_CHAR(REMOVE_DATE, 'yyyy-mm-dd hh24:mi:ss') REMOVE_DATE, ");
        sql.append("REMOVE_STAFF_ID, REMOVE_REASON, ");
        sql.append("TO_CHAR(SYNC_TIME, 'yyyy-mm-dd hh24:mi:ss') SYNC_TIME, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, ");
        sql.append("RSRV_NUM2, RSRV_NUM3, TO_CHAR(RSRV_NUM4) RSRV_NUM4, ");
        sql.append("TO_CHAR(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, RSRV_STR2, ");
        sql.append("RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, ");
        sql.append("RSRV_STR8, ");
        sql.append("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("TO_CHAR(RSRV_DATE4, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE4, ");
        sql.append("TO_CHAR(RSRV_DATE5, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE5, ");
        sql.append("RSRV_TAG1, RSRV_TAG2, RSRV_TAG3, RSRV_TAG4, RSRV_TAG5 ");
        sql.append("FROM TF_F_CUST_VIP ");
        sql.append("WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.append("AND REMOVE_TAG = '0' ");

        return Dao.qryBySql(sql, params);
    }

    /**
     * 根据客户标识USER_ID获取个人大客户资料表
     */
    public static IDataset qryVipInfoByUserId(String userId) throws Exception
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
        sql.append("SVC_CYCLE_CODE, TO_CHAR(BIRTHDAY, 'yyyy-mm-dd hh24:mi:ss') BIRTHDAY, ");
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

        return Dao.qryBySql(sql, params);
    }

    public static IDataset qryVipInfoByUserId(String userId, String eparchyCode) throws Exception
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
        sql.append("SVC_CYCLE_CODE, TO_CHAR(BIRTHDAY, 'yyyy-mm-dd hh24:mi:ss') BIRTHDAY, ");
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

        return Dao.qryBySql(sql, params, eparchyCode);
    }

    /** 根据用户手机号码查询用户是否是中高端用户 */
    public static IDataset queryCustVip(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" select * from tf_sm_tel_custvip");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" and source_type in('3','4')");
        parser.addSQL(" and remove_tag='0'");
        parser.addSQL(" and serial_number=:SERIAL_NUMBER");

        return Dao.qryByParse(parser);
    }

    public static IDataset queryCustVipInfoBySn(String serialNumber, String removeTag) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("REMOVE_TAG", removeTag);
        //TODO huanghua 35 codecode中有字段翻译函数，拆分
        return Dao.qryByCode("TF_F_CUST_VIP", "SEL_BY_SN_ALLINFO_NEW", param, Route.getCrmDefaultDb());
    }

    public static IDataset queryHighCustByUserId(String userId, String classId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("CLASS_ID", classId);

        return Dao.qryByCode("TF_F_CUST_VIP", "SEL_HIGH_CUST", param);
    }

    public static IDataset querySimBakByVipId(String vipId, String actTag) throws Exception
    {
        IData param = new DataMap();
        param.put("VIP_ID", vipId);
        param.put("ACT_TAG", actTag);
        IDataset simbaks = Dao.qryByCode("TF_F_CUST_VIPSIMBAK", "SEL_BY_VIPID", param);

        return simbaks;
    }

    /**
     * 根据手机号码查询大客户信息
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset queryVipInfoBySn(String serialNumber, String removeTag) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("REMOVE_TAG", removeTag);

        IDataset dataset = qryVipInfoBySn(serialNumber);// Dao.qryByCode("TF_F_CUST_VIP", "SEL_BY_SN_INTF", params);

        // 原来在sql中翻译，现在改在java中翻译
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (Object obj : dataset)
            {
                IData data = (IData) obj;

                data.put("CLIENT_INFO1", UStaffInfoQry.getCustManageNameByCustManagerId(data.getString("CUST_MANAGER_ID")));
                data.put("CLIENT_INFO2", UStaffInfoQry.getCustManageSnByCustManagerId(data.getString("CUST_MANAGER_ID")));
                data.put("CLIENT_INFO3", UDepartInfoQry.getDepartNameByDepartId(data.getString("JOIN_DEPART_ID")));
                data.put("CLIENT_INFO4", UStaffInfoQry.getStaffNameByStaffId(data.getString("JOIN_STAFF_ID").trim()));
                data.put("CLIENT_INFO5", UVipClassInfoQry.getVipClassNameByVipTypeCodeClassId(data.getString("VIP_TYPE_CODE"), data.getString("VIP_CLASS_ID")));
                data.put("CLIENT_INFO6", UDepartInfoQry.getDepartNameByDepartId(UDepartInfoQry.getDepartIdByCustManagerId(data.getString("CUST_MANAGER_ID"))));
                data.put("PREVALUE2", UVipClassInfoQry.getVipClassNameByVipTypeCodeClassId(data.getString("VIP_TYPE_CODE_B"), data.getString("VIP_CLASS_ID_B")));
                data.put("PREVALUE3", UVipTypeInfoQry.getVipTypeNameByVipTypeCode(data.getString("VIP_TYPE_CODE")));
                data.put("PREVALUE4", UVipTypeInfoQry.getVipTypeNameByVipTypeCode(data.getString("VIP_TYPE_CODE_B")));
            }
        }

        return dataset;
    }

    public static IDataset queryVipInfoByVipNo(String removeTag, String vipNo) throws Exception
    {
        IData param = new DataMap();
        param.put("REMOVE_TAG", removeTag);
        param.put("VIP_NO", vipNo);

        return Dao.qryByCode("TF_F_CUST_VIP", "SEL_BY_VNO", param);
    }

    /**
     * @data 2013-10-29
     * @param serviceId
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset queryVipRecommInfo(String serviceId, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("CUST_MANAGER_ID", CSBizBean.getVisit().getStaffId());
        param.put("SERVICE_ID", serviceId);

        // 1、查询客户经理名下的所有用户
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" Select t.cust_manager_id,t.User_Id,t.serial_number,t.cust_name, ");
        parser.addSQL(" '1' state_code,'已开通' state, ");
        parser.addSQL(" '新闻早晚报' SERVICE_NAME , ");
        parser.addSQL(" '0' RECOMMEND, '0' TRY,  '0' ORDER1 ");
        parser.addSQL(" From Tf_f_Cust_Vip t Where 1=1 ");
        parser.addSQL(" And t.cust_manager_id = :CUST_MANAGER_ID ");
        parser.addSQL(" And t.Remove_Tag = '0'  ");
        parser.addSQL(" And Exists ");
        parser.addSQL(" ( ");
        parser.addSQL(" Select 1 From tf_f_user_platsvc t1 Where t1.Biz_State_Code <> 'E' And t1.end_date > Sysdate ");
        parser.addSQL(" And t1.service_id = :SERVICE_ID ");
        parser.addSQL(" And t1.user_id = t.user_id ");
        parser.addSQL(" ) ");
        // 未开通
        parser.addSQL(" Union ");
        parser.addSQL(" Select t.cust_manager_id,t.User_Id,t.serial_number,t.cust_name, ");
        parser.addSQL(" '0' state_code,'未开通' state, ");
        parser.addSQL(" '新闻早晚报' SERVICE_NAME , ");
        parser.addSQL(" '0' RECOMMEND, '0' TRY,  '0' ORDER1 ");
        parser.addSQL(" From Tf_f_Cust_Vip t Where 1=1 ");
        parser.addSQL(" And t.cust_manager_id = :CUST_MANAGER_ID ");
        parser.addSQL(" And t.Remove_Tag = '0'  ");
        parser.addSQL(" And Not Exists ");
        parser.addSQL(" ( ");
        parser.addSQL(" Select 1 From tf_f_user_platsvc t1 Where t1.Biz_State_Code <> 'E' And t1.end_date > Sysdate ");
        parser.addSQL(" And t1.service_id = :SERVICE_ID ");
        parser.addSQL(" And t1.user_id = t.user_id ");
        parser.addSQL(" ) ");
        // 敏感客户
        parser.addSQL(" Union ");
        parser.addSQL(" Select t.cust_manager_id,t.User_Id,t.serial_number,t.cust_name, ");
        parser.addSQL(" '2' state_code,'敏感客户' state, ");
        parser.addSQL(" '新闻早晚报' SERVICE_NAME , ");
        parser.addSQL(" '0' RECOMMEND, '0' TRY, '0' ORDER1 ");
        parser.addSQL(" From Tf_f_Cust_Vip t Where 1=1 ");
        parser.addSQL(" And t.cust_manager_id = :CUST_MANAGER_ID ");
        parser.addSQL(" And t.Remove_Tag = '0'  ");
        parser.addSQL(" And Exists ");
        parser.addSQL(" ( ");
        parser.addSQL(" Select 1 From tf_f_user_platsvc t1 Where t1.Biz_State_Code = 'E' And t1.start_date > Sysdate-60 ");
        parser.addSQL(" And t1.service_id = :SERVICE_ID ");
        parser.addSQL(" And t1.user_id = t.user_id ");
        parser.addSQL(" ) ");
        return Dao.qryByParse(parser, page);
    }

    public static void updateVipSimBak(String resCode, String updateStaffId, String updateDepartId) throws Exception
    {
        IData vipSimData = new DataMap();
        vipSimData.put("START_DATE", "");
        vipSimData.put("SIM_CARD_NO", resCode);
        vipSimData.put("ACT_TAG", "1");
        vipSimData.put("UPDATE_STAFF_ID", updateStaffId);
        vipSimData.put("UPDATE_DEPART_ID", updateDepartId);
        vipSimData.put("REMARK", "备卡激活返销");
        Dao.executeUpdateByCodeCode("TF_F_CUST_VIPSIMBAK", "UPD_SIMBAKACT", vipSimData);
    }

    public IDataset telVipCustPersonInfos(IData params) throws Exception
    {

        IDataset dataset = Dao.qryByCode("TF_SM_TEL_CUSTVIP", "SEL_BY_SN", params);

        if (dataset != null && dataset.size() > 0)
        {
            return dataset;
        }

        return null;
    }
    
    /**
     * 
     * @Description: 用户是否是个人大客户【是:true】
     * @param userId
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Sep 23, 2014 11:22:22 PM
     */
    public static boolean isPersonCustVip(String userId) throws Exception
    {
        IDataset vipDataset = qryVipInfoByUserId(userId);
        
        if (IDataUtil.isNotEmpty(vipDataset))
        {
            String vipClassId = vipDataset.getData(0).getString("VIP_CLASS_ID");
            String vipTypeCode = vipDataset.getData(0).getString("VIP_TYPE_CODE");
            
            if(StringUtils.isNotBlank(vipClassId) && StringUtils.isNotBlank(vipTypeCode) && "0".equals(vipTypeCode))
            {
                String vipClassName = UVipClassInfoQry.getVipClassNameByVipTypeCodeClassId(vipTypeCode, vipClassId);
                if(StringUtils.isNotBlank(vipClassName))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static IDataset querySimBakInfo(String serialNumber, String removeTag) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("REMOVE_TAG", removeTag);
        IDataset vipInfo = Dao.qryByCode("TF_F_CUST_VIP", "SEL_BY_SN_ALLINFO_NOW", param);
        if (IDataUtil.isNotEmpty(vipInfo))
        {
        	int vipInfoSize = vipInfo.size();
            for(int i = 0 ; i < vipInfoSize ; i++){
            	IData vipInfoData = vipInfo.getData(i);
            	String departName = StaticUtil.getStaticValue(BizBean.getVisit(),"TD_M_DEPART","DEPART_ID","DEPART_NAME", vipInfoData.getString("JOIN_DEPART_ID",""));
            	vipInfoData.put("CLIENT_INFO3", departName);//JOIN_DEPART_ID
            	String staffName = StaticUtil.getStaticValue(BizBean.getVisit(),"TD_M_STAFF","STAFF_ID","STAFF_NAME", vipInfoData.getString("JOIN_STAFF_ID",""));
            	vipInfoData.put("CLIENT_INFO4", staffName);//JOIN_STAFF_ID
            	String className = StaticUtil.getStaticValue(BizBean.getVisit(),"TD_M_VIPCLASS",new String[] {"VIP_TYPE_CODE","CLASS_ID"},"CLASS_NAME", new String[] {vipInfoData.getString("VIP_TYPE_CODE",""),vipInfoData.getString("VIP_CLASS_ID","")});
            	vipInfoData.put("CLIENT_INFO5", className);//VIP_CLASS_ID  VIP_TYPE_CODE
            	String classNameB = StaticUtil.getStaticValue(BizBean.getVisit(),"TD_M_VIPCLASS",new String[] {"VIP_TYPE_CODE","CLASS_ID"},"CLASS_NAME", new String[] {vipInfoData.getString("VIP_TYPE_CODE_B",""),vipInfoData.getString("VIP_CLASS_ID_B","")});
            	vipInfoData.put("PREVALUE2", classNameB);
            	String vipTypeName = StaticUtil.getStaticValue(BizBean.getVisit(),"TD_M_VIPTYPE","VIP_TYPE_CODE","VIP_TYPE", vipInfoData.getString("VIP_TYPE_CODE",""));
            	vipInfoData.put("PREVALUE3", vipTypeName);
            	String vipTypeNameB = StaticUtil.getStaticValue(BizBean.getVisit(),"TD_M_VIPTYPE","VIP_TYPE_CODE","VIP_TYPE", vipInfoData.getString("VIP_TYPE_CODE_B",""));
            	vipInfoData.put("PREVALUE4", vipTypeNameB);
            }
        }
        return vipInfo;
    }
    /**
     * add by duhj
     * 2017/5/3 合版本新增
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryVipInfo(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);       
        return Dao.qryByCode("TF_F_CUST_VIP", "SEL_BY_USERID", param);
    }
}
