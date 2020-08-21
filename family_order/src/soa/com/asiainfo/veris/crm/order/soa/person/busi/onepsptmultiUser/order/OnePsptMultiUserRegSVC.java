package com.asiainfo.veris.crm.order.soa.person.busi.onepsptmultiUser.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class OnePsptMultiUserRegSVC extends OrderService
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6717612746197353309L;

	public String getOrderTypeCode() throws Exception
    {
        return "4912";
    }

    public String getTradeTypeCode() throws Exception
    {
        return "4912";
    }

}
