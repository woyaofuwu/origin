
package com.asiainfo.veris.crm.order.soa.person.busi.rubbishsmsmgr.order;

import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ProsecutionTradeSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return PersonConst.RUBBISH_SMS_TRADE;
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return PersonConst.RUBBISH_SMS_TRADE;
    }

}
