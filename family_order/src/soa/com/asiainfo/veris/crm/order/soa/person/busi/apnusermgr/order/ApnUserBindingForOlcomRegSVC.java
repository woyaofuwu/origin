
package com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;


public class ApnUserBindingForOlcomRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "1155";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "1155";
    }

}
