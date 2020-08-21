
package com.asiainfo.veris.crm.order.soa.person.busi.activesalecardopen.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 买断手工激活
 * 
 * @author sunxin
 */
public class ActiveSaleCardOpenRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

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
        CSAppCall.call("SS.FirstCallDealSVC.dealFirstCall", data);
    }

}
