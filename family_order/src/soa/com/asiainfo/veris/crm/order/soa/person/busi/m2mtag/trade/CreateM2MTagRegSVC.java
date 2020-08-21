package com.asiainfo.veris.crm.order.soa.person.busi.m2mtag.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class CreateM2MTagRegSVC extends OrderService
{
    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "7814");
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "7814");
    }
}
