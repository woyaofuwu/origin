package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct;

import java.util.Calendar;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import com.ailk.biz.BizVisit;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.ailk.service.bean.BaseBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdAssistant;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.SmsVerifyCodeBean;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.UserPasswordInfoComm;
import com.asiainfo.veris.crm.order.soa.person.common.util.IdcardUtils;

/**
 * 用户鉴权类
 */
public class UserAuthSVC extends CSBizService{
	private static final Logger log= Logger.getLogger(UserAuthSVC.class);
	/**
	 * 鉴权接口 BIZ_TYPE区分
	 * @param input
	 * @return
	 * @throws Exception
	 */
  	public IDataset userAuth(IData input) throws Exception {
		String bizType = IDataUtil.chkParam(input, "BIZ_TYPE");
		if("1011".equals(bizType)){
			return openResultAuth(input);
		}else if("1012".equals(bizType)){
			return writeCardNumberCheck(input);
		}else if("1001".equals(bizType)){//信用停机鉴权
			input.put("SERIAL_NUMBER", input.getString("ID_VALUE"));
			return CSAppCall.call("SS.CreditRoamingSVC.CreditPreOpen",input);
		}else if ("1013".equals(bizType) || "1014".equals(bizType)) {//异地销户
			return userDestroyAuth(input);
		}else if ("1010".equals(bizType)) {//异地停复机
			return stopOpenMobile(input);
		}else if("1016".equals(bizType)){//跨区换卡验证码+sim卡验证 落地
			return messageAndICCIDCheck(input);
		}else if("1017".equals(bizType)){//跨区销户前校验
			return destroyBeforeCheck(input);
		}else{
			return null;
		}
	}
	
	
	
	private IDataset stopOpenMobile(IData input) throws Exception{
		boolean nameCheck = false;
		boolean cardCheck = false;
		String serialNumber = IDataUtil.chkParam(input, "ID_VALUE");
		IDataset results = new DatasetList();
		IData resultData = new DataMap();
		resultData.put("ID_TYPE", "01");
		resultData.put("ID_VALUE", serialNumber);
		resultData.put("OPR_NUMB", input.getString("OPR_NUMB"));
		resultData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		resultData.put("BIZ_ORDER_RESULT", "0000");

		input.put("SERIAL_NUMBER", serialNumber);
		input.put("REMOVE_TAG", "0");
		String idType = IDataUtil.chkParam(input, "ID_TYPE");
		String oprNumb = IDataUtil.chkParam(input, "OPR_NUMB");
		String bizType = IDataUtil.chkParam(input, "BIZ_TYPE");
		String userName = input.getString("USER_NAME");
		String passWD = input.getString("CCPASSWD");
		String idCardType = input.getString("ID_CARD_TYPE");
		String idCardNum = input.getString("ID_CARD_NUM");
		IDataUtil.chkParam(input, "OPR_TIME");
		IDataUtil.chkParam(input, "BIZ_VERSION");
		input.put("USER_PASSWD", passWD);

		IData conditionInfo = new DataMap();
		IData blResultInfo = new DataMap();
		blResultInfo.put("CONDITION", "0");
		blResultInfo.put("BUS_STATE", "1");
		UcaData uca = new UcaData();
		try{
			uca = UcaDataFactory.getNormalUca(serialNumber);
		}catch(Exception e){
			resultData.put("X_RESULTCODE", "2009");
    		resultData.put("X_RESULTINFO", "用户不存在或用户非正常状态");
    		resultData.put("X_RSPTYPE", "2");
    		resultData.put("X_RSPCODE", "2998");
    		resultData.put("X_RSPDESC", "用户不存在或用户非正常状态");
			resultData.put("BIZ_ORDER_RESULT", "2009");
			resultData.put("RESULT_DESC", "用户不存在");
			blResultInfo.put("BUS_STATE", "0");//办理状态(0：不可办理1：可办理)
    	    blResultInfo.put("CONDITION", "1");//办理依赖条件(当BusState为0时填写0：无1：有)
    	    blResultInfo.put("REASON", "用户不存在！");//不能办理原因说明
    	    conditionInfo.put("INFO_CODE", "nameIsSame");
    	    conditionInfo.put("INFO_VALUE", "1");
    	    IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
    	    IDataset blResultInfos = new DatasetList();
    	    blResultInfos.add(blResultInfo);
    	    resultData.put("BL_RESULT_INFO", blResultInfos);
			results.add(resultData);
			return results;
		}
		
		//如果用户处于非0的非正常状态，那么当然是不能办理该业务的
		if(uca == null) {
			resultData.put("X_RESULTCODE", "2009");
    		resultData.put("X_RESULTINFO", "用户不存在或用户非正常状态");
    		resultData.put("X_RSPTYPE", "2");
    		resultData.put("X_RSPCODE", "2998");
    		resultData.put("X_RSPDESC", "用户不存在或用户非正常状态");
			resultData.put("BIZ_ORDER_RESULT", "2009");
			resultData.put("RESULT_DESC", "用户不存在");
			blResultInfo.put("BUS_STATE", "0");//办理状态(0：不可办理1：可办理)
    	    blResultInfo.put("CONDITION", "1");//办理依赖条件(当BusState为0时填写0：无1：有)
    	    blResultInfo.put("REASON", "用户不存在！");//不能办理原因说明
    	    conditionInfo.put("INFO_CODE", "nameIsSame");
    	    conditionInfo.put("INFO_VALUE", "1");
    	    IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
    	    IDataset blResultInfos = new DatasetList();
    	    blResultInfos.add(blResultInfo);
    	    resultData.put("BL_RESULT_INFO", blResultInfos);
			results.add(resultData);
			return results;
		}

		if (StringUtils.isEmpty(idCardNum)) {
			resultData.put("X_RESULTCODE", "3030");
    		resultData.put("X_RESULTINFO", "证件号码不能为空");
    		resultData.put("X_RSPTYPE", "2");
    		resultData.put("X_RSPCODE", "2998");
    		resultData.put("X_RSPDESC", "证件号码不能为空");
			resultData.put("BIZ_ORDER_RESULT", "3030");
			resultData.put("RESULT_DESC","证件号码不能为空"); 
			blResultInfo.put("BUS_STATE", "0");//办理状态(0：不可办理1：可办理)
    	    blResultInfo.put("CONDITION", "1");//办理依赖条件(当BusState为0时填写0：无1：有)
    	    blResultInfo.put("REASON", "证件号码不能为空!");//不能办理原因说明
    	    conditionInfo.put("INFO_CODE", "nameIsSame");
    	    conditionInfo.put("INFO_VALUE", "1");
    	    IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
    	    IDataset blResultInfos = new DatasetList();
    	    blResultInfos.add(blResultInfo);
    	    resultData.put("BL_RESULT_INFO", blResultInfos);
			results.add(resultData);
			return results;
         } 
		//客服密码校验
		if(StringUtils.isNotEmpty(passWD)){
			IData data = new DataMap();
			data.put("SERIAL_NUMBER", uca.getSerialNumber());
			data.put("USER_PASSWD", passWD);
			IData checkResut = UserPasswordInfoComm.checkUserPWDForL2F(data);
			if (!StringUtils.equals(checkResut.getString("X_CHECK_INFO"), "0"))
			{
				if(!checkFailedCount(uca,input.getString("BIZ_TYPE"))){
		        	resultData.put("BIZ_ORDER_RESULT", "2046");
				    resultData.put("RESULT_DESC", "鉴权失败超5次，系统已锁定！");
				    resultData.put("X_RESULTCODE", "2046");
		    	    resultData.put("X_RESULTINFO", "超过次数锁定");
		    	    resultData.put("X_RSPTYPE", "2");
		    	    resultData.put("X_RSPCODE", "2998");
		    	    resultData.put("X_RSPDESC", "失败");
		    	    blResultInfo.put("BUS_STATE", "0");//办理状态(0：不可办理1：可办理)
		    	    blResultInfo.put("CONDITION", "1");//办理依赖条件(当BusState为0时填写0：无1：有)
		    	    blResultInfo.put("REASON", "鉴权失败超5次，系统已锁定！");//不能办理原因说明
		    	    conditionInfo.put("INFO_CODE", "nameIsSame");
		    	    conditionInfo.put("INFO_VALUE", "1");
		    	    IDataset extendInfos = new DatasetList();
		            extendInfos.add(conditionInfo);
		            blResultInfo.put("EXTEND_INFO", extendInfos);
		    	    IDataset blResultInfos = new DatasetList();
		    	    blResultInfos.add(blResultInfo);
		    	    resultData.put("BL_RESULT_INFO", blResultInfos);
				    results.add(resultData);
				    return results;
				}
				resultData.put("X_RSPTYPE", "2");
	      		resultData.put("X_RSPCODE", "2998");
	      		resultData.put("X_RSPDESC", checkResut.getString("X_RESULTINFO"));
				resultData.put("X_RESULTCODE", "2036");
	      		resultData.put("X_RESULTINFO", checkResut.getString("X_RESULTINFO"));
	        	resultData.put("BIZ_ORDER_RESULT", "2036");
	        	resultData.put("RESULT_DESC",checkResut.getString("X_RESULTINFO")); 
	    		blResultInfo.put("REASON", "客服密码验证不通过");
	    		blResultInfo.put("BUS_STATE", "0");
	    		blResultInfo.put("CONDITION", "1");
	    		conditionInfo.put("INFO_CODE", "nameIsSame");
	    		conditionInfo.put("INFO_VALUE", "1");
	    		IDataset extendInfos = new DatasetList();
	            extendInfos.add(conditionInfo);
	            blResultInfo.put("EXTEND_INFO", extendInfos);
	            IDataset blResultInfos = new DatasetList();
	    		blResultInfos.add(blResultInfo);
	    		resultData.put("BL_RESULT_INFO", blResultInfos);
	        	results.add(resultData);
				return results;
			}
		}
		String ibossPsptTypeCode = this.getIBossPsptTypeParam(uca.getCustomer().getPsptTypeCode());
        //如果要验证证件号码是否一致的前面要先验证证件号码的类型是否一致  
          if (StringUtils.equals(idCardType,ibossPsptTypeCode ) 
          		&& StringUtils.equals(idCardNum, uca.getCustomer().getPsptId())) { 
        	  cardCheck = true;
        	  if(StringUtils.equals(userName,uca.getCustomer().getCustName())){
            		 nameCheck = true;
            	 }
          }
		String userStatus = getIBossUserStateParam(uca.getUser().getUserStateCodeset());
        boolean isCorrectStatus = true;
        String errorMsg = "";
        if (StringUtils.equals(userStatus, "00")) {
        	isCorrectStatus = true;
        	errorMsg = "ok";
        } else if(StringUtils.equals(userStatus, "03")) {
        	isCorrectStatus = false;
        	errorMsg = "用户已预销户！";
        } else if(StringUtils.equals(userStatus, "04")) {
        	isCorrectStatus = false;
        	errorMsg = "用户已销户！";
        }else if(StringUtils.equals("5", uca.getUser().getUserStateCodeset())){
        	isCorrectStatus = false;
        	errorMsg = "用户已欠费停机！";
        }
        
        if (!isCorrectStatus) {
        	resultData.put("RESULT_DESC", errorMsg);
        	resultData.put("BIZ_ORDER_RESULT", "2005");//2005:鉴权失败（欠费停机）
			resultData.put("X_RESULTCODE", "2005");
        	resultData.put("X_RESULTINFO", errorMsg);
      		resultData.put("X_RSPTYPE", "2");
      		resultData.put("X_RSPCODE", "2998");
      		resultData.put("X_RSPDESC", errorMsg);
    		blResultInfo.put("REASON", "用户状态非正常");
    		blResultInfo.put("BUS_STATE", "0");
    		blResultInfo.put("CONDITION", "1");
    		conditionInfo.put("INFO_CODE", "nameIsSame");
    		conditionInfo.put("INFO_VALUE", "1");
    		IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
        	resultData.put("RESULT_DESC", errorMsg);
        	IDataset blResultInfos = new DatasetList();
    		blResultInfos.add(blResultInfo);
    		resultData.put("BL_RESULT_INFO", blResultInfos);
        	results.add(resultData);
			return results;
        }
        if(cardCheck){
      	   //证件号码校验通过
      	   if(nameCheck){
      		   //客户姓名通过
      		   conditionInfo.put("INFO_CODE", "nameIsSame");
      		   conditionInfo.put("INFO_VALUE", "0");
      	   }else{
      		   //客户姓名未通过
      		   conditionInfo.put("INFO_CODE", "nameIsSame");
      		   conditionInfo.put("INFO_VALUE", "1");
      	   }
         }else{
         	resultData.put("X_RESULTCODE", "3030");
    		resultData.put("X_RESULTINFO", "证件校验失败");
    		resultData.put("X_RSPTYPE", "2");
    		resultData.put("X_RSPCODE", "2998");
    		resultData.put("X_RSPDESC", "证件校验失败");
          	resultData.put("BIZ_ORDER_RESULT", "3030");
          	resultData.put("RESULT_DESC","证件校验失败"); 
      		blResultInfo.put("REASON", "身份证信息验证不通过");
      		blResultInfo.put("BUS_STATE", "0");
      		blResultInfo.put("CONDITION", "1");
//          		blResultInfo.put("CONDITION_INFO", conditionInfo);
      		conditionInfo.put("INFO_CODE", "nameIsSame");
      		conditionInfo.put("INFO_VALUE", "1");
      		IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
            IDataset blResultInfos = new DatasetList();
      		blResultInfos.add(blResultInfo);
      		resultData.put("BL_RESULT_INFO", blResultInfos);
          	results.add(resultData);
  			return results;
         }

         IData preCheckData = new DataMap();
		if ("00".equals(userStatus)) {
			preCheckData.put("TRADE_TYPE_CODE", "131");//报停
		} else {
			preCheckData.put("TRADE_TYPE_CODE", "133");//报开
		}
		preCheckData.put("SERIAL_NUMBER", serialNumber);
		preCheckData.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);//预受理
		boolean isSuc = false;
		try {
			IDataset dataset = CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", preCheckData);
			if (IDataUtil.isNotEmpty(dataset)) {
				IData temp = dataset.getData(0);
				if (StringUtils.isNotEmpty(temp.getString("TRADE_ID"))) {
					isSuc = true;
				}
			}
			if(isSuc) {
				//预受理成功，不做处理
			} else {
				//预受理失败
				resultData.put("X_RESULTCODE", "3006");
				resultData.put("X_RESULTINFO", "服务预受理失败");
				resultData.put("X_RSPTYPE", "2");
				resultData.put("X_RSPCODE", "2998");
				resultData.put("X_RSPDESC", "失败");
				resultData.put("BIZ_ORDER_RESULT", "3006");
				resultData.put("RESULT_DESC", "服务预受理失败");
				blResultInfo.put("REASON", "服务预受理失败");
				blResultInfo.put("BUS_STATE", "0");
				blResultInfo.put("CONDITION", "1");
				conditionInfo.put("INFO_CODE", "nameIsSame");
				conditionInfo.put("INFO_VALUE", "1");
				IDataset extendInfos = new DatasetList();
				extendInfos.add(conditionInfo);
				blResultInfo.put("EXTEND_INFO", extendInfos);
				IDataset blResultInfos = new DatasetList();
				blResultInfos.add(blResultInfo);
				resultData.put("BL_RESULT_INFO", blResultInfos);
				results.add(resultData);
				return results;
			}
		} catch (Exception e) {
			resultData.put("X_RESULTCODE", "3006");
			resultData.put("X_RESULTINFO", e.getMessage());
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE", "2998");
			resultData.put("X_RSPDESC", "失败");
			resultData.put("BIZ_ORDER_RESULT", "3006");
			resultData.put("RESULT_DESC", e.getMessage());
			blResultInfo.put("REASON", e.getMessage());
			blResultInfo.put("BUS_STATE", "0");
			blResultInfo.put("CONDITION", "1");
			conditionInfo.put("INFO_CODE", "nameIsSame");
			conditionInfo.put("INFO_VALUE", "1");
			IDataset extendInfos = new DatasetList();
			extendInfos.add(conditionInfo);
			blResultInfo.put("EXTEND_INFO", extendInfos);
			IDataset blResultInfos = new DatasetList();
			blResultInfos.add(blResultInfo);
			resultData.put("BL_RESULT_INFO", blResultInfos);
			results.add(resultData);
			return results;
		}
      //生成身份凭证
		IDataset out = UserIdentInfoQry.getseqString();
		String seqId = ((IData) out.get(0)).getString("OUTSTR", "");
		seqId = seqId.substring(seqId.length()-6,seqId.length());
		StringBuilder strIdentCode = new StringBuilder("ua");
		strIdentCode.append(uca.getUserId());
		String strNow =  SysDateMgr.getSysDateYYYYMMDD();
		strIdentCode.append(strNow);
		strIdentCode.append(seqId);
		IData data = new DataMap();
		data.put("IDENT_CODE", strIdentCode.toString());
		data.put("USER_TYPE", "01");
		data.put("USER_ID", uca.getUserId());
		data.put("CUST_ID", uca.getCustId());
		data.put("SERIAL_NUMBER", uca.getSerialNumber());
		// 身份凭证信息入库
		data.put("IDENT_CODE_LEVEL", "01");
		data.put("IDENT_CODE_TYPE", "01");
		data.put("HOME_PROVINCE", "898");
		data.put("EFFECTIVE_TIME","1800");
		Dao.executeUpdateByCodeCode("TF_B_IDENTCARD_MANAGE", "INS_EFFECTIVE_IDENT", data);
        //查询用户品牌等信息
        IData userInfo = new DataMap();
        userInfo.put("USER_NAME", uca.getCustomer().getCustName());
        userInfo.put("HOME_PROV", "898");
        userInfo.put("USER_STATUS", userStatus);//是
        String brand = uca.getBrandCode();
    	if("G001".equals(brand)){
    		userInfo.put("BRAND", "00");//01:全球通
    	}else if("G002".equals(brand) || "GS01".equals(brand)){
    		userInfo.put("BRAND", "01");//02:神州行
    	}else if("G010".equals(brand) || "GS03".equals(brand)){
    		userInfo.put("BRAND", "02");//03:动感地带
    	}else{
    		userInfo.put("BRAND", "03");//09:其他品牌
    	}

