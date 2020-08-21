
package com.asiainfo.veris.crm.order.soa.person.busi.cpe.order;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserAcctDayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class CPEActiveRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "699";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "699";
    }
    
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {	
    	String firstTimeTag=input.getString("FIRSTTIME_TAG");
        if ("0".equals(firstTimeTag))
        {
            IData saleactiveData = new DataMap();
            saleactiveData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            saleactiveData.put("PRODUCT_ID", "99992825");
            saleactiveData.put("PACKAGE_ID", "99992825");
   
            CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData); 
        }
    }
    
    /**
     * 
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
