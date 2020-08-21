package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class GrpVideoTwoCheckIntfBean extends CSBizBean
{
	private static final transient Logger logger = Logger.getLogger(GrpVideoTwoCheckIntfBean.class);

	private static String productSpecCode = "";

	private static  String userId="";

	public static IData dealSpecialGrpVideo(IData input)throws Exception{
		IData result = new DataMap();
		if(logger.isDebugEnabled()){
			logger.debug("=========dealSpecialGrpVideo=========input"+input);
		}
		String productOfferId = input.getString("PRODUCTID", "");
		String sn = input.getString("SERIAL_NUMBER", "");

		String  payFlag = "";

		IDataset UserEcrecepProductInfoList = UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(productOfferId);
		IDataset  productUserInfoList = UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(productOfferId);
		if(IDataUtil.isNotEmpty(UserEcrecepProductInfoList)){//集客大厅处理
			productSpecCode = UserEcrecepProductInfoList.getData(0).getString("PRODUCT_SPEC_CODE","");
		}else if(IDataUtil.isNotEmpty(productUserInfoList)){//BBOSS处理
			productSpecCode = productUserInfoList.getData(0).getString("PRODUCT_SPEC_CODE","");
			userId = productUserInfoList.getData(0).getString("USER_ID","");
			//	productId =  productUserInfoList.getData(0).getString("USER_ID","");
		}else{
			CSAppException.apperr(ProductException.CRM_PRODUCT_141, IntfField.BbossGrpMemBizRet.ERR11[0], IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);
		}

		// 企业视频彩铃特殊处理  其他不处理
		if (!"910401".equals(productSpecCode))//
		{
			result.put("IS_NEED_CONTINUE","0");//不影响其他业务
			return result;
		}
		//第二次处理标记 即是二次短信回复确认之后，不需要再次处理
		if("1".equals(input.getString("IS_SECOND_DEAL",""))){
			result.put("IS_NEED_CONTINUE","0");//不影响其他业务
			return result;
		}

		//校验号码状态
		IData checkMemberStatus = checkMemberStatus(sn,productOfferId);
		if(IDataUtil.isNotEmpty(checkMemberStatus)&&"00".equals(checkMemberStatus.getString("RSP_CODE"))){

		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, checkMemberStatus.getString("RSP_CODE")+checkMemberStatus.getString("RSP_DESC"));
//			result.putAll(checkMemberStatus);
//			result.put("IS_NEED_CONTINUE","1");//号码状态异常，拦截
//			return result;
		}
		//互斥规则校验
		IData checkResult = checkGrpVideoAddVoLTERule(sn,productSpecCode);
		if(IDataUtil.isNotEmpty(checkResult)&&"00".equals(checkResult.getString("RSP_CODE"))){

		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, checkResult.getString("RSP_CODE")+checkResult.getString("RSP_DESC"));
//			result.putAll(checkResult);
//			result.put("IS_NEED_CONTINUE","1");//互斥规则校验失败，拦截
//			return result;
		}
		//获取产品标识 ：统付标识
		payFlag= GrpCommonBean.getAttrValueByAttrCode(userId,"9104010008");
		if(logger.isDebugEnabled()){
			logger.debug("=========dealSpecialGrpVideo=========payFlag"+payFlag);
		}
		// 企业视频彩铃统付不往下走做二次短信确认
		if ("910401".equals(productSpecCode) &&!"02".equals(payFlag) )//
		{
			result.put("IS_NEED_CONTINUE","0");//不影响其他业务
			return result;
		}

		//需二次短信确认
		String tradeTypeCode = "";

