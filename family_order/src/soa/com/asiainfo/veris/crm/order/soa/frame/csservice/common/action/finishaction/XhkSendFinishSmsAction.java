
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * 学护卡订购完工，下发代付短信
 *
 * @author J2EE
 */
public class XhkSendFinishSmsAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        if(!"3744".equals(tradeTypeCode)){
            return;
        }

        String tradeId = mainTrade.getString("TRADE_ID");
        String intfId = mainTrade.getString("INTF_ID");

        boolean existsPayRelation = StringUtils.indexOf(intfId, TradeTableEnum.TRADE_RELATION.getValue() + ",") > -1 ? true : false;
        
        String mainNum = "";
        String clientNum = "";
        
        String mainUserId = "";
        String clientUserId = "";
        
        String userIda = "";
        
        if (existsPayRelation) {
        	IDataset tradeRelationInfos = TradeRelaInfoQry.getTradeUURelaByTradeId(tradeId);
        	for (int i = 0; i < tradeRelationInfos.size(); i++) {
        		String relatoinTypeCode = tradeRelationInfos.getData(i).getString("RELATION_TYPE_CODE");
        		String roleCodeB = tradeRelationInfos.getData(i).getString("ROLE_CODE_B");
        		String serialNumber = tradeRelationInfos.getData(i).getString("SERIAL_NUMBER_B");
        		String userId = tradeRelationInfos.getData(i).getString("USER_ID_B");
        		String userIDAtemp = tradeRelationInfos.getData(i).getString("USER_ID_A");
        		
        		if ("56".equals(relatoinTypeCode)) {
        			userIda = userIDAtemp;
        			
        			if ("1".equals(roleCodeB)) {
        				mainNum = serialNumber;
        				mainUserId = userId;
        			} else if ("2".equals(roleCodeB)) {
        				clientNum = serialNumber;
        				clientUserId = userId;
        			}
        			
        		}
        	}
        	
        	if (!"".equals(clientNum)) {
        		if ("".equals(mainNum)) {
        			IDataset mainRelationInfo = RelaUUInfoQry.getRelationsByUserIdA("56", userIda, "1");
        			
        			if( IDataUtil.isNotEmpty(mainRelationInfo) ){
                		IData relamain = mainRelationInfo.getData(0);
                		mainNum = relamain.getString("SERIAL_NUMBER_B");
                		mainUserId = relamain.getString("USER_ID_B");
        			}
        		}
        		
        		String mainNoticeContent = "尊敬的客户，您已成功办理统一付费业务， 且已成功增加（" + clientNum + "）为您的统一付费业务的副号码，从本月起，您将为他(们)支付通信费用。";
    			
    			IData mainSmsData = new DataMap();
    			mainSmsData.put("RECV_OBJECT", mainNum);
    			mainSmsData.put("NOTICE_CONTENT", mainNoticeContent);
    			mainSmsData.put("BRAND_CODE", "ADCG");
    			mainSmsData.put("RECV_ID", mainUserId);
    	        SmsSend.insSms(mainSmsData);

    	        String noticeContent = "尊敬的客户：您已成为" + mainNum + "号码统一付费业务的副卡,从本月起,您的通信费用将由" + mainNum + "主卡统一支付.";;
    			
    			IData smsData = new DataMap();
    	        smsData.put("RECV_OBJECT", clientNum);
    	        smsData.put("NOTICE_CONTENT", noticeContent);
    	        smsData.put("BRAND_CODE", "ADCG");
    	        smsData.put("RECV_ID", clientUserId);
    	        SmsSend.insSms(smsData);
        	}
			
        }
    }
}
