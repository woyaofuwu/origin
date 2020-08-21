
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @author Administrator
 */
public class CreateWalletCardRegSVC extends OrderService
{

    private static final long serialVersionUID = -3190917907082421792L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "620";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "620";
    }

}
