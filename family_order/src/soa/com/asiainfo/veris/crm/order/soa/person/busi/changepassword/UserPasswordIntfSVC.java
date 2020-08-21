
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

/**
 * 密码接口服务
 * 
 * @author liutt
 */
public class UserPasswordIntfSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(UserPasswordIntfSVC.class);

    /**
     * 接口名（ITF_CRM_CheckUserInternetPWD） 校验用户互联网密码
     * 外围转入字段：SERIAL_NUMBER,REMOVE_TAG,USER_PASSWD,NET_TYPE_CODE,IN_MODE_CODE,
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData checkUserInternetPWD(IData data) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("校验用户互联网密码>>>" + data.toString());
        }
        return UserPasswordInfoComm.checkUserInternetPWD(data);
    }

    /**
     * 接口名ITF_CRM_CHANGEPWD 手机营业厅-客服密码修改接口服务
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset changePwdInfoForPhone(IData data) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("手机营业厅-客服密码修改接口>>>" + data.toString());
        }
        return UserPasswordInfoComm.changePwdInfoForPhone(data);
    }

    /**
     * 接口名ITF_CRM_ChangeUserPswInfoReg 服务密码修改(来自外围接口) X_MANAGEMODE 字段说明： 0=申请密码；1=取消密码
     * ；2=修改密码；3=随机密码（密码为空的情况下）；4=重置密码（用户使用证件号码进行密码重置,同样需要用户输入新的密码）； 5=随机密码（需证件号码）；7=随机密码（需证件号码）；（5、7没有发现有区别，可以删掉7）
     * 8=密保服务重置方式，选择这种方式的话，接口可以直接传入新密码来进行密码重置; 9=重置密码（不要任何校验）
     */
    public IDataset changeUserPwdInfo(IData data) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("服务密码修改接口>>>" + data.toString());
        }
        return UserPasswordInfoComm.changeUserPwdInfo(data);
    }

    /**
     * (接口名: ITF_CRM_checkProtectPass) 校验用户回答密码保护问题是否正确
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkProtectPass(IData data) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("校验服务密码保护答案接口>>>" + data.toString());
        }
        return UserPasswordInfoComm.checkProtectPass(data);
    }

    /**
     * 根据userId/密码明文来校验密码是否正确 参数 USER_ID，PASSWORD ,ROUTE_EAPRCHY_CODE[用来给服务路由]
     * 
     * @return
     * @throws Exception
     */
    public IDataset checkPwdByUserId(IData input) throws Exception
    {
        String userId = IDataUtil.chkParam(input, "USER_ID");
        String pwd = IDataUtil.chkParam(input, "PASSWORD");
        if (pwd.length() != 6)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_110);// 450006密码长度不正确
        }
        return UserPasswordInfoComm.checkPwdByUserId(userId, pwd);
    }

    /**
     * 校验用户密码是否为初始密码
     * 
     * @param data
     *            参数SERIAL_NUMBER、USER_PASSWD
     * @return
     * @throws Exception
     */
    public IData checkUserDefaultPWD(IData data) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("校验用户密码是否为初始密码>>>" + data.toString());
        }

        return UserPasswordInfoComm.checkUserDefaultPWD(data);

    }

    /**
     * 判断用户是否存在互联网密码
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkUserHasInternetPWD(IData data) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("判断用户是否存在互联网密码>>>" + data.toString());
        }
        return UserPasswordInfoComm.checkUserHasInternetPWD(data);
    }

    /**
     * 接口名（ITF_CRM_CheckUserPWD） 校验用户密码
     * 
     * @param data
     *            必备参数SERIAL_NUMBER,USER_PASSWD（明文）
     * @return X_CHECK_INFO,X_RESULTCODE,X_RESULTINFO
     * @throws Exception
     */
    public IData checkUserPWD(IData data) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("校验用户服务密码>>>" + data.toString());
        }
        return UserPasswordInfoComm.checkUserPWD(data);
    }

    /**
     * 接口名ITF_CRM_SSO_PasswdSendConfirm 一级BOSS调用密码重发确认接口
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData sendPasswdConfirm(IData data) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("一级BOSS调用密码重发确认接口>>>" + data.toString());
        }
        return UserPasswordInfoComm.sendPasswdConfirm(data);
    }
    /**
     * 校验证件号码
     * @param data 入参包括:
     * 
     *  SERIAL_NUMBER 手机号码 
	 *	PSPT_ID 证件号码
	 *	PSPT_TYPE_CODE 证件类型
	 * 
     * @return 0 成功，2998 失败
     * @throws Exception
     * @author tanzheng
     */
    public IData checkUserPsptId(IData data) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("校验用户证件号码>>>" + data.toString());
        }
        return UserPasswordInfoComm.checkUserPsptId(data);
    }
    /**
     * 
     * @Description：校验用户是否具有免押金开通国际长途业务
     * @param:@param data
     * @param:@return
     * @param:@throws Exception
     * @return IData
     * @throws
     * @Author :tanzheng
     * @date :2017-11-27上午10:48:56
     */
    public IData checkInternationalDeposit(IData data) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("校验用户是否具有免押金开通国际长途业务>>>" + data.toString());
        }
        return UserPasswordInfoComm.checkInternationalDeposit(data);
    }
    public IData queryAbleResNo(IData data) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("查询可用号码接口>>>" + data.toString());
        }
        return UserPasswordInfoComm.queryAbleResNo(data);
    }
    
    @Override
    public final void setTrans(IData input)
    {
        // 手机营业厅-客服密码修改接口传过来的手机号KEY名是IDVALUE，因要以手机号路由，则要改变它的KEY为SERIAL_NUMBER
        if (StringUtils.equals("SS.UserPasswordIntfSVC.changePwdInfoForPhone", getVisit().getXTransCode()))
        {
            if (StringUtils.isNotBlank(input.getString("IDVALUE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
                return;
            }
        }
    }

}
