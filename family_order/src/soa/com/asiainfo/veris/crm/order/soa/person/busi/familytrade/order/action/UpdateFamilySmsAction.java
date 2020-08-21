
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

public class UpdateFamilySmsAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	String serialNumber = "";
    	IData smsData = new DataMap(); // 短信数据
        smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
        smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空
        
        MainTradeData mainTD = btd.getMainTradeData();
        String strTdc = mainTD.getTradeTypeCode();
        
        //区分亲亲网升级业务，立即生效与下账期生效标识
        String strupdateNowCheckBox = btd.getRD().getPageRequestData().getString("updateNowCheckBox", "");
        
    	List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        if (discntTradeDatas != null && discntTradeDatas.size() > 0)
        {
            for (Object obj : discntTradeDatas)
            {
                DiscntTradeData dtd = (DiscntTradeData) obj;
                IData smsUser = UcaInfoQry.qryUserInfoByUserId(dtd.getUserId());
                if(smsUser!=null){
                	serialNumber = smsUser.getString("SERIAL_NUMBER","");
                }
                if ("3410".equals(dtd.getDiscntCode()) && (BofConst.MODIFY_TAG_ADD.equals(dtd.getModifyTag())))
                {
                	 
                     if( "399".equals(strTdc) ){
                    	 String templateId = "CRM_SMS_PER_COMM_3412";
                    	 IData templateInfo = TemplateQry.qryTemplateContentByTempateId(templateId);
                         String smsContent = templateInfo.getString("TEMPLATE_CONTENT1", "");
                         
                         //String sysSD = SysDateMgr.transTime(dtd.getStartDate());
                         if( "".equals(strupdateNowCheckBox) || !"true".equals(strupdateNowCheckBox) ){
                        	 smsContent = smsContent + "，下月1日生效。";
                         }else{
                        	 smsContent = smsContent + "，24小时内生效。";
                         }
                    	 
                    	 if(!smsContent.equals("")){
                     		smsData.put("FORCE_OBJECT", "10086");// 发送对象
                             smsData.put("RECV_OBJECT", serialNumber);// 接收对象
                             smsData.put("NOTICE_CONTENT", smsContent);// 短信内容
                             PerSmsAction.insTradeSMS(btd, smsData);
                     	}
                     }else{
                    	 String templateId = "CRM_SMS_PER_COMM_3410";
                    	 IData templateInfo = TemplateQry.qryTemplateContentByTempateId(templateId);
                         String smsContent = templateInfo.getString("TEMPLATE_CONTENT1", "");
                         
                    	 smsContent = smsContent + "，24小时内生效。";
                    	 
                    	 if(!smsContent.equals("")){
                     		smsData.put("FORCE_OBJECT", "10086");// 发送对象
                             smsData.put("RECV_OBJECT", serialNumber);// 接收对象
                             smsData.put("NOTICE_CONTENT", smsContent);// 短信内容
                             PerSmsAction.insTradeSMS(btd, smsData);
                     	}
                     }
                }
                if ("3411".equals(dtd.getDiscntCode()) && (BofConst.MODIFY_TAG_ADD.equals(dtd.getModifyTag())))
                {
					 if( "399".equals(strTdc) ){
						 String templateId = "CRM_SMS_PER_COMM_3413";
						 IData templateInfo = TemplateQry.qryTemplateContentByTempateId(templateId);
						 String smsContent = templateInfo.getString("TEMPLATE_CONTENT1", "");
						
						 //String sysSD = SysDateMgr.transTime(dtd.getStartDate());
                         if( "".equals(strupdateNowCheckBox) || !"true".equals(strupdateNowCheckBox) ){
                        	 smsContent = smsContent + "，下月1日生效。";
                         }else{
                        	 smsContent = smsContent + "，24小时内生效。";
                         }
						 
						 if(!smsContent.equals("")){
	                		 smsData.put("FORCE_OBJECT", "10086");// 发送对象
	                         smsData.put("RECV_OBJECT", serialNumber);// 接收对象
	                         smsData.put("NOTICE_CONTENT", smsContent);// 短信内容
	                         PerSmsAction.insTradeSMS(btd, smsData);
	                	}
					 }else{
						 String templateId = "CRM_SMS_PER_COMM_3411";
						 IData templateInfo = TemplateQry.qryTemplateContentByTempateId(templateId);
						 String smsContent = templateInfo.getString("TEMPLATE_CONTENT1", "");
						
						 String sysNow = SysDateMgr.getSysTime();
						 String sysSD = dtd.getStartDate();
						 //String sysdate = SysDateMgr.transTime(dtd.getStartDate());
						 int nCount = sysSD.compareTo(sysNow);
						 if (nCount > 0) {
							 smsContent = smsContent + "，下月1日生效。";
						 }else{
							 smsContent = smsContent + "，24小时内生效。";
						 }
						 
						 if(!smsContent.equals("")){
	                		 smsData.put("FORCE_OBJECT", "10086");// 发送对象
	                         smsData.put("RECV_OBJECT", serialNumber);// 接收对象
	                         smsData.put("NOTICE_CONTENT", smsContent);// 短信内容
	                         PerSmsAction.insTradeSMS(btd, smsData);
	                	}
					 }
                	
                }
            }
        }
        
    }

}
