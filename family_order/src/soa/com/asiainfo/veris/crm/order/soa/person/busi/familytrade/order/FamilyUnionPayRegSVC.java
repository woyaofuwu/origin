/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @CREATED by gongp@2014-5-22 修改历史 Revision 2014-5-22 上午09:23:32
 */
public class FamilyUnionPayRegSVC extends OrderService
{

    private static final long serialVersionUID = 4773879235088306577L;

    /*
     * (non-Javadoc)
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "325";
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "325";
    }

}