    	//客户星级
    	userInfo.put("USER_STAR", this.getUserStar(uca.getUserId()));

        IData ucaInfo = UcaInfoQry.qryUserInfoByUserId(uca.getUserId());
        String category = ucaInfo.getString("PREPAY_TAG","");
        userInfo.put("CATEGORY", category);//是
        if("PWLW".equals(uca.getBrandCode()) || "WLWG".equals(uca.getBrandCode())){
        	userInfo.put("INTER_USER", "0");//是
        }else{
        	userInfo.put("INTER_USER", "1");//是
        }
        conditionInfo.put("INFO_CODE", "nameIsSame");
        if(StringUtils.equals(userName, uca.getCustomer().getCustName())){
			conditionInfo.put("INFO_VALUE", "0");
	     }else{
	    	conditionInfo.put("INFO_VALUE", "1");
	     }
		IDataset extendInfos = new DatasetList();
        extendInfos.add(conditionInfo);
        blResultInfo.put("EXTEND_INFO", extendInfos);
        userInfo.put("IS_SHIMING", "1".equals(uca.getCustomer().getIsRealName())?"0":"1");//是
        IDataset userInfos = new DatasetList();
        userInfos.add(userInfo);
        resultData.put("USER_INFO", userInfos);
		resultData.put("IDENT_CODE", strIdentCode.toString());
		IDataset blResultInfos = new DatasetList();
		blResultInfos.add(blResultInfo);
		resultData.put("BL_RESULT_INFO", blResultInfos);
		
