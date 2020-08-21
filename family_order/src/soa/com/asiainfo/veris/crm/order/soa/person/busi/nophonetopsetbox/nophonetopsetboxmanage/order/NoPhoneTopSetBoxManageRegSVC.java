package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxmanage.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class NoPhoneTopSetBoxManageRegSVC extends OrderService
{
	 public String getOrderTypeCode() throws Exception
	    {
	        return this.input.getString("ORDER_TYPE_CODE", "4909");
	    }

	    public String getTradeTypeCode() throws Exception
	    {
	        return this.input.getString("TRADE_TYPE_CODE", "4909");
	    }
}
