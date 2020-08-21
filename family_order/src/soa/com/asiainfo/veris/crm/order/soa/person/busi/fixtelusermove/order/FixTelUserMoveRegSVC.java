
package com.asiainfo.veris.crm.order.soa.person.busi.fixtelusermove.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class FixTelUserMoveRegSVC extends OrderService
{

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "9703");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "9703");
    }
}
