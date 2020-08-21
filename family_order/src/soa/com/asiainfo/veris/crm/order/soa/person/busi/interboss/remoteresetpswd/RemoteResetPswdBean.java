package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remoteresetpswd;

import com.ailk.biz.util.StaticUtil;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;

public class RemoteResetPswdBean extends CSBizBean {
	/**
	 * 鉴权发起
	 * @param cycle
	 * @throws Exception
	 */
	public IDataset openResultAuthF(IData input) throws Exception{
		IData param = new DataMap();
		addOprNumb(param);
		param.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		param.put("ID_VALUE", input.getString("MOBILENUM"));
		param.put("ID_TYPE", "01");
		param.put("BIZ_TYPE", input.getString("BIZ_TYPE"));//跨区密码重置
		param.put("ID_CARD_TYPE", "00");//身份证
		param.put("ID_CARD_NUM", input.getString("PSPT_ID"));
		param.put("USER_NAME", input.getString("CUST_NAME"));
		param.put("BIZ_VERSION", "1.0.2");
		param.put("NUMBER_CHECK", input.getString("NUMBER_CHECK",""));
		param.put("CCPASSWD", input.getString("password",""));
		if("2".equals(input.getString("CHANGE_CARD_TAG"))){
			param.put("MESSAGE_CHECK", input.getString("MESSAGE_CHECK"));//验证码
			param.put("ICC_ID", input.getString("ICC_ID"));//sim卡
			param.put("BIZ_VERSION", "1.0.0");
		}
		IData result = new DataMap();
		result.put("RESULT", "0");
		//iboss 
		IData iResult = IBossCall.userAuth(param);
		if(IDataUtil.isNotEmpty(iResult)){
			if("0000".equals(iResult.getString("BIZ_ORDER_RESULT"))){
				String identCode = iResult.getString("IDENT_CODE");
				result.put("RESULT", "1");
				String reason = "";
				IDataset blResultInfos = iResult.getDataset("BL_RESULT_INFO");
				if(IDataUtil.isNotEmpty(blResultInfos)){
					IData blResultInfo = blResultInfos.getData(0);
					String busState = blResultInfo.getString("BUS_STATE");
					reason = blResultInfo.getString("REASON");
					result.put("BUS_STATE",busState);
					result.put("REASON",reason);
				}else{
					result.put("BUS_STATE","0");
					result.put("REASON","鉴权失败，归属省未按规范返回！"+iResult.getString("RESULT_DESC",iResult.getString("X_RSPDESC","")));
				}
				IDataset userInfos = iResult.getDataset("USER_INFO");
				if(IDataUtil.isEmpty(userInfos)){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "归属省未按规范正常返回（"+reason+"）");
				}
				IData userInfo = userInfos.getData(0);
				String custName=userInfo.getString("USER_NAME","");
	 			String level=userInfo.getString("LEVEL","");
	 			String brand=userInfo.getString("BRAND","");
	 			String userState=userInfo.getString("USER_STATUS","");
	 			String userStar=userInfo.getString("USER_STAR","");
	 			result.put("CUST_NAME",custName);
	 			result.put("USER_STATE",userState);
	 			result.put("BRAND_CODE",brand);
	 			result.put("LEVEL",level);
	 			result.put("PSPT_TYPE_CODE",input.getString("IDCARDTYPE",""));
	 			result.put("IDCARDNUM",input.getString("IDCARDNUM",""));
	 			result.put("IDENT_CODE", identCode);
	 			result.put("USER_STAR", userStar);
			}else{
				result.put("RESULT", "0");
				IDataset blResultInfos = iResult.getDataset("BL_RESULT_INFO");
				if(IDataUtil.isNotEmpty(blResultInfos)){
					IData blResultInfo = blResultInfos.getData(0);
					String busState = blResultInfo.getString("BUS_STATE");
					String reason = blResultInfo.getString("REASON");
					if(StringUtils.isBlank(reason)){
						reason = iResult.getString("RESULT_DESC",iResult.getString("X_RSPDESC",""));
					}
					result.put("BUS_STATE",busState);
					result.put("REASON",reason);
				}else{
					result.put("BUS_STATE","0");
					result.put("REASON","鉴权失败，归属省返回："+iResult.getString("RESULT_DESC",iResult.getString("X_RSPDESC","")));
				}
			}
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "鉴权出错！归属省未返回");
		}
		IDataset results = new DatasetList();
		results.add(result);
		return results;
	}
	
	/**
	 * 漫游省发起查询卡类型
	 * @param cycle
	 * @throws Exception
	 */
	public IDataset queryCardType(IData input) throws Exception{
		input.put("ID_VALUE", input.getString("SERIAL_NUMBER"));
		input.put("ID_TYPE", "01");
		input.put("BIZ_TYPE", input.getString("BIZ_TYPE"));//跨区密码重置
		addOprNumb(input);
		input.put("BIZ_VERSION", "1.0.0");
		IData result = new DataMap();
		try{
	        IData mebNumInfo = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER"),RouteInfoQry.getEparchyCodeBySn(input.getString("SERIAL_NUMBER")));//加上remove_tag 的条件
			if(IDataUtil.isNotEmpty(mebNumInfo)&&"00".equals(mebNumInfo.getString("NET_TYPE_CODE"))){
				result.put("result", "1");
				IDataset results = new DatasetList();
				results.add(result);
				return results;
			}
		}catch(Exception e){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询卡类型失败！"+e.getMessage());
		}
		IData iResult = IBossCall.queryCardTypeInfo(input);
		if(IDataUtil.isNotEmpty(iResult)){
			if("0000".equals(iResult.getString("BIZ_ORDER_RESULT"))){
				result.put("CARD_TYPE", iResult.getDataset("CARD_INFO").getData(0).getString("CARD_TYPE",""));
				result.put("IS_JXH", iResult.getDataset("CARD_INFO").getData(0).getString("IS_JXH",""));
				result.put("IS_SHIMING", iResult.getDataset("CARD_INFO").getData(0).getString("IS_SHIMING",""));
				result.put("USER_STATE", iResult.getDataset("CARD_INFO").getData(0).getString("USER_STATUS",""));
				result.put("PROVINCE_CODE", iResult.getDataset("CARD_INFO").getData(0).getString("HOME_PROV",""));
			}else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询卡类型失败！归属省返回："+iResult.getString("RESULT_DESC",iResult.getString("X_RSPDESC")));
			}
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询卡类型出错！归属省未返回原因");
		}
		IDataset results = new DatasetList();
		results.add(result);
		return results;
	}
	
	/**
	 * 查询卡类型落地接口
	 * @param cycle
	 * @throws Exception
	 */
	public IDataset cardTypeQuery(IData input) throws Exception{
		IDataset cardInfos =new DatasetList();
		IDataset results = new DatasetList();
		String serialNumber = IDataUtil.chkParam(input, "ID_VALUE");
		IDataUtil.chkParam(input, "ID_TYPE");
		IData result = new DataMap();
		result.put("BIZ_ORDER_RESULT", "0000");
		result.put("OPR_NUMB", input.getString("OPR_NUMB"));
		result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "ok");
		result.put("X_RSPTYPE", "0");
		result.put("X_RSPCODE", "0000");
		result.put("X_RSPDESC", "ok");
		//调用资源查询卡类型
		String isJXH = "";
		//是否吉祥号码
		IDataset jxhInfos = ResCall.querySerialNumberIsJXH(serialNumber);
		if(IDataUtil.isNotEmpty(jxhInfos)){
			IDataset outData = jxhInfos.getData(0).getDataset("OUTDATA");
			if(IDataUtil.isNotEmpty(outData)){
				String beautifualTag = outData.getData(0).getString("BEAUTIFUL_TAG");
				if("1".equals(beautifualTag)){
					isJXH = "0";
				}else{
					isJXH = "1";
				}
			}
		}
		if("".equals(isJXH)){
			result.put("BIZ_ORDER_RESULT", "4999");
			result.put("X_RESULTCODE", "4999");
			result.put("X_RESULTINFO", "失败");
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "2998");
			result.put("X_RSPDESC", "查询用户是否为吉祥号码失败！");
			result.put("RESULT_DESC", "查询用户是否为吉祥号码失败！");
			results.add(result);
			return results;
		}
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
		String status = getIBossUserStateParam(uca.getUser().getUserStateCodeset());
		String name = uca.getCustomer().getCustName();
		String lastName = name.substring(name.length()-1, name.length());
		String userName = "";
		String cardType = "0";
		for(int i = 0; i < name.length(); i++){
			if(i == name.length()-1){
				userName+=lastName;
			}else{
				userName+="x";
			}
		}
		//物联网号
		if(StringUtils.isNotBlank(uca.getBrandCode())&&StringUtils.equals("PWLW", uca.getBrandCode())){
			cardType="6";
    	}
		IData cardInfo = new DataMap();
		cardInfo.put("CARD_TYPE", cardType);
		cardInfo.put("IS_JXH", isJXH);
		cardInfo.put("IS_SHIMING","1".equals(uca.getCustomer().getIsRealName())?"0":"1");
		cardInfo.put("HOME_PROV","898");
		cardInfo.put("USER_NAME", userName);
		cardInfo.put("USER_STATUS",status);
		cardInfo.put("PROVINCE","海南");
		cardInfo.put("CITY", StaticUtil.getStaticValue("JOB_CALL_CITYCODE", uca.getUser().getCityCode()));
		cardInfos.add(cardInfo);
		result.put("CARD_INFO",cardInfos);
		results.add(result);
		return results;
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

	/**
	 * 跨区密码重置落地
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset passwordCZ(IData input) throws Exception{
		String passwd = IDataUtil.chkParam(input, "CCPASSWD");
		String serialNumber = IDataUtil.chkParam(input, "ID_VALUE");
		String identCode = IDataUtil.chkParam(input, "IDENT_CODE");
		IDataset results = new DatasetList();
		IData result = new DataMap();
		result.put("ID_TYPE", input.getString("ID_TYPE",""));
		result.put("ID_VALUE", input.getString("ID_VALUE",""));
		result.put("OPR_NUMB", input.getString("OPR_NUMB",""));
		result.put("BIZ_ORDER_RESULT", "0000");
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "ok");
		result.put("X_RSPTYPE", "0");
		result.put("X_RSPCODE", "0000");
		result.put("X_RSPDESC", "ok");
		// 身份凭证鉴权
        IDataset userAuth = UserIdentInfoQry.queryIdentInfoByCode(identCode, serialNumber);
        if(IDataUtil.isEmpty(userAuth)){
        	result.put("BIZ_ORDER_RESULT", "3018");
        	result.put("RESULT_DESC", "用户身份凭证错误");
        	result.put("X_RESULTCODE", "3018");
    		result.put("X_RESULTINFO", "用户身份凭证错误");
    		result.put("X_RSPTYPE", "2");
    		result.put("X_RSPCODE", "3018");
    		result.put("X_RSPDESC", "用户身份凭证错误");
        	results.add(result);
        	return results;
        }else {
        	IData identInfo = userAuth.getData(0);
        	String isValidate = identInfo.getString("TAG");
        	if("EXPIRE".equals(isValidate)){
        		result.put("BIZ_ORDER_RESULT", "3018");
            	result.put("RESULT_DESC", "用户身份凭证已失效/过期");
                result.put("X_RESULTCODE", "3018");
                result.put("X_RESULTINFO", "用户身份凭证已失效/过期");
     			result.put("X_RSPCODE", "3018");
     			result.put("X_RSPDESC", "用户身份凭证已失效/过期");
     			result.put("X_RSPTYPE", "2");
     			results.add(result);
            	return results;
        	}
		}
        //密码重置入参
		IData param = new DataMap();
		param.put("NEW_PASSWD", passwd);
		param.put("PASSWD_TYPE", "1");
		param.put("TRADE_TYPE_CODE", "73");
		if("01".equals(input.getString("ID_TYPE",""))){
			param.put("SERIAL_NUMBER", serialNumber);
		}
		
		try{
			IDataset modifyResults = CSAppCall.call("SS.ModifyUserPwdInfoRegSVC.tradeReg", param);
			//办理成功 记录发起端
			
			//发送业务成功短信
//			IData data = new DataMap();
//			data.put("SERIAL_NUMBER", input.getString("ID_VALUE"));
//			data.put("RECV_OBJECT", input.getString("ID_VALUE"));
//			data.put("USER_ID", modifyResults.getData(0).getString("USER_ID"));
//			data.put("NOTICE_CONTENT1", "尊敬的客户，您已成功办理服务密码重置业务，服务密码关系您个人信息安全，请注意保护。");
//			sendSMS(data,getVisit().getStaffEparchyCode());
			
			results.add(result);
			return results;
		}catch(Exception e){
			String errorMsg = e.getMessage();
			result.put("BIZ_ORDER_RESULT", "2998");
			result.put("RESULT_DESC", errorMsg);
			result.put("X_RESULTCODE", "2998");
    		result.put("X_RESULTINFO", errorMsg);
    		result.put("X_RSPTYPE", "2");
    		result.put("X_RSPCODE", "2998");
    		result.put("X_RSPDESC", errorMsg);
        	results.add(result);
			return results;
		}
	}
	
	/**
	 * 跨区密码重置发起
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset passwordCZF(IData input) throws Exception{
		//归属省入参
		IData inParam = new DataMap();
		inParam.put("CCPASSWD", input.getString("PASSWORD",""));
		inParam.put("ID_TYPE", "01");
		inParam.put("ID_VALUE", input.getString("MOBILENUM"));
		inParam.put("IDENT_CODE", input.getString("IDENT_CODE"));
		inParam.put("CHANNEL", input.getString("CHANNEL"));//CRM/BOSS(由系统发起)
		addOprNumb(inParam);
		inParam.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		inParam.put("BIZ_VERSION", "1.0.0");
		//iboss
		IData iResult = IBossCall.passwordCZ(inParam);
		if(IDataUtil.isNotEmpty(iResult)){
			if("0000".equals(iResult.getString("BIZ_ORDER_RESULT"))){
				//密码重置漫游省入参
				IData param = new DataMap();
				param.put("PSPT_ID", input.getString("IDCARDNUM",""));
				param.put("PSPT_TYPE_CODE", "00");
				param.put("CUST_NAME", input.getString("CUST_NAME"));
				param.put("SERIAL_NUMBER", input.getString("MOBILENUM"));
//				param.put("TRANSACTION_ID", input.getString("TRANSACTION_ID"));
				param.put("PASSWORD", input.getString("PASSWORD",""));
				param.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
				//漫游省记录客户资料到主台账 生成业务受理单
				IDataset results = CSAppCall.call("SS.RemoteResetPswdRegSVC.tradereg", param);
				return results;
			}else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "密码重置办理失败！归属省返回：："+iResult.getString("RESULT_DESC",iResult.getString("X_RSPDESC")));
			}
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "密码重置办理失败！归属省返回：："+iResult.getString("RESULT_DESC",iResult.getString("X_RSPDESC")));
		}
		return new DatasetList();
	}

	
	/**
	 * 发送短信
	 * @param data
	 * @param staffEparchyCode
	 * @return 
	 * @throws Exception 
	 */
	private int sendSMS(IData data, String eparchyCode) throws Exception {
		// TODO Auto-generated method stub
		IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("IN_MODE_CODE", "0");
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        param.put("USER_ID", data.getString("USER_ID"));
        param.put("NOTICE_CONTENT", data.getString("NOTICE_CONTENT1"));
        param.put("PRIORITY", "50");
        param.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        param.put("REMARK", "跨区密码重置发送办理成功短信");
        String seq = SeqMgr.getSmsSendId();// DualMgr.getSeqId(pd,"seq_smssend_id");
        long seq_id = Long.parseLong(seq);
        long partition_id = seq_id % 1000;
        param.put("SEQ_ID", seq);
        param.put("PARTITION_ID", partition_id + "");
        String sql = "INSERT INTO ti_o_sms(sms_notice_id,partition_id,send_count_code,refered_count,eparchy_code,in_mode_code," + "chan_id,recv_object_type,recv_object,recv_id,sms_type_code,sms_kind_code,"
                + "notice_content_type,notice_content,force_refer_count,sms_priority,refer_time," + "refer_staff_id,refer_depart_id,deal_time,deal_state,remark,send_time_code,send_object_code)"
                + " values (:SEQ_ID,:PARTITION_ID,'1','0',:EPARCHY_CODE,:IN_MODE_CODE,'11','00',:SERIAL_NUMBER,:USER_ID," + " '20','02','0',:NOTICE_CONTENT,1,:PRIORITY,sysdate,:STAFF_ID,:DEPART_ID,sysdate,'15',:REMARK,1,6)";

        return Dao.executeUpdate(new StringBuilder(sql), param);
	}
	public IData addOprNumb(IData inparam) throws Exception
	 {
	     //补充操作流水号
		 String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		 String seqRealId = SeqMgr.getRealId();
		 inparam.put("OPR_NUMB", "COP"+"898"+ date + seqRealId);
	     return inparam;
	 }
	/**
	 * 跨区换卡下发短信 k3
	 * @param data
	 * @param staffEparchyCode
	 * @return 
	 * @throws Exception 
	 */
	public IDataset simpleCardNotice(IData input) throws Exception{
		IDataset rtn=new DatasetList();
		IData param = new DataMap();
		addOprNumb(param);
		param.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		param.put("ID_VALUE", input.getString("MOBILENUM"));
		param.put("ID_TYPE", "01");
		param.put("BIZ_TYPE", input.getString("BIZ_TYPE"));//跨区换卡1016
		param.put("SEND_MARK", "0");
		param.put("BIZ_VERSION", "1.0.0");
		IData result = new DataMap();
		result.put("RESULT", "0");
		//iboss 
		IData iResult = IBossCall.remoteSendSms(param);
		if(IDataUtil.isNotEmpty(iResult)){
			String rtnDesc = iResult.getString("RESULT_DESC",iResult.getString("X_RSPDESC"));
			if("0000".equals(iResult.getString("BIZ_ORDER_RESULT"))){
				result.put("RESULT", "1");
			}else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "跨区换卡下发短信失败："+rtnDesc);
			}
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "跨区换卡下发短信失败！");
		}
		rtn.add(result);
		return rtn;
	}
	/**
	 * 好友号码查询
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset numCheckQuery(IData input)throws Exception{
		IDataset rtnList = new DatasetList();
		IData rtnMap=new DataMap();
		IData ibossParam=new DataMap();
		String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		String seqRealId = SeqMgr.getRealId();
		ibossParam.put("OPR_NUMB", "COP"+"898"+ date + seqRealId);
		ibossParam.put("ID_VALUE", input.getString("SERIAL_NUMBER"));
		ibossParam.put("ID_TYPE", "01");
		ibossParam.put("BIZ_TYPE", input.getString("BIZ_TYPE"));
		ibossParam.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		ibossParam.put("BIZ_VERSION", "1.0.0");
		IData iResult = IBossCall.numCheckQuery(ibossParam);
		if(IDataUtil.isNotEmpty(iResult)){
			String rtnCode = iResult.getString("BIZ_ORDER_RESULT");
			if("0000".equals(rtnCode)){
				rtnMap.put("RSP_CODE", "00");
				rtnMap.put("NUM_COUNT", iResult.getString("NUM_COUNT"));
			}else {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "好友查询失败："+iResult.getString("RESULT_DESC",iResult.getString("X_RSPDESC")));
			}
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "好友查询异常！归属省未返回");
		}
		rtnList.add(rtnMap);
		return rtnList;
	}
}
