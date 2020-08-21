/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.chlcommfeesub.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @CREATED by gongp@2014-4-15 修改历史 Revision 2014-4-15 下午02:48:20
 */
public class ChlCommFeeSubCancelRegSVC extends OrderService
{

    private static final long serialVersionUID = -1555196435587113603L;

    /*
     * (non-Javadoc)
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "6201";
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "6201";
    }

}
