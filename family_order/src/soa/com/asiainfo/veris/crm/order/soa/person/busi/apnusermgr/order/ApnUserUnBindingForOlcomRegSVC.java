
package com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;


public class ApnUserUnBindingForOlcomRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "1156";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "1156";
    }

}
