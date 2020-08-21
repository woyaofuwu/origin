package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 权益使用
 * @author 梁端刚
 * @version V1.0
 * @date 2019/12/10 9:24
 */
public class BenefitUseRegSVC extends OrderService {
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "713";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "713";
    }
}
