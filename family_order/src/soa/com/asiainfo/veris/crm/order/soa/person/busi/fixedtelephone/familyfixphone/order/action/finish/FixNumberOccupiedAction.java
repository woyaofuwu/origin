package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.familyfixphone.order.action.finish;

import com.ailk.common.data.IData; 
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction; 
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

/**
 * 固话号码实占
 */
public class FixNumberOccupiedAction implements ITradeFinishAction {

	@Override
	public void  executeAction(IData mainTrade) throws Exception 
	{ 
		String tradeId=mainTrade.getString("TRADE_ID","");
		String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE","");
		String userId = mainTrade.getString("USER_ID","");
		String fixNumber=mainTrade.getString("RSRV_STR1","");//固话号码
		String serialNumber = mainTrade.getString("SERIAL_NUMBER","");
		String custName =  mainTrade.getString("CUST_NAME","");
		if (tradeTypeCode!=null && "9601".equals(tradeTypeCode))
		{ 
			IDataset callset=ResCall.resPossessForMphone("", "", fixNumber, "-1", tradeId, "-1", "0");
			if(callset!=null && callset.size()>0){
				
			}
		}
	}
}
