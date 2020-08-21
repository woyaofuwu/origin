
package com.asiainfo.veris.crm.order.soa.frame.bof.rule;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

/**
 * @author Administrator
 */
public interface ITradeAfterRule
{
    public void checkTradeAfterRule(BusiTradeData btd) throws Exception;
}
