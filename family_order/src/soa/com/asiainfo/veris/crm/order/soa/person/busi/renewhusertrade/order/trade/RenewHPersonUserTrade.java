package com.asiainfo.veris.crm.order.soa.person.busi.renewhusertrade.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.trade.ChangeProductTrade;

public class RenewHPersonUserTrade extends ChangeProductTrade implements ITrade {
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		super.createBusiTradeData(btd);
	}
}
