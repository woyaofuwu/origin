
package com.asiainfo.veris.crm.order.soa.person.busi.oceanfront;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;


/**
 * REQ201805160025_2018年海洋通业务办理开发需求
 * @author zhuoyingzhi
 * @date 20180604
 *
 */
public class OceanFrontRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return input.getString("ORDER_TYPE_CODE", input.getString("TRADE_TYPE_CODE", ""));
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return input.getString("TRADE_TYPE_CODE", "");
    }
}
