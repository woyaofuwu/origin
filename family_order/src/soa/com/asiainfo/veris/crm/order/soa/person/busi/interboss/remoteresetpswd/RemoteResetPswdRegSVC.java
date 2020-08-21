package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remoteresetpswd;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class RemoteResetPswdRegSVC extends OrderService{

	@Override
	public String getOrderTypeCode() throws Exception {
		// TODO Auto-generated method stub
		return "79";//跨区密码重置
	}

	@Override
	public String getTradeTypeCode() throws Exception {
		// TODO Auto-generated method stub
		return "79";//跨区密码重置
	}

}
