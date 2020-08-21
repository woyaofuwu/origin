
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.trunktrade.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ChangeTrunkIdRegSVC extends OrderService
{

    /**  
	 * 
	 * */

    private static final long serialVersionUID = 2235735578603480208L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", getTradeTypeCode());
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

}
