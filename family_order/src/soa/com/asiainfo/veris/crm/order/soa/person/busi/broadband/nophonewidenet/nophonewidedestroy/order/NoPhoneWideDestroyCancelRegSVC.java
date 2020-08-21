package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidedestroy.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class NoPhoneWideDestroyCancelRegSVC extends OrderService
{

    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "1686");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "1686");
    }

    public IDataset tradeReg(IData input) throws Exception
    {
        if (!"KD_".equals(input.getString("SERIAL_NUMBER").substring(0, 3)))
        {
            input.put("SERIAL_NUMBER", "KD_" + input.getString("SERIAL_NUMBER"));
        }
        return super.tradeReg(input);
    }
}
