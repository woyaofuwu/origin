
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ReturnActiveExchangeRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "427";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "427";
    }

}
