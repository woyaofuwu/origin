
package com.asiainfo.veris.crm.order.soa.person.busi.changeacctinfo.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ModifyAcctInfoRegSVC extends OrderService
{
    /**
     * 80:前台账户资料变更 3812：无线固话账户资料变更（TRADE_TYPE_CODE从页面传）
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE");
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE");
    }

}
