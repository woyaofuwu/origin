package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class DelGroupNetMemberRegSVC extends OrderService{

	@Override
	public String getOrderTypeCode() throws Exception {
		
		return "384";
	}

	@Override
	public String getTradeTypeCode() throws Exception {
		
		return "384";
	}

}
