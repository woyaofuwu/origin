
package com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 移动E信90
 * 
 * @author liutt
 */
public class ModifyPostInfoRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "90";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "90";
    }

}
