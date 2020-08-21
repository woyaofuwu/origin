
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DESUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata.PlatReqData;

public class PlatSmsAction implements ITradeAction
{

    private static Log logger = LogFactory.getLog(PlatSmsAction.class);

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	String batchId = btd.getMainTradeData().getBatchId();
        PlatReqData req = (PlatReqData) btd.getRD();
        UcaData uca = btd.getRD().getUca();
        List<PlatSvcTradeData> platSvcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
        int size = platSvcTradeDatas.size();
        String oprRealsion="0";
        for (int i = 0; i < size; i++)
        {
            PlatSvcTradeData pstd = platSvcTradeDatas.get(i);
            PlatOfficeData officeData = null;
            if (pstd.getPmd() == null)
            {
                officeData = PlatOfficeData.getInstance(pstd.getElementId());
            }
            else
            {
                officeData = ((PlatSvcData) pstd.getPmd()).getOfficeData();
            }
            
            String operCode = pstd.getOperCode();
            String inModeCode = CSBizBean.getVisit().getInModeCode();
            String pkgSeq = pstd.getPkgSeq();
            String oprSource = pstd.getOprSource();
            String spCode = officeData.getSpCode();
            String bizCode = officeData.getBizCode();
            String billType = officeData.getBillType();
            String servMode = officeData.getServMode();
            String feeModeTag = officeData.getFeeModeTag();
            String bizTypeCode = officeData.getBizTypeCode();
            String secondConfirm = officeData.getSecConfirmTag();// 0:订购时，不需要二次确认 1:订购时需要二次确认
            
            /**
             * 关于咪咕世界杯畅享 会员包月0元业务的批量办理
             * <br/>
             * 批量办理“咪咕世界杯畅享权益包” SP_CODE: 698040,BIZ_CODE:69901930838080
             * 不需要发短信
             * @author zhuoyingzhi
             * @date 20180608
             */
            MainTradeData mainTradeData = btd.getMainTradeData();
            if(StringUtils.isNotEmpty(mainTradeData.getBatchId())
            		&&"06".equals(operCode)){
                //spCode   param_code
                //para_code1   bizCode
            	//&&"698040".equals(spCode)&&"69901930838080".equals(bizCode)
            	IDataset commparas = CommparaInfoQry.getCommParas("CSM", "2029", spCode, bizCode, CSBizBean.getUserEparchyCode());
            	if(IDataUtil.isNotEmpty(commparas)){
            		//不需要发短信
            		continue ;
            	}
            }
            /****************关于咪咕世界杯畅享 会员包月0元业务的批量办理_end***************************/
            
            
            if (pstd.getPmd() == null)  //139邮箱订购特殊处理
            {
            	oprRealsion = "0";
            }
            else
            {
            	oprRealsion = ((PlatSvcData) pstd.getPmd()).getOpeReasion();
            }
            //如果是有权限的工号办理批量退订，不发送二次确认短信
            if(StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SKIP_TWO_CHECK") && "07".equals(operCode) && StringUtils.isNotBlank(batchId)){
                continue;
            }
            IDataset smsConfigs = PlatSvcInfoQry.queryPlatSmsConfig(inModeCode, bizTypeCode, spCode, bizCode, operCode, oprSource, billType, servMode, feeModeTag);
            if (IDataUtil.isNotEmpty(smsConfigs))
            {
                int smsSize = smsConfigs.size();
                for (int j = 0; j < smsSize; j++)
                {
                    IData smsConfig = smsConfigs.getData(j);
                    String eventType = smsConfig.getString("EVENT_TYPE");
                    String templateId = smsConfig.getString("TEMPLATE_ID");
                    String mvelExpr = smsConfig.getString("MVEL_EXPR");
                    String smsType = smsConfig.getString("SMS_TYPE", "0");

                    if(StringUtils.isNotBlank(batchId) && BizEnv.getEnvBoolean("BATCHSECONDCONFIRM_PLATSVC"))
            		{
                    	eventType = "2";
            		}
                    if (PlatConstants.SMS_TYPE_SECOND_CONFIRM.equals(eventType))
                    {
                        // 如果当前获取的短信模版是二次确认短信模版，
                        // 1:如果是订购关系同步;2:或者是二次确认短信回复;3:如果局数据的二次确认标识为不需要二次确认 ;4:如果是批量过来的；5.如果是取消订购的(operCode为 07) 这五种情况不发二次确认短信
                        if (req.isSync() || "true".equals(req.getIsConfirm()) || "0".equals(secondConfirm) || StringUtils.isNotBlank(pkgSeq)||oprRealsion.equals("1") || "07".equals(operCode))
                        {
                        	if("0".equals(secondConfirm) && StringUtils.isNotBlank(batchId))
                        	{
                        		if(!BizEnv.getEnvBoolean("BATCHSECONDCONFIRM_PLATSVC"))
                        		{
                        			continue;
                        		}
                        	}
                        	else
                        	{
                        		continue;
                        	}
                        }
                    }

                    if (StringUtils.isNotBlank(mvelExpr))
                    {
                        MVELExecutor exector = new MVELExecutor();
                        exector.setMiscCache(CRMMVELMiscCache.getMacroCache());
                        exector.prepare(btd, pstd, officeData);
                        Object v = exector.execScript(mvelExpr);
                        boolean isFill = exector.getBooleanValue(v);
                        if (!isFill)
                        {
                            continue;
                        }
                    }
                    MVELExecutor exector = new MVELExecutor();
                    exector.setMiscCache(CRMMVELMiscCache.getMacroCache());
                    exector.prepare(btd, pstd, officeData);
                    String content = TemplateBean.getTemplate(templateId);
                    IData template = TemplateBean.getTemplateInfoByPk(templateId);
                	SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            		String thisTime=df.format(new Date());
                    String templateContent = content.replaceAll("@\\{timer\\}", thisTime);
                    String replaceContent = exector.applyTemplate(templateContent);
                    if (PlatConstants.SMS_TYPE_SECOND_CONFIRM.equals(eventType))
                    {
                        req.setPreType(BofConst.PRE_TYPE_SMS_CONFIRM);
                        IData inputData = new DataMap();
                        // inputData.putAll(pstd.toData());
                        inputData.put("SERVICE_ID", pstd.getElementId());
                        inputData.put("OPER_CODE", pstd.getOperCode());
                        inputData.put("OPR_SOURCE", pstd.getOprSource()); 
                         inputData.put("OPR_REASION", oprRealsion);
                        
                        if (StringUtils.isNotBlank(pstd.getIntfTradeId()))
                        {
                            inputData.put("INTF_TRADE_ID", pstd.getIntfTradeId());
                        }
                        if (StringUtils.isNotBlank(pstd.getGiftSerialNumber()))
                        {
                            inputData.put("GIFT_SERIAL_NUMBER", pstd.getGiftSerialNumber());
                            inputData.put("START_DATE", pstd.getStartDate());
                            inputData.put("END_DATE", pstd.getEndDate());
                        }
                        List<AttrTradeData> attrDatas = pstd.getAttrTradeDatas();
                        if (attrDatas != null && attrDatas.size() > 0)
                        {

                            int attrSize = attrDatas.size();
                            IDataset attrDataset = new DatasetList();
                            for (int k = 0; k < attrSize; k++)
                            {
                                IData attrData = new DataMap();
                                AttrTradeData attrTradeData = attrDatas.get(k);
                                attrData.put("ATTR_CODE", attrTradeData.getAttrCode());

                                // WLAN密码的特殊处理，WLAN密码需要加密，如果不解密，会被两次加密，
                                if (attrTradeData.getAttrCode().equals("AIOBS_PASSWORD"))
                                {

                                    if (PlatConstants.IS_ENCRYPT_PASSWORD)
                                    {
                                        attrData.put("ATTR_VALUE", DESUtil.decrypt(attrTradeData.getAttrValue()));
                                    }

                                }
                                else
                                {
                                    attrData.put("ATTR_VALUE", attrTradeData.getAttrValue());
                                }

                                attrDataset.add(attrData);
                            }
                            inputData.put("ATTR_PARAM", attrDataset);
                        }
                        inputData.put("PRE_TYPE", BofConst.PLAT_SVC_SEC);
                        inputData.put("SERIAL_NUMBER", uca.getSerialNumber());
                        inputData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
                        inputData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                        inputData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                        inputData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                        inputData.put("SVC_NAME", "SS.PlatRegSVC.tradeRegIntf");

                        IData twoCheckSms = new DataMap();
                        twoCheckSms.put("TEMPLATE_ID", templateId);// 海南需要传TEMPLATE_ID
                        twoCheckSms.put("SERIAL_NUMBER", uca.getSerialNumber());
                        twoCheckSms.put("SMS_CONTENT", replaceContent);
                        if(StringUtils.isNotBlank(batchId) && BizEnv.getEnvBoolean("BATCHSECONDCONFIRM_PLATSVC"))
                        {
                        	String serviceName = "服务";
            	            if (StringUtils.isNotBlank(pstd.getElementId()))
            	            {
            	                serviceName = UPlatSvcInfoQry.getSvcNameBySvcId(pstd.getElementId());
            	            }
                        	replaceContent = "订购确认：您好！感谢您对中国移动的大力支持，您即将订购由中国移动为您提供的"+ serviceName +"，请在24小时内回复“是”确认订购，回复其他内容或不回复，则不订购。我们一直努力，为您十分满意。【中国移动】您好";
                        	twoCheckSms.put("SMS_CONTENT", replaceContent);
                        }
                        twoCheckSms.put("SMS_TYPE", BofConst.PLAT_SVC_SEC);
                        twoCheckSms.put("OPR_SOURCE", "1");
                        TwoCheckSms.twoCheck(btd.getTradeTypeCode(), 0, inputData, twoCheckSms);
                        break;
                    }
                    else
                    {
                        if (!StringUtils.isEmpty(replaceContent))
                        {
                            stringTradeSms(btd, replaceContent, smsType, template.getString("TEMPLATE_TYPE"), templateId);
                            if(!"65".equals(smsConfig.getString("BIZ_TYPE_CODE")))
                            {
                            	break;
                            	
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 公用信息
     * 
     * @param btd
     * @return
     * @throws Exception
     */
    private IData getCommonSmsInfo(BusiTradeData btd, String smsType, String templateType) throws Exception
    {
        IData smsData = new DataMap();
        smsData.put("TRADE_ID", btd.getTradeId());
        smsData.put("RECV_OBJECT", btd.getRD().getUca().getSerialNumber()); // 接收短信号码
        smsData.put("RECV_ID", btd.getRD().getUca().getUserId());
        smsData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        smsData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        smsData.put("SMS_PRIORITY", "50");
        smsData.put("CANCEL_TAG", btd.getMainTradeData().getCancelTag());
        smsData.put("FORCE_OBJECT", "10086"); // 短信发送端口
        smsData.put("REMARK", "业务短信通知");
        smsData.put("NOTICE_CONTENT_TYPE", smsType);
        smsData.put("SMS_TYPE", smsType); // 如果SMS_TYPE为P，则为PUSH短信，接收方为10086，发送方为SERIAL_NUMBER
        smsData.put("SMS_TYPE_CODE", templateType); // 插TD_B_TEMPLATE的TEMPLATE_TYPE
        smsData.put("NOTICE_CONTENT_TYPE", smsType);

        return smsData;
    }

    private void stringTradeSms(BusiTradeData btd, String smsContent, String smsType, String templateType, String templateId) throws Exception
    {
        IData smsData = getCommonSmsInfo(btd, smsType, templateType);
        String sysdate = btd.getRD().getAcceptTime();

        SmsTradeData std = new SmsTradeData();
        std.setSmsNoticeId(smsData.getString("SMS_NOTICE_ID", SeqMgr.getSmsSendId()));
        std.setEparchyCode(smsData.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode()));
        std.setBrandCode(smsData.getString("BRAND_CODE", ""));
        std.setInModeCode(CSBizBean.getVisit().getInModeCode());
        std.setSmsNetTag(smsData.getString("SMS_NET_TAG", "0"));
        std.setChanId(smsData.getString("CHAN_ID", "11"));
        std.setSendObjectCode(smsData.getString("SEND_OBJECT_CODE", "6"));
        std.setSendTimeCode(smsData.getString("SEND_TIME_CODE", "1"));
        std.setSendCountCode(smsData.getString("SEND_COUNT_CODE", "1"));
        std.setRecvObjectType(smsData.getString("RECV_OBJECT_TYPE", "00"));
        std.setRecvObject(smsData.getString("RECV_OBJECT"));
        std.setRecvId(smsData.getString("RECV_ID", "-1"));
        std.setSmsTypeCode(smsData.getString("SMS_TYPE_CODE"));
        std.setSmsKindCode(smsData.getString("SMS_KIND_CODE", "02"));
        std.setNoticeContentType(smsData.getString("NOTICE_CONTENT_TYPE", "0"));
        std.setReferedCount(smsData.getString("REFERED_COUNT", "0"));
        std.setForceReferCount(smsData.getString("FORCE_REFER_COUNT", "1"));
        std.setForceObject(smsData.getString("FORCE_OBJECT")); // 发送短信端口
        std.setForceStartTime(smsData.getString("FORCE_START_TIME", ""));
        std.setForceEndTime(smsData.getString("FORCE_END_TIME", ""));
        std.setSmsPriority(smsData.getString("SMS_PRIORITY", "50"));
        std.setReferTime(smsData.getString("REFER_TIME", sysdate));
        std.setReferDepartId(smsData.getString("REFER_DEPART_ID", CSBizBean.getVisit().getDepartId()));
        std.setReferStaffId(smsData.getString("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId()));
        std.setDealTime(smsData.getString("DEAL_TIME", sysdate));
        std.setDealStaffid(smsData.getString("DEAL_STAFFID", CSBizBean.getVisit().getStaffId()));
        std.setDealDepartid(smsData.getString("DEAL_DEPARTID", CSBizBean.getVisit().getDepartId()));
        std.setDealState("0");// 处理状态，0：未处理
        std.setRemark(smsData.getString("REMARK"));
        std.setRevc1(smsData.getString("REVC1"));
        std.setRevc2(smsData.getString("REVC2"));
        std.setRevc3(templateId); // 预留字段3保存模版ID
        std.setRevc4(smsData.getString("REVC4"));
        std.setMonth(sysdate.substring(5, 7));
        std.setDay(sysdate.substring(8, 10));
        std.setCancelTag(smsData.getString("CANCEL_TAG"));

        // 短信截取
        String content = smsContent;
        int charLength = SmsSend.getCharLength(smsContent, 4000);
        content = content.substring(0, charLength);
        std.setNoticeContent(content);

        btd.add(btd.getRD().getUca().getUser().getSerialNumber(), std);
    }

}
