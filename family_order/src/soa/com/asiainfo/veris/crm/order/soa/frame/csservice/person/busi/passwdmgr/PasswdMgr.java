
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr;

import java.util.Random;


import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEncryptGeneInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;

public class PasswdMgr extends CSBizBean
{

    /**
     * 用户统一认证
     * 
     * @param data
     *            [CHECK_TAG,SERIAL_NUMBER,USER_PASSWD]
     * @return
     * @throws Exception
     */
    public static IData checkUserNetPWD(IData data) throws Exception
    {
        IData result = new DataMap();
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String userPasswd = data.getString("USER_PASSWD", "");
        String checkTag = data.getString("CHECK_TAG", "");

        if (checkTag == null || "".equals(checkTag))
        {
            result.put("X_RESULTCODE", "5004");
            result.put("X_RESULTINFO", "认证方式不正确");
            return result;
        }
        else if (null == serialNumber || "".equals(serialNumber))
        {
            result.put("X_RESULTCODE", "5002");
            result.put("X_RESULTINFO", "用户名为空");
            return result;
        }
        else if (null == userPasswd)
        {
            result.put("X_RESULTCODE", "4001");
            result.put("X_RESULTINFO", "用户互联网密码为空");
            return result;
        }
        else if (userPasswd.length() < 6 && userPasswd.length() > 16)// 密码长度不正确
        {
            result.put("X_RESULTCODE", "4001");
            result.put("X_RESULTINFO", "用户互联网密码长度必须大于6小于16");
            return result;
        }

        // 获取用户USER_ID
        IData users = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);

        String userId = null;
        String custId = null;
        String brandCode = null;
        String passwd = null;
        if (!users.isEmpty() && users.size() > 0)
        {
            userId = users.getString("USER_ID", "");
            custId = users.getString("CUST_ID", "");
            brandCode = users.getString("BRAND_CODE", "");
            passwd = users.getString("USER_PASSWD", "");
        }
        else
        {
            result.put("X_RESULTCODE", "5002");
            result.put("X_RESULTINFO", "用户不存在");
            return result;
        }

        String newPsw = PasswdMgr.encryptPassWD(userPasswd, userId);

        IDataset userAttrSet = UserAttrInfoQry.getUserAttrByUserInstType(userId, "USERNP");

        if (checkTag.equals("00"))
        {
            if (!passwd.equals(newPsw))
            {
                result.put("X_RESULTCODE", "4001");
                result.put("X_RESULTINFO", "用户服务密码错误");
                return result;
            }
        }
        else if (checkTag.equals("01"))
        {
            if (!userAttrSet.isEmpty() && userAttrSet.size() > 0)
            {
                String attrValue = userAttrSet.getData(0).getString("ATTR_VALUE", "");
                if (!attrValue.equals(newPsw))
                {
                    result.put("X_RESULTCODE", "4001");
                    result.put("X_RESULTINFO", "用户互联网密码错误");
                    return result;
                }
            }
            else
            {
                result.put("X_RESULTCODE", "4001");
                result.put("X_RESULTINFO", "用户未设置互联网密码");
                return result;

            }
        }
        else
        {
            result.put("X_RESULTCODE", "5004");
            result.put("X_RESULTINFO", "认证方式不正确");
            return result;

        }

        // 认证帐号，
        result.put("MSISDN", serialNumber);

        // 省编码
        IData param_area = new DataMap();
        param_area.put("DATA_ID", "CITY_CODE");
        param_area.put("TYPE_ID", "TD_S_STATIC");
        IData userArea = StaticInfoQry.getStaticInfoByTypeIdDataId("TD_S_STATIC", "CITY_CODE");
        if (!userArea.isEmpty() && userArea.size() > 0)
        {
            result.put("Province", userArea.getString("PDATA_ID", "")); // 省编码
        }

        // 客户名称
        IData custs = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (!custs.isEmpty() && custs.size() > 0)
        {
            result.put("Name", custs.getString("CUST_NAME", ""));
        }

        // 用户昵称---RSRV_STR1保存昵称
        if (!userAttrSet.isEmpty() && userAttrSet.size() > 0)
        {
            result.put("NickName", userAttrSet.getData(0).getString("RSRV_STR1", ""));
        }
        else
        {
            result.put("NickName", "");
        }

