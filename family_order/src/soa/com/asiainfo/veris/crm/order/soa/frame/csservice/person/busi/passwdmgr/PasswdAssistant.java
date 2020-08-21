
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.log.PasswordErrLogQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEncryptGeneInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class PasswdAssistant extends CSBizBean
{

    /**
     * 获取短信计数器阀值
     * 
     * @param inData
     * @throws Exception
     */
    public static IDataset getCommpara(String strParamAttr, String strParamCode) throws Exception
    {

        IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", strParamAttr);
        param.put("PARAM_CODE", strParamCode);
        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        return CommparaInfoQry.getCommparaAllCol("CSM", strParamAttr, strParamCode, BizRoute.getRouteId());
    }

    /**
     * 获取客户资料
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static IData getCustInfo(String strCustId) throws Exception
    {
        IData listCustInfo = UcaInfoQry.qryCustomerInfoByCustId(strCustId);
        if (IDataUtil.isEmpty(listCustInfo))
        {
            CSAppException.apperr(CustException.CRM_CUST_27, strCustId);
        }
        return listCustInfo;
    }

    /**
     * 获取用户今天密码错误计数器
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static int getErrorCountNow(String strUserId) throws Exception
    {
        IDataset listErrorLog = PasswordErrLogQry.queryPasswordErrLogCount(strUserId, "1", getVisit().getInModeCode());
        if (IDataUtil.isEmpty(listErrorLog))
        {
            return 0;
        }
        else
        {
            return listErrorLog.getData(0).getInt("ERROR_COUNT", 0);
        }
    }

    /**
     * 获取短信计数器阀值
     * 
     * @param inData
     * @throws Exception
     */
    public static int getErrorCountParam(String strParamAttr, String strParamCode) throws Exception
    {

        IDataset listCommPara = getCommpara(strParamAttr, strParamCode);
        if (IDataUtil.isEmpty(listCommPara))
        {
            return 0;
        }
        else
        {
            return Integer.parseInt((String) listCommPara.get(0, "PARA_CODE1"));
        }
    }

    /**
     * 获取允许密码连续输入错误次数
     * 
     * @return
     * @throws Exception
     */
    public static int getErrorLimitCount() throws Exception
    {
        IData tagInfo = TagInfoQry.getCsmTagInfo("CSB", "CS_NUM_PASSWORDERRORINPUT", "2", "3");
        if (IDataUtil.isEmpty(tagInfo))
        {
            return 0;
        }
        else
        {
            return tagInfo.getInt("TAG_NUMBER", 3);
        }
    }

    /**
     * 得到密码管理配置的 PARA_ATTR = 4445 的相关参数
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static IData getPassPara(IData inData) throws Exception
    {

        IDataset listPassPara = CommparaInfoQry.getCommparaAllCol("CSM", "4451", inData.getString("CHECK_MODE", "1"), BizRoute.getRouteId());
        if (IDataUtil.isEmpty(listPassPara))
        {
            IData outData = new DataMap();
            outData.put("PARA_CODE1", "0");
            outData.put("PARA_CODE2", "0");
            outData.put("PARA_CODE3", "0");
            outData.put("PARA_CODE4", "0");
            outData.put("PARA_CODE5", "0");
            return outData;
        }
        else if (listPassPara.size() == 1)
        {
            return listPassPara.getData(0);
        }
        else if (listPassPara.size() == 2)
        {
            for (int i = 0; i < listPassPara.size(); i++)
            {
                if (StringUtils.equals(BizRoute.getRouteId(), listPassPara.getData(i).getString("EPARCHY_CODE")))
                {
                    return listPassPara.getData(i);
                }
            }
        }
        CSAppException.apperr(ParamException.CRM_PARAM_287);
        return null;
    }

    /**
     * 获取手机号码归属地州
     * 
     * @param strSerialNumber
     * @return
     * @throws Exception
     */
    public static String getRouteEparchyCode(String strSerialNumber) throws Exception
    {

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", strSerialNumber);
        String moffice = RouteInfoQry.getEparchyCodeBySn(strSerialNumber);
        if (StringUtils.isNotBlank(moffice))
        {
            IData dataset = UcaInfoQry.qryUserMainProdInfoBySn(strSerialNumber);
            if (IDataUtil.isEmpty(dataset))
            {
                if (strSerialNumber.compareTo("089888146000") >= 0 && strSerialNumber.compareTo("089888149999") <= 0)
                {
                    return strSerialNumber.substring(0, 4);
                }
            }
            else
            {
                return dataset.getString("EPARCHY_CODE");
            }

            CSAppException.apperr(CrmUserException.CRM_USER_391, strSerialNumber);
        }
        return moffice;
    }

    /**
     * 截取USER_ID，获得加密参数
     * 
     * @param strUserID
     * @return
     * @throws Exception
     */
    public static String getSubUserId(String strUserID) throws Exception
    {

        String strSubUserId = null;
        if (strUserID.length() < 9)
        {
            strSubUserId = strUserID;
            for (int i = 0; i < (9 - strUserID.length()); i++)
            {
                strSubUserId = "0" + strSubUserId;
            }
        }
        else
        {
            strSubUserId = strUserID.substring(strUserID.length() - 9, strUserID.length());
        }
        return strSubUserId;
    }

    /**
     * 根据手机号码获取用户信息
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static IData getUserInfo(String strSerialNumber, String strUserId) throws Exception
    {
        IData userInfo = new DataMap();
        if (!StringUtils.isBlank(strUserId) && !StringUtils.equals(strUserId, "-1"))
        {
            userInfo = UcaInfoQry.qryUserInfoByUserId(strUserId);
            if (IDataUtil.isEmpty(userInfo))
            {
                userInfo = UserInfoQry.qryUserInfoByUserIdFromHis(strUserId);
                if (IDataUtil.isEmpty(userInfo))
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_386, strUserId);
                }
            }
        }
        else
        {
            userInfo = UcaInfoQry.qryUserInfoBySn(strSerialNumber);
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_389, strSerialNumber);
            }
        }
        return userInfo;
    }

    /**
     * 根据USER_ID 获取用户信息
     * 
     * @param strUserId
     * @return
     * @throws Exception
     */
    public static IData getUserInfoByUserID(String strUserId) throws Exception
    {

        IData listUserInfo = UcaInfoQry.qryUserMainProdInfoByUserId(strUserId);
        if (IDataUtil.isEmpty(listUserInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_387, strUserId);
        }
        return listUserInfo;
    }

    /**
     * 查寻用户密码加密因子
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static String getUserPasswdKey(String userId) throws Exception
    {
        IDataset res = UserEncryptGeneInfoQry.getEncryptGeneBySn(userId);
        if (IDataUtil.isEmpty(res))
            return null;
        else
        {
            String temp = "000000000" + res.getData(0).getString("ENCRYPT_GENE");
            return temp.substring(temp.length() - 9);
        }
    }

    /**
     * 获取用户SIM卡信息
     * 
     * @param userInfo
     * @return
     * @throws Exception
     */
    public static IData getUserSimInfo(String userId) throws Exception
    {
        IDataset listUserSimInfo = UserResInfoQry.queryUserSimInfo(userId, "1");
        if (IDataUtil.isEmpty(listUserSimInfo))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_47);
        }
        return listUserSimInfo.getData(0);
    }

    /**
     * 登记台账
     * 
     * @param inData
     * @param iTradeTypeCode
     * @throws Exception
     */
    public static void insertTfBhTrade(IData inData, IData userInfo, IData custInfo, String tradeTypeCode) throws Exception
    {

        IData param = new DataMap();
        String strTradeId = SeqMgr.getTradeId();
        inData.put("TRADE_ID", strTradeId);
        String strTradeStaffId;
        String strTradeDepartId;
        String strTradeCityId;
        String strTradeEparchyId;
        if (StringUtils.isBlank(getVisit().getStaffId()))
        {
            strTradeStaffId = "PASSMGR";
        }
        else
        {
            strTradeStaffId = getVisit().getStaffId();
        }
        if (StringUtils.isBlank(getVisit().getDepartId()))
        {
            strTradeDepartId = "00000";
        }
        else
        {
            strTradeDepartId = getVisit().getDepartId();
        }
        if (StringUtils.isBlank(getVisit().getCityCode()))
        {
            strTradeCityId = "0000";
        }
        else
        {
            strTradeCityId = getVisit().getCityCode();
        }
        if (StringUtils.isBlank(getVisit().getStaffEparchyCode()))
        {
            strTradeEparchyId = "0000";
        }
        else
        {
            strTradeEparchyId = getVisit().getStaffEparchyCode();
        }

        String strAcceptMonth = StrUtil.getAcceptMonthById(strTradeId);
        String strAcceptDate = SysDateMgr.getSysTime();

        param.put("TRADE_ID", strTradeId);
        param.put("ACCEPT_MONTH", strAcceptMonth);
        param.put("ORDER_ID", "-1");
        param.put("BPM_ID", "");
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("PRIORITY", "300");
        param.put("SUBSCRIBE_TYPE", "0");
        param.put("SUBSCRIBE_STATE", "9");
        param.put("NEXT_DEAL_TAG", "0");
        param.put("IN_MODE_CODE", "0");
        param.put("CUST_ID", custInfo.getString("CUST_ID", "-1"));
        param.put("CUST_NAME", custInfo.getString("CUST_NAME"));
        param.put("USER_ID", userInfo.getString("USER_ID"));
        param.put("ACCT_ID", "-1");
        param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        param.put("NET_TYPE_CODE", "00");
        param.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        param.put("CITY_CODE", userInfo.getString("CITY_CODE"));
        param.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID"));
        param.put("BRAND_CODE", userInfo.getString("BRAND_CODE"));
        param.put("ACCEPT_DATE", strAcceptDate);
        param.put("TRADE_STAFF_ID", strTradeStaffId);
        param.put("TRADE_DEPART_ID", strTradeDepartId);
        param.put("TRADE_CITY_CODE", strTradeCityId);
        param.put("TRADE_EPARCHY_CODE", strTradeEparchyId);
        param.put("OPER_FEE", "0");
        param.put("FOREGIFT", "0");
        param.put("ADVANCE_PAY", "0");
        param.put("FEE_STATE", "0");
        param.put("PROCESS_TAG_SET", "0");
        param.put("OLCOM_TAG", "1");
        param.put("FINISH_DATE", strAcceptDate);
        param.put("EXEC_TIME", strAcceptDate);
        param.put("CANCEL_TAG", "0");
        param.put("RSRV_STR1", inData.getString("PSPT_ID"));
        Dao.insert("TF_BH_TRADE", param,Route.getJourDb());
    }

    /**
     * 密码错误次数计数器
     * 
     * @param inData
     * @throws Exception
     */
    public static void insertTfBPasswdErrorlog(IData inData) throws Exception
    {
        IData param = new DataMap();
        String sysDate = SysDateMgr.getSysTime();
        String endDate = SysDateMgr.getLastSecond(SysDateMgr.getTomorrowDate());
        param.put("USER_ID", inData.getString("USER_ID"));
        param.put("PASSWD_TYPE_CODE", "1");
        param.put("IN_MODE_CODE", getVisit().getInModeCode());
        param.put("RSRV_NUM1", 0);
        param.put("RSRV_NUM2", 0);
        param.put("RSRV_STR1", "");
        param.put("RSRV_STR2", "");
        param.put("RSRV_DATE1", "");
        param.put("RSRV_DATE2", "");
        param.put("START_DATE", sysDate);
        param.put("END_DATE", endDate);
        param.put("INST_ID", SeqMgr.getInstId());
        param.put("UPDATE_TIME", sysDate);
        param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        param.put("REMARK", "密码输入错误信息记录");
        Dao.executeUpdateByCodeCode("TF_F_USER_PASSWD_ERROR", "INS_PASSWD_ERROR_INFO", param);
    }

    /**
     * 用户是弱密码, 插入TF_F_USER_OTHER
     * 
     * @param userInfo
     * @throws Exception
     */
    public static void insertTfFUserOther(IData inData) throws Exception
    {
        IData inparams = new DataMap();
        String sysdate = SysDateMgr.getSysTime();
        String tradeId = inData.getString("TRADE_ID", "");
        String instId = SeqMgr.getInstId();
        String endDate = SysDateMgr.getLastSecond(SysDateMgr.getTomorrowDate());
        String userId = inData.getString("USER_ID");
        inparams.put("TRADE_ID", tradeId);
        inparams.put("PARTITION_ID", new Long(userId) % 10000);
        inparams.put("USER_ID", userId);
        inparams.put("INST_ID", instId);
        inparams.put("RSRV_VALUE_CODE", "PWDLOCK");
        inparams.put("RSRV_VALUE", tradeId);
        inparams.put("START_DATE", sysdate); // 开始锁定时间为当前系统时间
        inparams.put("END_DATE", endDate); // 锁定一个自然日，第二天自动解锁
        inparams.put("STAFF_ID", getVisit().getStaffId());
        inparams.put("DEPART_ID", getVisit().getDepartId());
        inparams.put("MODIFY_TAG", "0");
        inparams.put("UPDATE_TIME", sysdate);
        inparams.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        inparams.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        inparams.put("REMARK", "密码连续输入错误锁定");
        Dao.insert("TF_F_USER_OTHER", inparams);
    }

    /**
     * 短信发送表 iSmsType : 0-弱密码提醒信息; 1-密码错误次数信息; 2-密码锁定信息; 4-密码解锁信息
     * 
     * @param inData
     * @throws Exception
     */
    public static int insertTiOSms(IData inData, String strContent, IData userInfo) throws Exception
    {

        IData param = new DataMap();
        String seq = SeqMgr.getSmsSendId();
        long lngSeqId = Long.parseLong(seq);
        long lngPartitionId = lngSeqId % 1000;
        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        param.put("IN_MODE_CODE", "0");
        param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        param.put("USER_ID", userInfo.getString("USER_ID"));
        param.put("NOTICE_CONTENT", strContent);
        param.put("PRIORITY", "50");
        param.put("STAFF_ID", getVisit().getStaffId());
        param.put("DEPART_ID", getVisit().getDepartId());
        param.put("REMARK", "密码校验短信通知");
        param.put("SEQ_ID", String.valueOf(lngSeqId));
        param.put("PARTITION_ID", String.valueOf(lngPartitionId));
        StringBuilder strSql = new StringBuilder();
        strSql.delete(0, strSql.length());
        strSql.append("INSERT INTO ti_o_sms(sms_notice_id,partition_id,send_count_code,refered_count,eparchy_code,in_mode_code,");
        strSql.append("chan_id,recv_object_type,recv_object,recv_id,sms_type_code,sms_kind_code,");
        strSql.append("notice_content_type,notice_content,force_refer_count,sms_priority,refer_time,");
        strSql.append("refer_staff_id,refer_depart_id,deal_time,deal_state,remark,send_time_code,send_object_code)");
        strSql.append(" values (:SEQ_ID,:PARTITION_ID,'1','0',:EPARCHY_CODE,:IN_MODE_CODE,'C006','00',:SERIAL_NUMBER,:USER_ID,");
        strSql.append(" '20','08','0',:NOTICE_CONTENT,1,:PRIORITY,sysdate,:STAFF_ID,:DEPART_ID,sysdate,'15',:REMARK,1,6)");
        return Dao.executeUpdate(strSql, param);
    }

    /**
     * 判断是否有密码锁
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static boolean isLockPwd(String userId) throws Exception
    {
        IDataset dataset = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "PWDLOCK");
        if (IDataUtil.isEmpty(dataset))
        {
            return false;
        }
        return true;
    }

    /**
     * 查找是否是弱密码用户 新疆特有
     * 
     * @param userInfo
     * @return
     * @throws Exception
     */
    public static int isSplPasswdUser(IData userInfo) throws Exception
    {
        return 0;
    }

    /**
     * 获取今天的错误日志
     * 
     * @param strSerialNumber
     * @return
     * @throws Exception
     */
    public static IDataset queryErrorLogBySerialNumber(String strSerialNumber) throws Exception
    {
        return PasswordErrLogQry.queryPasswordErrLogBySerialNumber(strSerialNumber, null);
    }

    /**
     * 删除实时记录表数据
     * 
     * @param strSerialNumber
     * @throws Exception
     */
    public static void removeTfBPasswdErrorlog(String strUserId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", strUserId);
        Dao.executeUpdateByCodeCode("TF_F_USER_PASSWD_ERROR", "DEL_PASSWD_ERROR_INFO", param);
    }

    /**
     * 如果是身份证， 统一截成15位判断
     * 
     * @param strInPsptTypeCode
     * @param strInPsptId
     * @return
     * @throws Exception
     */
    public static String standPsptId(String strPsptTypeCode, String strPsptId) throws Exception
    {

        if (StringUtils.equals(strPsptTypeCode, "0") || StringUtils.equals(strPsptTypeCode, "1")) // 为身份证或外地身份证
        {
            if (strPsptId.length() >= 18)
            {
                strPsptId = strPsptId.substring(0, 6) + strPsptId.substring(8, 17);
            }
        }
        return new String(strPsptId);
    }
    
    
    /**
     *  查询无手机宽带账号资源
     * @param inParam
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public static IDataset queryWideNetAccoutInfo(IData inParam) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCOUNT_ID", inParam.getString("ACCOUNT_ID"));
        param.put("STATE", inParam.getString("STATE"));
        IDataset widenetAcctInfo = Dao.qryByCode("TD_B_WIDENET_ACCOUNT", "SEL_TD_B_WIDENET_ACCOUNT", param, Route.CONN_CRM_CEN);
        return widenetAcctInfo;
    }

}
