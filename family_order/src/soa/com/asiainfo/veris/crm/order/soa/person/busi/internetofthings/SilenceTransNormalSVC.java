package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;



/**
 * 沉默期转正常 
 * @author huping
 *
 */
public class SilenceTransNormalSVC extends OrderService{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4598970766519579323L;

	@Override
	public String getOrderTypeCode() throws Exception {
		// TODO Auto-generated method stub
		return "277";
	}

	@Override
	public String getTradeTypeCode() throws Exception {
		// TODO Auto-generated method stub
		return "277";
	}

	
}