        // 默认等于2(神州行)
        result.put("Brand", "2");
        if (brandCode != null && brandCode.length() > 1)
        {
            // error("方法不存在了");
            CSAppException.apperr(CrmUserException.CRM_USER_81);
            IDataset brandSet = new DatasetList();
            if (null != brandSet && brandSet.size() > 0)
            {
                result.put("Brand", brandSet.getData(0).getString("BRAND_CODE", ""));
            }
        }

        // 客户级别
        IDataset vipCusts = CustVipInfoQry.qryVipInfoByCustId(custId);
        if (null != vipCusts && vipCusts.size() > 0)
        {
            result.put("Customers_Level", vipCusts.getData(0).getString("VIP_CLASS_ID", ""));
        }
        else
        {
            result.put("Customers_Level", "04");
        }

        // 中国移动用户状态
        String state = users.getString("USER_STATE_CODESET", "");
        if (!"".equals(state))
        {
            if ("-0-".indexOf(state) > 0)
            {
                state = "1"; // 开机
            }
            else if ("-1-2-4-5-7-A-B-G-I-R-".indexOf(state) > 0)
            {
                state = "2"; // 单向停机
            }
            else if ("-6-8-E-F-".indexOf(state) > 0)
            {
                state = "4"; // 预销户
            }
            else if ("-9-".indexOf(state) > 0)
            {
                state = "5";
            }
            else
            {
                state = "3"; // 双向停机
            }
            result.put("Status", state);
        }
        else
        {
            result.put("X_RESULTCODE", "5004");
            result.put("X_RESULTINFO", "用户状态不存在");
            return result;
        }

        // 查询用户是否开通139邮箱
        IDataset platSvc139 = UserPlatSvcInfoQry.queryPlatSvcInfo(userId, "16");
        if (null != platSvc139 && platSvc139.size() > 0)
        {
            result.put("139MailStatus", "0");
        }
        else
        {
            result.put("139MailStatus", "1");
        }

        // 查询用户是否开通飞信服务
        IDataset platSvcFetion = UserPlatSvcInfoQry.queryPlatSvcInfo(userId, "23");
        if (null != platSvcFetion && platSvcFetion.size() > 0)
        {
            result.put("FetionStatus", "0");
        }
        else
        {
            result.put("FetionStatus", "1");
        }

        result.put("X_RESULTCODE", "2001");
        result.put("X_RESULTINFO", "认证成功");

