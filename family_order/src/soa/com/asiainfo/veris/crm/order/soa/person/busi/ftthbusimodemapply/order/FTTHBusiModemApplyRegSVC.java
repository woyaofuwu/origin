
package com.asiainfo.veris.crm.order.soa.person.busi.ftthbusimodemapply.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class FTTHBusiModemApplyRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "6132";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "6132";
    }

}
