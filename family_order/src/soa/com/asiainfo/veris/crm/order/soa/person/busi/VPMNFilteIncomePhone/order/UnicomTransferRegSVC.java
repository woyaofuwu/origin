
package com.asiainfo.veris.crm.order.soa.person.busi.VPMNFilteIncomePhone.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class UnicomTransferRegSVC extends OrderService
{

    private static final long serialVersionUID = 1L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        /*
         * 对应原来编码 td.setTradeTypeCode("311"); td.setTradeLcuName("TCS_RegTransPhone");
         */
        return "311";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "311";
    }

}
