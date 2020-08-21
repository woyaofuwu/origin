/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @CREATED by gongp@2014-4-28 修改历史 Revision 2014-4-28 上午11:19:51
 */
public class FilteIncomePhoneSendSMSTradeRegSVC extends OrderService
{

    /*
     * (non-Javadoc)
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "271";
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "271";
    }

}
