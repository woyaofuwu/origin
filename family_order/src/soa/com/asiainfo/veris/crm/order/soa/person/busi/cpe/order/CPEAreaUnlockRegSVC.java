
package com.asiainfo.veris.crm.order.soa.person.busi.cpe.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class CPEAreaUnlockRegSVC extends OrderService
{
	/**
	 * 小区解锁
	 * */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "698";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "698";
    }

}