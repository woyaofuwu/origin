
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changepayrel.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/*
 * 固话付费关系变更
 * @author yuezy
 */
public class PayRelationInfoRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "9709";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "9709";
    }

}
