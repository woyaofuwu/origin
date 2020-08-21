package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class StopOpenMobileRegSVC extends OrderService {
    @Override
    public String getOrderTypeCode() throws Exception {
        return "9900";//跨区停复机
    }

    @Override
    public String getTradeTypeCode() throws Exception {
        return "9900";//跨区停复机
    }
}
