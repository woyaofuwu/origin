
package com.asiainfo.veris.crm.order.soa.person.busi.interroamday.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class InterRoamDayRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "300";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "300";
    }

}
