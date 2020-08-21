
package com.asiainfo.veris.crm.order.soa.person.busi.pcc.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class PCCRelieveRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "6980";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "6980";
    }

}