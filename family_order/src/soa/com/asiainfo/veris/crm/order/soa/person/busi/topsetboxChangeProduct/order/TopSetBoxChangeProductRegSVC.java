
package com.asiainfo.veris.crm.order.soa.person.busi.topsetboxChangeProduct.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TopSetBoxRegSVC.java
 * @Description: 机顶盒销售or换机登记服务
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-7-31 下午4:15:29 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-31 yxd v1.0.0 修改原因
 */
public class TopSetBoxChangeProductRegSVC extends OrderService
{

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "3902");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "3902");
    }

}
