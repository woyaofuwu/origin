
package com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ProtectPassInfoRegSVC extends OrderService
{

    private static final long serialVersionUID = 1L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "990";
        // return PersonConst.TRADE_TYPE_CODE_DESTROYUSERNOW;
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "990";
        // eturn PersonConst.TRADE_TYPE_CODE_DESTROYUSERNOW;
    }

}
