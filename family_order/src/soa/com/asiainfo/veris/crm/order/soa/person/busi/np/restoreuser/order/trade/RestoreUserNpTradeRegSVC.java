
package com.asiainfo.veris.crm.order.soa.person.busi.np.restoreuser.order.trade;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class RestoreUserNpTradeRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "39";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "39";
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd)
    {
        orderData.setExecTime(SysDateMgr.END_DATE_FOREVER);
    }

}
