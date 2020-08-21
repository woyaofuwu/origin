
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class WideUserCreateRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
    	if (this.input.getString("TRADE_TYPE_CODE", "").equals(""))
        {
	    	if(!input.getString("WIDE_TYPE","").equals(""))
	    	{
		    	if(input.getString("WIDE_TYPE").equals("1"))//GPON
		    	{
		        	input.put("TRADE_TYPE_CODE", "600");
		    	}
		        else if(input.getString("WIDE_TYPE").equals("2"))//ADSL
		        {
		        	input.put("TRADE_TYPE_CODE", "612");
		        }
		        else if(input.getString("WIDE_TYPE").equals("3")) //FTTH
		        {
		        	input.put("TRADE_TYPE_CODE", "613");
		        }
		        else if(input.getString("WIDE_TYPE").equals("4")) //校园
		        {
		        	input.put("TRADE_TYPE_CODE", "630");
		        }
	    	}
        }
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public String getTradeTypeCode() throws Exception
    {
    	if (this.input.getString("TRADE_TYPE_CODE", "").equals(""))
        {
	    	if(!this.input.getString("WIDE_TYPE","").equals(""))
	    	{
		    	if(input.getString("WIDE_TYPE").equals("1"))//GPON
		    	{
		    		input.put("TRADE_TYPE_CODE", "600");
		    	}
		        else if(input.getString("WIDE_TYPE").equals("2"))//ADSL
		        {
		        	input.put("TRADE_TYPE_CODE", "612");
		        }
		        else if(input.getString("WIDE_TYPE").equals("3")) //FTTH
		        {
		        	input.put("TRADE_TYPE_CODE", "613");
		        }
		        else if(input.getString("WIDE_TYPE").equals("4")) //校园
		        {
		        	input.put("TRADE_TYPE_CODE", "630");
		        }
	    	}
        }
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {

        orderData.setSubscribeType("300");
    }
}
