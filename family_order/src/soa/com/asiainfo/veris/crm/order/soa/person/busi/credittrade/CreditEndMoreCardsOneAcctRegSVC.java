
package com.asiainfo.veris.crm.order.soa.person.busi.credittrade;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class CreditEndMoreCardsOneAcctRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE");
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE");
    }

    @Override
    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        String tradeTypeCode = btd.getTradeTypeCode();
        if (StringUtils.equals("7325", tradeTypeCode))
        {
            orderData.setSubscribeType("200");
        }
    }
}
