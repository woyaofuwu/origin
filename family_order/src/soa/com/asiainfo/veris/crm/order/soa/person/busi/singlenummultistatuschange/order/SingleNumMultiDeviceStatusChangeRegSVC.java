
package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange.order;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class SingleNumMultiDeviceStatusChangeRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "397";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "397";
    }

    @Override
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
	{

	}
}
