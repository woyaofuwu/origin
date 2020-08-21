
package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;

public class CheckPsptVipPwdBean extends CSBizBean
{
    private static Logger logger = Logger.getLogger(CheckPsptVipPwdBean.class);

    public IData checkPassWord(String userId, String userPassWD, String userPWD) throws Exception
    {
        IData result = new DataMap();

        if (StringUtils.isBlank(userPWD))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_81);
        }

        if (userPassWD.length() != 6)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_110);
        }

        boolean flag = PasswdMgr.checkUserPassword(userPassWD, userId, userPWD);

        if (flag)
        {
            return result;
        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_91);
        }

        return result;
    }

    public IData checkPsptType(String userId, String psptTypeCode, String psptId) throws Exception
    {
        IData result = new DataMap();

        IData custInfo = UcaInfoQry.qryPerInfoByUserId(userId);
        if (IDataUtil.isNotEmpty(custInfo))
        {
            String pspttypecode = custInfo.getString("PSPT_TYPE_CODE");
            String psptid = custInfo.getString("PSPT_ID");

            if (StringUtils.isNotBlank(pspttypecode) && StringUtils.isNotBlank(psptid))
            {
                if (!pspttypecode.equals(psptTypeCode))
                {
                    CSAppException.apperr(CustException.CRM_CUST_101);
                }

                if (!psptid.equals(psptId))
                {
                    CSAppException.apperr(CustException.CRM_CUST_106);
                }
            }
            else
            {
                if (StringUtils.isNotBlank(psptTypeCode))
                {
                    CSAppException.apperr(CustException.CRM_CUST_101);
                }
                else if (StringUtils.isNotBlank(psptId))
                {
                    CSAppException.apperr(CustException.CRM_CUST_106);
                }

            }

        }
        else
        {
            CSAppException.apperr(CustException.CRM_CUST_35);
        }

        return result;
    }

    public IData CheckPsptVipsPwd(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "X_TAG");

        String userPassWD = data.getString("USER_PASSWD");
        String psptTypeCode = data.getString("PSPT_TYPE_CODE");
        String vipNo = data.getString("VIP_NO");

        if (StringUtils.isBlank(userPassWD) && StringUtils.isBlank(psptTypeCode) && StringUtils.isBlank(vipNo))
        {
            IDataUtil.chkParam(data, "USER_PASSWD");
            IDataUtil.chkParam(data, "PSPT_TYPE_CODE");
            IDataUtil.chkParam(data, "VIP_NO");
            IDataUtil.chkParam(data, "PSPT_ID");
        }

        String psptId = data.getString("PSPT_ID");

        String serialNumber = data.getString("SERIAL_NUMBER");

        // 获取用户资料
        int xTag = data.getInt("X_TAG");
        IData userInfo = getUserInfo(xTag, serialNumber);

        String userId = userInfo.getString("USER_ID");
        String userPWD = userInfo.getString("USER_PASSWD");

        data.put("USER_ID", userId);

        IData result = new DataMap();

        // 判断用户密码
        if (StringUtils.isNotBlank(userPassWD))
        {
            result = checkPassWord(userId, userPassWD, userPWD);
        }

        // 判断用户证件号码
        if (StringUtils.isNotBlank(psptTypeCode))
        {
            result = checkPsptType(userId, psptTypeCode, psptId);
        }

        // 判断用户VIP_NO
        if (StringUtils.isNotBlank(vipNo))
        {
            result = checkVipNo(userId, vipNo);
        }

        return result;
    }

    public IData checkVipNo(String userId, String vipNo) throws Exception
    {
        IData result = new DataMap();
        IDataset vipInfos = CustVipInfoQry.getCustVipByUserId(userId, "0", CSBizBean.getVisit().getStaffEparchyCode());

        if (IDataUtil.isNotEmpty(vipInfos))
        {
            IData vipInfo = vipInfos.getData(0);
            String vipno = vipInfo.getString("VIP_CARD_NO");

            if (StringUtils.isNotBlank(vipno))
            {
                if (!vipno.equals(vipNo))
                {
                    CSAppException.apperr(CustException.CRM_CUST_1101);
                }
            }
            else
            {
                if (StringUtils.isNotBlank(vipNo))
                {
                    CSAppException.apperr(CustException.CRM_CUST_1101);
                }
            }

        }
        else
        {
            CSAppException.apperr(CustException.CRM_CUST_1101);
        }
        return result;
    }

    public IData getUserInfo(int xTag, String serialNumber) throws Exception
    {
        IData userInfo = new DataMap();
        IDataset userInfos = new DatasetList();

        if (0 == xTag)// 正常用户
        {
            userInfo = UserInfoQry.getUserInfoBySN(serialNumber);
        }
        else if (5 == xTag)// 最后销号用户
        {
            userInfos = UserInfoQry.getDestroyUserInfoBySn(serialNumber);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1102);
        }

        if (IDataUtil.isEmpty(userInfo))
        {
            if (IDataUtil.isNotEmpty(userInfos))
            {
                userInfo = userInfos.getData(0);
            }
            else
            {
                CSAppException.apperr(CrmUserException.CRM_USER_112);
            }
        }

        return userInfo;
    }

}
