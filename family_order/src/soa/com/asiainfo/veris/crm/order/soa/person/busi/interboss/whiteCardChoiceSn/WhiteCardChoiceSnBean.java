
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.whiteCardChoiceSn;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.simcard.SimCardCheckBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.simcard.WriteCardBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserWhitCardChoiceSnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.AssemDynData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.EncAssemDynData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.EncAssemDynDataRsp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.IssueData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.WebServiceClient;


import java.util.ArrayList;
import java.util.List;

public class WhiteCardChoiceSnBean extends CSBizBean
{
	
	/**
	 * 网上选号
	 * */
	public IData sureChoiceSn(IData param) throws Exception
	{
		IData returnInfo = new DataMap();
		
		try {
			String serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER");  //开户号码
			String custName = IDataUtil.chkParam(param, "CUST_NAME");  //客户名称
			String psptId = IDataUtil.chkParam(param, "PSPT_ID");  //客户身份证号码
			String productId = IDataUtil.chkParam(param, "OFFER_CODE");  // 套餐编码
			String address = IDataUtil.chkParam(param, "CONTRACT_ADDRESS");  //地址
			String pay = IDataUtil.chkParam(param, "ADVANCE_PAY");  //预存金额   单位：分，没有传0
			String channelId = IDataUtil.chkParam(param, "CHANNEL_ID");  //发起渠道  0-Wap  1-网厅    2-其它
			String staffID = param.getString("TRADE_STAFF_ID",getVisit().getStaffId()); //工号
			String contractPhone = param.getString("CONTRACT_PHONE","");
			String email = param.getString("EMAIL","");
			
			
			//1.校验号码是否在临时日志表中
			IData WhitCardChoiceSnInfo  = UserWhitCardChoiceSnInfoQry.qryWhiteCardChoiceSnInfoBySn(serialNumber);
			if (IDataUtil.isNotEmpty(WhitCardChoiceSnInfo)) 
			{
				// 用户换卡异常状态是N
                returnInfo.put("X_RESULTCODE", "1");
                returnInfo.put("X_RESULTINFO", "该用户已经存在短信白卡放号流程!");
                returnInfo.put("TRANS_ID", "");
                return returnInfo;
			}

			//一证五号校验
			IData moreData = new DataMap();
			moreData.put("CUST_NAME",custName);
			moreData.put("PSPT_ID",psptId);
			moreData.put("PSPT_TYPE_CODE","0");
			IDataset checkMore = CSAppCall.call("SS.CreatePersonUserSVC.checkGlobalMorePsptId",moreData);
			if (IDataUtil.isNotEmpty(checkMore))
			{
				if (!"0".equals(checkMore.first().getString("CODE")))
				{
					returnInfo.put("X_RESULTCODE", "1");
					returnInfo.put("X_RESULTINFO", "一证五号校验失败！【"+checkMore.first().getString("MSG")+"】");
					returnInfo.put("TRANS_ID", "");
					return returnInfo;
				}
			}

			//2.调用资源接口号码选占
			//ResCall.resEngrossForMphone(serialNumber);
			
            //3.记录临时日志表    TF_F_SEL_WHITECARD_FLOW
			IData input = new DataMap();
	        String transId = SeqMgr.getTransId();
	        input.put("TRANS_ID", transId);
	        input.put("PSPT_ID", psptId);
	        input.put("CUST_NAME", custName);
	        input.put("CONTRACT_PHONE", contractPhone);
	        input.put("EMAIL", email);
	        input.put("CONTRACT_ADDR", address);
	        input.put("OFFER_CODE", productId);
	        input.put("ADVANCE_PAY", pay);
	        input.put("CHNNEL_ID", channelId);
	        input.put("USER_ID", "-1");
	        input.put("SERIAL_NUMBER", serialNumber);
	        input.put("SERIAL_NUMBER_TEMP", "");
	        input.put("EPARCHY_CODE", "0000");
	        input.put("STATE", "A");  //A' 白卡换号登记成功 'B' 第二步校验成功'C' 获取写卡数据成功  'N' 流程异常结束  'Y' 流程正常结束
	        input.put("START_DATE", SysDateMgr.getSysTime());
	        input.put("END_DATE", SysDateMgr.addDays2(15));
	        input.put("UPDATE_TIME", SysDateMgr.getSysTime());
	        
	        /*
	         * 1:本地用户使用本地进行白卡开户
	         * 2:本地用户使用异地进行白卡开户
	         * 3:异地使用本地进行白卡开户
	        */
	        input.put("RSRV_TAG1", "1");

			input.put("RSRV_STR2", staffID);

	        input.put("REMARK", "第一步:白卡换号登记成功");

	        Dao.insert("TF_F_SEL_WHITECARD_FLOW", input);
	         
	        returnInfo.put("X_RESULTCODE", "0");
            returnInfo.put("X_RESULTINFO", "第一步:白卡换号登记成功");
            returnInfo.put("TRANS_ID", transId);
            return returnInfo;

		} catch (Exception e) {
			SessionManager.getInstance().rollback();
            returnInfo.put("X_RESULTCODE", "-1");
            returnInfo.put("X_RESULTINFO", e.getMessage());
            returnInfo.put("TRANS_ID", "-1");
            return returnInfo;
		}
	}

