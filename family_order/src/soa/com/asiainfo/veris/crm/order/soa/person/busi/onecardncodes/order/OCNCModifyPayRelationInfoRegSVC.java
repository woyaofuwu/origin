
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 普通付费关系变更
 */
public class OCNCModifyPayRelationInfoRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "161";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "161";
    }

}
