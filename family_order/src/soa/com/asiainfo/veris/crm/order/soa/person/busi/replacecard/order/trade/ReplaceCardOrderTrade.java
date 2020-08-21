package com.asiainfo.veris.crm.order.soa.person.busi.replacecard.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.replacecard.order.requestdata.ReplaceCardReqData;

public class ReplaceCardOrderTrade extends BaseTrade implements ITrade{

	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
		ReplaceCardReqData reqData=(ReplaceCardReqData)bd.getRD();
		//处理主台账
		MainTradeData mainData=bd.getMainTradeData();
		
		mainData.setRsrvStr3(reqData.getSimCardNo());
		mainData.setRsrvStr4(reqData.getImsi());
		mainData.setRsrvStr5(reqData.getEmptyCardId());
		
	}
}
