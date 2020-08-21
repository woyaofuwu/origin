
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class DealPcrfTacticsRegSVC extends OrderService
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return this.input.getString("TRADE_TYPE_CODE");
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return this.input.getString("TRADE_TYPE_CODE");
    }

}
