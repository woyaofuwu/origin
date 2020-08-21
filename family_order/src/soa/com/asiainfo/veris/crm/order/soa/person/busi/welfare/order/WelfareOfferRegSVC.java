
package com.asiainfo.veris.crm.order.soa.person.busi.welfare.order;

import com.asiainfo.veris.crm.iorder.pub.welfare.consts.WelfareConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @Description 权益自有商品受理主服务
 * @Auther: zhenggang
 * @Date: 2020/7/3 9:59
 * @version: V1.0
 */
public class WelfareOfferRegSVC extends OrderService
{
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return WelfareConstants.TradeType.ACCEPT.getValue();
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return WelfareConstants.TradeType.ACCEPT.getValue();
    }
}
