
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.selfhelpcard;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.simcard.SimCardCheckBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.simcard.WriteCardBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserChangeCardFlowInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.rule.CheckTradeBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.AssemDynData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.EncAssemDynData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.EncAssemDynDataRsp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.EncPresetData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.IssueData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.LocalDecPreData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.LocalDecPreDataRsp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.RoamEncPreData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.RoamEncPreDataRsp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.WebServiceClient;

public class SelfHelpChangeCardBean extends CSBizBean
{

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

    public IData changeCard(IData input) throws Exception
    {
        IData returnInfo = new DataMap();
        try
        {
            chkParam(input, "TRANS_ID");
            chkParam(input, "WRITE_TAG");// 写卡成功标志 0-成功;1-失败
            chkParam(input, "SMS_CONTENT");
            chkParam(input, "SERIAL_NUMBER");

            String serialNumber = input.getString("SERIAL_NUMBER");
            String transId = input.getString("TRANS_ID");
            String writeTag = input.getString("WRITE_TAG");
//            String smsContent = input.getString("SMS_CONTENT");

            // 查询短信配置
            IDataset smsConfig = CommparaInfoQry.getCommParas("CSM", "4456", "LBYK", "3", "0898");
            if (IDataUtil.isEmpty(smsConfig))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取短信配置信息出错！");
            }
            IData flowInfo = getUserFlowInfo(transId, smsConfig);
            IData data = new DataMap();
            data.put("SERIAL_NUMBER", serialNumber);
            data.put("SERIAL_NUMBER_TEMP", flowInfo.getString("SERIAL_NUMBER_TEMP"));
            checkParam(data, flowInfo);
            if (!"C".equals(flowInfo.getString("STATE")))
            {
                returnInfo.put("X_RESULTCODE", "1005");
                returnInfo.put("X_RESULTINFO", "当前流程节点应在获取远程写卡信息之后!");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }
            
            //回复换卡失败
            if ("1".equals(writeTag)) {
            	// 下发短信
                smsConfig = CommparaInfoQry.getCommParas("CSM", "4456", "LBYK", "8", "0898");
                if (IDataUtil.isEmpty(smsConfig))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取短信配置信息出错！");
                }
                
			}else {

           	 // 调用换卡接口
               IData param = new DataMap();
               param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
               param.put("IMSI", flowInfo.getString("IMSI_NEW"));
               try
               {
                   CSAppCall.call("SS.ChangeCardSVC.tradeReg", param);
               }
               catch (Exception e)
               {
                   SessionManager.getInstance().rollback();
                   flowInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                   flowInfo.put("STATE", "N");
                   flowInfo.put("REMARK", "第四步:换卡业务失败！" + e.getMessage());
                   Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);

                   returnInfo.put("X_RESULTCODE", "1012");
                   returnInfo.put("X_RESULTINFO", "第四步:换卡业务失败！" + e.getMessage());
                   returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                   returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                   return returnInfo;
               }
            // 下发短信
               smsConfig = CommparaInfoQry.getCommParas("CSM", "4456", "LBYK", "4", "0898");
               if (IDataUtil.isEmpty(smsConfig))
               {
                   CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取短信配置信息出错！");
               }
			}
            
            String forceObj = smsConfig.getData(0).getString("PARA_CODE2", "").trim() + flowInfo.getString("TRANS_ID");
            String noticeContent = smsConfig.getData(0).getString("PARA_CODE4", "").trim() + smsConfig.getData(0).getString("PARA_CODE5", "").trim() + smsConfig.getData(0).getString("PARA_CODE6", "").trim()
                    + smsConfig.getData(0).getString("PARA_CODE7", "").trim() + smsConfig.getData(0).getString("PARA_CODE8", "").trim() + smsConfig.getData(0).getString("PARA_CODE9", "").trim();
            IData smsData = new DataMap();
            smsData.put("FORCE_OBJECT", forceObj);
            smsData.put("RECV_OBJECT", flowInfo.getString("SERIAL_NUMBER"));
            smsData.put("NOTICE_CONTENT", noticeContent);
            smsData.put("SMS_KIND_CODE", "08");
            smsData.put("SMS_PRIORITY", "50");
            smsData.put("IS_BIN", "1");
            SmsSend.insSms(smsData);

            // 预销户
            String tag = flowInfo.getString("RSRV_TAG1", "");
            if ("1".equals(tag))
            {
                IData desData = new DataMap();
                desData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
                desData.put("SIM_CARD_NO", flowInfo.getString("SIM_CARD_NO_TEMP"));
                desData.put("SERIAL_NUMBER", flowInfo.getString("SERIAL_NUMBER_TEMP"));
                desData.put("X_CHOICE_TAG", "1");
                desData.put("ORDER_TYPE_CODE", "503");
                desData.put("TRADE_TYPE_CODE", "503");
                desData.put("IMSI", flowInfo.getString("IMSI_TEMP"));
                desData.put("KI", flowInfo.getString("KI_TEMP"));
                desData.put("OPC", flowInfo.getString("OPC_TEMP"));
                desData.put("EMPTY_CARD_ID", flowInfo.getString("EMPTY_CARD_ID"));
                CSAppCall.call("SS.PhoneReturnRegSVC.tradeReg", desData);
            }
            else if ("2".equals(tag))
            {
                // 调用IBOSS接口
                IBossCall.preDestory(flowInfo.getString("RSRV_STR2"), flowInfo.getString("SERIAL_NUMBER"), flowInfo.getString("SERIAL_NUMBER_TEMP"), flowInfo.getString("SIM_CARD_NO_NEW"), flowInfo.getString("IMSI_NEW"));
            }

            if ("1".equals(writeTag)) {
				flowInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                flowInfo.put("STATE", "N");
                flowInfo.put("REMARK", "第四步:写卡失败！");
                Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);

                returnInfo.put("X_RESULTCODE", "2999");
                returnInfo.put("X_RESULTINFO", "对不起，本次换卡操作不成功，详情请咨询10086或前往就近营业厅领取新卡后重新操作换卡。中国移动！");
                
			} else{  
            	flowInfo.put("STATE", "Y");
                flowInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                flowInfo.put("REMARK", "第四步:换卡成功.");
                Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);

                // 搬迁到历史表并删除原表数据
                Dao.insert("TF_FH_SELFHELPCARD_FLOW", flowInfo);
                Dao.delete("TF_F_SELFHELPCARD_FLOW", flowInfo);

                returnInfo.put("X_RESULTCODE", "0");
                returnInfo.put("X_RESULTINFO", "第四步:换卡成功！");
			}
            
            return returnInfo;
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();
            returnInfo.put("X_RESULTCODE", "-1");
            returnInfo.put("X_RESULTINFO", e.getMessage());
            returnInfo.put("X_RSPTYPE", "2");// add by ouyk
            returnInfo.put("X_RSPCODE", "2998");// add by ouyk
            return returnInfo;
        }

    }

    public IData changeCardF2L(IData input) throws Exception
    {
        IData returnInfo = new DataMap();
        chkParam(input, "TRANS_ID");
        chkParam(input, "Result");// 写卡成功标志 0-成功;1-失败
        chkParam(input, "ICCID");
        chkParam(input, "TEMP_NUMBER");
        String tempNumber = input.getString("TEMP_NUMBER");
        String iccid = input.getString("ICCID");
        String writeTag = input.getString("Result");
        String rsrvStr2 = input.getString("TRANS_ID");

        IData flowInfo = UserChangeCardFlowInfoQry.qryUserCardFlowInfoByIBossId(rsrvStr2);
        if (IDataUtil.isEmpty(flowInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "(异地）短信申请临时卡销户trans_id=" + rsrvStr2 + "不存在!");
        }
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", flowInfo.getString("SERIAL_NUMBER"));
        data.put("SERIAL_NUMBER_TEMP", tempNumber);
        checkParam(data, flowInfo);

        if (!"C".equals(flowInfo.getString("STATE")))
        {
            returnInfo.put("X_RESULTCODE", "1005");
            returnInfo.put("X_RESULTINFO", "当前流程节点应在写卡数据校验之后!");
            returnInfo.put("X_RSPTYPE", "2");// add by ouyk
            returnInfo.put("X_RSPCODE", "2998");// add by ouyk
            return returnInfo;
        }
        if (!"0".equals(writeTag))
        {
            returnInfo.put("X_RESULTCODE", "1012");
            returnInfo.put("X_RESULTINFO", "第四步:写卡失败！");
            returnInfo.put("X_RSPTYPE", "2");// add by ouyk
            returnInfo.put("X_RSPCODE", "2998");// add by ouyk
            flowInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
            flowInfo.put("STATE", "N");
            flowInfo.put("REMARK", "第四步:写卡失败,流程结束.");
            Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);
            return returnInfo;
        }
        // 预销户
        IData desData = new DataMap();
        desData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        desData.put("SIM_CARD_NO", flowInfo.getString("SIM_CARD_NO_TEMP"));
        desData.put("SERIAL_NUMBER", flowInfo.getString("SERIAL_NUMBER_TEMP"));
        desData.put("X_CHOICE_TAG", "1");
        desData.put("ORDER_TYPE_CODE", "503");
        desData.put("TRADE_TYPE_CODE", "503");
        desData.put("IMSI", flowInfo.getString("IMSI_TEMP"));
        desData.put("KI", flowInfo.getString("KI_TEMP"));
        desData.put("OPC", flowInfo.getString("OPC_TEMP"));
        desData.put("EMPTY_CARD_ID", flowInfo.getString("EMPTY_CARD_ID"));
        CSAppCall.call("SS.PhoneReturnRegSVC.tradeReg", desData);
        
        //
        flowInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
        flowInfo.put("STATE", "Y");
        flowInfo.put("REMARK", "第四步:换卡成功.");
        Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);
        // 搬到历史表,删除B表数据
        Dao.insert("TF_FH_SELFHELPCARD_FLOW", flowInfo);
        Dao.delete("TF_F_SELFHELPCARD_FLOW", flowInfo);

        returnInfo.put("X_RESULTCODE", "0");
        returnInfo.put("X_RESULTINFO", "第四步:换卡成功！");
        return returnInfo;
    }

    /**
     * 自助换卡的附加规则
     * 
     * @throws Exception
     */
    private void changeCardRule(IData input, IData userInfo) throws Exception
    {
        IDataset userSvc = UserSvcInfoQry.getSvcUserId(userInfo.getString("USER_ID"), "22");
        // getUserSvcGprs(pd, userId);dao.queryListByCodeCode("TF_F_USER_SVC",
        // "SEL_USER_GPRS_SVC", userdata);
        if (IDataUtil.isEmpty(userSvc))
        {
            // common.error("为了保证号码正常使用，办理USIM卡换卡业务前，请先开通GPRS功能。");
            CSAppException.apperr(CrmCardException.CRM_CARD_240);
        }

        boolean lowDiscnt = false;
        String lowDiscntCode = "";
        // 与与4GUSIM卡互斥的卡互斥的优惠
        IDataset userDiscntInfo = new DatasetList();
        IDataset userDiscntInfoTmp = UserDiscntInfoQry.getAllValidDiscntByUserId(userInfo.getString("USER_ID"));// new
        // DatasetList();//
        // queryUserDiscntFor4GCheck(userId);dao.queryListByCodeCode("TF_F_USER_DISCNT",
        for (int i = 0; i < userDiscntInfoTmp.size(); i++)
        {
            String discntCode = userDiscntInfoTmp.getData(i).getString("DISCNT_CODE");
            IDataset discntSet = CommparaInfoQry.getCommparaInfoBy5("CSM", "8550", "4G", discntCode, "ZZZZ", null);
            if (IDataUtil.isNotEmpty(discntSet))
            {
                userDiscntInfo.add(userDiscntInfoTmp.getData(i));
            }
        }
        // "SEL_USER_DISCNT_FOR4GCHECK", param);
        if (IDataUtil.isNotEmpty(userDiscntInfo))
        {
            lowDiscnt = true;
            for (int i = 0; i < userDiscntInfo.size(); i++)
            {
                IData userDiscnt = userDiscntInfo.getData(i);
                lowDiscntCode += "".equals(lowDiscntCode) ? userDiscnt.getString("DISCNT_CODE", "") : ',' + userDiscnt.getString("DISCNT_CODE", "");
            }

        }

        if (lowDiscnt)
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_241, lowDiscntCode);
            // common.error("该号码已办理的优惠中，与USIM卡换卡业务存在互斥，存在互斥的优惠编码为["+lowDiscntCode+"]");
        }

        // IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID", ""));
        // if (!"1".equals(custInfo.getString("IS_REAL_NAME")))
        // {
        // // 非实名制用户无法操作自助换卡
        // CSAppException.apperr(CrmCommException.CRM_COMM_103, "非实名制用户无法操作自助换卡");
        // }
        if ("3".equals(userInfo.getString("USER_TAG_SET", "")))
        {
            // 用户有携转申请时无法操作自助换卡
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户有携转申请时无法操作自助换卡");
        }
    }

    /**
     * 参数校验
     * 
     * @param data
     * @param flowInfo
     * @throws Exception
     */
    private void checkParam(IData data, IData flowInfo) throws Exception
    {
        String tempNumber = data.getString("SERIAL_NUMBER_TEMP", "");
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String flowTempNumber = flowInfo.getString("SERIAL_NUMBER_TEMP", "");
        String flowSerialNumber = flowInfo.getString("SERIAL_NUMBER", "");
        if (!serialNumber.equals(flowSerialNumber))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "参数中手机号码与流程表[TF_F_SELFHELPCARD_FLOW]中手机号码不一致!");
        }
        if (!tempNumber.equals(flowTempNumber))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "参数中临时手机号码与流程表[TF_F_SELFHELPCARD_FLOW]中临时手机号码不一致!");
        }
    }

    public IData checkUserCardFlow(IData input) throws Exception
    {
        IData returnInfo = new DataMap();
        try
        {
            chkParam(input, "SERIAL_NUMBER");
            chkParam(input, "SERIAL_NUMBER_TEMP");// 4G手机临时号码
            String serialNumber = input.getString("SERIAL_NUMBER");
            String tempNumber = input.getString("SERIAL_NUMBER_TEMP");
            
            // REQ201503230020两不一快临时卡自助换卡前系统拦截限制
        	IDataset ds = ResCall.getTemPhoneResState(tempNumber);
        	if(IDataUtil.isEmpty(ds)){
        		returnInfo.put("X_RESULTCODE", "1553");
                returnInfo.put("X_RESULTINFO", "您好，号码"+tempNumber+"异常，请联系发卡营业前台操作赠送后再进行自助换卡，感谢您的配合!");
                returnInfo.put("X_RSPTYPE", "2");
                returnInfo.put("X_RSPCODE", "2998");
                return returnInfo;
        	}
            
            // 触发规则验证 142
            CheckTradeBean cBean = BeanManager.createBean(CheckTradeBean.class);
            IData data = new DataMap();
            IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            IData userProdInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber); 
            
            data.putAll(userInfo);
            data.put("BRAND_CODE", userProdInfo.getString("BRAND_CODE", ""));
            data.put("PRODUCT_ID", userProdInfo.getString("PRODUCT_ID", ""));
            data.put("X_CHOICE_TAG", "0");
            data.put("TRADE_TYPE_CODE", "142");
            cBean.checkBeforeTrade(data); 
            
            IDataset msisInfo = MsisdnInfoQry.getMsisDns(tempNumber);
            if (IDataUtil.isEmpty(msisInfo))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "无法获取号码路由信息！");
            }
            String snEparchyCode = msisInfo.getData(0).getString("AREA_CODE", "");
            changeCardRule(input, userInfo);
            IDataset snFlowInfos = UserChangeCardFlowInfoQry.qryUserCardFlowInfoBySn(serialNumber);
            IDataset tempSnFlowInfos = UserChangeCardFlowInfoQry.qryUserCardFlowInfoByTempSn(tempNumber); 
            
            if (!snFlowInfos.isEmpty())
            {
                // 用户换卡异常状态是N
                returnInfo.put("X_RESULTCODE", "1551");
                returnInfo.put("X_RESULTINFO", "用户已经存在短信换卡流程!");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }
            // 临时号码
            if (!tempSnFlowInfos.isEmpty())
            {
                // 用户换卡异常状态是N
                returnInfo.put("X_RESULTCODE", "1002");
                returnInfo.put("X_RESULTINFO", "临时号码已经存在短信换卡流程!");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }
            
            
            
            if ("0898".equals(snEparchyCode))
            {
                returnInfo = checkUserCardFlowL2L(input, userInfo);
            }
            else
            {
                returnInfo = checkUserCardFlowL2F(input, userInfo);
            }
            return returnInfo;

        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();
            returnInfo.put("X_RESULTCODE", "-1");
            returnInfo.put("X_RESULTINFO", e.getMessage());
            returnInfo.put("X_RSPTYPE", "2");// add by ouyk
            returnInfo.put("X_RSPCODE", "2998");// add by ouyk
            return returnInfo;
        }
    }

    public IData checkUserCardFlowF2L(IData data) throws Exception
    {
        IData returnInfo = new DataMap();
        returnInfo.put("TRANS_ID", data.getString("TRANS_ID"));
        returnInfo.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        returnInfo.put("TEMP_NUMBER", data.getString("TEMP_NUMBER"));
        try
        {
            chkParam(data, "TRANS_ID");
            chkParam(data, "SERIAL_NUMBER");
            chkParam(data, "TEMP_NUMBER");// 4G手机临时号码
            String serialNumber = data.getString("SERIAL_NUMBER");
            String tempNumber = data.getString("TEMP_NUMBER");
            String rsrvStr2 = data.getString("TRANS_ID");

            IDataset snFlowInfos = UserChangeCardFlowInfoQry.qryUserCardFlowInfoBySn(serialNumber);
            IDataset tempSnFlowInfos = UserChangeCardFlowInfoQry.qryUserCardFlowInfoByTempSn(tempNumber);
            if (IDataUtil.isNotEmpty(snFlowInfos))
            {
                // 用户换卡异常状态是N
                returnInfo.put("X_RESULTCODE", "1551");
                returnInfo.put("X_RESULTINFO", "用户已经存在短信换卡流程!");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }
            // 临时号码
            if (IDataUtil.isNotEmpty(tempSnFlowInfos))
            {
                // 用户换卡异常状态是N
                returnInfo.put("X_RESULTCODE", "1002");
                returnInfo.put("X_RESULTINFO", "临时号码已经存在短信换卡流程!");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }

            // 调用资源接口，查询临时号码信息
            IData resInfo = ResCall.qryPhoneEmptyBySn(tempNumber, null);
            if (IDataUtil.isEmpty(resInfo))
            {
                returnInfo.put("Response", "1003");
                returnInfo.put("X_RESULTCODE", "1003");
                returnInfo.put("X_RESULTINFO", "资源找不到该USIM卡临时号码的信息!");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }
            //

            IData param = new DataMap();
            String transId = SeqMgr.getTransId();
            param.put("TRANS_ID", transId);
            param.put("USER_ID", "-1");
            param.put("SERIAL_NUMBER", serialNumber);
            param.put("EPARCHY_CODE", "0000");
            param.put("STATE", "A");
            param.put("START_DATE", SysDateMgr.getSysTime());
            param.put("END_DATE", SysDateMgr.getTomorrowTime());
            param.put("UPDATE_TIME", SysDateMgr.getSysTime());
            param.put("SERIAL_NUMBER_TEMP", tempNumber);
            param.put("IMSI_TEMP", resInfo.getString("IMSI"));
            param.put("KI_TEMP", resInfo.getString("AKI"));
            param.put("OPC_TEMP", resInfo.getString("AOPC"));
            param.put("PIN_TEMP", resInfo.getString("PIN"));
            param.put("PIN2_TEMP", resInfo.getString("PIN2"));
            param.put("PUK_TEMP", resInfo.getString("PUK"));
            param.put("PUK2_TEMP", resInfo.getString("PUK2"));
            param.put("KI_NEW", resInfo.getString("AKI"));
            param.put("OPC_NEW", resInfo.getString("AOPC"));
            param.put("REMARK", "第一步:短信换卡申请校验通过");
            param.put("RSRV_STR2", rsrvStr2);
            param.put("RSRV_TAG1", "3");
            param.put("EMPTY_CARD_ID", resInfo.getString("EMPTY_CARD_ID"));
            param.put("SIM_CARD_NO_TEMP", resInfo.getString("SIM_CARD_NO"));
            Dao.insert("TF_F_SELFHELPCARD_FLOW", param);

            // 下发短信
            IDataset smsConfig = CommparaInfoQry.getCommParas("CSM", "4456", "LBYK", "1", "0898");
            if (IDataUtil.isEmpty(smsConfig))
            {
                returnInfo.put("X_RESULTCODE", "-1");
                returnInfo.put("X_RESULTINFO", "获取下发短信内容配置信息无数据！");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }

            String forceObj = smsConfig.getData(0).getString("PARA_CODE2", "").trim() + transId;
            String noticeContent = smsConfig.getData(0).getString("PARA_CODE4", "").trim() + smsConfig.getData(0).getString("PARA_CODE5", "").trim() + smsConfig.getData(0).getString("PARA_CODE6", "").trim()
                    + smsConfig.getData(0).getString("PARA_CODE7", "").trim() + smsConfig.getData(0).getString("PARA_CODE8", "").trim() + smsConfig.getData(0).getString("PARA_CODE9", "").trim();

            IData smsData = new DataMap();
            smsData.put("FORCE_OBJECT", forceObj);
            smsData.put("RECV_OBJECT", tempNumber);
            smsData.put("NOTICE_CONTENT", noticeContent);
            smsData.put("SMS_KIND_CODE", "08");
            smsData.put("SMS_PRIORITY", "50");
            smsData.put("IS_BIN", "1");
            SmsSend.insSms(smsData);

            returnInfo.put("Response", "1000");
            returnInfo.put("X_RESULTCODE", "0");
            returnInfo.put("X_RESULTINFO", "第一步:短信换卡申请校验通过.");
            return returnInfo;

        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();
            returnInfo.put("Response", "1033");
            returnInfo.put("X_RESULTCODE", "1033");
            returnInfo.put("X_RESULTINFO", "第一步:短信换卡申请校验不通过." + e.getMessage());
            returnInfo.put("X_RSPTYPE", "2");// add by ouyk
            returnInfo.put("X_RSPCODE", "2998");// add by ouyk
            return returnInfo;
        }
    }

    // 本地使用异地两不一快
    private IData checkUserCardFlowL2F(IData input, IData userInfo) throws Exception
    {
        IData returnInfo = new DataMap();
        IData param = prepareCardFlowInfo(input, userInfo);
        String transId = param.getString("TRANS_ID");
        String rsrvStr2 = getTransIdIBoss(transId);

        // 调用IBOSS接口
        IDataset bossData = IBossCall.checkUserCardFlow(rsrvStr2, param.getString("SERIAL_NUMBER"), param.getString("SERIAL_NUMBER_TEMP"));
        if (IDataUtil.isEmpty(bossData))
        {
            returnInfo.put("X_RESULTCODE", "1020");
            returnInfo.put("X_RESULTINFO", "异地写卡请求验证失败!");
            returnInfo.put("X_RSPTYPE", "2");// add by ouyk
            returnInfo.put("X_RSPCODE", "2998");// add by ouyk
            return returnInfo;
        }
        else
        {
            if (!"0000".equals(bossData.getData(0).getString("X_RSPCODE")))
            {
                returnInfo.put("X_RESULTCODE", "1020");
                returnInfo.put("X_RESULTINFO", "异地写卡请求验证失败!" + bossData.getData(0).getString("X_RSPDESC"));
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }
        }
        //
        param.put("REMARK", "第一步:短信换卡申请校验通过(本地使用异地)");
        param.put("RSRV_STR2", rsrvStr2);// 异地请求串
        param.put("RSRV_TAG1", "2");// 本地用户使用异地USIM卡进行自助补换卡
        Dao.insert("TF_F_SELFHELPCARD_FLOW", param);

        returnInfo.put("TRANS_ID", transId);
        returnInfo.put("IS_LOCAL", "1");// 本地使用外地卡
        returnInfo.put("X_RESULTCODE", "0");
        returnInfo.put("X_RESULTINFO", "第一步:短信换卡申请校验通过.");
        return returnInfo;
    }

    // 本地两不一快
    private IData checkUserCardFlowL2L(IData input, IData userInfo) throws Exception
    {
        IData returnInfo = new DataMap();
        // 调用资源接口查询临时号码资源数据
        IData resInfo = ResCall.qryPhoneEmptyBySn(input.getString("SERIAL_NUMBER_TEMP"), null);
        if (IDataUtil.isEmpty(resInfo))
        {
            returnInfo.put("X_RESULTCODE", "1003");
            returnInfo.put("X_RESULTINFO", "资源找不到该USIM卡临时号码的信息!");
            returnInfo.put("X_RSPTYPE", "2");// add by ouyk
            returnInfo.put("X_RSPCODE", "2998");// add by ouyk
            return returnInfo;
        }
        //

        // 查询短信配置
        IDataset smsConfig = CommparaInfoQry.getCommParas("CSM", "4456", "LBYK", "1", "0898");
        if (IDataUtil.isEmpty(smsConfig))
        {
            returnInfo.put("X_RESULTCODE", "-1");
            returnInfo.put("X_RESULTINFO", "获取下发短信内容配置信息无数据！");
            returnInfo.put("X_RSPTYPE", "2");// add by ouyk
            returnInfo.put("X_RSPCODE", "2998");// add by ouyk
            return returnInfo;
        }

        IData param = prepareCardFlowInfo(input, userInfo);
        param.put("IMSI_TEMP", resInfo.getString("IMSI"));
        param.put("KI_TEMP", resInfo.getString("KI"));
        param.put("OPC_TEMP", resInfo.getString("OPC"));
        param.put("PIN_TEMP", resInfo.getString("PIN"));
        param.put("PIN2_TEMP", resInfo.getString("PIN2"));
        param.put("PUK_TEMP", resInfo.getString("PUK"));
        param.put("PUK2_TEMP", resInfo.getString("PUK2"));
        param.put("REMARK", "第一步:短信换卡申请校验通过");
        param.put("RSRV_TAG1", "1");// 本地用户使用本地USIM卡进行自助补换卡
        param.put("EMPTY_CARD_ID", resInfo.getString("EMPTY_CARD_ID"));
        param.put("SIM_CARD_NO_TEMP", resInfo.getString("SIM_CARD_NO"));
        Dao.insert("TF_F_SELFHELPCARD_FLOW", param);

        String transId = param.getString("TRANS_ID");
        // 下发短信
        String forceObj = smsConfig.getData(0).getString("PARA_CODE2", "").trim() + transId;
        String noticeContent = smsConfig.getData(0).getString("PARA_CODE4", "").trim() + smsConfig.getData(0).getString("PARA_CODE5", "").trim() + smsConfig.getData(0).getString("PARA_CODE6", "").trim()
                + smsConfig.getData(0).getString("PARA_CODE7", "").trim() + smsConfig.getData(0).getString("PARA_CODE8", "").trim() + smsConfig.getData(0).getString("PARA_CODE9", "").trim();

        IData smsData = new DataMap();
        smsData.put("FORCE_OBJECT", forceObj);
        smsData.put("RECV_OBJECT", input.getString("SERIAL_NUMBER_TEMP"));
        smsData.put("NOTICE_CONTENT", noticeContent);
        smsData.put("SMS_KIND_CODE", "08");
        smsData.put("SMS_PRIORITY", "50");
        smsData.put("IS_BIN", "1");
        SmsSend.insSms(smsData);

        returnInfo.put("TRANS_ID", transId);
        returnInfo.put("SERIAL_NUMBER_TEMP", resInfo.getString("SERIAL_NUMBER"));
        returnInfo.put("X_RESULTCODE", "0");
        returnInfo.put("X_RESULTINFO", "第一步:短信换卡申请校验通过.");
        return returnInfo;
    }

    public IData checkWriteInfo(IData input) throws Exception
    {
        IData returnInfo = new DataMap();
        try
        {
            chkParam(input, "TRANS_ID");
            chkParam(input, "SMS_CONTENT");
            chkParam(input, "SERIAL_NUMBER_TEMP");
            String transId = input.getString("TRANS_ID");
            String smsContent = input.getString("SMS_CONTENT");
            String tempNumber = input.getString("SERIAL_NUMBER_TEMP");

            // 查询短信配置
            IDataset smsConfig = CommparaInfoQry.getCommParas("CSM", "4456", "LBYK", "2", "0898");
            if (IDataUtil.isEmpty(smsConfig))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取短信配置信息出错！");
            }
            IData flowInfo = getUserFlowInfo(transId, smsConfig);
            IData data = new DataMap();
            data.put("SERIAL_NUMBER", flowInfo.getString("SERIAL_NUMBER"));
            data.put("SERIAL_NUMBER_TEMP", tempNumber);
            checkParam(data, flowInfo);
            if (!"B".equals(flowInfo.getString("STATE")))
            {
                returnInfo.put("X_RESULTCODE", "1005");
                returnInfo.put("X_RESULTINFO", "当前流程节点应在获取远程写卡信息之后!");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }
            // 调用写卡平台验证写卡结果
            WebServiceClient wsc = new WebServiceClient();
            AssemDynData ass = new AssemDynData();
            ass.setSeqNo(flowInfo.getString("RSRV_STR5", ""));
            ass.setCardInfo("080A" + replace(flowInfo.getString("SIM_CARD_NO_NEW", "")) + "0E0A" + flowInfo.getString("EMPTY_CARD_ID", ""));
            ass.setCardRsp(smsContent);
            EncAssemDynDataRsp encData = null;

            // 调用webservice接口校验写卡结果
            //encData = wsc.repCheckClient(ass);
            String message = "0"; // 加密信息
            //
            String tag = flowInfo.getString("RSRV_TAG1", "");
            String writeTag = (message.equals("0")) ? "0" : "1";
            flowInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
            if ("1".equals(tag))
            {
                // 回写写卡资料
                WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
                bean.remoteWriteUpdate(flowInfo.getString("IMSI_NEW"), flowInfo.getString("EMPTY_CARD_ID"), flowInfo.getString("SIM_CARD_NO_NEW"), "2", writeTag);
            }
            else if ("3".equals(tag))
            {
                //两不一快不处理白卡
            	//ResCall.remoteWriteEmptyCard(writeTag, flowInfo.getString("EMPTY_CARD_ID"));
                // 如果写卡结果验证失败，回复RESULT=1
                data.put("Result", writeTag);
                data.put("TEMP_NUMBER", data.getString("SERIAL_NUMBER_TEMP"));
                data.put("IMSI", flowInfo.getString("IMSI_NEW"));
                // 通过加密获取EncK、EncOpc
                String k = flowInfo.getString("KI_TEMP");
                String opc = flowInfo.getString("OPC_TEMP");
                // 第一次解密
                KIFunc kifunc = new KIFunc();
                k = kifunc.DecryptKI(k);
                opc = kifunc.DecryptKI(opc);
                // 第二次解密webservice
                RoamEncPreData roam = new RoamEncPreData();
                roam.setSeqNo(flowInfo.getString("RSRV_STR5", ""));
                roam.setLocalProvCode("898");

                EncPresetData enc = new EncPresetData();
                enc.setK(k);
                enc.setOPC(opc);
                roam.setEncPresetData(enc);
                RoamEncPreDataRsp rsp = new RoamEncPreDataRsp();
                try
                {
                    rsp = wsc.encPreData(roam);
                    String resultCode = rsp.getResultCode();
                    if (!"0".equals(resultCode))
                    {
                        returnInfo.put("X_RESULTCODE", "1023");
                        returnInfo.put("X_RESULTINFO", "第三步:写卡平台返回错误，" + rsp.getResultMessage());

                        flowInfo.put("STATE", "N");
                        flowInfo.put("REMARK", "第三步:写卡平台返回错误，" + rsp.getResultMessage());
                        Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);
                        returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                        returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                        return returnInfo;
                    }
                }
                catch (Exception e)
                {
                    SessionManager.getInstance().rollback();
                    returnInfo.put("Response", "4029");
                    returnInfo.put("X_RESULTCODE", "4029");
                    returnInfo.put("X_RESULTINFO", "第三步:写卡平台返回错误，解密失败，" + rsp.getResultMessage());

                    flowInfo.put("STATE", "N");
                    flowInfo.put("REMARK", "第三步:写卡平台返回错误，解密失败，" + rsp.getResultMessage());
                    Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);
                    returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                    returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                    return returnInfo;

                }

                IDataset bossInfos = IBossCall.backWriteInfo(flowInfo.getString("RSRV_STR2", ""), flowInfo.getString("SERIAL_NUMBER"), flowInfo.getString("SERIAL_NUMBER_TEMP"), flowInfo.getString("IMSI_NEW"), rsp.getEncPresetDataK(), rsp
                        .getEncPresetDataOPc(), rsp.getSignature(), "898");

                if (IDataUtil.isEmpty(bossInfos))
                {
                    returnInfo.put("X_RESULTCODE", "1017");
                    returnInfo.put("X_RESULTINFO", "第三步:回传预置参数错误!");

                    flowInfo.put("STATE", "N");
                    flowInfo.put("REMARK", "第三步:回传预置参数错误!");
                    Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);
                    returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                    returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                    return returnInfo;
                }
                else
                {
                    if (!"0000".equals(bossInfos.getData(0).getString("X_RSPCODE")))
                    {
                        returnInfo.put("X_RESULTCODE", "1017");
                        returnInfo.put("X_RESULTINFO", "第三步:回传预置参数错误!" + bossInfos.getData(0).getString("X_RESULTINFO"));

                        flowInfo.put("STATE", "N");
                        flowInfo.put("REMARK", "第三步:回传预置参数错误!" + bossInfos.getData(0).getString("X_RESULTINFO"));
                        Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);
                        returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                        returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                        return returnInfo;
                    }
                }
            }

            smsConfig = CommparaInfoQry.getCommParas("CSM", "4456", "LBYK", "3", "0898");
            if (IDataUtil.isEmpty(smsConfig))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取短信配置信息出错！");
            }
            // 写卡结果验证失败，发送短信，临时卡属于本地
            if (!"0".equals(writeTag))
            {
                // 发送短信
                String forceObj = smsConfig.getData(0).getString("PARA_CODE2", "").trim() + flowInfo.getString("TRANS_ID");
                String noticeContent = "自助换卡写卡失败！";

                IData smsData = new DataMap();
                smsData.put("FORCE_OBJECT", forceObj);
                smsData.put("RECV_OBJECT", flowInfo.getString("SERIAL_NUMBER_TEMP"));
                smsData.put("NOTICE_CONTENT", noticeContent);
                smsData.put("SMS_KIND_CODE", "08");
                smsData.put("SMS_PRIORITY", "50");
                smsData.put("IS_BIN", "1");
                SmsSend.insSms(smsData);

                returnInfo.put("X_RESULTCODE", "1010");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                returnInfo.put("X_RESULTINFO", "第三步:写卡失败!错误编码：" + encData.getResultMessage());

                flowInfo.put("STATE", "N");
                flowInfo.put("REMARK", "第三步:写卡平台校验错误!错误编码：" + encData.getResultMessage());
                Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);

                return returnInfo;
            }

            // 下发XKCG短信
            if ("1".equals(tag))
            {
                // sim卡选占
                SimCardCheckBean checkBean = BeanManager.createBean(SimCardCheckBean.class);
                checkBean.preOccupySimCard(flowInfo.getString("SERIAL_NUMBER"), flowInfo.getString("SIM_CARD_NO_NEW"), "2", "1","0");

                // 给原手机下发短信
                String forceObj = smsConfig.getData(0).getString("PARA_CODE2", "").trim() + flowInfo.getString("TRANS_ID");
                String noticeContent = smsConfig.getData(0).getString("PARA_CODE4", "").trim() + smsConfig.getData(0).getString("PARA_CODE5", "").trim() + smsConfig.getData(0).getString("PARA_CODE6", "").trim()
                        + smsConfig.getData(0).getString("PARA_CODE7", "").trim() + smsConfig.getData(0).getString("PARA_CODE8", "").trim() + smsConfig.getData(0).getString("PARA_CODE9", "").trim();

                IData smsData = new DataMap();
                smsData.put("FORCE_OBJECT", forceObj);
                smsData.put("RECV_OBJECT", flowInfo.getString("SERIAL_NUMBER"));
                smsData.put("NOTICE_CONTENT", noticeContent);
                smsData.put("SMS_KIND_CODE", "08");
                smsData.put("SMS_PRIORITY", "50");
                smsData.put("IS_BIN", "1");
                SmsSend.insSms(smsData);

            }

            flowInfo.put("STATE", "C");
            flowInfo.put("RSRV_STR1", smsContent);// 短信内容
            flowInfo.put("REMARK", "第三步:写卡数据校验成功.");
            Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);

            returnInfo.put("X_RESULTCODE", "0");
            returnInfo.put("X_RESULTINFO", "第三步:写卡数据校验成功！");
            return returnInfo;

        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();
            returnInfo.put("X_RESULTCODE", "-1");
            returnInfo.put("X_RESULTINFO", e.getMessage());
            returnInfo.put("X_RSPTYPE", "2");// add by ouyk
            returnInfo.put("X_RSPCODE", "2998");// add by ouyk
            return returnInfo;
        }
    }

    public IData checkWriteInfoL2F(IData data) throws Exception
    {
        IData returnInfo = new DataMap();
        returnInfo.put("TRANS_ID", data.getString("TRANS_ID"));
        returnInfo.put("TEMP_NUMBER", data.getString("TEMP_NUMBER"));
        returnInfo.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        try
        {
            chkParam(data, "TRANS_ID");
            chkParam(data, "TEMP_NUMBER");
            chkParam(data, "SERIAL_NUMBER");
            chkParam(data, "IMSI");
            chkParam(data, "Result");

            String rsrvStr2 = data.getString("TRANS_ID");
            String tempNumber = data.getString("TEMP_NUMBER");
            String serialNumber = data.getString("SERIAL_NUMBER");
            String imsi = data.getString("IMSI");
            String result = data.getString("Result");

            IData flowInfo = UserChangeCardFlowInfoQry.qryUserCardFlowInfoByIBossId(rsrvStr2);
            if (IDataUtil.isEmpty(flowInfo))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "异地短信申请校验trans_id=" + rsrvStr2 + "不存在!");
            }
            IData numberData = new DataMap();
            numberData.put("SERIAL_NUMBER", serialNumber);
            numberData.put("SERIAL_NUMBER_TEMP", tempNumber);
            checkParam(numberData, flowInfo);
            if (!"B".equals(flowInfo.getString("STATE")))
            {
                returnInfo.put("Response", "1005");
                returnInfo.put("X_RESULTCODE", "1005");
                returnInfo.put("X_RESULTINFO", "当前流程节点应在获取远程写卡信息之后!");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }

            if (!"0".equals(result))
            {
                flowInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                flowInfo.put("STATE", "N");
                flowInfo.put("REMARK", "第三步:异地写卡失败!");
                Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);

                returnInfo.put("Response", "0000");
                returnInfo.put("X_RESULTCODE", "1010");
                returnInfo.put("X_RESULTINFO", "第三步:异地写卡失败!");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }

            // 加密K和OPC，并写卡参数回传
            LocalDecPreData local = new LocalDecPreData();
            local.setSeqNo(flowInfo.getString("RSRV_STR5", ""));
            local.setEncPresetDataK(data.getString("EncK", ""));
            local.setEncPresetDataOPc(data.getString("EncOpc", ""));
            local.setLocalProvCode(data.getString("LocalProvCode", ""));
            local.setSignature(data.getString("Signature", ""));

            WebServiceClient wsc = new WebServiceClient();
            LocalDecPreDataRsp rsp = new LocalDecPreDataRsp();
            try
            {
                rsp = wsc.decPreData(local);
                if (!"0".equals(rsp.getResultCode()))
                {
                    returnInfo.put("Response", "1024");
                    returnInfo.put("X_RESULTCODE", "1024");
                    returnInfo.put("X_RESULTINFO", "第三步:更新资源侧K与OPC失败！加密机返回错误" + rsp.getResultMessage());
                    returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                    returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                    flowInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    flowInfo.put("STATE", "N");
                    flowInfo.put("REMARK", "第三步:更新资源侧K与OPC失败！加密机返回错误!");
                    Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);

                    return returnInfo;
                }
            }
            catch (Exception e)
            {
                SessionManager.getInstance().rollback();
                returnInfo.put("Response", "1024");
                returnInfo.put("X_RESULTCODE", "1024");
                returnInfo.put("X_RESULTINFO", "第三步:更新资源侧K与OPC失败！加密机加解密错误，" + rsp.getResultMessage());
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                flowInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                flowInfo.put("STATE", "N");
                flowInfo.put("REMARK", "第三步:更新资源侧K与OPC失败！加密机加解密错误，" + rsp.getResultMessage());
                Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);

                return returnInfo;

            }
            String k = rsp.getPresetData().getK();
            String opc = rsp.getPresetData().getOPC();
            KIFunc kifunc = new KIFunc();
            k = kifunc.EncryptKI(k);
            opc = kifunc.EncryptKI(opc);

            try
            {
                WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
                bean.remoteWriteUpdate(flowInfo.getString("IMSI_NEW"), "", flowInfo.getString("SIM_CARD_NO_NEW"), "1", result,k,opc);
                SimCardCheckBean checkBean = BeanManager.createBean(SimCardCheckBean.class);
                checkBean.preOccupySimCard(flowInfo.getString("SERIAL_NUMBER"), flowInfo.getString("SIM_CARD_NO_NEW"), "1", "1");
            }
            catch (Exception e)
            {
                SessionManager.getInstance().rollback();
                returnInfo.put("Response", "1021");
                returnInfo.put("X_RESULTCODE", "1021");
                returnInfo.put("X_RESULTINFO", "第三步:更新资源侧K与OPC失败！" + e.getMessage());
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                flowInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                flowInfo.put("STATE", "N");
                flowInfo.put("REMARK", "第三步:更新资源侧K与OPC失败！" + rsp.getResultMessage());
                Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);
                return returnInfo;
            }

            flowInfo.put("STATE", "C");
            flowInfo.put("REMARK", "第三步:写卡数据校验成功(本地使用异地).");
            flowInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
            flowInfo.put("KI_NEW", k);
            flowInfo.put("OPC_NEW", opc);
            flowInfo.put("KI_TEMP", k);
            flowInfo.put("OPC_TEMP", opc);
            Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);// K与OPC回写

            // 下发短信
            IDataset smsConfig = CommparaInfoQry.getCommParas("CSM", "4456", "LBYK", "3", "0898");
            if (IDataUtil.isEmpty(smsConfig))
            {
                returnInfo.put("X_RESULTCODE", "-1");
                returnInfo.put("X_RESULTINFO", "获取下发短信内容配置信息无数据！");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }

            String forceObj = smsConfig.getData(0).getString("PARA_CODE2", "").trim() + flowInfo.getString("TRANS_ID");
            String noticeContent = smsConfig.getData(0).getString("PARA_CODE4", "").trim() + smsConfig.getData(0).getString("PARA_CODE5", "").trim() + smsConfig.getData(0).getString("PARA_CODE6", "").trim()
                    + smsConfig.getData(0).getString("PARA_CODE7", "").trim() + smsConfig.getData(0).getString("PARA_CODE8", "").trim() + smsConfig.getData(0).getString("PARA_CODE9", "").trim();

            IData smsData = new DataMap();
            smsData.put("FORCE_OBJECT", forceObj);
            smsData.put("RECV_OBJECT", flowInfo.getString("SERIAL_NUMBER"));
            smsData.put("NOTICE_CONTENT", noticeContent);
            smsData.put("SMS_KIND_CODE", "08");
            smsData.put("SMS_PRIORITY", "50");
            smsData.put("IS_BIN", "1");
            SmsSend.insSms(smsData);

            returnInfo.put("Response", "0000");
            returnInfo.put("X_RESULTCODE", "0");
            returnInfo.put("X_RESULTINFO", "第三步:写卡数据校验成功(本地使用异地)！");
            return returnInfo;
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();
            returnInfo.put("Response", "1031");
            returnInfo.put("X_RESULTCODE", "1031");
            returnInfo.put("X_RESULTINFO", "第三步:写卡数据校验失败！" + e.getMessage());
            returnInfo.put("X_RSPTYPE", "2");// add by ouyk
            returnInfo.put("X_RSPCODE", "2998");// add by ouyk
            return returnInfo;
        }
    }

    /**
     * 参数校验
     */
    public String chkParam(IData data, String strColName) throws Exception
    {
        String strParam = data.getString(strColName);

        if (StringUtils.isEmpty(strParam))
        {
            StringBuilder strError = new StringBuilder("-1:接口参数检查: 输入参数[");
            strError.append(strColName).append("]不存在或者参数值为空");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, strError);
        }
        return strParam;
    }

    /**
     * 获取序列 IBOSS使用
     */
    public String getTransIdIBoss(String transId) throws Exception
    {
        int a = transId.length();
        String ftrans = "";
        if (a > 6)
        {
            ftrans = transId.substring(a - 6, a);
        }
        else if (a < 6)
        {
            for (int i = 0; i < 6 - a; i++)
            {
                ftrans += "0";
            }
            ftrans += transId;
        }
        else
        {
            ftrans = transId;
        }
        // 3位省代码+8位业务编码（BIPCode）+14位组包时间YYYYMMDDHH24MMSS+6位流水号（定长），序号从000001开始，增量步长为1。
        String returnStr = "898" + "BIP2B026" + SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT) + ftrans;

        return returnStr;
    }

    /**
     * 根据transID获取换卡流程信息
     */
    private IData getUserFlowInfo(String transId, IDataset smsConfig) throws Exception
    {
        String reForceObj = smsConfig.getData(0).getString("PARA_CODE2").trim();
        if (!transId.startsWith(reForceObj))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "TRANS_ID格式不正确！");
        }
        int smsLen = reForceObj.length();
        transId = transId.substring(smsLen);
        IDataset flowInfo = UserChangeCardFlowInfoQry.qryUserCardFlowInfoByTransId(transId);
        if (IDataUtil.isEmpty(flowInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "短信申请校验TARNS_ID=" + transId + "不存在！");
        }
        return flowInfo.getData(0);
    }

    public IData getWriteSimCardInfo(IData input) throws Exception
    {
        IData returnInfo = new DataMap();
        try
        {
            chkParam(input, "TRANS_ID");
            chkParam(input, "SERIAL_NUMBER_TEMP");// 4G手机卡号
            String transId = input.getString("TRANS_ID");
            String tempNumber = input.getString("SERIAL_NUMBER_TEMP");

            // 查询短信配置
            IDataset smsConfig = CommparaInfoQry.getCommParas("CSM", "4456", "LBYK", "1", "0898");
            if (IDataUtil.isEmpty(smsConfig))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取短信配置信息出错！");
            }

            IData flowInfo = getUserFlowInfo(transId, smsConfig);
            IData data = new DataMap();
            data.put("SERIAL_NUMBER", flowInfo.getString("SERIAL_NUMBER"));
            data.put("SERIAL_NUMBER_TEMP", tempNumber);
            checkParam(data, flowInfo);
            if (!"A".equals(flowInfo.getString("STATE")))
            {
                returnInfo.put("X_RESULTCODE", "1005");
                returnInfo.put("X_RESULTINFO", "当前流程节点应在短信申请校验之后!");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }
            IDataset timeOut = UserChangeCardFlowInfoQry.checkFlowTimeOut(flowInfo.getString("TRANS_ID"));
            flowInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
            if (IDataUtil.isEmpty(timeOut))
            {
                flowInfo.put("STATE", "N");
                flowInfo.put("REMARK", "短信确认已超时!");
                Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);

                returnInfo.put("X_RESULTCODE", "1006");
                returnInfo.put("X_RESULTINFO", "短信确认已超时!");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }
            String tag = flowInfo.getString("RSRV_TAG1");
            String emptyCardId = flowInfo.getString("EMPTY_CARD_ID", "");
            String simCardNo = "";
            String imsi = "";
            String pin = "";
            String pin2 = "";
            String puk = "";
            String puk2 = "";
            String smsp = "";

            WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
            IData simInfo = new DataMap();
            if ("1".equals(tag))
            {
                // 调用资源信息获取写卡信息
                simInfo = bean.getSpeSimInfo(flowInfo.getString("SERIAL_NUMBER"), emptyCardId, "2", "2").getData(0);
                simCardNo = simInfo.getString("SIM_CARD_NO", "");
                imsi = simInfo.getString("IMSI", "");
                pin = simInfo.getString("PIN", "");
                pin2 = simInfo.getString("PIN2", "");
                puk = simInfo.getString("PUK", "");
                puk2 = simInfo.getString("PUK2", "");
                smsp = "+8613800898500";
            }
            else if ("3".equals(tag))
            {
                // 调用IBOSS接口获取写卡信息
                IDataset simInfos = IBossCall.getWriteCardInfo(flowInfo.getString("RSRV_STR2"), flowInfo.getString("SERIAL_NUMBER"), flowInfo.getString("SERIAL_NUMBER_TEMP"), flowInfo.getString("EMPTY_CARD_ID"), flowInfo
                        .getString("SIM_CARD_NO_TEMP"));
                if (IDataUtil.isEmpty(simInfos))
                {
                    returnInfo.put("X_RESULTCODE", "1007");
                    returnInfo.put("X_RESULTINFO", "第二步:获取远程写卡信息有异常(异地)!");

                    flowInfo.put("STATE", "N");
                    flowInfo.put("REMARK", "第二步:获取远程写卡信息有异常(异地)!");
                    Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);
                    returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                    returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                    return returnInfo;
                }
                else
                {
                    if (!"0000".equals(simInfos.getData(0).getString("X_RSPCODE")))
                    {
                        returnInfo.put("X_RESULTCODE", "1007");
                        returnInfo.put("X_RESULTINFO", "第二步:获取远程写卡信息有异常(异地)!" + simInfos.getData(0).getString("X_RESULTINFO"));

                        flowInfo.put("STATE", "N");
                        flowInfo.put("REMARK", "第二步:获取远程写卡信息有异常(异地)!" + simInfos.getData(0).getString("X_RESULTINFO"));
                        Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);
                        returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                        returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                        return returnInfo;
                    }
                    simInfo = simInfos.getData(0);
                }

                simCardNo = simInfo.getString("ICCID", "");
                imsi = simInfo.getString("IMSI", "");
                pin = simInfo.getString("PIN1", "");
                pin2 = simInfo.getString("PIN2", "");
                puk = simInfo.getString("PUK1", "");
                puk2 = simInfo.getString("PUK2", "");
                smsp = simInfo.getString("SMSP", "");
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
            eas1.setMsisdn(flowInfo.getString("SERIAL_NUMBER", ""));
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
                flowInfo.put("STATE", "N");
                flowInfo.put("REMARK", "第二步:调用写卡平台报错!" + e.getMessage());
                Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);

                returnInfo.put("X_RESULTCODE", "1008");
                returnInfo.put("X_RESULTINFO", "第二步:调用写卡平台报错!" + e.getMessage());
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                // log.error("写卡平台",e);
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
                flowInfo.put("STATE", "N");
                flowInfo.put("REMARK", "第二步:写卡平台返回错误" + encData.getResultMessage());
                Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);

                returnInfo.put("X_RESULTCODE", "1009");
                returnInfo.put("X_RESULTINFO", "第二步:写卡平台返回错误" + encData.getResultMessage());
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }
            //

            flowInfo.put("STATE", "B");
            flowInfo.put("REMARK", "第二步:获取远程写卡信息成功." + message);
            flowInfo.put("PIN_NEW", pin);
            flowInfo.put("PIN2_NEW", pin2);
            flowInfo.put("PUK_NEW", puk);
            flowInfo.put("PUK2_NEW", puk2);
            flowInfo.put("KI_NEW", flowInfo.getString("KI_TEMP"));
            flowInfo.put("IMSI_NEW", imsi);
            flowInfo.put("OPC_NEW", flowInfo.getString("OPC_TEMP"));
            flowInfo.put("SIM_CARD_NO_NEW", simCardNo);
            flowInfo.put("RSRV_STR5", seqNo);
            Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);

            // 下发短信 message
            String noticeContent = "BIN|" + message;

            smsConfig = CommparaInfoQry.getCommParas("CSM", "4456", "LBYK", "2", "0898");
            if (IDataUtil.isEmpty(smsConfig))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取短信配置信息出错！");
            }
            String forceObj = smsConfig.getData(0).getString("PARA_CODE2", "").trim() + flowInfo.getString("TRANS_ID");

            int ran = (int) (Math.random() * 10) % 5 + 1;
            if (ran > 6 || ran < 1)
            {
                ran = 5;
            }
            String smsKindCode = "2" + ran;

            IData smsData = new DataMap();
            smsData.put("FORCE_OBJECT", forceObj);
            smsData.put("RECV_OBJECT", input.getString("SERIAL_NUMBER_TEMP"));
            smsData.put("NOTICE_CONTENT", noticeContent);
            smsData.put("SMS_KIND_CODE", smsKindCode);
            smsData.put("SMS_PRIORITY", "50");
            smsData.put("IS_BIN", "1");
            SmsSend.insSms(smsData);

            returnInfo.put("X_RESULTCODE", "0");
            returnInfo.put("X_RESULTINFO", "第二步:获取远程写卡信息成功！");

            return returnInfo;

        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();
            returnInfo.put("X_RESULTCODE", "-1");
            returnInfo.put("X_RESULTINFO", e.getMessage());
            returnInfo.put("X_RSPTYPE", "2");// add by ouyk
            returnInfo.put("X_RSPCODE", "2998");// add by ouyk
            return returnInfo;
        }
    }

    public IData getWriteSimCardInfoL2F(IData data) throws Exception
    {
        IData returnInfo = new DataMap();
        returnInfo.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        returnInfo.put("TEMP_NUMBER", data.getString("TEMP_NUMBER"));
        returnInfo.put("TRANS_ID", data.getString("TRANS_ID"));
        try
        {
            chkParam(data, "TRANS_ID");
            chkParam(data, "SERIAL_NUMBER");
            chkParam(data, "TEMP_NUMBER");// 4G手机卡号
            chkParam(data, "Result");// 下发二次确认短信结果
            String rsrvStr2 = data.getString("TRANS_ID");
            String serialNumber = data.getString("SERIAL_NUMBER");
            String tempNumber = data.getString("TEMP_NUMBER");
            String result = data.getString("Result");
            IData flowInfo = UserChangeCardFlowInfoQry.qryUserCardFlowInfoByIBossId(rsrvStr2);
            if (IDataUtil.isEmpty(flowInfo))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "异地短信申请校验trans_id=" + rsrvStr2 + "不存在!");
            }
            IData numberData = new DataMap();
            numberData.put("SERIAL_NUMBER", serialNumber);
            numberData.put("SERIAL_NUMBER_TEMP", tempNumber);
            checkParam(numberData, flowInfo);
            if (!"A".equals(flowInfo.getString("STATE")))
            {
                returnInfo.put("Response", "1005");
                returnInfo.put("X_RESULTCODE", "1005");
                returnInfo.put("X_RESULTINFO", "当前流程节点应在短信申请校验之后!");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                return returnInfo;
            }
            IDataset timeOut = UserChangeCardFlowInfoQry.checkTimeOut(flowInfo.getString("TRANS_ID"));
            if (timeOut.isEmpty())
            {
                returnInfo.put("Response", "1006");
                returnInfo.put("X_RESULTCODE", "1006");
                returnInfo.put("X_RESULTINFO", "短信确认已超时!");
                returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                flowInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                flowInfo.put("STATE", "N");
                flowInfo.put("REMARK", "短信确认已超时!");
                Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);
                return returnInfo;
            }
            if (!"0".equals(result))
            {
                // 1：短信回复不正确（用户在规定的次数内未正确回复短信）；
                // 2：取消换卡；
                // 3：其他错误
                if ("1".equals(result))
                {
                    returnInfo.put("Response", "0000");
                    returnInfo.put("X_RESULTCODE", "1022");
                    returnInfo.put("X_RESULTINFO", "异地下发二次短信错误!");
                    returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                    returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                    flowInfo.put("STATE", "N");
                    flowInfo.put("REMARK", "异地下发二次短信错误!");
                }
                else if ("2".equals(result))
                {
                    returnInfo.put("Response", "0000");
                    returnInfo.put("X_RESULTCODE", "1026");
                    returnInfo.put("X_RESULTINFO", "异地取消换卡!");
                    returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                    returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                    flowInfo.put("STATE", "N");
                    flowInfo.put("REMARK", "异地取消换卡!");
                }
                else if ("3".equals(result))
                {
                    returnInfo.put("Response", "0000");
                    returnInfo.put("X_RESULTCODE", "1027");
                    returnInfo.put("X_RESULTINFO", "异地其它错误!");
                    returnInfo.put("X_RSPTYPE", "2");// add by ouyk
                    returnInfo.put("X_RSPCODE", "2998");// add by ouyk
                    flowInfo.put("STATE", "N");
                    flowInfo.put("REMARK", "异地其它错误!");
                }
                flowInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);
                return returnInfo;
            }
            // 获取资源写卡信息
            WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
            IData resInfo = bean.getSpeSimInfo(serialNumber, "", "2", "1").getData(0);

            flowInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
            flowInfo.put("STATE", "B");
            flowInfo.put("REMARK", "第二步:获取远程写卡信息成功(异地).");
            flowInfo.put("PIN_NEW", resInfo.getString("PIN"));
            flowInfo.put("PIN2_NEW", resInfo.getString("PIN2"));
            flowInfo.put("PUK_NEW", resInfo.getString("PUK"));
            flowInfo.put("PUK2_NEW", resInfo.getString("PUK2"));
            flowInfo.put("IMSI_NEW", resInfo.getString("IMSI"));
            flowInfo.put("SIM_CARD_NO_NEW", resInfo.getString("SIM_CARD_NO"));
            flowInfo.put("RSRV_STR5", SeqMgr.getTradeId().substring(6));// 加密seq_id
            flowInfo.put("EMPTY_CARD_ID", data.getString("CARDSN"));
            flowInfo.put("SIM_CARD_NO_TEMP", data.getString("ICCID"));
            Dao.update("TF_F_SELFHELPCARD_FLOW", flowInfo);

            returnInfo.put("IMSI", resInfo.getString("IMSI"));
            returnInfo.put("ICCID", resInfo.getString("SIM_CARD_NO"));
            returnInfo.put("SMSP", "+8613800898500");
            returnInfo.put("PIN1", resInfo.getString("PIN"));
            returnInfo.put("PIN2", resInfo.getString("PIN2"));
            returnInfo.put("PUK1", resInfo.getString("PUK"));
            returnInfo.put("PUK2", resInfo.getString("PUK2"));
            returnInfo.put("HomeProv", "898");
            returnInfo.put("Response", "0000");
            returnInfo.put("X_RESULTCODE", "0");
            returnInfo.put("X_RESULTINFO", "第二步:获取远程写卡信息成功(本地使用异地)！");
            return returnInfo;
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();
            returnInfo.put("Response", "1030");
            returnInfo.put("X_RESULTCODE", "1030");
            returnInfo.put("X_RESULTINFO", "第二步:获取远程写卡信息失败！" + e.getMessage());
            returnInfo.put("X_RSPTYPE", "2");// add by ouyk
            returnInfo.put("X_RSPCODE", "2998");// add by ouyk
            return returnInfo;
        }
    }

    private IData prepareCardFlowInfo(IData input, IData userInfo) throws Exception
    {
        IData param = new DataMap();
        String transId = SeqMgr.getTransId();
        param.put("TRANS_ID", transId);
        param.put("USER_ID", userInfo.getString("USER_ID"));
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        param.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE", ""));
        param.put("STATE", "A");
        param.put("START_DATE", SysDateMgr.getSysTime());
        param.put("END_DATE", SysDateMgr.getTomorrowTime());
        param.put("UPDATE_TIME", SysDateMgr.getSysTime());
        param.put("SERIAL_NUMBER_TEMP", input.getString("SERIAL_NUMBER_TEMP"));
        // param.put("RSRV_STR1", input.getString("SMS_CONTENT",""));// 短信内容
        return param;
    }
}
