
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class OneCardNCodesSaleRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "320";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "320";
    }

}
