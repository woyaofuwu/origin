package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;

public class DefaultPayFeeZero implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		List<FeeTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_FEESUB);
		
		if(discntTradeDatas == null || discntTradeDatas.size()==0){
			FeeTradeData feeTradeData = new FeeTradeData();
			feeTradeData.setFee("0");
			feeTradeData.setFeeMode("2");
			feeTradeData.setFeeTypeCode("0");
			feeTradeData.setOldfee("0");
			feeTradeData.setUserId(btd.getRD().getUca().getUserId());
			btd.add(btd.getRD().getUca().getSerialNumber(), feeTradeData);
		}
		else
		{
			boolean addZeroFee = false ;
			for(int i = 0 ; i < discntTradeDatas.size() ; i++)
			{
				FeeTradeData feeTrade = discntTradeDatas.get(i);
				String feeMode = feeTrade.getFeeMode();
				if("2".equals(feeMode))
				{
					addZeroFee = true;
					break;
				}
			}
			
			if(!addZeroFee)
			{
				FeeTradeData feeTradeData = new FeeTradeData();
				feeTradeData.setFee("0");
				feeTradeData.setFeeMode("2");
				feeTradeData.setFeeTypeCode("0");
				feeTradeData.setOldfee("0");
				feeTradeData.setUserId(btd.getRD().getUca().getUserId());
				btd.add(btd.getRD().getUca().getSerialNumber(), feeTradeData);
			}
		}	
	} 
}
