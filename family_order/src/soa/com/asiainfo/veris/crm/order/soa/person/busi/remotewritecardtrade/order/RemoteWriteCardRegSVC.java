
package com.asiainfo.veris.crm.order.soa.person.busi.remotewritecardtrade.order;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class RemoteWriteCardRegSVC extends OrderService
{

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "149");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "149");
    }
    
	public void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
	{
	}
}
