package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ScoreDonateZCSVC extends OrderService{
	
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "341";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "341";
    }

}
