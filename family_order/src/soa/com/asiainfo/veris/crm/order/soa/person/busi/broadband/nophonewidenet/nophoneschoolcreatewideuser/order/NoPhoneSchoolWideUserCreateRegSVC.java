
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophoneschoolcreatewideuser.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class NoPhoneSchoolWideUserCreateRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
    	if (this.input.getString("TRADE_TYPE_CODE", "").equals(""))
        {
    		input.put("TRADE_TYPE_CODE", "650");
        }
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public String getTradeTypeCode() throws Exception
    {
    	if (this.input.getString("TRADE_TYPE_CODE", "").equals(""))
        {
    		input.put("TRADE_TYPE_CODE", "650");
        }
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {

        orderData.setSubscribeType("300");
    }
}
