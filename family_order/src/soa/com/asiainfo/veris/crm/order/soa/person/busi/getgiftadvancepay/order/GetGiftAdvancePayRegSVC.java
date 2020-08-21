
package com.asiainfo.veris.crm.order.soa.person.busi.getgiftadvancepay.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class GetGiftAdvancePayRegSVC extends OrderService
{

    private static final long serialVersionUID = 1L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "346";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "346";
    }
}
