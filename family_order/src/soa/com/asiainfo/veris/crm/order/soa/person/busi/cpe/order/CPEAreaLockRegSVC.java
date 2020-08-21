
package com.asiainfo.veris.crm.order.soa.person.busi.cpe.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class CPEAreaLockRegSVC extends OrderService
{
	/**
	 * 小区锁定
	 * */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "697";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "697";
    }

}
