package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class OneCardMospFollowRegSVC extends OrderService {
    
    public String getOrderTypeCode() throws Exception {
        // TODO Auto-generated method stub
        return "3797";
    }

    
    public String getTradeTypeCode() throws Exception {
        // TODO Auto-generated method stub
        return "3797";
    }
    
    public void setTrans(IData input) throws Exception {
		// 没有传SERIAL_NUMBER，必须进行转换
		String serial_number=input.getString("SERIAL_NUMBER");
		if(null==serial_number||"".equals(serial_number)){
			serial_number=input.getString("MSISDN");
		}
		input.put("SERIAL_NUMBER", serial_number);
	}
    
}
