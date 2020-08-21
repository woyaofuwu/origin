package com.asiainfo.veris.crm.order.soa.person.busi.evaluecard.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;


public class EValueCardRegSVC extends OrderService {

	@Override
	public String getOrderTypeCode() throws Exception {
		
		return "5333";
	}

	@Override
	public String getTradeTypeCode() throws Exception {
		return "5333";
	}

}
