
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ChangeProductRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return input.getString("ORDER_TYPE_CODE", "110");
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return input.getString("TRADE_TYPE_CODE", "110");
    }

    @Override
    public final void setTrans(IData input)
    {
        if ("6".equals(this.getVisit().getInModeCode()))
        {
            if (!"".equals(input.getString("SERIAL_NUMBER", "")))
            {
                return;
            }
            else if (!"".equals(input.getString("IDITEMRANGE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("IDITEMRANGE"));
                return;
            }
        }
        else if ("1".equals(this.getVisit().getInModeCode()))
        { // 渠道 ：热线
            if (!"".equals(input.getString("MSISDN", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("MSISDN", ""));
                return;
            }
        }
    }
}
