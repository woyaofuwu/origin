package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class NoPhoneModemLoseRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "6804";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "6804";
    }

}
