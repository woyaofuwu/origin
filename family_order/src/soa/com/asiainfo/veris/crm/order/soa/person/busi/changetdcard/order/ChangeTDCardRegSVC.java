
package com.asiainfo.veris.crm.order.soa.person.busi.changetdcard.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ChangeTDCardRegSVC extends OrderService
{
    public String getOrderTypeCode() throws Exception
    {
        return "3821";
    }

    public String getTradeTypeCode() throws Exception
    {
        return "3821";
    }
}
