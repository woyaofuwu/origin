
package com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 手机实名制预登记
 * 
 * @author liutt
 */
public class RealNameRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "62";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "62";
    }

}
