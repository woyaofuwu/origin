package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remotedestroyuser.order;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class RemoteDestroyUserRegSVC extends OrderService {

    @Override
    public String getOrderTypeCode() throws Exception {
        return input.getString("ORDER_TYPE_CODE", "1990");
    }

    @Override
    public String getTradeTypeCode() throws Exception {
        return input.getString("TRADE_TYPE_CODE", "1990");
    }

    @Override
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
    }
}
