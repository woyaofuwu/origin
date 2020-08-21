
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangeproduct.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.CttConstants;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @Description: 铁通宽带产品变更
 */
public class CttBroadBandChangeProductRegSVC extends OrderService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
        return super.input.getString("ORDER_TYPE_CODE", CttConstants.CTT_BROADBAND_CHANGEOFFER);
    }

    public String getTradeTypeCode() throws Exception
    {
        return super.input.getString("ORDER_TYPE_CODE", CttConstants.CTT_BROADBAND_CHANGEOFFER);
    }

}
