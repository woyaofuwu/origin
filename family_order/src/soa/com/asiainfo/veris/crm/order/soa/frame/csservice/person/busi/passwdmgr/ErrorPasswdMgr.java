
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.Encryptor;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.Des;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.ConnMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;

public class ErrorPasswdMgr extends CSBizBean
{
	private static final Logger log = Logger.getLogger(ErrorPasswdMgr.class);

    private int iErrorCount = 0; /* 当前错误次数 */

    private int iErrorLimit = 0; /* 错误次数阀值 */

    private final int ILOCKEDSMS = 2; /* 短信类型：错误次数超过信息 */

    private final int IREMINDSMS = 1; /* 短信类型：错误次数提示信息 */

    private final int IUNLOCKSMS = 3; /* 短信类型：接触密码锁定信息 */

    private String strSerialNumber = ""; /* 用户手机号码 */

    private IData userInfo = new DataMap(); /* 用户信息 */

    private IData custInfo = new DataMap(); /* 客户信息 */
    /**
     * REQ201506020023 证件办理业务触发完善客户信息
     * chenxy3 2015-06-30
     * */
    private IData custPerson = new DataMap(); /* 客户资料信息TF_F_CUST_PERSON */

    /**
     * 参数配置 param_code: 验证类型 checkMode para_code1: 需要进行弱密码校验 para_code2: 需要发送弱密码提醒短信 para_code3: 需要记录错误次数 para_code4:
     * 达到阀值, 发送错误短信, 锁定密码 para_code5: 没有达到阀值情况下, 错误到达次数发送短信
     */
    private IData passParam = new DataMap();

    /**
     * 短信模板1
     */
    private final String REMINDSMS = "尊敬的客户，您今天已输入错误服务密码[FOULCOUNT]次，还可以输入[LEAVECOUNT]次，服务密码关系您个人信息安全，请注意保护!";

    /**
     * 短信模板2 "尊敬的客户，您今天已累计输入错误服务密码超过[ERRORCOUNT]次，如需通过服务密码办理业务，请您明天再试。请带相关证件去营业厅接触锁定!服务密码关系您个人信息安全，请注意保护。";
     */
    private final String LOCKEDSMS = "尊敬的客户，您今天已累计输入错误服务密码[ERRORCOUNT]次，如需通过服务密码办理业务，请您明天再试。服务密码关系您个人信息安全，请注意保护。";

    /**
     * 短信模板3
     */
    private final String UNLOCKSMS = "尊敬的客户，您的密码锁定已经解除，可以通过服务密码办理移动业务！";

