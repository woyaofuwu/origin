package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;

public class AdjustDiscntTimeAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		String ffDiscntFlag = btd.getRD().getPageRequestData().getString("FFDISCNT_FLAG","");
		String startDate = btd.getRD().getPageRequestData().getString("FFDISCNT_START_DATE","");
		System.out.println("FFDISCNT_FLAG参数"+ffDiscntFlag+";"+startDate);
		if(!"TRURE".equals(ffDiscntFlag))
		{
			return;
		}
		
		List<DiscntTradeData> discntTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		
		String userId = btd.getRD().getUca().getUser().getUserId();
		for(DiscntTradeData discnt:discntTradeData){
			String modifyTag = discnt.getModifyTag();
			if("0".equals(modifyTag)){
				
				discnt.setStartDate(startDate);
				
			}
			
		}
		
	}

}
