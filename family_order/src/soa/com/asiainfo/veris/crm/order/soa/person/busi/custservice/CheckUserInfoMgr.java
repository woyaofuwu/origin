
package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class CheckUserInfoMgr
{

    /**
     * 校验用户密码
     * 
     * @param userPassWd
     * @param String
     */
    public static IData checkUserPassWd(String userPassWd, String userId) throws Exception
    {
        IData result = new DataMap();
        result.put("RESULT_CODE", "-1");
        if (null == userPassWd || "".equals(userPassWd))// 用户服务密码不存在
        {
            CSAppException.apperr(CrmUserException.CRM_USER_81);
        }
        if (userPassWd.length() != 6)// 密码长度不正确
        {
            CSAppException.apperr(CrmUserException.CRM_USER_110);
        }

        boolean passWdResult = UserInfoQry.checkUserPassWd(userId, userPassWd);
        if (passWdResult == true)
        {
            result.put("RESULT_CODE", "0");
            result.put("RESULT_INFO", "OK");
            return result;
        }
        else if (passWdResult == false)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_91);
        }
        result.put("RESULT_CODE", "-1");
        return result;
    }

    /**
     * 校验用户密码
     * 
     * @param userPassWd
     * @param String
     */
    public static IData checkUserPassWdForSn(String userPassWd, String serialNumber) throws Exception
    {
        IData result = new DataMap();
        result.put("RESULT_CODE", "-1");
        if (null == userPassWd || "".equals(userPassWd))// 用户服务密码不存在
        {
            CSAppException.apperr(CrmUserException.CRM_USER_81);
        }
        if (userPassWd.length() != 6)// 密码长度不正确
        {
            CSAppException.apperr(CrmUserException.CRM_USER_110);
        }

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String userId = userInfo.getString("USER_ID");
        boolean passWdResult = UserInfoQry.checkUserPassWd(userId, userPassWd);
        if (passWdResult == true)
        {
            result.put("RESULT_CODE", "0");
            result.put("RESULT_INFO", "OK");
            return result;
        }
        else if (passWdResult == false)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_91);
        }
        result.put("RESULT_CODE", "-1");
        return result;
    }

    /**
     * 根据手机号码和证件号码校验
     * 
     * @param psptId
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData checkUserPsptIdBySN(String psptId, String serialNumber) throws Exception
    {

        if ("".equals(psptId) || psptId == null)
        {
            CSAppException.apperr(CustException.CRM_CUST_102);
        }
        IData result = new DataMap();
        result.put("RESULT_CODE", "-1");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String userId = userInfo.getString("USER_ID");
        IData custPersonInfo = UcaInfoQry.qryPerInfoByUserId(userId);
        if (IDataUtil.isEmpty(custPersonInfo))
        {
            CSAppException.apperr(CustException.CRM_CUST_29);
        }
        String custpsptid = custPersonInfo.getString("PSPT_ID").toUpperCase();

        if (!psptId.toUpperCase().equals(custpsptid))
        {
            CSAppException.apperr(CustException.CRM_CUST_118);
        }
        else
        {
            result.put("RESULT_CODE", "0");
            result.put("RESULT_INFO", "OK");
            return result;
        }

        return result;
    }

    /**
     * 校验证件号码，根据userid 证件号码校验
     * 
     * @param psptId
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData checkUserPsptIdByUserId(String psptId, String userId) throws Exception
    {

        if ("".equals(psptId) || psptId == null)
        {
            CSAppException.apperr(CustException.CRM_CUST_102);
        }
        IData result = new DataMap();
        result.put("RESULT_CODE", "-1");
        IData custPersonInfo = UcaInfoQry.qryPerInfoByUserId(userId);
        if (IDataUtil.isEmpty(custPersonInfo))
        {
            CSAppException.apperr(CustException.CRM_CUST_29);
        }
        String custpsptid = custPersonInfo.getString("PSPT_ID").toUpperCase();

        if (!psptId.toUpperCase().equals(custpsptid))
        {
            CSAppException.apperr(CustException.CRM_CUST_118);
        }
        else
        {
            result.put("RESULT_CODE", "0");
            result.put("RESULT_INFO", "OK");
            return result;
        }

        return result;
    }

    /**
     * 按照证件类型和证件号码校验
     * 
     * @param psptTypeCode
     * @param psptId
     * @param custId
     * @return
     * @throws Exception
     */
    public static IData checkUserPsptIdCustId(String psptTypeCode, String psptId, String custId) throws Exception
    {
        if ("".equals(psptTypeCode) || psptTypeCode == null)
        {
            CSAppException.apperr(CustException.CRM_CUST_103);
        }
        if ("".equals(psptId) || psptId == null)
        {
            CSAppException.apperr(CustException.CRM_CUST_102);
        }
        IData result = new DataMap();
        result.put("RESULT_CODE", "-1");
        IData custPersonInfo = UcaInfoQry.qryPerInfoByCustId(custId);
        if (IDataUtil.isEmpty(custPersonInfo))
        {
            CSAppException.apperr(CustException.CRM_CUST_29);
        }
        String custpspttypecode = custPersonInfo.getString("PSPT_TYPE_CODE");
        String custpsptid = custPersonInfo.getString("PSPT_ID");
        if (!psptTypeCode.equals(custpspttypecode))
        {
            CSAppException.apperr(CustException.CRM_CUST_175);
        }

        if (!psptId.equals(custpsptid))
        {

            CSAppException.apperr(CustException.CRM_CUST_118);
        }

        if (psptTypeCode.equals(custpspttypecode) && psptId.equals(custpsptid))
        {
            result.put("RESULT_CODE", "0");
            result.put("RESULT_INFO", "OK");
            return result;
        }
        return result;
    }

    /**
     * 按照证件类型和证件号码校验
     * 
     * @param psptTypeCode
     * @param psptId
     * @param custId
     * @return
     * @throws Exception
     */
    public static IData checkUserPsptIdUserId(String psptTypeCode, String psptId, String userId) throws Exception
    {
        if ("".equals(psptTypeCode) || psptTypeCode == null)
        {
            CSAppException.apperr(CustException.CRM_CUST_103);
        }
        if ("".equals(psptId) || psptId == null)
        {
            CSAppException.apperr(CustException.CRM_CUST_102);
        }
        IData result = new DataMap();
        result.put("RESULT_CODE", "-1");
        IData custPersonInfo = UcaInfoQry.qryPerInfoByUserId(userId);
        if (IDataUtil.isEmpty(custPersonInfo))
        {
            CSAppException.apperr(CustException.CRM_CUST_29);
        }
        String custpspttypecode = custPersonInfo.getString("PSPT_TYPE_CODE");
        String custpsptid = custPersonInfo.getString("PSPT_ID");
        if (!psptTypeCode.equals(custpspttypecode))
        {
            CSAppException.apperr(CustException.CRM_CUST_175);
        }

        if (!psptId.equals(custpsptid))
        {

            CSAppException.apperr(CustException.CRM_CUST_118);
        }

        if (psptTypeCode.equals(custpspttypecode) && psptId.equals(custpsptid))
        {
            result.put("RESULT_CODE", "0");
            result.put("RESULT_INFO", "OK");
            return result;
        }
        return result;
    }

    public static IData checkUserVipNo(String vipNo, String userId) throws Exception
    {
        IData result = new DataMap();
        result.put("RESULT_CODE", "-1");
        IDataset custVipInfo = CustVipInfoQry.qryVipInfoByUserId(userId);
        if (custVipInfo == null || custVipInfo.size() < 1)
        {
            CSAppException.apperr(CustException.CRM_CUST_224);
        }

        String custvipno = custVipInfo.getData(0).getString("VIP_CARD_NO");

        if (!vipNo.equals(custvipno))
        {

            CSAppException.apperr(CrmCardException.CRM_CARD_90);
        }
        else
        {
            result.put("RESULT_CODE", "0");
            result.put("RESULT_INFO", "OK");
            return result;
        }
        return result;
    }

}
