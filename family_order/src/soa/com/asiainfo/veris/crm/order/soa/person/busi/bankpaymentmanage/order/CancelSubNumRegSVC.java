/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @CREATED by gongp@2014-6-25 修改历史 Revision 2014-6-25 上午11:45:35
 */
public class CancelSubNumRegSVC extends OrderService
{
    private static final long serialVersionUID = -4135063253248939423L;

    /*
     * (non-Javadoc)
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "1394";
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "1394";
    }

}
