
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.restore.order;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class RestoreUserGHRegSVC extends OrderService
{

    private static final long serialVersionUID = 1L;

    // 重写父类的checkAfterRule，7302缴费复机的不走互斥依赖规则（老的就这样）
    public void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
    {
        if (StringUtils.equals("7302", btd.getTradeTypeCode()))
        {
            // 7302缴费复机的 老系统没有限制互斥依赖
        }
        else
        {
            super.checkAfterRule(tableData, btd);
        }
    }

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "9706");
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "9706");
    }

    /**
     * 修改主订单的SubscribeType 字段 200-信控执行，201-信控开机
     */
    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd)
    {
        String tradeTypeCode = orderData.getTradeTypeCode();
        if (StringUtils.equals("7302", tradeTypeCode))
        {
            orderData.setSubscribeType("201");
        }
    }

}
