
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetequrecycle.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.CttConstants;

public class CttBroadBandEquRecycleRegSVC extends OrderService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
        return super.input.getString("ORDER_TYPE_CODE", CttConstants.CTT_BROADBAND_DEVICEREYCLE);
    }

    public String getTradeTypeCode() throws Exception
    {
        return super.input.getString("ORDER_TYPE_CODE", CttConstants.CTT_BROADBAND_DEVICEREYCLE);
    }

}
