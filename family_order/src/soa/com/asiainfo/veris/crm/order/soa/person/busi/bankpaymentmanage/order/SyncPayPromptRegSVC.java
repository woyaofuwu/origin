/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @CREATED by gongp@2014-7-14 修改历史 Revision 2014-7-14 下午09:33:54
 */
public class SyncPayPromptRegSVC extends OrderService
{

    private static final long serialVersionUID = -5475698420689276625L;

    /*
     * (non-Javadoc)
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "94";
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "94";
    }

}
