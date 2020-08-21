package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.wideprereg.order;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class WidePreRegRegSVC extends OrderService{

	@Override
	public String getOrderTypeCode() throws Exception {
		//702
		return "702";
	}

	@Override
	public String getTradeTypeCode() throws Exception {
		//702
		return "702";
	}

	@Override
	public void resetReturn(IData input, IData output, BusiTradeData btd) throws Exception
	{
		output.put("code_Result", "0");  
	}
}
