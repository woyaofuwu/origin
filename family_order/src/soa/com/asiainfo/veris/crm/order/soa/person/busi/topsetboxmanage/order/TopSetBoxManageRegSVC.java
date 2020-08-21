package com.asiainfo.veris.crm.order.soa.person.busi.topsetboxmanage.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class TopSetBoxManageRegSVC extends OrderService
{
	 public String getOrderTypeCode() throws Exception
	    {
	        return this.input.getString("ORDER_TYPE_CODE", "3910");
	    }

	    public String getTradeTypeCode() throws Exception
	    {
	        return this.input.getString("TRADE_TYPE_CODE", "3910");
	    }
}
