
package com.asiainfo.veris.crm.order.soa.person.busi.activesalecardopen.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ActiveSaleCardOpenIntfSVC extends OrderService
{

    // @Override
    public String getOrderTypeCode() throws Exception
    {
        return "18";
    }

    // @Override
    public String getTradeTypeCode() throws Exception
    {
        return "18";
    }

    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        IData data = new DataMap();
        String time = SysDateMgr.getSysTime();

        data.put("USER_ID", btd.getRD().getUca().getUser().getUserId());
        data.put("SERIAL_NUMBER", btd.getRD().getUca().getUser().getSerialNumber());
        data.put("FIRST_CALL_TIME", time);
        data.put("FLAG", "6067_6070_TAG"); 	//6067,6070激活标志
        CSAppCall.call("SS.FirstCallDealSVC.dealFirstCall", data);
    }
}