	/**
	 * 开户请求校验
	 * */
	public IData checkUserRequest(IData param) throws Exception {
		
		IData returnInfo = new DataMap();
		
		/*try {*/
			String serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER");
			String serialNumberTmp = IDataUtil.chkParam(param,"SERIAL_NUMBER_TEMP");

			/*
			* 调用资源【RES_TEMPNUM_POOL & RES_EMPTY_ORIGIN】 校验 临时号码
			*	在白卡入库时，已录入临时号码
			 * */
			IData resSnTmpInfo =  ResCall.qryPhoneEmptyBySn(serialNumberTmp,null);
			if (IDataUtil.isEmpty(resSnTmpInfo))
			{
				returnInfo.put("X_RESULTCODE", "1");
	            returnInfo.put("X_RESULTINFO", "资源找不到该USIM卡临时号码的信息!");
	            return returnInfo;
			}

			//根据临时号码 查询日志表，校验是否重复请求
			IData WhitCardChoiceSnTInfo  = UserWhitCardChoiceSnInfoQry.qryWhiteCardChoiceSnInfoBySnTemp(serialNumberTmp);
			if (IDataUtil.isNotEmpty(WhitCardChoiceSnTInfo))
			{
				returnInfo.put("X_RESULTCODE", "1");
                returnInfo.put("X_RESULTINFO", "该临时号码已被选用，请更换临时号码!");
                return returnInfo;
			}

			//根据 号码 查询日志表，校验是否重复请求
			IData WhitCardChoiceSnInfo  = UserWhitCardChoiceSnInfoQry.qryWhiteCardChoiceSnInfoBySn(serialNumber);
			if (IDataUtil.isEmpty(WhitCardChoiceSnInfo))
			{
				returnInfo.put("X_RESULTCODE", "1");
                returnInfo.put("X_RESULTINFO", "该用户不存在白卡放号流程!");
                return returnInfo;
			}


			//校验黑名单
			param.put("PSPT_ID",WhitCardChoiceSnInfo.getString("PSPT_ID"));
			IDataset results = AcctCall.qryBlackListByPsptId(param); // 调账务接口查黑名单
	        String code = "0";
	        if (IDataUtil.isNotEmpty(results) )
	        {
	         	for(int i=0;i<results.size();i++){
	         		IData blackInfo=results.getData(i);
	         		String owe_fee=blackInfo.getString("OWE_FEE","0");
	         		if(owe_fee!=null && !"0".equals(owe_fee)){
	         			code="9";
	         		}
	         	}
	         	if("9".equals(code)){
	         		returnInfo.put("X_RESULTCODE", "-1");
	                returnInfo.put("X_RESULTINFO", "该用户是黑名单用户，不能办理白卡放号业务!");
	                return returnInfo;
	        	}
	        }


			//更新日志记录表
			String transId =  WhitCardChoiceSnInfo.getString("TRANS_ID"); // 白卡换号流水号

            WhitCardChoiceSnInfo.put("SERIAL_NUMBER_TEMP", serialNumberTmp);
            WhitCardChoiceSnInfo.put("SIM_CARD_NO_TEMP", resSnTmpInfo.getString("SIM_CARD_NO"));

            WhitCardChoiceSnInfo.put("IMSI_TEMP", resSnTmpInfo.getString("IMSI"));
            WhitCardChoiceSnInfo.put("PIN_TEMP", resSnTmpInfo.getString("PIN"));
            WhitCardChoiceSnInfo.put("PIN2_TEMP", resSnTmpInfo.getString("PIN2"));

            WhitCardChoiceSnInfo.put("PUK_TEMP", resSnTmpInfo.getString("PUK"));
            WhitCardChoiceSnInfo.put("PUK2_TEMP", resSnTmpInfo.getString("PUK2"));

            WhitCardChoiceSnInfo.put("KI_TEMP", resSnTmpInfo.getString("KI"));
            WhitCardChoiceSnInfo.put("OPC_TEMP", resSnTmpInfo.getString("OPC"));

            WhitCardChoiceSnInfo.put("EMPTY_CARD_ID", resSnTmpInfo.getString("EMPTY_CARD_ID"));

            WhitCardChoiceSnInfo.put("STATE", "B");
            WhitCardChoiceSnInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
            WhitCardChoiceSnInfo.put("END_DATE", SysDateMgr.getTomorrowTime());

            WhitCardChoiceSnInfo.put("REMARK", "第二步用户请求校验成功！");
            UserWhitCardChoiceSnInfoQry.excuteUpdate(WhitCardChoiceSnInfo);

	        // 下发  第一步  短信    用户24小时内回复  BKKH  确认开通
            IDataset smsConfig = CommparaInfoQry.getCommParas("CSM", "4456", "BKKH", "1", "0898");
            if (IDataUtil.isEmpty(smsConfig))
            {
                returnInfo.put("X_RESULTCODE", "-1");
                returnInfo.put("X_RESULTINFO", "获取下发短信内容配置信息无数据！");
                return returnInfo;
		    }

            String forceObj = smsConfig.getData(0).getString("PARA_CODE2", "").trim() + transId;
            String noticeContent = smsConfig.getData(0).getString("PARA_CODE4", "").trim() + smsConfig.getData(0).getString("PARA_CODE5", "").trim() + smsConfig.getData(0).getString("PARA_CODE6", "").trim()
                    + smsConfig.getData(0).getString("PARA_CODE7", "").trim() + smsConfig.getData(0).getString("PARA_CODE8", "").trim() + smsConfig.getData(0).getString("PARA_CODE9", "").trim();

            IData smsData = new DataMap();
            smsData.put("FORCE_OBJECT", forceObj);
            smsData.put("RECV_OBJECT", serialNumberTmp);
            smsData.put("NOTICE_CONTENT", noticeContent);
            smsData.put("SMS_KIND_CODE", "08");
            smsData.put("SMS_PRIORITY", "50");
            smsData.put("IS_BIN", "1");
            SmsSend.insSms(smsData);

            returnInfo.put("X_RESULTCODE", "0");
            returnInfo.put("X_RESULTINFO", "第二步用户请求校验成功");
            returnInfo.put("TRANS_ID", transId);
            return returnInfo;
	}
	
	
	/**
	 * 实时写卡数据获取
	 * @throws Exception 
	 * */
	public IData getWriteCartInfo (IData param) throws Exception
	{
		IData returnInfo = new DataMap();
		
		String serialNumberTmp = IDataUtil.chkParam(param,"SERIAL_NUMBER_TMP");
		//String serialNumber = IDataUtil.chkParam(param,"SERIAL_NUMBER");
		String transId = IDataUtil.chkParam(param, "TRANS_ID");

		IDataset smsConfig1 = CommparaInfoQry.getCommParas("CSM", "4456", "BKKH", "1", "0898");
		if (IDataUtil.isEmpty(smsConfig1))
		{
			returnInfo.put("X_RESULTCODE", "-1");
			returnInfo.put("X_RESULTINFO", "获取下发短信内容配置信息无数据！");
			return returnInfo;
		}

		IData whiteCardInfo = null;
		try {
			whiteCardInfo = getWhiteCardInfoByTransId(transId,smsConfig1);
		} catch (Exception e) {
			returnInfo.put("X_RESULTCODE", "-1");
			returnInfo.put("X_RESULTINFO", e.getMessage());
			return returnInfo;
		}

		if (IDataUtil.isNotEmpty(whiteCardInfo))
		{
			if (!"B".equals(whiteCardInfo.getString("STATE")))
			{ 
				String remark = whiteCardInfo.getString("REMARK");
				returnInfo.put("X_RESULTCODE", "-1");
		        returnInfo.put("X_RESULTINFO", "根据流水号查询，白卡开户流程不在此节点！该节点为：【"+remark+"】");
		        return returnInfo;
			}
		}else 
		{
			returnInfo.put("X_RESULTCODE", "-1");
	        returnInfo.put("X_RESULTINFO", "根据流水号查询，无此白卡开户记录！");
	        return returnInfo;
		}
		
        String flowSerialNumberTmp = whiteCardInfo.getString("SERIAL_NUMBER_TEMP", "");
        if (!serialNumberTmp.equals(flowSerialNumberTmp))
        {
            returnInfo.put("X_RESULTCODE", "-1");
	        returnInfo.put("X_RESULTINFO", "参数中临时手机号码与流程表[TF_F_SEL_WHITECARD_FLOW]中临时手机号码不一致!");
	        return returnInfo;
        }

		//一证五号校验
		IData moreData = new DataMap();
		moreData.put("CUST_NAME",whiteCardInfo.getString("CUST_NAME", ""));
		moreData.put("PSPT_ID",whiteCardInfo.getString("PSPT_ID", ""));
		moreData.put("PSPT_TYPE_CODE","0");
		IDataset checkMore = CSAppCall.call("SS.CreatePersonUserSVC.checkGlobalMorePsptId",moreData);
		if (IDataUtil.isNotEmpty(checkMore))
		{
			if (!"0".equals(checkMore.first().getString("CODE")))
			{
				returnInfo.put("X_RESULTCODE", "1");
				returnInfo.put("X_RESULTINFO", "一证五号校验失败！【"+checkMore.first().getString("MSG")+"】");
				return returnInfo;
			}
		}


		//2.短信超时校验
        String startDate = whiteCardInfo.getString("UPDATE_TIME");

        //短信超时结束时间  偏移两小时
        String endDate = SysDateMgr.getAddHoursDate(startDate,2);

        long time = SysDateMgr.hoursBetween(endDate,SysDateMgr.getSysTime());

        if (time > 0)
        {
			//发送超时短信；客户需要重新实名制认证

			whiteCardInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
			whiteCardInfo.put("STATE", "X");
			whiteCardInfo.put("REMARK", "短信确认已超时!");

            UserWhitCardChoiceSnInfoQry.excuteUpdate( whiteCardInfo);

			returnInfo.put("X_RESULTCODE", "-1");
			returnInfo.put("X_RESULTINFO", "短信确认已超时!");
            return returnInfo;
		}

		//3.加密卡数据
		String rsrvTag1 = whiteCardInfo.getString("RSRV_TAG1","");
		String emptyCardId = whiteCardInfo.getString("EMPTY_CARD_ID","");
		
		String simCardNo = "";
        String imsi = "";
        String pin = "";
        String pin2 = "";
        String puk = "";
        String puk2 = "";
        String smsp = "";
        
        WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
        IData simInfo = new DataMap();

        String serialNumber = whiteCardInfo.getString("SERIAL_NUMBER");
		if ("1".equals(rsrvTag1))
        {
            // 调用资源信息获取写卡信息
			try {
				simInfo = bean.getSpeSimInfo(serialNumber, emptyCardId, "2", "2").getData(0);
			} catch (Exception e) {
				SessionManager.getInstance().rollback();
				whiteCardInfo.put("STATE", "N");
				whiteCardInfo.put("REMARK", "第三步:调用资源获取写卡信息报错!" + e.getMessage());
				whiteCardInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
				whiteCardInfo.put("END_DATE", SysDateMgr.getSysTime());
				UserWhitCardChoiceSnInfoQry.excuteUpdate(whiteCardInfo);

				returnInfo.put("X_RESULTCODE", "-1");
				returnInfo.put("X_RESULTINFO", "第三步:调用资源获取写卡信息报错!" + e.getMessage());
				return returnInfo;
			}
			simCardNo = simInfo.getString("SIM_CARD_NO", "0");
            imsi = simInfo.getString("IMSI", "0");
            pin = simInfo.getString("PIN", "0");
            pin2 = simInfo.getString("PIN2", "0");
            puk = simInfo.getString("PUK", "0");
            puk2 = simInfo.getString("PUK2", "0");
            smsp = "+8613800898500";
        }
        else 
        {
        	returnInfo.put("X_RESULTCODE", "-1");
	        returnInfo.put("X_RESULTINFO", "当前写卡方式只支持本地用户使用本地SIM卡进行白卡开户");
	        return returnInfo;
        }

        // 调用写卡平台组装数据
        String seqNo = SeqMgr.getTradeId().substring(6);
        WebServiceClient wsc = new WebServiceClient();
        AssemDynData ass = new AssemDynData();
        ass.setCardInfo("080A" + replace(simCardNo) + "0E0A" + emptyCardId);
        ass.setSeqNo(seqNo);
        ass.setChanelflg("2");

        List<EncAssemDynData> list = new ArrayList<EncAssemDynData>();
        EncAssemDynData eas1 = new EncAssemDynData();
        IssueData issueData = new IssueData();
        issueData.setIccId(simCardNo);
        issueData.setImsi(imsi);
        issueData.setPin1(pin);
        issueData.setPin2(pin2);
        issueData.setPuk1(puk);
        issueData.setPuk2(puk2);
        issueData.setSmsp(smsp);
        eas1.setIssueData(issueData);
        eas1.setMsisdn(serialNumber);
        list.add(eas1);
        ass.setEnc(list);
        EncAssemDynDataRsp encData = null;
        try
        {
        	// 调用webservice接口获取加密数据
            encData = wsc.encAssemClient(ass);
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();

			// 1、临时号码销户
			destroyUserTem(whiteCardInfo);

			// 2、更新日志表记录
			whiteCardInfo.put("STATE", "N");
			whiteCardInfo.put("REMARK", "第三步:调用写卡平台报错!" + e.getMessage());
			whiteCardInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
			whiteCardInfo.put("END_DATE", SysDateMgr.getSysTime());
			//UserWhitCardChoiceSnInfoQry.excuteUpdate( whiteCardInfo);

			// 3、移入历史表  ,搬迁到历史表并删除原表数据
			Dao.insert("TF_FH_SEL_WHITECARD_FLOW", whiteCardInfo);
			Dao.delete("TF_F_SEL_WHITECARD_FLOW", whiteCardInfo,new String[]{"TRANS_ID"});

            returnInfo.put("X_RESULTCODE", "1008");
            returnInfo.put("X_RESULTINFO", "第三步:调用写卡平台报错!" + e.getMessage());
            return returnInfo;
        }
        String resultCode = encData.getResultCode();
        String message = "";
        if ("0".equals(resultCode))
        {
            message = encData.getIssueData(); // 加密信息
        }
        else
        {
			whiteCardInfo.put("STATE", "N");
			whiteCardInfo.put("REMARK", "第三步:写卡平台返回错误" + encData.getResultMessage());
			whiteCardInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
			whiteCardInfo.put("END_DATE", SysDateMgr.getSysTime());
			UserWhitCardChoiceSnInfoQry.excuteUpdate( whiteCardInfo);

			// 1、临时号码销户
			destroyUserTem(whiteCardInfo);

			// 2、更新日志表记录
			whiteCardInfo.put("STATE", "N");
			whiteCardInfo.put("REMARK", "第三步:写卡平台返回错误" + encData.getResultMessage());
			whiteCardInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
			whiteCardInfo.put("END_DATE", SysDateMgr.getSysTime());
			//UserWhitCardChoiceSnInfoQry.excuteUpdate( whiteCardInfo);

			// 3、移入历史表  ,搬迁到历史表并删除原表数据
			Dao.insert("TF_FH_SEL_WHITECARD_FLOW", whiteCardInfo);
			Dao.delete("TF_F_SEL_WHITECARD_FLOW", whiteCardInfo,new String[]{"TRANS_ID"});

            returnInfo.put("X_RESULTCODE", "-1");
            returnInfo.put("X_RESULTINFO", "第三步:写卡平台返回错误" + encData.getResultMessage());
            return returnInfo;
        }

		// 下发数据短信  至指定卡号   message
		String noticeContent = "BIN|" + message;

		IDataset smsConfig2 = CommparaInfoQry.getCommParas("CSM", "4456", "BKKH", "2", "0898");
		if (IDataUtil.isEmpty(smsConfig2))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取短信配置信息出错！");
		}

