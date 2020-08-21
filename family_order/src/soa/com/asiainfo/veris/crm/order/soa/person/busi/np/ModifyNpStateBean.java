
package com.asiainfo.veris.crm.order.soa.person.busi.np;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.NpConst;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ModifyNpStateBean.java
 * @Description: 对应老系统TCS_ModifyNpState 流程
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-5-22 下午2:44:28 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-5-22 lijm3 v1.0.0 修改原因
 */
public class ModifyNpStateBean extends CSBizBean
{

    private static Logger logger = Logger.getLogger(ModifyNpStateBean.class);

    private static final String notifyState = "040,031,041,060,061,030,131,130";// 告知状态

    // private static final IData responseState = new
    // DataMap("{\"121\":\"携出响应失败 \",\"021\":\"携入响应失败\",\"020\":\"携入响应成功\",\"050\":\"携入生效响应成功 \",\"120\":\"携出响应成功\",\"051\":\"携入生效响应失败 \"}");

    /**
     * @Function: ackMethod
     * @Description: 申请请求收到:SOA收到总部ACK回复调用TCS_ModifyNpState ackMethod接口进行处理。根据MESSAGETYPE=”2”，
     * @param param
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-21 上午10:06:54
     */
    public void ackMethod(IData param) throws Exception
    {

        String tradeid = param.getString("TRADEID");// tradeid
        // ACK回复的时候填写。其他时候不填写。
        String commAndCode = param.getString("COMMANDCODE");
        String dealMethod = param.getString("DEALMETHOD");// 处理方式
        String retrymethod = param.getString("RETRYMETHOD");// 绝对时间
        String retry_time = param.getString("RETRY_TIME");
        String messageid = param.getString("MESSAGEID");
        String tradeid2 = "";
        String tradeTypeCode = "";
        String state = "";
        String np_state = "";
        System.out.println("====================commAndCode=======81行"+commAndCode);
        boolean isNotAuth = true;
        if(NpConst.AUTH_RESP.equals(commAndCode))
    	{//查验流程不做工单处理判断 by dengyi5
    		isNotAuth = false;
    	}
        
        if (StringUtils.isNotBlank(tradeid))
        {// 发送方ACK

            int intMonth = Integer.parseInt(StrUtil.getAcceptMonthById(tradeid));
            String acceptMonth = String.valueOf(intMonth);
            IDataset ids = TradeNpQry.getTradeNpInfos(tradeid, acceptMonth);
            if (IDataUtil.isEmpty(ids) || ids.size() > 1)
            {
                throwsException("125096", "查询NP台帐表异常！");
            }

            tradeTypeCode = ids.getData(0).getString("TRADE_TYPE_CODE");
            np_state = ids.getData(0).getString("STATE");

            if ("0".equals(dealMethod))
            {// ACK成功

                if ("39".equals(tradeTypeCode) || "40".equals(tradeTypeCode))
                {

                    if (NpConst.APPLY_REQ.equals(commAndCode))
                    {
                        state = "000";// 发起方携入申请/复机申请ACK成功
                    }
                    else if (NpConst.ACT_REQ.equals(commAndCode))
                    {
                        state = "040";// 发起方携入生效/复机生效ACK成功
                    }
                    else if (NpConst.ACT_REQ_NEW.equals(commAndCode))
                    {
                        state = "040";// 发起方携入生效/复机生效ACK成功
                    }
                    else
                    {
                        throwsException("125097", "SOA参数取值错误，COMMANDCODE=【" + commAndCode + "】！");
                    }
                }	
                else if("41".equals(tradeTypeCode) && NpConst.AUTHCODE_REQ.equals(commAndCode))
                {
                	state = "100";
                }
                else
                {
                    state = "000";// 发起方发送成功
                }

                if ("009".equals(np_state))
                {// tf_b_trade_np 插入消息队列，批量更新状态为009

                    int flag = 0;
                    IData inParam = new DataMap();
                    inParam.put("TRADE_ID", tradeid);
                    inParam.put("MESSAGE_ID", messageid);
                    inParam.put("RETRY_TIME", retry_time);
                    inParam.put("STATE", state);
                    inParam.put("MSG_CMD_CODE", commAndCode);
                    inParam.put("REMARK", param.getString("REMARK", "携号转网"));
                    if ("0".equals(retrymethod))
                    {// retrymethod=0绝对时间1相对时间
                        flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_STATE_ACKSUCC0", inParam,Route.getJourDb(BizRoute.getRouteId()));
                    }
                   
                    else
                    {
                        flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_STATE_ACKSUCC1", inParam,Route.getJourDb(BizRoute.getRouteId()));
                    }
                    if (flag != 1)
                    {
                        throwsException("125008", "ACK返回成功：修改NP表状态时异常！");
                    }
                }

                }
            
            else
            {// ACK失败
                if ("39".equals(tradeTypeCode) || "40".equals(tradeTypeCode))
                {

                    if (NpConst.APPLY_REQ.equals(commAndCode))
                    {// 申请请求
                        state = "011";// 发起方携入申请/复机申请ACK失败
                    }
                    else if (NpConst.ACT_REQ.equals(commAndCode))
                    {// 生效请求
                        state = "041";// 发起方携入申请/复机申请ACK失败
                    }
                    else if (NpConst.ACT_REQ_NEW.equals(commAndCode))
                    {// 生效请求,ACK返回失败
                        state = "041";// 发起方携入申请/复机申请ACK失败
                    }
                    else
                    {
                        throwsException("125098", "SOA参数取值错误，COMMANDCODE=【" + commAndCode + "】！");
                    }
                    
                }
                else if("41".equals(tradeTypeCode) && NpConst.AUTHCODE_REQ.equals(commAndCode))
                {
                	state = "101";
                }
                else
                {
                    state = "011";
                }

                IData inParam = new DataMap();
                inParam.put("TRADE_ID", tradeid);
                inParam.put("MESSAGE_ID", messageid);
                inParam.put("RESULT_CODE", param.getString("RESPONSECODE"));
                inParam.put("STATE", state);
                inParam.put("RESULT_MESSAGE", param.getString("RESPONSEMESSAGE", "ACK发起方回复失败"));
                inParam.put("REMARK", param.getString("REMARK", "携号转网"));
                int flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_STATE_BY_ID", inParam,Route.getJourDb(BizRoute.getRouteId()));
                if (flag != 1)
                {
                    throwsException("125009", "ACK返回失败-人工干预：修改NP表状态时异常！");
                }

                IDataset tradeInfos = TradeInfoQry.getTradeInfobyTradeId(tradeid);
                if (IDataUtil.isEmpty(tradeInfos) && isNotAuth)
                {
                    throwsException("125009", "ACK返回失败-人工干预：获取主台帐资料异常！");
                }

                param.put("ACCT_ID", tradeInfos.getData(0).getString("ACCT_ID"));
                param.put("CUST_ID", tradeInfos.getData(0).getString("CUST_ID"));
                param.put("ORDER_ID", tradeInfos.getData(0).getString("ORDER_ID"));// 获取订单流水号，发短信用
                param.put("ALERT_TYPE_CODE", "1");// 返回失败，发送预警短信
                param.put("TRADE_TYPE_CODE", tradeTypeCode);
            }
    	}
        
        else
        {// 接收方ACK
            String flowId = param.getString("FLOWID");
            //对授权码过期进行判断 @panyu5
            System.out.println("======进入授权码过期判断=====参数commAndCode=======" + commAndCode);
            IDataset ids = new DatasetList();
            if("ACK".equals(commAndCode)){
            ids = TradeNpQry.getTradeNpByFlowId2(flowId);
            //多次申请授权码flowId不唯一,改为使用tradeId查询
            tradeid2 = ids.getData(0).getString("TRADE_ID");
            }
            else{
            ids = TradeNpQry.getTradeNpByFlowId(flowId);
            }
            if (IDataUtil.isNotEmpty(ids) && ids.size() == 1)
            {
                tradeTypeCode = ids.getData(0).getString("TRADE_TYPE_CODE");
                if ((NpConst.SUSPEND_REQ.equals(commAndCode) && "46".equals(tradeTypeCode)) || (NpConst.APPLY_REQ.equals(commAndCode) || NpConst.ACT_REQ.equals(commAndCode)  || NpConst.ACT_REQ_NEW.equals(commAndCode))&& "1503".equals(tradeTypeCode))
                {
                    return;
                }
                int flag = 0;
                if ("0".equals(dealMethod))
                {
                    IData inParam = new DataMap();
                    inParam.put("TRADE_ID", tradeid2);
                    inParam.put("MESSAGE_ID", messageid);
                    inParam.put("STATE", "002");
                    inParam.put("MSG_CMD_CODE", commAndCode);
                    flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_TRESULT_BY_TID", inParam,Route.getJourDb(BizRoute.getRouteId()));
                }
                else
                {
                    IData inParam = new DataMap();
                    inParam.put("TRADE_ID", tradeid2);
                    inParam.put("MESSAGE_ID", messageid);
                    inParam.put("STATE", "001");
                    inParam.put("DEALMETHOD", dealMethod);
                    inParam.put("MSG_CMD_CODE", commAndCode);
                    inParam.put("RESULT_MESSAGE", param.getString("RESPONSEMESSAGE","接收方ACK"));
                    inParam.put("RESULT_CODE", param.getString("RESPONSECODE"));
                    flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_RESULT_BY_TRADEID", inParam,Route.getJourDb(BizRoute.getRouteId()));
                }
                if (flag != 1)
                {
                    throwsException("124098", "ACK接收方根据FLOW_ID=【" + flowId + "】修改TRADE_NP表状态异常！");
                }

            }
            else if (ids.size() == 2)
            {

            	IData temp = new DataMap();
                for (int i = 0, len = ids.size(); i < len; i++)
                {
                    temp.put(ids.getData(i).getString("TRADE_TYPE_CODE").trim(), ids.getData(i));
                }
                String tId = "";
                // 根据FLOW_ID查询工单同时存在44和46，或者43和45，否则报错
                if (temp.containsKey("44") && temp.containsKey("46"))
                {
                    tId = temp.getData("46").getString("TRADE_ID");
                }
                if (temp.containsKey("43") && temp.containsKey("45"))
                {
                    tId = temp.getData("45").getString("TRADE_ID");
                }

                if (StringUtils.isBlank(tId))
                {
                    throwsException("124110", "ACK返回成功：根据FLOWID=【" + flowId + "】查询NP台帐表异常！");
                }
                else
                {
                    int flag = 0;
                    if ("0".equals(dealMethod))
                    {
                        IData inParam = new DataMap();
                        inParam.put("TRADE_ID", tId);
                        inParam.put("MESSAGE_ID", messageid);
                        inParam.put("STATE", "004");
                        inParam.put("MSG_CMD_CODE", commAndCode);
                        flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_TRESULT_BY_TID", inParam,Route.getJourDb(BizRoute.getRouteId()));
                    }
                    else
                    {// 人工干预
                        IData inParam = new DataMap();
                        inParam.put("TRADE_ID", tId);
                        inParam.put("MESSAGE_ID", messageid);
                        inParam.put("STATE", "003");
                        inParam.put("MSG_CMD_CODE", commAndCode);
                        inParam.put("RESULT_CODE", param.getString("RESPONSECODE"));
                        inParam.put("RESULT_MESSAGE", param.getString("RESPONSEMESSAGE", ""));
                        flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_FALLACK_BY_TRADEID", inParam,Route.getJourDb(BizRoute.getRouteId()));
                    }

                    if (flag != 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_124099, flowId);
                        throwsException("124099","ACK接收方根据TRADE_ID=【"+tId+"】修改TRADE_NP表状态异常！");
                    }
                }
            }
            else
            {
                throwsException("124111","ACK返回成功：根据FLOWID=【"+flowId+"】查询NP台帐表记录异常！");
                //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_124111, flowId);
            }

            if (!"0".equals(dealMethod))
            {
                IDataset tradeInfos = TradeInfoQry.getTradeInfobyTradeId(ids.getData(0).getString("TRADE_ID"));
                if (IDataUtil.isEmpty(tradeInfos) && isNotAuth)
                {
                    //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125009);
                    throwsException("125009","ACK返回失败-人工干预：获取主台帐资料异常！");
                }

                param.put("ACCT_ID", tradeInfos.getData(0).getString("ACCT_ID"));
                param.put("CUST_ID", tradeInfos.getData(0).getString("CUST_ID"));
                param.put("ORDER_ID", tradeInfos.getData(0).getString("ORDER_ID"));// 获取订单流水号，发短信用
                param.put("ALERT_TYPE_CODE", "1");// 返回失败，发送预警短信
                param.put("TRADE_TYPE_CODE", tradeTypeCode);
            }

        }
    }

    /**
     * @Function: dispatcherSoaNpWork
     * @Description: essageType =1 告知dealMethod = 0 成功
     * @param param
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-19 下午8:37:32
     */
    public IData dispatcherSoaNpWork(IData param) throws Exception
    {

        if (logger.isDebugEnabled())
        {
            logger.debug("======进入ModifyNpStateBean.dispatcherSoaNpWork方法=====参数params=======" + param.toString());
        }
        String messageType = param.getString("MESSAGETYPE");
        String dealMethod = param.getString("DEALMETHOD");
        String commandCode = param.getString("COMMANDCODE");
        String serialNumber = param.getString("NPCODE");


        String dealType = "";

        if ("1".equals(messageType) && "0".equals(dealMethod))
        {

            if (NpConst.ACT_NOTIFY.equals(commandCode) || NpConst.ACT_NOTIFY_NEW.equals(commandCode))// 生效告知
           
            {
                getActNotifyCode(param);

                if ("42".equals(param.getString("TRADE_TYPE_CODE")))
                {
                    if (StringUtils.isBlank(serialNumber))
                    {
                        CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "参数【NPCODE】不能为空！");
                    }
                    if (!repeatedTrade(serialNumber))
                    {
                        dealType = "NPOUTEFFECTIVE";
                    }
                }
                else
                {
                    dealType = "MODIFYNPSTATE";
                }

            }
            else if (NpConst.RETURN_NOTIFY.equals(commandCode))
            {
                dealType = "MOBILENORETURN";
            }// 号码归还告知
            else
            {
                dealType = "MODIFYNPSTATE";
            }
        }
        else
        {
            dealType = "MODIFYNPSTATE";
        }

        if ("NPOUTEFFECTIVE".equals(dealType))
        {
            // 对应老系统TCS_AcceptApplyDestroyUserReg携出生效
            CSAppCall.call("SS.NpOutEffectiveRegSVC.tradeReg", param);
        }
        else if ("MOBILENORETURN".equals(dealType))
        {
            transferForNpRequest(param);
            // 对应老系统 TCS_MobileNoReturn 号码归还
            CSAppCall.call("SS.MobileNoReturnRegSVC.tradeReg", param);
        }
        else if ("MODIFYNPSTATE".equals(dealType))
        {
            modifyNpState(param);
            if ("0".equals(dealMethod)){            	
            	modifyExecTime(param);//新增方法，查找号码工单，修改执行时间 add by dengyi5
            }
//          ActNotifyInsertSaleLog(param);//生效告知时插营销告警表  修改 panyu5
            String resTag = param.getString("RES_TAG");
            String alertTypeCode = param.getString("ALERT_TYPE_CODE");
            if ("1".equals(resTag))
            {
                /**
                 * b1.SIM_TAG=1;//SIM卡修改标识 0-不修改 1-修改 b1.SN_TAG=0;//号码修改标识 0-不修改 1-修改 b1.SIM_X_GETMODE=5;
                 * b1.MODIFY_TAG="0"
                 */
                // ResCall资源回收
                String tradeId = param.getString("TRADEID");
                if (logger.isDebugEnabled())
                {
                    logger.debug("===========TRADEID=======" + tradeId);
                }

                String simNo = "";
                IDataset ids = TradeResInfoQry.queryTradeResByTradeIdAndModifyTag(tradeId, "0");

                if (IDataUtil.isNotEmpty(ids))
                {
                    for (int i = 0, len = ids.size(); i < len; i++)
                    {
                        IData data = ids.getData(i);
                        if ("1".equals(data.getString("RES_TYPE_CODE")))
                        {
                            simNo = data.getString("RES_CODE");
                        }
                    }
                }
                if (StringUtils.isNotBlank(simNo))
                {
                    ResCall.modifyNpSimInfo(simNo, "5", "", "", "");
                }

            }
            else
            {
                if ("1".equals(alertTypeCode))
                {
                    // NP短信发送 ssTradeReceiptMgr.SendNpAlertSms
                    sendNpAlertSms(param);
                }
            }
        }

        return param;
    }
    /**
     * @Function: modifyExecTime
     * @Description: 申请授权码响应修改41工单的执行时间为立即执行 
     * @param param
     * @throws Exception
     * @version: v1.0.0
     * @author: dengyi5
     * @date: 2017-12-6
     */
    public void modifyExecTime(IData param) throws Exception{
    	String commandCode = param.getString("COMMANDCODE");
    	if (logger.isDebugEnabled())
        {
            logger.debug("===========modifyExecTime======param=" + param);
        }
    	if(NpConst.AUTHCODE_RESP.equals(commandCode)){
    		String messageId = param.getString("ORIGMESSAGEID");// 请求消息ID  //MESSAGEID查不到数据@panyu5
    		IDataset ids = TradeNpQry.getTradeNpsByMsgId(messageId);
    		
    		//容错处理，针对没有收到ACK直接收到响应的流程处理 add by dengyi5
    		if (IDataUtil.isEmpty(ids))
            {
            	IDataset commparas = CommparaInfoQry.getCommParas("CSM", "173", "0", commandCode, "0898");
                if (IDataUtil.isNotEmpty(commparas))
                {
                    for (int i = 0, len = commparas.size(); i < len; i++)
                    {
                        IData tdata = commparas.getData(i);
                        String tradeTypeCode = tdata.getString("PARA_CODE2");
                        ids = TradeNpQry.getTradeNpBySnTradeTypeCode(param.getString("NPCODE"), tradeTypeCode);
                        if (IDataUtil.isNotEmpty(ids))
                        {
                            break;
                        }
                    }
                }
            }
    		
    		if(DataUtils.isNotEmpty(ids)) {
                String tradeId = ids.getData(0).getString("TRADE_ID");
                // 2.通过获取NP台帐信息内的TRADE_ID查询主台帐信息
                IDataset tradeInfos = TradeInfoQry.getTradeInfobyTradeId(tradeId);
                if (IDataUtil.isEmpty(tradeInfos) || tradeInfos.size() > 1) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据TRADE_ID=【" + tradeId + "】查询主台帐表异常！");
                }
                IData updateInput = new DataMap();
                String orderId = tradeInfos.getData(0).getString("ORDER_ID");

                updateInput.clear();
                updateInput.put("ORDER_ID", orderId);// order的执行时间也得修改，老系统没有这个
                int flag = Dao.executeUpdateByCodeCode("TF_B_ORDER", "UPD_EXECTIME_BY_ID", updateInput,Route.getJourDb(BizRoute.getRouteId()));
                if (flag != 1) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "携出中生效：修改【TF_B_ORDER】执行时间异常！");
                }

                updateInput.clear();
                updateInput.put("TRADE_ID", tradeId);
                flag = Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_EXECTIME_BY_TID", updateInput,Route.getJourDb(BizRoute.getRouteId()));
                if (flag != 1) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "携出生效：修改【TF_B_TRADE】执行时间异常");
                }
            }
        }
    }
    /**
     * @Function: ActNotifyInsertSaleLog
     * @Description: 生效告知时插营销告警表
     * @param param
     * @throws Exception
     * @version: v1.0.0
     * @author: wangbo3
     * @date: 2015-4-3 下午8:37:56
     */
    public void sendNpAlertSms(IData param) throws Exception
    {

        // 先查询需要发送的号码
        String strTradeEparchyCode = param.getString("EPARCHY_CODE");
        String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
        IDataset commParas = CommparaInfoQry.getCommPkInfo("CSM", "2011", "HAIN", strTradeEparchyCode);
        if (IDataUtil.isNotEmpty(commParas))
        {
            String tradeType = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
            String levelCode = param.getString("LEVEL_CODE", "0");// 0-普通告警 1-严重告警
            String messageType = param.getString("MESSAGETYPE");
            String strLevelContent = "普通告警";
            String strSMSContent = "";
            if ("1".equals(levelCode))
            {
                strLevelContent = "严重告警";
            }
            if ("2".equals(messageType))// 2-请求返回
            {
                if (param.containsKey("TRADEID"))
                {
                    strSMSContent = "携号转网业务：" + tradeType + "，工单号：" + param.getString("TRADEID") + " 出现" + strLevelContent + "，请您尽快处理！" + SysDateMgr.getSysDate();
                }
                else
                {
                    strSMSContent = "携号转网业务：接收方ACK失败，FLOWID：" + param.getString("FLOWID") + " 出现" + strLevelContent + "，请您尽快处理！" + SysDateMgr.getSysDate();
                }

            }

            if (StringUtils.isNotBlank(strSMSContent))
            {
                for (int i = 0, len = commParas.size(); i < len; i++)
                {
                    IData data = commParas.getData(i);
                    data.put("RECV_OBJECT", data.getString("PARA_CODE1"));
                    data.put("REMARK", data.getString("携号转网人工干预"));
                    data.put("NOTICE_CONTENT", strSMSContent);
                    SmsSend.insSms(data);
                    // 发送短信
                }
            }

        }
    }
    /**
     * @Function: getActNotifyCode
     * @Description: 判断生效告知的业务类型
     * @param param
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-21 上午10:21:55
     */
    public void getActNotifyCode(IData param) throws Exception
    {
        String commAndCode = param.getString("COMMANDCODE");
        String serialNumber = param.getString("NPCODE");
        String flowId = param.getString("FLOWID");
        String messageId = param.getString("MESSAGEID");
        if (!NpConst.ACT_NOTIFY.equals(commAndCode))
        {
            throwsException("115006", "获取COMMANDCODE=【" + commAndCode + "】错误，应该为【ACT_NOTIFY】！");
        }
        
        if (StringUtils.isBlank(serialNumber))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "参数【NPCODE】不能为空！");
        }

        // 不能改成从UCa查，41工单完工后，立马 SOA 发启42 走uca取不到usertagset=3
        IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
        // 由于部分号码41工单完工后，usertagset状态不改变，再从NP表中取一遍
        IDataset userNpInfo = UserNpInfoQry.qryUserNpInfosBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            String userTagSet = userInfo.getData(0).getString("USER_TAG_SET", "").substring(0, 1);
            if(IDataUtil.isNotEmpty(userNpInfo)){
            String npTag = userNpInfo.getData(0).getString("NP_TAG","").substring(0, 1);
            if ("3".equals(userTagSet)||"3".equals(npTag))
            {
                param.put("SERIAL_NUMBER", serialNumber);
                param.put("USER_ID", userInfo.getData(0).getString("USER_ID"));
                param.put("TRADE_CITY_CODE", userInfo.getData(0).getString("CITY_CODE"));
                param.put("TRADE_DEPART_ID", "SOA");
                param.put("TRADE_STAFF_ID", "SOA");
                param.put("NP_SERVICE_TYPE", param.getString("SERVICETYPE"));
                param.put("TRADE_TYPE_CODE", "42");
                param.put("FLOW_ID", flowId);// 这个有点多余，先按老系统
                param.put("MESSAGE_ID", messageId);// 这个有点多余，先按老系统
            }
            else
            {
                // CSAppException.apperr(CrmUserNpException.CRM_USER_NP_115008, serialNumber);
                throwsException("115008", "该手机号【" + serialNumber + "】用户携转标识异常！");
            }
            }
        }
        else
        {
            param.put("FLOW_ID", flowId);// 这个有点多余，先按老系统
            param.put("MESSAGE_ID", messageId);// 这个有点多余，先按老系统
            param.put("TRADE_TYPE_CODE", "40");
       
        }
    }
    /**
     * @Function: modifyNpState
     * @Description: 修改TRADE_NP表状态
     * @param param
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-19 下午8:37:56
     */
    public void modifyNpState(IData param) throws Exception
    {

        param.put("TRADE_STAFF_ID", "SOA");// U-第三方接口
        param.put("IN_MODE_CODE", "U");
        param.put("RES_TAG", "0");// 资源修改标识 1-修改 0-不修改
        param.put("ALERT_TYPE_CODE", "0");// 1-发送预警短信 0-不发
        String responscode = param.getString("RESPONSECODE");
        
        if( responscode.equals("101")) 
    	{
        	param.put("RESPONSEMESSAGE", "重复的messageId");
    	} 
        else if (responscode.equals("500"))
    	{
    		param.put("RESPONSEMESSAGE", "接收方服务器异常");
    	} 
        else if (responscode.equals("699"))
    	{
    		//param.put("RESPONSEMESSAGE", "系统后台服务执行失败");
    	} 
        else if (responscode.equals("601"))
    	{
    		param.put("RESPONSEMESSAGE", "CSMS拒绝请求：一号多转");
    	} 
        else if (responscode.equals("405"))
    	{
    		param.put("RESPONSEMESSAGE", "无法识别发送方身份，或者发送方身份不正确（例如携入方发起欠费流程流程），或者发送方IP地址与消息发起方不对应");
    	}


        String messageType = param.getString("MESSAGETYPE");

        if ("2".equals(messageType))
        {
            // 请求返回
            ackMethod(param);
        }
        else if ("0".equals(messageType))
        {
            // 响应返回
            responseMethod(param);
        }
        else if ("1".equals(messageType))
        {
            // 告知
            notifyMethod(param);
        }
    }

  /*  public void ActNotifyInsertSaleLog(IData param) throws Exception
    {

    	String commandCode = param.getString("COMMANDCODE");
        
    	if(NpConst.APPLY_RESP.equals(commandCode) || NpConst.APPLY_NOTIFY.equals(commandCode) || NpConst.ACT_RESP.equals(commandCode) || NpConst.ACT_RESP_NEW.equals(commandCode) || NpConst.ACT_NOTIFY.equals(commandCode) || NpConst.ACT_NOTIFY_NEW.equals(commandCode))
    	{
    		String dealMethod  = param.getString("DEALMETHOD");//处理方式
    		String serialnumber  = param.getString("NPCODE", "");//号码取得
    		
    		if(!"".equals(serialnumber))
    		{
    			String errorcode  = param.getString("RESPONSECODE");
    			String errormessage  = param.getString("RESPONSEMESSAGE");
    			
    			String strNpFlag = "";
    			String strBusiType = "";
    			
    			if((NpConst.ACT_NOTIFY.equals(commandCode) || NpConst.ACT_NOTIFY_NEW.equals(commandCode)) && "0".equals(dealMethod))
    			{
    				strNpFlag = "1";
    				strBusiType = "3";
    			}
    			else if("1".equals(dealMethod) || "3".equals(dealMethod))
    			{
    				strNpFlag = "0";
    				strBusiType = "2";
    			}
    			else
    			{
    				return ;
    			}
    			
    			//以下与上一方法重复，多余的
//    			if( errorcode == "101") 
//    			{
//    				errormessage = "重复的messageId";
//    			} else if (errorcode == "500"){
//    				errormessage = "接收方服务器异常";
//    			} else if (errorcode == "699"){
//    				//errormessage = "系统后台服务执行失败";
//    			} else if (errorcode == "601"){
//    				errormessage = "CSMS拒绝请求：一号多转";
//    			} else if (errorcode == "405"){
//    				errormessage = "无法识别发送方身份，或者发送方身份不正确（例如携入方发起欠费流程流程），或者发送方IP地址与消息发起方不对应";
//    			}
    			
    			String npJobId = SeqMgr.getNpJobId();
    			IDataset ids = TradeNpQry.queryNpApplyInBySN(serialnumber);
    			if(IDataUtil.isEmpty(ids))
    			{
    				//CSAppException.appError("325659", "查询携出申请NP子台账台帐无记录!");mod by wangdelong 错误码改造
                	CSAppException.apperr(CrmUserNpException.CRM_USER_NP_325659);
    				
    			}
    			
    			//获取原运营商
    		  	String portOutNetId = ids.getData(0).getString("PORT_OUT_NETID","");        //获取携出方网络ID
    		  	if(!"".equals(portOutNetId))
    		  	{
    		  		portOutNetId = portOutNetId.substring(0,3);
    		  	}
    		  	
    		  	// 获取部门id
    		  	String tradeDepartId = "";    			
    			if ("1".equals(dealMethod)) 
    			{
    				IDataset tradeBhInfos = TradeBhQry.queryTradeInfoByPK(param.getString("TRADEID"),"1");
    				
    				if(IDataUtil.isNotEmpty(tradeBhInfos))
    				{
    					tradeDepartId = tradeBhInfos.getData(0).getString("TRADE_DEPART_ID");    					
    				}       				
    			} 
    			else if ("3".equals(dealMethod) || "0".equals(dealMethod)) 
    			{
    				IData params = new DataMap();
    		        params.put("TRADE_ID", param.getString("TRADEID"));
    		        params.put("CANCEL_TAG", "0");
    				IDataset tradeInfos = TradeInfoQry.queryTradeInfoByPK(params);
    				
    				if(IDataUtil.isNotEmpty(tradeInfos))
    				{
    					tradeDepartId = tradeInfos.getData(0).getString("TRADE_DEPART_ID");    					
    				}     				
    			}
    			
    			if (!"".equals(tradeDepartId)) {
    				IDataset departInfo = DepartInfoQry.getDepartById(tradeDepartId);
    			  	
    				if(IDataUtil.isNotEmpty(departInfo))
    				{
	    				String departFrame = departInfo.getData(0).getString("DEPART_FRAME","");
	    				
	    				tradeDepartId = departFrame.substring(5,10);
    				}
    			}
    			
    			IData smNpJobInfo = new DataMap();
    			smNpJobInfo.put("NP_JOB_ID", npJobId);
    		  	smNpJobInfo.put("CUST_NAME", ids.getData(0).getString("CUST_NAME"));
    		  	smNpJobInfo.put("SERIAL_NUMBER", serialnumber);
    		  	smNpJobInfo.put("USER_ID", ids.getData(0).getString("USER_ID"));
    		  	smNpJobInfo.put("CON_PHONE", ids.getData(0).getString("PHONE"));
    		  	smNpJobInfo.put("CONSU_PHONE", "");
    		  	smNpJobInfo.put("NP_FLAG", strNpFlag);
    		  	smNpJobInfo.put("BUSI_TYPE", strBusiType);
    		  	smNpJobInfo.put("TO_SERVICE", portOutNetId);
    		  	smNpJobInfo.put("NP_REASON", "");
    		  	smNpJobInfo.put("NP_LEVEL", "");
    		  	smNpJobInfo.put("CITY_CODE", "");    		 
    		  	smNpJobInfo.put("DEPART_ID", tradeDepartId);    		  	
    		  	smNpJobInfo.put("CREATE_STAFF", "");    		 	
    		  	smNpJobInfo.put("NP_JOBREMARK", "");
    		  	smNpJobInfo.put("DEAL_STATUS", "0");
    		  	smNpJobInfo.put("PRE_VALUE1", "");
    		  	smNpJobInfo.put("PRE_VALUE2", errormessage);
    		  	smNpJobInfo.put("PRE_VALUE3", tradeDepartId);
    		  	smNpJobInfo.put("PRE_VALUE4", "");
    		  	smNpJobInfo.put("PRE_VALUE5", "");
    		  	smNpJobInfo.put("PRE_VALUE6", "");
    		  	smNpJobInfo.put("PRE_VALUE7", "");
    		  	smNpJobInfo.put("PRE_VALUE8", "");
    		  	smNpJobInfo.put("PRE_VALUE9", "");
    		  	smNpJobInfo.put("PRE_VALUE10", "");
    		  	smNpJobInfo.put("PRE_VALUE11", "");
    			smNpJobInfo.put("PRE_VALUE12", "");
    			smNpJobInfo.put("PRE_VALUE13", "");
    			smNpJobInfo.put("PRE_VALUE14", "");
    			smNpJobInfo.put("PRE_VALUE15", "");
    			
    			if ("3".equals(dealMethod))
    			{
    				smNpJobInfo.put("PRE_VALUE16", "610");
    			}
    			else
    			{
    				smNpJobInfo.put("PRE_VALUE16", "");
    			}

    			smNpJobInfo.put("PRE_VALUE17", "");
    			smNpJobInfo.put("PRE_VALUE18", "");
    			smNpJobInfo.put("PRE_VALUE19", "");
    			smNpJobInfo.put("PRE_VALUE20", "");
    			smNpJobInfo.put("PRE_VALUE21", "");
    			smNpJobInfo.put("PRE_VALUE22", "");
    			smNpJobInfo.put("PRE_VALUE23", "");
    			smNpJobInfo.put("PRE_VALUE24", "");
    			smNpJobInfo.put("PRE_VALUE25", "");
    			smNpJobInfo.put("PRE_VALUE26", "");
    			smNpJobInfo.put("PRE_VALUE27", "");
    			smNpJobInfo.put("PRE_VALUE28", "");
    			smNpJobInfo.put("PRE_VALUE29", "");
    			smNpJobInfo.put("PRE_VALUE30", "");
                
    			Dao.executeUpdateByCodeCode("TF_SM_NP_JOB", "INS_SM_NP_JOB", smNpJobInfo);
    		}
    	}
        
        
    }
    */
    
    /**
     * @Function: modifyState
     * @Description: 封装响应、告知时状态判断
     * @param dealMethod
     * @param messageType
     * @param commandCode
     * @param tradeTypeCode
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-21 上午10:23:48
     */
    public String modifyState(String dealMethod, String messageType, String commandCode, String tradeTypeCode) throws Exception
    {
        // String dealMethod = param.getString("DEALMETHOD");
        // String messageType = param.getString("MESSAGETYPE");
        // String commandCode = param.getString("COMMANDCODE");
        String state = "";
        // String tradeTypeCode = param.getString("TRADE_TYPE_CODE");

        if ("0".equals(dealMethod))
        {
            if ("7240".equals(tradeTypeCode) || "7220".equals(tradeTypeCode) || "7301".equals(tradeTypeCode) || "7230".equals(tradeTypeCode) || "1503".equals(tradeTypeCode) || "192".equals(tradeTypeCode) || "191".equals(tradeTypeCode)
                    || "49".equals(tradeTypeCode) || "48".equals(tradeTypeCode) || "46".equals(tradeTypeCode) || "44".equals(tradeTypeCode))
            {// 发送方
                if ("0".equals(messageType))
                {// 0-响应
                    state = "020";// 发起方响应成功
                }
                else if ("1".equals(messageType))// 1-告知
                {
                    state = "030";// 发起方告知成功
                }
            }
            else if ("39".equals(tradeTypeCode) || "40".equals(tradeTypeCode))
            {
                if ("0".equals(messageType))
                {
                    if (NpConst.ACT_RESP.equals(commandCode))// ACT_RESP-生效响应
                    {
                        state = "050";
                    }
                    else if (NpConst.APPLY_RESP.equals(commandCode))// APPLY_RESP-申请响应
                    {
                        state = "020";
                    }
                    else if (NpConst.ACT_RESP_NEW.equals(commandCode))// ACT_RESP_NEW-授权码生效响应
                    {
                        state = "050";
                    }
                    else
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_196, commandCode);
                        throwsException("196","SOA参数COMMANDCODE=【"+commandCode+"】取值异常！");
                    }
                }
                else if ("1".equals(messageType))
                {
                    if (NpConst.ACT_NOTIFY.equals(commandCode))// ACT_NOTIFY-生效结果告知
                    {
                        state = "060";
                    }
                    else if (NpConst.APPLY_NOTIFY.equals(commandCode))// APPLY_NOTIFY-申请结果告知
                    {
                        state = "040";
                    }
                    else if (NpConst.ACT_NOTIFY_NEW.equals(commandCode))// ACT_NOTIFY_NEW-授权码生效结果告知
                    {
                        state = "060";
                    }
                    else
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_196, commandCode);
                        throwsException("196","SOA参数COMMANDCODE=【"+commandCode+"】取值异常！");
                    }
                }
            }
            else if ("42".equals(tradeTypeCode) && "1".equals(messageType))
            {
                state = "100";
            }
            else
            // 接收方
            {
                if ("0".equals(messageType))// 0-响应
                {
                    state = "120";// 接收方响应成功
                }
                else if ("1".equals(messageType))// 1-告知
                {
                    state = "130";// 接收方告知成功
                }
            }
        }
        else
        {
            if ("7240".equals(tradeTypeCode) || "7220".equals(tradeTypeCode) || "7301".equals(tradeTypeCode) || "7230".equals(tradeTypeCode) || "1503".equals(tradeTypeCode) || "192".equals(tradeTypeCode) || "191".equals(tradeTypeCode)
                    || "49".equals(tradeTypeCode) || "48".equals(tradeTypeCode) || "46".equals(tradeTypeCode) || "44".equals(tradeTypeCode))// 发送方
            {
                if ("0".equals(messageType))// 0-响应
                {
                    state = "021";// 发起方响应失败
                }
                else if ("1".equals(messageType))// 1-告知
                {
                    state = "031";// 发起方告知失败
                    if ("2".equals(dealMethod) && ("1503".equals(tradeTypeCode) || "49".equals(tradeTypeCode) || "48".equals(tradeTypeCode) || "46".equals(tradeTypeCode) || "44".equals(tradeTypeCode)))// 待时重发
                    // add
                    // by
                    // wangjx
                    // 2010-5-5
                    {
                        state = "000";
                    }
                }
            }
            else if ("40".equals(tradeTypeCode) || "39".equals(tradeTypeCode))
            {
                if ("0".equals(messageType))// 0-响应
                {
                    if (NpConst.ACT_RESP.equals(commandCode))// ACT_RESP-生效响应
                    {
                        state = "051";// 发起方生效响应失败
                    }
                    else if (NpConst.APPLY_RESP.equals(commandCode))// APPLY_RESP-申请响应
                    {
                        state = "021";// 发起方响应失败
                    }
                    else if (NpConst.ACT_RESP_NEW.equals(commandCode))// ACT_RESP_NEW-授权码生效响应
                    {
                        state = "051";// 发起方生效响应失败
                    }
                    else
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_196, commandCode);
                        throwsException("196","SOA参数COMMANDCODE=【"+commandCode+"】取值异常！");
                    }
                }
                else if ("1".equals(messageType))// 1-告知
                {
                    if (NpConst.ACT_NOTIFY.equals(commandCode))// ACT_NOTIFY-生效结果告知
                    {
                        state = "061";// 发起方生效告知失败
                        if ("2".equals(dealMethod))// 待时重发 add by wangjx
                        // 2010-5-5
                        {
                            state = "040";
                        }
                    }
                    else if (NpConst.APPLY_NOTIFY.equals(commandCode))// APPLY_NOTIFY-申请结果告知
                    {
                        state = "031";// 发起方告知失败
                        if ("2".equals(dealMethod))// 待时重发 add by wangjx
                        // 2010-5-5
                        {
                            state = "000";
                        }
                    }
                    else
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_196, commandCode);
                        throwsException("196","SOA参数COMMANDCODE=【"+commandCode+"】取值异常！");
                    }
                }
            }
            else
            // 接收方
            {
                if ("0".equals(messageType))// 0-响应
                {
                    state = "121";// 发起方响应失败
                }
                else if ("1".equals(messageType))// 1-告知
                {
                    state = "131";// 发起方告知失败
                }
            }
        }

        return state;
    }

    /**
     * @Function: notifyMethod
     * @Description: 申请告知：soa接收总部告知返回，调用TCS_ModifyNpState接口进行处理。根据MESSAGETYPE=“1”
     * @param param
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-21 上午10:23:23
     */
    public void notifyMethod(IData param) throws Exception
    {

        String dealMethod = param.getString("DEALMETHOD");// 处理方式
        String flowId = param.getString("FLOWID");
        String serialNumber = param.getString("NPCODE");
        String commAndCode = param.getString("COMMANDCODE");
        String messageType = param.getString("MESSAGETYPE");
        String resTag = "";

        IDataset npbuf = TradeNpQry.getTradeNpByFlowId(flowId);
 if (IDataUtil.isEmpty(npbuf))
        {
            IDataset commparas = CommparaInfoQry.getCommParas("CSM", "173", "0", commAndCode, "0898");
            if (IDataUtil.isNotEmpty(commparas))
            {
                for (int i = 0, len = commparas.size(); i < len; i++)
                {
                    IData tdata = commparas.getData(i);
                    String tradeTypeCode = tdata.getString("PARA_CODE2");
                    npbuf = TradeNpQry.getTradeNpBySnTradeTypeCode(serialNumber, tradeTypeCode);
                    if (IDataUtil.isNotEmpty(npbuf))
                    {
                        break;
                    }
                }
            }
        }

        if (IDataUtil.isEmpty(npbuf))
        {
            //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125101);
            throwsException("125101","携号转网：响应返回时根据消息ID查询NP台帐表异常！");
        }

        String tradeTypeCode = "", tradeId = "";
        if (npbuf.size() == 1)
        {
            tradeTypeCode = npbuf.getData(0).getString("TRADE_TYPE_CODE");
            tradeId = npbuf.getData(0).getString("TRADE_ID");
            if ((NpConst.SUSPEND_NOTIFY.equals(commAndCode) && "46".equals(tradeTypeCode)) || (NpConst.APPLY_NOTIFY.equals(commAndCode) || NpConst.ACT_NOTIFY.equals(commAndCode)) && "1503".equals(tradeTypeCode))
            {
                return;
            }
        }
        else if (npbuf.size() == 2)
        {// 携出欠费停机（携入方），然后开机，TF_B_TRADE_NP有两条FLOW_ID相同的情况
            IData temp = new DataMap();
            for (int i = 0, len = npbuf.size(); i < len; i++)
            {
                temp.put(npbuf.getData(i).getString("TRADE_TYPE_CODE").trim(), npbuf.getData(i));
            }

            if (temp.containsKey("46"))
            {
                IDataset ids = TradeNpQry.getTradeNpByTradeId(temp.getData("46").getString("TRADE_ID"));
                if (IDataUtil.isEmpty(ids) || ids.size() > 1)
                {
                    //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125209,"" );
                    throwsException("125209","告知返回-根据TRADE_ID=【"+temp.getData("46").getString("TRADE_ID")+"】查询NP台帐表异常！");
                }
            }
            // 44携转欠费停机(携入方) 46携转欠费开机(携入方)
            if (!(temp.containsKey("44") && temp.containsKey("46")))
            {
               // CSAppException.apperr(CrmUserNpException.CRM_USER_NP_124210, flowId);
                throwsException("124210","告知返回成功：根据FLOWID=【"+flowId+"】查询NP台帐表异常！");
            }

        }
        else
        {
            //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125211, flowId);
            throwsException("125211","告知返回-根据FLOWID=【"+flowId+"】查询NP台帐表异常！");
        }

        // 特殊处理：携出生效(42)如果生成工单前CSMS返回失败，不做事情，等待CSMS发起携出申请取消
        if (!"0".equals(dealMethod) && "41".equals(tradeTypeCode))
        {
            return;
        }
        String subscribeState="";
        // 2.通过获取NP台帐信息内的TRADE_ID查询主台帐信息
        IDataset tradeInfos = TradeInfoQry.getTradeInfobyTradeId(tradeId);
        if (IDataUtil.isEmpty(tradeInfos) || tradeInfos.size() > 1)
        {
            //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125112, tradeId);
            throwsException("125112","告知返回-根据TRADE_ID=【"+tradeId+"】查询主台帐表异常！");
        }else{
        	subscribeState=tradeInfos.getData(0).getString("SUBSCRIBE_STATE");
        }

        String state = modifyState(dealMethod, messageType, commAndCode, tradeTypeCode);
        IData inparam = new DataMap();
        inparam.clear();
        inparam.put("STATE", state);
        inparam.put("MESSAGE_ID_NEW", param.getString("MESSAGEID"));
        inparam.put("FLOW_ID", param.getString("FLOWID"));
        inparam.put("MSG_CMD_CODE", param.getString("COMMANDCODE"));
        inparam.put("RESULT_CODE", param.getString("RESPONSECODE"));
        inparam.put("RESULT_MESSAGE", param.getString("RESPONSEMESSAGE", "携号转网"));
        inparam.put("REMARK", param.getString("REMARK", "携号转网"));
        inparam.put("TRADE_ID", tradeId);

        if (IDataUtil.isNotEmpty(tradeInfos) && tradeInfos.size() == 1 && "0".equals(tradeInfos.getData(0).getString("CANCEL_TAG")))
        {
            String orderId = tradeInfos.getData(0).getString("ORDER_ID");
            if ("0".equals(dealMethod))
            {// 告知返回成功
                if (("30".equals(tradeTypeCode) || "40".equals(tradeTypeCode)) && NpConst.APPLY_NOTIFY.equals(commAndCode))
                {
                    String limitTime = "79200";// 默认当天22点
                    IDataset commParas = CommparaInfoQry.getCommparaAllCol("CSM", "2012", param.getString("PROVINCE_CODE", "HAIN"), param.getString("EPARCHY_CODE", ""));
                    if (IDataUtil.isNotEmpty(commParas) && commParas.size() == 1)
                    {
                        limitTime = commParas.getData(0).getString("PARA_CODE1");
                    }
                    else
                    {
                        //CSAppException.apperr(CrmCommException.CRM_COMM_125112);
                        throwsException("125112","查询COMMPARA参数表异常，没有找到PARAM_ATTR=2012的配置参数！");
                    }

                    inparam.put("LIMIT_TIME", limitTime);
                    /**
                     * REQ201606160012 携号转网优化
                     * chenxy3 20160628
                     * */
                    int flag = 1;
                    if("40".equals(tradeTypeCode)){
                    	flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_EFFECT_BY_TID2", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    }else{
                        flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_EFFECT_BY_TID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    }
                    if (flag != 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125113);
                        throwsException("125113","告知申请返回成功：NP台帐时间修改异常！");

                    }
                }
                else if ("42".equals(tradeTypeCode))
                {
                    int flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_EFFCTTIME_BY_TID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    if (flag != 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125114);
                        throwsException("125114","告知返回成功-携出生效：NP台帐内容修改异常！");
                    }

                    inparam.clear();
                    inparam.put("ORDER_ID", orderId);// order的执行时间也得修改，老系统没有这个
                    flag = Dao.executeUpdateByCodeCode("TF_B_ORDER", "UPD_EXECTIME_BY_ID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    if (flag != 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125118);
                        throwsException("125118","告知返回成功-携出生效：修改【TF_B_ORDER】执行时间异常！");
                    }

                    inparam.clear();
                    inparam.put("TRADE_ID", tradeId);
                    flag = Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_EXECTIME_BY_TID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    if (flag != 1)
                    {
                       //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125115);
                        throwsException("125115","告知返回成功-携出生效：修改主台帐执行时间异常！");
                    }
                }
                else
                {

                    int flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_STATE_BY_TID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    if (flag != 1)
                    {
                       // CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125116);
                        throwsException("125116","告知返回成功：NP台帐内容修改异常！");
                    }

                    inparam.clear();
                    inparam.put("ORDER_ID", orderId);
                    flag = Dao.executeUpdateByCodeCode("TF_B_ORDER", "UPD_EXECTIME_BY_ID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    if (flag != 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125118);
                        throwsException("125118","告知返回成功-携出生效：修改【TF_B_ORDER】执行时间异常！");
                    }

                    inparam.clear();
                    inparam.put("TRADE_ID", tradeId);
                    flag = Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_EXECTIME_BY_TID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    if (flag != 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125117);
                        throwsException("125117","告知生效返回成功：修改主台帐执行时间异常！");
                    }
                    if("49".equals(tradeTypeCode)){
                    //TODO tanjl  如果为49的工单的情况下需要新增销户的工单，销户只登记主台账和资源台账，主要是用与发送销户短信清除掉NPHLR上面的数据
                    IData npData = new DataMap();
                    npData.put("SERIAL_NUMBER", 		tradeInfos.getData(0).getString("SERIAL_NUMBER"));
                    //下面几个参数基本上是获取对应的台账信息里面的数据，具体的用途不大，写这里主要是涉及到接口服务有可能需要。
            		npData.put("TRADE_EPARCHY_CODE", 	"0898");
            		npData.put("TRADE_STAFF_ID", 		tradeInfos.getData(0).getString("TRADE_STAFF_ID"));
            		npData.put("TRADE_DEPART_ID", 		tradeInfos.getData(0).getString("TRADE_DEPART_ID"));
            		npData.put("TRADE_CITY_CODE", 		tradeInfos.getData(0).getString("TRADE_CITY_CODE"));
                    CSAppCall.call("SS.InNpUserDestroySVC.tradeReg", npData);
                }
                }
            }
            else if ("1".equals(dealMethod))
            {// 告知返回失败，完全撤销
                // 1.修改携入申请取消工单为取消状态

                inparam.clear();
                inparam.put("TRADE_ID", tradeId);
                inparam.put("UPDATE_STAFF_ID", "SOA");
                inparam.put("CANCEL_TAG", "1");
                int flag = Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_CANCELTAG_BY_TID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                if (flag != 1)
                {
                    //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125201);
                    throwsException("125201","告知返回失败-完全撤销：修改携转申请撤销工单取消标识异常！工单状态["+subscribeState+"|"+tradeId+"]");
                }
                // 2.将该工单记录搬迁到历史台帐表
                inparam.clear();
                inparam.put("TRADE_ID", tradeId);
                inparam.put("CANCEL_STAFF_ID", "SOA");
                inparam.put("CANCEL_DEPART_ID", "SOA");
                inparam.put("CANCEL_TAG", "1");
                flag = Dao.executeUpdateByCodeCode("TF_BH_TRADE", "INS_BY_TRADE_CANCEL", inparam,Route.getJourDb(BizRoute.getRouteId()));
                if (flag != 1)
                {
                   // CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125202);
                    throwsException("125202","告知返回失败-完全撤销：将撤消后的工单搬迁到历史台帐表发生异常！");
                }
                // 3.将台帐表内该条记录删除
                inparam.clear();
                inparam.put("TRADE_ID", tradeId);
                flag = Dao.executeUpdateByCodeCode("TF_B_TRADE", "DEL_CANCEL_BOOK", inparam,Route.getJourDb(BizRoute.getRouteId()));
                if (flag != 1)
                {
                    //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125203);
                    throwsException("125203","告知返回失败-完全撤销：台帐表内记录删除发生异常！");
                }
                // 4.修改TRADE_NP表内该记录为取消状态
                inparam.clear();
                inparam.put("STATE", state);
                inparam.put("MESSAGE_ID_NEW", param.getString("MESSAGEID"));
                inparam.put("FLOW_ID", param.getString("FLOWID"));
                inparam.put("MSG_CMD_CODE", param.getString("COMMANDCODE"));
                inparam.put("RESULT_CODE", param.getString("RESPONSECODE"));
                inparam.put("RESULT_MESSAGE", param.getString("RESPONSEMESSAGE", "携号转网"));
                inparam.put("REMARK", param.getString("REMARK", "携号转网"));
                inparam.put("TRADE_ID", tradeId);
                inparam.put("CANCEL_TAG", "1");
                flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_CANCELTAG_BY_TID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                if (flag != 1)
                {
                    //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125204);
                    throwsException("125204","告知返回失败-完全撤销：修改携转申请撤销NP台帐表取消标识异常！");
                }

                if ("1503".equals(tradeTypeCode))
                {
                    // 1.因为1503和39||40同一个FLOWID，所以通过FLOWID查询39||40的已撤销工单号 modi
                    // by wangjx 2010-5-18 不再通过历史表直接查询
                    IDataset npHisBuf = TradeNpQry.getTradeNp(flowId, "1", tradeTypeCode);
                    if (npHisBuf.size() != 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125405, flowId);
                        throwsException("125405","携入申请取消-完全撤销：查询原携转申请工单异常，FLOWID=【"+flowId+"】！");
                    }
                    // 2.修改历史台帐表内原携入申请开户工单为正常工单
                    inparam.clear();
                    inparam.put("TRADE_ID", npHisBuf.getData(0).getString("TRADE_ID"));
                    inparam.put("CANCEL_TAG", "0");
                    inparam.put("UPDATE_STAFF_ID", "SOA");
                    flag = Dao.executeUpdateByCodeCode("TF_BH_TRADE", "UPD_NORMAL_TRADE_BY_TID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    if (flag != 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125406, npHisBuf.getData(0).getString("TRADE_ID"));
                        throwsException("125406","携入申请取消-完全撤销：修改历史台帐表内原携入申请开户工单发生异常，TRADE_ID=【"+npHisBuf.getData(0).getString("TRADE_ID")+"】！");
                    }
                    // 3.将开户工单由历史表搬到台帐主表
                    inparam.clear();
                    inparam.put("TRADE_ID", npHisBuf.getData(0).getString("TRADE_ID"));
                    inparam.put("CANCEL_TAG", "0");
                    inparam.put("UPDATE_STAFF_ID", "SOA");
                    flag = Dao.executeUpdateByCodeCode("TF_B_TRADE", "INS_BY_BHTRADE", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    if (flag != 1)
                    {
                       // CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125407, npHisBuf.getData(0).getString("TRADE_ID"));
                        throwsException("125407","携入申请取消-完全撤销：将开户工单由历史表搬到台帐主表发生异常，TRADE_ID=【"+npHisBuf.getData(0).getString("TRADE_ID")+"】！");

                    }
                    // 4.删除历史台帐表内该条数据
                    inparam.clear();
                    inparam.put("TRADE_ID", npHisBuf.getData(0).getString("TRADE_ID"));
                    flag = Dao.executeUpdateByCodeCode("TF_BH_TRADE", "DEL_BY_TRADEID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    if (flag != 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125408, npHisBuf.getData(0).getString("TRADE_ID"));
                        throwsException("125408","携入申请取消-完全撤销：删除历史台帐表内该条工单发生异常，TRADE_ID=【"+npHisBuf.getData(0).getString("TRADE_ID")+"】！");

                    }
                    // 5.修改NP子台帐表为正常工单
                    inparam.clear();
                    inparam.put("CANCEL_TAG", "0");
                    inparam.put("TRADE_ID", npHisBuf.getData(0).getString("TRADE_ID"));
                    flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_CANCELTAG_BY_ID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    if (flag != 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125409, npHisBuf.getData(0).getString("TRADE_ID"));
                        throwsException("125409","携转申请取消-完全撤销：修改NP子台帐表工单异常，TRADE_ID=【"+npHisBuf.getData(0).getString("TRADE_ID")+"】！");
                    }
                }
                else if ("39".equals(tradeTypeCode) || "40".equals(tradeTypeCode))
                {
                    resTag = "1";// 资源回收标识
                    param.put("RES_TAG", resTag);
                    param.put("TRADE_CITY_CODE", tradeInfos.getData(0).getString("TRADE_CITY_CODE"));
                	param.put("TRADE_DEPART_ID", tradeInfos.getData(0).getString("TRADE_DEPART_ID"));
                	param.put("TRADE_ID", tradeInfos.getData(0).getString("TRADE_ID"));
                	param.put("BRAND_CODE", tradeInfos.getData(0).getString("BRAND_CODE"));
                	param.put("SERIAL_NUMBER", tradeInfos.getData(0).getString("SERIAL_NUMBER"));
                }
                if("40".equals(tradeTypeCode))
                {
					inparam.clear();
					inparam.put("TRADE_ID", tradeId);
					flag = Dao.executeUpdateByCodeCode("TF_BH_TRADE_STAFF", "INSERT_FROM_BH_TRADE", inparam);
					
					inparam.clear();
					inparam.put("TRADE_ID", tradeId);
					inparam.put("SUBSCRIBE_STATE", "A");
					flag = Dao.executeUpdateByCodeCode("TF_BH_TRADE_STAFF", "UPD_BY_TID", inparam);
                }
            }
            else if ("2".equals(dealMethod))
            {// 告知返回失败，待时重发
                String retryMethod = param.getString("RETRYMETHOD");
                if ("42".equals(tradeTypeCode) && NpConst.ACT_NOTIFY.equals(commAndCode) && "620".equals(param.getString("RESPONSECODE")))
                {
                    return;
                }
                else
                {
                    inparam.clear();
                    inparam.put("TRADE_ID", tradeId);
                    inparam.put("RESULT_CODE", param.getString("RESPONSECODE"));
                    inparam.put("RESULT_MESSAGE", param.getString("RESPONSEMESSAGE", "携号转网"));
                    inparam.put("MSG_CMD_CODE", param.getString("COMMANDCODE"));
                    inparam.put("REMARK", param.getString("REMARK", "携号转网"));
                    inparam.put("RETRY_TIME", param.getString("RETRY_TIME"));
                    inparam.put("STATE", state);
                    int flag = 0;
                    if ("0".equals(retryMethod))
                    {// 绝对时间
                        flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_STATE_BY_RM0", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    }
                    else
                    {
                        flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_STATE_BY_RM1", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    }
                    if (flag != 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125108);
                        throwsException("125108","告知返回失败-待时重发：修改NP子台帐表工单异常！");
                    }
                }
            }
            else if ("3".equals(dealMethod))
            {// //告知返回失败，人工干预

                inparam.clear();
                inparam.put("TRADE_ID", tradeId);
                inparam.put("RESULT_CODE", param.getString("RESPONSECODE"));
                inparam.put("RESULT_MESSAGE", param.getString("RESPONSEMESSAGE", "携号转网"));
                inparam.put("MSG_CMD_CODE", param.getString("COMMANDCODE"));
                inparam.put("REMARK", param.getString("REMARK", "携号转网"));
                inparam.put("RETRY_TIME", param.getString("RETRY_TIME"));
                inparam.put("STATE", state);

                int flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_STATE_BY_ID", inparam,Route.getJourDb(BizRoute.getRouteId()));

                if (flag != 1)
                {
                    //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125109);
                    throwsException("125109","告知返回失败-人工干预：修改NP子台帐表工单异常！");
                }
                param.put("ACCT_ID", tradeInfos.getData(0).getString("ACCT_ID"));
                param.put("CUST_ID", tradeInfos.getData(0).getString("CUST_ID"));
                param.put("ORDER_ID", tradeInfos.getData(0).getString("ORDER_ID"));
                param.put("ALERT_TYPE_CODE", "1");// 是否人工干预
            }
        }

    }

    /**
     * @Function: repeatedTrade
     * @Description: 重复工单判断
     * @param commAndCode
     * @param serialNumber
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-21 上午10:22:11
     */
    public boolean repeatedTrade(String serialNumber) throws Exception
    {

        IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);// 不能改成uca
        IDataset userNpInfo = UserNpInfoQry.qryUserNpInfosBySn(serialNumber);
        boolean isExist = false;
        if (IDataUtil.isEmpty(userInfo))
        {
            throwsException("116001", "根据手机号【" + serialNumber + "】查询用户资料异常！");
        }
        else
        {
            String userTagSet = userInfo.getData(0).getString("USER_TAG_SET").substring(0, 1);
            String npTag = userNpInfo.getData(0).getString("NP_TAG","").substring(0, 1);
            if ("3".equals(userTagSet)||"3".equals(npTag))
            {
                IDataset ids = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("42", userInfo.getData(0).getString("USER_ID"), "0");
                if (IDataUtil.isNotEmpty(ids))
                {
                    isExist = true;
                }
            }
            else
            {
                throwsException("116002", "该手机号【" + serialNumber + "】用户携转标识异常！");
            }

        }

        return isExist;
    }

    /**
     * @Function: responseMethod
     * @Description: :申请响应：soa接收总部响应返回，调用TCS_ModifyNpState接口进行处理。根据MESSAGETYPE=“0 ”，
     * @param param 
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-21 上午10:23:05
     */
    public void responseMethod(IData param) throws Exception
    {

        String origMessageId = param.getString("ORIGMESSAGEID");// 请求消息ID
        String commAndCode = param.getString("COMMANDCODE");
        String serialNumber = param.getString("NPCODE");
        String dealMethod = param.getString("DEALMETHOD");
        String responseCode = param.getString("RESPONSECODE");
        String responseMessage = param.getString("RESPONSEMESSAGE", "携号转网");
        String msgCmdCode = commAndCode;
        String remark = param.getString("REMARK", "携号转网");
        String flowId = param.getString("FLOWID");
        String messageIdNew = param.getString("MESSAGEID");
        String messageType = param.getString("MESSAGETYPE");

        String resTag = "";// 资源回收标记

        IDataset ids = TradeNpQry.getTradeNpsByMsgId(origMessageId);// 这时候我们表面可能没有messageId 如查不到数据，再根据号码和commAndCode查

        if (IDataUtil.isEmpty(ids))
        {
        	IDataset commparas = CommparaInfoQry.getCommParas("CSM", "173", "0", commAndCode, "0898");
            if (IDataUtil.isNotEmpty(commparas))
            {
                for (int i = 0, len = commparas.size(); i < len; i++)
                {
                    IData tdata = commparas.getData(i);
                    String tradeTypeCode = tdata.getString("PARA_CODE2");
                    ids = TradeNpQry.getTradeNpBySnTradeTypeCode(serialNumber, tradeTypeCode);
                    if (IDataUtil.isNotEmpty(ids))
                    {
                        break;
                    }
                }
            }
        }

        if (IDataUtil.isEmpty(ids))
        {
            //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125101);
            throwsException("125101","携号转网：响应返回时根据消息ID查询NP台帐表异常！");
        }
        if (ids.size() == 1 && (NpConst.SUSPEND_RESP.equals(commAndCode) || NpConst.APPLY_RESP.equals(commAndCode) || NpConst.ACT_RESP.equals(commAndCode)) && !"0".equals(ids.getData(0).getString("CANCEL_TAG")))
        {
            return;
        }
        String tradeTypeCode = ids.getData(0).getString("TRADE_TYPE_CODE");
        String tradeId = ids.getData(0).getString("TRADE_ID");
        IDataset tradeInfos = TradeInfoQry.getTradeInfobyTradeId(tradeId);
        if (IDataUtil.isNotEmpty(tradeInfos) && tradeInfos.size() == 1 && "0".equals(tradeInfos.getData(0).getString("CANCEL_TAG"))
        	||(NpConst.AUTH_RESP.equals(commAndCode)))//查验流程不生成工单
        {

            String state = modifyState(dealMethod, messageType, commAndCode, tradeTypeCode);

            IDataset npInfos = TradeNpQry.getTradeNpByTradeId(tradeId);
            if (IDataUtil.isNotEmpty(npInfos))
            {
                String srcstate = npInfos.getData(0).getString("STATE", "").trim();
                if("040".equals(srcstate) && "020".equals(state)){
                    return;
                }
                
                if("060".equals(srcstate) && "050".equals(state)){
                    return;
                }
                //这个逻辑屏蔽
//                if (notifyState.indexOf(srcstate) > -1)
//                {
//                    return;// 如果trade_np表里状态已经是告知状态，再发起响应直接反回不做任何处理
//                }
            }

            IData inparam = new DataMap();
            if ("0".equals(dealMethod))
            {// 响应返回成功

                inparam.put("RESULT_CODE", responseCode);
                inparam.put("RESULT_MESSAGE", responseMessage);
                inparam.put("MSG_CMD_CODE", msgCmdCode);
                inparam.put("REMARK", remark);
                inparam.put("FLOW_ID", flowId);
                inparam.put("MESSAGE_ID_NEW", messageIdNew);
                inparam.put("TRADE_ID", tradeId);
                inparam.put("STATE", state);
            	int flag = 0;
                if(NpConst.AUTHCODE_RESP.equals(commAndCode)){//返回代码是200时,新增授权码有效期字段add by dengyi5                	
                	String smsInfo = "";
                
                	if("200".equals(responseCode)){                		
                		String authcode = IDataUtil.chkParam(param, "AUTHCODE");//授权码
                		String expired = IDataUtil.chkParam(param, "EXPIRED");//有效期
                		inparam.put("AUTH_CODE", authcode);
                		inparam.put("AUTH_CODE_EXPIRED", expired);
                		inparam.put("STATE", "120");
                		inparam.put("DEALMETHOD", dealMethod);
                		flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_STATE_BY_TID_AUTH", inparam);
                		smsInfo = "尊敬的用户您好！您申请的携出授权码为【"+authcode;
                		smsInfo += "】，有效期到【"+expired+"】。您需在有效期内携带有效身份证件前往拟携入运营企业当地营业厅办理，逾期办理需重新申请授权码。";
//                		smsInfo += "。请在有效期内携带有效身份证件前往当地携入方营业厅办理，月末两天不受理此业务。逾期办理需重新申请授权码，详情请咨询客服电话10086。";
                		sendAuthSMS(serialNumber, smsInfo,"申请授权码响应");

                        /*新增关怀短信
                         * 客户收到授权码短信后发送
                         */
                        UcaData ucaDataTemp = UcaDataFactory.getNormalUca(serialNumber);
                        int day = SysDateMgr.dayInterval(ucaDataTemp.getUser().getOpenDate(), SysDateMgr.getSysDate());

                        String careInfo = "尊敬的用户，您好！今天是中国移动为您服务的第"+day+"天，感谢您曾选择我们。" +
                                "虽未能相伴到底，我们仍心存感激。无论您去哪里，中国移动将一直在这等您。【中国移动】";
                        sendAuthSMS(serialNumber, careInfo,"关怀短信");
                    }
                }else{

                	flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_STATE_BY_TID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                }
                if (flag != 1)
                {
                   // CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125003);
                    throwsException("125003","响应返回成功：修改NP表状态时异常！");
                }

            }
            else if ("1".equals(dealMethod))
            {// 响应返回失败，完全撤销
            	//授权码响应失败短信add by dengyi5 
            	boolean isNotAuthSMS = true;//是否不发送授权码短信
            	String smsInfo = "";
            	if(NpConst.AUTHCODE_RESP.equals(commAndCode)){//授权码失败发送短信add by dengyi5                	
            		//工信部返回授权码申请失败原因
            		IDataset paramTradeTypeCodes = CommparaInfoQry.getCommparaInfoByCode("CSM", "173", "RESPONSECODE", responseCode , "0898");
            		if(IDataUtil.isNotEmpty(paramTradeTypeCodes)){//PARA_CODE25授权码提示短信内容，字段长度符合 by panyu5
            			smsInfo = paramTradeTypeCodes.getData(0).getString("PARA_CODE25");
            			isNotAuthSMS = false;//需要发送授权码短信
            		}
            	}
            	if(NpConst.AUTH_RESP.equals(commAndCode))
            	{//查验流程不做工单处理判断 by dengyi5
            		isNotAuthSMS = false;
            	}
                // 1.修改携入申请取消工单为取消状态
                inparam.clear();
                inparam.put("TRADE_ID", tradeId);
                inparam.put("UPDATE_STAFF_ID", "SOA");
                inparam.put("CANCEL_TAG", "1");
                int flag = Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_CANCELTAG_BY_TID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                if (flag != 1&&isNotAuthSMS)
                {
                    //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125301);
                    throwsException("125301","响应返回失败-完全撤销：修改携转申请撤销工单取消标识异常！");
                }
                // 2.将该工单记录搬迁到历史台帐表
                inparam.clear();
                inparam.put("TRADE_ID", tradeId);
                inparam.put("CANCEL_TAG", "1");
                inparam.put("CANCEL_STAFF_ID", "SOA");
                inparam.put("CANCEL_DEPART_ID", "SOA");
                flag = Dao.executeUpdateByCodeCode("TF_BH_TRADE", "INS_BY_TRADE_CANCEL", inparam,Route.getJourDb(BizRoute.getRouteId()));
                if (flag != 1&&isNotAuthSMS)
                {
                   // CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125302);
                    throwsException("125302","响应返回失败-完全撤销：将撤消后的工单搬迁到历史台帐表发生异常！");
                }
                // 3.将台帐表内该条记录删除
                inparam.clear();
                inparam.put("TRADE_ID", tradeId);
                flag = Dao.executeUpdateByCodeCode("TF_B_TRADE", "DEL_CANCEL_BOOK", inparam,Route.getJourDb(BizRoute.getRouteId()));
                if (flag != 1&&isNotAuthSMS)
                {
                    //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125303);
                    throwsException("125303","响应返回失败-完全撤销：台帐表内记录删除发生异常！");
                }
                // 4.修改TRADE_NP表内该记录为取消状态
                inparam.clear();
                inparam.put("TRADE_ID", tradeId);
                inparam.put("RESULT_CODE", responseCode);
                inparam.put("RESULT_MESSAGE", responseMessage);
                inparam.put("MSG_CMD_CODE", msgCmdCode);
                inparam.put("REMARK", remark);
                inparam.put("CANCEL_TAG", "1");
                inparam.put("STATE", state);
                flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_CANCELTAG_BY_TID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                if (flag != 1&&isNotAuthSMS)
                {
                    //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125304);
                    throwsException("125304","响应返回失败-完全撤销：修改携转申请撤销NP台帐表取消标识异常！");
                }

                // 携入申请取消完全撤销时，还需恢复原携入申请工单
                if ("1503".equals(tradeTypeCode))
                {
                    // 1.因为1503和39||40同一个FLOWID，所以通过FLOWID查询39||40的已撤销工单号 modi
                    // by wangjx 2010-5-18 不再通过历史表直接查询
                    IDataset tradeNps = TradeNpQry.getTradeNp(flowId, "1", tradeTypeCode);
                    if (IDataUtil.isEmpty(tradeNps) || tradeNps.size() > 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125305, flowId);
                        throwsException("125305","携入申请取消-完全撤销：查询原携转申请工单异常，FLOWID=【"+flowId+"】！");
                    }
                    // 2.修改历史台帐表内原携入申请开户工单为正常工单
                    inparam.clear();
                    inparam.put("TRADE_ID", tradeNps.getData(0).getString("TRADE_ID"));
                    inparam.put("CANCEL_TAG", "0");
                    inparam.put("UPDATE_STAFF_ID", "SOA");
                    flag = Dao.executeUpdateByCodeCode("TF_BH_TRADE", "UPD_NORMAL_TRADE_BY_TID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    if (flag != 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125306, tradeNps.getData(0).getString("TRADE_ID"));
                        throwsException("125306","携入申请取消-完全撤销：修改历史台帐表内原携入申请开户工单发生异常，trade_id=【"+tradeNps.getData(0).getString("TRADE_ID")+"】！");
                    }
                    // 3.将开户工单由历史表搬到台帐主表
                    inparam.clear();
                    inparam.put("TRADE_ID", tradeNps.getData(0).getString("TRADE_ID"));
                    inparam.put("CANCEL_TAG", "0");
                    inparam.put("UPDATE_STAFF_ID", "SOA");
                    flag = Dao.executeUpdateByCodeCode("TF_B_TRADE", "INS_BY_BHTRADE", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    if (flag != 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125307, tradeNps.getData(0).getString("TRADE_ID"));
                        throwsException("125307","携入申请取消-完全撤销：将开户工单由历史表搬到台帐主表发生异常，，TRADE_ID=【"+tradeNps.getData(0).getString("TRADE_ID")+"】！");
                    }
                    // 4.删除历史台帐表内该条数据
                    inparam.clear();
                    inparam.put("TRADE_ID", tradeNps.getData(0).getString("TRADE_ID"));
                    flag = Dao.executeUpdateByCodeCode("TF_BH_TRADE", "DEL_BY_TRADEID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    if (flag != 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125308, tradeNps.getData(0).getString("TRADE_ID"));
                        throwsException("125308","携入申请取消-完全撤销：删除历史台帐表内该条工单发生异常，，TRADE_ID=【"+tradeNps.getData(0).getString("TRADE_ID")+"】！");

                    }
                    // 5.修改NP子台帐表为正常工单
                    inparam.clear();
                    inparam.put("CANCEL_TAG", "0");
                    inparam.put("TRADE_ID", tradeNps.getData(0).getString("TRADE_ID"));
                    flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_CANCELTAG_BY_ID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                    if (flag != 1)
                    {
                        //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125309, tradeNps.getData(0).getString("TRADE_ID"));
                        throwsException("125309","携转申请取消：修改NP子台帐表工单异常，TRADE_ID=【"+tradeNps.getData(0).getString("TRADE_ID")+"】！");

                    }
                }
                else if ("39".equals(tradeTypeCode) || "40".equals(tradeTypeCode))
                {
                    resTag = "1";
                    param.put("RES_TAG", resTag);
                    param.put("TRADE_CITY_CODE", tradeInfos.getData(0).getString("TRADE_CITY_CODE"));
                	param.put("TRADE_DEPART_ID", tradeInfos.getData(0).getString("TRADE_DEPART_ID"));
                	param.put("TRADE_ID", tradeInfos.getData(0).getString("TRADE_ID"));
                	param.put("BRAND_CODE", tradeInfos.getData(0).getString("BRAND_CODE"));
                	param.put("SERIAL_NUMBER", tradeInfos.getData(0).getString("SERIAL_NUMBER"));
                }
                
                if("40".equals(tradeTypeCode))
                {
					inparam.clear();
					inparam.put("TRADE_ID", tradeId);
					flag = Dao.executeUpdateByCodeCode("TF_BH_TRADE_STAFF", "INSERT_FROM_BH_TRADE", inparam);
					
					inparam.clear();
					inparam.put("TRADE_ID", tradeId);
					inparam.put("SUBSCRIBE_STATE", "A");
					flag = Dao.executeUpdateByCodeCode("TF_BH_TRADE_STAFF", "UPD_BY_TID", inparam);
                }
            	if(!isNotAuthSMS && !NpConst.AUTH_RESP.equals(commAndCode)){//需要发送授权码失败短信,查验流程不发短信
            		sendAuthSMS(serialNumber, smsInfo,"申请授权码响应");
                }
            }
            else if ("2".equals(dealMethod))
            {// 响应返回失败，待时重发

                String retryMethod = param.getString("RETRYMETHOD");
                String retryTime = param.getString("RETRY_TIME");

                inparam.clear();
                inparam.put("TRADE_ID", tradeId);
                inparam.put("RESULT_CODE", responseCode);
                inparam.put("RESULT_MESSAGE", responseMessage);
                inparam.put("MSG_CMD_CODE", msgCmdCode);
                inparam.put("REMARK", remark);

                inparam.put("STATE", state);

                // 月未 和节假日不处理retryTime 时得改
                if ("604".equals(responseCode) && (NpConst.ACT_RESP.equals(commAndCode) || NpConst.ACT_RESP.equals(commAndCode) || NpConst.DACT_RESP.equals(commAndCode) || NpConst.RESUME_RESP.equals(commAndCode)))
                {
                    String lastDate = SysDateMgr.decodeTimestamp(SysDateMgr.getLastDateThisMonth(), SysDateMgr.PATTERN_STAND_YYYYMMDD);
                    String sysDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD);

                    if (lastDate.compareTo(sysDate) == 0)
                    {
                        retryTime = SysDateMgr.addDays(sysDate, 1);
                    }
                    // 查询处理时间是不是在法定节假日内
                    IDataset list = CommparaInfoQry.getNpHolidayInfos("CSM", "172", "0", SysDateMgr.decodeTimestamp(retryTime, SysDateMgr.PATTERN_STAND_YYYYMMDD));
                    if (IDataUtil.isNotEmpty(list))
                    {
                        retryTime = list.getData(0).getString("PARA_CODE3");
                    }
                }

                inparam.put("RETRY_TIME", retryTime);
                int flag = 0;
                if ("0".equals(retryMethod))
                {
                    flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_STATE_BY_RM0", inparam,Route.getJourDb(BizRoute.getRouteId()));
                }
                else
                {
                    flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_STATE_BY_RM1", inparam,Route.getJourDb(BizRoute.getRouteId()));
                }

                if (flag != 1)
                {
                    //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125005);
                    throwsException("125005","响应返回失败-待时重发：修改NP表状态时异常！");
                }
            }
            else if ("3".equals(dealMethod))
            {// 响应返回失败，人工干预
                inparam.clear();
                inparam.put("TRADE_ID", tradeId);
                inparam.put("RESULT_CODE", responseCode);
                inparam.put("RESULT_MESSAGE", responseMessage);
                inparam.put("MSG_CMD_CODE", msgCmdCode);
                inparam.put("REMARK", remark);
                inparam.put("STATE", state);
                int flag = Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_STATE_BY_ID", inparam,Route.getJourDb(BizRoute.getRouteId()));
                if (flag != 1)
                {
                    //CSAppException.apperr(CrmUserNpException.CRM_USER_NP_125006);
                    throwsException("125006","响应返回失败-人工干预：修改NP表状态时异常！");
                }

                param.put("ALERT_TYPE_CODE", "1");// 返回失败，发送预警短信
                if(!NpConst.AUTH_RESP.equals(commAndCode))
                {
                	param.put("ORDER_ID", tradeInfos.getData(0).getString("ORDER_ID"));
                	param.put("ACCT_ID", tradeInfos.getData(0).getString("ACCT_ID"));
                	param.put("CUST_ID", tradeInfos.getData(0).getString("CUST_ID"));
                }
            }
        }else{
            
            param.put("DEAL_INFO", "没任何处理");
        }
    }
    public void sendAuthSMS(String serialNumber,String smsInfo,String remark) throws Exception{
    	IData sendInfo = new DataMap();
		sendInfo.put("EPARCHY_CODE", "0898");
		sendInfo.put("RECV_OBJECT", serialNumber);
		sendInfo.put("RECV_ID", serialNumber);
		sendInfo.put("SMS_PRIORITY", "50");
		sendInfo.put("NOTICE_CONTENT", smsInfo);
		sendInfo.put("REMARK", remark);
		sendInfo.put("FORCE_OBJECT", "10086");
		SmsSend.insSms(sendInfo);
    } 
    /**
     * @Function: sendNpAlertSms
     * @Description: 携号转网状态修改接口TCS_ModifyNpState人工干预报警短信发送
     * @param param
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-6-7 上午9:57:06
     */
   
    private void throwsException(String errorCode, String errorInfo) throws Exception
    {
        IDataset errorInfos = new DatasetList();

        IData errorData = new DataMap();
        errorData.put("TIPS_CODE", errorCode);
        errorData.put("TIPS_INFO", errorInfo);
        errorInfos.add(errorData);

        IData checkData = new DataMap();
        checkData.put("TIPS_TYPE_ERROR", errorInfos);

        CSAppException.breerr(checkData);
    }

    /**
     * @Function: transferForNpRequest
     * @Description: SOA传值转换 成tradeTypeCode
     * @param param
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-21 上午10:21:06
     */
    public void transferForNpRequest(IData param) throws Exception
    {

        String strCommandCode = param.getString("COMMANDCODE");
        String strSerialNumber = param.getString("NPCODE");
        String strTradeEparchyCode = param.getString("EPARCHY_CODE");
        String strServiceType = param.getString("SERVICETYPE");
        String strFlowId = param.getString("FLOWID");

        String strUserId = "";
        String strUserNpTag = "";
        String strResCode = "";
        String iremoveTag = "";

        String iTradeTypeCode = "0";
        String iResultCode = "699";
        IDataset userInfos = null;

        if (NpConst.SUSPEND_REQ.equals(strCommandCode))
        {
            userInfos = UserInfoQry.getUserInfosByCodeSet(strSerialNumber, "0", "Y");
            if (IDataUtil.isNotEmpty(userInfos))
            {
                return;
            }
        }

        if (NpConst.DACT_NOTIFY.equals(strCommandCode))
        {
            iremoveTag = "7";
        }
        else
        {
            iremoveTag = "0";
        }

        if (!NpConst.CANCEL_IND.equals(strCommandCode) && !NpConst.CANCEL_REQ.equals(strCommandCode) && !NpConst.RETURN_NOTIFY.equals(strCommandCode))
        {
            userInfos = UserInfoQry.getUserInfoBySerailNumber(iremoveTag, strSerialNumber);
            if (userInfos.size() != 1)
            {
                String strAreaCode = "", strMessageID = "";
                strMessageID = param.getString("MESSAGEID").substring(3, 6);
                if (NpConst.APPLY_REQ.equals(strCommandCode))
                {
                    iResultCode = "613";

                    IDataset msisdns = MsisdnInfoQry.getMsisDns(strSerialNumber);
                    if (IDataUtil.isNotEmpty(msisdns))
                    {
                        String areaCode = msisdns.getData(0).getString("AREA_CODE");
                        if (StringUtils.isNotBlank(areaCode) && areaCode.length() == 4)
                        {
                            strAreaCode = areaCode.substring(1, 4);
                        }
                        else
                        {
                            strAreaCode = areaCode;
                        }
                        if (!strAreaCode.equals(strMessageID))
                        {
                            iResultCode = "696";
                            throwsException("696", "MessageID与号码的NP号段区域不匹配，请核实!");
                        }

                    }
                    else
                    {
                        iResultCode = "696";
                        throwsException("696", "该号码不存在于号段表中，请核查!");
                    }

                }
                else if (NpConst.SUSPEND_REQ.equals(strCommandCode) || NpConst.DACT_REQ.equals(strCommandCode))
                {
                    iResultCode = "649";
                }
                else if (NpConst.RESUME_REQ.equals(strCommandCode))
                {
                    param.put("RESULT_CODE", "699"); // 为了申请恢复的业务使用
                    iResultCode = "699";
                }
                else
                {
                    iResultCode = "699";
                }

                throwsException(iResultCode, "获取SERIAL_NUMBER=[" + strSerialNumber + "]的用户资料失败！");

            }
            String userTagSet = "";
            if (IDataUtil.isNotEmpty(userInfos))
            {

            strUserId = userInfos.getData(0).getString("USER_ID");
            userTagSet = userInfos.getData(0).getString("USER_TAG_SET");
            }
            if (StringUtils.isBlank(userTagSet))
            {
                strUserNpTag = "0";
            }
            else
            {
                strUserNpTag = userTagSet.substring(0, 1);
            }

        }
        else if (NpConst.CANCEL_IND.equals(strCommandCode))
        {
            userInfos = UserInfoQry.getUserInfoBySerailNumber(iremoveTag, strSerialNumber);
            if (IDataUtil.isNotEmpty(userInfos) && userInfos.size() == 1)
            {
                strUserId = userInfos.getData(0).getString("USER_ID");
                String userTagSet = userInfos.getData(0).getString("USER_TAG_SET");
                if (StringUtils.isBlank(userTagSet))
                {
                    strUserNpTag = "0";
                }
                else
                {
                    strUserNpTag = userTagSet.substring(0, 1);
                }
            }

            if (IDataUtil.isEmpty(userInfos))
            {
                userInfos = UserInfoQry.getUserInfoBySerailNumber("7", strSerialNumber);
                if (IDataUtil.isNotEmpty(userInfos) && userInfos.size() == 1)
                {
                    strUserId = userInfos.getData(0).getString("USER_ID");
                    String userTagSet = userInfos.getData(0).getString("USER_TAG_SET");
                    if (StringUtils.isBlank(userTagSet))
                    {
                        strUserNpTag = "0";
                    }
                    else
                    {
                        strUserNpTag = userTagSet.substring(0, 1);
                    }
                }
                else if (IDataUtil.isEmpty(userInfos))
                {
                    IDataset ids = TradeNpQry.getTradeNpByFlowId(strFlowId);
                    if (IDataUtil.isEmpty(ids))
                    {
                        iTradeTypeCode = "0";
                        return;
                    }
                    else if (ids.size() != 1)
                    {
                        iResultCode = "638";
                        throwsException(iResultCode, "获取FLOWID=[" + strFlowId + "]的携入申请记录失败！");
                    }

                }
            }
            if ("0".equals(strUserNpTag) || "1".equals(strUserNpTag) || "6".equals(strUserNpTag))
            {

                iTradeTypeCode = "1504";
                iResultCode = "638";
                IDataset tradeNps = TradeNpQry.getTradeNpByFlowId2(strFlowId);
                if (IDataUtil.isEmpty(tradeNps))
                {
                    return;
                }
                else if (tradeNps.size() == 1)
                {
                    String strState = tradeNps.getData(0).getString("STATE", "");
                    if ("021".equals(strState) || "041".equals(strState))
                    {
                        iResultCode = "636";
                        throwsException(iResultCode, "FLOWID=[" + strFlowId + "]的业务申请未成功！不允许取消！");
                    }
                    else if ("020".equals(strState))
                    {
                        iResultCode = "637";
                        throwsException(iResultCode, "FLOWID=[" + strFlowId + "]的业务申请处理中！不允许取消！");
                    }
                }
                else if (tradeNps.size() > 1)
                {
                    throwsException(iResultCode, "获取FLOWID=[" + strFlowId + "]的携入申请记录失败！");
                }
            }

            if ("3".equals(strUserNpTag))
            {
                iTradeTypeCode = "1504"; // 申请取消请求
                iResultCode = "638";
                IDataset tradeNps = TradeNpQry.getTradeNpByFlowId2(strFlowId);

                if (IDataUtil.isEmpty(tradeNps) || tradeNps.size() > 1)
                {
                    throwsException("115001", "获取FLOWID=[" + strFlowId + "]的携出申请记录失败！");
                }
                strResCode = tradeNps.getData(0).getString("SERIAL_NUMBER");
            }

//            if ("4".equals(strUserNpTag) || "8".equals(strUserNpTag))
//            {
//                iResultCode = "638";
//                throwsException(iResultCode, "号码【" + strResCode + "】已经携出生效，业务取消失败！");
//            }
        }

        param.put("PSPT_ID", param.getString("CREDNUMBER", ""));
        param.put("PSPT_TYPE_CODE", param.getString("CREDTYPE", ""));
        // -----CommandCode转换TradeTypeCode开始------------------------------------------------------------------------------------
        if (NpConst.APPLY_REQ.equals(strCommandCode))
        {
            iTradeTypeCode = "41"; // 携出申请
        }
        else if (NpConst.SUSPEND_REQ.equals(strCommandCode))
        {
            iTradeTypeCode = "43"; // 欠费停机请求
        }
        else if (NpConst.RESUME_REQ.equals(strCommandCode))
        {
            iTradeTypeCode = "45"; // 缴费开机请求
        }
        else if (NpConst.DACT_REQ.equals(strCommandCode))
        {
            iTradeTypeCode = "47"; // 欠费注销请求
        }
        else if (NpConst.DACT_NOTIFY.equals(strCommandCode))
        {
            iTradeTypeCode = "48"; // 欠费注销告知
        }
        else if (NpConst.RETURN_NOTIFY.equals(strCommandCode))
        {
            iTradeTypeCode = "189"; // 号码归还告知
        }
        else if (NpConst.CANCEL_IND.equals(strCommandCode))
        {
            iTradeTypeCode = "1503";// 先默认为1503
        }
        else if (NpConst.CANCEL_REQ.equals(strCommandCode))
        {
            iTradeTypeCode = "1504"; // 申请取消请求
        }
        else if(NpConst.AUTHCODE_REQ.equals(strCommandCode))
        {
        	iTradeTypeCode = "41";
        	param.put("AUTH_TAG", "AUTHCODE_REQ");
        }
        else
        {
            throwsException(iResultCode, "获取CommandCode=[" + strCommandCode + "]对应的业务编码失败！");
        }

        if ("1503".equals(iTradeTypeCode))
        {
            IDataset tradeNps = TradeNpQry.getTradeNpByFlowId2(strFlowId);
            if (IDataUtil.isEmpty(tradeNps))
            {
                iTradeTypeCode = "0";
                return;
            }
            else if (tradeNps.size() == 1)
            {
                strUserId = tradeNps.getData(0).getString("USER_ID");
                String tradeCode = tradeNps.getData(0).getString("TRADE_TYPE_CODE");
                if ("41".equals(tradeCode))
                {
                    iTradeTypeCode = "1504";
                }
                if ("1503".equals(iTradeTypeCode))
                {
                    String strTradeId = tradeNps.getData(0).getString("TRADE_ID", "");
                    IDataset tradeReses = TradeResInfoQry.getTradeRes(strTradeId, "1", "0");
                    if (IDataUtil.isEmpty(tradeReses) || tradeReses.size() > 1)
                    {
                        throwsException(iResultCode, "获取TRADE_ID=[" + strTradeId + "]的携号资源失败！");
                    }
                    strResCode = tradeReses.getData(0).getString("RES_CODE", "");
                }
            }
            else
            {
                throwsException(iResultCode, "FLOWID=[" + strFlowId + "]的记录有未完工的已生效工单，请核查！");
            }    
        }
        
        // -----CommandCode转换TradeTypeCode结束--------------
        if ("45".equals(iTradeTypeCode))
        {
            // 老系统罗辑拆成下面逻辑
            // if(mainSvcStateCount>1){inBuf.SetInt("RESULT_CODE",663);}else if(mainSvcStateCount==1 &&
            // mainSvcStateBuf.GetString("STATE_CODE") ==
            // "Y"){inBuf.SetInt("RESULT_CODE",200);}else{inBuf.SetInt("RESULT_CODE",200);}
            IDataset mainSvcStates = UserSvcStateInfoQry.getUserMainState(strUserId);
            param.put("RESULT_CODE", "200");
            if (mainSvcStates.size() > 1)
            {
                param.put("RESULT_CODE", "663");
            }
        }

        if ("1504".equals(iTradeTypeCode))
        {
            iResultCode = "638";
            IDataset tradeNps = TradeNpQry.getTradeNpByFlowId2(strFlowId);
            if (IDataUtil.isEmpty(tradeNps) || tradeNps.size() > 1)
            {
                throwsException("115001", "获取FLOWID=[" + strFlowId + "]的携出申请记录失败！");
            }
        }        param.put("SERIAL_NUMBER", strSerialNumber);
        param.put("USER_ID", strUserId);
        param.put("TRADE_TYPE_CODE", iTradeTypeCode);
        param.put("TRADE_EPARCHY_CODE", strTradeEparchyCode);
        param.put("NP_SERVICE_TYPE", strServiceType);
        param.put("FLOW_ID", strFlowId);
        param.put("TRADE_CITY_CODE", strTradeEparchyCode);
        param.put("TRADE_DEPART_ID", "0001");
        param.put("TRADE_STAFF_ID", "SOA");
        param.put("USER_NP_TAG", strUserNpTag);
        param.put("RES_NO", strResCode);
        if (param.containsKey("MESSAGEID"))
        {
            param.put("MESSAGE_ID", param.getString("MESSAGEID"));
        }
        }
            
    /**
     * 接收消息容错处理
     * 1、收到ACK回复，直接收到响应的情况
     * 2、未收到响应，直接收到告知的情况
     *
     * @param commAndCode
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public IDataset getMessageReversed(String commAndCode,String serialNumber) throws Exception {
        IDataset npbuf = new DatasetList();
        IDataset paramTradeTypeCodes = CommparaInfoQry.getCommparaInfoByCode("CSM", "173", "COMMANDCODE",commAndCode,"0898");
        if (IDataUtil.isNotEmpty(paramTradeTypeCodes)) {
            IData paramTradeTypeCode = paramTradeTypeCodes.getData(0);
            npbuf = TradeNpQry.getTradeNpBySnTradeTypeCode(serialNumber, paramTradeTypeCode.getString("PARA_CODE2"));
        }
        return npbuf;
        
    }
}

/****************
 * tf_f_user_np np_tag, 也是tf_f_user USER_TAG_SET 0-非携转：没有发生NP业务。 1-已携入：携入方营业员审核用户携入申请资格后，生效流程完工生成有效的用户资料后为“已携入”。（携入方状态）
 * 2-携入已销：用户在携入开户后，由于携出方欠费销号或者携入方欠费销号或前台立即销号后将标志为“携入已销”。（携入方状态） 3-携出中：携入申请业务发起后，针对携出方如果同意携出将标记为“携出中”。（携出方状态）
 * 4-已携出：用户真正携出生效后所标识状态（携出方状态） 5-携出已销：携出用户因为携出方发起或者携入方发起的注销生效之后，携出方将其状态置为“携出已销”（携出方状态）
 * 6-携回：本网用户携出一段时间后再携入归属运营商时标记为“携回”。（归属方状态） 7-携回已销：携回用户发生欠费销号或者立即销号时状态置为“携回已销”（携出方状态）
 * 8-携入携出：针对携入的用户超过三月后再携入到其他的运营商时状态置为“携入已出”（携出方状态）
 **/


/***
 * tf_b_trade_np 表里 state状态描述 000---初始状态；001---携出方ACK更新失败； 002---携出方ACK更新成功；009---更新初始状态成功； 011---携入请求失败；020---携入响应成功
 * 021---携入响应失败；030---携入告知成功；031---携入告知失败 040---携入申请告知成功；041---携入申请告知失败； 050---携入生效响应成功；051---携入生效响应失败
 * 060---携入生效告知成功；061---携入生效告知失败 100---携出生效成功；120---携出响应成功；121---携出响应失败 130---携出告知成功；131---携出告知失败
 */

/**
 * tf_f_user 表 remove_tag 加两状态 7携出已销 8携出欠费销号
 **/

/**
 * tf_f_user表里user_state_codeset 是由TF_F_USER_SVCSTATE 表里main_tag=1 和 t.end_date>SYSDATE 的state_code 拼起来;
 * TF_F_USER_SVCSTATE state_code X携出销号 Y携出欠费停机 Z携出欠费销号
 */

