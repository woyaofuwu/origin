
package com.asiainfo.veris.crm.order.soa.person.busi.firstcall.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class OtnMailRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
        return "120";
    }

    public String getTradeTypeCode() throws Exception
    {
        return "120";
    }
}
