
package com.asiainfo.veris.crm.order.soa.frame.bof.rule;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;

/**
 * @author Administrator
 */
public interface IProductModuleAfterRule
{
    public void checkProductModuleAfterRule(ProductModuleTradeData dealPmtd, BusiTradeData btd) throws Exception;
}
