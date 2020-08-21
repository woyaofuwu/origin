
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class DestroyFamilyRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "288";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "288";
    }

}
