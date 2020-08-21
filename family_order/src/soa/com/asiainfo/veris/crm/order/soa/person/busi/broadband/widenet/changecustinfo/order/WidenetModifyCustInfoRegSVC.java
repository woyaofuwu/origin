
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changecustinfo.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class WidenetModifyCustInfoRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {

        orderData.setSubscribeType("300");
    }

    /**
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset tradeReg(IData input) throws Exception
    {

        input.put("SERIAL_NUMBER", "KD_" + input.getString("SERIAL_NUMBER"));
        return super.tradeReg(input);
    }
}
