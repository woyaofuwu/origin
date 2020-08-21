
package com.asiainfo.veris.crm.order.soa.person.busi.vipserver.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class AirportVipServerRegSVC extends OrderService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -4051871573792678756L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "360";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "360";
    }

}
