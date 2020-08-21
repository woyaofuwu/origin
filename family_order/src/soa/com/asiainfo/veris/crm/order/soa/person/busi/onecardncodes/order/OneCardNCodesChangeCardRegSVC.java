
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes.order;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class OneCardNCodesChangeCardRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "321";
        // return PersonConst.TRADE_TYPE_CODE_DESTROYUSERNOW;
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "321";
        // eturn PersonConst.TRADE_TYPE_CODE_DESTROYUSERNOW;
    }
    
    @Override
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception {
    	String osn = input.getString("OTHERSN");
    	String osimcardNo = input.getString("SIM_CARD_NO_O");
    	String oimsi = input.getString("commInfo_IMSI1");
    	String oki = input.getString("KI_B");
    	
    	if(StringUtils.isNotEmpty(osn)){
    		IData newOtherData =  new DataMap();
    		newOtherData.put("SERIAL_NUMBER", osn);
    		newOtherData.put("SIM_CARD_NO_M", osimcardNo);
    		newOtherData.put("commInfo_IMSI", oimsi);
    		newOtherData.put("KI_A", oki);
    		newOtherData.put("IS_MAIN", "false");

        	CSAppCall.call("SS.OneCardNCodesChangeCardRegSVC.tradeReg", newOtherData);
    	}
    }

}
