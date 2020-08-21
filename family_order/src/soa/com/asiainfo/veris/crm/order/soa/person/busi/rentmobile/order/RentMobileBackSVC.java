
package com.asiainfo.veris.crm.order.soa.person.busi.rentmobile.order;

import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class RentMobileBackSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return PersonConst.TRADE_TYPE_CODE_RENTMOBILEBACK;
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return PersonConst.TRADE_TYPE_CODE_RENTMOBILEBACK;
    }

}
