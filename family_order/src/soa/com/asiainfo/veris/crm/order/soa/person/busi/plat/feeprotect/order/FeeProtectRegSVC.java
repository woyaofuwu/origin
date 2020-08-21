
package com.asiainfo.veris.crm.order.soa.person.busi.plat.feeprotect.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class FeeProtectRegSVC extends OrderService
{
	private static final long serialVersionUID = 1L;

	@Override
    public String getOrderTypeCode() throws Exception
    {
        return "3701";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "3701";
    }

}
