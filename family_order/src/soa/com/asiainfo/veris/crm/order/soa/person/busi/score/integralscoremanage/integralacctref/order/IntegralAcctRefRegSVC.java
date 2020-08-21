
package com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.integralacctref.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class IntegralAcctRefRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "544";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "544";
    }

}
