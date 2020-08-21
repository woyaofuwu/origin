
package com.asiainfo.veris.crm.order.soa.person.busi.userident;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.IdentcardInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.UserPasswordInfoComm;
import com.asiainfo.veris.crm.order.soa.person.common.util.IBossCovertor;
 
public class UserIdentBean extends CSBizBean
{
    // 服务客服密码检验
    public final String CUSTOME_SERVICE_PWD = "01";

    public final String RAND_PWD = "02";

    public final String INTERNET_PWD = "03";

    // 身份凭证类型
    public final String EFFICTIVE_CERT = "01";

    public final String TEMP_CERT = "02";

    private static Logger log = Logger.getLogger(UserIdentBean.class);

    /**
     * 触发客户管理接触信息接口进行接触信息记录
     * 
     * @param pd
     * @param data
     * @param strTradeType
     *            |业务标识
     * @throws Exception
     */
    public void callUspRequest(IData data, String strTradeType) throws Exception
    {
        IDataset resDs = new DatasetList();
        try
        {
            resDs = SccCall.getUSPRequestInfo(data.getString("MSISDN", ""), data.getString("USER_TYPE", ""), data.getString("IDENT_CODE", ""), data.getString("IDENT_CODE_TYPE", ""), data.getString("IDENT_CODE_LEVEL", ""), strTradeType);
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
            {
                log.debug("************ 调用客户管理接触信息接口异常,异常原因：" + e.toString());
            }
            CSAppException.apperr(CrmCommException.CRM_COMM_1112, e.toString());
            return;
        }

        if (IDataUtil.isNotEmpty(resDs) && !StringUtils.equals(resDs.getData(0).getString("X_RESULTCODE", ""), "0"))
        {
            if (log.isDebugEnabled())
            {
                log.debug("************ 调用客户管理接触信息接口出错,出错原因：" + resDs.getData(0).getString("X_RESULTINFO"));
            }
            CSAppException.apperr(CrmCommException.CRM_COMM_1112, resDs.getData(0).getString("X_RESULTINFO"));
        }
    }

    private String changeBrandCode(String strUserId, String strBrandIn) throws Exception
    {
        String strBrandOut = "09";
        String brandCode = strBrandIn;

        IDataset brandInfos = UserInfoQry.getUserInfoChgByUserId(strUserId);
        if (IDataUtil.isNotEmpty(brandInfos))
        {
            brandCode = brandInfos.getData(0).getString("BRAND_CODE");
        }
        if ("G001".equals(brandCode))
        {
            strBrandOut = "01"; // 全球通
        }
        else if ("G002".equals(brandCode))
        {
            strBrandOut = "02"; // 神州行
        }
        else if ("G010".equals(brandCode))
        {
            strBrandOut = "03"; // 动感地带
        }
        else
        {
            strBrandOut = "09"; // 其它品牌
        }
        return strBrandOut;
    }

