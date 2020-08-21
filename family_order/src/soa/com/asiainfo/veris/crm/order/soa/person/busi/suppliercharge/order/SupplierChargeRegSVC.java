
package com.asiainfo.veris.crm.order.soa.person.busi.suppliercharge.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class SupplierChargeRegSVC extends OrderService
{

    private static final long serialVersionUID = 1L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "2108";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "2108";
    }

}