		results.add(resultData);
		return results;
		
	}
	private IDataset userDestroyAuth(IData input) throws Exception {

		IDataset results = new DatasetList();
		IData resultData = new DataMap();
		String serialNumber = IDataUtil.chkParam(input, "ID_VALUE");
		String idType = IDataUtil.chkParam(input, "ID_TYPE");
		String oprNumb = IDataUtil.chkParam(input, "OPR_NUMB");
		String bizType = IDataUtil.chkParam(input, "BIZ_TYPE");
		String idCardNum = "";
		IDataUtil.chkParam(input, "OPR_TIME");
		IDataUtil.chkParam(input, "BIZ_VERSION");
		String idCardType = input.getString("ID_CARD_TYPE");
		if(StringUtils.isNotBlank(idCardType)){
			idCardNum = IDataUtil.chkParam(input, "ID_CARD_NUM");
		}
		//客服密码
    	String ccPwd = input.getString("CCPASSWD");
		resultData.put("ID_TYPE", "01");
		resultData.put("ID_VALUE", serialNumber);
		resultData.put("OPR_NUMB", input.getString("OPR_NUMB"));
		resultData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		resultData.put("BIZ_ORDER_RESULT", "0000");
		IData conditionInfo = new DataMap();
		IData extendInfo = new DataMap();
		IData blResultInfo = new DataMap();
		blResultInfo.put("CONDITION", "0");
		blResultInfo.put("BUS_STATE", "1");
		resultData.put("X_RESULTCODE", "0000");
  		resultData.put("X_RESULTINFO", "ok");
  		resultData.put("X_RSPTYPE", "0");
  		resultData.put("X_RSPCODE", "0000");
  		resultData.put("X_RSPDESC", "ok");
  		UcaData uca = new UcaData();
		try{
			uca = UcaDataFactory.getNormalUca(serialNumber);
		}catch(Exception e){
			resultData.put("X_RESULTCODE", "2009");
    		resultData.put("X_RESULTINFO", "用户不存在或用户非正常状态");
    		resultData.put("X_RSPTYPE", "2");
    		resultData.put("X_RSPCODE", "2998");
    		resultData.put("X_RSPDESC", "用户不存在或用户非正常状态");
			resultData.put("BIZ_ORDER_RESULT", "2009");
			resultData.put("RESULT_DESC", "用户不存在");
			blResultInfo.put("BUS_STATE", "0");//办理状态(0：不可办理1：可办理)
    	    blResultInfo.put("CONDITION", "1");//办理依赖条件(当BusState为0时填写0：无1：有)
    	    blResultInfo.put("REASON", "用户不存在！");//不能办理原因说明
    	    conditionInfo.put("INFO_CODE", "nameIsSame");
    	    conditionInfo.put("INFO_VALUE", "1");
    	    IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
    	    IDataset blResultInfos = new DatasetList();
    	    blResultInfos.add(blResultInfo);
    	    resultData.put("BL_RESULT_INFO", blResultInfos);
			results.add(resultData);
			return results;
		}
		if(uca == null) {
			resultData.put("BIZ_ORDER_RESULT", "2009");
			resultData.put("RESULT_DESC", "用户不存在");
			resultData.put("X_RESULTCODE", "2009");
	  		resultData.put("X_RESULTINFO", "用户不存在");
	  		resultData.put("X_RSPTYPE", "2");
	  		resultData.put("X_RSPCODE", "2009");
	  		resultData.put("X_RSPDESC", "用户不存在");
    		blResultInfo.put("REASON", "用户不存在");
    		blResultInfo.put("BUS_STATE", "0");
    		conditionInfo.put("INFO_CODE", "nameIsSame");
    		conditionInfo.put("INFO_VALUE", "1");
    		IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
    		IDataset blResultInfos = new DatasetList();
    		blResultInfos.add(blResultInfo);
    		resultData.put("BL_RESULT_INFO", blResultInfos);
        	results.add(resultData);
			return results;
		}
		if (StringUtils.isEmpty(idCardNum)) {
			resultData.put("BIZ_ORDER_RESULT", "3030");
			resultData.put("RESULT_DESC","证件号码不能为空"); 
			resultData.put("X_RESULTCODE", "3030");
	  		resultData.put("X_RESULTINFO", "证件号码不能为空");
	  		resultData.put("X_RSPTYPE", "2");
	  		resultData.put("X_RSPCODE", "3030");
	  		resultData.put("X_RSPDESC", "证件号码不能为空");
    		blResultInfo.put("REASON", "证件号码不能为空");
    		blResultInfo.put("BUS_STATE", "0");
    		conditionInfo.put("INFO_CODE", "nameIsSame");
    		conditionInfo.put("INFO_VALUE", "1");
    		IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
    		IDataset blResultInfos = new DatasetList();
    		blResultInfos.add(blResultInfo);
    		resultData.put("BL_RESULT_INFO", blResultInfos);
        	results.add(resultData);
			return results;
         } else{
        	 if (!StringUtils.isEmpty(idCardNum)) {
       			String ibossPsptTypeCode = this.getIBossPsptTypeParam(uca.getCustomer().getPsptTypeCode());
               //如果要验证证件号码是否一致的前面要先验证证件号码的类型是否一致  
                 if (StringUtils.equals(idCardType,ibossPsptTypeCode ) 
                 		&& StringUtils.equals(idCardNum, uca.getCustomer().getPsptId())) { 	
                
                 } else {            	
                	resultData.put("BIZ_ORDER_RESULT", "3030");
                	resultData.put("RESULT_DESC","证件校验失败"); 
                	resultData.put("X_RESULTCODE", "3030");
        	  		resultData.put("X_RESULTINFO", "证件校验失败");
        	  		resultData.put("X_RSPTYPE", "2");
        	  		resultData.put("X_RSPCODE", "3030");
        	  		resultData.put("X_RSPDESC", "证件校验失败");
            		blResultInfo.put("REASON", "身份证信息验证不通过");
            		blResultInfo.put("BUS_STATE", "0");
            		conditionInfo.put("INFO_CODE", "nameIsSame");
            		conditionInfo.put("INFO_VALUE", "1");
            		IDataset extendInfos = new DatasetList();
                    extendInfos.add(conditionInfo);
                    blResultInfo.put("EXTEND_INFO", extendInfos);
            		IDataset blResultInfos = new DatasetList();
            		blResultInfos.add(blResultInfo);
            		resultData.put("BL_RESULT_INFO", blResultInfos);
                	results.add(resultData);
        			return results;
                 }
         	 }
         }
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
        	resultData.put("BIZ_ORDER_RESULT", "2005");
        	resultData.put("X_RESULTINFO", errorMsg);
        	resultData.put("X_RESULTCODE", "2005");
      		resultData.put("X_RSPTYPE", "2");
      		resultData.put("X_RSPCODE", "2998");
      		resultData.put("X_RSPDESC", errorMsg);
        	blResultInfo.put("REASON", "用户状态非正常");
    		blResultInfo.put("BUS_STATE", "0");
    		conditionInfo.put("INFO_CODE", "nameIsSame");
    		conditionInfo.put("INFO_VALUE", "1");
    		IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
        	resultData.put("RESULT_DESC", errorMsg);
        	IDataset blResultInfos = new DatasetList();
    		blResultInfos.add(blResultInfo);
    		resultData.put("BL_RESULT_INFO", blResultInfos);
        	results.add(resultData);
			return results;
        }
      //客服密码校验
		if(StringUtils.isNotEmpty(ccPwd)){
			IData data = new DataMap();
			data.put("SERIAL_NUMBER", uca.getSerialNumber());
			data.put("USER_PASSWD", ccPwd);
			IData checkResut = UserPasswordInfoComm.checkUserPWDForL2F(data);
			if (!StringUtils.equals(checkResut.getString("X_CHECK_INFO"), "0"))
			{
				if(!checkFailedCount(uca,input.getString("BIZ_TYPE"))){
		        	resultData.put("BIZ_ORDER_RESULT", "2046");
				    resultData.put("RESULT_DESC", "鉴权失败超5次，系统已锁定！");
				    resultData.put("X_RESULTCODE", "2046");
		    	    resultData.put("X_RESULTINFO", "超过次数锁定");
		    	    resultData.put("X_RSPTYPE", "2");
		    	    resultData.put("X_RSPCODE", "2998");
		    	    resultData.put("X_RSPDESC", "失败");
		    	    blResultInfo.put("BUS_STATE", "0");//办理状态(0：不可办理1：可办理)
		    	    blResultInfo.put("CONDITION", "1");//办理依赖条件(当BusState为0时填写0：无1：有)
		    	    blResultInfo.put("REASON", "鉴权失败超5次，系统已锁定！");//不能办理原因说明
		    	    conditionInfo.put("INFO_CODE", "nameIsSame");
		    	    conditionInfo.put("INFO_VALUE", "1");
		    	    IDataset extendInfos = new DatasetList();
		            extendInfos.add(conditionInfo);
		            blResultInfo.put("EXTEND_INFO", extendInfos);
		    	    IDataset blResultInfos = new DatasetList();
		    	    blResultInfos.add(blResultInfo);
		    	    resultData.put("BL_RESULT_INFO", blResultInfos);
				    results.add(resultData);
				    return results;
				}
				resultData.put("X_RSPTYPE", "2");
	      		resultData.put("X_RSPCODE", "2998");
	      		resultData.put("X_RSPDESC", checkResut.getString("X_RESULTINFO"));
				resultData.put("X_RESULTCODE", "2036");
	      		resultData.put("X_RESULTINFO", checkResut.getString("X_RESULTINFO"));
	        	resultData.put("BIZ_ORDER_RESULT", "2036");
	        	resultData.put("RESULT_DESC",checkResut.getString("X_RESULTINFO")); 
	    		blResultInfo.put("REASON", "客服密码验证不通过");
	    		blResultInfo.put("BUS_STATE", "0");
	    		blResultInfo.put("CONDITION", "1");
	    		conditionInfo.put("INFO_CODE", "nameIsSame");
	    		conditionInfo.put("INFO_VALUE", "1");
	    		IDataset extendInfos = new DatasetList();
	            extendInfos.add(conditionInfo);
	            blResultInfo.put("EXTEND_INFO", extendInfos);
	            IDataset blResultInfos = new DatasetList();
	    		blResultInfos.add(blResultInfo);
	    		resultData.put("BL_RESULT_INFO", blResultInfos);
	        	results.add(resultData);
				return results;
			}
		}
        //好友号码验证
        String numberCheck = input.getString("NUMBER_CHECK","");
        try {
        	if(!StringUtils.isBlank(numberCheck)){
        		IDataset numberResults = AcctCall.checkFriend(serialNumber, numberCheck);
        		if(IDataUtil.isNotEmpty(numberResults)){
        			IData numberResult = numberResults.getData(0);
        			if(!"0000".equals(numberResult.getString("RESULT_CODE",""))){
        				if(!checkFailedCount(uca,input.getString("BIZ_TYPE"))){
        					resultData.put("BIZ_ORDER_RESULT", "2046");
        					resultData.put("RESULT_DESC", "鉴权失败超5次，系统已锁定！");
        					resultData.put("X_RESULTCODE", "2046");
        					resultData.put("X_RESULTINFO", "超过次数锁定");
        					resultData.put("X_RSPTYPE", "2");
        					resultData.put("X_RSPCODE", "2998");
        					resultData.put("X_RSPDESC", "失败");
        					blResultInfo.put("BUS_STATE", "0");//办理状态(0：不可办理1：可办理)
        					blResultInfo.put("CONDITION", "1");//办理依赖条件(当BusState为0时填写0：无1：有)
        					blResultInfo.put("REASON", "鉴权失败超5次，系统已锁定！");//不能办理原因说明
        					conditionInfo.put("INFO_CODE", "nameIsSame");
        					conditionInfo.put("INFO_VALUE", "1");
        					IDataset extendInfos = new DatasetList();
        					extendInfos.add(conditionInfo);
        					blResultInfo.put("EXTEND_INFO", extendInfos);
        					IDataset blResultInfos = new DatasetList();
        					blResultInfos.add(blResultInfo);
        					resultData.put("BL_RESULT_INFO", blResultInfos);
        					results.add(resultData);
        					return results;
        				}
        				resultData.put("X_RSPTYPE", "2");
        				resultData.put("X_RSPCODE", "2998");
        				resultData.put("X_RSPDESC", "好友号码" + numberResult.getString("FAILED_NUMBER") + "验证未通过！");
        				resultData.put("X_RESULTCODE", "2057");
        				resultData.put("X_RESULTINFO", "好友号码" + numberResult.getString("FAILED_NUMBER") + "验证未通过！");
        				resultData.put("BIZ_ORDER_RESULT", "2057");
        				blResultInfo.put("REASON", numberResult.getString("FAILED_NUMBER")+"验证不通过");
        				blResultInfo.put("BUS_STATE", "0");
        				blResultInfo.put("CONDITION", "0");
        				IDataset blResultInfos = new DatasetList();
        				blResultInfos.add(blResultInfo);
        				resultData.put("BL_RESULT_INFO", blResultInfos);
        				resultData.put("RESULT_DESC", "好友号码" + numberResult.getString("FAILED_NUMBER") + "验证未通过！");
        				results.add(resultData);
        				return results;
        			}
        		}
        	}
		} catch (Exception e) {
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE", "2998");
			resultData.put("X_RSPDESC", e.getMessage());
			resultData.put("X_RESULTCODE", "2057");
			resultData.put("X_RESULTINFO", e.getMessage());
			resultData.put("BIZ_ORDER_RESULT", "2057");
			blResultInfo.put("REASON", e.getMessage());
			blResultInfo.put("BUS_STATE", "0");
			blResultInfo.put("CONDITION", "0");
			IDataset blResultInfos = new DatasetList();
			blResultInfos.add(blResultInfo);
			resultData.put("BL_RESULT_INFO", blResultInfos);
			resultData.put("RESULT_DESC", e.getMessage());
			results.add(resultData);
			return results;
		}

		IData preCheckData = new DataMap();
		preCheckData.put("TRADE_TYPE_CODE", "192");//立即销户
		preCheckData.put("SERIAL_NUMBER", serialNumber);
		preCheckData.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);//预受理
		preCheckData.put(Route.ROUTE_EPARCHY_CODE, uca.getUserEparchyCode());
		boolean isSuc = false;
		try {
			IDataset dataset = CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", preCheckData);
			if (IDataUtil.isNotEmpty(dataset)) {
				IData temp = dataset.getData(0);
				if (StringUtils.isNotEmpty(temp.getString("TRADE_ID"))) {
					isSuc = true;
				}
			}
			if (isSuc) {
				//预受理成功,不做处理
			} else {
				//预受理失败
				resultData.put("X_RESULTCODE", "3006");
				resultData.put("X_RESULTINFO", "号码存在销号业务办理条件的限制，不允许销户");
				resultData.put("X_RSPTYPE", "2");
				resultData.put("X_RSPCODE", "2998");
				resultData.put("X_RSPDESC", "失败");
				resultData.put("BIZ_ORDER_RESULT", "3006");
				resultData.put("RESULT_DESC", "号码存在销号业务办理条件的限制，不允许销户");
				blResultInfo.put("REASON", "号码存在销号业务办理条件的限制，不允许销户");
				blResultInfo.put("BUS_STATE", "0");
				blResultInfo.put("CONDITION", "1");
				conditionInfo.put("INFO_CODE", "nameIsSame");
				conditionInfo.put("INFO_VALUE", "1");
				IDataset extendInfos = new DatasetList();
				extendInfos.add(conditionInfo);
				blResultInfo.put("EXTEND_INFO", extendInfos);
				IDataset blResultInfos = new DatasetList();
				blResultInfos.add(blResultInfo);
				resultData.put("BL_RESULT_INFO", blResultInfos);
				results.add(resultData);
				return results;
			}
		} catch (Exception e) {
			resultData.put("X_RESULTCODE", "3006");
			resultData.put("X_RESULTINFO", e.getMessage());
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE", "2998");
			resultData.put("X_RSPDESC", "失败");
			resultData.put("BIZ_ORDER_RESULT", "3006");
			resultData.put("RESULT_DESC", e.getMessage());
			blResultInfo.put("REASON", e.getMessage());
			blResultInfo.put("BUS_STATE", "0");
			blResultInfo.put("CONDITION", "1");
			conditionInfo.put("INFO_CODE", "nameIsSame");
			conditionInfo.put("INFO_VALUE", "1");
			IDataset extendInfos = new DatasetList();
			extendInfos.add(conditionInfo);
			blResultInfo.put("EXTEND_INFO", extendInfos);
			IDataset blResultInfos = new DatasetList();
			blResultInfos.add(blResultInfo);
			resultData.put("BL_RESULT_INFO", blResultInfos);
			results.add(resultData);
			return results;
		}


        //查询用户品牌等信息
        IData userInfo = new DataMap();
        userInfo.put("USER_NAME", uca.getCustomer().getCustName());
        userInfo.put("HOME_PROV", "898");
        userInfo.put("USER_STATUS", userStatus);//是
        String brand = uca.getBrandCode();
    	if("G001".equals(brand)){
    		userInfo.put("BRAND", "00");//01:全球通
    	}else if("G002".equals(brand) || "GS01".equals(brand)){
    		userInfo.put("BRAND", "01");//02:神州行
    	}else if("G010".equals(brand) || "GS03".equals(brand)){
    		userInfo.put("BRAND", "02");//03:动感地带
    	}else{
    		userInfo.put("BRAND", "03");//09:其他品牌
    	}

		//客户星级
		userInfo.put("USER_STAR", this.getUserStar(uca.getUserId()));

		IData ucaInfo = UcaInfoQry.qryUserInfoByUserId(uca.getUserId());
        String category = ucaInfo.getString("PREPAY_TAG","");
        userInfo.put("CATEGORY", category);//是
        if("PWLW".equals(uca.getBrandCode()) || "WLWG".equals(uca.getBrandCode())){
        	userInfo.put("INTER_USER", "0");//是
        }else{
        	userInfo.put("INTER_USER", "1");//是
        }
        conditionInfo.put("INFO_CODE", "nameIsSame");
		conditionInfo.put("INFO_VALUE", "0");
		IDataset extendInfos = new DatasetList();
        extendInfos.add(conditionInfo);
        blResultInfo.put("EXTEND_INFO", extendInfos);
        userInfo.put("IS_SHIMING", "1".equals(uca.getCustomer().getIsRealName())?"0":"1");//是
        IDataset userInfos = new DatasetList();
        userInfos.add(userInfo);
        resultData.put("USER_INFO", userInfos);
        //生成用户凭证
        IData certificateParam = new DataMap();
        certificateParam.put("USER_ID", uca.getUserId());
        certificateParam.put("CUST_ID", uca.getCustId());
        certificateParam.put("BIZ_TYPE", input.getString("BIZ_TYPE","1013"));
        certificateParam.put("ID_VALUE", input.getString("ID_VALUE"));
		resultData.put("IDENT_CODE", buildUserCertificate(certificateParam));
		IDataset blResultInfos = new DatasetList();
		blResultInfos.add(blResultInfo);
		resultData.put("BL_RESULT_INFO", blResultInfos);
		
		results.add(resultData);
		return results;
	
	}
	private IDataset writeCardNumberCheck(IData input) throws Exception {


		IDataset results = new DatasetList();
		IData resultData = new DataMap();
		String serialNumber = IDataUtil.chkParam(input, "ID_VALUE");
		String idType = IDataUtil.chkParam(input, "ID_TYPE");
		String oprNumb = IDataUtil.chkParam(input, "OPR_NUMB");
		String bizType = IDataUtil.chkParam(input, "BIZ_TYPE");
		String idCardNum = "";
		IDataUtil.chkParam(input, "OPR_TIME");
		IDataUtil.chkParam(input, "BIZ_VERSION");
		String idCardType = input.getString("ID_CARD_TYPE");
		if(StringUtils.isNotBlank(idCardType)){
			idCardNum = IDataUtil.chkParam(input, "ID_CARD_NUM");
		}
		//客服密码
    	String ccPwd = input.getString("CCPASSWD");
		resultData.put("ID_TYPE", "01");
		resultData.put("ID_VALUE", serialNumber);
		resultData.put("OPR_NUMB", input.getString("OPR_NUMB"));
		resultData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		resultData.put("BIZ_ORDER_RESULT", "0000");
		IData conditionInfo = new DataMap();
		IData extendInfo = new DataMap();
		IData blResultInfo = new DataMap();
		blResultInfo.put("CONDITION", "0");
		blResultInfo.put("BUS_STATE", "1");
		resultData.put("X_RESULTCODE", "0000");
  		resultData.put("X_RESULTINFO", "ok");
  		resultData.put("X_RSPTYPE", "0");
  		resultData.put("X_RSPCODE", "0000");
  		resultData.put("X_RSPDESC", "ok");
  		UcaData uca = new UcaData();
		try{
			uca = UcaDataFactory.getNormalUca(serialNumber);
		}catch(Exception e){
			resultData.put("X_RESULTCODE", "2009");
    		resultData.put("X_RESULTINFO", "用户不存在或用户非正常状态");
    		resultData.put("X_RSPTYPE", "2");
    		resultData.put("X_RSPCODE", "2998");
    		resultData.put("X_RSPDESC", "用户不存在或用户非正常状态");
			resultData.put("BIZ_ORDER_RESULT", "2009");
			resultData.put("RESULT_DESC", "用户不存在");
			blResultInfo.put("BUS_STATE", "0");//办理状态(0：不可办理1：可办理)
    	    blResultInfo.put("CONDITION", "1");//办理依赖条件(当BusState为0时填写0：无1：有)
    	    blResultInfo.put("REASON", "用户不存在！");//不能办理原因说明
    	    conditionInfo.put("INFO_CODE", "nameIsSame");
    	    conditionInfo.put("INFO_VALUE", "1");
    	    IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
    	    IDataset blResultInfos = new DatasetList();
    	    blResultInfos.add(blResultInfo);
    	    resultData.put("BL_RESULT_INFO", blResultInfos);
			results.add(resultData);
			return results;
		}
		if(uca == null) {
			resultData.put("BIZ_ORDER_RESULT", "2009");
			resultData.put("RESULT_DESC", "用户不存在");
			resultData.put("X_RESULTCODE", "2009");
	  		resultData.put("X_RESULTINFO", "用户不存在");
	  		resultData.put("X_RSPTYPE", "2");
	  		resultData.put("X_RSPCODE", "2009");
	  		resultData.put("X_RSPDESC", "用户不存在");
    		blResultInfo.put("REASON", "用户不存在");
    		blResultInfo.put("BUS_STATE", "0");
    		conditionInfo.put("INFO_CODE", "nameIsSame");
    		conditionInfo.put("INFO_VALUE", "1");
    		IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
    		IDataset blResultInfos = new DatasetList();
    		blResultInfos.add(blResultInfo);
    		resultData.put("BL_RESULT_INFO", blResultInfos);
        	results.add(resultData);
			return results;
		}
		if (StringUtils.isEmpty(idCardNum)) {
			resultData.put("BIZ_ORDER_RESULT", "3030");
			resultData.put("RESULT_DESC","证件号码不能为空"); 
			resultData.put("X_RESULTCODE", "3030");
	  		resultData.put("X_RESULTINFO", "证件号码不能为空");
	  		resultData.put("X_RSPTYPE", "2");
	  		resultData.put("X_RSPCODE", "3030");
	  		resultData.put("X_RSPDESC", "证件号码不能为空");
    		blResultInfo.put("REASON", "证件号码不能为空");
    		blResultInfo.put("BUS_STATE", "0");
    		conditionInfo.put("INFO_CODE", "nameIsSame");
    		conditionInfo.put("INFO_VALUE", "1");
    		IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
    		IDataset blResultInfos = new DatasetList();
    		blResultInfos.add(blResultInfo);
    		resultData.put("BL_RESULT_INFO", blResultInfos);
        	results.add(resultData);
			return results;
         } else{
        	 if (!StringUtils.isEmpty(idCardNum)) {
       			String ibossPsptTypeCode = this.getIBossPsptTypeParam(uca.getCustomer().getPsptTypeCode());
               //如果要验证证件号码是否一致的前面要先验证证件号码的类型是否一致  
                 if (StringUtils.equals(idCardType,ibossPsptTypeCode ) 
                 		&& StringUtils.equals(idCardNum, uca.getCustomer().getPsptId())) { 	
                
                 } else {            	
                	resultData.put("BIZ_ORDER_RESULT", "3030");
                	resultData.put("RESULT_DESC","证件校验失败"); 
                	resultData.put("X_RESULTCODE", "3030");
        	  		resultData.put("X_RESULTINFO", "证件校验失败");
        	  		resultData.put("X_RSPTYPE", "2");
        	  		resultData.put("X_RSPCODE", "3030");
        	  		resultData.put("X_RSPDESC", "证件校验失败");
            		blResultInfo.put("REASON", "身份证信息验证不通过");
            		blResultInfo.put("BUS_STATE", "0");
            		conditionInfo.put("INFO_CODE", "nameIsSame");
            		conditionInfo.put("INFO_VALUE", "1");
            		IDataset extendInfos = new DatasetList();
                    extendInfos.add(conditionInfo);
                    blResultInfo.put("EXTEND_INFO", extendInfos);
            		IDataset blResultInfos = new DatasetList();
            		blResultInfos.add(blResultInfo);
            		resultData.put("BL_RESULT_INFO", blResultInfos);
                	results.add(resultData);
        			return results;
                 }
         	 }
         }
		String userState = uca.getUser().getUserStateCodeset();
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
        if("1".equals(userState)||"2".equals(userState)){//用户申请停机不拦截
        	isCorrectStatus = true;
			errorMsg = "ok";
        }
        if (!isCorrectStatus) {
        	resultData.put("BIZ_ORDER_RESULT", "2005");
        	resultData.put("X_RESULTINFO", errorMsg);
        	resultData.put("X_RESULTCODE", "2005");
      		resultData.put("X_RSPTYPE", "2");
      		resultData.put("X_RSPCODE", "2998");
      		resultData.put("X_RSPDESC", "用户状态非正常");
        	blResultInfo.put("REASON", errorMsg);
    		blResultInfo.put("BUS_STATE", "0");
    		conditionInfo.put("INFO_CODE", "nameIsSame");
    		conditionInfo.put("INFO_VALUE", "1");
    		IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
        	resultData.put("RESULT_DESC", errorMsg);
        	IDataset blResultInfos = new DatasetList();
    		blResultInfos.add(blResultInfo);
    		resultData.put("BL_RESULT_INFO", blResultInfos);
        	results.add(resultData);
			return results;
        }
      //客服密码校验
		if(StringUtils.isNotEmpty(ccPwd)){
			IData data = new DataMap();
			data.put("SERIAL_NUMBER", uca.getSerialNumber());
			data.put("USER_PASSWD", ccPwd);
			IData checkResut = UserPasswordInfoComm.checkUserPWDForL2F(data);
			if (!StringUtils.equals(checkResut.getString("X_CHECK_INFO"), "0"))
			{
				if(!checkFailedCount(uca,input.getString("BIZ_TYPE"))){
		        	resultData.put("BIZ_ORDER_RESULT", "2046");
				    resultData.put("RESULT_DESC", "鉴权失败超5次，系统已锁定！");
				    resultData.put("X_RESULTCODE", "2046");
		    	    resultData.put("X_RESULTINFO", "超过次数锁定");
		    	    resultData.put("X_RSPTYPE", "2");
		    	    resultData.put("X_RSPCODE", "2998");
		    	    resultData.put("X_RSPDESC", "失败");
		    	    blResultInfo.put("BUS_STATE", "0");//办理状态(0：不可办理1：可办理)
		    	    blResultInfo.put("CONDITION", "1");//办理依赖条件(当BusState为0时填写0：无1：有)
		    	    blResultInfo.put("REASON", "鉴权失败超5次，系统已锁定！");//不能办理原因说明
		    	    conditionInfo.put("INFO_CODE", "nameIsSame");
		    	    conditionInfo.put("INFO_VALUE", "1");
		    	    IDataset extendInfos = new DatasetList();
		            extendInfos.add(conditionInfo);
		            blResultInfo.put("EXTEND_INFO", extendInfos);
		    	    IDataset blResultInfos = new DatasetList();
		    	    blResultInfos.add(blResultInfo);
		    	    resultData.put("BL_RESULT_INFO", blResultInfos);
				    results.add(resultData);
				    return results;
				}
				resultData.put("X_RSPTYPE", "2");
	      		resultData.put("X_RSPCODE", "2998");
	      		resultData.put("X_RSPDESC", checkResut.getString("X_RESULTINFO"));
				resultData.put("X_RESULTCODE", "2036");
	      		resultData.put("X_RESULTINFO", checkResut.getString("X_RESULTINFO"));
	        	resultData.put("BIZ_ORDER_RESULT", "2036");
	        	resultData.put("RESULT_DESC",checkResut.getString("X_RESULTINFO")); 
	    		blResultInfo.put("REASON", "客服密码验证不通过");
	    		blResultInfo.put("BUS_STATE", "0");
	    		blResultInfo.put("CONDITION", "1");
	    		conditionInfo.put("INFO_CODE", "nameIsSame");
	    		conditionInfo.put("INFO_VALUE", "1");
	    		IDataset extendInfos = new DatasetList();
	            extendInfos.add(conditionInfo);
	            blResultInfo.put("EXTEND_INFO", extendInfos);
	            IDataset blResultInfos = new DatasetList();
	    		blResultInfos.add(blResultInfo);
	    		resultData.put("BL_RESULT_INFO", blResultInfos);
	        	results.add(resultData);
				return results;
			}
		}
        //好友号码验证
        String numberCheck = input.getString("NUMBER_CHECK","");
        try {
        	if(!StringUtils.isBlank(numberCheck)){
        		IDataset numberResults = AcctCall.checkFriend(serialNumber, numberCheck);
        		if(IDataUtil.isNotEmpty(numberResults)){
        			IData numberResult = numberResults.getData(0);
        			if(!"0000".equals(numberResult.getString("RESULT_CODE",""))){
        				if(!checkFailedCount(uca,input.getString("BIZ_TYPE"))){
        					resultData.put("BIZ_ORDER_RESULT", "2046");
        					resultData.put("RESULT_DESC", "鉴权失败超5次，系统已锁定！");
        					resultData.put("X_RESULTCODE", "2046");
        					resultData.put("X_RESULTINFO", "超过次数锁定");
        					resultData.put("X_RSPTYPE", "2");
        					resultData.put("X_RSPCODE", "2998");
        					resultData.put("X_RSPDESC", "失败");
        					blResultInfo.put("BUS_STATE", "0");//办理状态(0：不可办理1：可办理)
        					blResultInfo.put("CONDITION", "1");//办理依赖条件(当BusState为0时填写0：无1：有)
        					blResultInfo.put("REASON", "鉴权失败超5次，系统已锁定！");//不能办理原因说明
        					conditionInfo.put("INFO_CODE", "nameIsSame");
        					conditionInfo.put("INFO_VALUE", "1");
        					IDataset extendInfos = new DatasetList();
        					extendInfos.add(conditionInfo);
        					blResultInfo.put("EXTEND_INFO", extendInfos);
        					IDataset blResultInfos = new DatasetList();
        					blResultInfos.add(blResultInfo);
        					resultData.put("BL_RESULT_INFO", blResultInfos);
        					results.add(resultData);
        					return results;
        				}
        				resultData.put("X_RSPTYPE", "2");
        				resultData.put("X_RSPCODE", "2998");
        				resultData.put("X_RSPDESC", "好友号码" + numberResult.getString("FAILED_NUMBER") + "验证未通过！");
        				resultData.put("X_RESULTCODE", "2057");
        				resultData.put("X_RESULTINFO", "好友号码" + numberResult.getString("FAILED_NUMBER") + "验证未通过！");
        				resultData.put("BIZ_ORDER_RESULT", "2057");
        				blResultInfo.put("REASON", numberResult.getString("FAILED_NUMBER")+"验证不通过");
        				blResultInfo.put("BUS_STATE", "0");
        				blResultInfo.put("CONDITION", "0");
        				IDataset blResultInfos = new DatasetList();
        				blResultInfos.add(blResultInfo);
        				resultData.put("BL_RESULT_INFO", blResultInfos);
        				resultData.put("RESULT_DESC", "好友号码" + numberResult.getString("FAILED_NUMBER") + "验证未通过！");
        				results.add(resultData);
        				return results;
        			}
        		}
        	}
		} catch (Exception e) {
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE", "2998");
			resultData.put("X_RSPDESC", e.getMessage());
			resultData.put("X_RESULTCODE", "2057");
			resultData.put("X_RESULTINFO", e.getMessage());
			resultData.put("BIZ_ORDER_RESULT", "2057");
			blResultInfo.put("REASON", e.getMessage());
			blResultInfo.put("BUS_STATE", "0");
			blResultInfo.put("CONDITION", "0");
			IDataset blResultInfos = new DatasetList();
			blResultInfos.add(blResultInfo);
			resultData.put("BL_RESULT_INFO", blResultInfos);
			resultData.put("RESULT_DESC", e.getMessage());
			results.add(resultData);
			return results;
		}
        //查询用户品牌等信息
        IData userInfo = new DataMap();
        userInfo.put("USER_NAME", uca.getCustomer().getCustName());
        userInfo.put("HOME_PROV", "898");
        userInfo.put("USER_STATUS", userStatus);//是
        String brand = uca.getBrandCode();
    	if("G001".equals(brand)){
    		userInfo.put("BRAND", "00");//01:全球通
    	}else if("G002".equals(brand) || "GS01".equals(brand)){
    		userInfo.put("BRAND", "01");//02:神州行
    	}else if("G010".equals(brand) || "GS03".equals(brand)){
    		userInfo.put("BRAND", "02");//03:动感地带
    	}else{
    		userInfo.put("BRAND", "03");//09:其他品牌
    	}

		//客户星级
		userInfo.put("USER_STAR", this.getUserStar(uca.getUserId()));

		IData ucaInfo = UcaInfoQry.qryUserInfoByUserId(uca.getUserId());
        String category = ucaInfo.getString("PREPAY_TAG","");
        userInfo.put("CATEGORY", category);//是
        if("PWLW".equals(uca.getBrandCode()) || "WLWG".equals(uca.getBrandCode())){
        	userInfo.put("INTER_USER", "0");//是
        }else{
        	userInfo.put("INTER_USER", "1");//是
        }
        conditionInfo.put("INFO_CODE", "nameIsSame");
		conditionInfo.put("INFO_VALUE", "0");
		IDataset extendInfos = new DatasetList();
        extendInfos.add(conditionInfo);
        blResultInfo.put("EXTEND_INFO", extendInfos);
        userInfo.put("IS_SHIMING", "1".equals(uca.getCustomer().getIsRealName())?"0":"1");//是
        IDataset userInfos = new DatasetList();
        userInfos.add(userInfo);
        resultData.put("USER_INFO", userInfos);
		resultData.put("IDENT_CODE", buildCertificate(uca));
		IDataset blResultInfos = new DatasetList();
		blResultInfos.add(blResultInfo);
		resultData.put("BL_RESULT_INFO", blResultInfos);
		results.add(resultData);
		return results;
	
	
	}
	public IDataset openResultAuth(IData input) throws Exception {
		IDataset results = new DatasetList();
		IData resultData = new DataMap();
		String serialNumber = IDataUtil.chkParam(input, "ID_VALUE");
		String idType = IDataUtil.chkParam(input, "ID_TYPE");
		String pstpId = "";
		String pstpTypeCode =  getCrmPsptTypeParam(input.getString("ID_CARD_TYPE","").trim());
		IDataUtil.chkParam(input, "OPR_TIME");
		IDataUtil.chkParam(input, "BIZ_VERSION");
		String idCardType = input.getString("ID_CARD_TYPE");
		if(StringUtils.isNotBlank(idCardType)){
			pstpId = IDataUtil.chkParam(input, "ID_CARD_NUM");
		}
		
		resultData.put("ID_TYPE", "01");
		resultData.put("ID_VALUE", serialNumber);
		resultData.put("OPR_NUMB", input.getString("OPR_NUMB"));
		resultData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		resultData.put("BIZ_ORDER_RESULT", "0000");
		IData conditionInfo = new DataMap();
		IData extendInfo = new DataMap();
		conditionInfo.put("INFO_CODE", "");
		conditionInfo.put("INFO_VALUE", "");
		IData blResultInfo = new DataMap();
		blResultInfo.put("CONDITION", "0");
		blResultInfo.put("BUS_STATE", "1");
		resultData.put("X_RESULTCODE", "0000");
  		resultData.put("X_RESULTINFO", "ok");
  		resultData.put("X_RSPTYPE", "0");
  		resultData.put("X_RSPCODE", "0000");
  		resultData.put("X_RSPDESC", "ok");
  		UcaData uca = new UcaData();
		try{
			uca = UcaDataFactory.getNormalUca(serialNumber);
		}catch(Exception e){
			resultData.put("X_RESULTCODE", "2009");
    		resultData.put("X_RESULTINFO", "用户不存在或用户非正常状态");
    		resultData.put("X_RSPTYPE", "2");
    		resultData.put("X_RSPCODE", "2998");
    		resultData.put("X_RSPDESC", "用户不存在或用户非正常状态");
			resultData.put("BIZ_ORDER_RESULT", "2009");
			resultData.put("RESULT_DESC", "用户不存在");
			blResultInfo.put("BUS_STATE", "0");//办理状态(0：不可办理1：可办理)
    	    blResultInfo.put("CONDITION", "1");//办理依赖条件(当BusState为0时填写0：无1：有)
    	    blResultInfo.put("REASON", "用户不存在！");//不能办理原因说明
    	    conditionInfo.put("INFO_CODE", "nameIsSame");
    	    conditionInfo.put("INFO_VALUE", "1");
    	    IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
    	    IDataset blResultInfos = new DatasetList();
    	    blResultInfos.add(blResultInfo);
    	    resultData.put("BL_RESULT_INFO", blResultInfos);
			results.add(resultData);
			return results;
		}
		if(uca == null) {
			resultData.put("BIZ_ORDER_RESULT", "2009");
			resultData.put("RESULT_DESC", "用户不存在或用户非正常状态");
			resultData.put("X_RESULTCODE", "2009");
    		resultData.put("X_RESULTINFO", "用户不存在或用户非正常状态");
    		resultData.put("X_RSPTYPE", "2");
    		resultData.put("X_RSPCODE", "2998");
    		resultData.put("X_RSPDESC", "用户不存在或用户非正常状态");
    		blResultInfo.put("REASON", "身份证信息验证不通过");
    		blResultInfo.put("BUS_STATE", "0");
    		conditionInfo.put("INFO_CODE", "nameIsSame");
    		conditionInfo.put("INFO_VALUE", "1");
    		IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
    		IDataset blResultInfos = new DatasetList();
    		blResultInfos.add(blResultInfo);
    		resultData.put("BL_RESULT_INFO", blResultInfos);
			results.add(resultData);
			return results;
		}
		if (StringUtils.isEmpty(pstpId)) {
			resultData.put("BIZ_ORDER_RESULT", "3030");
			resultData.put("RESULT_DESC","证件号码不能为空");
			resultData.put("X_RESULTCODE", "3030");
    		resultData.put("X_RESULTINFO", "证件号码不能为空");
    		resultData.put("X_RSPTYPE", "2");
    		resultData.put("X_RSPCODE", "2998");
    		resultData.put("X_RSPDESC", "证件号码不能为空");
    		blResultInfo.put("REASON", "身份证信息验证不通过");
    		blResultInfo.put("BUS_STATE", "0");
    		conditionInfo.put("INFO_CODE", "nameIsSame");
    		conditionInfo.put("INFO_VALUE", "1");
    		IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
    		IDataset blResultInfos = new DatasetList();
    		blResultInfos.add(blResultInfo);
    		resultData.put("BL_RESULT_INFO", blResultInfos);
			results.add(resultData);
			return results;
         } else{
        	 if (!StringUtils.isEmpty(pstpId)) {
        		 boolean checkError = false;
	            if (StringUtils.equals(pstpId, uca.getCustomer().getPsptId())) {
	                if(input.getString("ID_CARD_TYPE","").trim().equals("00")){
	                    if( StringUtils.equals("0", uca.getCustomer().getPsptTypeCode())|| StringUtils.equals("1", uca.getCustomer().getPsptTypeCode())){ 
	                    	
	                    }else{
	                        checkError = true;
	                    }
	                }else{
	                    if( StringUtils.equals(pstpTypeCode, uca.getCustomer().getPsptTypeCode())){                        
	                    }else{
	                        checkError = true;
	                    }
	                }
	            } else {
	            	if(input.getString("ID_CARD_TYPE","").trim().equals("00")){
	            		if( pstpId.length() == 15)
	            		{
	            			pstpId = IdcardUtils.conver15CardTo18(pstpId);
	            			if (StringUtils.equals(pstpId.toUpperCase(), uca.getCustomer().getPsptId().toUpperCase())) {
	            				
	            			}
	            			else{
	                            checkError = true;
	                        } 
			
	            		}
	            		if( uca.getCustomer().getPsptId().length() == 15)
	            		{
	            			String strPstpId = IdcardUtils.conver15CardTo18(uca.getCustomer().getPsptId());
	            			if (StringUtils.equals(pstpId.toUpperCase(), strPstpId.toUpperCase())) {
	            				
	            			}
	            			else{
	                            checkError = true;
	                        } 
	
	            		}
	            	}
	            	else 
	            		checkError = true;
	            }
	            
	            if(checkError){
	            	resultData.put("X_RESULTCODE", "3030");
            		resultData.put("X_RESULTINFO", "证件校验失败");
            		resultData.put("X_RSPTYPE", "2");
            		resultData.put("X_RSPCODE", "2998");
            		resultData.put("X_RSPDESC", "证件校验失败");
                 	resultData.put("BIZ_ORDER_RESULT", "3030");
                 	resultData.put("RESULT_DESC","证件校验失败"); 
             		blResultInfo.put("REASON", "身份证信息验证不通过");
             		blResultInfo.put("BUS_STATE", "0");
             		conditionInfo.put("INFO_CODE", "nameIsSame");
             		conditionInfo.put("INFO_VALUE", "1");
             		IDataset extendInfos = new DatasetList();
                    extendInfos.add(conditionInfo);
                    blResultInfo.put("EXTEND_INFO", extendInfos);
             		IDataset blResultInfos = new DatasetList();
             		blResultInfos.add(blResultInfo);
             		resultData.put("BL_RESULT_INFO", blResultInfos);
                 	results.add(resultData);
         			return results;
	            }}
         }
		String userState = uca.getUser().getUserStateCodeset();
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
        if("1".equals(userState)||"2".equals(userState)){
        	isCorrectStatus = true;
        	errorMsg = "ok";
        }
        if (!isCorrectStatus) {
        	if (StringUtils.equals("5", uca.getUser().getUserStateCodeset()) // 欠费停机
        			|| StringUtils.equals("A", uca.getUser().getUserStateCodeset())) {// 欠费半停机
        		resultData.put("BIZ_ORDER_RESULT", "2005");//2005:鉴权失败（欠费停机）
        		resultData.put("X_RESULTCODE", "2005");
        		errorMsg = "鉴权失败（欠费停机或欠费半停机）";
        	} else {//二级返回码对于状态只有欠费停机与主动停机两种归类，其他的状态错误，只能归类于主动停机了。
        		resultData.put("BIZ_ORDER_RESULT", "2053");//2053:鉴权失败（用户主动停机）
        		resultData.put("X_RESULTCODE", "2053");
        		errorMsg = "鉴权失败（非正常在网用户）";
        	}
        	resultData.put("X_RESULTINFO", errorMsg);
      		resultData.put("X_RSPTYPE", "2");
      		resultData.put("X_RSPCODE", "2998");
      		resultData.put("X_RSPDESC", errorMsg);
    		blResultInfo.put("REASON", errorMsg);
    		blResultInfo.put("BUS_STATE", "0");
    		conditionInfo.put("INFO_CODE", "nameIsSame");
    		conditionInfo.put("INFO_VALUE", "1");
    		IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
        	resultData.put("RESULT_DESC", errorMsg);
        	IDataset blResultInfos = new DatasetList();
    		blResultInfos.add(blResultInfo);
    		resultData.put("BL_RESULT_INFO", blResultInfos);
        	results.add(resultData);
			return results;
        }
        //好友号码验证
        String numberCheck = input.getString("NUMBER_CHECK","");
        try {
        	if(!StringUtils.isBlank(numberCheck)){
        		IDataset numberResults = AcctCall.checkFriend(serialNumber, numberCheck);
        		if(IDataUtil.isNotEmpty(numberResults)){
        			IData numberResult = numberResults.getData(0);
        			if(!"0000".equals(numberResult.getString("RESULT_CODE",""))){
        				if(!checkFailedCount(uca,input.getString("BIZ_TYPE"))){
        					resultData.put("BIZ_ORDER_RESULT", "2046");
        					resultData.put("RESULT_DESC", "鉴权失败超5次，系统已锁定！");
        					resultData.put("X_RESULTCODE", "2046");
        					resultData.put("X_RESULTINFO", "超过次数锁定");
        					resultData.put("X_RSPTYPE", "2");
        					resultData.put("X_RSPCODE", "2998");
        					resultData.put("X_RSPDESC", "失败");
        					blResultInfo.put("BUS_STATE", "0");//办理状态(0：不可办理1：可办理)
        					blResultInfo.put("CONDITION", "1");//办理依赖条件(当BusState为0时填写0：无1：有)
        					blResultInfo.put("REASON", "鉴权失败超5次，系统已锁定！");//不能办理原因说明
        					conditionInfo.put("INFO_CODE", "nameIsSame");
        					conditionInfo.put("INFO_VALUE", "1");
        					IDataset extendInfos = new DatasetList();
        					extendInfos.add(conditionInfo);
        					blResultInfo.put("EXTEND_INFO", extendInfos);
        					IDataset blResultInfos = new DatasetList();
        					blResultInfos.add(blResultInfo);
        					resultData.put("BL_RESULT_INFO", blResultInfos);
        					results.add(resultData);
        					return results;
        				}
        				resultData.put("X_RSPTYPE", "2");
        				resultData.put("X_RSPCODE", "2998");
        				resultData.put("X_RSPDESC", "好友号码" + numberResult.getString("FAILED_NUMBER") + "验证未通过！");
        				resultData.put("X_RESULTCODE", "2057");
        				resultData.put("X_RESULTINFO", "好友号码" + numberResult.getString("FAILED_NUMBER") + "验证未通过！");
        				resultData.put("BIZ_ORDER_RESULT", "2057");
        				blResultInfo.put("REASON", numberResult.getString("FAILED_NUMBER")+"验证不通过");
        				blResultInfo.put("BUS_STATE", "0");
        				blResultInfo.put("CONDITION", "0");
        				IDataset blResultInfos = new DatasetList();
        				blResultInfos.add(blResultInfo);
        				resultData.put("BL_RESULT_INFO", blResultInfos);
        				resultData.put("RESULT_DESC", "好友号码" + numberResult.getString("FAILED_NUMBER") + "验证未通过！");
        				results.add(resultData);
        				return results;
        			}
        		}
        	}
		} catch (Exception e) {
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE", "2998");
			resultData.put("X_RSPDESC",e.getMessage());
			resultData.put("X_RESULTCODE", "2057");
			resultData.put("X_RESULTINFO",e.getMessage());
			resultData.put("BIZ_ORDER_RESULT", "2057");
			blResultInfo.put("REASON", e.getMessage());
			blResultInfo.put("BUS_STATE", "0");
			blResultInfo.put("CONDITION", "0");
			IDataset blResultInfos = new DatasetList();
			blResultInfos.add(blResultInfo);
			resultData.put("BL_RESULT_INFO", blResultInfos);
			resultData.put("RESULT_DESC",e.getMessage());
			results.add(resultData);
			return results;
		}
        //查询用户品牌等信息
        IData userInfo = new DataMap();
        userInfo.put("USER_NAME", uca.getCustomer().getCustName());
        userInfo.put("HOME_PROV", "898");
        userInfo.put("USER_STATUS", userStatus);//是
        String brand = uca.getBrandCode();
    	if("G001".equals(brand)){
    		userInfo.put("BRAND", "00");//01:全球通
    	}else if("G002".equals(brand) || "GS01".equals(brand)){
    		userInfo.put("BRAND", "01");//02:神州行
    	}else if("G010".equals(brand) || "GS03".equals(brand)){
    		userInfo.put("BRAND", "02");//03:动感地带
    	}else{
    		userInfo.put("BRAND", "03");//09:其他品牌
    	}

		//客户星级
		userInfo.put("USER_STAR", this.getUserStar(uca.getUserId()));

		IData ucaInfo = UcaInfoQry.qryUserInfoByUserId(uca.getUserId());
        String category = ucaInfo.getString("PREPAY_TAG","");
        userInfo.put("CATEGORY", category);//是
        if("PWLW".equals(uca.getBrandCode()) || "WLWG".equals(uca.getBrandCode())){
        	userInfo.put("INTER_USER", "0");//是
        }else{
        	userInfo.put("INTER_USER", "1");//是
        }
        
        userInfo.put("IS_SHIMING", "1".equals(uca.getCustomer().getIsRealName())?"0":"1");//是
        IDataset userInfos = new DatasetList();
        userInfos.add(userInfo);
        resultData.put("USER_INFO", userInfos);
		resultData.put("IDENT_CODE", buildCertificate(uca));
		conditionInfo.put("INFO_CODE", "nameIsSame");
		conditionInfo.put("INFO_VALUE", "0");
		IDataset extendInfos = new DatasetList();
        extendInfos.add(conditionInfo);
        blResultInfo.put("EXTEND_INFO", extendInfos);
		IDataset blResultInfos = new DatasetList();
		blResultInfos.add(blResultInfo);
		resultData.put("BL_RESULT_INFO", blResultInfos);
		
		results.add(resultData);
		return results;
	}
	/**
	 * 跨区换卡下发短信验证码与销户短信代发  落地接口
	 * @param input
	 * @return
	 */
	public IDataset simpleCardNotice(IData input)throws Exception{
		IDataset rtnData = new DatasetList();
		IData data = new DataMap();
		try {
			IDataUtil.chkParam(input, "ID_TYPE");
			IDataUtil.chkParam(input, "OPR_TIME");
			IDataUtil.chkParam(input, "OPR_NUMB");
			IDataUtil.chkParam(input, "BIZ_VERSION");
			String idValue = IDataUtil.chkParam(input, "ID_VALUE");
			String bizType = IDataUtil.chkParam(input, "BIZ_TYPE");
			String sendMark = IDataUtil.chkParam(input, "SEND_MARK");
			data.put("X_RESULTCODE", "0000");
			data.put("X_RESULTINFO", "ok");
			data.put("X_RSPTYPE", "0");
			data.put("X_RSPCODE", "0000");
			data.put("X_RSPDESC", "ok");
			UcaData uca = UcaDataFactory.getNormalUca(idValue);
			if("0".equals(sendMark)){//下发短信验证码
				if(uca!=null){
					String serialNumber = uca.getSerialNumber();
		    		Object cacheCode = SharedCache.get(SmsVerifyCodeBean.VERIFY_CODE_CACHE_KEY+serialNumber);
		    		if(null!=cacheCode&&!"".equals(cacheCode)){
		    			data.put("BIZ_ORDER_RESULT", "4042");
		    			data.put("RESULT_DESC", "短信验证码5分钟内不能重复申请！");
		    			data.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		    			data.put("X_RESULTCODE", "4042");
		    			data.put("X_RESULTINFO", "短信验证码5分钟内不能重复申请！");
		    			data.put("X_RSPTYPE", "2");
		    			data.put("X_RSPCODE", "4042");
		    			data.put("X_RSPDESC", "短信验证码5分钟内不能重复申请！");
		    			rtnData.add(data);
		    			return rtnData;
		    		}
					//生成短信验证码
					String verifyCode = RandomStringUtils.randomNumeric(6);
					String msg = "【短信验证码】尊敬的客户，您好！您的跨区换卡业务短信验证码为"+verifyCode+"（5分钟内有效），请注意保密，仅向漫游地移动公司为您办理换卡的服务人员提供。注：如您的卡为特殊卡，换卡后相应的业务将无法使用。【中国移动】";
					String remark = "跨区换卡验证码短信";
					int validMinutes = 5;
					//发送短信通知
					IData inparam = new DataMap();
					inparam.put("NOTICE_CONTENT", msg);
					inparam.put("RECV_OBJECT", serialNumber);
					inparam.put("RECV_ID", serialNumber);
					inparam.put("REFER_STAFF_ID", getVisit().getStaffId());
					inparam.put("REFER_DEPART_ID", getVisit().getDepartId());
					inparam.put("REMARK", remark);
					SmsSend.insSms(inparam);
					//保存短信验证码
					SharedCache.set(SmsVerifyCodeBean.VERIFY_CODE_CACHE_KEY+serialNumber, verifyCode, 60*validMinutes);
					data.put("BIZ_ORDER_RESULT", "0000");
					data.put("RESULT_DESC", "成功");
				}
			}else if("1".equals(sendMark)){
				if("1013".equals(input.getString("BIZ_TYPE"))){
					IDataset messChange = input.getDataset("MESS_CHANGE");
					IDataset config = CommparaInfoQry.getCommparaAllCol("CSM", "2020", "REMOTE_DESTROY_REPLACE_SMS", "0898");
					IData templateConfig = config.getData(0);
					String templateId="";
					String InfoCode="";
					String InfoValue="";
					String content = "";
					if("1".equals(input.getString("MESS_TYPE"))){
						templateId=templateConfig.getString("PARA_CODE1");//销户成功受理短信模板
					}else if("2".equals(input.getString("MESS_TYPE"))){
						templateId=templateConfig.getString("PARA_CODE2");//用户原因销户失败短信模板
					}else if ("3".equals(input.getString("MESS_TYPE"))) {
						templateId=templateConfig.getString("PARA_CODE3");//销户成功短信模板
					}
					//根据模板ID获取短信
					IData smsTemplateData = TemplateQry.qryTemplateContentByTempateId(templateId);
					if(IDataUtil.isNotEmpty(smsTemplateData)){
						content = smsTemplateData.getString("TEMPLATE_CONTENT1","");
					}
					for(int k=0;k<messChange.size();k++){
						InfoCode = messChange.getData(k).getString("INFO_CODE");
						InfoValue = messChange.getData(k).getString("INFO_VALUE");
						content=content.replaceAll(InfoCode, InfoValue);
					}
					IData smsParam = new DataMap();
					smsParam.put("SERIAL_NUMBER", input.getString("ID_VALUE"));//这是联系人号码
					smsParam.put("NOTICE_CONTENT",content);
					sendSms(smsParam);
					data.put("BIZ_ORDER_RESULT", "0000");
					data.put("RESULT_DESC", "成功");
				}
			}else{
				data.put("BIZ_ORDER_RESULT", "4042");
				data.put("RESULT_DESC", "发送标识【SEND_MARK】不正确");
				data.put("X_RESULTCODE", "4042");
    			data.put("X_RESULTINFO", "发送标识【SEND_MARK】不正确");
    			data.put("X_RSPTYPE", "2");
    			data.put("X_RSPCODE", "4042");
    			data.put("X_RSPDESC", "发送标识【SEND_MARK】不正确");
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			data.put("BIZ_ORDER_RESULT", "4042");
			data.put("RESULT_DESC", e.getMessage());
			data.put("X_RESULTCODE", "4042");
			data.put("X_RESULTINFO", e.getMessage());
			data.put("X_RSPTYPE", "2");
			data.put("X_RSPCODE", "4042");
			data.put("X_RSPDESC", e.getMessage());
		}
		data.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		rtnData.add(data);
		return rtnData;
	}

	/**
	 * 跨区换卡 验证码+ICCID验证
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset messageAndICCIDCheck(IData input)throws Exception{
		String strInSimCardNo = IDataUtil.chkParam(input, "ICC_ID");
		String verifyCode = IDataUtil.chkParam(input, "MESSAGE_CHECK");
		String serialNumber = IDataUtil.chkParam(input, "ID_VALUE");
		String idType = IDataUtil.chkParam(input, "ID_TYPE");
		IDataUtil.chkParam(input, "OPR_TIME");
		IDataUtil.chkParam(input, "BIZ_VERSION");
		IDataUtil.chkParam(input, "OPR_NUMB");
		IDataset results = new DatasetList();
		IData resultData = new DataMap();
		resultData.put("ICC_ID", strInSimCardNo);
		resultData.put("MESSAGE_CHECK", verifyCode);
		resultData.put("ID_TYPE", "01");
		resultData.put("ID_VALUE", serialNumber);
		resultData.put("OPR_NUMB", input.getString("OPR_NUMB"));
		resultData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		resultData.put("BIZ_ORDER_RESULT", "0000");
		IData conditionInfo = new DataMap();
		IData extendInfo = new DataMap();
		conditionInfo.put("INFO_CODE", "");
		conditionInfo.put("INFO_VALUE", "");
		IData blResultInfo = new DataMap();
		blResultInfo.put("CONDITION", "0");
		blResultInfo.put("BUS_STATE", "1");
		resultData.put("X_RESULTCODE", "0000");
  		resultData.put("X_RESULTINFO", "ok");
  		resultData.put("X_RSPTYPE", "0");
  		resultData.put("X_RSPCODE", "0000");
  		resultData.put("X_RSPDESC", "ok");
  		UcaData uca = new UcaData();
		try{
			uca = UcaDataFactory.getNormalUca(serialNumber);
			if(uca == null) {
				resultData.put("BIZ_ORDER_RESULT", "2009");
				resultData.put("RESULT_DESC", "用户不存在或用户非正常状态");
				resultData.put("X_RESULTCODE", "2009");
	    		resultData.put("X_RESULTINFO", "用户不存在或用户非正常状态");
	    		resultData.put("X_RSPTYPE", "2");
	    		resultData.put("X_RSPCODE", "2998");
	    		resultData.put("X_RSPDESC", "用户不存在或用户非正常状态");
	    		blResultInfo.put("REASON", "用户不存在或用户非正常状态");
	    		blResultInfo.put("BUS_STATE", "0");
	    		conditionInfo.put("INFO_CODE", "nameIsSame");
	    		conditionInfo.put("INFO_VALUE", "1");
	    		IDataset extendInfos = new DatasetList();
	            extendInfos.add(conditionInfo);
	            blResultInfo.put("EXTEND_INFO", extendInfos);
	    		IDataset blResultInfos = new DatasetList();
	    		blResultInfos.add(blResultInfo);
	    		resultData.put("BL_RESULT_INFO", blResultInfos);
				results.add(resultData);
				return results;
			}else {
	    		String userId = uca.getUser().getUserId();
	    		Object cacheCode = SharedCache.get(SmsVerifyCodeBean.VERIFY_CODE_CACHE_KEY+serialNumber);
	    		System.out.println("cacheCode========="+cacheCode);
	    		if(null == cacheCode || cacheCode.equals("")){
	    			resultData.put("BIZ_ORDER_RESULT", "2068");
	    			resultData.put("RESULT_DESC", "短信验证码无效！");
	    			resultData.put("X_RESULTCODE", "2068");
	        		resultData.put("X_RESULTINFO", "短信验证码无效！");
	        		resultData.put("X_RSPTYPE", "2");
	        		resultData.put("X_RSPCODE", "2068");
	        		resultData.put("X_RSPDESC", "短信验证码无效！");
	        		blResultInfo.put("REASON", "短信验证码无效！");
	        		blResultInfo.put("BUS_STATE", "0");
	        		conditionInfo.put("INFO_CODE", "nameIsSame");
	        		conditionInfo.put("INFO_VALUE", "1");
	        		IDataset extendInfos = new DatasetList();
	                extendInfos.add(conditionInfo);
	                blResultInfo.put("EXTEND_INFO", extendInfos);
	        		IDataset blResultInfos = new DatasetList();
	        		blResultInfos.add(blResultInfo);
	        		resultData.put("BL_RESULT_INFO", blResultInfos);
	    			results.add(resultData);
	    			return results;
	    		}
	    		if(cacheCode.equals(verifyCode)){
	    			SharedCache.delete(SmsVerifyCodeBean.VERIFY_CODE_CACHE_KEY+serialNumber);
	    			IData userSimInfo = PasswdAssistant.getUserSimInfo(userId);
	    	        String strSimCardNo = userSimInfo.getString("RES_CODE");
	    	        if (!StringUtils.equals(strInSimCardNo, strSimCardNo))
	    	        {
	    	        	IDataset simCardInfos = ResCall.getSimCardInfo("0", strSimCardNo, "", "1");
	    	            if (IDataUtil.isEmpty(simCardInfos))
	    	            {
	    	                resultData.put("BIZ_ORDER_RESULT", "4048");
	    	    			resultData.put("RESULT_DESC", "获取用户SIM卡信息失败");
	    	    			resultData.put("X_RESULTCODE", "4048");
	    	        		resultData.put("X_RESULTINFO", "获取用户SIM卡信息失败");
	    	        		resultData.put("X_RSPTYPE", "2");
	    	        		resultData.put("X_RSPCODE", "4048");
	    	        		resultData.put("X_RSPDESC", "获取用户SIM卡信息失败");
	    	        		blResultInfo.put("REASON", "获取用户SIM卡信息失败");
	    	        		blResultInfo.put("BUS_STATE", "0");
	    	        		conditionInfo.put("INFO_CODE", "nameIsSame");
	    	        		conditionInfo.put("INFO_VALUE", "1");
	    	        		IDataset extendInfos = new DatasetList();
	    	                extendInfos.add(conditionInfo);
	    	                blResultInfo.put("EXTEND_INFO", extendInfos);
	    	        		IDataset blResultInfos = new DatasetList();
	    	        		blResultInfos.add(blResultInfo);
	    	        		resultData.put("BL_RESULT_INFO", blResultInfos);
	    	    			results.add(resultData);
	    	    			return results;
	    	                
	    	            }
	    	            String emptyCardId = simCardInfos.getData(0).getString("EMPTY_CARD_ID", "");
	    	            if (!StringUtils.equals(strInSimCardNo, emptyCardId))
	    	            {
	    	                resultData.put("BIZ_ORDER_RESULT", "4048");
	    	    			resultData.put("RESULT_DESC", "SIM卡号（或白卡号）不正确");
	    	    			resultData.put("X_RESULTCODE", "4048");
	    	        		resultData.put("X_RESULTINFO", "SIM卡号（或白卡号）不正确");
	    	        		resultData.put("X_RSPTYPE", "2");
	    	        		resultData.put("X_RSPCODE", "4048");
	    	        		resultData.put("X_RSPDESC", "SIM卡号（或白卡号）不正确");
	    	        		blResultInfo.put("REASON", "SIM卡号（或白卡号）不正确");
	    	        		blResultInfo.put("BUS_STATE", "0");
	    	        		conditionInfo.put("INFO_CODE", "nameIsSame");
	    	        		conditionInfo.put("INFO_VALUE", "1");
	    	        		IDataset extendInfos = new DatasetList();
	    	                extendInfos.add(conditionInfo);
	    	                blResultInfo.put("EXTEND_INFO", extendInfos);
	    	        		IDataset blResultInfos = new DatasetList();
	    	        		blResultInfos.add(blResultInfo);
	    	        		resultData.put("BL_RESULT_INFO", blResultInfos);
	    	    			results.add(resultData);
	    	    			return results;
	    	            }
	    	        }
	    		}else{
	    			resultData.put("BIZ_ORDER_RESULT", "2068");
	    			resultData.put("RESULT_DESC", "短信验证码不正确！");
	    			
	    			resultData.put("BIZ_ORDER_RESULT", "2068");
	    			resultData.put("RESULT_DESC", "短信验证码不正确！");
	    			resultData.put("X_RESULTCODE", "2068");
	        		resultData.put("X_RESULTINFO", "短信验证码不正确！");
	        		resultData.put("X_RSPTYPE", "2");
	        		resultData.put("X_RSPCODE", "2068");
	        		resultData.put("X_RSPDESC", "短信验证码不正确！");
	        		blResultInfo.put("REASON", "短信验证码不正确！");
	        		blResultInfo.put("BUS_STATE", "0");
	        		conditionInfo.put("INFO_CODE", "nameIsSame");
	        		conditionInfo.put("INFO_VALUE", "1");
	        		IDataset extendInfos = new DatasetList();
	                extendInfos.add(conditionInfo);
	                blResultInfo.put("EXTEND_INFO", extendInfos);
	        		IDataset blResultInfos = new DatasetList();
	        		blResultInfos.add(blResultInfo);
	        		resultData.put("BL_RESULT_INFO", blResultInfos);
	    			results.add(resultData);
	    			return results;
	    		}
	    	
			}
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
	        		resultData.put("BIZ_ORDER_RESULT", "2005");//2005:鉴权失败（欠费停机）
	        		resultData.put("X_RESULTCODE", "2005");
	        		errorMsg = "鉴权失败（欠费停机或欠费半停机）";
	        	} else {//二级返回码对于状态只有欠费停机与主动停机两种归类，其他的状态错误，只能归类于主动停机了。
	        		resultData.put("BIZ_ORDER_RESULT", "2053");//2053:鉴权失败（用户主动停机）
	        		resultData.put("X_RESULTCODE", "2053");
	        		errorMsg = "鉴权失败（非正常在网用户）";
	        	}
	        	resultData.put("X_RESULTINFO", errorMsg);
	      		resultData.put("X_RSPTYPE", "2");
	      		resultData.put("X_RSPCODE", "2998");
	      		resultData.put("X_RSPDESC", errorMsg);
	    		blResultInfo.put("REASON", errorMsg);
	    		blResultInfo.put("BUS_STATE", "0");
	    		conditionInfo.put("INFO_CODE", "nameIsSame");
	    		conditionInfo.put("INFO_VALUE", "1");
	    		IDataset extendInfos = new DatasetList();
	            extendInfos.add(conditionInfo);
	            blResultInfo.put("EXTEND_INFO", extendInfos);
	        	resultData.put("RESULT_DESC", errorMsg);
	        	IDataset blResultInfos = new DatasetList();
	    		blResultInfos.add(blResultInfo);
	    		resultData.put("BL_RESULT_INFO", blResultInfos);
	        	results.add(resultData);
				return results;
	        }
	        
	        //查询用户品牌等信息
	        IData userInfo = new DataMap();
	        userInfo.put("USER_NAME", uca.getCustomer().getCustName());
	        userInfo.put("HOME_PROV", "898");
	        userInfo.put("USER_STATUS", userStatus);//是
	        String brand = uca.getBrandCode();
	    	if("G001".equals(brand)){
	    		userInfo.put("BRAND", "00");//01:全球通
	    	}else if("G002".equals(brand) || "GS01".equals(brand)){
	    		userInfo.put("BRAND", "01");//02:神州行
	    	}else if("G010".equals(brand) || "GS03".equals(brand)){
	    		userInfo.put("BRAND", "02");//03:动感地带
	    	}else{
	    		userInfo.put("BRAND", "03");//09:其他品牌
	    	}

			//客户星级
			userInfo.put("USER_STAR", this.getUserStar(uca.getUserId()));

			IData ucaInfo = UcaInfoQry.qryUserInfoByUserId(uca.getUserId());
	        String category = ucaInfo.getString("PREPAY_TAG","");
	        userInfo.put("CATEGORY", category);//是
	        if("PWLW".equals(uca.getBrandCode()) || "WLWG".equals(uca.getBrandCode())){
	        	userInfo.put("INTER_USER", "0");//是
	        }else{
	        	userInfo.put("INTER_USER", "1");//是
	        }
	        userInfo.put("IS_SHIMING", "1".equals(uca.getCustomer().getIsRealName())?"0":"1");//是
	        IDataset userInfos = new DatasetList();
	        userInfos.add(userInfo);
	        resultData.put("USER_INFO", userInfos);
			resultData.put("IDENT_CODE", buildCertificate(uca));
			conditionInfo.put("INFO_CODE", "nameIsSame");
			conditionInfo.put("INFO_VALUE", "0");
			IDataset extendInfos = new DatasetList();
	        extendInfos.add(conditionInfo);
	        blResultInfo.put("EXTEND_INFO", extendInfos);
			IDataset blResultInfos = new DatasetList();
			blResultInfos.add(blResultInfo);
			resultData.put("BL_RESULT_INFO", blResultInfos);
			results.add(resultData);
			return results;
		}catch(Exception e){
			log.error(e.getMessage(),e);
			resultData.put("X_RESULTCODE", "2999");
    		resultData.put("X_RESULTINFO", e.getMessage());
    		resultData.put("X_RSPTYPE", "2");
    		resultData.put("X_RSPCODE", "2999");
    		resultData.put("X_RSPDESC", e.getMessage());
			resultData.put("BIZ_ORDER_RESULT", "2999");
			resultData.put("RESULT_DESC", e.getMessage());
			blResultInfo.put("BUS_STATE", "0");//办理状态(0：不可办理1：可办理)
    	    blResultInfo.put("CONDITION", "1");//办理依赖条件(当BusState为0时填写0：无1：有)
    	    blResultInfo.put("REASON", "用户不存在！");//不能办理原因说明
    	    conditionInfo.put("INFO_CODE", "nameIsSame");
    	    conditionInfo.put("INFO_VALUE", "1");
    	    IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
    	    IDataset blResultInfos = new DatasetList();
    	    blResultInfos.add(blResultInfo);
    	    resultData.put("BL_RESULT_INFO", blResultInfos);
			results.add(resultData);
			return results;
		}
		
	}
	
	/**
	 * 好友查询落地接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset numCheckQuery(IData input)throws Exception{
		IDataset rtnList = new DatasetList();
		IData rtnMap = new DataMap();
		rtnMap.put("BIZ_ORDER_RESULT", "0000");
		rtnMap.put("RESULT_DESC", "成功");
		rtnMap.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		rtnMap.put("X_RESULTCODE", "0000");
		rtnMap.put("X_RESULTINFO", "ok");
		rtnMap.put("X_RSPTYPE", "0");
		rtnMap.put("X_RSPCODE", "0000");
		rtnMap.put("X_RSPDESC", "ok");
		try {
			String bizType = IDataUtil.chkParam(input, "BIZ_TYPE");
			String idVlaue = IDataUtil.chkParam(input, "ID_VALUE");
			IDataUtil.chkParam(input, "ID_TYPE");
			IDataUtil.chkParam(input, "OPR_TIME");
			IDataUtil.chkParam(input, "OPR_NUMB");
			IDataUtil.chkParam(input, "BIZ_VERSION");
			UcaData uca = UcaDataFactory.getNormalUca(idVlaue);
			if(uca!=null){
				IDataset friendList = AcctCall.queryFriend(idVlaue);
				if(IDataUtil.isNotEmpty(friendList)){
					IData friend = friendList.getData(0);
					String acctRtnCode = friend.getString("BIZ_ORDER_RESULT");
					String acctRtnDesc = friend.getString("RESULT_DESC");
					String counts = friend.getString("NUM_COUNT");
					if("0000".equals(acctRtnCode)){
						rtnMap.put("NUM_COUNT", counts);
					}else{
						rtnMap.put("BIZ_ORDER_RESULT", acctRtnCode);
						rtnMap.put("RESULT_DESC", acctRtnDesc);
						rtnMap.put("X_RESULTCODE", "2998");
						rtnMap.put("X_RESULTINFO", acctRtnDesc);
						rtnMap.put("X_RSPTYPE", "2");
						rtnMap.put("X_RSPCODE", "2998");
						rtnMap.put("X_RSPDESC", acctRtnDesc);
					}
				}else{
					rtnMap.put("BIZ_ORDER_RESULT", "2998");
					rtnMap.put("RESULT_DESC", "好友查询失败，账务未返回失败原因");
					rtnMap.put("X_RESULTCODE", "2998");
					rtnMap.put("X_RESULTINFO", "好友查询失败，账务未返回失败原因");
					rtnMap.put("X_RSPTYPE", "2");
					rtnMap.put("X_RSPCODE", "2998");
					rtnMap.put("X_RSPDESC", "好友查询失败，账务未返回失败原因");
				}
			}else{
				rtnMap.put("BIZ_ORDER_RESULT", "2009");
				rtnMap.put("RESULT_DESC", "用户不存在或已销户");
				rtnMap.put("X_RESULTCODE", "2009");
				rtnMap.put("X_RESULTINFO", "用户不存在或已销户");
				rtnMap.put("X_RSPTYPE", "2");
				rtnMap.put("X_RSPCODE", "2009");
				rtnMap.put("X_RSPDESC", "用户不存在或已销户");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);;
			rtnMap.put("BIZ_ORDER_RESULT", "2999");
			rtnMap.put("RESULT_DESC", e.getMessage());
			rtnMap.put("X_RESULTCODE", "2999");
			rtnMap.put("X_RESULTINFO", e.getMessage());
			rtnMap.put("X_RSPTYPE", "2");
			rtnMap.put("X_RSPCODE", "2999");
			rtnMap.put("X_RSPDESC", e.getMessage());
		}
		rtnList.add(rtnMap);
		return rtnList;
	}
	
	
	
	/**
     * 获取全网证件类型编码
     */
	 private String getCrmPsptTypeParam(String param)
	 {   
	        //将海南个性化定义字典转为平台要求的全网编码
	        IData psptTypeData = new DataMap();
	        //海南个性化定义字典 来源于td_s_static type_id=TD_S_PASSPORTTYPE2
	        psptTypeData.put("00","0" );// 0:本地身份证 <-->00:身份证件
	        psptTypeData.put("00","1" );// 1:外地身份证<-->00:身份证件
	        psptTypeData.put("11","2" );// 2:户口本<-->11:户口簿
	        psptTypeData.put("02","A" );// A:护照<-->02:护照
	        psptTypeData.put("04","C" );// C:军官证<-->04:军官证
	        psptTypeData.put("99","D" );// D:单位证明<-->99:其他证件
	        psptTypeData.put("99","E" );// E:营业执照<-->99:其他证件
	        psptTypeData.put("99","G" );// G:事业单位法人证书<-->99:其他证件
	        psptTypeData.put("99","H" );// H:港澳居民回乡证<-->99:其他证件
	        psptTypeData.put("99","I" );// I:台湾居民回乡证<-->99:其他证件
	        psptTypeData.put("99","J" );// J:港澳通行证<-->99:其他证件
	        psptTypeData.put("99","L" );// L:社会团体法人登记证书<-->99:其他证件
	        psptTypeData.put("99","M" );// M:组织机构代码证<-->99:其他证件
	        psptTypeData.put("13","N" );// N:台湾居民来往大陆通行证<-->13:台湾居民来往大陆通行证
	        psptTypeData.put("12","O" );// O:港澳居民来往内地通行证<-->12:港澳居民往来内地通行证
	        psptTypeData.put("14","R");// R:外国人永久居住身份证-->14:外国人永久居住身份证
	        

	        return psptTypeData.getString(param,"");//其他的转换为99:其他证件
	        //全网证件编码中01:VIP卡，05:武装警察身份证，10:临时居民身份证 在海南本地字典没有。若后面添加，不修改代码的话，会转换为99:其他证件
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
     * 生成用户凭证
     * @param uca
     * @return
     * @throws Exception
     */
    public String buildCertificate(UcaData uca) throws Exception {
		IDataset out = UserIdentInfoQry.getseqString();
		String strDate = DateFormatUtils.format(Calendar.getInstance().getTime(),"yyyyMMdd");
	    String seqId = ((IData) out.get(0)).getString("OUTSTR", "");
	    String identCode = "IDENTCODE"+strDate+((BizVisit)BaseBean.getVisit()).getStaffEparchyCode()+seqId;
	    String registTime = SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss");
		int effctTime = 1800;
	    String endDate = SysDateMgr.date2String(DateUtils.addSeconds(SysDateMgr.string2Date(registTime,"yyyy-MM-dd HH:mm:ss"),effctTime), "yyyy-MM-dd HH:mm:ss");
	    IData reqData=new DataMap();
		// 身份凭证信息入库
	    reqData.put("IDENT_CODE_LEVEL", "01");
	    reqData.put("IDENT_CODE_TYPE", "01");
	    reqData.put("HOME_PROVINCE", "898");
	    reqData.put("START_DATE",registTime);
	    reqData.put("END_DATE",endDate);
	    reqData.put("IDENT_CODE", identCode);
	    reqData.put("USER_TYPE", "01");
	    reqData.put("USER_ID", uca.getUserId());
	    reqData.put("CUST_ID", uca.getCustId());
	    reqData.put("EFFECTIVE_TIME", effctTime);
	    reqData.put("BUSINESS_CODE", "1011");
	    reqData.put("SERIAL_NUMBER", uca.getSerialNumber());
		Dao.executeUpdateByCodeCode("TF_B_IDENTCARD_MANAGE", "INS_TEMP_IDENT", reqData);
		return identCode;
	}

	/**
	 * 查询客户星级
	 *
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private String getUserStar(String userId) throws Exception {
		String userStar = "";
		IDataset oData = CreditCall.getCreditInfo(userId, "0");
		if (IDataUtil.isNotEmpty(oData)) {
			String creditClassvalue = oData.getData(0).getString("CREDIT_CLASS", "0");
			if ("-1".equals(oData.getData(0).getString("CREDIT_CLASS", "")) || "".equals(oData.getData(0).getString("CREDIT_CLASS", "")) || oData.getData(0).getString("CREDIT_CLASS", "") == null
					|| "0".equals(oData.getData(0).getString("CREDIT_CLASS", ""))) {
				userStar = "09";//未评级
			} else {
				if ("0".equals(creditClassvalue)) {
					userStar = "00";//准星
				} else if ("1".equals(creditClassvalue)) {
					userStar = "01";//一星
				} else if ("2".equals(creditClassvalue)) {
					userStar = "02";//二星
				} else if ("3".equals(creditClassvalue)) {
					userStar = "03";//三星
				} else if ("4".equals(creditClassvalue)) {
					userStar = "04";//四星
				} else if ("5".equals(creditClassvalue)) {
					userStar = "05";//五星银
				} else if ("6".equals(creditClassvalue)) {
					userStar = "06";//五星金
				} else if ("7".equals(creditClassvalue)) {
					userStar = "07";//五星钻
				} else {
					userStar = "09";//未评级
				}
			}
		} else {
			userStar = "09";//未评级
		}
		return userStar;
	}

    /**
     * 获取全网证件类型编码
     */
    private String getIBossPsptTypeParam(String param)
    {
    	//将陕西个性化定义字典转为平台要求的全网编码
        IData psptTypeData = new DataMap();
        //陕西个性化定义字典 来源于td_s_static type_id=PSPT_TYPE_CODE
        psptTypeData.put("0", "00");// 0:本地身份证 -->00:身份证件
        psptTypeData.put("1", "00");// 1:身份证 -->00:身份证件
        psptTypeData.put("A", "02");// A:护照-->02:护照
        psptTypeData.put("C", "04");// C:军官证-->04:军官证
        psptTypeData.put("G", "11");// G:户口簿-->11:户口簿
        psptTypeData.put("H", "05");// H:警官证-->05:武装警察身份证
        psptTypeData.put("I", "12");// I:港澳居民内地通行证-->12:港澳居民往来内地通行证
        psptTypeData.put("E", "99");// E:营业执照-->99:其他证件
        psptTypeData.put("K", "99");// K:组织机构代码证-->99:其他证件
        psptTypeData.put("M", "99");// M:社会保险号-->99:其他证件
        psptTypeData.put("J", "13");// J:台湾居民往来大陆通行证-->13:台湾居民来往大陆通行证        

        return psptTypeData.getString(param,"99");//其他的转换为99:其他证件
        
    }
    
    public boolean checkFailedCount(UcaData uca,String bizType) throws Exception{
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
     * 跨区销户前校验  落地接口
     * @param input
     * @return
     * @throws Exception
     */
    private IDataset destroyBeforeCheck(IData input) throws Exception {

		IDataset results = new DatasetList();
		IData resultData = new DataMap();
		String serialNumber = IDataUtil.chkParam(input, "ID_VALUE");
		IDataUtil.chkParam(input, "BIZ_VERSION");
		IDataUtil.chkParam(input, "ID_TYPE");
		resultData.put("ID_TYPE", "01");
		resultData.put("ID_VALUE", serialNumber);
		resultData.put("OPR_NUMB", input.getString("OPR_NUMB",""));
		resultData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		resultData.put("BIZ_ORDER_RESULT", "0000");
		IData conditionInfo = new DataMap();
		IData extendInfo = new DataMap();
		IData blResultInfo = new DataMap();
		blResultInfo.put("CONDITION", "0");
		blResultInfo.put("BUS_STATE", "1");
		resultData.put("X_RESULTCODE", "0000");
  		resultData.put("X_RESULTINFO", "ok");
  		resultData.put("X_RSPTYPE", "0");
  		resultData.put("X_RSPCODE", "0000");
  		resultData.put("X_RSPDESC", "ok");
  		UcaData uca = new UcaData();
		try{
			uca = UcaDataFactory.getNormalUca(serialNumber);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			resultData.put("X_RESULTCODE", "2009");
    		resultData.put("X_RESULTINFO", e.getMessage());
    		resultData.put("X_RSPTYPE", "2");
    		resultData.put("X_RSPCODE", "2009");
    		resultData.put("X_RSPDESC", "用户不存在或用户非正常状态");
			resultData.put("BIZ_ORDER_RESULT", "2009");
			resultData.put("RESULT_DESC", e.getMessage());
			blResultInfo.put("BUS_STATE", "0");//办理状态(0：不可办理1：可办理)
    	    blResultInfo.put("CONDITION", "1");//办理依赖条件(当BusState为0时填写0：无1：有)
    	    blResultInfo.put("REASON", e.getMessage());//不能办理原因说明
    	    conditionInfo.put("INFO_CODE", "nameIsSame");
    	    conditionInfo.put("INFO_VALUE", "1");
    	    IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
    	    IDataset blResultInfos = new DatasetList();
    	    blResultInfos.add(blResultInfo);
    	    resultData.put("BL_RESULT_INFO", blResultInfos);
			results.add(resultData);
			return results;
		}
		if(uca == null) {
			resultData.put("BIZ_ORDER_RESULT", "2009");
			resultData.put("RESULT_DESC", "用户不存在");
			resultData.put("X_RESULTCODE", "2009");
	  		resultData.put("X_RESULTINFO", "用户不存在");
	  		resultData.put("X_RSPTYPE", "2");
	  		resultData.put("X_RSPCODE", "2009");
	  		resultData.put("X_RSPDESC", "用户不存在");
    		blResultInfo.put("REASON", "用户不存在");
    		blResultInfo.put("BUS_STATE", "0");
    		conditionInfo.put("INFO_CODE", "nameIsSame");
    		conditionInfo.put("INFO_VALUE", "1");
    		IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
    		IDataset blResultInfos = new DatasetList();
    		blResultInfos.add(blResultInfo);
    		resultData.put("BL_RESULT_INFO", blResultInfos);
        	results.add(resultData);
			return results;
		}
		
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
        	resultData.put("BIZ_ORDER_RESULT", "2005");
        	resultData.put("X_RESULTINFO", errorMsg);
        	resultData.put("X_RESULTCODE", "2005");
      		resultData.put("X_RSPTYPE", "2");
      		resultData.put("X_RSPCODE", "2005");
      		resultData.put("X_RSPDESC", errorMsg);
        	blResultInfo.put("REASON", errorMsg);
    		blResultInfo.put("BUS_STATE", "0");
    		conditionInfo.put("INFO_CODE", "nameIsSame");
    		conditionInfo.put("INFO_VALUE", "1");
    		IDataset extendInfos = new DatasetList();
            extendInfos.add(conditionInfo);
            blResultInfo.put("EXTEND_INFO", extendInfos);
        	resultData.put("RESULT_DESC", errorMsg);
        	IDataset blResultInfos = new DatasetList();
    		blResultInfos.add(blResultInfo);
    		resultData.put("BL_RESULT_INFO", blResultInfos);
        	results.add(resultData);
			return results;
        }
		IData preCheckData = new DataMap();
		preCheckData.put("TRADE_TYPE_CODE", "192");//立即销户
		preCheckData.put("SERIAL_NUMBER", serialNumber);
		preCheckData.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);//预受理
		preCheckData.put(Route.ROUTE_EPARCHY_CODE, uca.getUserEparchyCode());
		boolean isSuc = false;
		try {
			IDataset dataset = CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", preCheckData);
			if (IDataUtil.isNotEmpty(dataset)) {
				IData temp = dataset.getData(0);
				if (StringUtils.isNotEmpty(temp.getString("TRADE_ID"))) {
					isSuc = true;
				}
			}
			if (isSuc) {
				//预受理成功,不做处理
			} else {
				//预受理失败
				resultData.put("X_RESULTCODE", "3006");
				resultData.put("X_RESULTINFO", "号码存在销号业务办理条件的限制，不允许销户");
				resultData.put("X_RSPTYPE", "2");
				resultData.put("X_RSPCODE", "3006");
				resultData.put("X_RSPDESC", "失败");
				resultData.put("BIZ_ORDER_RESULT", "3006");
				resultData.put("RESULT_DESC", "号码存在销号业务办理条件的限制，不允许销户");
				blResultInfo.put("REASON", "号码存在销号业务办理条件的限制，不允许销户");
				blResultInfo.put("BUS_STATE", "0");
				blResultInfo.put("CONDITION", "1");
				conditionInfo.put("INFO_CODE", "nameIsSame");
				conditionInfo.put("INFO_VALUE", "1");
				IDataset extendInfos = new DatasetList();
				extendInfos.add(conditionInfo);
				blResultInfo.put("EXTEND_INFO", extendInfos);
				IDataset blResultInfos = new DatasetList();
				blResultInfos.add(blResultInfo);
				resultData.put("BL_RESULT_INFO", blResultInfos);
				results.add(resultData);
				return results;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			resultData.put("X_RESULTCODE", "3006");
			resultData.put("X_RESULTINFO", e.getMessage());
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE", "3006");
			resultData.put("X_RSPDESC", "失败");
			resultData.put("BIZ_ORDER_RESULT", "3006");
			resultData.put("RESULT_DESC", e.getMessage());
			blResultInfo.put("REASON", e.getMessage());
			blResultInfo.put("BUS_STATE", "0");
			blResultInfo.put("CONDITION", "1");
			conditionInfo.put("INFO_CODE", "nameIsSame");
			conditionInfo.put("INFO_VALUE", "1");
			IDataset extendInfos = new DatasetList();
			extendInfos.add(conditionInfo);
			blResultInfo.put("EXTEND_INFO", extendInfos);
			IDataset blResultInfos = new DatasetList();
			blResultInfos.add(blResultInfo);
			resultData.put("BL_RESULT_INFO", blResultInfos);
			results.add(resultData);
			return results;
		}


        //查询用户品牌等信息
        IData userInfo = new DataMap();
        userInfo.put("USER_NAME", uca.getCustomer().getCustName());
        userInfo.put("HOME_PROV", "898");
        userInfo.put("USER_STATUS", userStatus);//是
        String brand = uca.getBrandCode();
    	if("G001".equals(brand)){
    		userInfo.put("BRAND", "00");//01:全球通
    	}else if("G002".equals(brand) || "GS01".equals(brand)){
    		userInfo.put("BRAND", "01");//02:神州行
    	}else if("G010".equals(brand) || "GS03".equals(brand)){
    		userInfo.put("BRAND", "02");//03:动感地带
    	}else{
    		userInfo.put("BRAND", "03");//09:其他品牌
    	}

		//客户星级
		userInfo.put("USER_STAR", this.getUserStar(uca.getUserId()));

		IData ucaInfo = UcaInfoQry.qryUserInfoByUserId(uca.getUserId());
        String category = ucaInfo.getString("PREPAY_TAG","");
        userInfo.put("CATEGORY", category);//是
        if("PWLW".equals(uca.getBrandCode()) || "WLWG".equals(uca.getBrandCode())){
        	userInfo.put("INTER_USER", "0");//是
        }else{
        	userInfo.put("INTER_USER", "1");//是
        }
        conditionInfo.put("INFO_CODE", "nameIsSame");
		conditionInfo.put("INFO_VALUE", "0");
		IDataset extendInfos = new DatasetList();
        extendInfos.add(conditionInfo);
        blResultInfo.put("EXTEND_INFO", extendInfos);
        userInfo.put("IS_SHIMING", "1".equals(uca.getCustomer().getIsRealName())?"0":"1");//是
        IDataset userInfos = new DatasetList();
        userInfos.add(userInfo);
        resultData.put("USER_INFO", userInfos);
        //生成用户凭证
        IData certificateParam = new DataMap();
        certificateParam.put("USER_ID", uca.getUserId());
        certificateParam.put("CUST_ID", uca.getCustId());
        certificateParam.put("BIZ_TYPE", input.getString("BIZ_TYPE","1017"));
        certificateParam.put("ID_VALUE", input.getString("ID_VALUE"));
		resultData.put("IDENT_CODE", buildUserCertificate(certificateParam));
		IDataset blResultInfos = new DatasetList();
		blResultInfos.add(blResultInfo);
		resultData.put("BL_RESULT_INFO", blResultInfos);
		
		results.add(resultData);
		return results;
	
	}

    /**
     * 生成销户凭证  将BIZ_TYPE编码记入TF_B_IDENTCARD_MANAGE表的BUSINESS_CODE字段
     * @param input
     * @return
     * @throws Exception
     */
    public String buildUserCertificate(IData input) throws Exception {
		IDataset out = UserIdentInfoQry.getseqString();
		String strDate = DateFormatUtils.format(Calendar.getInstance().getTime(),"yyyyMMdd");
	    String seqId = ((IData) out.get(0)).getString("OUTSTR", "");
	    String identCode = "IDENTCODE"+strDate+((BizVisit)BaseBean.getVisit()).getStaffEparchyCode()+seqId;
	    String registTime = SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss");
		int effctTime = 1800;
	    String endDate = SysDateMgr.date2String(DateUtils.addSeconds(SysDateMgr.string2Date(registTime,"yyyy-MM-dd HH:mm:ss"),effctTime), "yyyy-MM-dd HH:mm:ss");
	    IData reqData=new DataMap();
		// 身份凭证信息入库
	    reqData.put("IDENT_CODE_LEVEL", "01");
	    reqData.put("IDENT_CODE_TYPE", "01");
	    reqData.put("HOME_PROVINCE", "898");
	    reqData.put("START_DATE",registTime);
	    reqData.put("END_DATE",endDate);
	    reqData.put("IDENT_CODE", identCode);
	    reqData.put("USER_TYPE", "01");
	    reqData.put("USER_ID", input.getString("USER_ID"));
	    reqData.put("CUST_ID", input.getString("CUST_ID"));
	    reqData.put("EFFECTIVE_TIME", effctTime);
	    reqData.put("BUSINESS_CODE", input.getString("BIZ_TYPE"));
	    reqData.put("SERIAL_NUMBER", input.getString("ID_VALUE"));
		Dao.executeUpdateByCodeCode("TF_B_IDENTCARD_MANAGE", "INS_TEMP_IDENT", reqData);
		return identCode;
	}
	/**
	 * 短信发送
	 *
	 * @param input
	 * @throws Exception
	 */
	private void sendSms(IData input) throws Exception {
		IData sendInfo = new DataMap();
		sendInfo.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(input.getString("SERIAL_NUMBER")));
		sendInfo.put("RECV_OBJECT", input.getString("SERIAL_NUMBER"));
		sendInfo.put("RECV_ID", input.getString("SERIAL_NUMBER"));
		sendInfo.put("SMS_PRIORITY", "50");
		sendInfo.put("NOTICE_CONTENT", input.getString("NOTICE_CONTENT"));
		sendInfo.put("REMARK", "跨区销户归属省受理");
		sendInfo.put("FORCE_OBJECT", "10086");
		SmsSend.insSms(sendInfo, RouteInfoQry.getEparchyCodeBySn(input.getString("SERIAL_NUMBER")));
	}
	public IDataset sendAndCheckSecurityCode(IData input) throws Exception{
		String sn = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String sendMark = IDataUtil.chkParam(input, "SEND_MARK");
		IDataset rtnlist = new DatasetList();
		IData rtnData = new DataMap();
		rtnData.put("X_RESULTCODE", "0000");
		rtnData.put("X_RESULTINFO", "ok");
		rtnData.put("X_RSPTYPE", "0");
		rtnData.put("X_RSPCODE", "0000");
		rtnData.put("X_RSPDESC", "ok");
		UcaData uca = UcaDataFactory.getNormalUca(sn);
		if("0".equals(sendMark)){//下发短信验证码
			if(uca!=null){
				String serialNumber = uca.getSerialNumber();
	    		Object cacheCode = SharedCache.get(SmsVerifyCodeBean.VERIFY_CODE_CACHE_KEY+serialNumber);
	    		if(null!=cacheCode&&!"".equals(cacheCode)){
	    			rtnData.put("RESULT_CODE", "4042");
	    			rtnData.put("RESULT_DESC", "短信验证码5分钟内不能重复申请！");
	    			rtnData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	    			rtnData.put("X_RESULTCODE", "4042");
	    			rtnData.put("X_RESULTINFO", "短信验证码5分钟内不能重复申请！");
	    			rtnData.put("X_RSPTYPE", "2");
	    			rtnData.put("X_RSPCODE", "4042");
	    			rtnData.put("X_RSPDESC", "短信验证码5分钟内不能重复申请！");
	    			rtnlist.add(rtnData);
	    			return rtnlist;
	    		}
				//生成短信验证码
				String verifyCode = RandomStringUtils.randomNumeric(6);
				IDataset config = CommparaInfoQry.getCommparaAllCol("CSM", "2020", "SECURITYCODE_SMSCONTENT_TIME", "0898");
				IData templateConfig = config.getData(0);
				String content = "";
				String templateId=templateConfig.getString("PARA_CODE1");
				//根据模板ID获取短信
				IData smsTemplateData = TemplateQry.qryTemplateContentByTempateId(templateId);
				if(IDataUtil.isNotEmpty(smsTemplateData)){
					content = smsTemplateData.getString("TEMPLATE_CONTENT1","");
				}
				content=content.replace("%VERIFYCODE%", verifyCode);
				String remark = "短信验证码";
				int validMinutes = 5;
				if(StringUtils.isNotBlank(templateConfig.getString("PARA_CODE2"))){
					validMinutes=Integer.parseInt(templateConfig.getString("PARA_CODE2"));
				}
				//发送短信通知
				IData inparam = new DataMap();
				inparam.put("NOTICE_CONTENT", content);
				inparam.put("RECV_OBJECT", serialNumber);
				inparam.put("RECV_ID", serialNumber);
				inparam.put("REFER_STAFF_ID", getVisit().getStaffId());
				inparam.put("REFER_DEPART_ID", getVisit().getDepartId());
				inparam.put("REMARK", remark);
				SmsSend.insSms(inparam);
				//保存短信验证码
				SharedCache.set(SmsVerifyCodeBean.VERIFY_CODE_CACHE_KEY+serialNumber, verifyCode, 60*validMinutes);
				rtnData.put("RESULT_CODE", "0000");
				rtnData.put("RESULT_DESC", "成功");
				rtnlist.add(rtnData);
			}
		}else if("1".equals(sendMark)){//短信验证码验证
			String verifyCode = IDataUtil.chkParam(input, "VERIFY_CODE");
			Object cacheCode = SharedCache.get(SmsVerifyCodeBean.VERIFY_CODE_CACHE_KEY+sn);
    		System.out.println("cacheCode========="+cacheCode);
    		if(null == cacheCode || cacheCode.equals("")){
    			rtnData.put("RESULT_CODE", "2068");
    			rtnData.put("RESULT_DESC", "短信验证码无效！");
    			rtnData.put("X_RESULTCODE", "2068");
    			rtnData.put("X_RESULTINFO", "短信验证码无效！");
    			rtnData.put("X_RSPTYPE", "2");
    			rtnData.put("X_RSPCODE", "2068");
    			rtnData.put("X_RSPDESC", "短信验证码无效！");
    		}else if(cacheCode.equals(verifyCode)){
    			SharedCache.delete(SmsVerifyCodeBean.VERIFY_CODE_CACHE_KEY+sn);
    			rtnData.put("RESULT_CODE", "0000");
				rtnData.put("RESULT_DESC", "成功");
    		}else{
    			rtnData.put("RESULT_CODE", "2068");
    			rtnData.put("RESULT_DESC", "短信验证码不正确！");
    			rtnData.put("X_RESULTCODE", "2068");
    			rtnData.put("X_RESULTINFO", "短信验证码不正确！");
    			rtnData.put("X_RSPTYPE", "2");
    			rtnData.put("X_RSPCODE", "2068");
    			rtnData.put("X_RSPDESC", "短信验证码不正确！");
    		}
    		rtnlist.add(rtnData);
		}
		return rtnlist;
	}
}