    /**
     * 身份凭证鉴权
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @author huangsl
     */
    public void identAuth(IData data) throws Exception
    {
    	 String identCode = data.getString("IDENT_CODE", "");
         if (StringUtils.isBlank(identCode))
         {
             CSAppException.apperr(CrmCommException.CRM_COMM_913, "IDENT_CODE");
         }
    	String serialNumber = data.getString("SERIAL_NUMBER");
        String businessCode = data.getString("BUSINESS_CODE", "");
        String identCodeType = data.getString("IDENT_CODE_TYPE", "");
        String identCodeLevel = data.getString("IDENT_CODE_LEVEL", "");
        String userType = data.getString("USER_TYPE", "");

        IDataset idents = UserIdentInfoQry.checkIdent(identCode, businessCode, identCodeType, identCodeLevel, serialNumber);
        if (IDataUtil.isEmpty(idents))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_915);
        }

        if (StringUtils.equals(identCodeType, "02") && StringUtils.isBlank(businessCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1103);
        }

        SccCall.getUSPRequestInfo(serialNumber, userType, identCode, identCodeType, identCodeLevel, "identAuth");
        /*IData res = IdentcardInfoQry.checkIdentInfoByIdent("", "", data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(res))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1055);
        }
        data.put("IDENT_CODE_TYPE", res.getString("IDENT_CODE_TYPE"));

        if (data.getString("IDENT_CODE_TYPE").equals("02") && 0 == data.getString("BUSINESS_CODE", "").length())
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1103);
        }
        callUspRequest(data, "identAuth");*/
    }
    
    public IData loginIdent(IData input) throws Exception
    {
        IData userInfo = null;
        String userId = null;
        IData result = new DataMap();
        String strCustStatus = "N";
        StringBuilder strSb;
        String strPwdType;
        String strIdentCodeType;
        int recordCount = 0;

        // 设置必须返回的以下3字段初值
        result.put("IDENT_CODE", "0");
        result.put("IDENT_CODE_LEVEL", "00");
        result.put("IDENT_CODE_TYPE", "00");
        try
        {
            // 参数检查
            IDataUtil.chkParam(input, "USER_TYPE");
            strPwdType = IDataUtil.chkParam(input, "PWD_TYPE");
            String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
            strIdentCodeType = IDataUtil.chkParam(input, "IDENT_CODE_TYPE");
            String inModeCode = IDataUtil.chkParam(input, "IN_MODE_CODE");
            IDataUtil.chkParam(input, "EFFECTIVE_TIME");

            if (!StringUtils.equals(inModeCode, "11"))// 当前仅支持统一认证中心进行调用
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_914);
            }

            input.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

            strIdentCodeType = input.getString("IDENT_CODE_TYPE");
            // 临时身份凭证,必须传入业务编码
            if (StringUtils.equals(strIdentCodeType, TEMP_CERT))
            {
                IDataUtil.chkParam(input, "BUSINESS_CODE");
            }
            // 客服密码&互联网密码类型时,必须传入密码值
            if (!StringUtils.equals(strPwdType, RAND_PWD))
            {
                IDataUtil.chkParam(input, "USER_PWD");
                input.put("USER_PASSWD", input.getString("USER_PWD"));
            }

            // 获取入参CUST_ID,用于比较cust_id是否已经变更
            String strCustIDInParam = input.getString("CUST_ID");
            /**
             * ================================================REQ201905300003  统一认证二级返回码优化（五项考核保障）新增校验开始(hzl)=============================================
             */
            IDataset userInfos = UserInfoQry.getLatestUserInfosBySerialNumber(serialNumber);
           
            if(IDataUtil.isNotEmpty(userInfos)){
            	userInfo = userInfos.getData(0);
            	result.put("PRI_SERIAL_NUMBER", serialNumber);
            	result.put("RSP_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
				if("1".equals(userInfo.getString("REMOVE_TAG")) || "3".equals(userInfo.getString("REMOVE_TAG"))){
					result.put("X_RSPCODE", "2998");
	                result.put("X_RSPDESC", "受理失败");
	                result.put("X_RSPTYPE", "2");
	                result.put("X_RESULTCODE", "2006");
	                result.put("X_RESULTINFO", "用户预销户");
	                return result;
				} else if("2".equals(userInfo.getString("REMOVE_TAG")) || "4".equals(userInfo.getString("REMOVE_TAG"))){
					result.put("X_RSPCODE", "2998");
	                result.put("X_RSPDESC", "受理失败");
	                result.put("X_RSPTYPE", "2");
	                result.put("X_RESULTCODE", "2007");
	                result.put("X_RESULTINFO", "用户已销户");
	                return result;
				}
			} else {
				result.put("X_RSPCODE", "2998");
                result.put("X_RSPDESC", "受理失败");
                result.put("X_RSPTYPE", "2");
                result.put("X_RESULTCODE", "4005");
                result.put("X_RESULTINFO", "手机账号不存在");
                return result;
			}
            /**
             * ================================================REQ201905300003  统一认证二级返回码优化（五项考核保障）新增校验结束(hzl)=============================================
             */

            // 获取用户信息
            userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);

            if (IDataUtil.isEmpty(userInfo))
            {
                result.put("X_RSPCODE", "2998");
                result.put("X_RSPDESC", "受理失败");
                result.put("X_RSPTYPE", "2");
                result.put("X_RESULTCODE", "501001");
                result.put("X_RESULTINFO", "用户资料不存在！");
                return result;
            }

            userId = userInfo.getString("USER_ID");

            input.put("USER_ID", userId);
            input.put("CUST_ID", userInfo.getString("CUST_ID"));

            // 检查用户密码
            if (StringUtils.equals(strPwdType, CUSTOME_SERVICE_PWD))
            {// 服务客服密码检验
                IData rsData = UserPasswordInfoComm.checkUserPWD(input);
                if (!StringUtils.equals(rsData.getString("X_RESULTCODE", ""), "0"))
                {
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RSPDESC", "受理失败");
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RESULTCODE", rsData.getString("X_RESULTCODE", ""));
                    result.put("X_RESULTINFO", rsData.getString("X_RESULTINFO", ""));
                    return result;
                }
            }
            else
            {
                // 对于互联网密码、随机密码目前未定义
            }

            // 检查CUST_ID是否变更
            if (StringUtils.isNotBlank(strCustIDInParam) && !StringUtils.equals(strCustIDInParam, input.getString("CUST_ID")))
            {
                strCustStatus = "Y";
            }

            // 生成身份凭证
            strSb = new StringBuilder("ua");

            strSb.append(userId);
            // 处理某些省user_id长度不为16位,后面补0
            for (int i = 0; i < 16 - userId.length(); ++i)
                strSb.append("0");
            //2015-04-28 lxm
            //String strNow = DateFormatUtils.format(Calendar.getInstance(), "yyyyMMddHHmmss");
            String strNow = SeqMgr.getTradeIdFromDb();
            strNow=strNow.substring(2);
            strSb.append(strNow);
            input.put("IDENT_CODE", strSb.toString());

            // 已经存在有效的身份凭证的话,则注销现有凭证
            IData identUser = IdentcardInfoQry.qryIdentInfoByUserId(userId, strIdentCodeType,strPwdType);
            if (IDataUtil.isNotEmpty(identUser))
            {
                recordCount = Dao.executeUpdateByCodeCode("TF_B_IDENTCARD_MANAGE", "UPD_DISABLE_BY_USERID", input);
                if (recordCount <= 0)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_916);
                }
            }

            // 身份凭证信息入库
            input.put("IDENT_CODE_LEVEL", strPwdType);
            input.put("HOME_PROVINCE", "898");
            if (StringUtils.equals(strIdentCodeType, EFFICTIVE_CERT))
                recordCount = Dao.executeUpdateByCodeCode("TF_B_IDENTCARD_MANAGE", "INS_EFFECTIVE_IDENT", input);
            //if (recordCount <= 0)
           // {
               // CSAppException.apperr(CrmCommException.CRM_COMM_919);
            //}
            else if (StringUtils.equals(strIdentCodeType, TEMP_CERT))
                recordCount = Dao.executeUpdateByCodeCode("TF_B_IDENTCARD_MANAGE", "INS_TEMP_IDENT", input);
            if (recordCount <= 0)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_919);
            }

            // 调用客户管理接触信息接口
            input.put("MSISDN", input.getString("SERIAL_NUMBER"));
            callUspRequest(input, "login_auth");

        }
        catch (Exception e)
        {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            if (log.isDebugEnabled())
            {
                log.debug("exception:" + e.getMessage() + ",stack:" + writer.getBuffer().toString());
                log.debug("************鉴权接口异常,异常原因：" + e.toString());
            }

            result.put("X_RSPCODE", "2998");
            result.put("X_RSPDESC", "受理失败");
            result.put("X_RSPTYPE", "2");
            if (e.getMessage().indexOf(":") >= 1)// 预防抛出的异常信息格式不规范
                result.put("X_RESULTCODE", e.getMessage().substring(1, e.getMessage().indexOf(":")));
            else
                result.put("X_RESULTCODE", "20501230");// 出现异常时,返回给一级boss的的默认code
            if (e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2)
                result.put("X_RESULTINFO", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
            else
                result.put("X_RESULTINFO", "业务处理中出现异常");
            return result;
        }

        // 返回认证信息
        result.put("X_RESULTCODE", "0");
        // 避免覆盖检验密码的返回信息
        if (StringUtils.isBlank(result.getString("X_RESULTINFO")))
        {
            result.put("X_RESULTINFO", "OK");
        }
        String brandCode = changeBrandCode(userId, userInfo.getString("BRAND_CODE"));
        result.put("CUST_ID_STATUS", strCustStatus);
        result.put("IDENT_CODE", strSb.toString());
        result.put("IDENT_CODE_LEVEL", strPwdType);
        result.put("IDENT_CODE_TYPE", strIdentCodeType);
        result.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));

        result.put("CUST_ID", input.getString("CUST_ID"));
        result.put("HOME_PROVINCE", "898");
        result.put("BRAND_CODE", brandCode);
        result.put("USER_STATUS", IBossCovertor.getIBossUserState(userInfo.getString("USER_STATE_CODESET")));
        result.put("X_RSPCODE", "0000");
        result.put("X_RSPTYPE", "0");
        result.put("X_RSPDESC", "受理成功");
        return result;
    }

    /**
     * REQ201806080004+ by mengqx 20180828
     * @param input
     * @return
     * @throws Exception
     */
    public IData loginIdentNew(IData input) throws Exception
    {
        IData userInfo = null;
        String userId = null;
        IData result = new DataMap();
        String strCustStatus = "N";
        StringBuilder strSb;
        String strPwdType;
        String strIdentCodeType;
        int recordCount = 0;

        // 设置必须返回的以下3字段初值
        result.put("IDENT_CODE", "0");
        result.put("IDENT_CODE_LEVEL", "00");
        result.put("IDENT_CODE_TYPE", "00");
        try
        {
            // 参数检查
            IDataUtil.chkParam(input, "USER_TYPE");
            strPwdType = IDataUtil.chkParam(input, "PWD_TYPE");
            String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
            strIdentCodeType = IDataUtil.chkParam(input, "IDENT_CODE_TYPE");
            String inModeCode = IDataUtil.chkParam(input, "IN_MODE_CODE");
            IDataUtil.chkParam(input, "EFFECTIVE_TIME");

            if (!StringUtils.equals(inModeCode, "11"))// 当前仅支持统一认证中心进行调用
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_914);
            }

            input.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

            strIdentCodeType = input.getString("IDENT_CODE_TYPE");
            // 临时身份凭证,必须传入业务编码
            if (StringUtils.equals(strIdentCodeType, TEMP_CERT))
            {
                IDataUtil.chkParam(input, "BUSINESS_CODE");
            }
            // 客服密码&互联网密码类型时,必须传入密码值
            if (!StringUtils.equals(strPwdType, RAND_PWD))
            {
                IDataUtil.chkParam(input, "USER_PWD");
                input.put("USER_PASSWD", input.getString("USER_PWD"));
            }

            // 获取入参CUST_ID,用于比较cust_id是否已经变更
            String strCustIDInParam = input.getString("CUST_ID");

            IDataset userInfos = UserInfoQry.getLatestUserInfosBySerialNumber(serialNumber);
            /**
             * ================================================新增校验开始===============================================
             */
            if(IDataUtil.isNotEmpty(userInfos)){
            	userInfo = userInfos.getData(0);
            	result.put("PRI_SERIAL_NUMBER", serialNumber);
            	result.put("RSP_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
				if("1".equals(userInfo.getString("REMOVE_TAG")) || "3".equals(userInfo.getString("REMOVE_TAG"))){
					result.put("X_RSPCODE", "2998");
	                result.put("X_RSPDESC", "受理失败");
	                result.put("X_RSPTYPE", "2");
	                result.put("X_RESULTCODE", "2006");
	                result.put("X_RESULTINFO", "用户预销户");
	                return result;
				} else if("2".equals(userInfo.getString("REMOVE_TAG")) || "4".equals(userInfo.getString("REMOVE_TAG"))){
					result.put("X_RSPCODE", "2998");
	                result.put("X_RSPDESC", "受理失败");
	                result.put("X_RSPTYPE", "2");
	                result.put("X_RESULTCODE", "2007");
	                result.put("X_RESULTINFO", "用户已销户");
	                return result;
				}
			} else {
				result.put("X_RSPCODE", "2998");
                result.put("X_RSPDESC", "受理失败");
                result.put("X_RSPTYPE", "2");
                result.put("X_RESULTCODE", "4005");
                result.put("X_RESULTINFO", "手机账号不存在");
                return result;
			}
            
            // 获取用户信息
            userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);

            
            userId = userInfo.getString("USER_ID");
            
            IDataset svcstateLists = UserSvcStateInfoQry.getUserMainState(userId);
    		if (svcstateLists == null || svcstateLists.size() <= 0) {
    			result.put("X_RSPCODE", "2998");
                result.put("X_RSPDESC", "受理失败");
                result.put("X_RSPTYPE", "2");
                result.put("X_RESULTCODE", "2099");
                result.put("X_RESULTINFO", "主号其他原因");
                return result;
    		} else {
    			if("0".equals(svcstateLists.getData(0).getString("SERVICE_ID"))){
    				String stateCode = svcstateLists.getData(0).getString("STATE_CODE");
    				if(!"0".equals(stateCode)){
    					if("1".equals(stateCode)){
    						result.put("X_RSPCODE", "2998");
    		                result.put("X_RSPDESC", "受理失败");
    		                result.put("X_RSPTYPE", "2");
    		                result.put("X_RESULTCODE", "2004");
    		                result.put("X_RESULTINFO", "用户已单向停机");
    		                return result;
    					}else if("5".equals(stateCode)){
    						result.put("X_RSPCODE", "2998");
    		                result.put("X_RSPDESC", "受理失败");
    		                result.put("X_RSPTYPE", "2");
    		                result.put("X_RESULTCODE", "2005");
    		                result.put("X_RESULTINFO", "用户已停机");
    		                return result;
    					} else {
    						result.put("X_RSPCODE", "2998");
    		                result.put("X_RSPDESC", "受理失败");
    		                result.put("X_RSPTYPE", "2");
    		                result.put("X_RESULTCODE", "2009");
    		                result.put("X_RESULTINFO", "用户状态非法");
    		                return result;
    					}
    				}
    			} else {
    				result.put("X_RSPCODE", "2998");
	                result.put("X_RSPDESC", "受理失败");
	                result.put("X_RSPTYPE", "2");
	                result.put("X_RESULTCODE", "2099");
	                result.put("X_RESULTINFO", "主号其他原因");
	                return result;
    			}
    		}
    		
            
            //================================================新增校验结束===============================================
           


            input.put("USER_ID", userId);
            input.put("CUST_ID", userInfo.getString("CUST_ID"));

            
            // 获取客户资料
            IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(input.getString("CUST_ID"));
            if (custInfo == null || custInfo.size() < 1)
            {
                result.put("X_RESULTCODE", "4005");
                result.put("X_CHECK_INFO", "3");
                result.put("X_RESULTINFO", "手机账号不存在");
                return result;
            }else if("0".equals(custInfo.getString("IS_REAL_NAME"))){
    			result.put("X_RSPCODE", "2998");
                result.put("X_RSPDESC", "受理失败");
                result.put("X_RSPTYPE", "2");
                result.put("X_RESULTCODE", "2031");
                result.put("X_RESULTINFO", "用户未实名登记");
                return result;
    		}

            input.put("PSPT_ID", custInfo.getString("PSPT_ID"));
            input.put("USER_OLD_PASSWD", userInfo.getString("USER_PASSWD"));
            // 检查用户密码
            if (StringUtils.equals(strPwdType, CUSTOME_SERVICE_PWD))
            {// 服务客服密码检验
                IData rsData = UserPasswordInfoComm.checkUserPWDNew(input);
                if (!StringUtils.equals(rsData.getString("X_RESULTCODE", ""), "0000"))
                {
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RSPDESC", "受理失败");
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RESULTCODE", rsData.getString("X_RESULTCODE", ""));
                    result.put("X_RESULTINFO", rsData.getString("X_RESULTINFO", ""));
                    return result;
                }
            }
            else
            {
                // 对于互联网密码、随机密码目前未定义
            }

            // 检查CUST_ID是否变更
            if (StringUtils.isNotBlank(strCustIDInParam) && !StringUtils.equals(strCustIDInParam, input.getString("CUST_ID")))
            {
                strCustStatus = "Y";
            }

            // 生成身份凭证
            strSb = new StringBuilder("ua");

            strSb.append(userId);
            // 处理某些省user_id长度不为16位,后面补0
            for (int i = 0; i < 16 - userId.length(); ++i)
                strSb.append("0");
            String strNow = SeqMgr.getTradeIdFromDb();
            strNow=strNow.substring(2);
            strSb.append(strNow);
            input.put("IDENT_CODE", strSb.toString());

            // 已经存在有效的身份凭证的话,则注销现有凭证
            IData identUser = IdentcardInfoQry.qryIdentInfoByUserId(userId, strIdentCodeType,strPwdType);
            if (IDataUtil.isNotEmpty(identUser))
            {
                recordCount = Dao.executeUpdateByCodeCode("TF_B_IDENTCARD_MANAGE", "UPD_DISABLE_BY_USERID", input);
                if (recordCount <= 0)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_916);
                }
            }

            // 身份凭证信息入库
            input.put("IDENT_CODE_LEVEL", strPwdType);
            input.put("HOME_PROVINCE", "898");
            if (StringUtils.equals(strIdentCodeType, EFFICTIVE_CERT))
                recordCount = Dao.executeUpdateByCodeCode("TF_B_IDENTCARD_MANAGE", "INS_EFFECTIVE_IDENT", input);
            else if (StringUtils.equals(strIdentCodeType, TEMP_CERT))
                recordCount = Dao.executeUpdateByCodeCode("TF_B_IDENTCARD_MANAGE", "INS_TEMP_IDENT", input);
            if (recordCount <= 0)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_919);
            }


        }
        catch (Exception e)
        {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            if (log.isDebugEnabled())
            {
                log.debug("exception:" + e.getMessage() + ",stack:" + writer.getBuffer().toString());
                log.debug("************鉴权接口异常,异常原因：" + e.toString());
            }

            result.put("X_RSPCODE", "2998");
            result.put("X_RSPDESC", "受理失败");
            result.put("X_RSPTYPE", "2");
            if (e.getMessage().indexOf(":") >= 1)// 预防抛出的异常信息格式不规范
                result.put("X_RESULTCODE", e.getMessage().substring(1, e.getMessage().indexOf(":")));
            else
                result.put("X_RESULTCODE", "20501230");// 出现异常时,返回给一级boss的的默认code
            if (e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2)
                result.put("X_RESULTINFO", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
            else
                result.put("X_RESULTINFO", "业务处理中出现异常");
            return result;
        }

        // 返回认证信息
        result.put("X_RESULTCODE", "0000");
        // 避免覆盖检验密码的返回信息
        if (StringUtils.isBlank(result.getString("X_RESULTINFO")))
        {
            result.put("X_RESULTINFO", "业务成功");
        }
        String brandCode = changeBrandCode(userId, userInfo.getString("BRAND_CODE"));
        result.put("CUST_ID_STATUS", strCustStatus);
        result.put("IDENT_CODE", strSb.toString());
        result.put("IDENT_CODE_LEVEL", strPwdType);
        result.put("IDENT_CODE_TYPE", strIdentCodeType);
        result.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));

        result.put("CUST_ID", input.getString("CUST_ID"));
        result.put("HOME_PROVINCE", "898");
        result.put("BRAND_CODE", brandCode);
        result.put("USER_STATUS", IBossCovertor.getIBossUserState(userInfo.getString("USER_STATE_CODESET")));
        result.put("X_RSPCODE", "0000");
        result.put("X_RSPTYPE", "0");
        result.put("X_RSPDESC", "受理成功");
        return result;
    }

}
