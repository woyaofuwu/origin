
package com.asiainfo.veris.crm.order.soa.person.busi.np.destroynp.order.trade;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class DestroyNpTradeRegSVC extends OrderService
{
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "49";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "49";
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd)
    {
        orderData.setExecTime(SysDateMgr.END_DATE_FOREVER);
    }

}
