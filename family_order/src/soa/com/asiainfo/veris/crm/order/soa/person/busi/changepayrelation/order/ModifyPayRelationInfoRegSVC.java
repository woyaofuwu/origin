
package com.asiainfo.veris.crm.order.soa.person.busi.changepayrelation.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 普通付费关系变更
 * 
 * @author liutt
 */
public class ModifyPayRelationInfoRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "160";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "160";
    }

}
