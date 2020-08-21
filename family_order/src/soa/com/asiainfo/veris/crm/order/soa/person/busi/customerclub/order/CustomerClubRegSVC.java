
package com.asiainfo.veris.crm.order.soa.person.busi.customerclub.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 客户俱乐部业务//REQ201708300021+俱乐部会员页面增加入会协议需求 by mnegqx 20180711
 * @author mengqx
 *
 */
public class CustomerClubRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "709";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "709";
    }

}
