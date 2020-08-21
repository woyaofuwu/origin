
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.CttConstants;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CttBroadBandCreateRegSVC.java
 * @Description: 铁通宽带开户
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-3-3 上午10:53:16 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-3 yxd v1.0.0 修改原因
 */
public class CttBroadBandCreateRegSVC extends OrderService
{

    public String getOrderTypeCode() throws Exception
    {
        return super.input.getString("ORDER_TYPE_CODE", CttConstants.CTT_BROADBAND_CREATE);
    }

    public String getTradeTypeCode() throws Exception
    {
        return super.input.getString("ORDER_TYPE_CODE", CttConstants.CTT_BROADBAND_CREATE);
    }

}