        return result;
    }

    /**
     * 密码认证
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IData checkUserPasswd(IData inparams) throws Exception
    {
        inparams.put("USER_SERIAL_NUMBER", inparams.getString("SERIAL_NUMBER"));
        inparams.put("PASSWORD", inparams.getString("USER_PASSWD", ""));

        ErrorPasswdMgr mgr = new ErrorPasswdMgr(inparams);
        IData passwdCheck = mgr.checkUserPasswd(inparams);
        if ("0".equals(passwdCheck.getString("RESULT_CODE")))
        {
            /**
             * 弱密码校验，海南这边放到前台脚本控制，无需下发短信通知 if ("310".equals(inparams.getString("TRADE_TYPE_CODE", "-1"))) { return
             * passwdCheck; } SimplePasswdMgr spMgr = new SimplePasswdMgr(inparams); spMgr.isSimplePasswd(inparams);
             * IData outData = spMgr.isLimitSimplePasswd(inparams); if (!"0".equals(outData.getString("RESULT_CODE"))) {
             * passwdCheck.put("RESULT_CODE", outData.getString("RESULT_CODE")); passwdCheck.put("RESULT_INFO",
             * outData.getString("RESULT_INFO")); }
             */
        }
        else
        {
            // 记录认证错误记录
            passwdCheck.put("ERROR_COUNT", mgr.getErrorCount());
            passwdCheck.put("ERROR_LIMIT", mgr.getErrorLimit());
        }
        return passwdCheck;

    }

    /**
     * 校验用户密码是否正确
     * 
     * @param passwd
     *            明文密码
     * @param userId
     *            用户ID
     * @param originUserPassWd
     *            用户表中的密码
     * @return
     * @throws Exception
     */
    public static boolean checkUserPassword(String passwd, String userId, String originUserPassWd) throws Exception
    {
        if (StringUtils.isBlank(originUserPassWd))
        {
            IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
            originUserPassWd = userInfo.getString("USER_PASSWD");
        }

        if (StringUtils.equals(originUserPassWd, encryptPassWD(passwd, userId)))
        {
            return true;
        }
        else
        {
            // 查询是否存在密码因子
            IDataset genSet = UserEncryptGeneInfoQry.getEncryptGeneBySn(userId);
            // 密码因子表存在该用户对应的密码 因子，则用密码因子代替user_id来加密
            if (IDataUtil.isNotEmpty(genSet) && StringUtils.isNotBlank(genSet.getData(0).getString("ENCRYPT_GENE")))
            {
                userId = genSet.getData(0).getString("ENCRYPT_GENE");
                if (StringUtils.equals(originUserPassWd, encryptPassWD(passwd, userId)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据传入的明文密码和加密参数对密码进行加密
     * 
     * @param passwd
     * @param userId
     * @return
     * @throws Exception
     */
    public static String encryptPassWD(String passwd, String userId) throws Exception
    {
        return Encryptor.fnEncrypt(passwd, genUserId(userId));
    }

    // 产生随机密码不加密
    public static String genNewPasswd()
    {

        String password = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++)
        {
            int k = random.nextInt(10);
            password += k;
        }
        return password;
    }

    // 产生随机密码
    public static String genNewPsw(String userId)
    {

        String password = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++)
        {
            int k = random.nextInt(10);
            password += k;
        }
        password = Encryptor.fnEncrypt(password, userId);
        return password;
    }

    // 产生用户id后九位的
    public static String genUserId(String userId) throws Exception
    {
        String userIdTemp = "";
        if (userId.length() >= 9) // 加密那里是userid的后九位，不足前面补零
            userIdTemp = userId.substring(userId.length() - 9, userId.length());
        else
        {
            for (int i = 0; i < 9 - userId.length(); i++)
            {
                userIdTemp += "0";
            }
            userIdTemp += userId;
        }
        return userIdTemp;
    }

    /**
     * 校验用户密码是否为初始密码
     * 
     * @param serialNumber
     * @param password
     * @return
     * @throws Exception
     */
    public static boolean ifDefaultPassWd(String serialNumber, String password) throws Exception
    {
        IDataset dataset = UserInfoQry.getUserDefaultPassWd(serialNumber, "0");
        String strDefultPwd = TagInfoQry.getSysTagInfo("CS_INF_DEFAULTPWD", "TAG_INFO", "0", BizRoute.getUserEparchyCode());

        if (StringUtils.isEmpty(strDefultPwd)||dataset == null || dataset.size() == 0)
        {
            return false;
        }
        String depositPwd = Encryptor.fnEncrypt(strDefultPwd, dataset.getData(0).getString("USER_ID",""));

        if (StringUtils.isNotEmpty(depositPwd)&&depositPwd.equals(password))
            return true;

        return false;
    }

    /**
     * 根据输入的managermode判断操作种类
     * 
     * @param inparams
     *            managerMode 处理代码 psw 用户记忆中的旧密码 nPsw 用户输入的新密码
     *            params[USER_ID,CHECK_TAG,USER_PASSWD,SERIAL_NUMBER,PSPT_ID,PSPT_TYPE_CODE]
     * @return void
     * @throws Exception
     * @author ykx
     */
    public static String judgeManagerMode(String managerMode, String psw, String nPsw, IData params, String psptIdOld, String psptCodeOld) throws Exception
    {

        String tmpPass = "";
        String userIdTemp = genUserId(params.getString("USER_ID"));
        String check_tag = params.getString("CHECK_TAG", "");// 海南设置的标记位，取消密码和申请随机密码时需要相应的验证
        if (managerMode.equals("0"))
        { // 申请新密码，用它输入的，同时需要验证应该没有原密码，有则报错
            String password = params.getString("USER_PASSWD");
            if (password == null || "".equals(password))
            {
                tmpPass = Encryptor.fnEncrypt(nPsw, userIdTemp);
            }
            else
            {
                CSAppException.apperr(CrmUserException.CRM_USER_99);
            }
        }
        if (managerMode.equals("1"))
        { // 取消密码
            if (check_tag.equals("1"))
            {// 海南要求验证密码
                String temp = Encryptor.fnEncrypt(psw, userIdTemp);
                String psssWd = params.getString("USER_PASSWD");
                if (psssWd == null || "".equals(psssWd))
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_81);
                }
                else
                {
                    // 说明过去有密码并且密码验证正确,置密码为空
                    if (psssWd.equals(temp))
                    {
                        tmpPass = "";
                    }
                    else
                    {
                        CSAppException.apperr(CrmUserException.CRM_USER_90);
                    }
                }
            }
            else
            {
                tmpPass = "";
            }
        }

        if (managerMode.equals("2"))
        { // 变更密码
            /* add by gaoyuan @2010-02-10 desc :: 修改密码的原密码错误同样需要记录 */
            IData inData = new DataMap();
            inData.put("USER_SERIAL_NUMBER", params.getString("SERIAL_NUMBER"));
            inData.put("USER_PASSWD", psw);
            inData.put("IN_MODE_CODE", getVisit().getInModeCode());
            inData.put("CHECK_MODE", "1");
            ErrorPasswdMgr epMgr = new ErrorPasswdMgr(inData);
            epMgr.checkUserPasswd(inData);
            /* add by gaoyuan @2010-02-10 desc :: 修改密码的原密码错误同样需要记录 */

            String temp = Encryptor.fnEncrypt(psw, userIdTemp);

            String psssWd = params.getString("USER_PASSWD");
            if (psssWd == null || "".equals(psssWd))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_81);
            }
            else
            {
                // 说明过去有密码并且密码验证正确，可以将新的插入了
                if (psssWd.equals(temp))
                {
                    tmpPass = Encryptor.fnEncrypt(nPsw, userIdTemp);
                }
                else
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_90);
                }
            }
        }
        if (managerMode.equals("3"))
        { // 申请随机密码
            String psssWd = params.getString("USER_PASSWD");
            if (psssWd == null || "".equals(psssWd))
            {
                tmpPass = genNewPasswd();
            }
            else
            {
                CSAppException.apperr(CrmUserException.CRM_USER_99);
            }
        }
        if (managerMode.equals("4"))
        { // 用户使用证件号码进行密码重置,同样需要用户输入新的密码
            // IDataset idt1 = judgeCustAll(td);
            // String psptId = idt1.getData(0).getString("PSPT_ID");
            // String psptTypeCode = idt1.getData(0).getString("PSPT_TYPE_CODE");
            String psptId = params.getString("PSPT_ID");
            String psptTypeCode = params.getString("PSPT_TYPE_CODE");

            if (psptIdOld.equals(psptId) && psptCodeOld.equals(psptTypeCode))
            { // 证件类型等校验成功
                tmpPass = Encryptor.fnEncrypt(nPsw, userIdTemp);
            }
            else
            {
                CSAppException.apperr(CustException.CRM_CUST_117);
            }
        }
        if (managerMode.equals("5") || managerMode.equals("7"))
        {
            if ("1".equals(check_tag))
            {// 海南需要验证证件号码
                String psptId = params.getString("PSPT_ID");
                if (psptIdOld.equals(psptId))
                { // 证件号码校验成功
                    tmpPass = genNewPasswd();
                }
                else
                {
                    CSAppException.apperr(CustException.CRM_CUST_32);
                }
            }
            else
            {// 申请随机密码，不校验
                tmpPass = genNewPasswd();
            }
        }
        if (managerMode.equals("9")) // 我自己定义的，当是9的时候，就是随意的重置密码，不要任何校验，但是要他自己输入想要的密码
        { // 重置密码
            tmpPass = Encryptor.fnEncrypt(nPsw, userIdTemp);
        }
        return tmpPass;
    }

    // 修改用户密码
    public static IData updateUserPasswd(IData inparams) throws Exception
    {

        String resultInfo;
        int resultCode;

        IData returnInfo = new DataMap();

        IDataset result = new DatasetList();

        String tradeLcuName = inparams.getString("TRADE_LCU_NAME");
        result = CSAppCall.call(tradeLcuName, inparams);

        if (result != null && result.size() > 0)
        {
            IData resultData = new DataMap();

            resultData = (IData) result.get(0);

            resultCode = resultData.getInt("X_RESULTCODE");
            resultInfo = resultData.getString("X_RESULTINFO");

            if (resultCode != 0)
            {
                // 业务中断

                CSAppException.apperr(BizException.CRM_BIZ_5, resultInfo);
            }
            else
            {
                returnInfo.put("X_RESULTCODE", "0");
                returnInfo.put("PLUG_TYPE", "1");
                returnInfo.put("X_RESULTINFO", "设置密码成功！");
            }
        }

        return returnInfo;
    }

}
