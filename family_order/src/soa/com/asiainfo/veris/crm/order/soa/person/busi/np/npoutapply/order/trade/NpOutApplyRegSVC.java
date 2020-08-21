
package com.asiainfo.veris.crm.order.soa.person.busi.np.npoutapply.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: NpOutApplyRegSVC.java
 * @Description: 携出申请登记服务 对应 TCS_AcceptNpOutApply
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-5-5 上午10:05:24 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-5-5 lijm3 v1.0.0 修改原因
 */
public class NpOutApplyRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "41";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "41";
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {
        orderData.setExecTime(btd.getMainTradeData().getExecTime());
    }

}
