
package com.asiainfo.veris.crm.order.soa.person.busi.coupons.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class CouponsRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "6222";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "6222";
    }

}
