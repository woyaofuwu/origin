/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.entitycard.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @CREATED by gongp@2014-6-4 修改历史 Revision 2014-6-4 上午09:36:29
 */
public class SaleEntityCardRegSVC extends OrderService
{

    private static final long serialVersionUID = -7612909124134909312L;

    /*
     * (non-Javadoc)
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "421";
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "421";
    }

}
