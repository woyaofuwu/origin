package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2020/1/15 20:13
 */
public class BenefitAddUseNumRegSvc extends OrderService {
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "714";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "714";
    }
}