		String forceObj = smsConfig2.getData(0).getString("PARA_CODE2", "").trim()+whiteCardInfo.getString("TRANS_ID");

		String smsKindCode = smsConfig2.getData(0).getString("PARA_CODE3", "").trim();

		IData smsData = new DataMap();
		smsData.put("FORCE_OBJECT", forceObj);
		smsData.put("RECV_OBJECT", whiteCardInfo.getString("SERIAL_NUMBER_TEMP"));
		smsData.put("NOTICE_CONTENT", noticeContent);
		smsData.put("SMS_KIND_CODE", smsKindCode);
		smsData.put("SMS_PRIORITY", "50");
		smsData.put("IS_BIN", "1");
		SmsSend.insSms(smsData);

		whiteCardInfo.put("STATE", "C");
		whiteCardInfo.put("REMARK", "第三步:获取远程写卡信息成功." + message);
		whiteCardInfo.put("PIN_NEW", pin);
		whiteCardInfo.put("PIN2_NEW", pin2);
		whiteCardInfo.put("PUK_NEW", puk);
		whiteCardInfo.put("PUK2_NEW", puk2);
		whiteCardInfo.put("KI_NEW", whiteCardInfo.getString("KI_NEW"));
		whiteCardInfo.put("IMSI_NEW", imsi);
		whiteCardInfo.put("OPC_NEW", whiteCardInfo.getString("OPC_NEW"));
		whiteCardInfo.put("SIM_CARD_NO_NEW", simCardNo);
		whiteCardInfo.put("RSRV_STR5", seqNo);
		
		whiteCardInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
        UserWhitCardChoiceSnInfoQry.excuteUpdate(whiteCardInfo);

		returnInfo.put("X_RESULTCODE", "0");
        returnInfo.put("X_RESULTINFO", "写卡数据获取成功");
        return returnInfo;
	}

	/**
	 * 开户激活接口
	 * @throws Exception 
	 * */
	public IData surePersonUserCreate (IData param) throws Exception
	{
		IData returnInfo = new DataMap();
		
		String serialNumberTmp = IDataUtil.chkParam(param, "SERIAL_NUMBER_TEMP"); // 临时号码
		String smsContent = IDataUtil.chkParam(param, "SMS_CONTENT");
		String transId = IDataUtil.chkParam(param, "TRANS_ID");      //业务流水号
		
		IDataset smsConfig2 = CommparaInfoQry.getCommParas("CSM", "4456", "BKKH", "2", "0898");
		if (IDataUtil.isEmpty(smsConfig2))
		{
			returnInfo.put("X_RESULTCODE", "-1");
			returnInfo.put("X_RESULTINFO", "获取下发短信内容配置信息无数据！");
			return returnInfo;
		}

		IData whiteCardInfo = null;
		try {
			whiteCardInfo = getWhiteCardInfoByTransId(transId,smsConfig2);
		} catch (Exception e) {
			returnInfo.put("X_RESULTCODE", "-1");
			returnInfo.put("X_RESULTINFO", e.getMessage());
			return returnInfo;
		}

		if (IDataUtil.isNotEmpty(whiteCardInfo))
		{
			if (!"C".equals(whiteCardInfo.getString("STATE")))
			{ 
				String remark = whiteCardInfo.getString("REMARK");
				returnInfo.put("X_RESULTCODE", "-1");
		        returnInfo.put("X_RESULTINFO", "根据临时号码查询，白卡开户流程不在此节点！当前节点为：【"+remark+"】");
		        return returnInfo;
			}
		}else {
			returnInfo.put("X_RESULTCODE", "-1");
	        returnInfo.put("X_RESULTINFO", "根据临时号码查询，无此白卡开户记录！");
	        return returnInfo;
		}

		// 调用写卡平台验证写卡结果
		WebServiceClient wsc = new WebServiceClient();
		AssemDynData ass = new AssemDynData();
		ass.setSeqNo(whiteCardInfo.getString("RSRV_STR5", ""));
		ass.setCardInfo("080A" + replace(whiteCardInfo.getString("SIM_CARD_NO_NEW", "")) + "0E0A" + whiteCardInfo.getString("EMPTY_CARD_ID", ""));
		ass.setCardRsp(smsContent);
		EncAssemDynDataRsp encData = null;

		// 调用webservice接口校验写卡结果
		//encData = wsc.repCheckClient(ass);
		String message = "0"; // 加密信息
		String writeTag = (message.equals("0")) ? "0" : "1";

		// 回写写卡资料
		try {
			WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
			bean.remoteWriteUpdate(whiteCardInfo.getString("IMSI_NEW"), whiteCardInfo.getString("EMPTY_CARD_ID"), whiteCardInfo.getString("SIM_CARD_NO_NEW"), "2", writeTag);
		} catch (Exception e) {
			returnInfo.put("X_RESULTCODE", "-1");
			returnInfo.put("X_RESULTINFO", "回写写卡异常!："+e.getMessage());
			return returnInfo;
		}
		// 写卡结果验证失败，发送短信，临时卡属于本地
		if (!"0".equals(writeTag))
		{
			returnInfo.put("X_RESULTCODE", "-1");
			returnInfo.put("X_RESULTINFO", "第三步:写卡失败!错误编码：" + encData.getResultMessage());

			whiteCardInfo.put("STATE", "N");
			whiteCardInfo.put("REMARK", "第三步:写卡平台校验错误!错误编码：" + encData.getResultMessage());
			Dao.update("TF_F_SEL_WHITECARD_FLOW", whiteCardInfo,new String[]{"TRANS_ID"});

			return returnInfo;
		}

		//sim卡选占
		try {
			SimCardCheckBean checkBean = BeanManager.createBean(SimCardCheckBean.class);
			checkBean.preOccupySimCard(whiteCardInfo.getString("SERIAL_NUMBER", ""), whiteCardInfo.getString("SIM_CARD_NO_NEW", ""), "2", "1","0");
		} catch (Exception e) {
			returnInfo.put("X_RESULTCODE", "-1");
			returnInfo.put("X_RESULTINFO", "sim卡选占异常!："+e.getMessage());
			return returnInfo;
		}

		// 1、拼装数据调用开户接口
		String payName = whiteCardInfo.getString("CUST_NAME");
		String psptTypeCode = "0";
		String psptId = whiteCardInfo.getString("PSPT_ID");
		String custName = whiteCardInfo.getString("CUST_NAME");
		String productId = whiteCardInfo.getString("OFFER_CODE");

		String staffId = whiteCardInfo.getString("RSRV_STR2");
		CSBizBean.getVisit().setStaffId(staffId);

		IData createPersonUserData = new DataMap();
		createPersonUserData.put("SERIAL_NUMBER", whiteCardInfo.getString("SERIAL_NUMBER"));
		createPersonUserData.put("SIM_CARD_NO", whiteCardInfo.getString("SIM_CARD_NO_NEW"));
		createPersonUserData.put("USER_TYPE_CODE", "0");

		createPersonUserData.put("PAY_NAME", payName);
		createPersonUserData.put("PAY_MODE_CODE", "0");
		createPersonUserData.put("TRADE_TYPE_CODE", "10");
		createPersonUserData.put("ORDER_TYPE_CODE", "10");

		createPersonUserData.put("PSPT_TYPE_CODE", psptTypeCode);
		createPersonUserData.put("PSPT_ID", psptId);
		createPersonUserData.put("CUST_NAME", custName);

		createPersonUserData.put("FLAG_4G", "1"); //标识 4g卡

		createPersonUserData.put("IMSI", whiteCardInfo.getString("IMSI_NEW",whiteCardInfo.getString("IMSI_TEMP")));
		createPersonUserData.put("KI", whiteCardInfo.getString("KI_NEW",whiteCardInfo.getString("KI_TEMP")));

		IDataset selectedelements = new DatasetList();

		createPersonUserData.put("PRODUCT_ID", productId);

		// 必选或者默认的元素
		IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "1", "1");
		forceElements = ProductUtils.offerToElement(forceElements, productId);

		if (IDataUtil.isNotEmpty(forceElements))
		{
			String sysDate = SysDateMgr.getSysTime();

			for (int i = 0; i < forceElements.size(); i++)
			{
				IData element = new DataMap();
				element.put("ELEMENT_ID", forceElements.getData(i).getString("ELEMENT_ID"));
				element.put("ELEMENT_TYPE_CODE", forceElements.getData(i).getString("ELEMENT_TYPE_CODE"));
				element.put("PRODUCT_ID", createPersonUserData.getString("PRODUCT_ID"));
				element.put("PACKAGE_ID", forceElements.getData(i).getString("PACKAGE_ID"));
				element.put("MODIFY_TAG", "0");
				element.put("START_DATE", sysDate);
				element.put("END_DATE", "2050-12-31");
				selectedelements.add(element);
			}
		}
		else
		{
			CSAppException.appError("-1", "个人用户产品["+productId+"]没有配置,请联系管理员!");
		}

		createPersonUserData.put("SELECTED_ELEMENTS", selectedelements.toString());

		//认证方式  默认Z
		createPersonUserData.put("CHECK_MODE",  "Z" );

		//受理单标记：无手机宽带开户融合个人开户时：个人开户 不打印 受理单
		createPersonUserData.put("TEMPLATE", "N");

		IDataset result;

		String userId = "";
		try {
  			result = CSAppCall.call("SS.CreatePersonUserRegSVC.tradeReg", createPersonUserData);
			userId = result.getData(0).getString("USER_ID");
		}
		catch (Exception e)
		{
			returnInfo.put("X_RESULTCODE", "-1");
			returnInfo.put("X_RESULTINFO", "开户失败："+e.getMessage());
			return returnInfo;
		}

		// 2、临时号码销户
		destroyUserTem(whiteCardInfo);

		// 3、更新日志表记录
		whiteCardInfo.put("STATE", "Y");
		whiteCardInfo.put("USER_ID", userId);
		whiteCardInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
		whiteCardInfo.put("END_DATE", SysDateMgr.getSysTime());
		whiteCardInfo.put("REMARK", "第四步:白卡开户成功.");
		UserWhitCardChoiceSnInfoQry.excuteUpdate(whiteCardInfo);

		// 4、移入历史表  ,搬迁到历史表并删除原表数据
		Dao.insert("TF_FH_SEL_WHITECARD_FLOW", whiteCardInfo);
		Dao.delete("TF_F_SEL_WHITECARD_FLOW", whiteCardInfo,new String[]{"TRANS_ID"});


		// 5、下发短信
		IDataset smsConfig5 = CommparaInfoQry.getCommParas("CSM", "4456", "BKKH", "5", "0898");
		if (IDataUtil.isEmpty(smsConfig5))
		{
			returnInfo.put("X_RESULTCODE", "-1");
			returnInfo.put("X_RESULTINFO", "获取下发短信内容配置信息无数据！");
			return returnInfo;
		}

		String forceObj = smsConfig5.getData(0).getString("PARA_CODE2", "").trim();
		String noticeContent = smsConfig5.getData(0).getString("PARA_CODE4", "").trim() +whiteCardInfo.getString("SERIAL_NUMBER")+ smsConfig5.getData(0).getString("PARA_CODE5", "").trim() + smsConfig5.getData(0).getString("PARA_CODE6", "").trim()
				+ smsConfig5.getData(0).getString("PARA_CODE7", "").trim() + smsConfig5.getData(0).getString("PARA_CODE8", "").trim() + smsConfig5.getData(0).getString("PARA_CODE9", "").trim();

		IData smsData = new DataMap();
		smsData.put("FORCE_OBJECT", forceObj);
		smsData.put("RECV_OBJECT", whiteCardInfo.getString("SERIAL_NUMBER"));
		smsData.put("SMS_KIND_CODE", "08");
		smsData.put("NOTICE_CONTENT", noticeContent);
		smsData.put("SMS_PRIORITY", "50");
		smsData.put("IS_BIN", "1");
		SmsSend.insSms(smsData);

		returnInfo.put("X_RESULTCODE", "0");
		returnInfo.put("X_RESULTINFO", "开户激活成功");
		return returnInfo;
	}
	
	
	/**
	 * 
	 * 自动化任务   短信回复超时  服务
	 * 
	 * 调资源接口回收临时号码，并将资料搬至历史表
	 * @throws Exception 
	 * */
	public void overtimeSMS(IData whiteCardInfo) throws Exception 
	{
		String serialNumberTmp = whiteCardInfo.getString("SERIAL_NUMBER_TEMP");
		
		String rsrvTag2 = "1";  // 临时号码回收标识  1：标识已回收，  2：标识回收异常
		
		try {
			//1、调资源接口回收临时号码
			ResCall.undoResPossessForMphone(serialNumberTmp);

			whiteCardInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
			whiteCardInfo.put("RSRV_TAG2", rsrvTag2);
			whiteCardInfo.put("END_DATE", SysDateMgr.getSysTime());
			whiteCardInfo.put("STATE", "X");

			UserWhitCardChoiceSnInfoQry.excuteUpdate(whiteCardInfo);
			
			// 2、移入历史表  ,搬迁到历史表并删除原表数据
	        Dao.insert("TF_FH_SEL_WHITECARD_FLOW", whiteCardInfo);
	        Dao.delete("TF_F_SEL_WHITECARD_FLOW", whiteCardInfo,new String[]{"TRANS_ID"});
	        
		} 
		catch (Exception e) 
		{
			String message = e.getMessage();
			rsrvTag2 = "2";
			whiteCardInfo.put("RSRV_STR3", message); // 回收临时号码异常信息
			
			whiteCardInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
			whiteCardInfo.put("RSRV_TAG2", rsrvTag2);
			whiteCardInfo.put("STATE", "X");
			whiteCardInfo.put("END_DATE", SysDateMgr.getSysTime());

			UserWhitCardChoiceSnInfoQry.excuteUpdate(whiteCardInfo);
		}
	}
	
	/**
	 * 临时号码销户
	 * 		临时号码在预配时已做了批量USIM卡预开业务（502）
	 * @throws Exception
	 * */
	public void destroyUserTem(IData whiteCardChoiceInfo) throws Exception
	{
		IData desData = new DataMap();
        desData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        desData.put("SIM_CARD_NO", whiteCardChoiceInfo.getString("SIM_CARD_NO_TEMP"));
        desData.put("SERIAL_NUMBER", whiteCardChoiceInfo.getString("SERIAL_NUMBER_TEMP"));
        desData.put("X_CHOICE_TAG", "1");
        desData.put("ORDER_TYPE_CODE", "503");
        desData.put("TRADE_TYPE_CODE", "503");
        desData.put("IMSI", whiteCardChoiceInfo.getString("IMSI_TEMP"));
        desData.put("KI", whiteCardChoiceInfo.getString("KI_TEMP"));
        desData.put("OPC", whiteCardChoiceInfo.getString("OPC_TEMP"));
        desData.put("EMPTY_CARD_ID", whiteCardChoiceInfo.getString("EMPTY_CARD_ID"));
        CSAppCall.call("SS.PhoneReturnRegSVC.tradeReg", desData);
	}
	
	// sim卡单双两位互换公共方法
    private static String replace(String a)
    {
        char[] chars = a.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
            if (i % 2 == 0)
            {
                char a1 = chars[i];
                chars[i] = chars[i + 1];
                chars[i + 1] = a1;
            }
        }
        String a2 = String.valueOf(chars);
        return a2;
    }

    /**
	 * 根据业务流水号，查找白卡开户流水日志
	 * */
    private static IData getWhiteCardInfoByTransId(String transId,IDataset smsConfig) throws Exception
	{
		String reForceObj = smsConfig.getData(0).getString("PARA_CODE2").trim();
		if (!transId.startsWith(reForceObj))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "TRANS_ID格式不正确！");
		}
		int smsLen = reForceObj.length();
		transId = transId.substring(smsLen);
		IData flowInfo = UserWhitCardChoiceSnInfoQry.qryWhiteCardChoiceSnInfoByTransId(transId);
		if (IDataUtil.isEmpty(flowInfo))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "短信申请校验TARNS_ID=" + transId + "不存在！");
		}
		return flowInfo;
	}

}
