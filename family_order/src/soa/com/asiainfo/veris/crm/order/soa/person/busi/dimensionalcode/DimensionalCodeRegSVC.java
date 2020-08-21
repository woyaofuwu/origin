package com.asiainfo.veris.crm.order.soa.person.busi.dimensionalcode;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
/**
 * 二维码
 */
public class DimensionalCodeRegSVC extends OrderService {

	private static final long serialVersionUID = -7596352454867420186L;

	public String getOrderTypeCode() throws Exception {
		return DimensionalCodeBean.TRADE_TYPE_CODE;
	}
	public String getTradeTypeCode() throws Exception {
		return DimensionalCodeBean.TRADE_TYPE_CODE;
	}
}