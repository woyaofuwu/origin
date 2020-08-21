
package com.asiainfo.veris.crm.order.soa.person.common.action.sms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;


/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: PerSatisfySmsFinishAction.java
 * @Description: 
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014年8月26日 下午8:27:56 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014年8月26日 lijm3 v1.0.0 修改原因
 */

public class AutoBookWNSmsAction implements ITradeAction
{

	@SuppressWarnings(
		    { "rawtypes", "unchecked" })
    public void executeAction(BusiTradeData btd) throws Exception
    {
		MainTradeData mainTrade = btd.getMainTradeData();
        String tradeId = btd.getRD().getTradeId();
        

        String tradeTypeCode = mainTrade.getTradeTypeCode();
        String serialNumber = mainTrade.getSerialNumber();
        String cancelTag = mainTrade.getCancelTag();
        String inModeCode = mainTrade.getInModeCode();
    	String product_id=mainTrade.getRsrvStr1();
    	String package_id=mainTrade.getRsrvStr2();
      

    	//宽带1+营销活动自动续约,短信下发
        String auto_book = (String)btd.getRD().getPageRequestData().getString("AUTO_BOOK","");
        if("1".equals(auto_book) && ("240".equals(tradeTypeCode)&& ("69908001".equals(product_id) || "69908012".equals(product_id) || "69908015".equals(product_id))))
        {
        	String forceObject = "10086";
            
        	String old_product_id = (String)btd.getRD().getPageRequestData().getString("OLD_PRODUCT_ID","");
        	String old_package_id = (String)btd.getRD().getPageRequestData().getString("OLD_PACKAGE_ID","");
        	IDataset tempInfos = new DatasetList();
        	if((!"".equals(old_product_id)) && (!"".equals(old_package_id)))
        	{
        		tempInfos = BreQryForCommparaOrTag.getCommpara("CSM", 956, old_product_id, old_package_id, "0898");
        	}else
        	{
        		tempInfos = BreQryForCommparaOrTag.getCommpara("CSM", 956, product_id, package_id, "0898");
        	}
        	
        	String smsContent = "";
        	if ( tempInfos != null && tempInfos.size() > 0)
			{
        		IData tempInfo = new DataMap();
        		tempInfo = (IData)tempInfos.get(0);
        		smsContent = tempInfo.getString("PARA_CODE20") + tempInfo.getString("PARA_CODE21");
			}

            String sysdate = btd.getRD().getAcceptTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, 1);
            cal.set(Calendar.HOUR_OF_DAY, 10);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
			String SendTime = sdf.format(cal.getTime());//取当前时间
			
			
            SmsTradeData std = new SmsTradeData();
            std.setSmsNoticeId(SeqMgr.getSmsSendId());
            std.setEparchyCode(mainTrade.getEparchyCode());
            std.setBrandCode(mainTrade.getBrandCode());
            std.setInModeCode(CSBizBean.getVisit().getInModeCode());
            std.setSmsNetTag("0");
            std.setChanId("11");
            std.setSendObjectCode("6");
            std.setSendTimeCode("1");
            std.setSendCountCode("1");
            std.setRecvObjectType("00");
            std.setRecvObject(serialNumber);
            std.setRecvId(mainTrade.getUserId());
            // std.setSmsTypeCode(smsData.getString("SMS_TYPE_CODE", "20"));
            std.setSmsTypeCode("I0");
            std.setSmsKindCode("0");
            std.setNoticeContentType("0");
            std.setReferedCount("0");
            std.setForceReferCount("1");
            std.setForceObject(forceObject);
            std.setForceStartTime(SendTime);
            std.setForceEndTime("");
            std.setSmsPriority("50");
            std.setReferTime(sysdate);
            std.setReferDepartId(CSBizBean.getVisit().getDepartId());
            std.setReferStaffId(CSBizBean.getVisit().getStaffId());
            std.setDealTime(sysdate);
            std.setDealStaffid(CSBizBean.getVisit().getStaffId());
            std.setDealDepartid(CSBizBean.getVisit().getDepartId());
            std.setDealState("0");// 处理状态，0：未处理
            std.setRemark("宽带1+自动续约提醒短信下发");
            std.setRevc1("");
            std.setRevc2("");
            std.setRevc3("");
            std.setRevc4("");
            std.setMonth(sysdate.substring(5, 7));
            std.setDay(sysdate.substring(8, 10));
            std.setCancelTag(cancelTag);

            // 短信截取
            int charLength = SmsSend.getCharLength(smsContent, 4000);
            smsContent = smsContent.substring(0, charLength);
            std.setNoticeContent(smsContent);

            btd.add(btd.getRD().getUca().getUser().getSerialNumber(), std);
            
        }
    }

}
