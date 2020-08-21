package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * BUG20190527100855 （立行立改）海南和多号订购扣费提醒问题
 * @author mqx
 * 20190528
 */
public class MOSPSmsFinishAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
	
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        IDataset trade_relation_info = TradeRelaInfoQry.getTradeRelaByTradeIdRelaType(tradeId, "M2");

        if (!DataUtils.isEmpty(trade_relation_info)) {
        	for (int i = 0, j = trade_relation_info.size(); i < j; i++) {
                IData relationTradeData = trade_relation_info.getData(i);
                if (!DataUtils.isEmpty(relationTradeData)) {
                	String serialNumberB = relationTradeData.getString("SERIAL_NUMBER_B");
                    String modify = relationTradeData.getString("MODIFY_TAG");
                    if (BofConst.MODIFY_TAG_ADD.equals(modify)) {//添加
                    	
                    	IDataset platSvcTradeData = TradePlatSvcInfoQry.getTradePlatSvcByTradeId(tradeId);
                    	if (!DataUtils.isEmpty(platSvcTradeData)) {
                        	String serviceId = platSvcTradeData.getData(0).getString("SERVICE_ID");
                        	IData offerData = UpcCall.queryOfferByOfferId("Z", serviceId);
                        	
                    		IData smsData = new DataMap();
                    		String smsContent = "尊敬的客户，您好！您已成功订购中国移动和多号副号"+serialNumberB+"，业务资费："+offerData.getString("OFFER_NAME")+"，立即生效。";
                    		
                    		smsData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                    		smsData.put("RECV_OBJECT", serialNumber);
                    		smsData.put("RECV_ID", userId);
                    		smsData.put("NOTICE_CONTENT", smsContent);
                    		smsData.put("REMARK", "和多号订购扣费提醒");
                    		SmsSend.insSms(smsData);
                    	}
                    }
                }
        	}
        }
	}
}
