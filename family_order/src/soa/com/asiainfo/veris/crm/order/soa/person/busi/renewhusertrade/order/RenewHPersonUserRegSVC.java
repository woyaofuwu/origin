package com.asiainfo.veris.crm.order.soa.person.busi.renewhusertrade.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class RenewHPersonUserRegSVC extends OrderService
{

    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "7511");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "7511");
    }

    public IDataset tradeReg(IData input) throws Exception
    {
        if (!"H".equals(input.getString("SERIAL_NUMBER").substring(0, 1)))
        {
            input.put("SERIAL_NUMBER", "H" + input.getString("SERIAL_NUMBER"));
        }
       
        return super.tradeReg(input);
    }
}
