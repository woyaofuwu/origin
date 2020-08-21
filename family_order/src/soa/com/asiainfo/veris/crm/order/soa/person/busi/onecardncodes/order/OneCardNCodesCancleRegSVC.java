
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class OneCardNCodesCancleRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "322";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "322";
    }

}
