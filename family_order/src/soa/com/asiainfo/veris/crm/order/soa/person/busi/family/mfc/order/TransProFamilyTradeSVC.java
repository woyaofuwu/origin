package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class TransProFamilyTradeSVC extends OrderService
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getOrderTypeCode() throws Exception 
	{
		return this.input.getString("ORDER_TYPE_CODE", "2583");
	}
	
	@Override
	public String getTradeTypeCode() throws Exception 
	{
		return this.input.getString("TRADE_TYPE_CODE", "2583");
	}
	
}
