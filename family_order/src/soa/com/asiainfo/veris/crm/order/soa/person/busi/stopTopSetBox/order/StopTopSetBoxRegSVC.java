package com.asiainfo.veris.crm.order.soa.person.busi.stopTopSetBox.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class StopTopSetBoxRegSVC extends OrderService{

	public String getOrderTypeCode() throws Exception
    {
        return input.getString("ORDER_TYPE_CODE", "3900");
    }

    public String getTradeTypeCode() throws Exception
    {
        return input.getString("TRADE_TYPE_CODE", "3900");
    }

}
