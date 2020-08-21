
package com.asiainfo.veris.crm.order.soa.person.busi.bank.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class BankBindDealRegSVC extends OrderService
{

    private static final long serialVersionUID = 1L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "511";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "511";
    }

}
