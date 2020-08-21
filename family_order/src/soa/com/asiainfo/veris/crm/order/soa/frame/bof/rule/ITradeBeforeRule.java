
package com.asiainfo.veris.crm.order.soa.frame.bof.rule;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @author Administrator
 */
public interface ITradeBeforeRule
{

    public void checkBeforeRule(BaseReqData brd) throws Exception;
}
