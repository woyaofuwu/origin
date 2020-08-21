package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 权益绑定关系修改
 * @author 梁端刚
 * @version V1.0
 * @date 2019/12/11 10:07
 */
public class BenefitBindRelChangeRegSVC extends OrderService {
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "712";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "712";
    }
}
