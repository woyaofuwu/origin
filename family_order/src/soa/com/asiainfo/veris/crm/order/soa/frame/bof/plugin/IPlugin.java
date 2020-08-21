
package com.asiainfo.veris.crm.order.soa.frame.bof.plugin;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

public interface IPlugin
{

    public void deal(List<BusiTradeData> btds, IData input) throws Exception;
}
