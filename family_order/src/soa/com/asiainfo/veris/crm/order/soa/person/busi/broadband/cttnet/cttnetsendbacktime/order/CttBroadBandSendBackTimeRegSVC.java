
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetsendbacktime.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.CttConstants;

public class CttBroadBandSendBackTimeRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return input.getString("ORDER_TYPE_CODE", CttConstants.CTT_BROADBAND_FILLOUTTIME);
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return input.getString("ORDER_TYPE_CODE", CttConstants.CTT_BROADBAND_FILLOUTTIME);
    }

}
