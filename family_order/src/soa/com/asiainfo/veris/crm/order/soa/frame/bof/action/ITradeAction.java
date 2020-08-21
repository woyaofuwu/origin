
package com.asiainfo.veris.crm.order.soa.frame.bof.action;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

/**
 * @author Administrator
 */
public interface ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception;
}
