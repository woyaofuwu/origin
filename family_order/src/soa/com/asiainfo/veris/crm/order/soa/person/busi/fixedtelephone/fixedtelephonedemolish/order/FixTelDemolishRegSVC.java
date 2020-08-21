
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.fixedtelephonedemolish.order;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class FixTelDemolishRegSVC extends OrderService
{

    /**
     * 固话拆机 TRADE_TYPE_CODE:9705
     **/

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "9705");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "9705");
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        String tradeTypeCode = btd.getTradeTypeCode();
        if (StringUtils.equals(tradeTypeCode, "7804"))
        {
            orderData.setSubscribeType("200");
        }
    }
}
