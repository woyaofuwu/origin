/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.specdiscntbusiness.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: SpecDiscntBusinessRegSVC.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: maoke
 * @date: May 28, 2014 10:59:55 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 28, 2014 maoke v1.0.0 修改原因
 */
public class SpecDiscntBusinessRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "153";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "153";
    }

}
