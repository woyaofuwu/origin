package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order;

import org.apache.log4j.Logger;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 智能组网增值产品开通
 * 
 * @author lizj
 */
public class DredgeSmartNetworkRegSVC extends OrderService{
	
	static Logger logger = Logger.getLogger(DredgeSmartNetworkRegSVC.class);
	

	@Override
	public String getOrderTypeCode() throws Exception {
		
		 return this.input.getString("TRADE_TYPE_CODE","870");
	}

	@Override
	public String getTradeTypeCode() throws Exception {

		return this.input.getString("TRADE_TYPE_CODE","870");
	}

}
