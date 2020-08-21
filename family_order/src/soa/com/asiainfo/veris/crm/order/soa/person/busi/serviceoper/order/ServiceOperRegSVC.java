
package com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ServiceOperRegSVC extends OrderService
{
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return this.input.getString("TRADE_TYPE_CODE", "130");
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return this.input.getString("TRADE_TYPE_CODE", "130");
    }

}
