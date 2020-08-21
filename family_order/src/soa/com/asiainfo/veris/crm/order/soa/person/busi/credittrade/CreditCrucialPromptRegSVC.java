
package com.asiainfo.veris.crm.order.soa.person.busi.credittrade;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class CreditCrucialPromptRegSVC extends OrderService
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

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        String tradeTypeCode = btd.getTradeTypeCode();
        if (StringUtils.equals(tradeTypeCode, "7506") // 关键时刻缴费提醒
                || StringUtils.equals(tradeTypeCode, "7507") // 关键时刻GPRS流量上限提醒
                || StringUtils.equals(tradeTypeCode, "7508"))
        {
            orderData.setSubscribeType("200");
        }
    }
}
