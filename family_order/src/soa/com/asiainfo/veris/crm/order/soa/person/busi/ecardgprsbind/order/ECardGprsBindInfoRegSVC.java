
package com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbind.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ECardGprsBindInfoRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "245";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "245";
    }

}
