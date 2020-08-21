
package com.asiainfo.veris.crm.order.soa.frame.bof.execute;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

public interface ITrade
{
    public BusiTradeData createTrade(BaseReqData brd) throws Exception;
}
