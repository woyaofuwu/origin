
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideunionpay.order;


import org.apache.log4j.Logger;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class NophoneWideUnionPayRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;
    
    private static transient Logger logger = Logger.getLogger(NophoneWideUnionPayRegSVC.class);

    public String getOrderTypeCode() throws Exception
    {
        return "328";
    }

    public String getTradeTypeCode() throws Exception
    {
    	return "328";
    }
    
}
