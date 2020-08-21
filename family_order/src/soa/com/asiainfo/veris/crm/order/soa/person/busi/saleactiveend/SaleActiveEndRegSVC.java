
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class SaleActiveEndRegSVC extends OrderService
{
    private static final long serialVersionUID = 7980201543651978058L;

    public String getOrderTypeCode() throws Exception
    {
        return input.getString("ORDER_TYPE_CODE", "237");
    }

    public String getTradeTypeCode() throws Exception
    {
        return input.getString("TRADE_TYPE_CODE", "237");
    }
}
