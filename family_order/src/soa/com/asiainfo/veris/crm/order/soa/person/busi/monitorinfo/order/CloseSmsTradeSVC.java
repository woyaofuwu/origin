
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo.order;

import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class CloseSmsTradeSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return PersonConst.CLOSE_SMS_TRADE;
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return PersonConst.CLOSE_SMS_TRADE;
    }

}
