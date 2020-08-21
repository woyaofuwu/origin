
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ScoreConvertRegSVC extends OrderService
{

    private static final long serialVersionUID = 1L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "349";
        // return PersonConst.TRADE_TYPE_CODE_RESTOREUSER;
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "349";
        // return PersonConst.TRADE_TYPE_CODE_RESTOREUSER;
    }

}
