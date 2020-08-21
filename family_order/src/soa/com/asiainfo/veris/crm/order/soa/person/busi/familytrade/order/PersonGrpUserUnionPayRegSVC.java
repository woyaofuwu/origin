
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 和商务融合产品包订购
 * @author chenzg
 * @date 2018-04-11
 */
public class PersonGrpUserUnionPayRegSVC extends OrderService
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
     * (non-Javadoc)
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "327";
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "327";
    }

}
