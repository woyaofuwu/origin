package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetbox.order.action;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePlatSvcInfoQry;

public class ModifyNoPhoneTopSetBoxPlatsvcEndDateAction implements ITradeFinishAction{

	public void executeAction(IData mainTrade) throws Exception {
		String tradeId=mainTrade.getString("TRADE_ID");
		
		
		/*
		 * 修改平台服务的订购时间为订单完成时间
		 * 
		 */
		String startDate=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
		
		TradePlatSvcInfoQry.updatePlatsvcTradeStartDate(tradeId, startDate, mainTrade.getString("TRADE_EPARCHY_CODE", "0898"));
		
	}
	
}
