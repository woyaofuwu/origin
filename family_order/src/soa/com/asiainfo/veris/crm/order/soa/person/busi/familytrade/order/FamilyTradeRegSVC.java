
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class FamilyTradeRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "250";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "250";
    }

}
