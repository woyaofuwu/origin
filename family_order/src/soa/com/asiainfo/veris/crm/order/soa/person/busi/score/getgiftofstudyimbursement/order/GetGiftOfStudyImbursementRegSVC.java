
package com.asiainfo.veris.crm.order.soa.person.busi.score.getgiftofstudyimbursement.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class GetGiftOfStudyImbursementRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "336";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "336";
    }

}
