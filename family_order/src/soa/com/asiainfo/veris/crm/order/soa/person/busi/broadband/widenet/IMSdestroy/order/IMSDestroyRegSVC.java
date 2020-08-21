
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.IMSdestroy.order;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class IMSDestroyRegSVC extends OrderService
{

    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
    	input.put("ORDER_TYPE_CODE", "6805");
        return this.input.getString("ORDER_TYPE_CODE", "");
    }

    public String getTradeTypeCode() throws Exception
    {
    	input.put("TRADE_TYPE_CODE", "6805");
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {
        orderData.setSubscribeType("0");
    }
    
    
    public void resetReturn(IData input , IData result, BusiTradeData btd )
    {
    	result.put("IMS_PRODUCT", input.getString("PRODUCT_NAME"));
    	result.put("IMS_BRAND",  input.getString("BRAND_CODE"));
    	result.put("IMS_SERIAL_NUMBER",  input.getString("IMS_NUMBER"));
    }
}
