package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetchangeprod.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class NoPhoneWideRenewRegSVC extends OrderService
{

    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "682");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "682");
    }

    public IDataset tradeReg(IData input) throws Exception
    {
        if (!"KD_".equals(input.getString("SERIAL_NUMBER").substring(0, 3)))
        {
            input.put("SERIAL_NUMBER", "KD_" + input.getString("SERIAL_NUMBER"));
        }
       
        return super.tradeReg(input);
    }
    //停机后开机，要调用宽带开机
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {	
    	 //停机后开机，要调用宽带开机
        String stopOpenTag=input.getString("STOP_OPEN_TAG");
        if(stopOpenTag!=null && "1".equals(stopOpenTag)){
        	IData openData = new DataMap();
        	openData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));  
        	openData.put("TRADE_TYPE_CODE", "684");  
        	//SS.ChangeWidenetSvcStateRegSVC.tradeReg  SS.OpenWidenetSvcStateRegSVC.tradeReg
            CSAppCall.call("SS.ChangeWidenetSvcStateRegSVC.tradeReg", openData);  
        }
    }
}
