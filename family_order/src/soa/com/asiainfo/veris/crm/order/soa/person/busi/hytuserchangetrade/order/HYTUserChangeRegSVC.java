package com.asiainfo.veris.crm.order.soa.person.busi.hytuserchangetrade.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class HYTUserChangeRegSVC extends OrderService {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "598");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "598");
    }
    
    /**
     * 海洋通产品变更登记
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset tradeReg(IData input) throws Exception
    {
        return super.tradeReg(input);
    }
}
