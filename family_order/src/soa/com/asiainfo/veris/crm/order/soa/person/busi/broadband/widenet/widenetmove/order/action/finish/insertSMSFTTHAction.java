
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * 3、提速100M到期前，如果用户进行了宽带套餐变更（无论宽带是变更成50M、100M还是其他带宽），则按用户变更的套餐生效，同时提速活动结束，下发短信提醒用户活动提速的100M已终止，
 * 内容如下：尊敬的用户，你参加的FTTH迁移活动提速至100M已结束，您所使用的宽带业务以您最新办理的宽带套餐为准，感谢您的支持。中国移动。
 */
public class insertSMSFTTHAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String serial_number = mainTrade.getString("SERIAL_NUMBER");
        String user_id = mainTrade.getString("USER_ID");
        String tradeId = mainTrade.getString("TRADE_ID");
        String inModeCode = mainTrade.getString("IN_MODE_CODE");
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        String cancelTag = mainTrade.getString("CANCEL_TAG");
		
		IDataset prodChgInfo = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
        if(prodChgInfo!=null&&prodChgInfo.size()>0){
        	IDataset widenetMoveList = WidenetInfoQry.getWindenetMove(serial_number,user_id);
        	if(widenetMoveList!=null&&widenetMoveList.size()>0){
        		for (int i = 0; i < widenetMoveList.size(); i++) {
            		IData widenetMove = widenetMoveList.getData(i);
            		String userId = widenetMove.getString("USER_ID");
            		String serialNumber = widenetMove.getString("SERIAL_NUMBER");
            		widenetMove.put("SERIAL_NUMBER", serialNumber);
            		widenetMove.put("USER_ID", userId);
        	        widenetMove.put("RSRV_STR1", "C");
        	        Dao.update("TF_F_USER_WIDENET_MOVE", widenetMove, new String[] { "SERIAL_NUMBER","USER_ID" });
        		}
        		
        		if(serial_number.startsWith("KD_")){
            		serial_number = serial_number.substring(3);
            	}
            	IData smsData = new DataMap();
    		        smsData.put("TRADE_ID", tradeId);
    		        smsData.put("EPARCHY_CODE", eparchyCode);
    		        smsData.put("IN_MODE_CODE", inModeCode);
    		        smsData.put("SMS_PRIORITY", "5000");
    		        smsData.put("CANCEL_TAG", cancelTag);
    		        smsData.put("REMARK", "业务短信通知");
    		        smsData.put("NOTICE_CONTENT_TYPE", "0");
    		        smsData.put("SMS_TYPE_CODE", "20");
    		        smsData.put("RECV_OBJECT", serial_number);
    		        smsData.put("RECV_ID", user_id);
    		        smsData.put("FORCE_OBJECT", serial_number);
    		        smsData.put("NOTICE_CONTENT", "尊敬的用户，你参加的FTTH迁移活动提速至100M已结束，您所使用的宽带业务以您最新办理的宽带套餐为准，感谢您的支持。中国移动。");
    		        SmsSend.insSms(smsData);
        	}
        }
        
    }
}
