/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @CREATED by gongp@2014-6-7 修改历史 Revision 2014-6-7 下午06:48:41
 */
public class CancelGiveValueCardRegSVC extends OrderService
{

    private static final long serialVersionUID = -7915625961728243376L;

    /*
     * (non-Javadoc)
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "424";
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "424";
    }

}
