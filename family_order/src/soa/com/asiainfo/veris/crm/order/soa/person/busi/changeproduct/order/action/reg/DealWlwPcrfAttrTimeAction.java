package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;

public class DealWlwPcrfAttrTimeAction implements ITradeAction{
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		if(!"PWLW".equals(btd.getRD().getUca().getBrandCode())){
            return;
        }
		List<AttrTradeData> tradeAttrlist  = btd.getTradeDatas(TradeTableEnum.TRADE_ATTR);
		if(tradeAttrlist.size() > 0 && tradeAttrlist != null){
			for(AttrTradeData attrTrade:tradeAttrlist){
				if("ServiceStartDateTime".equals(attrTrade.getAttrCode())){
					attrTrade.setAttrValue(attrTrade.getAttrValue()+SysDateMgr.START_DATE_FOREVER);
				}
				if("ServiceEndDateTime".equals(attrTrade.getAttrCode())){
					attrTrade.setAttrValue(attrTrade.getAttrValue()+SysDateMgr.END_DATE);
				}
			}
		}
	}
}
