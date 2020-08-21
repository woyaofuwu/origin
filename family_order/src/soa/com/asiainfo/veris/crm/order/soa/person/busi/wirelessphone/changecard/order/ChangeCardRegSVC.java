/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.changecard.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeCardRegSVC.java
 * @Description: 换卡登记
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-7-17 上午10:11:17 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-17 yxd v1.0.0 修改原因
 */
public class ChangeCardRegSVC extends OrderService
{

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "142");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "142");
    }

}
