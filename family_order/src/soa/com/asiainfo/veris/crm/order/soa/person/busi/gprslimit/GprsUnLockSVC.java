package com.asiainfo.veris.crm.order.soa.person.busi.gprslimit;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class GprsUnLockSVC extends OrderService{
	
	@Override
    public String getOrderTypeCode() throws Exception
    {
        return "798";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "798";
    }

}
