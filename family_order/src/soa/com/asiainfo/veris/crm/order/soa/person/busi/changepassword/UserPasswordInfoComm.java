
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSmsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPasswdQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;

/**
 * 密码修改接口服务
 * 
 * @author liutt
 */
public class UserPasswordInfoComm
{

    private static Logger log = Logger.getLogger(UserPasswordInfoComm.class);

    private static final long serialVersionUID = 1L;

    /**
     * 手机营业厅-客服密码修改接口服务
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset changePwdInfoForPhone(IData data) throws Exception
    {
        String idtype = data.getString("IDTYPE", "");
        String idValue = data.getString("IDVALUE", "");

        if (!"01".equals(idtype))
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_30);// 标识类型错误
        }
        if (StringUtils.isBlank(idValue))
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_31);// 标识号码错误
        }

        String ccpasswd = data.getString("CCPASSWD");
        String nnpasswd = data.getString("NNPASSWD");
        if (StringUtils.isNotBlank(ccpasswd) && StringUtils.equals(ccpasswd, nnpasswd))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_255);// 512004新旧密码不能一致
        }
        data.put("X_MANAGEMODE", "2");// 修改密码
        data.put("USER_PASSWD", ccpasswd);// 老密码
        data.put("X_NEW_PASSWD", nnpasswd);// 新密码
        data.put("SERIAL_NUMBER", idValue);

        IDataset dataset = changeUserPwdInfo(data);
        return dataset;

    }

    /**
     * 服务密码修改(来自外围接口) X_MANAGEMODE 字段说明： 0=申请密码；1=取消密码 ；2=修改密码；3=随机密码（密码为空的情况下）；4=重置密码（用户使用证件号码进行密码重置,同样需要用户输入新的密码）；
     * 5=随机密码（需证件号码）；7=随机密码（需证件号码）；（5、7没有发现有区别，可以删掉7） 8=密保服务重置方式，选择这种方式的话，接口可以直接传入新密码来进行密码重置; 9=重置密码（不要任何校验）
     */
    public static IDataset changeUserPwdInfo(IData data) throws Exception
    {
        String managerMode = IDataUtil.chkParam(data, "X_MANAGEMODE");// 修改密码类型
        String psw = data.getString("USER_PASSWD"); // 用户输入的旧密码
        String newPsw = data.getString("X_NEW_PASSWD"); // 用户输入的新密码
        String serialNumber = data.getString("SERIAL_NUMBER");
        if ("1".equals(data.getString("X_CNVTAG", "")))
        { // 判断是否传入的是密文
            if (StringUtils.isNotBlank(psw))
            {
                psw = PasswordUtil.decrypt(psw);// 解密
                data.put("USER_PASSWD", psw);
            }
            if (StringUtils.isNotBlank(newPsw))
            {
                newPsw = PasswordUtil.decrypt(newPsw);// 解密
                data.put("NEW_PASSWD", newPsw);
            }
        }
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_87);// 未找到用户资料
        }

        IData custInfo = CustomerInfoQry.qryCustInfo(userInfo.getString("CUST_ID"));
        if (IDataUtil.isEmpty(custInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_397);// 获取客户资料无数据!
        }
        String oldPsw = userInfo.getString("USER_PASSWD");
        String oldPsptId = custInfo.getString("PSPT_ID");
        String userId = userInfo.getString("USER_ID");
        String newPsptId = data.getString("PSPT_ID", "");
        PasswordUtil.checkPwdComplx(managerMode, serialNumber, oldPsptId, newPsw); // 新密码进行复杂度校验
        PasswordUtil.judgeManagerMode(managerMode, psw, newPsw, oldPsw, newPsptId, oldPsptId, "1", userId);// 针对不同的变更类型检验服务密码
        String passwdType = PasswordUtil.getPasswdTypeByManagemode(managerMode);

        IData param = new DataMap();
        param.put("NEW_PASSWD", newPsw);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("X_MANAGEMODE", managerMode);
        param.put("PASSWD_TYPE", passwdType);
        param.put("TRADE_TYPE_CODE", "71");// 密码修改服务

        IDataset dataset = CSAppCall.call("SS.ModifyUserPwdInfoRegSVC.tradeReg", param);
        return dataset;
    }

    /**
     * 校验用户回答密码保护问题是否正确
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData checkProtectPass(IData data) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataset userset = UserInfoQry.getUserInfoBySN(serialNumber, "0", "00");
        if (IDataUtil.isEmpty(userset))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_395);// 880002:获取用户信息无数据！
        }
        String userId = userset.getData(0).getString("USER_ID");
        boolean resultFirst = false;
        boolean resultSecond = false;
        boolean resultThird = false;
        String checkEmail = "";
        int count = 0;// 回答正确数量

        IDataset usersvcset = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(userId, "3312");
        if (IDataUtil.isNotEmpty(usersvcset))
        {
            IDataset userotherset = UserOtherInfoQry.getUserOtherInfoByAll(userId, "SPWP");

            if (IDataUtil.isNotEmpty(userotherset))
            {
                IData userotherData = userotherset.getData(0);
                checkEmail = userotherData.getString("RSRV_STR17");
                if (StringUtils.equals(userotherData.getString("RSRV_STR12"), data.getString("ANSWER_FIRST")))
                {
                    count += 1;
                    resultFirst = true;
                }
                if (StringUtils.equals(userotherData.getString("RSRV_STR14"), data.getString("ANSWER_SECOND")))
                {
                    count += 1;
                    resultSecond = true;
                }
                if (StringUtils.equals(userotherData.getString("RSRV_STR16"), data.getString("ANSWER_THIRD")))
                {
                    count += 1;
                    resultThird = true;
                }
            }
        }
        IData otherParam = new DataMap();
        otherParam.put("CHECK_FLAG_FIRST", resultFirst);
        otherParam.put("CHECK_FLAG_SECOND", resultSecond);
        otherParam.put("CHECK_FLAG_THIRD", resultThird);
        otherParam.put("RIGHT_COUNT", count);
        otherParam.put("CHECK_EMAIL", checkEmail);
        return otherParam;
    }

    /**
     * 通过userId校验用户密码是否正确
     * 
     * @param userId
     *            用户编号
     * @param pwd
     *            明文密码
     * @return
     * @throws Exception
     */
    public static IDataset checkPwdByUserId(String userId, String pwd) throws Exception
    {
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
        if (IDataUtil.isEmpty(userInfo))
        {
            // 查一下销户用户资料表
            userInfo = UserInfoQry.qryUserInfoByUserIdFromHis(userId);
            if (IDataUtil.isEmpty(userInfo))
            {
                // 不能查询到则报错
                CSAppException.apperr(CrmUserException.CRM_USER_189, userId);
            }
        }

        String userOldPsw = userInfo.getString("USER_PASSWD");
        if (StringUtils.isBlank(userOldPsw))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_81);// 450000: 用户服务密码不存在！
        }

        boolean flag = PasswdMgr.checkUserPassword(pwd, userId, userOldPsw);// 一定要传入原密码，避免方法里面再去查询user信息。
        IDataset dataset = new DatasetList();
        IData map = new DataMap();
        map.put("CHECK_RESULT", flag);// 返回校验结果
        dataset.add(map);
        return dataset;
    }

    /**
     * 校验用户密码是否为初始密码
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData checkUserDefaultPWD(IData data) throws Exception
    {
        IData result = new DataMap();
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_87);// 未找到用户资料
            result.put("X_RESULTCODE", "450007");
            result.put("X_CHECK_INFO", "450007:用户资料不存在");
            result.put("X_RESULTINFO", "用户资料不存在");
            return result;
        }
        String userOldPsw = userInfo.getString("USER_PASSWD");
        if (StringUtils.isBlank(userOldPsw))
        {
            result.put("X_RESULTCODE", "450000");
            result.put("X_CHECK_INFO", "450000:用户服务密码不存在");
            result.put("X_RESULTINFO", "用户服务密码不存在");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;

        }
        boolean flag = false;
        String tagInfo = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_TAG", new String[]
        { "EPARCHY_CODE", "TAG_CODE" }, "TAG_INFO", new String[]
        { "0898", "CS_INF_DEFAULTPWD" });
        if (StringUtils.isNotBlank(tagInfo))
        {
            String password = PasswdMgr.encryptPassWD(data.getString("USER_PASSWD"), userInfo.getString("USER_ID"));

            String defaultPw = PasswdMgr.encryptPassWD(tagInfo, userInfo.getString("USER_ID"));

            if (StringUtils.equals(password, defaultPw))
            {
                flag = true;
            }
        }
        if (flag)
        {
            result.put("X_RESULTCODE", "450001");
            result.put("X_CHECK_INFO", "450067:密码存在，且为初始化密码");
            result.put("X_RESULTINFO", "密码存在，且为初始化密码");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
        else
        {
            result.put("X_RESULTCODE", "450003");
            result.put("X_CHECK_INFO", "450068:密码存在，且不为初始化密码");
            result.put("X_RESULTINFO", "密码存在且不是初始化密码");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }

    }

    /**
     * 判断用户是否存在互联网密码
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData checkUserHasInternetPWD(IData data) throws Exception
    {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_CHECK_INFO", "0"); // 默认校验成功
        result.put("X_RESULTINFO", "Trade OK!");

        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_87);// 未找到用户资料
            result.put("X_RESULTCODE", "450007");
            result.put("X_CHECK_INFO", "3");
            result.put("X_RESULTINFO", "用户资料不存在");
            return result;
        }
        String netOldPsw = userInfo.getString("RSRV_STR5");
        // 用户互联网密码不存在
        if (StringUtils.isBlank(netOldPsw))
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_1108);// 450000用户互联网密码不存在
            result.put("X_RESULTCODE", "450000");
            result.put("X_CHECK_INFO", "1");
            result.put("X_RESULTINFO", "用户互联网密码不存在");
            return result;
        }
        return result;
    }

    /**
     * 校验用户互联网密码
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData checkUserInternetPWD(IData data) throws Exception
    {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_CHECK_INFO", "0"); // 默认校验成功
        result.put("X_RESULTINFO", "Trade OK!");
        String netNewpsw = data.getString("USER_PASSWD", "");
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        if ("1".equals(data.getString("X_CNVTAG", "")))
        {
            if (null != netNewpsw)
            {
                netNewpsw = PasswordUtil.decrypt(netNewpsw);
                data.put("USER_PASSWD", netNewpsw);
            }
        }
        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_87);// 未找到用户资料
            result.put("X_RESULTCODE", "450007");
            result.put("X_CHECK_INFO", "3");
            result.put("X_RESULTINFO", "用户资料不存在");
            return result;
        }
        data.put("USER_ID", userInfo.getString("USER_ID"));
        data.put("BRAND_NAME", userInfo.getString("BRAND_NAME"));
        String userId = userInfo.getString("USER_ID");
        String netOldPsw = userInfo.getString("RSRV_STR5");
        // 用户互联网密码不存在
        if (StringUtils.isBlank(netOldPsw))
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_1108);// 450000用户互联网密码不存在
            result.put("X_RESULTCODE", "450000");
            result.put("X_CHECK_INFO", "1");
            result.put("X_RESULTINFO", "用户互联网密码不存在");
            return result;
        }
        String eparchyCode = CSBizBean.getUserEparchyCode();
        IData tagInfo = ParamInfoQry.getCsmTagInfo(eparchyCode, "CS_NUM_PASSWORDERRORINPUT", "CSB", "0", "2", "3");
        int pwdErrorNum = tagInfo.getInt("TAG_NUMBER", 3);// 密码锁定的阈值
        // 查询密码是否已锁定
        IDataset userOtherSet = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "PWDLOCK");
        String inModeCode = data.getString("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        if (IDataUtil.isNotEmpty(userOtherSet))
        {
            IDataset errorData = UserPasswdQry.queryPasswdErrorCount(userId, "", "");
            int errorCount = errorData.getData(0).getInt("ERROR_COUNT", 0);
            result.put("X_SELCOUNT", errorCount); // 密码校验不成功次数
            result.put("X_OLD_COUNT", pwdErrorNum); // 密码锁定的阈值
            result.put("X_RESULTCODE", "450009");
            result.put("X_CHECK_INFO", "1");
            result.put("X_RESULTINFO", "当日输入错误密码已达" + pwdErrorNum + "次，请明日再试！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
        if (netNewpsw.trim().length() < 6 || netNewpsw.trim().length() > 16)
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_110);// 450006:密码长度不正确
            result.put("X_RESULTCODE", 450006);
            result.put("X_CHECK_INFO", "密码长度不正确");
            result.put("X_RESULTINFO", "密码长度不正确");
            return result;
        }
        // String encryNewNetPwd = PasswdMgr.getUserEncryptPassWD(netNewpsw,userId);
        if (PasswdMgr.checkUserPassword(netNewpsw, userId, netOldPsw))
        {// 密码正确
            // 在允许连续输入错误次数的范围内，如果密码输入正确，则清楚之前记录的密码输入错误信息
            delPwdErrInfo(userId);
        }
        else
        {// 密码错误
            recordPasswdErrorInfo(userId, "1", inModeCode);// 第二个参数:设置互联网密码类型，1-客服密码 2-互联网密码,这里老系统传的是1,不知道为什么
            IDataset errorData = UserPasswdQry.queryPasswdErrorCount(userId, "", "");
            int errorCount = errorData.getData(0).getInt("ERROR_COUNT", 0);
            // 连续输错3次，自动锁定密码
            // 每出错一次就在原来的基础上加一
            // 判断是否达到锁定次数
            if (errorCount >= pwdErrorNum)
            {
                data.put("ERROR_NUM", pwdErrorNum);
                data.put("LOCK_IN_MODE_CODE", data.getString("IN_MODE_CODE"));// IN_MODE_CODE传不过去，所以更改KEY名
                CSAppCall.call("SS.LockUserPwdRegSVC.tradeReg", data);// 调用密码锁定登记服务
                result.put("X_RESULTCODE", "450009");
                result.put("X_CHECK_INFO", "1");
                result.put("X_SELCOUNT", errorCount); // 密码校验不成功次数
                result.put("X_OLD_COUNT", pwdErrorNum); // 密码锁定的阈值
                result.put("X_RESULTINFO", "当日输入错误密码已达" + pwdErrorNum + "次，请明日再试！");
                result.put("X_RSPTYPE", "2");// add by ouyk
                result.put("X_RSPCODE", "2998");// add by ouyk
                return result;
            }
            else
            {
                // 互联网密码校验插入短信下发表;
                String smsContent = getSmsContent(data, "互联网");
                recordMessage(data, smsContent, "用户互联网密码输入错误");
                insertUserOther(data, "用户互联网密码输入错误");

            }

            result.put("X_RESULTCODE", "450001");
            result.put("X_CHECK_INFO", "2");
            result.put("X_SELCOUNT", errorCount); // 密码校验不成功次数
            result.put("X_OLD_COUNT", pwdErrorNum); // 密码锁定的阈值
            result.put("X_RESULTINFO", "当日累计" + (errorCount) + "次密码输入错误!");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;

        }
        return result;

    }

    /**
     * 校验用户密码
     * 
     * @param data
     *            必备参数SERIAL_NUMBER,USER_PASSWD（明文）
     * @return X_CHECK_INFO,X_RESULTCODE,X_RESULTINFO
     * @throws Exception
     */
    public static IData checkUserPWD(IData data) throws Exception
    {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_CHECK_INFO", "0"); // 默认校验成功
        result.put("X_RESULTINFO", "Trade OK!");
        String userNewPsw = IDataUtil.chkParam(data, "USER_PASSWD");// 用户需要校验的密码
        if ("1".equals(data.getString("X_CNVTAG", "")))
        { // 判断是否传入的是密文
            if (StringUtils.isNotBlank(userNewPsw))
            {
                userNewPsw = PasswordUtil.decrypt(userNewPsw);// 解密
                data.put("USER_PASSWD", userNewPsw);
            }

        }
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_87);// 未找到用户资料
            result.put("X_RESULTCODE", "450007");
            result.put("X_CHECK_INFO", "3");
            result.put("X_RESULTINFO", "用户资料不存在");
            return result;
        }
        data.put("USER_ID", userInfo.getString("USER_ID"));
        data.put("CUST_ID", userInfo.getString("CUST_ID"));
        // 获取客户资料
        IData custInfo = UcaInfoQry.qryCustInfoByCustId(userInfo.getString("CUST_ID"));
        if (custInfo == null || custInfo.size() < 1)
        {
            result.put("X_RESULTCODE", "450008");
            result.put("X_CHECK_INFO", "3");
            result.put("X_RESULTINFO", "客户资料不存在");
            return result;
        }

        data.put("PSPT_ID", custInfo.getString("PSPT_ID"));
        data.put("PASSWD_TYPE_CODE", "1");
        data.put("BRAND_NAME", userInfo.getString("BRAND_NAME"));
        String userId = userInfo.getString("USER_ID");
        String eparchyCode = CSBizBean.getUserEparchyCode();
        IData tagInfo = ParamInfoQry.getCsmTagInfo(eparchyCode, "CS_NUM_PASSWORDERRORINPUT", "CSB", "0", "2", "3");
        int pwdErrorNum = tagInfo.getInt("TAG_NUMBER", 3);// 密码锁定的阈值
        // 查询密码是否已锁定
        IDataset userOtherSet = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "PWDLOCK");
        if (IDataUtil.isNotEmpty(userOtherSet))
        {
            IDataset errorData = UserPasswdQry.queryPasswdErrorCount(userId, "", "");
            int num = errorData.getData(0).getInt("ERROR_COUNT", 0);
            result.put("X_SELCOUNT", num); // 密码校验不成功次数
            result.put("X_OLD_COUNT", pwdErrorNum); // 密码锁定的阈值
            result.put("X_RESULTCODE", "450009");
            result.put("X_CHECK_INFO", "1");
            result.put("X_RESULTINFO", "当日输入错误密码已达" + pwdErrorNum + "次，请明日再试！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
        String userOldPsw = userInfo.getString("USER_PASSWD");
        if (StringUtils.isBlank(userOldPsw))
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_81);客服要求是 Integer 类型
            result.put("X_RESULTCODE", "450000");
            result.put("X_CHECK_INFO", "1");
            result.put("X_RESULTINFO", "用户服务密码不存在");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;

        }
        if (userNewPsw.trim().length() != 6)
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_110);// 450006密码长度不正确
            result.put("X_RESULTCODE", 450006);
            result.put("X_CHECK_INFO", "密码长度不正确");
            result.put("X_RESULTINFO", "密码长度不正确");
            return result;
        }

        if (StringUtils.isBlank(data.getString("IN_MODE_CODE")))
        {
            data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        }

        if (PasswdMgr.checkUserPassword(userNewPsw, userId, userOldPsw))
        {// 密码正确encryUserNewPwd
            // 在允许连续输入错误次数的范围内，如果密码输入正确，则清楚之前记录的密码输入错误信息
            delPwdErrInfo(userId);
            result = PasswordUtil.checkUserPoorPWD(data);
            if (result.size() > 0)
            {
                // 只要是弱密码提示信息都一样
                if ("0".equals(result.getString("X_RESULTCODE")) && !"0".equals(result.getString("X_CHECK_INFO")))
                {
                    result.put("X_RESULTINFO", "您设置的服务密码较为简单，为保护您个人信息安全，建议您进行修改！");
                }
                return result;
            }
            // 判断是否初始化密码
            boolean isDefaultPwd = false;
            // 查询海南配置的初始密码
            String defaultPwd = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_TAG", new String[]
            { "EPARCHY_CODE", "TAG_CODE" }, "TAG_INFO", new String[]
            { "0898", "CS_INF_DEFAULTPWD" });
            if (StringUtils.isNotBlank(defaultPwd))// 海南根本就没配置初始密码,所以这里始终为空
            {
                String password = PasswdMgr.encryptPassWD(userNewPsw, userId);
                String defaultPw = PasswdMgr.encryptPassWD(defaultPwd, userId);
                if (StringUtils.equals(password, defaultPw))
                {
                    isDefaultPwd = true;
                }
            }
            if (isDefaultPwd)
            {
                result.put("X_RESULTCODE", "0");
                result.put("X_CHECK_INFO", "6");
                result.put("X_RESULTINFO", "密码正确，但为初始化密码");
                return result;
            }
            else
            {
                result.put("X_RESULTCODE", "0");
                result.put("X_CHECK_INFO", "0");
                result.put("X_RESULTINFO", "密码正确，但不是初始化密码");//
                return result;
            }
        }
        else
        {// 密码错误
            recordPasswdErrorInfo(userId, "1", data.getString("IN_MODE_CODE"));// 第二个参数:设置互联网密码类型，1-客服密码
            // 2-互联网密码,这里老系统传的是1,不知道为什么
            IDataset errorData = UserPasswdQry.queryPasswdErrorCount(userId, "", "");
            int num = errorData.getData(0).getInt("ERROR_COUNT", 0);
            // 连续输错3次，自动锁定密码
            // 每出错一次就在原来的基础上加一
            // 判断是否达到锁定次数
            if (num >= pwdErrorNum)
            {
                data.put("ERROR_NUM", pwdErrorNum);
                data.put("LOCK_IN_MODE_CODE", data.getString("IN_MODE_CODE"));// IN_MODE_CODE传不过去，所以更改KEY名
                CSAppCall.call("SS.LockUserPwdRegSVC.tradeReg", data);// 调用密码锁定登记服务
                result.put("X_RESULTCODE", "450009");
                result.put("X_CHECK_INFO", "1");
                result.put("X_SELCOUNT", num); // 密码校验不成功次数
                result.put("X_OLD_COUNT", pwdErrorNum); // 密码锁定的阈值
                result.put("X_RESULTINFO", "当日输入错误密码已达" + pwdErrorNum + "次，请明日再试！");
                result.put("X_RSPTYPE", "2");// add by ouyk
                result.put("X_RSPCODE", "2998");// add by ouyk
                return result;
            }
            else
            {
                String smsContent = getSmsContent(data, "服务");
                recordMessage(data, smsContent, "用户 服务密码输入错误");// 密码校验插入短信下发表;
                insertUserOther(data, "用户服务密码输入错误");
            }

            result.put("X_RESULTCODE", "450001");
            result.put("X_CHECK_INFO", "2");
            result.put("X_SELCOUNT", num); // 密码校验不成功次数
            result.put("X_OLD_COUNT", pwdErrorNum); // 密码锁定的阈值
            result.put("X_RESULTINFO", "当日累计" + (num) + "次密码输入错误!");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
    }
    
    
    
    
    /**
     * 统一认证接口独立，不与和其他接口共用
     * 校验用户密码
     * by mengqx 2018027
     * @param data
     *            必备参数SERIAL_NUMBER,USER_PASSWD（明文）
     * @return X_CHECK_INFO,X_RESULTCODE,X_RESULTINFO
     * @throws Exception
     */
    public static IData checkUserPWDNew(IData data) throws Exception
    {
    	
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_CHECK_INFO", "0"); // 默认校验成功
        result.put("X_RESULTINFO", "业务成功");
        String userNewPsw = IDataUtil.chkParam(data, "USER_PASSWD");// 用户需要校验的密码
        if ("1".equals(data.getString("X_CNVTAG", "")))
        { // 判断是否传入的是密文
            if (StringUtils.isNotBlank(userNewPsw))
            {
                userNewPsw = PasswordUtil.decrypt(userNewPsw);// 解密
                data.put("USER_PASSWD", userNewPsw);
            }

        }
//        // 获取客户资料
//        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(data.getString("CUST_ID"));
//        System.out.println("-------------mqx--------------:获取客户资料："+custInfo.toString());
//        if (custInfo == null || custInfo.size() < 1)
//        {
//            result.put("X_RESULTCODE", "4005");
//            result.put("X_CHECK_INFO", "3");
//            result.put("X_RESULTINFO", "手机账号不存在");
//            return result;
//        }else if("0".equals(custInfo.getString("IS_REAL_NAME"))){
//			result.put("X_RSPCODE", "2998");
//            result.put("X_RSPDESC", "受理失败");
//            result.put("X_RSPTYPE", "2");
//            result.put("X_RESULTCODE", "2031");
//            result.put("X_RESULTINFO", "用户未实名登记");
//            return result;
//		}
//
//        data.put("PSPT_ID", custInfo.getString("PSPT_ID"));
        data.put("PASSWD_TYPE_CODE", "1");
        String userId = data.getString("USER_ID");
        String eparchyCode = CSBizBean.getUserEparchyCode();
        IData tagInfo = ParamInfoQry.getCsmTagInfo(eparchyCode, "CS_NUM_PASSWORDERRORINPUT", "CSB", "0", "2", "3");
        int pwdErrorNum = tagInfo.getInt("TAG_NUMBER", 3);// 密码锁定的阈值
        // 查询密码是否已锁定
        IDataset userOtherSet = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "PWDLOCK");
        if (IDataUtil.isNotEmpty(userOtherSet))
        {
            IDataset errorData = UserPasswdQry.queryPasswdErrorCount(userId, "", "");
            int num = errorData.getData(0).getInt("ERROR_COUNT", 0);
            result.put("X_SELCOUNT", num); // 密码校验不成功次数
            result.put("X_OLD_COUNT", pwdErrorNum); // 密码锁定的阈值
            result.put("X_RESULTCODE", "2046");
            result.put("X_CHECK_INFO", "1");
            result.put("X_RESULTINFO", "手机账号锁定。当日输入错误密码已达" + pwdErrorNum + "次，请明日再试！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
        String userOldPsw = data.getString("USER_OLD_PASSWD");
        if (StringUtils.isBlank(userOldPsw))
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_81);客服要求是 Integer 类型
            result.put("X_RESULTCODE", "450000");
            result.put("X_CHECK_INFO", "1");
            result.put("X_RESULTINFO", "用户服务密码不存在");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;

        }
        if (userNewPsw.trim().length() != 6)
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_110);// 450006密码长度不正确
            result.put("X_RESULTCODE", "2036");
            result.put("X_CHECK_INFO", "密码长度不正确");
            result.put("X_RESULTINFO", "用户密码输入不正确");
            return result;
        }

        if (StringUtils.isBlank(data.getString("IN_MODE_CODE")))
        {
            data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        }
       
        if (PasswdMgr.checkUserPassword(userNewPsw, userId, userOldPsw))
        {// 密码正确encryUserNewPwd
            // 在允许连续输入错误次数的范围内，如果密码输入正确，则清楚之前记录的密码输入错误信息
            delPwdErrInfo(userId);
            result = PasswordUtil.checkUserPoorPWD(data);
            if("0".equals(result.getString("X_RESULTCODE"))){
            	result.put("X_RESULTCODE", "0000");
            }
            if (result.size() > 0)
            {
                // 只要是弱密码提示信息都一样
                if ("0000".equals(result.getString("X_RESULTCODE")) && !"0".equals(result.getString("X_CHECK_INFO")))
                {
                    result.put("X_RESULTINFO", "您设置的服务密码较为简单，为保护您个人信息安全，建议您进行修改！");
                }
                return result;
            }
            // 判断是否初始化密码
            boolean isDefaultPwd = false;
            // 查询海南配置的初始密码
            String defaultPwd = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_TAG", new String[]
            { "EPARCHY_CODE", "TAG_CODE" }, "TAG_INFO", new String[]
            { "0898", "CS_INF_DEFAULTPWD" });
            if (StringUtils.isNotBlank(defaultPwd))// 海南根本就没配置初始密码,所以这里始终为空
            {
                String password = PasswdMgr.encryptPassWD(userNewPsw, userId);
                String defaultPw = PasswdMgr.encryptPassWD(defaultPwd, userId);
                if (StringUtils.equals(password, defaultPw))
                {
                    isDefaultPwd = true;
                }
            }
            if (isDefaultPwd)
            {
                result.put("X_RESULTCODE", "0000");
                result.put("X_CHECK_INFO", "6");
                result.put("X_RESULTINFO", "业务成功。密码正确，但为初始化密码");
                return result;
            }
            else
            {
                result.put("X_RESULTCODE", "0000");
                result.put("X_CHECK_INFO", "0");
                result.put("X_RESULTINFO", "业务成功。密码正确，但不是初始化密码");//
                return result;
            }
        }
        else
        {// 密码错误
            recordPasswdErrorInfo(userId, "1", data.getString("IN_MODE_CODE"));// 第二个参数:设置互联网密码类型，1-客服密码
            // 2-互联网密码,这里老系统传的是1,不知道为什么
            IDataset errorData = UserPasswdQry.queryPasswdErrorCount(userId, "", "");
            int num = errorData.getData(0).getInt("ERROR_COUNT", 0);
            // 连续输错3次，自动锁定密码
            // 每出错一次就在原来的基础上加一
            // 判断是否达到锁定次数
            if (num >= pwdErrorNum)
            {
                data.put("ERROR_NUM", pwdErrorNum);
                data.put("LOCK_IN_MODE_CODE", data.getString("IN_MODE_CODE"));// IN_MODE_CODE传不过去，所以更改KEY名
                CSAppCall.call("SS.LockUserPwdRegSVC.tradeReg", data);// 调用密码锁定登记服务
                result.put("X_RESULTCODE", "2036");
                result.put("X_CHECK_INFO", "1");
                result.put("X_SELCOUNT", num); // 密码校验不成功次数
                result.put("X_OLD_COUNT", pwdErrorNum); // 密码锁定的阈值
                result.put("X_RESULTINFO", "用户密码输入不正确。当日输入错误密码已达" + pwdErrorNum + "次，请明日再试！");
                result.put("X_RSPTYPE", "2");// add by ouyk
                result.put("X_RSPCODE", "2998");// add by ouyk
                return result;
            }
            else
            {
                String smsContent = getSmsContent(data, "服务");
                recordMessage(data, smsContent, "用户 服务密码输入错误");// 密码校验插入短信下发表;
                insertUserOther(data, "用户服务密码输入错误");
            }

            result.put("X_RESULTCODE", "2036");
            result.put("X_CHECK_INFO", "2");
            result.put("X_SELCOUNT", num); // 密码校验不成功次数
            result.put("X_OLD_COUNT", pwdErrorNum); // 密码锁定的阈值
            result.put("X_RESULTINFO", "用户密码输入不正确。当日累计" + (num) + "次密码输入错误!");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
    }


    /**
     * 密码输入错误次数清零:删除TF_F_USER_PASSWD_ERROR表中的相关信息
     * 
     * @param userId
     * @throws Exception
     */
    public static void delPwdErrInfo(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        Dao.executeUpdateByCodeCode("TF_F_USER_PASSWD_ERROR", "DEL_PASSWD_ERROR_INFO", param);
    }

    private static String getSmsContent(IData data, String chanel) throws Exception
    {
        String noticeContent = null;
        IDataset smsInfos = TradeSmsInfoQry.getTradeSmsInfo("890", "-1", null, "0", null, null, null);
        if (IDataUtil.isNotEmpty(smsInfos))
        {
            IData smsContent = smsInfos.getData(0);
            noticeContent = smsContent.getString("NOTICE_CONTENT", "");
        }
        String date = SysDateMgr.getSysTime();
        String inmode = data.getString("IN_MODE_CODE");
        if (inmode == null || "".equals(inmode))
        {
            inmode = "Z";
        }
        String inModeName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_INMODE", "IN_MODE_CODE", "IN_MODE", inmode);
        String brandName = data.getString("BRAND_NAME");
        if (brandName == null)
        {
            brandName = "";
        }
        if (noticeContent != null || "".equals(noticeContent))
        {

            date = SysDateMgr.decodeTimestamp(date, SysDateMgr.PATTERN_CHINA_TIME);
            noticeContent = noticeContent.replace("%1%", brandName);
            noticeContent = noticeContent.replace("%2%", date);
            noticeContent = noticeContent.replace("%3%", inModeName);
            noticeContent = noticeContent.replace("%4%", chanel);
        }
        else
        {
            noticeContent = "提醒服务：您好！您于" + date + "通过" + inmode + "输入" + chanel + "密码错误，服务密码关系您个人信息安全，请注意保护。中国移动";
        }
        return noticeContent;

    }

    /**
     * 插入USER_OTHER表
     * 
     * @param inparams
     * @param make
     * @throws Exception
     */
    private static void insertUserOther(IData inparams, String remark) throws Exception
    {
        String sysdate = SysDateMgr.getSysTime();
        IData inparam = new DataMap();
        inparam.put("PARTITION_ID", Long.valueOf(inparams.getString("USER_ID")) % 10000);
        inparam.put("USER_ID", inparams.getString("USER_ID"));
        inparam.put("RSRV_VALUE_CODE", "MMEE");// 密码错误
        inparam.put("RSRV_VALUE", inparams.getString("SERIAL_NUMBER"));
        inparam.put("RSRV_STR1", inparams.getString("IN_MODE_CODE"));
        inparam.put("STAFF_ID", inparams.getString("TRADE_STAFF_ID"));
        inparam.put("DEPART_ID", inparams.getString("TRADE_DEPART_ID"));
        inparam.put("TRADE_ID", "");
        inparam.put("START_DATE", sysdate);
        inparam.put("END_DATE", sysdate);
        inparam.put("UPDATE_TIME", sysdate);
        inparam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("REMARK", remark);
        inparam.put("INST_ID", SeqMgr.getInstId());
        Dao.insert("TF_F_USER_OTHER", inparam);
    }

    public static void recordMessage(IData data, String noticeContent, String remark) throws Exception
    {
        IData param = new DataMap();
        param.putAll(data);
        param.put("NOTICE_CONTENT", noticeContent);
        param.put("REFER_STAFF_ID", data.getString("TRADE_STAFF_ID"));
        param.put("REFER_DEPART_ID", data.getString("TRADE_DEPART_ID"));
        param.put("RECV_OBJECT", data.getString("SERIAL_NUMBER"));
        param.put("RECV_ID", data.getString("USER_ID"));// 被叫对象标识:传用户标识
        param.put("SMS_PRIORITY", 5000);// 短信优先级
        param.put("SMS_KIND_CODE", "08");
        param.put("CHAN_ID", "C006");
        param.put("DEAL_STATE", "15");
        param.put("REMARK", remark);
        SmsSend.insSms(param);
    }

    /**
     * 密码输入错误信息记录
     * 
     * @param userId
     * @param pwdTypeCode
     * @param inModeCode
     * @throws Exception
     */
    public static void recordPasswdErrorInfo(String userId, String pwdTypeCode, String inModeCode) throws Exception
    {
        String sysdate = SysDateMgr.getSysTime();
        IData param = new DataMap();
        param.put("PARTITION_ID", Long.parseLong(userId) % 10000);
        param.put("USER_ID", userId);
        param.put("PASSWD_TYPE_CODE", pwdTypeCode);
        param.put("IN_MODE_CODE", inModeCode);
        param.put("RSRV_NUM1", "0");
        param.put("RSRV_NUM2", "0");
        param.put("START_DATE", sysdate);
        param.put("END_DATE", SysDateMgr.getLastSecond(SysDateMgr.getTomorrowDate()));
        param.put("INST_ID", SeqMgr.getInstId());
        param.put("UPDATE_TIME", sysdate);
        param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        param.put("REMARK", "密码输入错误信息记录");
        Dao.insert("TF_F_USER_PASSWD_ERROR", param);

    }

    /**
     * 一级BOSS调用密码重发确认接口
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData sendPasswdConfirm(IData data) throws Exception
    {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_CHECK_INFO", "0");
        result.put("X_RESULTINFO", "Trade OK!");
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        String wlanSeq = IDataUtil.chkParam(data, "WLAN_SEQ");
        String oprNumb = IDataUtil.chkParam(data, "OPR_NUMB");
        String smsNoticId = SeqMgr.getSmsSendId();
        data.put("SEQ_ID", smsNoticId.substring(4));// 为了与短信营业厅传过来的12位值统一
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        data.put("OPR_NUMBER", oprNumb);
        data.put("WLAN_CARD_SEQ", wlanSeq);
        data.put("OPR_TYPE", "F0C");// 密码重发确认
        data.put("STATE", "Y0A");
        data.put("OPR_DATE", SysDateMgr.getSysTime());
        data.put("OPR_STAFF_ID", "ITF00000");
        data.put("OPR_DEPART_ID", "ITF00");
        Dao.insert("TF_B_WLAN_FEE_CARD_LOG", data, Route.CONN_CRM_CEN);
        return result;
    }
    
    public static IData checkUserPWDForL2F(IData data) throws Exception
    {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_CHECK_INFO", "0"); // 默认校验成功
        result.put("X_RESULTINFO", "Trade OK!");
        String userNewPsw = IDataUtil.chkParam(data, "USER_PASSWD");// 用户需要校验的密码
        if ("1".equals(data.getString("X_CNVTAG", "")))
        { // 判断是否传入的是密文
            if (StringUtils.isNotBlank(userNewPsw))
            {
                userNewPsw = PasswordUtil.decrypt(userNewPsw);// 解密
                data.put("USER_PASSWD", userNewPsw);
            }

        }
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_87);// 未找到用户资料
            result.put("X_RESULTCODE", "450007");
            result.put("X_CHECK_INFO", "3");
            result.put("X_RESULTINFO", "用户资料不存在");
            return result;
        }
        data.put("USER_ID", userInfo.getString("USER_ID"));
        data.put("CUST_ID", userInfo.getString("CUST_ID"));
        // 获取客户资料
        IData custInfo = UcaInfoQry.qryCustInfoByCustId(userInfo.getString("CUST_ID"));
        if (custInfo == null || custInfo.size() < 1)
        {
            result.put("X_RESULTCODE", "450008");
            result.put("X_CHECK_INFO", "3");
            result.put("X_RESULTINFO", "客户资料不存在");
            return result;
        }

        data.put("PSPT_ID", custInfo.getString("PSPT_ID"));
        data.put("PASSWD_TYPE_CODE", "1");
        data.put("BRAND_NAME", userInfo.getString("BRAND_NAME"));
        String userId = userInfo.getString("USER_ID");
        String eparchyCode = CSBizBean.getUserEparchyCode();
        IData tagInfo = ParamInfoQry.getCsmTagInfo(eparchyCode, "CS_NUM_PASSWORDERRORINPUT", "CSB", "0", "2", "3");
        int pwdErrorNum = tagInfo.getInt("TAG_NUMBER", 3);// 密码锁定的阈值
        // 查询密码是否已锁定
        IDataset userOtherSet = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "PWDLOCK");
        if (IDataUtil.isNotEmpty(userOtherSet))
        {
            IDataset errorData = UserPasswdQry.queryPasswdErrorCount(userId, "", "");
            int num = errorData.getData(0).getInt("ERROR_COUNT", 0);
            result.put("X_SELCOUNT", num); // 密码校验不成功次数
            result.put("X_OLD_COUNT", pwdErrorNum); // 密码锁定的阈值
            result.put("X_RESULTCODE", "450009");
            result.put("X_CHECK_INFO", "1");
            result.put("X_RESULTINFO", "当日输入错误密码已达" + pwdErrorNum + "次，请明日再试！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
        String userOldPsw = userInfo.getString("USER_PASSWD");
        if (StringUtils.isBlank(userOldPsw))
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_81);客服要求是 Integer 类型
            result.put("X_RESULTCODE", "450000");
            result.put("X_CHECK_INFO", "1");
            result.put("X_RESULTINFO", "用户服务密码不存在");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;

        }
        if (userNewPsw.trim().length() != 6)
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_110);// 450006密码长度不正确
            result.put("X_RESULTCODE", 450006);
            result.put("X_CHECK_INFO", "密码长度不正确");
            result.put("X_RESULTINFO", "密码长度不正确");
            return result;
        }

        if (StringUtils.isBlank(data.getString("IN_MODE_CODE")))
        {
            data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        }

        if (PasswdMgr.checkUserPassword(userNewPsw, userId, userOldPsw))
        {// 密码正确encryUserNewPwd
            // 在允许连续输入错误次数的范围内，如果密码输入正确，则清楚之前记录的密码输入错误信息
            delPwdErrInfo(userId);
            
            /* REQ201707250004NGBOSS优化需求:用户鉴权接口取消弱密码判断，只要是密码正确就通过。        
			result = PasswordUtil.checkUserPoorPWD(data);
            if (result.size() > 0)
            {
                // 只要是弱密码提示信息都一样
                if ("0".equals(result.getString("X_RESULTCODE")) && !"0".equals(result.getString("X_CHECK_INFO")))
                {
                    result.put("X_RESULTINFO", "您设置的服务密码较为简单，为保护您个人信息安全，建议您进行修改！");
                }
                return result;
            }
            */
            // 判断是否初始化密码
            boolean isDefaultPwd = false;
            // 查询海南配置的初始密码
            String defaultPwd = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_TAG", new String[]
            { "EPARCHY_CODE", "TAG_CODE" }, "TAG_INFO", new String[]
            { "0898", "CS_INF_DEFAULTPWD" });
            if (StringUtils.isNotBlank(defaultPwd))// 海南根本就没配置初始密码,所以这里始终为空
            {
                String password = PasswdMgr.encryptPassWD(userNewPsw, userId);
                String defaultPw = PasswdMgr.encryptPassWD(defaultPwd, userId);
                if (StringUtils.equals(password, defaultPw))
                {
                    isDefaultPwd = true;
                }
            }
            if (isDefaultPwd)
            {
                result.put("X_RESULTCODE", "0");
                result.put("X_CHECK_INFO", "6");
                result.put("X_RESULTINFO", "密码正确，但为初始化密码");
                return result;
            }
            else
            {
                result.put("X_RESULTCODE", "0");
                result.put("X_CHECK_INFO", "0");
                result.put("X_RESULTINFO", "密码正确，但不是初始化密码");//
                return result;
            }
        }
        else
        {// 密码错误
            recordPasswdErrorInfo(userId, "1", data.getString("IN_MODE_CODE"));// 第二个参数:设置互联网密码类型，1-客服密码
            // 2-互联网密码,这里老系统传的是1,不知道为什么
            IDataset errorData = UserPasswdQry.queryPasswdErrorCount(userId, "", "");
            int num = errorData.getData(0).getInt("ERROR_COUNT", 0);
            // 连续输错3次，自动锁定密码
            // 每出错一次就在原来的基础上加一
            // 判断是否达到锁定次数
            if (num >= pwdErrorNum)
            {
                data.put("ERROR_NUM", pwdErrorNum);
                data.put("LOCK_IN_MODE_CODE", data.getString("IN_MODE_CODE"));// IN_MODE_CODE传不过去，所以更改KEY名
                CSAppCall.call("SS.LockUserPwdRegSVC.tradeReg", data);// 调用密码锁定登记服务
                result.put("X_RESULTCODE", "450009");
                result.put("X_CHECK_INFO", "1");
                result.put("X_SELCOUNT", num); // 密码校验不成功次数
                result.put("X_OLD_COUNT", pwdErrorNum); // 密码锁定的阈值
                result.put("X_RESULTINFO", "当日输入错误密码已达" + pwdErrorNum + "次，请明日再试！");
                result.put("X_RSPTYPE", "2");// add by ouyk
                result.put("X_RSPCODE", "2998");// add by ouyk
                return result;
            }
            else
            {
                String smsContent = getSmsContent(data, "服务");
                recordMessage(data, smsContent, "用户 服务密码输入错误");// 密码校验插入短信下发表;
                insertUserOther(data, "用户服务密码输入错误");
            }

            result.put("X_RESULTCODE", "450001");
            result.put("X_CHECK_INFO", "2");
            result.put("X_SELCOUNT", num); // 密码校验不成功次数
            result.put("X_OLD_COUNT", pwdErrorNum); // 密码锁定的阈值
            result.put("X_RESULTINFO", "当日累计" + (num) + "次密码输入错误!");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
    }

	public static IData checkUserPsptId(IData data) throws Exception {
		 IData result = new DataMap();
         result.put("X_RESULTCODE", "0");
         result.put("X_RESULTINFO", "校验通过!");
         String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
         String psptId = IDataUtil.chkParam(data, "PSPT_ID");
         String psptTypeCode = IDataUtil.chkParam(data, "PSPT_TYPE_CODE");
         IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
         if (IDataUtil.isEmpty(userInfo))
         {
        	 result.put("X_RESULTCODE", "2998");
             result.put("X_RESULTINFO", "手机号码不存在!");
             return result;
         }
         String custId = userInfo.getString("CUST_ID");
         IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
         if (IDataUtil.isEmpty(custInfo))
         {
        	 result.put("X_RESULTCODE", "2998");
             result.put("X_RESULTINFO", "证件信息不存在!");
             return result;
         }
         if(!psptId.equals(custInfo.get("PSPT_ID"))){
        	 result.put("X_RESULTCODE", "2998");
             result.put("X_RESULTINFO", "证件号码不正确!");
             return result;
         }
         if(!psptTypeCode.equals(custInfo.get("PSPT_TYPE_CODE"))){
        	 result.put("X_RESULTCODE", "2998");
             result.put("X_RESULTINFO", "证件类型不正确!");
             return result;
         }
		 return result;
	}

	/**
	 * @Description：校验用户是否具有免押金开通国际长途业务
	 * @param:@param data
	 * @param:@return
	 * @return IData
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-27上午10:51:00
	 */
	public static IData checkInternationalDeposit(IData data) throws Exception {
		 IData result = new DataMap();
         result.put("X_RESULTCODE", "0");
         result.put("X_RESULTINFO", "操作成功!");
         result.put("isPermision", "0");//默认免押金
         String userId = IDataUtil.chkParam(data, "USER_ID");
         String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
         IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
         if (IDataUtil.isEmpty(userInfo))
         {
        	 CSAppException.apperr(BofException.CRM_BOF_002);
         }
         if(!userId.equals(userInfo.get("USER_ID"))){
        	 CSAppException.apperr(BofException.CRM_BOF_024);
         }
         UcaData uca = null;
		 uca = UcaDataFactory.getUcaByUserId(userId);
		 String strAcctBlance = uca.getAcctBlance();
		 String strCreditClass = uca.getUserCreditClass();
		 int iCreditClass = Integer.parseInt(strCreditClass);
		 int iAcctBlance = Integer.parseInt(strAcctBlance) / 100;
		 if (-1 == iCreditClass || 0 == iCreditClass)
		 {
			if (iAcctBlance < 200)
			{
				result.put("isPermision", "1");//需要收取押金押金
			}
		 } 
         
		 return result;
	}
	public static IData queryAbleResNo(IData input) throws Exception{
		IData result = new DataMap();
		
		result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "操作成功!");
		IDataset phoneList = new DatasetList();
		input.put("X_GETMODE", "7");//X_GETMODE=7，查询使用号码信息，多行返回
		input.put("RES_TRADE_CODE", "IGetMphoneCodeInfo");
		input.put("X_CHOICE_TAG", "0");//0:查询普通号码1：查询吉祥号码
		input.put("CITY_CODE","HNHK");//因平台传来的总是 0898，导致查询不出数据，所以暂先默认HNHK
		phoneList = ResCall.getNetWorkPhone(input);
		if(phoneList == null || phoneList.size() == 0 || "0".equals(phoneList.get(0, "X_RECORDNUM"))) 
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取号码资源信息失败！");
		}
		potingResultNo(result,phoneList);
		
		return result;
	}
	private static void potingResultNo(IData result, IDataset phoneList) {
		IDataset resultList = new DatasetList();
		for (int i = 0; i < phoneList.size(); i++){
			IData ele = phoneList.getData(i);
			IData numberInfo = new DataMap();
			numberInfo.put("resNo", ele.getString("SERIAL_NUMBER"));//号码
			numberInfo.put("resStateCode", ele.getString("RES_STATE"));//
			resultList.add(numberInfo);
		}	
		result.put("resList", resultList);
	}

}
