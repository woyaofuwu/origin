package com.asiainfo.veris.crm.order.soa.person.busi.cmonline.selfterminal;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.abilityopenplatform.AbilityOpenPlatQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPasswdQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityPlatBean;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityPlatOrderBean;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityRuleCheck;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.PasswordUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.UserPasswordInfoComm;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.FamilyOperPreBean;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.FamilyCreateBean;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.selfhelpcard.KIFunc;
import com.asiainfo.veris.crm.order.soa.person.common.util.IdcardUtils;

/**
 * 新型自助终端接口
 * @author Administrator
 *
 */
public class SelfTerminalOperateSVC extends CSBizService {
	/**
	 * 支付订单同步接口 
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData payOrderSync(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceNumber");
			IDataUtil.chkParam(input, "orderNo");
			IDataUtil.chkParam(input, "accountMoney");
			IDataUtil.chkParam(input, "payMent");
			IDataUtil.chkParam(input, "orderMoney");
			IDataUtil.chkParam(input, "busiType");
			IDataUtil.chkParam(input, "payMentType");
			IDataUtil.chkParam(input, "paytrans");
			IDataUtil.chkParam(input, "payStatus");
			IDataUtil.chkParam(input, "orderStatusCode");
			IDataUtil.chkParam(input, "hallCode");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		SelfTerminalBean bean=BeanManager.createBean(SelfTerminalBean.class);
		input.put("OP_ID", getVisit().getStaffId());
		bean.addPayOrder(input);
		return SelfTerminalUtil.responseSuccess(new DataMap());
	}
	/**
	 * 用户密码校验
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData checkUserPwd(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		IData resultInfo=new DataMap();
		output.put("retInfo", resultInfo);
		
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceNum");
			IDataUtil.chkParam(input, "svcPwd");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		//获取用户资料
		String serialNumber=input.getString("serviceNum");
		String userInputPwd=input.getString("svcPwd");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo)){
	          return SelfTerminalUtil.responseFail("用户资料不存在",null);
	    }
		
        // 获取客户资料
		input.put("USER_ID", userInfo.getString("USER_ID"));
		input.put("CUST_ID", userInfo.getString("CUST_ID"));
        IData custInfo = UcaInfoQry.qryCustInfoByCustId(userInfo.getString("CUST_ID"));
        if (custInfo == null || custInfo.size() < 1)
        {
        	return SelfTerminalUtil.responseFail("客户资料不存在",null);
        }
        input.put("PSPT_ID", custInfo.getString("PSPT_ID"));
        input.put("PASSWD_TYPE_CODE", "1");
        input.put("BRAND_NAME", userInfo.getString("BRAND_NAME"));
        
        String userId = userInfo.getString("USER_ID");
        String eparchyCode = CSBizBean.getUserEparchyCode();
        IData tagInfo = ParamInfoQry.getCsmTagInfo(eparchyCode, "CS_NUM_PASSWORDERRORINPUT", "CSB", "0", "2", "3");
        int pwdErrorNum = tagInfo.getInt("TAG_NUMBER", 3);// 密码锁定的阈值
        
        // 查询密码是否已锁定
        //IDataset userOtherSet = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "PWDLOCK");
       // if (IDataUtil.isNotEmpty(userOtherSet))
       // {
        IDataset errorData = UserPasswdQry.queryPasswdErrorCount(userId, "", "");
        if (IDataUtil.isNotEmpty(errorData)){
        	int num = errorData.getData(0).getInt("ERROR_COUNT", 0);
        	if(num>pwdErrorNum){
        		resultInfo.put("checkResult", "0"); // 密码错误次数达到阀值
        		resultInfo.put("isWeak", "");
        		resultInfo.put("pwdLevel", "");
        		return SelfTerminalUtil.responseSuccess(output);
        	}
		}
       // }
        
        //密码校验
        String userOrignPsw = userInfo.getString("USER_PASSWD");
        if (StringUtils.isBlank(userOrignPsw)){
        	SelfTerminalUtil.responseFail("用户服务密码不存在",null);
        }
        if (StringUtils.isBlank(input.getString("IN_MODE_CODE")))
        {
        	input.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        }
        if (PasswdMgr.checkUserPassword(userInputPwd, userId, userOrignPsw)){// 密码正确
        	resultInfo.put("checkResult", "1");
        	//清除密码错误信息
        	UserPasswordInfoComm.delPwdErrInfo(userId);
        	//弱密码校验
        	input.put("USER_PASSWD", userInputPwd);
        	input.put("SERIAL_NUMBER", serialNumber);
        	IData result = PasswordUtil.checkUserPoorPWD(input);
        	if(IDataUtil.isNotEmpty(result)
        			&&("0".equals(result.getString("X_RESULTCODE"))
        			&& !"0".equals(result.getString("X_CHECK_INFO")))
                    ){
        		resultInfo.put("isWeak", "1");
        	}else{
        		resultInfo.put("isWeak", "0");
        	}
        	//是否初始密码
        	 boolean isDefaultPwd = false;
        	// 查询配置的初始密码
             String defaultPwd = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_TAG", new String[]
             { "EPARCHY_CODE", "TAG_CODE" }, "TAG_INFO", new String[]{ "0898", "CS_INF_DEFAULTPWD" });
             if (StringUtils.isNotBlank(defaultPwd)){
                 String password = PasswdMgr.encryptPassWD(userInputPwd, userId);
                 String defaultPw = PasswdMgr.encryptPassWD(defaultPwd, userId);
                 if(StringUtils.equals(password, defaultPw)){
                     isDefaultPwd = true;
                 }
             }
             if(isDefaultPwd){
            	 resultInfo.put("pwdLevel", "1");
             }else{
            	 resultInfo.put("pwdLevel", "2");
             }
        }else{// 密码错误
        	resultInfo.put("checkResult", "-1");
        	resultInfo.put("isWeak", "");
    		resultInfo.put("pwdLevel", "");
        	UserPasswordInfoComm.recordPasswdErrorInfo(userId, "1", input.getString("IN_MODE_CODE"));
        }
        
        return SelfTerminalUtil.responseSuccess(output);
	}
	/**
	 * 实名制校验
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData checkIsRealName(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		output.put("resultTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		
		String customerName=input.getString("customerName");
		String idCardType=input.getString("idCardType");
		String idCardNum=input.getString("idCardNum");
		String serviceType=input.getString("serviceType");
		String serviceNo=input.getString("serviceNo");
		
		if(StringUtils.isNotEmpty(serviceNo)||
				(StringUtils.isNotEmpty(customerName)&&StringUtils.isNotEmpty(idCardNum))){
			AbilityPlatBean apBean=BeanManager.createBean(AbilityPlatBean.class);
			//手机号码为空
			if(StringUtils.isEmpty(serviceNo)){
				IDataset custInfoList = apBean.getCustInfoByPspt(customerName,idCardType, idCardNum);
				if(IDataUtil.isEmpty(custInfoList)){
					return SelfTerminalUtil.responseFail("业务号码与客户姓名、证件号码信息不匹配！",output);
				}
			}
			//手机号码不为空
			if(StringUtils.isNotEmpty(serviceNo)){
				IData userInfo = UcaInfoQry.qryUserInfoBySn(serviceNo);
				if(IDataUtil.isEmpty(userInfo)){
			        return SelfTerminalUtil.responseFail("用户信息不存在！",output);
			   	}
				String custId=userInfo.getString("CUST_ID", "");
				IData custInfo=UcaInfoQry.qryCustomerInfoByCustId(custId);
	       	 	if(IDataUtil.isNotEmpty(custInfo)){
	       	 		//实名制标识
	       	 		String isRealName=custInfo.getString("IS_REAL_NAME", "");
	       	 		if("".equals(isRealName)||"0".equals(isRealName)){
	       			    //非实名制
	       	 			return SelfTerminalUtil.responseFail("未实名登记",output);
	       	 		}
	       	 	}
	       		IDataset custList = apBean.getCustInfoByPspt(custId,customerName,idCardType, idCardNum);
			   	if(IDataUtil.isEmpty(custList)){
			        return SelfTerminalUtil.responseFail("业务号码与客户姓名、证件号码信息不匹配！",output);
			   	}
			}
			
			return SelfTerminalUtil.responseSuccess(output);
		}else{
			return SelfTerminalUtil.responseFail("服务号码或者姓名和证件号码不能同时为空",output);
		}
	}
	/**
	 * 实名制登记
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData submitRealName(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceNum");
			IDataUtil.chkParam(input, "custName");
			IDataUtil.chkParam(input, "custCertNum");
			IDataUtil.chkParam(input, "certAddress");
			IDataUtil.chkParam(input, "userRealnameWayId");
			IDataUtil.chkParam(input, "contactType");
			IDataUtil.chkParam(input, "identitySN");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		//获取用户信息
		String serialNumber=input.getString("serviceNum");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if(IDataUtil.isEmpty(userInfo)){
	        return SelfTerminalUtil.responseFail("用户资料不存在！",output);
	   	}
		//获取客户信息
		IData pdData=new DataMap();
		pdData.put("CUST_EPARCHY_CODE", "0898");
		pdData.put("ROUTE_EPARCHY_CODE", "0898");
		pdData.put("TRADE_TYPE_CODE", "60");
		pdData.put("CHECK_MODE", input.getString("CHECK_MODE","Z"));
		pdData.put("SERIAL_NUMBER", serialNumber);
		pdData.put("CUST_ID", userInfo.get("CUST_ID"));
		pdData.put("USER_ID", userInfo.get("USER_ID"));
		IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", pdData);
		if(IDataUtil.isEmpty(custInfos)){
			return SelfTerminalUtil.responseFail("客户资料不存在！",output);
		}
		IData custInfo=custInfos.getData(0).getData("CUST_INFO");
		
		//拼装入参
		IData data=new DataMap();
		data.put("IS_REAL_NAME", "1");
		data.put("SERIAL_NUMBER", input.getString("serviceNum"));
		data.put("TRADE_TYPE_CODE", "60");
		//data.put("CHECK_MODE", "Z");
		
		data.put("CUST_NAME", input.getString("custName"));
		data.put("PSPT_ID", input.getString("custCertNum"));
		data.put("PSPT_ADDR", input.getString("certAddress"));
		data.put("POST_ADDRESS", input.getString("custAddress",custInfo.getString("POST_ADDRESS","")));
		data.put("HOME_ADDRESS",  input.getString("custAddress",custInfo.getString("HOME_ADDRESS","")));
		data.put("CITY_CODE_A", custInfo.getString("CITY_CODE_A",""));
		data.put("PSPT_TYPE_CODE", "0");//custInfo.getString("PSPT_TYPE_CODE","0"));
		String sex="";
		if(StringUtils.isEmpty(input.getString("gender"))){
			sex=custInfo.getString("SEX");
		}else{
			if("0".equals(input.getString("gender"))){
				sex="M";
			}else if("1".equals(input.getString("gender"))){
				sex="F";
			}
		}
		
		//民族转换
		String folkCode="";
		if(StringUtils.isNotEmpty(input.getString("nation"))){
			IDataset folkList=CommparaInfoQry.getCommNetInfo("CSM", "3838", "FOLK_CODE");
			for(int i=0;i<folkList.size();i++){
				if(input.getString("nation").equals(folkList.getData(i).getString("PARA_CODE2"))){
					folkCode=folkList.getData(i).getString("PARA_CODE1");
					break;
				}
			}
		}else{
			folkCode=custInfo.getString("FOLK_CODE","");
		}
		
		
		data.put("SEX", sex);
		data.put("PSPT_END_DATE", input.getString("certExpDate",custInfo.getString("PSPT_END_DATE","")));
		data.put("PHONE", input.getString("mainServiceNum",custInfo.getString("PHONE","")));
		data.put("CONTACT", input.getString("custName",custInfo.getString("CONTACT","")));
		data.put("CONTACT_PHONE", input.getString("mainServiceNum",custInfo.getString("CONTACT_PHONE","")));
		data.put("POST_CODE", input.getString("postCode",custInfo.getString("POST_CODE","")));
		data.put("CONTACT_TYPE_CODE",custInfo.getString("CONTACT_TYPE_CODE",""));
		data.put("WORK_NAME",custInfo.getString("WORK_NAME",""));
		data.put("WORK_DEPART",custInfo.getString("WORK_DEPART",""));
		data.put("BIRTHDAY", input.getString("birthday",custInfo.getString("BIRTHDAY","")));
		data.put("JOB_TYPE_CODE",custInfo.getString("JOB_TYPE_CODE",""));
		data.put("JOB",custInfo.getString("JOB",""));
		data.put("EDUCATE_DEGREE_CODE",custInfo.getString("EDUCATE_DEGREE_CODE",""));
		data.put("EMAIL",custInfo.getString("EMAIL",""));
		data.put("FAX_NBR",custInfo.getString("FAX_NBR",""));
		data.put("MARRIAGE",custInfo.getString("MARRIAGE",""));
		data.put("NATIONALITY_CODE",custInfo.getString("NATIONALITY_CODE",""));
		data.put("CHARACTER_TYPE_CODE",custInfo.getString("CHARACTER_TYPE_CODE",""));
		data.put("WEBUSER_ID",custInfo.getString("WEBUSER_ID",""));
		data.put("LANGUAGE_CODE",custInfo.getString("LANGUAGE_CODE",""));
		data.put("LOCAL_NATIVE_CODE",custInfo.getString("LOCAL_NATIVE_CODE",""));
		data.put("COMMUNITY_ID",custInfo.getString("COMMUNITY_ID",""));
		data.put("RELIGION_CODE",custInfo.getString("RELIGION_CODE",""));
		data.put("FOLK_CODE",folkCode);//民族
		data.put("REVENUE_LEVEL_CODE",custInfo.getString("REVENUE_LEVEL_CODE",""));
		data.put("AGENT_CUST_NAME",custInfo.getString("RSRV_STR7",""));
		data.put("AGENT_PSPT_TYPE_CODE",custInfo.getString("RSRV_STR8",""));
		data.put("AGENT_PSPT_ID",custInfo.getString("RSRV_STR9",""));
		data.put("AGENT_PSPT_ADDR",custInfo.getString("RSRV_STR10",""));
		data.put("USE",custInfo.getString("USE",""));
		data.put("USE_PSPT_TYPE_CODE",custInfo.getString("USE_PSPT_TYPE_CODE",""));
		data.put("USE_PSPT_ID",custInfo.getString("USE_PSPT_ID",""));
		data.put("USE_PSPT_ADDR",custInfo.getString("USE_PSPT_ADDR",""));
		data.put("CHANNEL_ID",input.getString("realnameChannelId",""));
		data.put("ISSUING_AUTHORITY",input.getString("registrationOrgan",""));
		data.put("TRANSACTION_ID",input.getString("identitySN",""));
		data.put("PSPT_START_DATE",input.getString("certEffDate",""));
		
		IDataset results=CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", data);
		output.put("transactionId", results.getData(0).getString("ORDER_ID"));
		
		//记录日志
		IData logParam=new DataMap();
		logParam.put("TRANS_ACTION_ID", input.getString("transactionId"));
		logParam.put("IDENTITY_SN", input.getString("identitySN"));
		logParam.put("CRM_ORDER_ID", results.getData(0).getString("ORDER_ID"));
		logParam.put("CRM_TRADE_ID", results.getData(0).getString("TRADE_ID"));
		logParam.put("OP_ID", getVisit().getStaffId());
		logParam.put("OP_ORG", getVisit().getDepartId());
		logParam.put("TRADE_TYPE_CODE", "60");
		logParam.put("SERIAL_NUMBER", serialNumber);
		SelfTerminalBean bean=BeanManager.createBean(SelfTerminalBean.class);
		bean.addSelfLog(logParam);
		
		return SelfTerminalUtil.responseSuccess(output);
	}
	/**
	 * 套餐办理
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData changeProduct(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceNum");
			IDataUtil.chkParam(input, "planId");
			IDataUtil.chkParam(input, "oprType");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		if((!("00".equals(input.getString("oprType"))))
				&&(!("01".equals(input.getString("oprType"))))){
			return SelfTerminalUtil.responseFail("不支持的操作类型oprType="+input.getString("oprType"),null);
		}
		
		String productId=input.getString("planId");
		String serialNumber=input.getString("serviceNum");
		String effectType=input.getString("effectType");
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if(IDataUtil.isEmpty(userInfo)){
	        return SelfTerminalUtil.responseFail("用户资料不存在！",output);
	   	}
		
		IDataset results =null;
		//订购
		if("00".equals(input.getString("oprType"))){
			IData productData = new DataMap();
	        productData.put("SERIAL_NUMBER", serialNumber);
	        productData.put("ELEMENT_ID", productId);
	        productData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);// 产品
	        productData.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);// 产品修改标识
	       
	        if("01".equals(effectType)){
	        	 productData.put("BOOKING_TAG", "1");//次月生效
	        	 productData.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
	        }else if("02".equals(effectType)){
	        	productData.put("BOOKING_TAG", "1");//次日生效
	        	productData.put("START_DATE",  SysDateMgr.addDays(SysDateMgr.getSysDate(), 1));
	        }else {
	        	 productData.put("BOOKING_TAG", "0");//立即生效
	        }
	        
	        results=CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", productData);
	    //取消预约    
		}else{ 
			 IData inData = new DataMap();
			 inData.put("SERIAL_NUMBER", serialNumber);
			 IDataset cancelTradeInfos =CSAppCall.call("SS.CancelChangeProductSVC.queryChangeProductTrade", inData);
			 if(IDataUtil.isEmpty(cancelTradeInfos)){
				 return SelfTerminalUtil.responseFail("该用户没有可取消的预约产品变更数据！",output);
			 }
			 String tradeId="";
			 for(int i=0;i<cancelTradeInfos.size();i++){
				 if(productId.equals(cancelTradeInfos.getData(i).getString("PRODUCT_ID"))){
					 tradeId=cancelTradeInfos.getData(i).getString("TRADE_ID");
					 break;
				 }
			 }
			 if(StringUtils.isEmpty(tradeId)){
				 return SelfTerminalUtil.responseFail("找不到产品["+productId+"]可取消的预约产品变更数据！",output);
			 }
			 inData.put("TRADE_ID", tradeId);
			 results=CSAppCall.call("SS.CancelChangeProductSVC.cancelChangeProductTrade", inData);
		}
        output.put("orderId", results.getData(0).getString("ORDER_ID"));
        
        //记录日志
		IData logParam=new DataMap();
		logParam.put("TRANS_ACTION_ID", input.getString("transactionId"));
		logParam.put("CRM_ORDER_ID", results.getData(0).getString("ORDER_ID"));
		logParam.put("CRM_TRADE_ID", results.getData(0).getString("TRADE_ID"));
		logParam.put("OP_ID", getVisit().getStaffId());
		logParam.put("OP_ORG", getVisit().getDepartId());
		logParam.put("TRADE_TYPE_CODE", "110");
		logParam.put("SERIAL_NUMBER", serialNumber);
		SelfTerminalBean bean=BeanManager.createBean(SelfTerminalBean.class);
		bean.addSelfLog(logParam);
		
		return SelfTerminalUtil.responseSuccess(output);
	}
	/**
	 * 停开机
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData stopAndOpenMobile(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		output.put("operTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceType");
			IDataUtil.chkParam(input, "serviceNum");
			IDataUtil.chkParam(input, "operType");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		String serialNumber=input.getString("serviceNum");
		String operType=input.getString("operType");
		if("00".equals(operType)){//本地开机
			IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
			if(IDataUtil.isEmpty(userInfo)){
		        return SelfTerminalUtil.responseFail("用户资料不存在！",output);
		   	}
			//组织入参
			IData data=new DataMap();
			data.put("AUTH_SERIAL_NUMBER", serialNumber);
			data.put("SERIAL_NUMBER", serialNumber);
			data.put("TRADE_TYPE_CODE", "133");
			data.put("IS_OPEN_WIDE", "N");
			data.put("IS_OPEN_IMS", "N");
			//data.put("CHECK_MODE", "Z");
			
			IDataset results = CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", data);
		    output.put("orderId", results.getData(0).getString("ORDER_ID"));
		    
		    //记录日志
			IData logParam=new DataMap();
			logParam.put("TRANS_ACTION_ID", input.getString("transactionId"));
			logParam.put("CRM_ORDER_ID", results.getData(0).getString("ORDER_ID"));
			logParam.put("CRM_TRADE_ID", results.getData(0).getString("TRADE_ID"));
			logParam.put("OP_ID", getVisit().getStaffId());
			logParam.put("OP_ORG", getVisit().getDepartId());
			logParam.put("TRADE_TYPE_CODE", "133");
			logParam.put("SERIAL_NUMBER", serialNumber);
			SelfTerminalBean bean=BeanManager.createBean(SelfTerminalBean.class);
			bean.addSelfLog(logParam);
			
		    return SelfTerminalUtil.responseSuccess(output);
		}else if("01".equals(operType)){//本地停机
			IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
			if(IDataUtil.isEmpty(userInfo)){
		        return SelfTerminalUtil.responseFail("用户资料不存在！",output);
		   	}
			//组织入参
			IData data=new DataMap();
			data.put("AUTH_SERIAL_NUMBER", serialNumber);
			data.put("SERIAL_NUMBER", serialNumber);
			data.put("TRADE_TYPE_CODE", "131");
			data.put("IS_STOP_WIDE", "N");
			//data.put("CHECK_MODE", "Z");
			
			IDataset results = CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", data);
		    output.put("orderId", results.getData(0).getString("ORDER_ID"));
		    
		    //记录日志
			IData logParam=new DataMap();
			logParam.put("TRANS_ACTION_ID", input.getString("transactionId"));
			logParam.put("CRM_ORDER_ID", results.getData(0).getString("ORDER_ID"));
			logParam.put("CRM_TRADE_ID", results.getData(0).getString("TRADE_ID"));
			logParam.put("OP_ID", getVisit().getStaffId());
			logParam.put("OP_ORG", getVisit().getDepartId());
			logParam.put("TRADE_TYPE_CODE", "131");
			logParam.put("SERIAL_NUMBER", serialNumber);
			SelfTerminalBean bean=BeanManager.createBean(SelfTerminalBean.class);
			bean.addSelfLog(logParam);
			
		    return SelfTerminalUtil.responseSuccess(output);
		}else{
			return SelfTerminalUtil.responseFail("暂不支持该操作类型[operType="+operType+"]",null);
		}
	}
	/**
	 * 用户密码修改
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData modifyUserPwd(IData inParam) throws Exception { 
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		IData resultInfo=new DataMap();
		output.put("retInfo", resultInfo);
		
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceNum");
			IDataUtil.chkParam(input, "oldPwd");
			IDataUtil.chkParam(input, "newPwd");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		String serialNumber=input.getString("serviceNum");
		String oldPwd=input.getString("oldPwd");
		String newPwd=input.getString("newPwd");
		String newPwdConfirm=input.getString("newPwdConfirm");
		
		//获取用户资料
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo)){
	          return SelfTerminalUtil.responseFail("用户资料不存在",null);
	    }
		IData custInfo = UcaInfoQry.qryCustInfoByCustId(userInfo.getString("CUST_ID"));
        if (custInfo == null || custInfo.size() < 1)
        {
        	return SelfTerminalUtil.responseFail("客户资料不存在",null);
        }
		//密码校验
        String userOrignPsw = userInfo.getString("USER_PASSWD");
        String userId=userInfo.getString("USER_ID");
        if (StringUtils.isBlank(userOrignPsw)){
        	SelfTerminalUtil.responseFail("用户服务密码不存在",null);
        }
        if (!PasswdMgr.checkUserPassword(oldPwd, userId, userOrignPsw)){
        	 return SelfTerminalUtil.responseFail("旧密码认证不通过",null);
        }
        if(StringUtils.isNotEmpty(newPwdConfirm)&&(!newPwdConfirm.equals(newPwd))){
        	return SelfTerminalUtil.responseFail("新密码与确认密码不一致",null);
        }
        
        if(newPwd.length()!=6){
			return SelfTerminalUtil.responseFail("请设置6位密码",null);
		}
		try{
			Integer.parseInt(newPwd);
		}catch(Exception e){
			return SelfTerminalUtil.responseFail("新密码非数字",null);
		}
        
        //弱密码校验
        input.put("USER_PASSWD", newPwd);
        input.put("SERIAL_NUMBER", serialNumber);
        input.put("USER_ID", userInfo.getString("USER_ID"));
		input.put("CUST_ID", userInfo.getString("CUST_ID"));
        input.put("PSPT_ID", custInfo.getString("PSPT_ID"));
        input.put("PASSWD_TYPE_CODE", "1");
        input.put("BRAND_NAME", userInfo.getString("BRAND_NAME"));
        if (StringUtils.isBlank(input.getString("IN_MODE_CODE")))
        {
        	input.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        }
        IData result = PasswordUtil.checkUserPoorPWD(input);
    	if(IDataUtil.isNotEmpty(result)
    			&&("0".equals(result.getString("X_RESULTCODE"))
    			&& !"0".equals(result.getString("X_CHECK_INFO")))
                ){
    		return SelfTerminalUtil.responseFail(result.getString("X_RESULTINFO"),null);
    	}
    	
        //构造修改密码入参
        IData data=new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("PASSWD_TYPE", "1");
        data.put("NEW_PASSWD", newPwd);
        data.put("TRADE_TYPE_CODE", "71");
        //data.put("CHECK_MODE", "Z");
        
        IDataset results = CSAppCall.call("SS.ModifyUserPwdInfoRegSVC.tradeReg", data);
        resultInfo.put("orderId", results.getData(0).getString("ORDER_ID"));
        
        //记录日志
		IData logParam=new DataMap();
		logParam.put("TRANS_ACTION_ID", input.getString("transactionId"));
		logParam.put("CRM_ORDER_ID", results.getData(0).getString("ORDER_ID"));
		logParam.put("CRM_TRADE_ID", results.getData(0).getString("TRADE_ID"));
		logParam.put("OP_ID", getVisit().getStaffId());
		logParam.put("OP_ORG", getVisit().getDepartId());
		logParam.put("TRADE_TYPE_CODE", "71");
		logParam.put("SERIAL_NUMBER", serialNumber);
		SelfTerminalBean bean=BeanManager.createBean(SelfTerminalBean.class);
		bean.addSelfLog(logParam);
		
	    return SelfTerminalUtil.responseSuccess(output);
	}
	/**
	 * 密码重置
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData retUserPwd(IData inParam) throws Exception { 
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		IData resultInfo=new DataMap();
		output.put("retInfo", resultInfo);
		
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceNum");
			IDataUtil.chkParam(input, "newPwd");
			IDataUtil.chkParam(input, "custCertNum");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		String serialNumber=input.getString("serviceNum");
		String newPwd=input.getString("newPwd");
		String custCertNum=input.getString("custCertNum");
		String custCertType=input.getString("custCertType");
		
		if(newPwd.length()!=6){
			return SelfTerminalUtil.responseFail("请设置6位密码",null);
		}
		try{
			Integer.parseInt(newPwd);
		}catch(Exception e){
			return SelfTerminalUtil.responseFail("新密码非数字",null);
		}
		
		//获取用户资料
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo)){
	         return SelfTerminalUtil.responseFail("用户资料不存在",null);
	    }
		IData custInfo = UcaInfoQry.qryCustInfoByCustId(userInfo.getString("CUST_ID"));
        if (custInfo == null || custInfo.size() < 1)
        {
        	return SelfTerminalUtil.responseFail("客户资料不存在",null);
        }
        //证件号码校验
        
        //15位转18位
        if(StringUtils.isNotEmpty(custInfo.getString("PSPT_ID"))&&15==custInfo.getString("PSPT_ID").length()){
        	String psptId18=IdcardUtils.conver15CardTo18(custInfo.getString("PSPT_ID"));
        	custInfo.put("PSPT_ID",psptId18);
        }
        
        if(!custInfo.getString("PSPT_ID").equals(custCertNum)){
        	return SelfTerminalUtil.responseFail("证件号码不正确",null);
        }
        if(StringUtils.isNotEmpty(custCertType)){
        	IDataset cardList=CommparaInfoQry.getCommNetInfo("CSM", "3838", "IDCARD");
        	boolean isHave=false;
        	for(int i=0;i<cardList.size();i++){
        		if(custInfo.getString("PSPT_TYPE_CODE","").equals(cardList.getData(i).getString("PARA_CODE2"))){
        			if(custCertType.equals(cardList.getData(i).getString("PARA_CODE3"))){
        				isHave=true;
        				break;
        			}
        		}
        	}
        	if(!isHave){
        		return SelfTerminalUtil.responseFail("证件类型不正确",null);
        	}
        }
        
        //弱密码校验
        input.put("USER_PASSWD", newPwd);
        input.put("SERIAL_NUMBER", serialNumber);
        input.put("USER_ID", userInfo.getString("USER_ID"));
		input.put("CUST_ID", userInfo.getString("CUST_ID"));
        input.put("PSPT_ID", custInfo.getString("PSPT_ID"));
        input.put("PASSWD_TYPE_CODE", "1");
        input.put("BRAND_NAME", userInfo.getString("BRAND_NAME"));
        if (StringUtils.isBlank(input.getString("IN_MODE_CODE")))
        {
        	input.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        }
        IData result = PasswordUtil.checkUserPoorPWD(input);
    	if(IDataUtil.isNotEmpty(result)
    			&&("0".equals(result.getString("X_RESULTCODE"))
    			&& !"0".equals(result.getString("X_CHECK_INFO")))
                ){
    		return SelfTerminalUtil.responseFail(result.getString("X_RESULTINFO"),null);
    	}
        
        //构造修改密码入参
        IData data=new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("PASSWD_TYPE", "1");
        data.put("NEW_PASSWD", newPwd);
        data.put("TRADE_TYPE_CODE", "73");
        //data.put("CHECK_MODE", "Z");
        
        IDataset results = CSAppCall.call("SS.ModifyUserPwdInfoRegSVC.tradeReg", data);
        resultInfo.put("orderId", results.getData(0).getString("ORDER_ID"));
        resultInfo.put("newPwd", newPwd);
        
        //记录日志
		IData logParam=new DataMap();
		logParam.put("TRANS_ACTION_ID", input.getString("transactionId"));
		logParam.put("CRM_ORDER_ID", results.getData(0).getString("ORDER_ID"));
		logParam.put("CRM_TRADE_ID", results.getData(0).getString("TRADE_ID"));
		logParam.put("OP_ID", getVisit().getStaffId());
		logParam.put("OP_ORG", getVisit().getDepartId());
		logParam.put("TRADE_TYPE_CODE", "73");
		logParam.put("SERIAL_NUMBER", serialNumber);
		SelfTerminalBean bean=BeanManager.createBean(SelfTerminalBean.class);
		bean.addSelfLog(logParam);
        
	    return SelfTerminalUtil.responseSuccess(output);
	}
	/**
	 * 家庭网成员管理接口
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData operatorFamilyNet(IData inParam) throws Exception { 
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		output.put("operTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceNum");
			IDataUtil.chkParam(input, "oprCode");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		String oprCode=input.getString("oprCode");
		String serialNumber=input.getString("serviceNum");
		
		if("01".equals(oprCode)){//添加成员
			
			try{
				IDataUtil.chkParam(input, "productId");
				IDataUtil.chkParam(input, "memberPhoneNo");
				//IDataUtil.chkParam(input, "MemberShortNoNew");
			}catch(Exception e){
				return SelfTerminalUtil.responseFail(e.getMessage(),null);
			}
			
			String productId=input.getString("productId");
			String memberPhoneNo=input.getString("memberPhoneNo");
			String MemberShortNoNew=input.getString("MemberShortNoNew");
			
			IData inputData=new DataMap();
			inputData.put("PRODUCT_ID",productId);
			inputData.put("SERIAL_NUMBER_B",memberPhoneNo);
			inputData.put("SERIAL_NUMBER",serialNumber);
			
			String SERIAL_NUMBER = inputData.getString("SERIAL_NUMBER");
			String SERIAL_NUMBER_B = inputData.getString("SERIAL_NUMBER_B");
			
			//通过手机号码搜索用户信息
			IData user = UcaInfoQry.qryUserInfoBySn(SERIAL_NUMBER);
			String USER_ID = user.getString("USER_ID");
			
			//设置副号短号
			//查询已经存在的副号码信息
			if(StringUtils.isEmpty(MemberShortNoNew)){
		        IDataset haveMebList = new DatasetList();
		        String[] shortCodes=new String[]{"520","521","522","523","524","525","526","527","528","529"};
		        IDataset result = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(USER_ID, "45", null);
		        if (IDataUtil.isNotEmpty(result))
		        {
		            IData rela = result.getData(0);
		            String userIdA = rela.getString("USER_ID_A");
		            IDataset mebList = RelaUUInfoQry.getUserRelationByUserIDA(userIdA,"45");
		            if (IDataUtil.isNotEmpty(mebList))
		            {
		                haveMebList = mebList;
		            }
		        }
		        for(int i=0;i<shortCodes.length;i++){
		        	boolean isHave=false;
		        	for(int j=0;j<haveMebList.size();j++){
		        		if(shortCodes[i].equals(haveMebList.getData(j).getString("SHORT_CODE"))){
		        			isHave=true;
		        		}
		        	}
		        	if(!isHave){
		        		MemberShortNoNew=shortCodes[i];
		        		break;
		        	}
		        }
			}
			inputData.put("SHORT_CODE_B",MemberShortNoNew);
			
			
			//查询号码是否有未完工的台账
			IDataset unfinishTrade = TradeInfoQry.getTradeInfoBySn(SERIAL_NUMBER);
			if(IDataUtil.isNotEmpty(unfinishTrade)){
				//returnData.put("X_RESULTCODE", "9999");
				//returnData.put("X_RESULTINFO", "该用户有未完工的台账");
				return SelfTerminalUtil.responseFail("该用户有未完工的台账",null);
			}
			//查询副卡是否有未完工的台账
			IDataset unfinishVice = TradeInfoQry.getTradeInfoBySn(SERIAL_NUMBER_B);
			if(IDataUtil.isNotEmpty(unfinishVice)){
				//returnData.put("X_RESULTCODE", "9996");
				//returnData.put("X_RESULTINFO", "该副卡有未完工的台账");
				return SelfTerminalUtil.responseFail("该副卡有未完工的台账",null);
			}
			
			//先查看该号码是否开通了家庭网
			IDataset familyNet = checkFamily(USER_ID);
			if(IDataUtil.isEmpty(familyNet)){
				//returnData.put("X_RESULTCODE", "2001");
				//returnData.put("X_RESULTINFO", "订购关系不存在（或已销户）");
				return SelfTerminalUtil.responseFail("该号码未开通家庭网",null);
			}
			if(!"0".equals(user.getString("USER_STATE_CODESET"))){
				//returnData.put("X_RESULTCODE", "2005");
				//returnData.put("X_RESULTINFO", "该用户已经停机");
				return SelfTerminalUtil.responseFail("该用户已经停机",null);
			}
			
			
			String user_id_a = familyNet.getData(0).getString("USER_ID_A");
			//通过副卡手机号搜副卡信息
			IData vice = UcaInfoQry.qryUserInfoBySn(SERIAL_NUMBER_B);
			String VICE_ID = vice.getString("USER_ID");
			//检查副卡是否开通了家庭网
			IDataset viceNet = checkFamily(VICE_ID);
			if(IDataUtil.isNotEmpty(viceNet)){
				
				if(!user_id_a.equals(viceNet.getData(0).getString("USER_ID_A"))){
					//returnData.put("X_RESULTCODE", "3004");
					//returnData.put("X_RESULTINFO", "存在互斥关系，不允许用户订购");
					return SelfTerminalUtil.responseFail("存在互斥关系，不允许用户订购",null);
				}
				//returnData.put("X_RESULTCODE", "2000");
				//returnData.put("X_RESULTINFO", "该副卡已开通家庭网");
				return SelfTerminalUtil.responseFail("该副卡已开通家庭网",null);
			}
			
			if(!"0".equals(vice.getString("USER_STATE_CODESET"))){
				//returnData.put("X_RESULTCODE", "2005");
				//returnData.put("X_RESULTINFO", "该用户已经停机");
				return SelfTerminalUtil.responseFail("该副卡已经停机",null);
			}
			
			//黑名单查询
			if(checkBlack(SERIAL_NUMBER)){
				//returnData.put("X_RESULTCODE", "8993");
				//returnData.put("X_RESULTINFO", "该卡为黑名单用户");
				return SelfTerminalUtil.responseFail("该卡为黑名单用户",null);
			}
			
			if(checkBlack(SERIAL_NUMBER_B)){
				//returnData.put("X_RESULTCODE", "8993");
				//returnData.put("X_RESULTINFO", "该卡为黑名单用户");
				return SelfTerminalUtil.responseFail("该副卡为黑名单用户",null);
			}
			
			//查询主卡优惠
			String product_id = inputData.getString("PRODUCT_ID");
			boolean flag = true;
			IDataset discntInfos = CommparaInfoQry.getCommNetInfo("CSM", "5", "VPCN");
			IData discnt_real = new DataMap();
			for(Object discntInfo : discntInfos){
				IData realInfo = (IData) discntInfo;
				if(product_id.equals(realInfo.getString("PARA_CODE1"))){
					flag = false;
					discnt_real = realInfo;
					product_id = realInfo.getString("PARA_CODE1");
					break;
				}
			}
			if(flag){
				//returnData.put("X_RESULTCODE", "9997");
				//returnData.put("X_RESULTINFO", "家庭网产品有误");
				return SelfTerminalUtil.responseFail("家庭网产品有误",null);
			}
			//主卡优惠
			String discnt_code = discnt_real.getString("PARA_CODE2");
			String discnt_code_b = discnt_real.getString("PARA_CODE3");
			// 处理主号是新产品还是老产品
			UcaData mebUca = UcaDataFactory.getNormalUca(serialNumber);
			boolean isOld=false;
			List<DiscntTradeData> userDiscntList = mebUca.getUserDiscnts();
			for (int j = 0; j < userDiscntList.size(); j++) {
				DiscntTradeData userDiscnt = userDiscntList.get(j);
				if ("3403".equals(userDiscnt.getDiscntCode())) {
					isOld = true;
					break;
				}
			}
			if(!isOld){
				discnt_code = discnt_real.getString("PARA_CODE6");
				discnt_code_b = discnt_real.getString("PARA_CODE7");
			}
			
			//检验短号的合法性
			String shortCode = inputData.getString("SHORT_CODE_B");
			IDataset dataset = new DatasetList();
	        dataset = CommparaInfoQry.getCommpara("CSM", "112", "QQWLIMIT","ZZZZ");  //根据套餐代码查询本省套餐
			if(IDataUtil.isEmpty(dataset)){
		        Pattern p = Pattern.compile("52\\d|53\\d");
		        Matcher m = p.matcher(shortCode);
		        boolean b = m.matches();
		        if (!b)
		        {
		            // 短号非法,短号必须为【520-539】
		            //CSAppException.apperr(FamilyException.CRM_FAMILY_716,shortCode);
		        	return SelfTerminalUtil.responseFail("短号"+shortCode+"非法,短号必须为【520-539】",null);
		        }	
			}else{
		        Pattern p = Pattern.compile("52\\d");
		        Matcher m = p.matcher(shortCode);
		        boolean b = m.matches();
		        if (!b)
		        {
		            // 短号非法,短号必须为【520-529】
		            //CSAppException.apperr(FamilyException.CRM_FAMILY_833,shortCode);
		        	return SelfTerminalUtil.responseFail("短号"+shortCode+"非法,短号必须为【520-529】",null);
		        }	
			}
			//副卡数量
			IDataset members = countMembers(user_id_a);
			int size = members.size();
			//准备数据
			String start_date = familyNet.getData(0).getString("START_DATE");
			String end_date = familyNet.getData(0).getString("END_DATE");
			
			inputData.put("VALID_MEMBER_NUMBER", size);
			inputData.put("FMY_DISCNT_CODE", discnt_code);
			inputData.put("DISCNT_CODE", discnt_code);
			inputData.put("SERIAL_NUMBER", SERIAL_NUMBER);
			inputData.put("FMY_VERIFY_MODE", "1");
			inputData.put("VERIFY_MODE", "1");
			inputData.put("START_DATE", start_date);
			inputData.put("END_DATE", end_date);
			inputData.put("FMY_START_DATE", start_date);
			inputData.put("FMY_END_DATE", end_date);
			inputData.put("PRODUCT_ID", product_id);
			inputData.put("FMY_PRODUCT_ID", product_id);
			inputData.put("CHECK_MODE", input.getString("CHECK_MODE","Z"));
			inputData.put("ADD_MEMBER_NUMBER", input.getString("ADD_MEMBER_NUMBER","1"));
			
			IDataset vices = new DatasetList();
			IData viceInfo = new DataMap();
			viceInfo.put("SERIAL_NUMBER_B", SERIAL_NUMBER_B);
			viceInfo.put("DISCNT_CODE_B", discnt_code_b);
			viceInfo.put("DISCNT_NAME_B", DiscntInfoQry.getDiscntInfoByDisCode(discnt_code_b).getData(0).getString("DISCNT_NAME"));
			viceInfo.put("SHORT_CODE_B", inputData.getString("SHORT_CODE_B"));
			viceInfo.put("START_DATE", "立即");
			viceInfo.put("END_DATE", end_date);
//			viceInfo.put("MEMBER_ROLE_B", "10");
//			viceInfo.put("MEMBER_KIND_B", "0");
			viceInfo.put("tag", "0");
			vices.add(viceInfo);
			
			inputData.put("MEB_LIST", vices);
			//数据准备完就可以走台账了
			IDataset result = CSAppCall.call("SS.FamilyCreateRegSVC.tradeReg", inputData);
			if(IDataUtil.isEmpty(result)){
				//returnData.put("X_RESULTCODE", "9996");
				//returnData.put("X_RESULTINFO", "办理业务有误");
				return SelfTerminalUtil.responseFail("办理业务有误",null);
			}
			
			//记录日志
			IData logParam=new DataMap();
			logParam.put("TRANS_ACTION_ID", input.getString("transactionId"));
			logParam.put("CRM_ORDER_ID", result.getData(0).getString("ORDER_ID"));
			logParam.put("CRM_TRADE_ID", result.getData(0).getString("TRADE_ID"));
			logParam.put("OP_ID", getVisit().getStaffId());
			logParam.put("OP_ORG", getVisit().getDepartId());
			logParam.put("TRADE_TYPE_CODE", "283");
			logParam.put("SERIAL_NUMBER", serialNumber);
			SelfTerminalBean selfBean=BeanManager.createBean(SelfTerminalBean.class);
			selfBean.addSelfLog(logParam);
			
			return SelfTerminalUtil.responseSuccess(output);
			
			
		}else if("02".equals(oprCode)){//删除成员
			IData inputData=new DataMap();
			inputData.put("SERIAL_NUMBER",serialNumber);
			
			try{
				IDataUtil.chkParam(input, "memberPhoneNo");
			}catch(Exception e){
				return SelfTerminalUtil.responseFail(e.getMessage(),null);
			}
			
			//删除副卡
			inputData.put("SERIAL_NUMBER_B", input.getString("memberPhoneNo"));
			
			//获取主卡的三户资料
			String serial_number = inputData.getString("SERIAL_NUMBER");
			IData mainCard = UcaInfoQry.qryUserInfoBySn(serial_number);
			String user_id = mainCard.getString("USER_ID");
			//获取副卡的三户资料
			String serial_number_b = inputData.getString("SERIAL_NUMBER_B");
			IData viceCard = UcaInfoQry.qryUserInfoBySn(serial_number_b);
			String user_id_b = viceCard.getString("USER_ID");
			//判断主副卡是否是黑名单用户
			if(checkBlack(serial_number)){
				return SelfTerminalUtil.responseFail("主卡为黑名单用户",null);
			}
			
			if(checkBlack(serial_number_b)){
				return SelfTerminalUtil.responseFail("副卡为黑名单用户",null);
			}
			
			if(!"0".equals(mainCard.getString("USER_STATE_CODESET"))){
				//returnData.put("X_RESULTCODE", "2005");
				//returnData.put("X_RESULTINFO", "该用户已经停机");
				return SelfTerminalUtil.responseFail("该用户已经停机",null);
			}
			
			//查询号码是否有未完工的台账
			IDataset unfinishTrade = TradeInfoQry.getTradeInfoBySn(serial_number);
			if(IDataUtil.isNotEmpty(unfinishTrade)){
				//returnData.put("X_RESULTCODE", "9999");
				//returnData.put("X_RESULTINFO", "该用户有未完工的台账");
				return SelfTerminalUtil.responseFail("该用户有未完工的台账",null);
			}
			//查询副卡是否有未完工的台账
			IDataset unfinishVice = TradeInfoQry.getTradeInfoBySn(serial_number_b);
			if(IDataUtil.isNotEmpty(unfinishVice)){
				//returnData.put("X_RESULTCODE", "9996");
				//returnData.put("X_RESULTINFO", "该副卡有未完工的台账");
				return SelfTerminalUtil.responseFail("该副卡有未完工的台账",null);
			}
			//主副卡家庭网校验
			IDataset mainFamilys = checkFamily(user_id);
			IDataset viceFamilys = checkFamily(user_id_b);
			if(IDataUtil.isEmpty(mainFamilys)){
				//returnData.put("X_RESULTCODE", "2001");
				//returnData.put("X_RESULTINFO", "订购关系不存在（或已销户）");
				return SelfTerminalUtil.responseFail("不存在家庭网",null);
			}
			if(IDataUtil.isEmpty(viceFamilys)){
				//returnData.put("X_RESULTCODE", "2001");
				//returnData.put("X_RESULTINFO", "订购关系不存在（或已销户）");
				return SelfTerminalUtil.responseFail("副号不存在家庭网",null);
			}
			IData mainFamily = mainFamilys.getData(0);
			IData viceFamily = viceFamilys.getData(0);
			if(!mainFamily.getString("USER_ID_A").equals(viceFamily.getString("USER_ID_A"))){
				//returnData.put("X_RESULTCODE", "9993");
				//returnData.put("X_RESULTINFO", "这两张卡不在同一个家庭网中");
				return SelfTerminalUtil.responseFail("这两张卡不在同一个家庭网中",null);
			}
			//获得副卡短号
			String short_code_b = viceFamily.getString("SHORT_CODE");
			String start_date = viceFamily.getString("START_DATE");
			String endDate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
			String end_date = endDate.substring(0, 4)+"-"+endDate.substring(4, 6)+"-"+endDate.substring(6, 8)+" "+endDate.substring(8, 10)+":"+endDate.substring(10, 12)+":"+endDate.substring(12);
			String last_date = SysDateMgr.getLastDateThisMonth();
			//获取副卡优惠
			IDataset discnt_vice = CommparaInfoQry.getCommNetInfo("CSM", "5", "VPCN");
			String discnt_b = discnt_vice.getData(0).getString("PARA_CODE3");
			// 处理主号是新产品还是老产品
			UcaData mebUca = UcaDataFactory.getNormalUca(serialNumber);
			boolean isOld=false;
			List<DiscntTradeData> userDiscntList = mebUca.getUserDiscnts();
			for (int j = 0; j < userDiscntList.size(); j++) {
				DiscntTradeData userDiscnt = userDiscntList.get(j);
				if ("3403".equals(userDiscnt.getDiscntCode())) {
					isOld = true;
					break;
				}
			}
			if(!isOld){
				discnt_b = discnt_vice.getData(0).getString("PARA_CODE7");
			}
			
			String product_id = discnt_vice.getData(0).getString("PARA_CODE1");
			
			//根据优惠id搜产品信息
			String offer_name = DiscntInfoQry.getDiscntInfoByDisCode(discnt_b).getData(0).getString("DISCNT_NAME");
			//获取inst_id*
			IData inputInfo = new DataMap();
			inputInfo.put("SERIAL_NUMBER", serial_number);
			FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
			IData rsd = bean.getViceMebList(inputInfo);
			IDataset members = rsd.getDataset("MEB_LIST");
			String inst_id = null;
			for(Object member : members){
				IData mem = (IData)member;
				if(serial_number_b.equals(mem.getString("SERIAL_NUMBER_B"))){
					inst_id = mem.getString("U_INST_ID");
					break;
				}
			}
			if(StringUtils.isBlank(inst_id)){
				//returnData.put("X_RESULTCODE", "9992");
				//returnData.put("X_RESULTINFO", "信息有误");
				return SelfTerminalUtil.responseFail("信息有误",null);
			}
			//获取家庭网中多少号码
			String user_id_a = mainFamily.getString("USER_ID_A");
			int num = countMembers(user_id_a).size();
			//准备参数
			inputData.put("CHECK_MODE", input.getString("CHECK_MODE","Z"));
			inputData.put("DELETE_MEMBER_NUMBER", "1");
			inputData.put("SUBMIT_SOURCE", "CRM_PAGE");
			inputData.put("VALID_MEMBER_NUMBER", num+"");
			IDataset memberInfo = new DatasetList();
			IData memInfo = new DataMap();
			memInfo.put("INST_ID_B", inst_id);
			memInfo.put("SERIAL_NUMBER_B", serial_number_b);
			memInfo.put("DISCNT_CODE_B", discnt_b);
			memInfo.put("DISCNT_NAME_B", offer_name);
			memInfo.put("SHORT_CODE_B", short_code_b);
			memInfo.put("START_DATE", start_date);
			memInfo.put("END_DATE", end_date);
			memInfo.put("LAST_DAY_THIS_ACCT", last_date);
			memInfo.put("EFFECT_NOW", "YES");
			memInfo.put("tag", "2");
			memberInfo.add(memInfo);
			inputData.put("MEB_LIST", memberInfo);
			inputData.put("PRODUCT_ID", product_id);
			
			//台账系统
			IDataset result = CSAppCall.call("SS.DelFamilyNetMemberRegSVC.tradeReg", inputData);
			if(IDataUtil.isEmpty(result)){
				//returnData.put("X_RESULTCODE", "9996");
				//returnData.put("X_RESULTINFO", "办理业务有误");
				return SelfTerminalUtil.responseFail("办理业务有误",null);
			}
			
			//记录日志
			IData logParam=new DataMap();
			logParam.put("TRANS_ACTION_ID", input.getString("transactionId"));
			logParam.put("CRM_ORDER_ID", result.getData(0).getString("ORDER_ID"));
			logParam.put("CRM_TRADE_ID", result.getData(0).getString("TRADE_ID"));
			logParam.put("OP_ID", getVisit().getStaffId());
			logParam.put("OP_ORG", getVisit().getDepartId());
			logParam.put("TRADE_TYPE_CODE", "284");
			logParam.put("SERIAL_NUMBER", serialNumber);
			SelfTerminalBean selfBean=BeanManager.createBean(SelfTerminalBean.class);
			selfBean.addSelfLog(logParam);
			
			return SelfTerminalUtil.responseSuccess(output);
		}else{
			return SelfTerminalUtil.responseFail("无此操作 oprCode["+oprCode+"]", null);
		}
	}
	//家庭网查询
	private IDataset checkFamily(String userId) throws Exception{
		IData checkFamily = new DataMap();
		checkFamily.put("USER_ID_B", userId);
		checkFamily.put("RELATION_TYPE_CODE", "45");
		return FamilyOperPreBean.getUserRelationByUserIdB(checkFamily);
	}
	//家庭网查询
	private IDataset countMembers(String userId) throws Exception{
		IData checkFamily = new DataMap();
		checkFamily.put("USER_ID_A", userId);
		checkFamily.put("RELATION_TYPE_CODE", "45");
		return FamilyOperPreBean.getAllRoleB(checkFamily);
	}
	//黑名单查询
	private boolean checkBlack(String serial_number) throws Exception{
		
		String psptTypeCode= "";
        String psptId = "";
        IData info = new DataMap();
        info.put("SERIAL_NUMBER", serial_number);
    	IDataset custInfo = CustomerInfoQry.getCustInfoByAllSn(info, null);
    	if(IDataUtil.isNotEmpty(custInfo))
    	{
    		psptTypeCode = custInfo.getData(0).getString("PSPT_TYPE_CODE");
    		psptId = custInfo.getData(0).getString("PSPT_ID");
    	}
    	if (UCustBlackInfoQry.isBlackCust(psptTypeCode, psptId))
        {
			return true;
        }else{
        	return false;
        }
	}
	/**
	 * 平台业务办理接口
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData openAndClosePlatSvc(IData inParam) throws Exception { 
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		output.put("oprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceType");
			IDataUtil.chkParam(input, "serviceNum");
			IDataUtil.chkParam(input, "oprNumb");
			IDataUtil.chkParam(input, "bizType");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		IDataset productInfo=input.getDataset("productInfo");
		if(IDataUtil.isEmpty(productInfo)){
			return SelfTerminalUtil.responseFail("产品列表不能为空",null);
		}
		
		String serviceType=input.getString("serviceType");
		String serviceNum=input.getString("serviceNum");
		String oprNumb=input.getString("oprNumb");
		String bizType=input.getString("bizType");
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serviceNum);
		if(IDataUtil.isEmpty(userInfo)){
			return SelfTerminalUtil.responseFail("用户资料不存在",null);
		}
		if(!("01".equals(serviceType))){
			return SelfTerminalUtil.responseFail("不支持的serviceType="+serviceType,null);
		}
		//参数校验
		for(int i=0;i<productInfo.size();i++){
			String productType=productInfo.getData(i).getString("productType");
			String oprCode=productInfo.getData(i).getString("oprCode");
			String productId=productInfo.getData(i).getString("productId");
			String spId=productInfo.getData(i).getString("spId");
			String bizCode=productInfo.getData(i).getString("bizCode");
			if(StringUtils.isEmpty(oprCode)){
				return SelfTerminalUtil.responseFail("oprCode不能为空",null);
			}
			if(StringUtils.isEmpty(productType)){
				return SelfTerminalUtil.responseFail("productType不能为空",null);
			}
			if("01".equals(productType)&&StringUtils.isEmpty(productId)){
				return SelfTerminalUtil.responseFail("productId不能为空",null);
			}
			if("02".equals(productType)){
				if(StringUtils.isEmpty(spId)){
					return SelfTerminalUtil.responseFail("spId不能为空",null);
				}
				if(StringUtils.isEmpty(bizCode)){
					return SelfTerminalUtil.responseFail("bizCode不能为空",null);
				}
				if (bizCode.indexOf("|") == -1){
					return SelfTerminalUtil.responseFail("BIZ_CODE格式错误，应该是BIZ_CODE|BIZ_TYPE_CODE格式",null);
				}
			}
		}
		IDataset logList=new DatasetList();
		//执行结果
		IDataset prodOrderRec = new DatasetList();
		for(int i=0;i<productInfo.size();i++){
			IData inData=new DataMap();
			inData.put("SERIAL_NUMBER", serviceNum);
			inData.put("OPR_NUMB", oprNumb);
			inData.put("OPR_CODE", productInfo.getData(i).getString("oprCode"));
			inData.put("PRODUCT_TYPE", productInfo.getData(i).getString("productType"));
			inData.put("EFFT_WAY", productInfo.getData(i).getString("effectiveType"));
			inData.put("PRODUCT_ID", productInfo.getData(i).getString("productId"));
			inData.put("SP_ID", productInfo.getData(i).getString("spId"));
			inData.put("BIZ_CODE", productInfo.getData(i).getString("bizCode"));
			inData.put("BIZ_TYPE", bizType);
			
			//开通
			IDataset result=new DatasetList();
			if("01".equals(productInfo.getData(i).getString("oprCode"))){
				result=this.openPlatSvc(inData);
			//取消	
			}else{
				result=this.closePlatSvc(inData);
			}
			//设置返回值
			if(IDataUtil.isNotEmpty(result)){
				IData logData=new DataMap();
				logData.putAll(result.getData(0));
				logList.add(logData);
				
				result.getData(0).put("effectTime", SysDateMgr.decodeTimestamp(result.getData(0).getString("EFFECT_TIME",SysDateMgr.getSysDate()), SysDateMgr.PATTERN_STAND_SHORT));
				result.getData(0).put("productId", productInfo.getData(i).getString("productId",""));
				result.getData(0).put("spId", productInfo.getData(i).getString("spId",""));
				result.getData(0).put("bizCode", productInfo.getData(i).getString("bizCode",""));
				result.getData(0).remove("EFFECT_TIME");
				result.getData(0).remove("DB_SOURCE");
				result.getData(0).remove("ORDER_ID");
				result.getData(0).remove("TRADE_ID");
				result.getData(0).remove("USER_ID");
				result.getData(0).remove("ORDER_TYPE_CODE");
				prodOrderRec.add(result.getData(0));
			}
		}
		
		//记录日志
		if(IDataUtil.isNotEmpty(logList)){
			IData logParam=new DataMap();
			logParam.put("TRANS_ACTION_ID", input.getString("transactionId"));
			logParam.put("CRM_ORDER_ID", logList.getData(0).getString("ORDER_ID",""));
			logParam.put("CRM_TRADE_ID", logList.getData(0).getString("TRADE_ID",""));
			logParam.put("OP_ID", getVisit().getStaffId());
			logParam.put("OP_ORG", getVisit().getDepartId());
			logParam.put("TRADE_TYPE_CODE", logList.getData(0).getString("ORDER_TYPE_CODE",""));
			logParam.put("SERIAL_NUMBER", input.getString("serviceNum"));
			SelfTerminalBean selfBean=BeanManager.createBean(SelfTerminalBean.class);
			selfBean.addSelfLog(logParam);
		}
		
		output.put("prodOrderRec", prodOrderRec);
		return SelfTerminalUtil.responseSuccess(output);
	}
	/**
	 * 开通平台业务
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private IDataset openPlatSvc(IData data) throws Exception{
        String serialNumber = data.getString("SERIAL_NUMBER");
        String oprNumb = data.getString("OPR_NUMB");
        String operCode = data.getString("OPR_CODE");
        String productType = data.getString("PRODUCT_TYPE");
        String efftWay = data.getString("EFFT_WAY");
        String ibossProductId = data.getString("PRODUCT_ID");
        String spCode = data.getString("SP_ID");
        String bizCode4IBoss = data.getString("BIZ_CODE");
        String oprSource = data.getString("BIZ_TYPE","");
        
        //开始受理
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        
        //针对产品
        if ("01".equals(productType)){
        	
        	String productId = "";
            String elementTypeCode = "";
            String platDiscnt = "";
            String bizCodeStr = "";
            String spCodeStr = "";
            
            IDataset configs = CommparaInfoQry.getCommpara("CSM", "2788", ibossProductId, BizRoute.getRouteId());
            if (IDataUtil.isNotEmpty(configs)){
            	IData config = configs.getData(0);
                productId = config.getString("PARA_CODE1");
                elementTypeCode = config.getString("PARA_CODE2");

                platDiscnt = config.getString("PARA_CODE5", "");
                bizCodeStr = config.getString("PARA_CODE6", "");
                spCodeStr = config.getString("PARA_CODE7", "");
            }else{
            	 char head = ibossProductId.charAt(0);
                 if (('P' == head) || ('D' == head) || ('S' == head)){
                     productId = ibossProductId.substring(1);
                     elementTypeCode = String.valueOf(head);
                 }else{
                     CSAppException.apperr(ProductException.CRM_PRODUCT_240, ibossProductId);
                 }
            }
            
            if (!"".equals(platDiscnt)){
            	return dealPlatBusiTrade(serialNumber, oprNumb, bizCodeStr, spCodeStr, PlatConstants.OPER_ORDER, oprSource);
            }else{
            	if ("00".equals(efftWay)){
                    param.put("START_DATE", SysDateMgr.getSysDate());// 立即
                    param.put("BOOKING_TAG", "0");// 非预约
                }else if ("01".equals(efftWay)){
                    param.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth()); // 下月
                    param.put("BOOKING_TAG", "1");// 非预约
                }else if ("02".equals(efftWay)){
                    param.put("START_DATE", SysDateMgr.getTomorrowDate());// 次日
                    param.put("BOOKING_TAG", "1");// 非预约
                }else{
                    param.put("START_DATE", SysDateMgr.getSysDate());// 默认立即生效
                    param.put("BOOKING_TAG", "0");// 非预约
                }

                param.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                param.put("ELEMENT_TYPE_CODE", elementTypeCode);
                param.put("ELEMENT_ID", productId);
                
                //渠道标识
                param.put("RSRV_STR8", oprSource);
                //调用产品变更总接口
                IDataset result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param);
                //增加生效时间
                result.getData(0).put("EFFECT_TIME", param.getString("START_DATE"));
                return result;
            }
        
        //针对平台业务	
        }else if ("02".equals(productType)){
        	return this.dealPlatBusiTrade(serialNumber, oprNumb, bizCode4IBoss, spCode, PlatConstants.OPER_ORDER, oprSource);
        }else{
            return new DatasetList();
        }
        
	}
	/**
	 * 取消平台业务
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private IDataset closePlatSvc(IData data) throws Exception{
        String serialNumber = data.getString("SERIAL_NUMBER");
        String oprNumb = data.getString("OPR_NUMB");
        String operCode = data.getString("OPR_CODE");
        String productType = data.getString("PRODUCT_TYPE");
        String efftWay = data.getString("EFFT_WAY");
        String ibossProductId = data.getString("PRODUCT_ID");
        String spCode = data.getString("SP_ID");
        String bizCode4IBoss = data.getString("BIZ_CODE");
        String oprSource = data.getString("BIZ_TYPE","");
        
        String [] productInfos=ibossProductId.split("_");	
		String appId="";
		String isVideoPck="N";
		if(productInfos.length>0&&productInfos.length==2){//视频流量包
			ibossProductId=productInfos[0];
		    appId=productInfos[1];
		    isVideoPck="Y";			    
		}
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if(IDataUtil.isEmpty(userInfo)){
			CSAppException.apperr(CrmUserException.CRM_USER_117,serialNumber);
		}
			
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		//操作产品
		if ("01".equals(productType)){ 
			String productId = "";
            String elementTypeCode = "";
            String platDiscnt = "";
            String bizCodeStr = "";
            String spCodeStr = "";
            IDataset configs = CommparaInfoQry.getCommpara("CSM", "2788", ibossProductId, BizRoute.getRouteId());
            if (IDataUtil.isNotEmpty(configs)){
                IData config = configs.getData(0);
                productId = config.getString("PARA_CODE1");
                elementTypeCode = config.getString("PARA_CODE2");

                platDiscnt = config.getString("PARA_CODE5", "");
                bizCodeStr = config.getString("PARA_CODE6", "");
                spCodeStr = config.getString("PARA_CODE7", "");
            }else{
                char head = ibossProductId.charAt(0);
                if (('P' == head) || ('D' == head) || ('S' == head)){
                    productId = ibossProductId.substring(1);
                    elementTypeCode = String.valueOf(head);
                }else{
                    CSAppException.apperr(ProductException.CRM_PRODUCT_240, ibossProductId);
                }
            }
            if (StringUtils.isNotBlank(platDiscnt)){
                return dealPlatBusiTrade(serialNumber, oprNumb, bizCodeStr, spCodeStr, PlatConstants.OPER_CANCEL_ORDER, oprSource);
            }else{
            	param.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                param.put("ELEMENT_TYPE_CODE", elementTypeCode);
                param.put("ELEMENT_ID", productId);
                param.put("BOOKING_TAG", "0");// 非预约
                if("Y".equals(isVideoPck)){//如果是视频流量包则做特殊处理                                          
                    IData retData=AbilityRuleCheck.checkAppState(userInfo.getString("USER_ID"),productId,appId,userInfo.getString("EPARCHY_CODE"));
                    if(IDataUtil.isNotEmpty(retData)){
                    	param.put("IS_VIDEOPCK", isVideoPck);
                    	IData info=new DataMap(); 
                    	IDataset attrInfos=new DatasetList();
                    	info.put("ATTR_CODE", retData.getString("ATTR_CODE"));
                    	info.put("ATTR_VALUE", retData.getString("ATTR_VALUE"));//给要退订的app赋特殊值                   	
                    if("N".equals(retData.getString("IS_LAST_APP"))){//不是删除最后一个app,则走变更属性,修改
                        param.put("MODIFY_TAG", BofConst.MODIFY_TAG_UPD); 
                        info.put("ATTR_CODE", retData.getString("ATTR_CODE"));
                    	info.put("ATTR_VALUE", "-1");//给要退订的app赋特殊值                      
                    }
                    attrInfos.add(info);
                    param.put("ATTR_PARAM", attrInfos);
                    }
                }
                //渠道标识
                param.put("RSRV_STR8", oprSource);
                /************end******************/
                // 调用产品变更总接口
                IDataset result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param);
                // 设置失效时间
                String userProductId = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber).getString("PRODUCT_ID", "");
                IData elementInfo = ProductInfoQry.getElementByProductIdElemId(userProductId, productId).getData(0);
                if (null != elementInfo){
                    IData returnData = result.getData(0);
                    String cancelTag = elementInfo.getString("CANCEL_TAG");
                    if ("0".equals(cancelTag)){ // 立即取消
                        returnData.put("EFFECT_TIME", SysDateMgr.getSysDate());
                    }else if ("1".equals(cancelTag)){ // 昨天取消
                        String curDate = SysDateMgr.addDays(-1);
                        returnData.put("EFFECT_TIME", curDate + SysDateMgr.END_DATE);
                    }else if ("2".equals(cancelTag)){// 今天取消
                        String curDate = SysDateMgr.addDays(0);
                        returnData.put("EFFECT_TIME", curDate + SysDateMgr.END_DATE);
                    }else if ("3".equals(cancelTag)){ // 本账期末取消，月底取消
                        returnData.put("EFFECT_TIME", SysDateMgr.getLastDateThisMonth());
                    }else if ("4".equals(cancelTag)){ // 未到元素结束日期不能取消
                        returnData.put("EFFECT_TIME", SysDateMgr.getSysDate());
                    }else if ("5".equals(cancelTag)){
                    	returnData.put("EFFECT_TIME", SysDateMgr.getSysDate());
                    } else if ("6".equals(cancelTag)){
                    	returnData.put("EFFECT_TIME", SysDateMgr.getAddMonthsLastDay(-1,SysDateMgr.getSysDate()));
                    }
                }
                return result;
            }
		//平台业务	
		}else if ("02".equals(productType)){ 
			 return this.dealPlatBusiTrade(serialNumber, oprNumb, bizCode4IBoss, spCode, PlatConstants.OPER_CANCEL_ORDER, oprSource);
		}else{
			return new DatasetList();
		}
	}
	/**
	 * 处理IBOSS过来平台业务
	 * @param serialNumber
	 * @param oprNumb
	 * @param bizCode4IBoss
	 * @param spCode
	 * @param operCode
	 * @param oprSource
	 * @return
	 * @throws Exception
	 */
	private IDataset dealPlatBusiTrade(String serialNumber, String oprNumb, String bizCode4IBoss, String spCode, String operCode, String oprSource) throws Exception{
		/*if (bizCode4IBoss.indexOf("|") == -1){
            CSAppException.apperr(ParamException.CRM_PARAM_442);
        }*/
		String bizCode = bizCode4IBoss.split("\\|")[0];
        String bizTypeCode = bizCode4IBoss.split("\\|")[1];
        
        IData platParam = new DataMap();

        platParam.put("SERIAL_NUMBER", serialNumber);
        platParam.put("TRANS_ID", oprNumb);
        platParam.put("BIZ_CODE", bizCode);
        platParam.put("BIZ_TYPE_CODE", bizTypeCode);
        platParam.put("SP_CODE", spCode);
        platParam.put("OPER_CODE", operCode);
        String changeOprSoure = CustServiceHelper.getCustomerServiceChannel(oprSource);                
        if(changeOprSoure!=null&&changeOprSoure.trim().length()>0){
            oprSource = changeOprSoure;
        }
        
        platParam.put("OPR_SOURCE", oprSource);
        platParam.put("IS_NEED_PF", "1");//移动商城业务默认走服开
		
        //渠道标识
        platParam.put("RSRV_STR8", oprSource);
        /********************end*****************************/
        
        IDataset result = CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", platParam);
        //增加生效时间
        result.getData(0).put("EFFECT_TIME", SysDateMgr.getSysDate());
        return result;
	}
	/**
	 * 补换卡预校验接口
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData checkChangeCard(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		if(StringUtils.isEmpty(input.getString("replaceType"))){
			input.put("replaceType","00");
		}
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceNum");
			IDataUtil.chkParam(input, "replaceType");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		//本地补换卡校验
		if("00".equals(input.getString("replaceType"))){
			String serviceNum=input.getString("serviceNum");
			
			IData userInfo = UcaInfoQry.qryUserInfoBySn(serviceNum);
			if(IDataUtil.isEmpty(userInfo)){
				return SelfTerminalUtil.responseFail("用户资料不存在",null);
			}
			
			//单位证件不允许办理补换卡
			IData custInfo=UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID"));
			if(IDataUtil.isEmpty(custInfo)){
				return SelfTerminalUtil.responseFail("客户资料不存在",null);
			}
			IDataset cardTypeList=CommparaInfoQry.getCommParas("CSM", "7979", "PRECHECK_CARDTYPE", custInfo.getString("PSPT_TYPE_CODE"), "0898");
			if(IDataUtil.isNotEmpty(cardTypeList)){
				return SelfTerminalUtil.responseFail("单位用户不允许办理补换卡",null);
			}
			
			IData params=new DataMap();
			params.put("SERIAL_NUMBER", serviceNum);
			params.put("X_CHOICE_TAG", "0");
			params.put("TRADE_TYPE_CODE", "142");
			params.put("ROUTE_EPARCHY_CODE", "0898");
			params.put("EPARCHY_CODE", "0898");
			
			IDataset results=CSAppCall.call("SS.CreatePersonIntfSVC.checkBusinessRule", params);
			IDataset tipInfo=results.getData(0).getDataset("TIPS_TYPE_ERROR");
			if(IDataUtil.isEmpty(tipInfo)){
				return SelfTerminalUtil.responseSuccess(new DataMap());
			}else{
				return SelfTerminalUtil.responseFail(tipInfo.getData(0).getString("TIPS_INFO"), null);
			}
		//异地补换卡校验	
		}else{
			//非空判断
			try{
				IDataUtil.chkParam(input, "idCardType");
				IDataUtil.chkParam(input, "idCardNum");
				IDataUtil.chkParam(input, "serviceCode");
			}catch(Exception e){
				return SelfTerminalUtil.responseFail(e.getMessage(),null);
			}
			
			String serviceNum=input.getString("serviceNum");
			String idCardType=input.getString("idCardType");
			String idCardNum=input.getString("idCardNum");
			String serviceCode=input.getString("serviceCode");
			
			IData params=new DataMap();
			params.put("BIZ_TYPE", "1012");
			params.put("ID_VALUE", serviceNum);
			params.put("ID_CARD_NUM", idCardNum);
			params.put("ID_CARD_TYPE", idCardType);
			params.put("CCPASSWD", serviceCode);
			params.put("ID_TYPE", "0");
			params.put("OPR_NUMB", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			params.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			params.put("BIZ_VERSION", "1");
			
			IDataset results=CSAppCall.call("SS.RemoteResetPswdSVC.userAuth", params);
			String resutlCode=results.getData(0).getString("X_RESULTCODE");
			if("0000".equals(resutlCode)){
				return SelfTerminalUtil.responseSuccess(new DataMap());
			}else{
				return SelfTerminalUtil.responseFail(results.getData(0).getString("X_RESULTINFO"), null);
			}
		}
	}
	/**
	 * 号码预占接口
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData preOccupySvcNum(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceNumber");
			IDataUtil.chkParam(input, "idCardType");
			IDataUtil.chkParam(input, "idCardNum");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		IData params=new DataMap();
		params.put("RES_NO", input.getString("serviceNumber"));//调资源接口需传预占号码
		params.put("RES_TRADE_CODE", "IRes_NetSel_MphoneCode");//普通网上选号 李全修改
		params.put("OCCUPY_TYPE_CODE", "1");//选占类型,1：网上选占
		params.put("RES_TYPE_CODE", "0");//0-号码
		params.put("USER_EPARCHY_CODE", CSBizBean.getUserEparchyCode());
		params.put("ROUTE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		params.put("PSPT_ID", input.getString("idCardNum"));//选占证件号码
		params.put("PSPT_TYPE", "");//选占证件类型，非必传
		params.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 受理地州
		params.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 受理业务区
		params.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 受理部门
		params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 受理员工
		params.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode()); // 接入渠道
		
		try{
			ResCall.resTempOccupyByNetSel(params);
		}catch(Exception e){
			output.put("isSuc", "1");
			return SelfTerminalUtil.responseFail(e.getMessage(),output);
		}
		output.put("isSuc", "0");
		return SelfTerminalUtil.responseSuccess(output);
	}
	/**
	 * 开户受理接口
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData openSvcNum(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		//非空判断
		try{
			IDataUtil.chkParam(input, "msisdn");//已选号码
			IDataUtil.chkParam(input, "iccid");//卡号
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		//套餐信息
		IData goodsInfo=input.getData("goodsInfo");
		if(IDataUtil.isEmpty(goodsInfo)){
			return SelfTerminalUtil.responseFail("goodsInfo不能为空",null);
		}
		
		try{
			IDataUtil.chkParam(goodsInfo, "goodsId");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		IData offer=UpcCall.queryOfferByOfferId("P", goodsInfo.getString("goodsId"));
		if(IDataUtil.isEmpty(offer)){
			return SelfTerminalUtil.responseFail("办理的主套餐不存在productId="+goodsInfo.getString("goodsId"),null);
		}
		
		//支付信息
		IData paymentInfo=input.getData("paymentInfo");
		if(IDataUtil.isEmpty(paymentInfo)){
			return SelfTerminalUtil.responseFail("paymentInfo不能为空",null);
		}
		try{
			IDataUtil.chkParam(paymentInfo, "chargeType");
			IDataUtil.chkParam(paymentInfo, "totalFee");
			IDataUtil.chkParam(paymentInfo, "payment");
			IDataUtil.chkParam(paymentInfo, "paymentOrderId");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		//客户信息
		IData certificateInfo=input.getData("certificateInfo");
		if(IDataUtil.isEmpty(certificateInfo)){
			return SelfTerminalUtil.responseFail("certificateInfo不能为空",null);
		}
		try{
			IDataUtil.chkParam(certificateInfo, "certificateType");
			IDataUtil.chkParam(certificateInfo, "certificateNo");
			IDataUtil.chkParam(certificateInfo, "certificateAddr");
			IDataUtil.chkParam(certificateInfo, "legalName");
			IDataUtil.chkParam(certificateInfo, "gender");
			IDataUtil.chkParam(certificateInfo, "birthday");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		//证件类型转换
        String psptTypeCode="";
        IDataset cardList=CommparaInfoQry.getCommNetInfo("CSM", "3838", "IDCARD");
    	for(int i=0;i<cardList.size();i++){
    		if(certificateInfo.getString("certificateType","").equals(cardList.getData(i).getString("PARA_CODE3"))){
    			psptTypeCode=cardList.getData(i).getString("PARA_CODE2","0");
    			break;
    		}
    	}
    	
    	if(StringUtils.isEmpty(psptTypeCode)){
    		output.put("isSuc", "2");
    		return SelfTerminalUtil.responseFail("证件类型不存在",output);
    	}
		
		 // 先进行sim卡选占
        IData checkSimData = new DataMap();
        checkSimData.put("SIM_CARD_NO", input.getString("iccid"));
        checkSimData.put("SERIAL_NUMBER", input.getString("msisdn"));
        IDataset checkSimResult = CSAppCall.call("SS.CreatePersonUserSVC.checkSimCardNo", checkSimData);
        String SIM_CARD_SALE_MONEY = checkSimResult.getData(0).getString("SIM_CARD_SALE_MONEY", "0");
        String SIM_FEE_TAG = checkSimResult.getData(0).getString("SIM_FEE_TAG", "");
        
    	//组织入参
        IData openParam = new DataMap();
        openParam.put("SIM_CARD_SALE_MONEY", SIM_CARD_SALE_MONEY);//paymentInfo.getString("simPrice","0"));
        openParam.put("SIM_FEE_TAG", SIM_FEE_TAG);
        openParam.put("SERIAL_NUMBER", input.getString("msisdn"));
        openParam.put("SIM_CARD_NO", input.getString("iccid"));
        openParam.put("EMPTY_CARD_ID", input.getString("cardSN",""));
        openParam.put("PSPT_TYPE_CODE", psptTypeCode);
        openParam.put("PSPT_ID", certificateInfo.getString("certificateNo"));
        openParam.put("CUST_NAME", certificateInfo.getString("legalName"));
        openParam.put("PSPT_ADDR",  certificateInfo.getString("certificateAddr"));
        openParam.put("USER_TYPE_CODE", "0");
        openParam.put("PHONE", input.getString("msisdn"));
        openParam.put("PAY_NAME", certificateInfo.getString("legalName"));
        openParam.put("PAY_MODE_CODE", "0");
        openParam.put("ACCT_DAY", "1");
        openParam.put("USER_PASSWD", genNewPasswd());
        openParam.put("PRODUCT_ID", goodsInfo.getString("goodsId"));
        String brandCode = UProductInfoQry.getBrandCodeByProductId(goodsInfo.getString("goodsId"));
        openParam.put("BRAND_CODE", brandCode);
        openParam.put("TRADE_TYPE_CODE", "10");
        openParam.put("ORDER_TYPE_CODE", "10");
        openParam.put("TRADE_DEPART_PASSWD", "0");//孙鑫说没用到，但被校验不能空，所以随便赋值了
        openParam.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        openParam.put("ADVANCE_PAY", "-1");//预存款 2 0  为了避免出现多一条记录的情况，所以赋值为-1
        openParam.put("OPER_FEE", "0");//卡费  0 10
        openParam.put("FOREGIFT", "0");//押金费 1 0
        openParam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
		
        //费用参数
        String payModeCode=paymentInfo.getString("chargeType","0");//付费类型
        IDataset X_TRADE_FEESUB = new DatasetList();
        openParam.put("X_TRADE_FEESUB", X_TRADE_FEESUB);
        int totalFee = 0;
        //预存款项
        IData X_TRADE_FEESUB_ITEM = new DataMap();
        X_TRADE_FEESUB.add(X_TRADE_FEESUB_ITEM);
        X_TRADE_FEESUB_ITEM.put("TRADE_TYPE_CODE", "10");
        X_TRADE_FEESUB_ITEM.put("FEE_TYPE_CODE", "0");
        X_TRADE_FEESUB_ITEM.put("FEE", paymentInfo.getString("prePayment","0"));
        X_TRADE_FEESUB_ITEM.put("OLDFEE", paymentInfo.getString("prePayment","0"));
        X_TRADE_FEESUB_ITEM.put("FEE_MODE", "2");
        totalFee+=Integer.parseInt(paymentInfo.getString("prePayment","0"));
        //营业款项
        String operFee=String.valueOf(Integer.parseInt(paymentInfo.getString("numberPrice","0"))
        		+Integer.parseInt(paymentInfo.getString("simPrice","0"))
        		+Integer.parseInt(paymentInfo.getString("otherFee","0")));
        IData X_TRADE_FEESUB_ITEM1 = new DataMap();
        X_TRADE_FEESUB.add(X_TRADE_FEESUB_ITEM1);
        X_TRADE_FEESUB_ITEM1.put("TRADE_TYPE_CODE", "10");
        X_TRADE_FEESUB_ITEM1.put("FEE_TYPE_CODE", "10");
        X_TRADE_FEESUB_ITEM1.put("FEE", operFee);
        X_TRADE_FEESUB_ITEM1.put("OLDFEE", operFee);
        X_TRADE_FEESUB_ITEM1.put("FEE_MODE", "0");
        totalFee+=Integer.parseInt(openParam.getString("OPER_FEE","0"));
        //总支付费用
        IDataset X_TRADE_PAYMONEY = new DatasetList();
        openParam.put("X_TRADE_PAYMONEY", X_TRADE_PAYMONEY);
        IData X_TRADE_PAYMONEY_ITEM = new DataMap();
        X_TRADE_PAYMONEY.add(X_TRADE_PAYMONEY_ITEM);
        X_TRADE_PAYMONEY_ITEM.put("PAY_MONEY_CODE", payModeCode);
        X_TRADE_PAYMONEY_ITEM.put("MONEY", paymentInfo.getString("totalFee",totalFee+""));
        
        openParam.put("PAY_MONEY_CODE", payModeCode);//付费类型
        
        IDataset results = CSAppCall.call("SS.CreatePersonUserIntfSVC.tradeReg", openParam);
        
        if(IDataUtil.isNotEmpty(results)&&StringUtils.isNotEmpty(results.getData(0).getString("ORDER_ID"))){
        	output.put("isSuc", "1");
        	
        	//记录日志
			IData logParam=new DataMap();
			logParam.put("TRANS_ACTION_ID", input.getString("transactionId"));
			logParam.put("CHANNEL_ID", input.getString("channelId",""));
			logParam.put("ORDER_ID", input.getString("orderId",""));
			logParam.put("OSP_ORDER_ID", input.getString("ospOrderId",""));
			logParam.put("CRM_ORDER_ID", results.getData(0).getString("ORDER_ID"));
			logParam.put("CRM_TRADE_ID", results.getData(0).getString("TRADE_ID"));
			logParam.put("OP_ID", getVisit().getStaffId());
			logParam.put("OP_ORG", getVisit().getDepartId());
			logParam.put("TRADE_TYPE_CODE", "10");
			logParam.put("SERIAL_NUMBER", input.getString("msisdn"));
			logParam.put("ORDER_MONEY", paymentInfo.getString("totalFee","0"));
			logParam.put("PAYMENT_MONEY", paymentInfo.getString("payment","0"));
			SelfTerminalBean selfBean=BeanManager.createBean(SelfTerminalBean.class);
			selfBean.addSelfLog(logParam);
        	
			//记录支付信息
			IData payData=new DataMap();
			payData.put("transactionId", input.getString("transactionId"));
			payData.put("serviceNumber", input.getString("msisdn"));
			payData.put("orderNo", input.getString("orderId"));
			payData.put("accountMoney", paymentInfo.getString("prePayment"));
			payData.put("payMent", paymentInfo.getString("prePayment"));
			payData.put("orderMoney", paymentInfo.getString("totalFee"));
			payData.put("busiType", "10");
			payData.put("payMentType", paymentInfo.getString("chargeType","0"));
			payData.put("paytrans", paymentInfo.getString("paymentOrderId"));
			payData.put("OP_ID", getVisit().getStaffId());
			selfBean.addPayOrder(payData);
			
        	return SelfTerminalUtil.responseSuccess(output); 
        }else{
        	output.put("isSuc", "2");
        	return SelfTerminalUtil.responseFail("开户失败",output);
        }
	}
	/**
	 * 电子发票推送设置接口
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData setEpostInfo(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceNum");
			IDataUtil.chkParam(input, "opType");
			IDataUtil.chkParam(input, "pushType");
			IDataUtil.chkParam(input, "pushAddr");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		String serviceNum=input.getString("serviceNum");
		String opType=input.getString("opType");
		String pushType=input.getString("pushType");
		
		//操作类型转换
		if("01".equals(opType)){//新增
			opType="0";
		}else if("02".equals(opType)){//取消
			opType="2";
		}else if("03".equals(opType)){//修改
			opType="1";
		}else{
			return SelfTerminalUtil.responseFail("不支持的参数类型opType="+opType,null);
		}
		
		//推送类型转换
		if("10".equals(pushType)){//短信推送
			pushType="0";
		}else if("11".equals(pushType)){//邮箱推送
			pushType="1";
		}else{
			return SelfTerminalUtil.responseFail("不支持的参数类型pushType="+pushType,null);
		}
		
		IData params=new DataMap();
		params.put("SERIAL_NUMBER", serviceNum);
		params.put("RECEIVE_NUMBER", serviceNum);
		params.put("POST_ADR", input.getString("pushAddr"));
		params.put("POST_CHANNEL", pushType);
		params.put("POST_DATE", input.getString("pushDate","1"));
		params.put("OPER_CODE", opType);
		
		IDataset results = CSAppCall.call("SS.ModifyEPostInfoSVC.modifyEPost", params);
		if(IDataUtil.isNotEmpty(results)){
			if("0".equals(results.getData(0).getString("RESULT_CODE"))){
				return SelfTerminalUtil.responseSuccess(output);
			}else{
				return SelfTerminalUtil.responseFail(results.getData(0).getString("RESULT_INFO"),null);
			}
		}else{
			return SelfTerminalUtil.responseFail("设置出错",null);
		}
	}
	/**
	 * 通话记录校验接口
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData userAuth(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		//非空判断
		try{
			IDataUtil.chkParam(input, "oprNumb");
			IDataUtil.chkParam(input, "idType");
			IDataUtil.chkParam(input, "idv");
			IDataUtil.chkParam(input, "bizType");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		output.put("idType", input.getString("idType"));
		output.put("idv", input.getString("idv"));
		output.put("oprNumb", input.getString("oprNumb"));
		output.put("oprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		output.put("bizOrderResult", "0000");
		//鉴权结果
		IData blResultInfo=new DataMap();
		output.put("blResultInfo", blResultInfo);
		
		String serialNumber=input.getString("idv");
		
		String key="aibizhall-biz-zzyyt-1234";
		IDataset keyList=CommparaInfoQry.getCommNetInfo("CSM", "3838", "ENCRYPT_KEY");
		if(IDataUtil.isNotEmpty(keyList)){
			key=keyList.getData(0).getString("PARA_CODE1","aibizhall-biz-zzyyt-1234");
		}
		String pstpId=Des3Utils.decryptThreeDESECB(input.getString("idCardNum"), key);
		String ccPasswd=Des3Utils.decryptThreeDESECB(input.getString("ccPasswd"), key);
		String username=Des3Utils.decryptThreeDESECB(input.getString("username"), key);
		
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
		if(uca==null){
			blResultInfo.put("busState", "0");
    		blResultInfo.put("condition", "0");
    		blResultInfo.put("reason", "用户信息不存在");
    		return output;
		}
		
		String psptTypeCode="";
		if(StringUtils.isNotEmpty(input.getString("idCardType"))){
	        IDataset cardList=CommparaInfoQry.getCommNetInfo("CSM", "3838", "IDCARD");
	    	for(int i=0;i<cardList.size();i++){
	    		if(input.getString("idCardType","").equals(cardList.getData(i).getString("PARA_CODE3"))){
	    			psptTypeCode=cardList.getData(i).getString("PARA_CODE2","0");
	    			break;
	    		}
	    	}
	    	if(StringUtils.isEmpty(psptTypeCode)){
	    		blResultInfo.put("busState", "0");
	    		blResultInfo.put("condition", "0");
	    		blResultInfo.put("reason", "证件类型不存在");
	    		return output;
	    	}
	    	if(StringUtils.isEmpty(pstpId)){
	    		blResultInfo.put("busState", "0");
	    		blResultInfo.put("condition", "0");
	    		blResultInfo.put("reason", "证件号码不能为空");
	    		return output;
	    	}
	    	
		}
		//证件校验
		if (!StringUtils.isEmpty(pstpId)) {
			boolean checkError = false;
			String errorMsg="";
            if (StringUtils.equals(pstpId, uca.getCustomer().getPsptId())) {
                if(input.getString("idCardType","").trim().equals("00")){
                    if( StringUtils.equals("0", uca.getCustomer().getPsptTypeCode())|| StringUtils.equals("1", uca.getCustomer().getPsptTypeCode())){ 
                    	
                    }else{
                    	errorMsg="本地证件类型非身份证";
                        checkError = true;
                    }
                }else{
                    if( StringUtils.equals(psptTypeCode, uca.getCustomer().getPsptTypeCode())){                        
                    }else{
                    	errorMsg="证件类型不匹配";
                        checkError = true;
                    }
                }
            } else {
            	if(input.getString("idCardType","").trim().equals("00")){
            		if( pstpId.length() == 15){
            			pstpId = IdcardUtils.conver15CardTo18(pstpId);
            			if(StringUtils.equals(pstpId.toUpperCase(), uca.getCustomer().getPsptId().toUpperCase())){
            				
            			}else{
            				errorMsg="证件号码不匹配";
                            checkError = true;
                        } 
            		}else if(uca.getCustomer().getPsptId().length() == 15){
            			String strPstpId = IdcardUtils.conver15CardTo18(uca.getCustomer().getPsptId());
            			if (StringUtils.equals(pstpId.toUpperCase(), strPstpId.toUpperCase())) {
            				
            			}
            			else{
            				errorMsg="证件号码不匹配";
                            checkError = true;
                        } 

            		}else{
            			errorMsg="证件号码不匹配";
                		checkError = true;
            		}
            	}else{
            		errorMsg="证件号码不匹配";
            		checkError = true;
            	}
            }
            if(checkError){
            	blResultInfo.put("busState", "0");
	    		blResultInfo.put("condition", "0");
	    		blResultInfo.put("reason", errorMsg);
	    		return output;
            }
            
            //姓名校验
            if(StringUtils.isNotEmpty(username)&&(!(username.equals(uca.getCustomer().getCustName())))){
            	blResultInfo.put("busState", "0");
	    		blResultInfo.put("condition", "1");
	    		blResultInfo.put("reason", "姓名和身份证姓名不一致");
	    		IData conditionInfo=new DataMap();
	    		conditionInfo.put("infoCode", "nameIsSame");
	    		conditionInfo.put("infoValue", "1");
	    		blResultInfo.put("conditionInfo", conditionInfo);
	    		return output;
            }
		}
		//密码校验
		if(StringUtils.isNotEmpty(ccPasswd)){
	        String userOrignPsw = uca.getUser().getUserPasswd();
	        String userId=uca.getUserId();
	        if (StringUtils.isBlank(userOrignPsw)){
	        	blResultInfo.put("busState", "0");
	    		blResultInfo.put("condition", "0");
	    		blResultInfo.put("reason", "用户服务密码不存在！");
	    		return output;
	        }
	        if (!PasswdMgr.checkUserPassword(ccPasswd, userId, userOrignPsw)){
        		blResultInfo.put("busState", "0");
	    		blResultInfo.put("condition", "0");
	    		blResultInfo.put("reason", "密码校验不通过！");
	    		return output;
	        }
		}
		
		//用户状态校验
		String userStatus = getIBossUserStateParam(uca.getUser().getUserStateCodeset());
        boolean isCorrectStatus = false;
        String errorMsg = "";
        if (StringUtils.equals(userStatus, "00")) {
        	isCorrectStatus = true;
        	errorMsg = "ok";
        } else if(StringUtils.equals(userStatus, "01")) {
        	isCorrectStatus = false;
        	errorMsg = "用户已单向停机！";
        } else if(StringUtils.equals(userStatus, "02")) {
        	isCorrectStatus = false;
        	errorMsg = "用户已停机！";
        } else if(StringUtils.equals(userStatus, "03")) {
        	isCorrectStatus = false;
        	errorMsg = "用户已预销户！";
        } else if(StringUtils.equals(userStatus, "04")) {
        	isCorrectStatus = false;
        	errorMsg = "用户已销户！";
        } else {
        	isCorrectStatus = false;
        	errorMsg = "用户状态不正确！";
        }
        if (!isCorrectStatus) {
        	if (StringUtils.equals("5", uca.getUser().getUserStateCodeset()) // 欠费停机
        			|| StringUtils.equals("A", uca.getUser().getUserStateCodeset())) {// 欠费半停机
        		errorMsg = "鉴权失败（欠费停机或欠费半停机）";
        	} else {
        		errorMsg = "鉴权失败（非正常在网用户）";
        	}
        	blResultInfo.put("busState", "0");
    		blResultInfo.put("condition", "0");
    		blResultInfo.put("reason", errorMsg);
    		return output;
        }
        
        //通话记录校验
        String numberCheck = input.getString("numberCheck","");
        if(StringUtils.isNotEmpty(numberCheck)){
        	if(numberCheck.split("\\|").length<5){
        		blResultInfo.put("busState", "0");
	    		blResultInfo.put("condition", "0");
	    		blResultInfo.put("reason", "好友号码至少5个！");
	    		return output;
        	}
        	try {
        		IDataset numberResults = AcctCall.checkFriend(serialNumber, numberCheck);
        		if(IDataUtil.isNotEmpty(numberResults)){
            		IData numberResult = numberResults.getData(0);
            		if(!"0000".equals(numberResult.getString("RESULT_CODE",""))){
            			if(!checkFailedCount(uca,input.getString("bizType"))){
            				blResultInfo.put("busState", "0");
            	    		blResultInfo.put("condition", "0");
            	    		blResultInfo.put("reason", "鉴权失败超5次，系统已锁定！");
            	    		return output;
            			}
            			blResultInfo.put("busState", "0");
        	    		blResultInfo.put("condition", "0");
        	    		blResultInfo.put("reason", "好友号码" + numberResult.getString("FAILED_NUMBER") + "验证未通过！");
        	    		return output;
            		}
            	}
        	}catch(Exception e){
        		blResultInfo.put("busState", "0");
        		blResultInfo.put("condition", "0");
        		blResultInfo.put("reason", e.getMessage());
        		return output;
        	}
        }
        
        //返回可办理
        blResultInfo.put("busState", "1");
		blResultInfo.put("condition", "0");
		
        //用户资料
		IData userInfo=new DataMap();
		userInfo.put("homeProv", "898");
	    userInfo.put("userStatus", userStatus);//是
	    String brand = uca.getBrandCode();
    	if("G001".equals(brand)){
    		userInfo.put("brand", "00");//01:全球通
    	}else if("G002".equals(brand) || "GS01".equals(brand)){
    		userInfo.put("brand", "01");//02:神州行
    	}else if("G010".equals(brand) || "GS03".equals(brand)){
    		userInfo.put("brand", "02");//03:动感地带
    	}else{
    		userInfo.put("brand", "03");//09:其他品牌
    	}
    	
    	IData ucaInfo = UcaInfoQry.qryUserInfoByUserId(uca.getUserId());
        String category = ucaInfo.getString("PREPAY_TAG","");
        userInfo.put("category", category);//是
        if("PWLW".equals(uca.getBrandCode()) || "WLWG".equals(uca.getBrandCode())){
        	userInfo.put("interUser", "0");//是
        }else{
        	userInfo.put("interUser", "1");//是
        }
        
        userInfo.put("isShiMing", "1".equals(uca.getCustomer().getIsRealName())?"0":"1");//是
	    
        output.put("userInfo", userInfo);
        
        return output;
	}
	 /**
     * 获取用户状态编码
     */
    private String getIBossUserStateParam(String param)
    {
        IData userStateData = new DataMap();
        userStateData.put("N", "00");// 信用有效时长开通
        userStateData.put("T", "01");// 骚扰电话半停机
        userStateData.put("0", "00");// 开通
        userStateData.put("1", "02");// 申请停机
        userStateData.put("2", "02");// 挂失停机
        userStateData.put("3", "02");// 并机停机
        userStateData.put("4", "02");// 局方停机
        userStateData.put("5", "02");// 欠费停机
        userStateData.put("6", "04");// 申请销号
        userStateData.put("7", "02");// 高额停机
        userStateData.put("8", "03");// 欠费预销号
        userStateData.put("9", "04");// 欠费销号
        userStateData.put("A", "01");// 欠费半停机
        userStateData.put("B", "01");// 高额半停机
        userStateData.put("E", "02");// 转网销号停机
        userStateData.put("F", "02");// 申请预销停机
        userStateData.put("G", "01");// 申请半停机
        userStateData.put("I", "02");// 申请停机（收月租）

        return userStateData.getString(param);
    }
    private boolean checkFailedCount(UcaData uca,String bizType) throws Exception{
		IData inparam = new DataMap();
		String userId = uca.getUserId();
		String auth = "CHECK_AUTH";
		int authCount = 4;
		
		IDataset errCounts = UserOtherInfoQry.getAllOtherInfoByUserIdValueCode(userId,auth);
		if(IDataUtil.isEmpty(errCounts)){
			//新增一条记录
			inparam.put("USER_ID", uca.getUserId());
			inparam.put("PARTITION_ID", uca.getUserId().substring(uca.getUserId().length() - 4));
			inparam.put("RSRV_VALUE_CODE", auth);
			inparam.put("RSRV_VALUE", "1");
			inparam.put("START_DATE", SysDateMgr.getSysDateYYYYMMDD()+"000000");
			inparam.put("END_DATE", SysDateMgr.getSysDateYYYYMMDD()+"235959");
			inparam.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			inparam.put("INST_ID", SeqMgr.getInstId());
			inparam.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
			inparam.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
			inparam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
			inparam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
			Dao.insert("TF_F_USER_OTHER", inparam);
		}else{
			//首先判断是否是今天的
			String endDate = errCounts.getData(0).getString("END_DATE");
			String dEndDate = DateFormatUtils.format(SysDateMgr.string2Date(endDate,"yyyy-MM-dd HH:mm:ss"),"yyyyMMddHHmmss");
			String sysDate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
			if(dEndDate.compareTo(sysDate) < 0){
				//如果不是今天的 则更新时间 并修改RSRV_VALUE为1
				IData param = new DataMap();
				param.putAll(errCounts.first());
				param.put("START_DATE", SysDateMgr.getSysDateYYYYMMDD()+"000000");
				param.put("END_DATE", SysDateMgr.getSysDateYYYYMMDD()+"235959");
				param.put("RSRV_VALUE", "1");
				param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
				param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
				SQLParser parser = new SQLParser(param);

		        parser.addSQL(" UPDATE TF_F_USER_OTHER ");
		        parser.addSQL(" SET START_DATE = TO_DATE(:START_DATE,'YYYY/MM/DD HH24:MI:SS'), ");
		        parser.addSQL(" END_DATE = TO_DATE(:END_DATE,'YYYY/MM/DD HH24:MI:SS'), ");
		        parser.addSQL(" RSRV_VALUE = :RSRV_VALUE, ");
		        parser.addSQL(" UPDATE_STAFF_ID = :UPDATE_STAFF_ID,  ");
		        parser.addSQL(" UPDATE_DEPART_ID = :UPDATE_DEPART_ID  ");
		        parser.addSQL(" WHERE USER_ID = :USER_ID ");
		        parser.addSQL(" AND RSRV_VALUE_CODE = :RSRV_VALUE_CODE ");
				Dao.executeUpdate(parser);
			}else{
				//如果是今天的则判断是否次数超过限制次数，未超过则增加一次次数
				String count = errCounts.getData(0).getString("RSRV_VALUE");
				int iCount = Integer.valueOf(count);
				if(iCount > authCount){
					return false;
				}else{
					IData param = new DataMap();
					param.putAll(errCounts.first());
					param.put("RSRV_VALUE", String.valueOf(iCount+1));
					param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
					param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
					SQLParser parser = new SQLParser(param);

			        parser.addSQL(" UPDATE TF_F_USER_OTHER ");
			        parser.addSQL(" SET RSRV_VALUE = :RSRV_VALUE, ");
			        parser.addSQL(" UPDATE_STAFF_ID = :UPDATE_STAFF_ID,  ");
			        parser.addSQL(" UPDATE_DEPART_ID = :UPDATE_DEPART_ID  ");
			        parser.addSQL(" WHERE USER_ID = :USER_ID ");
			        parser.addSQL(" AND RSRV_VALUE_CODE = :RSRV_VALUE_CODE ");
					Dao.executeUpdate(parser);
				}
			}
		}
		return true;
	}
    /**
     * 写卡结果反馈
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData returnWriteCardResult(IData inParam) throws Exception {
    	IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		
		//非空判断
		try{
			IDataUtil.chkParam(input, "presetType");
			IDataUtil.chkParam(input, "cardType");
			IDataUtil.chkParam(input, "mobileNum");
			IDataUtil.chkParam(input, "imsi");
			IDataUtil.chkParam(input, "result");
			IDataUtil.chkParam(input, "orderId");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),output);
		}
		
		String presetType=input.getString("presetType");
		String cardType=input.getString("cardType");
		String emptyCardId=input.getString("cardSn");
		String serialNumber=input.getString("mobileNum");
		String imsi=input.getString("imsi");
		String result=input.getString("result");
		
		//设置必填返回参数
		output.put("mobileNum", input.getString("mobileNum"));
		output.put("orderId", input.getString("orderId"));
		output.put("extOrderId", input.getString("extOrderId"));
		
		//集中写卡自助激活增加
		boolean isSelfTerm=true;
//		if(StringUtils.isNotEmpty(input.getString("channelId"))){
//			String channelId=input.getString("channelId");
//			//是否在渠道里
//			IDataset channelList=CommparaInfoQry.getCommParas("CSM", "7979", "JZXK_CHANNEL", channelId, "0898");
//			if(IDataUtil.isNotEmpty(channelList)){
//				isSelfTerm=false;
//			}
//		}
		IDataset orderlist = AbilityPlatOrderBean.queryOrderInfo(input.getString("orderId",""));
		if(IDataUtil.isNotEmpty(orderlist)){
			isSelfTerm=false;
		}
		
		//新型自助终端分支
		if(isSelfTerm){
			if(!("1".equals(presetType))){
				return SelfTerminalUtil.responseFail("目前不支持presetType="+presetType,output);
			}
			
			if("2".equals(cardType)||"3".equals(cardType)){
				try{
					IDataUtil.chkParam(input, "cardSn");
				}catch(Exception e){
					return SelfTerminalUtil.responseFail(e.getMessage(),output);
				}
			}else{
				return SelfTerminalUtil.responseFail("目前不支持的cardType="+cardType,output);
			}
			
			IDataset resInfos = ResCall.backWriteSimCard(imsi, emptyCardId, "", CSBizBean.getTradeEparchyCode(), "ST", "", "", "", "2", result);
			if (IDataUtil.isEmpty(resInfos) || !"0".equals(resInfos.getData(0).getString("X_RESULTCODE"))) {
	            CSAppException.apperr(CrmCommException.CRM_COMM_103, "写卡信息回写错误！");
	        }
		//集中写卡自助激活分支	
		}else{
//			//非空判断
//			try{
//				IDataUtil.chkParam(input, "orderId");
//			}catch(Exception e){
//				return SelfTerminalUtil.responseFail(e.getMessage(),output);
//			}
			String channelId=input.getString("channelId");
			//办理工号
			IDataset staffList=CommparaInfoQry.getCommParas("CSM", "7979", "JZXK_CHANNEL_STAFF", channelId, "0898");
			if(IDataUtil.isNotEmpty(staffList)){
				//赋值登录工号
				if(StringUtils.isNotEmpty(staffList.getData(0).getString("PARA_CODE2"))){
					getVisit().setStaffId(staffList.getData(0).getString("PARA_CODE2"));
				}
				//赋值登录部门ID
				if(StringUtils.isNotEmpty(staffList.getData(0).getString("PARA_CODE3"))){
					getVisit().setDepartId(staffList.getData(0).getString("PARA_CODE3"));
				}
				//赋值部门代码
				if(StringUtils.isNotEmpty(staffList.getData(0).getString("PARA_CODE4"))){
					getVisit().setDepartCode(staffList.getData(0).getString("PARA_CODE4"));
				}
				//赋值业务区
				if(StringUtils.isNotEmpty(staffList.getData(0).getString("PARA_CODE5"))){
					getVisit().setCityCode(staffList.getData(0).getString("PARA_CODE5"));
				}
			}
			
			//4、写卡成功申请开通改造
		    //（1）通过订单号查询订单表，如果是21，调资源回写成功后，通过订单号将SIM_CARD_NO、和白卡号更新到TF_F_OAOWRITECARDORDER_INFO STATE=1
			String OAOorderId=input.getString("orderId");
			//查询订单信息
	        IDataset OAOorderInfos= AbilityPlatOrderBean.querySuborderInfoByNumberOperType(null, OAOorderId, null);
	        String numberOprType = "";
	        String suborderId = "";
	        if(IDataUtil.isNotEmpty(OAOorderInfos)){
	        	numberOprType = OAOorderInfos.getData(0).getString("NUMBER_OPRTYPE"); 
	        	suborderId = OAOorderInfos.getData(0).getString("SUBORDER_ID"); 
	        }
	        //40物流OAO白卡写卡，面对面写白卡
			if("21".equals(numberOprType)||"23".equals(numberOprType)){
				//写卡成功
				if("0".equals(input.getString("result"))){
					//回写资源
			        IData usimPreInfo=input.getData("usimPreInfo");
			        String encKi ="";
			        String encOpc ="";
			        if(IDataUtil.isNotEmpty(usimPreInfo)){
			        	 encKi = usimPreInfo.getString("encK");
					     encOpc = usimPreInfo.getString("encOpc");
			        }
			       
			        String writeTag = "3";
			        String simCardNo = "";
			        String remoteMode = "1";
			        String xChoiceTag = "0";  
	
			        KIFunc kifunc = new KIFunc();
			        String ki = kifunc.EncryptKI(encKi);
			        String opc = kifunc.EncryptKI(encOpc);
			        IDataset resInfos = ResCall.backWriteSimCardByIntf(imsi,writeTag,simCardNo,remoteMode, xChoiceTag,ki,opc,serialNumber);
			        if (IDataUtil.isEmpty(resInfos) || !"0".equals(resInfos.getData(0).getString("X_RESULTCODE")))
			        {
			            CSAppException.apperr(CrmCommException.CRM_COMM_103, "写卡信息回写错误！");
			        }
			        
			        //申请开通
			        simCardNo=resInfos.getData(0).getString("SIM_CARD_NO");
			        AbilityPlatOrderBean.updateOAOwriteCardOrderInfosimCard(simCardNo,emptyCardId,"1",OAOorderId, suborderId);
				}
			//携入上门写白卡
			}else if("24".equals(numberOprType)){
				//写卡成功
				if("0".equals(input.getString("result"))){
					//回写资源
					IData usimPreInfo=input.getData("usimPreInfo");
					String encKi ="";
					String encOpc ="";
					if(IDataUtil.isNotEmpty(usimPreInfo)){
						encKi = usimPreInfo.getString("encK");
						encOpc = usimPreInfo.getString("encOpc");
					}

					String writeTag = "3";
					String simCardNo = "";
					String remoteMode = "1";
					String xChoiceTag = "0";

					KIFunc kifunc = new KIFunc();
					String ki = kifunc.EncryptKI(encKi);
					String opc = kifunc.EncryptKI(encOpc);
					IDataset resInfos = ResCall.backWriteSimCardByIntf(imsi,writeTag,simCardNo,remoteMode, xChoiceTag,ki,opc,serialNumber);
					if (IDataUtil.isEmpty(resInfos) || !"0".equals(resInfos.getData(0).getString("X_RESULTCODE")))
					{
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "写卡信息回写错误！");
					}
					//申请开通
					simCardNo=resInfos.getData(0).getString("SIM_CARD_NO");
					//更新np表
					IData param = new DataMap();
					param.put("SIM_CARD_NO", simCardNo);
					param.put("EMPTY_CARD", emptyCardId);
					param.put("STATE", "4");
					param.put("ORDER_ID", OAOorderId);
					param.put("SUBORDER_ID", suborderId);
					AbilityPlatOrderBean.updateOAOwriteCardOrderInfoNP(param);
				}
			//针对08，09，集中写卡自助激活
			}else{
				//写卡成功
				if("0".equals(input.getString("result"))){
					String route = RouteInfoQry.getEparchyCodeBySnForCrm(serialNumber);
					IData eleInfo = new DataMap();
					IData saleActiveInfo = new DataMap();
					//查询订单信息
					String orderId=input.getString("orderId");
					IDataset orderInfos= AbilityPlatOrderBean.querySuborderInfoByNumberOperType(null, orderId, null);
					if(IDataUtil.isEmpty(orderInfos)) {
						return SelfTerminalUtil.responseFail("根据订单号["+orderId+"]查询不到订单信息",output);
					}
					if("AC".equals(orderInfos.getData(0).getString("STATE"))){
						return SelfTerminalUtil.responseFail("订单号["+orderId+"]已激活",output);
					}
					//查询产品信息
			        IDataset prodList = AbilityPlatOrderBean.queryProductorderInfo(orderInfos.getData(0).getString("SUBORDER_ID"));
			        if (IDataUtil.isEmpty(prodList)) {
			        	return SelfTerminalUtil.responseFail("根据订单号["+orderId+"]查询不到产品信息",output);
			        }
			        String ctrmProductId ="";
			        String ctrmProductIdNew ="";
			        for (int k = 0; k < prodList.size(); k++) {
			            IData prodInfo = prodList.getData(k);
			            /* 查询产品对应关系表信息 */
			            ctrmProductId = prodInfo.getString("PRODUCT_ID"); // CTRM_PRODUCT_ID
			            //产品ID得到本地映射的产品
			            IDataset relaProducts = AbilityOpenPlatQry.queryListInfo(route, ctrmProductId);
			            if (IDataUtil.isEmpty(relaProducts)) {
			                return SelfTerminalUtil.responseFail("订单产品ID【" + ctrmProductId + "】不存在本地产品的映射关系，请管理员进行配置！",output);
			            }
			            //能力开放平台产品编码类型,1-终端产品，2-合约产品，3-套餐及增值产品  
			            for(int j=0;j<relaProducts.size();j++){
			            	IData relaProductsData = relaProducts.getData(j);
				            if ("3".equals(relaProductsData.getString("CTRM_PRODUCT_TYPE"))
				            		&&"P".equals(relaProductsData.getString("ELEMENT_TYPE_CODE"))) {
				                eleInfo.put("ELEMENT_ID", relaProductsData.getString("PRODUCT_ID"));
				                eleInfo.put("ELEMENT_TYPE_CODE", relaProductsData.getString("ELEMENT_TYPE_CODE"));
				                eleInfo.put("MODIFY_TAG", "0");
				            }else if("2".equals(relaProductsData.getString("CTRM_PRODUCT_TYPE"))){
				            	if(StringUtils.isNotEmpty(relaProductsData.getString("PRODUCT_ID"))
				            			&&(!("-1".equals(relaProductsData.getString("PRODUCT_ID"))))
				            			&&StringUtils.isNotEmpty(relaProductsData.getString("PACKAGE_ID"))
				            			&&(!("-1".equals(relaProductsData.getString("PACKAGE_ID"))))){
				            		saleActiveInfo.put("PRODUCT_ID", relaProductsData.getString("PRODUCT_ID"));
				            		saleActiveInfo.put("PACKAGE_ID", relaProductsData.getString("PACKAGE_ID"));
				            		ctrmProductIdNew = prodInfo.getString("PRODUCT_ID");
				            	}
				            }
			            }
			        }
			        
			        if(IDataUtil.isEmpty(eleInfo)){
			        	return SelfTerminalUtil.responseFail( "此产品的本地产品的映射关系配置非套餐及增值产品，中止订购，请检查本地产品的映射关系！",output);
			        }
			        
			        String brandCode = UProductInfoQry.getBrandCodeByProductId(eleInfo.getString("ELEMENT_ID"));
			        String productName=UProductInfoQry.getProductNameByProductId(eleInfo.getString("ELEMENT_ID"));
			        IDataset selectElements=SelfTerminalUtil.getElements(eleInfo.getString("ELEMENT_ID"));
				    
					//回写资源
			        IData usimPreInfo=input.getData("usimPreInfo");
			        String encKi ="";
			        String encOpc ="";
			        if(IDataUtil.isNotEmpty(usimPreInfo)){
			        	 encKi = usimPreInfo.getString("encK");
					     encOpc = usimPreInfo.getString("encOpc");
			        }
			       
			        String writeTag = "3";
			        String simCardNo = "";
			        String remoteMode = "1";
			        String xChoiceTag = "0";  
	
			        KIFunc kifunc = new KIFunc();
			        String ki = kifunc.EncryptKI(encKi);
			        String opc = kifunc.EncryptKI(encOpc);
			        IDataset resInfos = ResCall.backWriteSimCardByIntfAndCardSN(imsi,writeTag,simCardNo,remoteMode, xChoiceTag,ki,opc,serialNumber,emptyCardId);
			        if (IDataUtil.isEmpty(resInfos) || !"0".equals(resInfos.getData(0).getString("X_RESULTCODE")))
			        {
			            CSAppException.apperr(CrmCommException.CRM_COMM_103, "写卡信息回写错误！");
			        }
			        
			        //申请开通
			        simCardNo=resInfos.getData(0).getString("SIM_CARD_NO");
			        
			        IDataset preOrderInfos= SelfTerminalBean.queryPreOrderInfo(orderId, orderInfos.getData(0).getString("SUBORDER_ID"));
			        if(IDataUtil.isEmpty(preOrderInfos)){
				        IData reqInfo=new DataMap();
				        reqInfo.put("ORDER_ID", orderId);
				        reqInfo.put("SUBORDER_ID",orderInfos.getData(0).getString("SUBORDER_ID"));
				        reqInfo.put("SIM_CARD_NO", simCardNo);
				        reqInfo.put("BILL_ID", serialNumber);
				        reqInfo.put("SIM_CARD_NO", simCardNo);
				        reqInfo.put("SELELCT_ELEMENTS", selectElements.toString());
				        reqInfo.put("RSRV_STR1", ctrmProductIdNew);
				        reqInfo.put("RSRV_STR2", productName);
				        reqInfo.put("RSRV_STR7", emptyCardId);
				        reqInfo.put("STATE", "0");
			        	reqInfo.put("EXEC_COUNT", "0");
			        	reqInfo.put("ACCEPT_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			        	reqInfo.put("PRODUCT_ID", eleInfo.getString("ELEMENT_ID"));
			        	reqInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
			        	reqInfo.put("BRAND_CODE", brandCode);
			        	reqInfo.put("STAFF_ID", getVisit().getStaffId());
			        	reqInfo.put("DEPT_ID", getVisit().getDepartId());
			        	reqInfo.put("IN_MODE_CODE", getVisit().getInModeCode());
			        	
			        	if(IDataUtil.isNotEmpty(saleActiveInfo)){
			        		 reqInfo.put("RSRV_STR3", saleActiveInfo.getString("PRODUCT_ID"));
						     reqInfo.put("RSRV_STR4", saleActiveInfo.getString("PACKAGE_ID"));
			        	}
			        	
			        	Dao.insert("TF_F_PREORDER_INFO", reqInfo, Route.CONN_CRM_CEN);
			        }else{
			        	String state=preOrderInfos.getData(0).getString("STATE");
			        	String exeState=preOrderInfos.getData(0).getString("EXEC_STATE");
			        	if("1".equals(state)&&"-1".equals(exeState)){
			        		SelfTerminalBean.updatePreOrderInfo("0",null,orderId,  orderInfos.getData(0).getString("SUBORDER_ID"));
			        	}
			        }
				}
			}
		}
		
		return SelfTerminalUtil.responseSuccess(output);
    }
    /**
     * 用户身份结果同步
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData syncUserCheckResult(IData inParam) throws Exception {
    	IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceNum");
			IDataUtil.chkParam(input, "identitySN");
			IDataUtil.chkParam(input, "checkResult");
			IDataUtil.chkParam(input, "checkValue");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(), null);
		}
		/*IData cardData=input.getData("certificateInfo");
		if(IDataUtil.isEmpty(cardData)){
			return SelfTerminalUtil.responseFail("certificateInfo不能为空", null);
		}*/
		input.put("OP_ID", getVisit().getStaffId());
		input.put("OP_ORG", getVisit().getDepartId());
		
		SelfTerminalBean selfBean=BeanManager.createBean(SelfTerminalBean.class);
		selfBean.addUserSyncResult(input);
		
		return SelfTerminalUtil.responseSuccess(output);
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
    /**
     * 白卡出库接口
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData emptyOutStock(IData inParam) throws Exception {
    	IData input = new DataMap(inParam.toString());
    	IData output=new DataMap();
		output.put("RESULT_CODE", "0");
		output.put("RESULT_INFO", "SUCCESS");
		
		IDataUtil.chkParam(input, "EMPTY_CARD_ID");
    	IDataUtil.chkParam(input, "ICCID");
    	IDataUtil.chkParam(input, "MOBILENUM");
    	IDataUtil.chkParam(input, "WRITE_STATUS");
    	
    	//写卡失败不做处理
    	if("01".equals(input.getString("WRITE_STATUS"))){
    		return output;
    	}
	
    	//先调白卡出库
    	IDataset resultInfo =CSAppCall.call("SS.RemoteWriteCardSVC.applyResultActiveCallRes", input);
    	if(IDataUtil.isNotEmpty(resultInfo)){
    		//生成跨区补卡149工单
    		IData params=new DataMap();
    		params.put("SERIAL_NUMBER", input.getString("MOBILENUM"));
    		params.put("ICCID", input.getString("ICCID"));
    		params.put("EMPTY_CARD_ID", input.getString("EMPTY_CARD_ID"));
    		
    		IData custInfo=new DataMap();
    		custInfo.put("IDCARDTYPE", "0");
    		custInfo.put("IDCARDNUM", "0");
    		custInfo.put("CUST_NAME", input.getString("MOBILENUM"));
    		
    		params.put("AUTH_CUST_INFO", custInfo);
    		
    		CSAppCall.call("SS.RemoteWriteCardRegSVC.tradeReg", params);
    		
    	}else{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "白卡出库失败");
    	}
    	
    	return output;
    }
    /**
     * 退款接口
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData backFee(IData inParam) throws Exception {
    	IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		output.put("RESULT_CODE", "0");
		output.put("RESULT_INFO", "SUCCESS");
		//非空判断
		try{
			IDataUtil.chkParam(input, "transactionId");
		}catch(Exception e){
			output.put("RESULT_CODE", "-1");
			output.put("RESULT_INFO", e.getMessage());
			return output;
		}
		
		//获取能开URL
		IData param1 = new DataMap();
    	param1.put("PARAM_NAME", "crm.ABILITY.BACKFEE");
    	IDataset Abilityurls = Dao.qryBySql(
    			new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' ")
    			, param1, "cen");
    	String Abilityurl = "";
    	if (Abilityurls != null && Abilityurls.size() > 0){
    		Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
    	}else{
    		CSAppException.appError("-1", "crm.ABILITY.BACKFEE接口地址未在TD_S_BIZENV表中配置");
    	}
    	
    	//组装能开入参
    	IData abilityData = new DataMap();
    	abilityData.put("refundType", "02");//纯支付退款
    	abilityData.put("transactionId", input.getString("transactionId",""));
    	abilityData.put("orderNo", "");//refundType为02时不填
    	abilityData.put("refundFee", input.getString("refundFee",""));//退款金额，单位为“分” 目前只支持全额退款，该字段不填。
    	abilityData.put("refundReason", input.getString("refundReason",""));//退款理由非必填
    	IData responeResult = AbilityEncrypting.callAbilityPlatCommon(Abilityurl,abilityData);
    	String resCode=responeResult.getString("resCode");
    	if(!"00000".equals(resCode)){
            output.put("RESULT_CODE", "-1");
			output.put("RESULT_INFO", responeResult.getString("resMsg"));
        }else{
        	IData out=responeResult.getData("result");
        	String status=out.getString("status");
        	if("SUCCESS".equals(status)){
        		output.put("refundOrderNo", out.getString("refundOrderNo"));
        		output.put("reqDate", out.getString("reqDate"));
        	}else{
        		output.put("RESULT_CODE", "-1");
     			output.put("RESULT_INFO", "平台退款处理失败");
        	}
        }
    	output.put("transactionId", input.getString("transactionId",""));
    	SelfTerminalBean selfBean=BeanManager.createBean(SelfTerminalBean.class);
		selfBean.updateSettleResult(output, "0");
		return output;
    }
    /**
     * 退款结果通知
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData notifyBackFee(IData inParam) throws Exception {
    	IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		output.put("returnCode", "00000");
		output.put("returnMessage", "SUCCESS");
		//非空判断
		try{
			IDataUtil.chkParam(input, "transactionId");
		}catch(Exception e){
			output.put("returnCode", "-1");
			output.put("returnMessage", e.getMessage());
		}
		SelfTerminalBean selfBean=BeanManager.createBean(SelfTerminalBean.class);
		selfBean.updateSettleResult(input, "1");
		return output;
    }
    /**
	 * 携入开户受理接口
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData openNpSvcNum(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		//非空判断
		try{
			IDataUtil.chkParam(input, "msisdn");//已选号码
			IDataUtil.chkParam(input, "iccid");//卡号
			IDataUtil.chkParam(input, "homeOperator");//归属运营商
			IDataUtil.chkParam(input, "networkType");//网络类型
			IDataUtil.chkParam(input, "authCode");//授权码
			IDataUtil.chkParam(input, "authCodeExpired");//授权码有效期
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		//套餐信息
		IData goodsInfo=input.getData("goodsInfo");
		if(IDataUtil.isEmpty(goodsInfo)){
			return SelfTerminalUtil.responseFail("goodsInfo不能为空",null);
		}
		
		try{
			IDataUtil.chkParam(goodsInfo, "goodsId");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		IData offer=UpcCall.queryOfferByOfferId("P", goodsInfo.getString("goodsId"));
		if(IDataUtil.isEmpty(offer)){
			return SelfTerminalUtil.responseFail("办理的主套餐不存在productId="+goodsInfo.getString("goodsId"),null);
		}
		
		//支付信息--携入开户无支付信息
//		IData paymentInfo=input.getData("paymentInfo");
//		if(IDataUtil.isEmpty(paymentInfo)){
//			return SelfTerminalUtil.responseFail("paymentInfo不能为空",null);
//		}
//		try{
//			IDataUtil.chkParam(paymentInfo, "chargeType");
//			IDataUtil.chkParam(paymentInfo, "totalFee");
//			IDataUtil.chkParam(paymentInfo, "payment");
//			//IDataUtil.chkParam(paymentInfo, "paymentOrderId");
//		}catch(Exception e){
//			return SelfTerminalUtil.responseFail(e.getMessage(),null);
//		}
		
		//客户信息
		IData certificateInfo=input.getData("certificateInfo");
		if(IDataUtil.isEmpty(certificateInfo)){
			return SelfTerminalUtil.responseFail("certificateInfo不能为空",null);
		}
		try{
			IDataUtil.chkParam(certificateInfo, "certificateType");
			IDataUtil.chkParam(certificateInfo, "certificateNo");
			IDataUtil.chkParam(certificateInfo, "certificateAddr");
			IDataUtil.chkParam(certificateInfo, "legalName");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		//证件类型转换
        String psptTypeCode="";
        IDataset cardList=CommparaInfoQry.getCommNetInfo("CSM", "3838", "IDCARD");
    	for(int i=0;i<cardList.size();i++){
    		if(certificateInfo.getString("certificateType","").equals(cardList.getData(i).getString("PARA_CODE3"))){
    			psptTypeCode=cardList.getData(i).getString("PARA_CODE2","0");
    			break;
    		}
    	}
    	
    	if(StringUtils.isEmpty(psptTypeCode)){
    		output.put("isSuc", "2");
    		return SelfTerminalUtil.responseFail("证件类型不存在",output);
    	}
		
		 // 先进行sim卡选占
        IData checkSimData = new DataMap();
        checkSimData.put("SIM_CARD_NO", input.getString("iccid"));
        checkSimData.put("SERIAL_NUMBER", input.getString("msisdn"));
        checkSimData.put("ROUTE_EPARCHY_CODE", "0898");
        IDataset checkSimResults = CSAppCall.call("SS.CreateNpUserTradeSVC.checkSimCardNo", checkSimData);
        IData checkSimResult = checkSimResults.getData(0);
        IData resInfoData = checkSimResult.getData("RES_INFO_DATA");
        String SIM_CARD_SALE_MONEY = resInfoData.getString("SIM_CARD_SALE_MONEY", "0");
        String SIM_FEE_TAG = resInfoData.getString("SIM_FEE_TAG", "");
        System.out.println("--------------openNpSvcNum----mqx-----------checkSimResult="+checkSimResult);

    	//组织入参
        IData openParam = new DataMap();
        openParam.put("SIM_CARD_SALE_MONEY", SIM_CARD_SALE_MONEY);//paymentInfo.getString("simPrice","0"));
        openParam.put("SIM_FEE_TAG", SIM_FEE_TAG);
        openParam.put("SERIAL_NUMBER", input.getString("msisdn"));
        openParam.put("SIM_CARD_NO", input.getString("iccid"));
        openParam.put("HOME_OPERATOR", input.getString("homeOperator"));
        openParam.put("NETWORK_TYPE", input.getString("networkType"));
        openParam.put("PROV_CODE", "898");
        openParam.put("IS_REAL_NAME", "1");
        openParam.put("NP_BACK", "0");
        openParam.put("EMPTY_CARD_ID", input.getString("cardSN",""));
        openParam.put("PSPT_TYPE_CODE", psptTypeCode);
        openParam.put("PSPT_ID", certificateInfo.getString("certificateNo"));
        openParam.put("CUST_NAME", certificateInfo.getString("legalName"));
        openParam.put("PSPT_ADDR",  certificateInfo.getString("certificateAddr"));
        openParam.put("USER_TYPE_CODE", "0");
        openParam.put("PHONE", input.getString("msisdn"));
        openParam.put("PAY_NAME", certificateInfo.getString("legalName"));
        openParam.put("PAY_MODE_CODE", "0");
        openParam.put("ACCT_DAY", "1");
        openParam.put("USER_PASSWD", genNewPasswd());
        String product_id = goodsInfo.getString("goodsId");
        openParam.put("PRODUCT_ID", product_id);
        String brandCode = UProductInfoQry.getBrandCodeByProductId(goodsInfo.getString("goodsId"));
        openParam.put("BRAND_CODE", brandCode);
        openParam.put("TRADE_TYPE_CODE", "10");
        openParam.put("ORDER_TYPE_CODE", "10");
        openParam.put("TRADE_DEPART_PASSWD", "0");//孙鑫说没用到，但被校验不能空，所以随便赋值了
        openParam.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        openParam.put("ADVANCE_PAY", "-1");//预存款 2 0  为了避免出现多一条记录的情况，所以赋值为-1
        openParam.put("OPER_FEE", "0");//卡费  0 10
        openParam.put("FOREGIFT", "0");//押金费 1 0
        openParam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        openParam.put("AUTH_CODE", input.getString("authCode"));
        openParam.put("AUTH_CODE_EXPIRED", input.getString("authCodeExpired"));
		
        IDataset selecments = SelfTerminalUtil.getElements(product_id);
        openParam.put("SELECTED_ELEMENTS", selecments);
        
//        SIM_CARD_NO、IMSI、KI、SIM_CARD_TYPE、OPC_VALUE、SIM_CARD_TYPE、OPC_VALUE、
//        RES_KIND_NAME、RES_TYPE_CODE、SIM_FEE_TAG、SIM_CARD_SALE_MONEY
        
        openParam.put("RES_INFO_DATA", resInfoData);
        resInfoData.put("SIM_CARD_NO", input.getString("iccid"));
        resInfoData.put("IMSI", resInfoData.getString("IMSI"));
        resInfoData.put("KI", resInfoData.getString("KI"));
        resInfoData.put("SIM_CARD_TYPE", resInfoData.getString("SIM_CARD_TYPE"));
        resInfoData.put("OPC_VALUE", resInfoData.getString("OPC_VALUE"));
        resInfoData.put("RES_KIND_NAME", resInfoData.getString("RES_KIND_NAME"));
        resInfoData.put("RES_TYPE_CODE", resInfoData.getString("RES_TYPE_CODE"));
        resInfoData.put("SIM_FEE_TAG", SIM_FEE_TAG);
        resInfoData.put("SIM_CARD_SALE_MONEY", SIM_CARD_SALE_MONEY);
        
        //费用参数--携入开户无支付信息
        
        System.out.println("--------------openNpSvcNum----mqx-----------openParam="+openParam);
        IDataset results = CSAppCall.call("SS.CreateNpUserTradeRegSVC.tradeReg", openParam);
        System.out.println("--------------openNpSvcNum----mqx-----------results="+results);
        
        if(IDataUtil.isNotEmpty(results)&&StringUtils.isNotEmpty(results.getData(0).getString("ORDER_ID"))){
        	output.put("isSuc", "1");
        	
        	//记录日志
			IData logParam=new DataMap();
			logParam.put("TRANS_ACTION_ID", input.getString("transactionId"));
			logParam.put("CHANNEL_ID", input.getString("channelId",""));
			logParam.put("ORDER_ID", input.getString("orderId",""));
			logParam.put("OSP_ORDER_ID", input.getString("ospOrderId",""));
			logParam.put("CRM_ORDER_ID", results.getData(0).getString("ORDER_ID"));
			logParam.put("CRM_TRADE_ID", results.getData(0).getString("TRADE_ID"));
			logParam.put("OP_ID", getVisit().getStaffId());
			logParam.put("OP_ORG", getVisit().getDepartId());
			logParam.put("TRADE_TYPE_CODE", "10");
			logParam.put("SERIAL_NUMBER", input.getString("msisdn"));
//			logParam.put("ORDER_MONEY", paymentInfo.getString("totalFee","0"));
//			logParam.put("PAYMENT_MONEY", paymentInfo.getString("payment","0"));
			logParam.put("ORDER_MONEY", "0");
			logParam.put("PAYMENT_MONEY", "0");
			SelfTerminalBean selfBean=BeanManager.createBean(SelfTerminalBean.class);
			selfBean.addSelfLog(logParam);
        	
			//记录支付信息--携入开户无支付信息
			
        	return SelfTerminalUtil.responseSuccess(output); 
        }else{
        	output.put("isSuc", "2");
        	return SelfTerminalUtil.responseFail("开户失败",output);
        }
	}
    public static void main(String args[]){
		
		try {
			System.out.println("2019-04-30 11:27:44.0".replace(".0", ""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
	}
	/**
	 * 号码预占接口
	 * REQ201909040033_关于在白卡写卡开户界面加入批零价差话费礼包的功能
	 * @author k3
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData preOccupySvcNum2(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceNumber");
			IDataUtil.chkParam(input, "idCardType");
			IDataUtil.chkParam(input, "idCardNum");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		IData params=new DataMap();
		params.put("RES_NO", input.getString("serviceNumber"));//调资源接口需传预占号码
		params.put("RES_TRADE_CODE", "IRes_NetSel_MphoneCode");//普通网上选号 李全修改
		params.put("OCCUPY_TYPE_CODE", "1");//选占类型,1：网上选占
		params.put("RES_TYPE_CODE", "0");//0-号码
		params.put("USER_EPARCHY_CODE", CSBizBean.getUserEparchyCode());
		params.put("ROUTE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		params.put("PSPT_ID", input.getString("idCardNum"));//选占证件号码
		params.put("PSPT_TYPE", "");//选占证件类型，非必传
		params.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 受理地州
		params.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 受理业务区
		params.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 受理部门
		params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 受理员工
		params.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode()); // 接入渠道
		
		try{
			ResCall.resTempOccupyByNetSel(params);
		}catch(Exception e){
			output.put("isSuc", "1");
			return SelfTerminalUtil.responseFail(e.getMessage(),output);
		}
		output.put("isSuc", "0");
		return SelfTerminalUtil.responseSuccess(output);
	}
}