    public ErrorPasswdMgr(IData inData) throws Exception
    {
        strSerialNumber = inData.getString("SERIAL_NUMBER", "");
        if (StringUtils.equals("", strSerialNumber))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_390);
        }

        if (!userInfo.containsKey("USER_ID"))
        {
            userInfo.putAll(PasswdAssistant.getUserInfo(strSerialNumber, inData.getString("USER_ID", "-1")));
        }
        if (!custInfo.containsKey("CUST_ID"))
        {
            custInfo.putAll(PasswdAssistant.getCustInfo(userInfo.getString("CUST_ID", "")));
        }
        
        iErrorLimit = PasswdAssistant.getErrorLimitCount();
        iErrorCount = PasswdAssistant.getErrorCountNow(userInfo.getString("USER_ID"));
        passParam = PasswdAssistant.getPassPara(inData);

    }

    /**
     * 密码验证的后续处理: 记录错误次数, 发送短信, 写台账记录解/锁定操作
     * 
     * @param inData
     * @throws Exception
     */
    public void checkAfterForPasswdMgr(IData inData) throws Exception
    {
        if (!chkValidateCond(inData))
        {
            return;
        }
        if (StringUtils.equals("1", inData.getString("RESULT_CODE")) && iErrorLimit > 0)
        {
            iErrorCount++; // 错误次数加1
            // 如果需要记录日志
            if ("1".equals(passParam.getString("PARA_CODE3", "-1")))
            {
                // 如果没有超过阀值, 仍然需要记录日志
                if (iErrorCount <= iErrorLimit)
                {
                    PasswdAssistant.insertTfBPasswdErrorlog(inData);
                }
            }
            // 如果错误已经达到阀值, 发送锁定短信, 插入锁定台账
            if (StringUtils.equals("1", passParam.getString("PARA_CODE4", "-1")))
            {
                if (iErrorCount >= iErrorLimit)
                {
                    // 记录历史台账数据
                    PasswdAssistant.insertTfBhTrade(inData, userInfo, custInfo, "76");
                    // TF_F_USER_OTHER表锁定
                    PasswdAssistant.insertTfFUserOther(inData);
                    inData.put("IS_CLOSE", "1");
                    this.sendSms(inData, this.ILOCKEDSMS);
                    return;
                }
            }
            // 发送错误次数提醒短信
            if (passParam.getInt("PARA_CODE5", 0) > 0 && iErrorCount >= passParam.getInt("PARA_CODE5", 0))
            {
                this.sendSms(inData, this.IREMINDSMS);
            }
        }
        else if (StringUtils.equals("0", inData.getString("RESULT_CODE")))
        {
            // 如果需要记录日志
            if ("1".equals(passParam.getString("PARA_CODE3", "-1")))
            {
                // 如果没有超过阀值, 仍然需要记录日志
                if (iErrorCount <= iErrorLimit)
                {
                    PasswdAssistant.removeTfBPasswdErrorlog(inData.getString("USER_ID"));
                }
            }
            // 解锁
            if (StringUtils.equals("1", passParam.getString("PARA_CODE4", "-1")))
            {
                if (iErrorCount >= iErrorLimit)
                {
                    PasswdAssistant.insertTfBhTrade(inData, userInfo, custInfo, "77");
                    this.sendSms(inData, this.IUNLOCKSMS);
                    return;
                }
            }
        }
    }

    /**
     * 密码校验前检查, 主要判断是否已经锁定密码
     * 
     * @param inData
     * @throws Exception
     */
    private void checkBeforeForPasswdMgr(IData inData) throws Exception
    {
        if (!chkValidateCond(inData))
        {
            return;
        }
        if (StringUtils.equals("1", passParam.getString("PARA_CODE4", "0")))
        {
            if (iErrorCount > 0 && PasswdAssistant.isLockPwd(userInfo.getString("USER_ID")))
            {
                this.sendSms(inData, this.ILOCKEDSMS);
                inData.put("RESULT_CODE", "1");
                inData.put("IS_CLOSE", "1");
                inData.put("RESULT_INFO", this.LOCKEDSMS.replaceAll("ERRORCOUNT", String.valueOf(iErrorLimit)));
            }
        }
    }

    /**
     * case 3 : 服务号码
     * 
     * @param inData
     * @throws Exception
     */
    private void checkPhoneNo(IData inData) throws Exception
    {

        String strInSerialNumber = inData.getString("SERIAL_NUMBER");
        if (StringUtils.equals(strInSerialNumber, userInfo.getString("SERIAL_NUMBER")))
        {
            inData.put("RESULT_CODE", "0");
            inData.put("RESULT_INFO", "");
            return;
        }
        else
        {
            inData.put("RESULT_CODE", "1");
            inData.put("RESULT_INFO", "服务号码不正确！");
            return;
        }
    }
    
    /**
     * case 5: 短信号码验证
     * 
     * @param inData
     * @throws Exception
     */
    private void checkSmsVerifyCode(IData inData) throws Exception
    {
    	String serialNumber = inData.getString("SERIAL_NUMBER");
    	String verifyCode = inData.getString("VERIFY_CODE", "");
    	Object cacheCode = SharedCache.get(SmsVerifyCodeBean.VERIFY_CODE_CACHE_KEY+serialNumber);
    	if(null == cacheCode || cacheCode.equals("")){
    		inData.put("RESULT_CODE", "1");
            inData.put("RESULT_INFO", "短信验证码无效！");
            return;
    	}
    	if(cacheCode.equals(verifyCode)){
    		inData.put("RESULT_CODE", "0");
            inData.put("RESULT_INFO", "");
            inData.put("CHECK_MODE_DESC", "短信验证码");
            SharedCache.delete(SmsVerifyCodeBean.VERIFY_CODE_CACHE_KEY+serialNumber);
    	}else{
    		inData.put("RESULT_CODE", "1");
            inData.put("RESULT_INFO", "短信验证码不正确！");
    	}
        return;
    }

    /**
     * 服务密码校验
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    private void checkPhonePasswd(IData inData) throws Exception
    {
        String inputPasswd = inData.getString("USER_PASSWD", "");
        String userId = userInfo.getString("USER_ID", "");
        String userPaswd = userInfo.getString("USER_PASSWD", "");
        
        /**
         * 密码解密
         * 2015-04-14
         * */
    	if(inputPasswd!=null&&!"".equals(inputPasswd)&&inputPasswd.indexOf("xxyy")>-1){ 
        	Des desObj = new Des();
        	inputPasswd=desObj.getDesPwd(inputPasswd);
    	}
        
        /**
         * 如果是宽带用户，验证密码需要定向校验绑定手机的服务密码
         */
        String serialNumber = userInfo.getString("SERIAL_NUMBER");
        if (serialNumber.startsWith("KD_"))
        {
        	String sn = null;
        	
        	//集团商务宽带
        	String rsrvStr10 = userInfo.getString("RSRV_STR10","");
            //无手机宽带鉴权处理
            String ifNoPhone="NO";
            if(serialNumber.length()==10){
                IData param=new DataMap();
                param.put("ACCOUNT_ID", sn);
                param.put("STATE", "3");
                IDataset wideAcctInfos = PasswdAssistant.queryWideNetAccoutInfo(param);
                if(wideAcctInfos!=null && wideAcctInfos.size()>0){
                    ifNoPhone="YES";
                }
            } 
            if(((StringUtils.isNotBlank(rsrvStr10) && "BNBD".equals(rsrvStr10))) || "YES".equals(ifNoPhone)){
        		sn = serialNumber;
        	} else {
        		sn = serialNumber.substring(3);
        	}
        	
            IData userData = UcaInfoQry.qryUserInfoBySn(sn);
            if (IDataUtil.isEmpty(userData))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_528, sn);
            }
            userId = userData.getString("USER_ID", "");
            userPaswd = userData.getString("USER_PASSWD", "");
        }

        String genKey = PasswdAssistant.getSubUserId(userId);
        String strPassword = Encryptor.fnEncrypt(inputPasswd, genKey);
        if (StringUtils.isBlank(strPassword))
            strPassword = "";

        if (StringUtils.equals(strPassword, userPaswd))
        {
            inData.put("RESULT_CODE", "0");
            inData.put("RESULT_INFO", "");
            inData.put("CHECK_MODE_DESC", "服务号码＋用户密码");
            return;
        }
        else
        {
            genKey = PasswdAssistant.getUserPasswdKey(userId);
            if (StringUtils.isNotBlank(genKey))
            {
                strPassword = Encryptor.fnEncrypt(inputPasswd, genKey);
                if (StringUtils.isBlank(strPassword))
                    strPassword = "";
                if (StringUtils.equals(strPassword, userPaswd))
                {
                    inData.put("RESULT_CODE", "0");
                    inData.put("RESULT_INFO", "");
                    inData.put("CHECK_MODE_DESC", "服务号码＋用户密码");
                    return;
                }
            }
            inData.put("RESULT_CODE", "1");
            inData.put("RESULT_INFO", "服务密码不正确!");
            return;
        }
    }

    /**
     * 证件号码校验
     * 
     * @param inData
     * @throws Exception
     */
    private void checkPsptId(IData inData) throws Exception
    {
    	/**
         * REQ201506020023 证件办理业务触发完善客户信息
         * */
        if (!custPerson.containsKey("CUST_ID"))
        {
        	//这里会导致集团宽带的业务报错，因为集团宽带鉴权的时候有CUSTOMER表，但是没有cust_person表。
        	//要确认是否集团宽带鉴权的时候会不会使用二代证读卡器读。
        	custPerson=CustPersonInfoQry.qryPerCustInfoByCustIDCustType(userInfo.getString("CUST_ID", ""), "0").getData(0);
        }
        String strInPsptId = inData.getString("PSPT_ID", "");
        String strInPsptTypeCode = inData.getString("PSPT_TYPE_CODE", "");

        String strPsptTypeCode = custInfo.getString("PSPT_TYPE_CODE");
        String strPsptId = custInfo.getString("PSPT_ID");
        
        /**
         * REQ201506020023 证件办理业务触发完善客户信息
         * chenxy3 2015-06-30
         * */
        String e_name=inData.getString("E_NAME", "");
        String e_address=inData.getString("E_ADDRESS", "");
        
        String custName=custPerson.getString("CUST_NAME", "");
        String psptAddr=custPerson.getString("PSPT_ADDR","");
        
        /**由于市场部要求，将证件类型判断取消
         * REQ201503170006关于开户、补卡等业务校验规则优化的需求
         * chenxy3 2015-03-20
         * 根据需求要求，要放开证件类型的校验
         * 判断录入的证件类型：
         * 非身份证的，要证件类型+证件号码均对应数据库
         * 身份证，不区分本地外地
         */
        if (!StringUtils.equals(strInPsptTypeCode, "0") && !StringUtils.equals(strInPsptTypeCode, "1")){
	        if (!StringUtils.equals(strInPsptTypeCode, strPsptTypeCode))
	        {
	        	inData.put("RESULT_CODE", "1");
	            inData.put("RESULT_INFO", "证件类型不正确！");
	            return;
	        }
        }else{
        	if(!StringUtils.equals(strPsptTypeCode, "0") && !StringUtils.equals(strPsptTypeCode, "1")){
        		inData.put("RESULT_CODE", "1");
	            inData.put("RESULT_INFO", "证件类型与原始资料不匹配！");
	            return;
        	}
        }
        // 为身份证或外地身份证
        if (StringUtils.equals(strPsptTypeCode, "0") || StringUtils.equals(strPsptTypeCode, "1"))
        {
            // 证件号码非15或18位的
            if (strInPsptId.length() != 15 && strInPsptId.length() != 18)
            {
                inData.put("RESULT_CODE", "1");
                inData.put("RESULT_INFO", "证件号码不正确！");
                return;
            }
            
            /**
             * REQ201506020023 证件办理业务触发完善客户信息
             * chenxy3 2015-06-30
             * */
        	if(e_name!=null && !"".equals(e_name)){
        		//REQ201510260005 身份证件校验优化【2015下岛优化】 chenxy3 20151106 18位带X身份证不区分大小写
        		if(strPsptId.length() == 18 && !StringUtils.equalsIgnoreCase(strInPsptId, strPsptId)){
        			inData.put("RESULT_CODE", "1");
                    inData.put("RESULT_INFO", "证件号码不正确！");
                    return;
        		}else if(strPsptId.length() == 15){
        			String strInPsptId15 = PasswdAssistant.standPsptId(strPsptTypeCode, strInPsptId);
        			if (!StringUtils.equals(strInPsptId15, strPsptId))
        	        { 
        				inData.put("RESULT_CODE", "1");
                        inData.put("RESULT_INFO", "证件号码不正确！");
                        return;
        	        }else{
        	        	try{
	        	        	//调用资料变更服务
	        	        	//1、15位身份证自动升级18位。2、证件地址长度不一样，自动更新证件地址
	        	        	IData inputData=new DataMap();
	        	        	inputData.put("USER_ID", userInfo.getString("USER_ID",""));
	        	        	inputData.put("CUST_ID", userInfo.getString("CUST_ID",""));
	        	        	inputData.put("SERIAL_NUMBER", inData.getString("SERIAL_NUMBER"));
	        	        	
	        	        	inputData.put("PSPT_ID",  strInPsptId);
	        	        	inputData.put("CUST_NAME",  e_name);
	        	        	inputData.put("PSPT_ADDR",  e_address);
	        	        	inputData.put("REMARK",  "");
	        	        	inputData.put("UPDATE_STAFF_ID",  this.getVisit().getStaffId());
	        	        	
	        	            /*IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", inputData);
	        	            
	        	            IData params=new DataMap();
	        	            params = new DataMap(custInfos.getData(0).getData("CUST_INFO")); 
	        	            params.put("SERIAL_NUMBER", inData.getString("SERIAL_NUMBER"));
	        	            params.put("CHECK_MODE", inData.getString("CHECK_MODE"));// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱
	        	            params.put("TRADE_TYPE_CODE", "60");
	        	            params.put("REMARK", "系统自动触发完善证件号码或地址信息");
	        	            params.put("PSPT_ID", strInPsptId);
	        	            CSBizBean.getVisit().setStaffId("ITFSM000");//待确认
	        	            if(e_address.length()!=psptAddr.length()){
	        	            	params.put("PSPT_ADDR", e_address);
	        	            }
	        	            params.putAll(inData); 
	        	     
	        	            IDataset results = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", params);*/
	        	        	SQLParser parser = new SQLParser(inputData); 
	        	            parser.addSQL(" select t.*  ");
	        	            parser.addSQL(" from TF_B_AUTO_UPD_PSPTID t  ");
	        	            parser.addSQL(" where t.USER_ID=:USER_ID ");
	        	            parser.addSQL(" AND T.CUST_ID=:CUST_ID "); 
	        	            parser.addSQL(" AND T.AUTO_TAG='0' ");
	        	            IDataset isets=Dao.qryByParse(parser);
	        	            if(isets==null || isets.size()==0){
	        	            	Dao.executeUpdateByCodeCode("TF_B_AUTO_UPD_PSPTID", "INS_B_AUTO_UPD_PSPTID", inputData); 
	        	            }
        	        	}catch(Exception e){
        	        		//log.info("(e);
        	        		inData.put("RESULT_INFO", "系统发现当前用户的身份证号码或者证件地址与读取的二代证不一致，自动触发修改失败。");
                        	inData.put("RESULT_CODE", "2"); //提示但不阻止
                            return;
        	        	}
        	        }
        		}
        		//REQ201510260005 身份证件校验优化【2015下岛优化】 chenxy3 20151106 18位带X身份证不区分大小写
        		if(strPsptId.length() == 18 && StringUtils.equalsIgnoreCase(strInPsptId, strPsptId) && e_address.length()!=psptAddr.length()){
        			try{
        	        	//调用资料变更服务
        	        	//2、证件地址长度不一样，自动更新证件地址
        	        	IData inputData=new DataMap();
        	        	inputData.put("USER_ID", userInfo.getString("USER_ID",""));
        	        	inputData.put("CUST_ID", userInfo.getString("CUST_ID",""));
        	        	inputData.put("SERIAL_NUMBER", inData.getString("SERIAL_NUMBER"));
        	        	
        	        	inputData.put("PSPT_ID",  strInPsptId);
        	        	inputData.put("CUST_NAME",  e_name);
        	        	inputData.put("PSPT_ADDR",  e_address);
        	        	inputData.put("REMARK",  "");
        	        	inputData.put("UPDATE_STAFF_ID",  this.getVisit().getStaffId());
        	        	 
        	            /*IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", inputData);
        	            
        	            IData params=new DataMap();
        	            params = new DataMap(custInfos.getData(0).getData("CUST_INFO")); 
        	            params.put("SERIAL_NUMBER", inData.getString("SERIAL_NUMBER"));
        	            params.put("CHECK_MODE", inData.getString("CHECK_MODE"));// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱
        	            params.put("TRADE_TYPE_CODE", "60");
        	            params.put("REMARK", "系统自动触发完善证件号码或地址信息"); 
        	            CSBizBean.getVisit().setStaffId("ITFSM000");//待确认
        	            params.put("PSPT_ADDR", e_address);
        	            params.putAll(inData); 
        	     
        	            IDataset results = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", params);*/
        	        	SQLParser parser = new SQLParser(inputData); 
        	            parser.addSQL(" select t.*  ");
        	            parser.addSQL(" from TF_B_AUTO_UPD_PSPTID t  ");
        	            parser.addSQL(" where t.USER_ID=:USER_ID ");
        	            parser.addSQL(" AND T.CUST_ID=:CUST_ID "); 
        	            parser.addSQL(" AND T.AUTO_TAG='0' ");
        	            IDataset isets=Dao.qryByParse(parser);
        	            if(isets==null || isets.size()==0){
        	            	Dao.executeUpdateByCodeCode("TF_B_AUTO_UPD_PSPTID", "INS_B_AUTO_UPD_PSPTID", inputData); 
        	            }
    	        	}catch(Exception e){
    	        		//log.info("(e);
    	        		inData.put("RESULT_INFO", "系统发现当前用户的证件地址与读取的二代证不一致，自动触发修改失败。");
                    	inData.put("RESULT_CODE", "2"); //提示但不阻止
                        return;
    	        	}
        		}
        		
        		// 系统里的身份证或外地身份证证件号码非15或非18位的不给予认证办理业务，按贾宗帅要求。
                if (strPsptId.length() != 15 && strPsptId.length() != 18)
                {
                	if(!StringUtils.equalsIgnoreCase(strInPsptId, strPsptId)){
                		inData.put("RESULT_CODE", "1");
                		inData.put("RESULT_INFO", "证件号码不匹配正确，不能办理业务！！");
                		return;
                	}
                }
        		
            	if (!StringUtils.equals(e_name, custName)) { 
            		inData.put("RESULT_INFO", "号码登记姓名和办理证件不一致，请与客户确认后进行修改");
                	inData.put("RESULT_CODE", "2"); //提示但不阻止
                    return;
                }
        	}else{            
	            if (strInPsptId.length() == 18 && strPsptId.length() == 15)
	            {
	                strInPsptId = PasswdAssistant.standPsptId(strPsptTypeCode, strInPsptId);
	            }
	            if (strInPsptId.length() == 15 && strPsptId.length() == 18)
	            {
	                strPsptId = PasswdAssistant.standPsptId(strPsptTypeCode, strPsptId);
	            }
	            //REQ201510260005 身份证件校验优化【2015下岛优化】 chenxy3 20151106 18位带X身份证不区分大小写
	            if (StringUtils.equalsIgnoreCase(strInPsptId, strPsptId))
	            {
	            	
	            	inData.put("RESULT_INFO", "");
	            	inData.put("RESULT_CODE", "0");
	                inData.put("CHECK_MODE_DESC", "用户服务号码＋用户证件");
	                return;
	            }
	            else
	            {
	                inData.put("RESULT_CODE", "1");
	                inData.put("RESULT_INFO", "证件号码不正确！");
	                return;
	            }
        	}
        }
        if (!StringUtils.equals(strPsptTypeCode, "0") && !StringUtils.equals(strPsptTypeCode, "1")){
        	if (StringUtils.equals(strInPsptId, strPsptId))
            {
                inData.put("RESULT_CODE", "0");
                inData.put("RESULT_INFO", "");
                inData.put("CHECK_MODE_DESC", "用户服务号码＋用户证件");
                return;
            }
            else
            {
                inData.put("RESULT_CODE", "1");
                inData.put("RESULT_INFO", "证件号码不正确！");
                return;
            }
        }
    }

    /**
     * SIM卡号（或白卡号）校验
     * 
     * @param inData
     * @throws Exception
     */
    private void checkSimCardNo(IData inData) throws Exception
    {
        String strInSimCardNo = inData.getString("SIM_NO", "");
        String userId = userInfo.getString("USER_ID", "");
        if (StringUtils.isBlank(userId))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_429);
        }
        IData userSimInfo = PasswdAssistant.getUserSimInfo(userId);
        String strSimCardNo = userSimInfo.getString("RES_CODE");
        if (!StringUtils.equals(strInSimCardNo, strSimCardNo))
        {
        	IDataset simCardInfos = ResCall.getSimCardInfo("0", strSimCardNo, "", "1");
            if (IDataUtil.isEmpty(simCardInfos))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_47);
            }
            String emptyCardId = simCardInfos.getData(0).getString("EMPTY_CARD_ID", "");
            if (!StringUtils.equals(strInSimCardNo, emptyCardId))
            {
                inData.put("RESULT_CODE", "1");
                inData.put("RESULT_INFO", "SIM卡号（或白卡号）不正确");
                inData.put("CHECK_MODE_DESC", "SIM卡号（或白卡号）不正确");
                return;
            }
        }
    	inData.put("RESULT_CODE", "0");
        inData.put("RESULT_INFO", "");
        inData.put("CHECK_MODE_DESC", "SIM卡号（或白卡号）");
    }

    /**
     * 弱密码管理入口
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public IData checkUserPasswd(IData inData) throws Exception
    {
        String routeEparchyCode = BizRoute.getRouteId();
        IData outData = new DataMap();
        int iCheckMode = inData.getInt("CHECK_MODE", 1);
        try
        {
            this.checkBeforeForPasswdMgr(inData);
            if (StringUtils.equals("1", inData.getString("RESULT_CODE", "0")))
            {
                ConnMgr.getConnection(routeEparchyCode).commit();
                outData.put("RESULT_CODE", "1");
                outData.put("IS_CLOSE", inData.getString("IS_CLOSE", "0"));
                outData.put("RESULT_INFO", inData.getString("RESULT_INFO", "对不起，密码已经锁定！"));
                return outData;
            }
            switch (iCheckMode)
            {
                case 0: // 证件号码
                {
                    this.checkPsptId(inData);
                    break;
                }
                case 1: // 服务密码
                {
                    this.checkPhonePasswd(inData);
                    break;
                }
                case 2: // 服务密码 + SIM卡号(白卡号)
                {
                    this.checkPhonePasswd(inData);
                    if (StringUtils.equals("0", inData.getString("RESULT_CODE")))
                    {
                        this.checkSimCardNo(inData);
                    }
                    break;
                }
                case 3: // 服务号码 + 证件号码
                {
                    this.checkPhoneNo(inData);
                    if (StringUtils.equals("0", inData.getString("RESULT_CODE")))
                    {
                        this.checkPsptId(inData);
                    }
                    break;
                }
                case 4: // 服务密码 + 证件号码
                {
                    this.checkPhonePasswd(inData);
                    if (StringUtils.equals("0", inData.getString("RESULT_CODE")))
                    {
                        this.checkPsptId(inData);
                    }
                    break;
                }
                case 5: //短信验证 + SIM卡号(白卡号)
                {
                    this.checkSmsVerifyCode(inData);
                    if (StringUtils.equals("0", inData.getString("RESULT_CODE")))
                    {
                        this.checkSimCardNo(inData);
                    }
                    break;
                }
                case 6: //服务密码+验证码
                {
                    this.checkSmsVerifyCode(inData);
                    if (StringUtils.equals("0", inData.getString("RESULT_CODE")))
                    {
                    	this.checkPhonePasswd(inData);
                    }
                    break;
                }
                /**
                 * REQ201606270002 非实名用户关停改造需求
                 * chenxy3 20160628
                 * */
                case 7: //验证码
                {
                    this.checkSmsVerifyCode(inData); 
                    break;
                }
                case 8: //SIM卡号(白卡号)
                {
                	this.checkSimCardNo(inData);
                    break;
                }
                case 9: //证件号码+验证码
                {
                	this.checkPsptId(inData);
                	if (StringUtils.equals("0", inData.getString("RESULT_CODE")))
                    {
                		this.checkSmsVerifyCode(inData); 
                    }
                    break;
                }
                default:
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_531);
                    break;
                }
            }
            this.checkAfterForPasswdMgr(inData);
            ConnMgr.getConnection(routeEparchyCode).commit();
        }
        catch (Exception e)
        {
        	//log.info("(e);
            ConnMgr.getConnection(routeEparchyCode).rollback();
            CSAppException.apperr(BizException.CRM_BIZ_5, "进行认证校验异常，请与管理员联系");
        }
        finally
        {
            ConnMgr.getConnection(routeEparchyCode).close();
        }
        outData.put("IS_CLOSE", inData.getString("IS_CLOSE", "0"));
        outData.put("RESULT_CODE", inData.getString("RESULT_CODE", "0"));
        outData.put("RESULT_INFO", inData.getString("RESULT_INFO", ""));
        if (StringUtils.isNotBlank(inData.getString("SMS_CONTENT", "")))
        {
            outData.put("RESULT_INFO", inData.getString("RESULT_INFO", "") + "\n\n" + inData.getString("SMS_CONTENT", ""));
        }
        outData.put("CHECK_MODE_DESC", inData.getString("CHECK_MODE_DESC", ""));
        return outData;
    }

    /**
     * 判断是否需要检验的前置条件
     * 
     * @param inData
     * @return
     */
    private boolean chkValidateCond(IData inData)
    {
        // 判断是否需要进行弱密码校验，如果不需要，则直接返回
        if (StringUtils.equals("0", passParam.getString("PARA_CODE2", "0")))
        {
            return false;
        }
        return true;
    }

    /**
     * 提供给解锁界面的接口方法
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public IData eventUnlock(IData inData) throws Exception
    {

        this.checkPsptId(inData);
        if (StringUtils.equals("0", inData.getString("RESULT_CODE", "1")))
        {
            this.unlock(inData);
        }
        IData outData = new DataMap();
        outData.put("RESULT_CODE", inData.getString("RESULT_CODE"));
        outData.put("RESULT_INFO", inData.getString("RESULT_INFO"));
        return outData;
    }

    public int getErrorCount()
    {
        return this.iErrorCount;
    }

    public int getErrorLimit()
    {
        return this.iErrorLimit;
    }

    /**
     * 短信发送表 iSmsType : 0-弱密码提醒信息; 1-密码错误次数信息; 2-密码锁定信息
     * 
     * @param inData
     * @throws Exception
     */
    private void sendSms(IData inData, int iSmsType) throws Exception
    {

        String strContent = "";
        if (iSmsType == this.ILOCKEDSMS)
        {
            strContent = this.LOCKEDSMS.replaceAll("ERRORCOUNT", String.valueOf(this.iErrorLimit));
        }
        else if (iSmsType == this.IUNLOCKSMS)
        {
            strContent = this.UNLOCKSMS;
        }
        else if (iSmsType == this.IREMINDSMS)
        {
            strContent = this.REMINDSMS.replaceAll("FOULCOUNT", String.valueOf(iErrorCount));
            strContent = strContent.replaceAll("LEAVECOUNT", String.valueOf(iErrorLimit - iErrorCount));
        }
        inData.put("SMS_CONTENT", strContent);
        PasswdAssistant.insertTiOSms(inData, strContent, userInfo);
    }

    /**
     * 解锁过程
     * 
     * @param inData
     * @throws Exception
     */
    private void unlock(IData inData) throws Exception
    {
        PasswdAssistant.insertTfBhTrade(inData, userInfo, custInfo, "77");
        PasswdAssistant.removeTfBPasswdErrorlog(inData.getString("USER_ID"));
        this.sendSms(inData, this.IUNLOCKSMS);
    }
     

}
