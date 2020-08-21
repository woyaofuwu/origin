
package com.asiainfo.veris.crm.order.soa.person.busi.changephone.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ChangePhonePreRegisterCancelTradeSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "803";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "803";
    }
}
