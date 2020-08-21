package com.asiainfo.veris.crm.order.soa.person.busi.jrbank.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class CreateSignBankRegSVC extends OrderService {

	@Override
	public String getOrderTypeCode() throws Exception {
		// TODO Auto-generated method stub
		return "1336";
	}

	@Override
	public String getTradeTypeCode() throws Exception {
		// TODO Auto-generated method stub
		return "1336";
	}

}
