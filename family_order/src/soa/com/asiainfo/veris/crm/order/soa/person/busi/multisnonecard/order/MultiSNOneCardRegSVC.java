
package com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class MultiSNOneCardRegSVC extends OrderService
{

    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
        return "375";
    }

    public String getTradeTypeCode() throws Exception
    {
        return "375";
    }

}
