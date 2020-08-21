package com.asiainfo.veris.crm.order.soa.person.common.action.sms;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class SpecialSmsAction implements ITradeAction{

	@Override
	@SuppressWarnings({ "rawtypes"})
	public void executeAction(BusiTradeData btd) throws Exception {
		
        sendWlanUnlimitedSms(btd);
		
	}
	
	/**
	 * 188元及以上降档为[98,188)元主套餐或98元以下升档为[98,188)元主套餐用户、
	 * 188元及以上降档为98元以下主套餐或[98,188元）降档为98元以下主套餐用户，短信提示用户自行订购短信提醒
	 * @param btd
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
    private void sendWlanUnlimitedSms(BusiTradeData btd) throws Exception {

        List<DiscntTradeData> tradeDiscnts = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        if(tradeDiscnts.isEmpty()){
        	return;
        }
        
        List<PlatSvcTradeData> wlanSvcData = btd.getRD().getUca().getUserPlatSvcByServiceId("98002401");
        if(wlanSvcData.isEmpty()){//没有订购wlan功能，不下发提醒短信
        	return;
        }
        
        boolean needSendWlanSms = false;
        boolean needCancleOldWlanDiscnt = false;
        String oldWlanDiscntCode = "";
        String newWlanDiscntLevel = "";
        for (int i = 0,size =tradeDiscnts.size(); i < size; i++){
            DiscntTradeData tradeDiscnt = tradeDiscnts.get(i);
            String modifyTag = tradeDiscnt.getModifyTag();
            String discntCode = tradeDiscnt.getDiscntCode();
            IDataset attrConfigs = CommparaInfoQry.getCommparaInfos("CSM", "2780", discntCode);
            if(IDataUtil.isNotEmpty(attrConfigs) && BofConst.MODIFY_TAG_DEL.equals(modifyTag)){
            	needCancleOldWlanDiscnt = true;
            	oldWlanDiscntCode = attrConfigs.first().getString("PARA_CODE2").trim();
            	for(int j=tradeDiscnts.size()- 1; j >0; j--){
            		IDataset attrConfigs1 = CommparaInfoQry.getCommparaInfos("CSM", "2780", tradeDiscnts.get(j).getDiscntCode());
            		if(IDataUtil.isNotEmpty(attrConfigs1) && BofConst.MODIFY_TAG_ADD.equals(tradeDiscnts.get(j).getModifyTag())){
                        String newLevel = attrConfigs1.first().getString("PARA_CODE1").trim();
                        String oldLevel = attrConfigs.first().getString("PARA_CODE1").trim();
                        if(!newLevel.equals(oldLevel) && (Integer.parseInt(newLevel)>0)){
                			needSendWlanSms = true;
                			newWlanDiscntLevel = newLevel;
                			break;
                        }else if(newLevel.equals(oldLevel)){//产品变更前后为同一档次，不处理
                        	needCancleOldWlanDiscnt = false;
                        	break;
                        }
            		}
            	}
            }
            if(needSendWlanSms){
            	break;
            }
        }
        
        if(needCancleOldWlanDiscnt){
            List<DiscntTradeData> oldDicntDatas = btd.getRD().getUca().getUserDiscntByDiscntId(oldWlanDiscntCode); 
            if(!oldDicntDatas.isEmpty()){//自动退订不符合档次要求的资费
                cancelOldoldWlanDiscnt(btd,oldDicntDatas.get(0));
            }
        }
        
        if(needSendWlanSms && StringUtils.isNotBlank(newWlanDiscntLevel)){
        	String notieContet = "";
        	if("5".equals(newWlanDiscntLevel)){
        		notieContet = "尊敬的客户您好，您可发送KTWLANRWY5至10086订购5元WLAN任我用套餐，每月WLAN使用100GB后将暂时关闭WLAN功能，下月自动恢复。您可在手机系统中配置CMCC热点的自动认证功能，首次配置成功后，无需输入账号和密码，即可完成自动认证登录，享受更加便捷的互联网服务。配置方法请见链接：http://dx.10086.cn/Z3A7NFb 【中国移动】";              	                                      		
        	}else if("10".equals(newWlanDiscntLevel)){
                notieContet = "尊敬的客户您好，您可发送KTWLANRWY10至10086订购10元WLAN任我用套餐，每月WLAN使用100GB后将暂时关闭WLAN功能，下月自动恢复。您可在手机系统中配置CMCC热点的自动认证功能，首次配置成功后，无需输入账号和密码，即可完成自动认证登录，享受更加便捷的互联网服务。配置方法请见链接：http://dx.10086.cn/Z3A7NFb 【中国移动】";              	
        	}else if("1".equals(newWlanDiscntLevel)){
                notieContet = "尊敬的客户您好，您可发送KTWLANRZTC至10086订购WLAN日租卡套餐，每天1元不使用不收费，每月WLAN使用100GB后将暂时关闭WLAN功能，下月自动恢复。您可在手机系统中配置CMCC热点的自动认证功能，首次配置成功后，无需输入账号和密码，即可完成自动认证登录，享受更加便捷的互联网服务。配置方法请见链接：http://dx.10086.cn/Z3A7NFb 【中国移动】";              	
        	}      
        	if(StringUtils.isNotBlank(notieContet)){
                IData smsData = getCommonSmsInfo(btd, new DataMap());
                smsData.put("NOTICE_CONTENT", notieContet);
                insTradeSMS(btd, smsData);         		
        	}       	
        }	
		
	}
	
	@SuppressWarnings("unchecked")
	private void cancelOldoldWlanDiscnt(BusiTradeData btd,DiscntTradeData oldDiscntTradeData) throws Exception {
		UcaData uca = btd.getRD().getUca();
		DiscntTradeData discntTradeData = new DiscntTradeData(oldDiscntTradeData.toData());
        discntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        discntTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());
        discntTradeData.setRemark("用户升降档自动退订附属于原档次的WLAN套餐！");
        btd.add(uca.getSerialNumber(), discntTradeData);
        
        //退订wlan资费，拼wlan平台服务、属性台账
        String bindServiceId = "98002401";
        List<PlatSvcTradeData> platSvcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
        for(PlatSvcTradeData platSvcTradeData : platSvcTradeDatas){
        	if(bindServiceId.equals(platSvcTradeData.getElementId()) && "2".equals(platSvcTradeData.getModifyTag())){
        		return;//如果在自动绑定0元权益处理类中已处理了，则返回
        	}
        }

        List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcByServiceId(bindServiceId);
        PlatSvcTradeData platSvcTradeData = userPlatSvcs.get(0);
        platSvcTradeData.setOperCode(PlatConstants.OPER_CANCEL_TC);
        platSvcTradeData.setOprSource("08");
        platSvcTradeData.setIsNeedPf("1");
        platSvcTradeData.setModifyTag("2");
        btd.add(uca.getSerialNumber(), platSvcTradeData);
        
        List<AttrTradeData> attrTradeDatas = uca.getUserAttrs();
		IDataset discntConfigList = CommparaInfoQry.getCommparaInfoByCode2("CSM", "3700", bindServiceId,oldDiscntTradeData.getDiscntCode(),"ZZZZ");
        String attrCode = discntConfigList.first().getString("PARA_CODE3");
        String attrValue = discntConfigList.first().getString("PARA_CODE1");
        for(AttrTradeData attrTradeData : attrTradeDatas){
        	if(attrTradeData.getAttrCode().equals(attrCode) || attrTradeData.getAttrValue().equals(attrValue)){
        		attrTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        		attrTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());
        		btd.add(uca.getSerialNumber(), attrTradeData); 
        		break;
        	}
        } 
        
	}

	@SuppressWarnings("rawtypes")
    protected IData getCommonSmsInfo(BusiTradeData btd, IData smsInfo) throws Exception
    {
        IData smsData = new DataMap();

        smsData.put("TRADE_ID", btd.getTradeId());
        String specSn = smsInfo.getString("SPEC_SERIAL_NUMBER", "");
        smsData.put("RECV_OBJECT", specSn == "" ? btd.getRD().getUca().getSerialNumber() : specSn);
        String specUserId = smsInfo.getString("SPEC_USER_ID", "");
        smsData.put("RECV_ID", specUserId == "" ? btd.getRD().getUca().getUserId() : specUserId);
        smsData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        smsData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        smsData.put("SMS_PRIORITY", "50");
        smsData.put("CANCEL_TAG", btd.getMainTradeData().getCancelTag());
        smsData.put("REMARK", "业务短信通知");
        smsData.put("NOTICE_CONTENT_TYPE", smsInfo.getString("SMS_TYPE"));
        smsData.put("REVC4", smsInfo.getString("_TEMPLATE_ID", "").equals("") ? smsInfo.getString("TEMPLATE_ID") : smsInfo.getString("_TEMPLATE_ID", ""));// 对应模板ID

        String tempLateId = smsInfo.getString("TEMPLATE_ID");
        if (StringUtils.isNotEmpty(tempLateId))
        {
            IData templateIds = TemplateBean.getTemplateInfoByPk(tempLateId);
            if (IDataUtil.isNotEmpty(templateIds))
            {
                smsData.put("SMS_TYPE_CODE", templateIds.getString("TEMPLATE_TYPE"));
                smsData.put("SMS_KIND_CODE", templateIds.getString("TEMPLATE_KIND"));
                smsData.put("SMS_PRIORITY", templateIds.getString("SMS_PRIORITY"));
            }
        }
        return smsData;
    }

    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    public static void insTradeSMS(BusiTradeData btd, IData smsData) throws Exception
    {
        String sysdate = btd.getRD().getAcceptTime();

        SmsTradeData std = new SmsTradeData();
        std.setSmsNoticeId(smsData.getString("SMS_NOTICE_ID", SeqMgr.getSmsSendId()));
        std.setEparchyCode(smsData.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode()));
        std.setBrandCode(smsData.getString("BRAND_CODE"));
        std.setInModeCode(CSBizBean.getVisit().getInModeCode());
        std.setSmsNetTag(smsData.getString("SMS_NET_TAG", "0"));
        std.setChanId(smsData.getString("CHAN_ID", "11"));
        std.setSendObjectCode(smsData.getString("SEND_OBJECT_CODE", "6"));
        std.setSendTimeCode(smsData.getString("SEND_TIME_CODE", "1"));
        std.setSendCountCode(smsData.getString("SEND_COUNT_CODE", "1"));
        std.setRecvObjectType(smsData.getString("RECV_OBJECT_TYPE", "00"));
        std.setRecvObject(smsData.getString("RECV_OBJECT"));
        std.setRecvId(smsData.getString("RECV_ID", "-1"));
        // std.setSmsTypeCode(smsData.getString("SMS_TYPE_CODE", "20"));
        std.setSmsTypeCode(smsData.getString("SMS_TYPE_CODE", "I0"));
        std.setSmsKindCode(smsData.getString("SMS_KIND_CODE", "02"));
        std.setNoticeContentType(smsData.getString("NOTICE_CONTENT_TYPE", "0"));
        std.setReferedCount(smsData.getString("REFERED_COUNT", "0"));
        std.setForceReferCount(smsData.getString("FORCE_REFER_COUNT", "1"));
        std.setForceObject(smsData.getString("FORCE_OBJECT"));
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
        std.setRevc3(smsData.getString("REVC3"));
        std.setRevc4(smsData.getString("REVC4"));
        std.setMonth(sysdate.substring(5, 7));
        std.setDay(sysdate.substring(8, 10));
        std.setCancelTag(smsData.getString("CANCEL_TAG"));

        // 短信截取
        String content = smsData.getString("NOTICE_CONTENT", "");
        int charLength = SmsSend.getCharLength(content, 4000);
        content = content.substring(0, charLength);
        std.setNoticeContent(content);

        btd.add(btd.getRD().getUca().getUser().getSerialNumber(), std);
    }
}
