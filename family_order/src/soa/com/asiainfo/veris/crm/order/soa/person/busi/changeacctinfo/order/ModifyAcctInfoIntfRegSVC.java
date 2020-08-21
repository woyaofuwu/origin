
package com.asiainfo.veris.crm.order.soa.person.busi.changeacctinfo.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ModifyAcctInfoIntfRegSVC extends OrderService
{
    /**
     * 接口账户资料变更
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "80";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "80";
    }

}
