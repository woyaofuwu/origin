
package com.asiainfo.veris.crm.order.soa.person.common.action.sms;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.userident.UserIdentBean;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: PerSatisfySmsFinishAction.java
 * @Description: 生成满意度调查短信内容并发送
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014年8月26日 下午8:27:56 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014年8月26日 lijm3 v1.0.0 修改原因
 */
public class PerSatisfySmsFinishAction implements ITradeFinishAction
{
    private static transient Logger logger = Logger.getLogger(PerSatisfySmsFinishAction.class);

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        logger.info(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<进入 PerSatisfySmsFinishAction <<<<<<<<<<<<<<<<<<<");





        String tradeId = mainTrade.getString("TRADE_ID");

        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String tradeDepartId = mainTrade.getString("TRADE_DEPART_ID");
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String cancelTag = mainTrade.getString("CANCEL_TAG");
        String inModeCode = mainTrade.getString("IN_MODE_CODE");
        String strTradeStaffId = mainTrade.getString("TRADE_STAFF_ID");
        strTradeStaffId = StringUtils.upperCase(strTradeStaffId);
        String acceptMonth = mainTrade.getString("ACCEPT_MONTH");
        String batchId = mainTrade.getString("BATCH_ID","");
        
        String rsrvStr1 = mainTrade.getString("RSRV_STR1","");
        String rsrvStr3 = mainTrade.getString("RSRV_STR3","");


        //不发送满意度短信业务类型修改为配置@tanzheng@20100120
        IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", "9045");
        param.put("PARAM_CODE", "0");
        IDataset dataset = CommparaInfoQry.getCommparaInfoByPara(param);
        if(IDataUtil.isNotEmpty(dataset)){
            String paraCode20 = dataset.first().getString("PARA_CODE20");
            logger.debug("PerSatisfySmsFinishAction>>tz>>>>"+paraCode20);
            String tradeTypeCode1 = "|"+tradeTypeCode+"|";
            if(paraCode20.contains(tradeTypeCode1)){
                logger.debug("PerSatisfySmsFinishAction>>tz>>>>返回");
                return;
            }
        }




        if("240".equals(tradeTypeCode)){
        	//不发满意度短信的活动配置
            IDataset activeConfig = CommparaInfoQry.getCommparaInfoByCode("CSM", "9934", "NO_SEND_SMS", rsrvStr1, eparchyCode);
            if(IDataUtil.isNotEmpty(activeConfig) && activeConfig.size() > 0){
            	logger.info(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< nosendsmsWuHao5PerSatisfySmsFinishAction--58 <<<<<<<<<<<<<<<<<<<");
            	return;//指定活动不发送满意度短信
            }
        }
        
        if ("52000010".equals(rsrvStr3))
        {
            return; //流量卡充值不发
        }


        if (StringUtils.isNotBlank(batchId))
        {
            return; //批量业务不发送
        }

        if(StringUtils.indexOf(strTradeStaffId,"CREDIT") > -1
                || StringUtils.indexOf(strTradeStaffId,"SUPERUSR") > -1)
        {
            return; // 信控业务不发送
        }

        if (StringUtils.isBlank(serialNumber))
        {
            return;
        }

        if (serialNumber.indexOf("KD_") > -1)
        {
            serialNumber = serialNumber.substring(3);
        }

        if (!serialNumber.matches("^[0-9]*$") || serialNumber.length() != 11 || !serialNumber.startsWith("1"))
        {
            return;
        }

    /*    if ("7301".equals(tradeTypeCode) || "9701".equals(tradeTypeCode) || "9702".equals(tradeTypeCode) || "9703".equals(tradeTypeCode) || "9704".equals(tradeTypeCode) || "9705".equals(tradeTypeCode) || "9706".equals(tradeTypeCode)
                || "9707".equals(tradeTypeCode) || "9708".equals(tradeTypeCode) || "9709".equals(tradeTypeCode) || "9710".equals(tradeTypeCode) || "7101".equals(tradeTypeCode))
        {
            return;
        }*/

        if (("600".equals(tradeTypeCode) || "611".equals(tradeTypeCode) || "612".equals(tradeTypeCode) || "613".equals(tradeTypeCode)) && "3".equals(cancelTag))
        {
            return;
        }


       /* if ("1522".equals(tradeTypeCode) || "6014".equals(tradeTypeCode) || "6015".equals(tradeTypeCode) || "6016".equals(tradeTypeCode) || "6018".equals(tradeTypeCode) || "6019".equals(tradeTypeCode) || "6020".equals(tradeTypeCode))
        {
            return;
        }*/



        // 一级客服系统集中化服务评价业务——需要调用客户满意度调研请求的渠道类型配置
        IDataset departIdConfig = CommparaInfoQry.getCommparaInfoByCode("CSM", "9544", "SATISFY_DEPART_ID", tradeDepartId, eparchyCode);

        // 一级客服系统集中化服务评价业务——需要调用客户满意度调研请求的业务类型配置
        IDataset satisfyConfig = CommparaInfoQry.getCommparaInfoByCode("CSM", "9544", "SATISFY_TRADETYPE", tradeTypeCode, eparchyCode);

        // 调用客户满意度调研请求的渠道的业务类型不需要再次发送满意度调查短信
        if (IDataUtil.isNotEmpty(departIdConfig) && IDataUtil.isNotEmpty(satisfyConfig) && "0".equals(inModeCode)) {
            return;
        }

        if (("0".equals(inModeCode) || "3".equals(inModeCode) || "2".equals(inModeCode) || "5".equals(inModeCode) || "D".equals(inModeCode) || "X".equals(inModeCode)) && (strTradeStaffId.length() > 4 && strTradeStaffId.substring(0, 4) != "HNYD"))
        {

            IDataset cparams = null;
            if ("0".equals(inModeCode) || "3".equals(inModeCode))
            {

                cparams = ParamInfoQry.getCparamsBySnDays(serialNumber, "0", acceptMonth);
            }
            else
            {
                cparams = ParamInfoQry.getCparamsBySnDaysInModeCode(serialNumber, "0", inModeCode, acceptMonth);
            }
            
            // 如果当天内没发过则发送
            if (IDataUtil.isEmpty(cparams))
            {

                String smsKindCode = "0"; // 0 营业渠道, 1 10086渠道,2 12580渠道
                String str = "营业厅";
                if ("0".equals(inModeCode))
                {
                    str = "到中国移动营业网点";
                }
                else if ("3".equals(inModeCode))
                {
                    str = "到中国移动营业网点";
                }
                else if ("2".equals(inModeCode))
                {
                    str = "通过中国移动网上营业厅";
                }
                else if ("5".equals(inModeCode))
                {
                    str = "通过中国移动短信营业厅";
                }
                else if ("D".equals(inModeCode) || "X".equals(inModeCode))
                {
                    str = "通过中国移动自助终端";
                }

                String forceObject = "";
                if ("0".equals(inModeCode) || "3".equals(inModeCode))
                {
                    forceObject = "100867001";
                }
                else if ("2".equals(inModeCode))
                {
                    forceObject = "100867002";
                }
                else if ("5".equals(inModeCode))
                {
                    forceObject = "100867003";
                }
                else if ("D".equals(inModeCode) || "X".equals(inModeCode))
                {
                    forceObject = "100867004";
                }
                
                String smsTemplate=null;
                String sysDate=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT);
                if(sysDate.compareTo("20161231235959")<=0){
                	smsTemplate="尊敬的客户，感谢您@CHANNEL_NAME@查询办理业务，请您为本次服务的满意度进行评价：满意（10分）回复10，一般（9分）回复9，不满意（8分）回复8。我们一直在努力，只为您10分满意！装宽带、找移动！";
                }else{
                	smsTemplate="尊敬的客户，感谢您@CHANNEL_NAME@查询办理业务，请您为本次服务的满意度进行评价：满意（10分）回复10，一般（9分）回复9，不满意（8分）回复8。我们一直在努力，只为您10分满意！";
                }
                
                IDataset smsTemplateData=CommparaInfoQry.getCommNetInfo("CSM", "1704", "SATISFY_SMS_CONTENT");
                if(IDataUtil.isNotEmpty(smsTemplateData)){
                	IData templateData=smsTemplateData.first();
                	
                	StringBuffer templateTemp=new StringBuffer();
                	templateTemp.append(templateData.getString("PARA_CODE2",""));
                	templateTemp.append(templateData.getString("PARA_CODE3",""));
                	templateTemp.append(templateData.getString("PARA_CODE4",""));
                	templateTemp.append(templateData.getString("PARA_CODE5",""));
                	templateTemp.append(templateData.getString("PARA_CODE6",""));
                	templateTemp.append(templateData.getString("PARA_CODE7",""));
                	templateTemp.append(templateData.getString("PARA_CODE8",""));
                	templateTemp.append(templateData.getString("PARA_CODE9",""));
                	templateTemp.append(templateData.getString("PARA_CODE10",""));
                	templateTemp.append(templateData.getString("PARA_CODE11",""));
                	templateTemp.append(templateData.getString("PARA_CODE12",""));
                	templateTemp.append(templateData.getString("PARA_CODE13",""));
                	templateTemp.append(templateData.getString("PARA_CODE14",""));
                	templateTemp.append(templateData.getString("PARA_CODE15",""));
                	templateTemp.append(templateData.getString("PARA_CODE16",""));
                	templateTemp.append(templateData.getString("PARA_CODE17",""));
                	templateTemp.append(templateData.getString("PARA_CODE18",""));
                	
                	smsTemplate=templateTemp.toString();
                }
                
                
//                String smsContent = "尊敬的客户，感谢您" + str + "查询办理业务，请您为本次服务的满意程度进行评价：满意（10分）回复10，一般（9分）回复9，不满意（8分）回复8。我们一直在努力，只为您“10分”满意！";
                String smsContent=smsTemplate.replaceAll("@CHANNEL_NAME@", str);
                
                String smsState = "0";

                String tradeSmsId = SeqMgr.getSmsSendId();

                IData smsData = new DataMap();
                smsData.put("TRADE_ID", tradeId);
                smsData.put("SERIAL_NUMBER", serialNumber);
                smsData.put("RECV_ID", mainTrade.getString("USER_ID"));
                smsData.put("EPARCHY_CODE", mainTrade.getString("TRADE_EPARCHY_CODE"));
                smsData.put("IN_MODE_CODE", inModeCode);
                smsData.put("SMS_STATE", smsState);
                smsData.put("CANCEL_TAG", cancelTag);

                smsData.put("TRADE_TYPE_CODE", tradeTypeCode);
                smsData.put("SMS_CONTENT", smsContent);
                smsData.put("FORCE_OBJECT", forceObject);
                smsData.put("SMS_KIND_CODE", smsKindCode);
                smsData.put("REVERT_SMS_STATE", "0");
                smsData.put("REVERT_SMS_CONTENT", "");
                smsData.put("REVERT_DATE", "");

                smsData.put("TRADE_SMS_ID", tradeSmsId);

                smsData.put("RSRV_STR1", "");
                smsData.put("RSRV_STR2", "");
                smsData.put("RSRV_STR3", "");
                smsData.put("RSRV_STR4", "");
                smsData.put("RSRV_STR5", "");
                smsData.put("RSRV_DATE1", "");
                smsData.put("RSRV_DATE2", "");
                smsData.put("RSRV_DATE3", "");
                smsData.put("RSRV_TAG1", "");
                smsData.put("RSRV_TAG2", "");
                smsData.put("RSRV_TAG3", "");
                smsData.put("RSRV_NUM1", "0");
                smsData.put("RSRV_NUM2", "0");
                smsData.put("RSRV_NUM3", "0");
                smsData.put("SOURCE_NUMBER", forceObject);
                Dao.executeUpdateByCodeCode("TD_S_CPARAM", "INS_TI_O_TRADE_SMS", smsData);

                smsData.clear();
                
                
                /*
                 * 获取短信延迟发送的时间
                 */
                int delayTime=1800;	//单位为秒
                IDataset smsDelaySendTime=CommparaInfoQry.getCommNetInfo("CSM", "1705", "0");
                if(IDataUtil.isNotEmpty(smsDelaySendTime)){
                	String strDelayTime=smsDelaySendTime.first().getString("PARA_CODE1","");
                	
                	if(strDelayTime!=null&&!strDelayTime.equals("")){
                		delayTime=Integer.parseInt(strDelayTime);
                	}
                }
                
                smsData.put("SMS_NOTICE_ID", tradeSmsId);
                smsData.put("EPARCHY_CODE", mainTrade.getString("TRADE_EPARCHY_CODE"));
                smsData.put("RECV_OBJECT", serialNumber);
                smsData.put("FORCE_OBJECT", forceObject);// 发送号码
                smsData.put("RECV_ID", mainTrade.getString("USER_ID"));
                smsData.put("NOTICE_CONTENT", smsContent);
                smsData.put("FORCE_START_TIME", SysDateMgr.addSecond(SysDateMgr.getSysTime(), delayTime));
                smsData.put("FORCE_END_TIME", SysDateMgr.addDays(SysDateMgr.getSysTime(), 1));
                SmsSend.insSms(smsData);
            }

        }

    }

}
