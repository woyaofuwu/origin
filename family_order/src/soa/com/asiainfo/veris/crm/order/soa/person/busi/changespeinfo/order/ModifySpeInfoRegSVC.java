
package com.asiainfo.veris.crm.order.soa.person.busi.changespeinfo.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 特殊资料变更登记
 * 
 * @author liutt
 */
public class ModifySpeInfoRegSVC extends OrderService
{
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "72";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "72";
    }

}
