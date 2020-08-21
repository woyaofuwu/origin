
package com.asiainfo.veris.crm.order.soa.person.busi.tempphonereturn.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class PhoneReturnRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "");
    }
}
