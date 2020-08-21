package com.asiainfo.veris.crm.order.soa.person.busi.widerealnamesupplement;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class wideRealnameSupplementRegSVC extends OrderService
{
	 @Override
	 public String getOrderTypeCode() throws Exception
	 {
		return this.input.getString("TRADE_TYPE_CODE", "826");
	 }
	
	 @Override
	 public String getTradeTypeCode() throws Exception
	 {
		 return this.input.getString("TRADE_TYPE_CODE", "826");
	 }
}