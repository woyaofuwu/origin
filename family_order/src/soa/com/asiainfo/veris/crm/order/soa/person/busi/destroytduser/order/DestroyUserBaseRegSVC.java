
package com.asiainfo.veris.crm.order.soa.person.busi.destroytduser.order;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class DestroyUserBaseRegSVC extends OrderService
{

    private static final long serialVersionUID = 1L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    /**
     * 修改主订单的SubscribeType 字段 200-信控执行，201-信控开机
     */
    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd)
    {
        String tradeTypeCode = orderData.getTradeTypeCode();
        if (StringUtils.equals("7230", tradeTypeCode))
        {
            orderData.setSubscribeType("200");
        }
    }

}