//		//获取trade_type_code
//		IDataset ids = AttrBizInfoQry.getBizAttrByIdTypeObjCodeEparchy(offerId, "P", BizCtrlType.CreateMember, "TradeTypeCode","ZZZZ",null);
//		if(IDataUtil.isNotEmpty(ids)){
//			tradeTypeCode = ids.getData(0).getString("ATTR_VALUE", "");
//		}
		IDataset config = CommparaInfoQry.getCommparaAllCol("CSM", "2019", "GRPVIDEO_SEND_SMS", "ZZZZ");
		if(IDataUtil.isEmpty(config)){
			tradeTypeCode = "";
		}else{
			tradeTypeCode=config.getData(0).getString("PARA_CODE1","");
		}

		twoCheck(sn,tradeTypeCode,input);

		result.put("IS_NEED_CONTINUE","1");//拦截，等待回复
		result.put("IS_NEED_INSERT","1");//拦截，等待回复,不需要入表TF_TP_BBOSS_XML_INFO

		// 4- 返回受理成功标志
		result.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
		result.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
		result.put("RSP_CODE", "00");
		result.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
		result.put("TRADE_ID", SeqMgr.getTradeId());
		return  result ;
	}
	public  static IData checkGrpVideoAddVoLTERule(String memSerialNumber ,String productSpecCode)throws Exception{
		if (logger.isDebugEnabled()) {
			logger.debug(">>>>>>>>>>>>>>>>>>进入CheckGrpVideoAddVoLTERule判断>>>>>>>>>>>>>>>>>>");
		}
		IData resultMap = new DataMap();
		IData inparams = new DataMap();

		resultMap.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
		resultMap.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
		resultMap.put("RSP_CODE", "00");
		resultMap.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
		//4- 企业视屏彩铃 ，与VOL依赖 、与一号通互斥
		//String memUserId = memUserInfo.getString("USER_ID");
		IDataset memberUserInfoList = UserInfoQry.getUserInfoBySn(memSerialNumber, "0");
		if (IDataUtil.isEmpty(memberUserInfoList)&&IDataUtil.isEmpty(memberUserInfoList.getData(0))){
			resultMap.put("SERIAL_NUMBER",memSerialNumber);
			resultMap.put("RSP_CODE","13");
			resultMap.put("RSP_DESC","用户状态不正常");
			return resultMap;
		}
		boolean flag = true ;
		String memUserId = memberUserInfoList.getData(0).getString("USER_ID");
		//String netTypeCode = memberUserInfoList.getData(0).getString("NET_TYPE_CODE");
		IDataset userProducts = UserProductInfoQry.queryMainProduct(memUserId);

		/**
		 * 海南以品牌区分不同的固话：
   			1、 集客IMS固话 ： IMSG
   			2、家庭IMS固话  ：IMSP
   			3、铁通固话：TT01	温情家话;TT02	商务电话甲种;TT03	海岛商话;TT04	商务电话乙种;TT05	时尚E话;TDTT	TD无线座机（铁通）
		 */
		if("IMSG,IMSP,TT01,TT02,TT03,TT04,TT05,TDTT".contains(userProducts.getData(0).getString("BRAND_CODE"))){
			flag = false;
		}

		//固话号码不进行VoLTE服务判断
		if ("910401".equals(productSpecCode)&&flag)
		{
			// 4-1- 根据成员用户编号查询UU关系表，查看该成员是否开通了VoLTE服务
			IDataset userSvcInfo = UserSvcInfoQry.queryUserSvcByUserId(memUserId, "889", CSBizBean.getUserEparchyCode());
			if(IDataUtil.isEmpty(userSvcInfo)){
				resultMap.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
				resultMap.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
				resultMap.put("SERIAL_NUMBER", memSerialNumber);
				resultMap.put("RSP_CODE", "99");
				resultMap.put("RSP_DESC", "用户未开通VoLTE服务，不能订购企业视频彩铃业务!");
				return resultMap;
			}

			// 4-2- 根据成员用户编号查询UU关系表，查看该成员是否开通了一号通产品
			inparams.put("USER_ID_B",memUserId);
			inparams.put("RELATION_TYPE_CODE","E2");
			IDataset oneCardUserRelaList = CSAppCall.call("CS.RelaUUInfoQrySVC.getRelaUUInfoByUserRelarIdB",inparams);
			if (!IDataUtil.isEmpty(oneCardUserRelaList))
			{
				resultMap.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
				resultMap.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
				resultMap.put("SERIAL_NUMBER", memSerialNumber);
				resultMap.put("RSP_CODE", "12");
				resultMap.put("RSP_DESC", "企业视屏彩铃与一号通产品互斥，不能订购该产品!");
				return resultMap;
			}
		}
		return resultMap;
	}

	public static IData checkMemberStatus(String memSerialNumber,String offerId) throws Exception{
		IData resultMap = new DataMap();
		resultMap.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
		resultMap.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
		resultMap.put("RSP_CODE", "00");
		resultMap.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
		// 1- 集团用户信息不存在，说明本省非用户开展，工单无法开通
		IData inparams = new DataMap();
//		String offerId = memberInfo.getString("PRODUCTID", "");
//		String memSerialNumber = memberInfo.getString("SERIAL_NUMBER","");
		inparams.put("PRODUCT_OFFER_ID", offerId);
		IDataset userProductInfoList = UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(offerId);
		if (IDataUtil.isEmpty(userProductInfoList))
		{
			resultMap.put("SERIAL_NUMBER", memSerialNumber);
			resultMap.put("RSP_CODE", "99");
			resultMap.put("RSP_DESC", "成员归属省不在业务开展省内，无法同步成员签约关系");
			resultMap.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
			resultMap.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
			return resultMap;
		}
		//IData memUserInfo = memberUserInfoList.getData(0);
		IData userProductInfo = userProductInfoList.getData(0);
		productSpecCode = userProductInfo.getString("PRODUCT_SPEC_CODE", "");
		// 2- 成员用户信息不存在，工单无法开通
		inparams.clear();
		inparams.put("SERIAL_NUMBER", memSerialNumber);
		inparams.put("REMOVE_TAG", "0");
		IDataset memberUserInfoList = CSAppCall.call("CS.UserInfoQrySVC.getUserInfoBySnNoProduct",inparams);

		//非本省成员&&集团业务不允许外省成员开通 的情况下返回用户状态不正常
		if (IDataUtil.isEmpty(memberUserInfoList)&& IDataUtil.isEmpty(memberUserInfoList.getData(0)))
		{
			resultMap.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
			resultMap.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
			resultMap.put("SERIAL_NUMBER", memSerialNumber);
			resultMap.put("RSP_CODE", "13");
			resultMap.put("RSP_DESC", "用户状态不正常");
			return resultMap;

		}

		if (IDataUtil.isNotEmpty(memberUserInfoList)){
			IData memUserInfo = memberUserInfoList.getData(0);
			String memUserId = memUserInfo.getString("USER_ID");
			String userStateCodeset = memUserInfo.getString("USER_STATE_CODESET", "0");

			if(!"0".equals(userStateCodeset)){
				resultMap.put("SERIAL_NUMBER", memSerialNumber);
				resultMap.put("RSP_CODE", "13");
				resultMap.put("RSP_DESC", "用户状态不正常");
				resultMap.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
				resultMap.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
				return resultMap;
			}

			// 3- 判断成员号码是否欠费
			IData userOwnFee = AcctCall.getOweFeeByUserId(memUserId);
			if (userOwnFee.getInt("ACCT_BALANCE", 0) < 0){
				resultMap.put("SERIAL_NUMBER", memSerialNumber);
				resultMap.put("RSP_CODE", "99");
				resultMap.put("RSP_DESC", "该号码已欠费:"+memSerialNumber);
				resultMap.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
				resultMap.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
				return resultMap;
			}
		}

		return resultMap;
	}

	public static void twoCheck(String memSerialNumber,String tradeTypeCode,IData input) throws Exception{

		IData smsData = new DataMap();
		//尊敬的客户，您好！@{COMPANY_NAME}单位将为您开通企业视频彩铃业务，资费**元/月，立即生效，24小时内回复“是”确认开通，回复其他内容和不回复则不开通。中国移动
		String smsContent = "";
		String companyName="";
		IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
		if(IDataUtil.isNotEmpty(userInfo)){
			IData custInfos = UcaInfoQry.qryCustomerInfoByCustIdForGrp(userInfo.getString("CUST_ID"));
			if(IDataUtil.isNotEmpty(custInfos)){
				companyName = custInfos.getString("CUST_NAME");
			}
			if(logger.isDebugEnabled()){
				logger.debug("========twoCheck==========userInfo==="+userInfo);
				logger.debug("========twoCheck==========custInfos==="+custInfos);
			}
		}
		IDataset config = CommparaInfoQry.getCommparaAllCol("CSM", "2019", "GRPVIDEO_SEND_SMS", "ZZZZ");
		if(IDataUtil.isEmpty(config)){
			smsContent = "";
		}else{
			smsContent=config.getData(0).getString("PARA_CODE20","");
		}
		smsContent = smsContent.replaceAll("@\\{COMPANY_NAME\\}", companyName);
		smsData.put("SERIAL_NUMBER", memSerialNumber);
		smsData.put("SMS_CONTENT", smsContent);
		smsData.put("OPR_SOURCE", "1");
		smsData.put("SMS_TYPE", BofConst.GRP_BUSS_SEC);
		IData preData = new DataMap(input);
		preData.put("SERIAL_NUMBER", memSerialNumber);
		preData.put("IS_SECOND_DEAL", "1");//新加参数标记二次短信确认后
		preData.put("TRADE_TYPE_CODE", tradeTypeCode);
		preData.put("TRADE_NAME", "企业视频彩铃业务");
		preData.put("PRE_TYPE", BofConst.GRP_BUSS_SEC);
		preData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		preData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
		preData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		preData.put("SVC_NAME", "CS.SynMebWordOrderSVC.dealMebWordOrder");
		//preData.put("BMC_TWOCHECK_CODE", "10086510");
		TwoCheckSms.twoCheck(tradeTypeCode, 24, preData, smsData);
	}


}
