package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino;



import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 一卡多号
 */
public class OneCardMultiNoRegSVC extends OrderService {

	private static final long serialVersionUID = -7596352454867420186L;

	public String getOrderTypeCode() throws Exception {
		return OneCardMultiNoBean.TRADE_TYPE_CODE;
	}

	public String getTradeTypeCode() throws Exception {
		return OneCardMultiNoBean.TRADE_TYPE_CODE;
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