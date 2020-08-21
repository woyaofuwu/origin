/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.grpvaluecard.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.FlowCardCall;

/**
 * @CREATED by gongp@2014-5-13 修改历史 Revision 2014-5-13 上午11:30:20
 */
public class GRPValueCardRegSVC extends OrderService
{

    /*
     * (non-Javadoc)
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "461";
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "461";
    }
}
