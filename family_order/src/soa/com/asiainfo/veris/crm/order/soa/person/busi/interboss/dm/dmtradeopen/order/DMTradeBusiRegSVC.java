
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmtradeopen.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class DMTradeBusiRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return BofConst.TRADE_TYPE_CODE_DMALLBUSI;
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return BofConst.TRADE_TYPE_CODE_DMALLBUSI;
    }
}
