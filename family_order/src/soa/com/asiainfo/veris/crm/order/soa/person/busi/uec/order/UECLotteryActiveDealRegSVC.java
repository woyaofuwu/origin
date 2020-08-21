
package com.asiainfo.veris.crm.order.soa.person.busi.uec.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class UECLotteryActiveDealRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "306";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "306";
    }

}
