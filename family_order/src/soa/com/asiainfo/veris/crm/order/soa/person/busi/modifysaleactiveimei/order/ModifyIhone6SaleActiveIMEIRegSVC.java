
package com.asiainfo.veris.crm.order.soa.person.busi.modifysaleactiveimei.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ModifyIhone6SaleActiveIMEIRegSVC extends OrderService
{

	private static final long serialVersionUID = 4298926081379155347L;

	@Override
    public String getOrderTypeCode() throws Exception
    {
        return "518";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "518";
    }

}
