
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ReOpenGPRSRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "130";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "130";
    }

}
