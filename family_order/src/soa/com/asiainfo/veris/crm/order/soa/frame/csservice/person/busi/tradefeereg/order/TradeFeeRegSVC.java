
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.tradefeereg.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class TradeFeeRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
        return "8006";
    }

    public String getTradeTypeCode() throws Exception
    {
        return "8006";
    }

}
