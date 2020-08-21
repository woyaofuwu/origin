
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonechangeuser.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
 

public class NoPhoneWideChangeUserRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "690";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "690";
    }

}
