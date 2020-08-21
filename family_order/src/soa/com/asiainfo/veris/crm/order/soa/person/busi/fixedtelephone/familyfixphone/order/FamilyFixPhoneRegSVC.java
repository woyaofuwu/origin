
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.familyfixphone.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/*
 * 家庭固话
 */
public class FamilyFixPhoneRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "9601";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "9601";
    }

}
