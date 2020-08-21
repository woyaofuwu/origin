
package com.asiainfo.veris.crm.order.soa.frame.bof.builder;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

public interface IFilterOut
{
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception;
}
