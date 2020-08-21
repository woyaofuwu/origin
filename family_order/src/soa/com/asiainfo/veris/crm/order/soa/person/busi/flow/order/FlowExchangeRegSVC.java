
package com.asiainfo.veris.crm.order.soa.person.busi.flow.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;


public class FlowExchangeRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "667";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "667";
    }

}
