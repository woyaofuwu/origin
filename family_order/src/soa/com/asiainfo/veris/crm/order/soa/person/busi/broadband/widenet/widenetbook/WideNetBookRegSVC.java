
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetbook;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 
 * @author zyz
 *
 */
public class WideNetBookRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "6182";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "6182";
    }

}
