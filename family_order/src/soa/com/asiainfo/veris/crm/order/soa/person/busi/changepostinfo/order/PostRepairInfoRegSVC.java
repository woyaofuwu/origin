
package com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 邮寄资料补录91
 * 
 * @author liutt
 */
public class PostRepairInfoRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "91";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "91";
    }

}
