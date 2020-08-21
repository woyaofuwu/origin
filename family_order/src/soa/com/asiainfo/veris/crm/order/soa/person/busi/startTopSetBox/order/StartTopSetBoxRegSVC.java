package com.asiainfo.veris.crm.order.soa.person.busi.startTopSetBox.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class StartTopSetBoxRegSVC extends OrderService{

	public String getOrderTypeCode() throws Exception
    {
        return input.getString("ORDER_TYPE_CODE", "3901");
    }

    public String getTradeTypeCode() throws Exception
    {
        return input.getString("TRADE_TYPE_CODE", "3901");
    }

}
