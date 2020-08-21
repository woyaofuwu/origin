/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcontinuepay.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.CttConstants;

/**
 * @CREATED by gongp@2013-10-17 修改历史 Revision 2013-10-17 下午08:17:04
 */
public class CttBroadBandContinuePayRegSVC extends OrderService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 8286465634076878254L;

    /*
     * (non-Javadoc)
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return super.input.getString("ORDER_TYPE_CODE", CttConstants.CTT_BROADBAND_CONTINUEFEE);

    }

    /*
     * (non-Javadoc)
     */
    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return super.input.getString("ORDER_TYPE_CODE", CttConstants.CTT_BROADBAND_CONTINUEFEE);

    }

}
