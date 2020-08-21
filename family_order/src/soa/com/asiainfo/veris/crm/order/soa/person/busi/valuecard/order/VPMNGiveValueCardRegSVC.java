/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @CREATED by gongp@2014-8-4 修改历史 Revision 2014-8-4 上午09:41:03
 */
public class VPMNGiveValueCardRegSVC extends OrderService
{

    private static final long serialVersionUID = -1028924109925223191L;

    /*
     * (non-Javadoc)
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "430";
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "430";
    }

}
